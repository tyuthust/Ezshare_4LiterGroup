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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

public class ServerClass {

	public static boolean DEFAULT_RELAY_MODE = true;
	public static int DEFAULT_PORT = 3000;
	public static int DEFAULT_QUERY_TIMEOUT = 6000;

	private ServerCmds cmds;
	private static int resultSize = 1;

	// A list to save client subscribed info
	private HashMap<String, Resource> subscribeList = new HashMap<String, Resource>();

	public static boolean ServerDebugModel = false;
	// A flag to judge the while loop in socket.accept function
	private static boolean flag = true;

	public static InetAddress ServerHost;

	protected static Logger logger = Logger.getLogger(ServerClass.class.getName());
	// Resource Map
	private ResourceWarehouse resourceWarehouse;
	// Server List
	private static Set<String> Servers;
	private static int intervalTime = 600;

	public ServerClass(ServerCmds cmds) throws UnknownHostException {
		ServerDebugModel = cmds.debug;
		ServerHost = InetAddress.getByName(cmds.advertisedhostname);
		resourceWarehouse = new ResourceWarehouse();
		this.cmds = cmds;
		if (-1 == this.cmds.port) {
			this.cmds.port = DEFAULT_PORT;
		}
		if (null == this.cmds.secret) {
			this.cmds.generateSecret();
		}
		if (cmds.exchangeinterval > 0) {
			intervalTime = cmds.exchangeinterval;
		}
		Servers = new HashSet() {
			{
				add(cmds.advertisedhostname + ":" + Integer.toString(cmds.port));
			}
		};
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
				logger.info("The IP:" + cmds.advertisedhostname + "\n" + "The port:" + cmds.port);
			}

			// serverInteraction function
			// startTimer();
			// Wait for connections.
			while (true) {
				flag = true;
				Socket client = server.accept();

				// Start a new thread for a connection in the thread pool
				Thread t = new Thread(() -> serveClient(client));
				ThreadPool.execute(t);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void serveClient(Socket client) {
		boolean subflag = false;
		String id = null;
		try (Socket clientSocket = client) {

			// The JSON Parser
			JSONParser parser = new JSONParser();
			// Input stream
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			// Output Stream
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

			// Receive more data..
			while (flag) {
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
					} else if (command.get("command").equals("SUBSCRIBE")) {
						results = handleSubscribe(command, output);
					} else if (command.get("command").equals("UNSUBSCRIBE")) {
						results = handleUnsubscribe(command, output);
					}

					output.writeUTF(results.toJSONString());
					output.flush();

					// judge whether the subcribeList contains
					// the id of the client in this thread
					// if no, close the clientsocket and close the thread
					
					if(command.containsKey("id")){
						id = command.get("id").toString();
						subflag = subscribeList.containsKey(id);
					}
					if(subflag){
						flag = !flag;
					}
					flag = !flag;
				}
			}
			clientSocket.close();
		} catch (IOException | ParseException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}


	private JSONObject handleSubscribe(JSONObject jsonObject, DataOutputStream output){
		//TODO: address subscribe function
		System.out.println("subscribe function");
		JSONObject results = new JSONObject();
		results.put("response", "success");
		return results;
	}

	private JSONObject handleUnsubscribe(JSONObject jsonObject, DataOutputStream output){
		//TODO: address unsubscribe function
		System.out.println("unsubscribe function");
		JSONObject results = new JSONObject();
		results.put("response", "success");
		return results;
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
			relayMode = 
					//false;
					(jsonObject.get("relay").equals("true")) ? true:false;
			System.out.println("Query Relay "+ (relayMode?"On":"Off"));
		}
		try {
			IResourceTemplate resource = ServerOperationHandler.query(jsonObject);
			Resource[] hitResources = this.resourceWarehouse.FindReource(resource);
			if (null != hitResources) {
				String serverInfo = ServerHost.getHostAddress() + ":" + this.cmds.port;
				for (Resource hitresource : hitResources) {
//					if (!hitresource.getOwner().equals(null) && !hitresource.getOwner().equals("")) {
						hitresource.setOwner("*");
						hitresource.setEZServer(serverInfo);
//					}
					resultResources.add(hitresource);
				}
			}
			if(relayMode){
				JSONObject relayjsonObject = (JSONObject) jsonObject.clone();
				relayjsonObject.replace("relay", "false");
				if(ServerDebugModel){
					logger.setLevel(Level.INFO);
					logger.info("Start Realy");
				}
				ArrayList<Resource> relayResources = queryOtherServers(jsonObject, Servers);
				if(ServerDebugModel){
					logger.setLevel(Level.INFO);
					logger.info("relayResources Total Number: " + relayResources.size());
					logger.info("End Relay");
				}
				resultResources.addAll(relayResources);
			}


			results.put("response", "success");
			if (null != resultResources && resultResources.size() > 0) {
				JSONArray resourcesArray = new JSONArray();
				for (Resource resultResource : resultResources) {
					resourcesArray.add(resourcePack(resultResource));
				}
				results.put("resource", resourcesArray);
				results.put("resultSize", resultResources.size());
			} else {
				results.put("resultSize", 0);
			}

		} catch (OperationRunningException e) {
			// TODO Auto-generated catch block
			results.put("response", "error");
			results.put("errorMessage", e.toString());
		}
		finally {
			if (ServerDebugModel) {
				logger.info(results.toJSONString());
			}
			return results;
		}



	}

	private ArrayList<Resource> queryOtherServers(JSONObject jsonObject,Set<String> serverList) {
		Callable<ArrayList<Resource>> run = new Callable<ArrayList<Resource>>()
		    {
		        @Override
		        public ArrayList<Resource> call() throws Exception
		        {
		        	ArrayList<Resource> relayQueryResources = new ArrayList<>();
		            
		        	// your code to be timed
		        	
		        	for (String string : serverList) {
		        		//not query the server self
		        		if(!string.equals(ServerHost.getHostAddress()+":"+cmds.port)){
		        			System.out.println("Query server: "+ string);
				        	JSONObject queryResult = queryRelay(jsonObject, string);
				        	System.out.println(queryResult.toString());
				        	JSONArray resourceArray = new JSONArray();
				        	if(queryResult.containsKey("resource")){
					        	resourceArray = (JSONArray) queryResult.get("resource");
					        	for (int i = 0; i < resourceArray.size(); i++) {
					        		
									relayQueryResources.add(
											ServerOperationHandler.convertJSONOBjectResourceToResource(
													(JSONObject)resourceArray.get(i)
													)
											);
									
								}
				        	}

		        		}
					}
		        	if(ServerDebugModel){
		        		logger.setLevel(Level.INFO);
			        	logger.info("Total Hit Query From Relay: " + relayQueryResources.size());
		        	}
		        	return relayQueryResources;
		        }
		    };

		    RunnableFuture future = new FutureTask(run);
		    ExecutorService service = Executors.newSingleThreadExecutor();
		    service.execute(future);
		    ArrayList<Resource> foundResources = null;
		    try
		    {
		        try {
		        	foundResources = (ArrayList<Resource>) future.get(DEFAULT_QUERY_TIMEOUT, TimeUnit.MILLISECONDS);
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    catch (TimeoutException ex)
		    {
		        // timed out. Try to stop the code if possible.
		        future.cancel(true);
		    }
		    service.shutdown();
		    return foundResources;
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
		int i = 0;
		try {
			String[] serverlist = ServerOperationHandler.exchange(jsonObject);
			for (String string : serverlist) {
				Servers.add(string);
			}
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

	private static void serverInteraction(Set<String> Servers) {
		if (Servers != null && Servers.size() > 1) {
			Random ran = new Random();
			int index = ran.nextInt(Servers.size());
			String selectedServer = "";
			for (int i = 0; i < index + 1; i++) {
				if (Servers.iterator().hasNext()) {
					selectedServer = Servers.iterator().next();
				}
			}
			System.out.println("The selected server is:" + selectedServer);
			String[] IPandPort = selectedServer.split(":");
			String addr = IPandPort[0];
			int Port = Integer.parseInt(IPandPort[1]);
			try (Socket socket = new Socket(addr, Port)) {
				// Output and Input Stream

				DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				System.out.println("Server Interaction");
				JSONObject jsonObject = new JSONObject();
				JSONArray jsonMap = new JSONArray();

				jsonObject.put("command", "EXCHANGE");
				if (Servers != null) {
					for (String string : Servers) {
						String[] DomainAndPort = string.split(":");
						JSONObject jsonObject2 = new JSONObject();
						jsonObject2.put("hostname", DomainAndPort[0]);
						int port = Integer.parseInt(DomainAndPort[1]);
						jsonObject2.put("port", port);
						jsonMap.add(jsonObject2);
					}
					jsonObject.put("serverList", jsonMap);

				}
				// add to logger
				System.out.println("Sending Exchange command is :" + jsonObject.toJSONString());

				// Read hello from server..
				String message = input.readUTF();
				System.out.println(message);

				// Send RMI to Server
				output.writeUTF(jsonObject.toJSONString());
				output.flush();

				// Print out results received from server..
				String result = input.readUTF();
				System.out.println("Received from server: " + result);

			} catch (UnknownHostException e) {
			} catch (IOException e) {

			}
		}
	}

	private static JSONObject queryRelay(JSONObject jsonObject, String server) throws ParseException {
		String[] IPandPort = server.split(":");
		String addr = IPandPort[0];
		int Port = Integer.parseInt(IPandPort[1]);
		JSONObject command = new JSONObject();
		try (Socket socket = new Socket(addr, Port)) {
			// Output and Input Stream

			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

			System.out.println("query relay function!");

			if (jsonObject.containsKey("relay")) {
				jsonObject.replace("relay", "false");
			}
			// add to logger
			System.out.println("Query Relay JSONPack:" + jsonObject.toJSONString());

			// Send RMI to Server
			output.writeUTF(jsonObject.toJSONString());
			output.flush();

			// Print out results received from server..
			JSONParser parser = new JSONParser();
			boolean unfinish = true;
			while (unfinish) {
				if (input.available() > 0) {
					String result = input.readUTF();
					System.out.println("Received from server: " + result);
					command = (JSONObject) parser.parse(result);
					unfinish = false;
				}
			}
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
		return command;
	}

	private static void startTimer() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// System.out.println("task begin:" + getCurrentTime());
				serverInteraction(Servers);
				try {
					Thread.sleep(1000 * 3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// System.out.println("task end:" + getCurrentTime());
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1000 * 5, intervalTime * 1000);
	}

	private static String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}