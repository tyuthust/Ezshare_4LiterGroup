package com.unimelb.comp90015.fourLiterGroup.ezshare;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.logging.*;

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.IResourceTemplate;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.Resource;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.ResourceWarehouse;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.ServerOperationHandler;

public class Server {

	public static boolean DEFAULT_RELAY_MODE = true;

	private ServerCmds cmds;
	// Identifies the user number connected
	private static int counter = 0;

	private static int resultSize = 1;

	public static boolean ServerDebugModel = false;
	
	public static InetAddress ServerHost;
	
	protected static Logger logger = Logger.getLogger(Server.class.getName());
	// Resource Map
	private ResourceWarehouse resourceWarehouse;
	// Server List
	private static String[] Servers = null;
	private static int intervalTime = 30;

	public Server(ServerCmds cmds) throws UnknownHostException {
		ServerDebugModel = cmds.debug;
		ServerHost =  InetAddress.getByName(cmds.advertisedhostname);
		resourceWarehouse = new ResourceWarehouse();
		this.cmds = cmds;
		if (null == this.cmds.secret) {
			this.cmds.generateSecret();
		}
		intervalTime = cmds.exchangeinterval;
	}

	public void run() {
		System.out.println(cmds.port);
	}

	public void setup() {
		logger.setLevel(Level.INFO);
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		// start a Thread Pool. Threads that have not been used for more than
		// sixty seconds are terminated and removed from the cache.
		ExecutorService ThreadPool = Executors.newCachedThreadPool();

		try (ServerSocket server = factory.createServerSocket(this.cmds.port)) {
			if (cmds.debug) {
				logger.info("setting server debug on. ");
				logger.info("The port: " + cmds.port);
			}
			System.out.println("Waiting for client connection..");
			// sending server list if it exist
			
			// Wait for connections.
			while (true) {
				Socket client = server.accept();
				//startTimer();
				counter++;
				System.out.println("Client " + counter + ": Applying for connection!");

				// Start a new thread for a connection in the thread pool
				Thread t = new Thread(() -> serveClient(client));
				ThreadPool.execute(t);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void serveClient(Socket client) {
		try (Socket clientSocket = client) {

			// The JSON Parser
			JSONParser parser = new JSONParser();
			// Input stream
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			// Output Stream
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("CLIENT: " + input.readUTF());

			if (cmds.debug) {
				logger.info("[sent]" + "Server: Hi Client " + counter + " !!!");
			}
			output.writeUTF("Server: Hi Client " + counter + " !!!");
			output.flush();

			// Receive more data..
			while (true) {
				if (input.available() > 0) {
					// Attempt to convert read data to JSON
					JSONObject command = (JSONObject) parser.parse(input.readUTF());
					if (cmds.debug) {
						logger.info("COMMAND RECEIVED: " + command.toJSONString());
					}
					JSONObject results = new JSONObject();// return json pack

					// TODO: change to ServerOperationHandler
					if (command.get("command").equals("PUBLISH")) {
						results = handlePublish(command, output);
						// results = publish(command);
					} else if (command.get("command").equals("QUERY")) {
						results = handleQuery(command, output);
						 
						
						// results = query(command);
					} else if (command.get("command").equals("REMOVE")) {
						results = handleRemove(command, output);
						// results = remove(command);
					} else if (command.get("command").equals("SHARE")) {
						results = handleShare(command, output);
						// results = share(command);
					} else if (command.get("command").equals("FETCH")) {
						results = handleFetch(command, output);
						// results = fetch(command);
					} else if (command.get("command").equals("EXCHANGE")) {
						results = handleExchange(command, output);
						// results = exchange(command);
					}
					output.writeUTF(results.toJSONString());
					output.flush();
				}
			}
		} catch (IOException | ParseException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	private JSONObject handlePublish(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		try {
			Resource resource = ServerOperationHandler.publish(jsonObject);
			if (resourceWarehouse.AddResource(resource)) {
				results.put("response", "success");

				resourceWarehouse.printResourceMap();
			} else {
				results.put("response", "error");
				results.put("errorMessage", "invalid resource");

			}
		} catch (OperationRunningException e) {
			results.put("response", "error");
			results.put("errorMessage", e.toString());
		}
		if (cmds.debug) {
			logger.info(results.toJSONString());
		}
		return results;
	}

	private JSONObject handleQuery(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		ArrayList<Resource> resultResources = new ArrayList<>();
		Boolean relayMode = DEFAULT_RELAY_MODE;
		if (null != jsonObject.get("relay")) {
			relayMode = jsonObject.get("relay") == "false" ? false : true;
		}
		try {
			IResourceTemplate resource = ServerOperationHandler.query(jsonObject);
			Resource[] hitResources = this.resourceWarehouse.FindReource(resource);
			if (null != hitResources) {
				for (Resource hitresource : hitResources) {
					if (!hitresource.getOwner().equals(null) && !hitresource.getOwner().equals("")) {
						hitresource.setOwner("*");
					}
					resultResources.add(hitresource);
				}
			}

			results.put("response", "success");
			if (null != hitResources && hitResources.length > 0) {
				for (Resource hitResource : hitResources) {
					results.put("resource", resourcePack(hitResource));
				}
				results.put("resultSize", hitResources.length);
			} else {
				results.put("resultSize", 0);
			}

		} catch (OperationRunningException e) {
			// TODO Auto-generated catch block
			results.put("response", "error");
			results.put("errorMessage", e.toString());
		}
		if (cmds.debug) {
			logger.info(results.toJSONString());
		}
//		//this try-catch to achieve multicast sending part.
//		try (DatagramSocket SendSocket = new DatagramSocket()) {
//            for (int i = 0; i < Servers.length; i++) {
//            	String msg="aa";
//                String[] addrAndPort=Servers[i].split(":");
//                InetAddress addr = InetAddress.getByName(addrAndPort[0]);
//                int PORT = Integer.parseInt(addrAndPort[1]);
//                // Create a packet that will contain the data
//                // (in the form of bytes) and send it.
//                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, addr, PORT);
//                SendSocket.send(msgPacket);
//     
//                System.out.println("Server sent packet with msg: " +msg);
//                //Thread.sleep(200);
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
		return results;
	}

	private JSONObject handleShare(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		// if secret is incorrect

		// check secret here because server keep the secret
		if (jsonObject.get("secret") != null && !(jsonObject.get("secret").toString()).equals(this.cmds.secret)) {
			results.put("response", "error");
			results.put("errorMessage", "incorrect secret");
		} else {
			try {
				Resource resource = ServerOperationHandler.share(jsonObject);
				if (resourceWarehouse.AddResource(resource)) {
					results.put("response", "success");
				} else {
					// if same URI same channel different OWner,
					// error
					results.put("response", "error");
					results.put("errorMessage", "invalid resource");

				}
			} catch (OperationRunningException e) {
				results.put("response", "error");
				results.put("errorMessage", e.toString());

			}
		}
		if (cmds.debug) {
			logger.info(results.toJSONString());
		}
		return results;

	}

	private JSONObject handleFetch(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();

		try {
			Resource resource = ServerOperationHandler.fetch(jsonObject);

			if (resourceWarehouse.FindResource(resource.getChannel(), resource.getURI())) {
				System.out.println(results.toJSONString());

				// get filename from uri
				String uri = resource.getURI();
				String filename = uri.replaceFirst("file://", "");

				// String filename = "server_files/"+ resource.getName();
				// String filename = "/Users/fangrisheng/Desktop/sauron.jpg";

				File f = new File(filename);
				JSONObject trigger = new JSONObject();

				if (f.exists()) {

					// Send trigger back to client so that they know what the
					// file is.
					try {
						trigger.put("command_name", "SENDING_FILE");
						trigger.put("file_name", "sauron.jpg");
						trigger.put("file_size", f.length());
						output.writeUTF(trigger.toJSONString());
						output.flush();

						// Start sending file
						RandomAccessFile byteFile = new RandomAccessFile(f, "r");
						resource.setResourceSize(f.length());
						byte[] sendingBuffer = new byte[1024 * 1024];
						int num;
						// While there are still bytes to send..
						while ((num = byteFile.read(sendingBuffer)) > 0) {
							System.out.println(num);
							output.write(Arrays.copyOf(sendingBuffer, num));
						}
						output.flush();
						results.put("response", "success");
						results.put("resource", resourcePack(resource));
						results.put("resultSize", resultSize);
						byteFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					throw new OperationRunningException("Download file error");
				}
			} else {
				results.put("response", "error");
				results.put("errorMessage", "invalid resourceTemplate");
			}
		} catch (OperationRunningException e) {
			results.put("response", "error");
			results.put("errorMessage", e.toString());

		}
		if (cmds.debug) {
			logger.info(results.toJSONString());
		}
		return results;
	}

	private JSONObject handleExchange(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		try {
			Servers = ServerOperationHandler.exchange(jsonObject).clone();
			if (cmds.debug) {
				for (String string : Servers) {
					logger.info("Server list: " + string);
				}
			}

			results.put("response", "success");
		} catch (OperationRunningException e) {
			results.put("response", "error");
			results.put("errorMessage", e.toString());

		}
		if (cmds.debug) {
			logger.info(results.toJSONString());
		}
		return results;

	}

	private JSONObject handleRemove(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		try {
			Resource resource = ServerOperationHandler.remove(jsonObject);
			if (resourceWarehouse.RemoveResource(resource)) {
				results.put("response", "success");
				resourceWarehouse.printResourceMap();
			} else {
				results.put("response", "error");
				results.put("errorMessage", "cannot remove resource");
			}
		} catch (OperationRunningException e) {
			results.put("response", "error");
			results.put("errorMessage", e.toString());
		}
		if (cmds.debug) {
			logger.info(results.toJSONString());
		}
		return results;
	}

	private JSONObject resourcePack(Resource resource) {
		JSONObject results = new JSONObject();
		results.put("name", resource.getName());
		if (resource.getTags() != null) {
			List<String> tagList = new ArrayList<String>();
			for (String string : resource.getTags()) {
				tagList.add(string);
			}
			results.put("tags", tagList);
		} else {
			results.put("tags", null);
		}
		results.put("description", resource.getDescription());
		results.put("uri", resource.getURI());
		results.put("channel", resource.getChannel());
		results.put("owner", resource.getOwner());
		results.put("ezserver", resource.getEzserver());
		results.put("resourceSize", resource.getSize());
		if (cmds.debug) {
			logger.info(results.toJSONString());
		}
		return results;
	}

	private static void serverInteraction(String[] Servers) {
		if(Servers!=null){
			Random ran = new Random();
			int index = ran.nextInt(Servers.length);
			String selectedServer = Servers[index];
			String[] IPandPort = selectedServer.split(":");
			String addr = IPandPort[0];
			int Port = Integer.parseInt(IPandPort[1]);
			try (Socket socket = new Socket(addr, Port)) {
				// Output and Input Stream
				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());

	    		JSONObject jsonObject = new JSONObject();
	    		JSONArray jsonMap = new JSONArray();
				jsonObject.put("command", "EXCHANGE");
				if (Servers != null) {
					for (String string :Servers) {
						String[] DomainAndPort = string.split(":");
						JSONObject jsonObject2 = new JSONObject();
						jsonObject2.put("hostname", DomainAndPort[0]);
						jsonObject2.put("port", DomainAndPort[1]);
						jsonMap.add(jsonObject2);
					}
					jsonObject.put("serverList", jsonMap);
				} else {
					jsonObject.put("serverList", null);
				}
	    		
	    		//add to logger
				System.out.println("Sending Exchange command is :"+jsonObject.toJSONString());
	    		
	    		// Read hello from server..
	    		String message = input.readUTF();
	    		System.out.println(message);
	    		
	    		// Send RMI to Server
	    		output.writeUTF(jsonObject.toJSONString());
	    		output.flush();
	    		
	    		// Print out results received from server..
	    		String result = input.readUTF();
	    		System.out.println("Received from server: "+result);
	    		
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {

			}
		}else{
			System.out.println("The server list is null !");
		}	
	}
	
	private static void startTimer(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task begin:"+getCurrentTime());
                serverInteraction(Servers);
                try {
                    Thread.sleep(1000*3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("task end:"+getCurrentTime());
            }
        };
        Timer timer = new Timer();
        timer.schedule(task,1000*5,1000*30);
    }
	
    private static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}