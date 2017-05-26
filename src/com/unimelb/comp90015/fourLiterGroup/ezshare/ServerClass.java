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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.IResourceTemplate;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.OperationRunningException;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.Resource;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.ResourceWarehouse;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.ServerOperationHandler;

public class ServerClass {
	class connectedClient {
		String id = null;
		private ArrayList<Resource> resources = new ArrayList<Resource>();
		int resultSize=0;
		
		public connectedClient(int initialSize, String id) {
			this.resultSize = initialSize;
			this.id = id;
		}
		

		public void addResource(Resource resource) {
			resources.add(resource);
			addresultSize();
		}
		
		private void addresultSize(){
			resultSize++;
		}
		
		public ArrayList<Resource> getandRefreshResources(){
			ArrayList<Resource> returnresources = (ArrayList<Resource>) this.resources.clone();
			this.resources = new ArrayList<>();
			return returnresources;
		}

	}

	public static boolean DEFAULT_RELAY_MODE = true;
	public static int DEFAULT_PORT = 3000;
	public static int DEFAULT_QUERY_TIMEOUT = 6000;

	private ServerCmds cmds;
	private static int resultSize = 1;

	// A list to save client subscribed info
	private static HashMap<String, IResourceTemplate> subscribeList = new HashMap<String, IResourceTemplate>();
	private static HashMap<String, connectedClient> notifyList = new HashMap<String, connectedClient>();

	public static boolean ServerDebugModel = false;

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

	public void setup() {
		logger.setLevel(Level.INFO);
		ServerSocketFactory factory = ServerSocketFactory.getDefault();

		// start a Thread Pool. Threads that have not been used for more than
		// sixty seconds are terminated and removed from the cache.
		ExecutorService ThreadPool = Executors.newCachedThreadPool();
		// serverInteraction function
		startTimer();
		try (ServerSocket server = factory.createServerSocket(this.cmds.port)) {
			if (cmds.debug) {
				logger.info("setting server debug on. ");
				logger.info("The IP:" + cmds.advertisedhostname + "\n" + "The port:" + cmds.port);
			}

			// Wait for connections.
			while (true) {
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
		// A flag to judge the while loop in socket.accept function
		boolean unfinishFlag = true;
//		boolean subflag = false;
		String id = null;
		connectedClient cclient = null;
		
		int resouceHitNumber = 0;

		try (Socket clientSocket = client) {

			// The JSON Parser
			JSONParser parser = new JSONParser();
			// Input stream
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			// Output Stream
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			
			
			JSONObject command = null;
			// Receive more data..
			while (unfinishFlag) {
				
				
				if (input.available() > 0) {
					// Attempt to convert read data to JSON
					command = (JSONObject) parser.parse(input.readUTF());
					if (cmds.debug) {
						logger.info("COMMAND RECEIVED: " + command.toJSONString());
					}
					Object results = null;// return json pack

					// TODO: change to ServerOperationHandler
					if (command.get("command").equals("PUBLISH")) {
						results = new JSONObject();
						results = handlePublish(command, output);

					} else if (command.get("command").equals("QUERY")) {
						results = new ArrayList<JSONObject>();
						results = handleQuery(command, output);
					} else if (command.get("command").equals("REMOVE")) {
						results = new JSONObject();
						results = handleRemove(command, output);
					} else if (command.get("command").equals("SHARE")) {
						results = new JSONObject();
						results = handleShare(command, output);
					} else if (command.get("command").equals("FETCH")) {
						results = new JSONObject();
						results = handleFetch(command, output);
					} else if (command.get("command").equals("EXCHANGE")) {
						results = new JSONObject();
						results = handleExchange(command, output);
					} else if (command.get("command").equals("SUBSCRIBE")) {
						results = new ArrayList<JSONObject>();
						results = handleSubscribe(command, output);
					} else if (command.get("command").equals("UNSUBSCRIBE")) {
						results = new JSONObject();
						results = handleUnsubscribe(command, output);
					} else {
						// Unrecognized command
						// jump out
						break;
					}
					if (command.get("command").equals("PUBLISH") 
							|| command.get("command").equals("REMOVE")
							|| command.get("command").equals("SHARE") 
							|| command.get("command").equals("FETCH")
							|| command.get("command").equals("EXCHANGE")
							|| command.get("command").equals("UNSUBSCRIBE")) {
						if (ServerDebugModel) {
							logger.setLevel(Level.INFO);
							logger.info("Send MSG:" + ((JSONObject) results).toJSONString());
						}
						output.writeUTF(((JSONObject) results).toJSONString());
						output.flush();
						unfinishFlag = false;
					} else if (command.get("command").equals("QUERY") | command.get("command").equals("SUBSCRIBE")) {
						ArrayList<JSONObject> resultArrayList = (ArrayList<JSONObject>) results;
						if (ServerDebugModel) {
							logger.setLevel(Level.INFO);
							logger.info("MSG count:" + resultArrayList.size());
						}
						if(command.get("command").equals("QUERY")){
							//define resource count
							//if arrayList size <2(only one json msg)
							//msg must be error
							//otherwise successful
							//reduce 1 for response
							//reduce 1 for size msg
							resouceHitNumber = resultArrayList.size()<2?0:(resultArrayList.size()-2);
						}
						else {	//command.get("command").equals("SUBSCRIBE")
							//define resource count
							//if arrayList size <1
							//error or no hit resources
							//otherwise have resource
							//reduce 1 response count
							resouceHitNumber = resultArrayList.size()<2?0:(resultArrayList.size()-1);
						}

						
						for (Iterator<JSONObject> iterator = resultArrayList.iterator(); iterator.hasNext();) {
							JSONObject jsonMsg = iterator.next();
							if (ServerDebugModel) {
								logger.setLevel(Level.INFO);
								logger.info("Send MSG:" + jsonMsg.toJSONString());
							}
							output.writeUTF(jsonMsg.toJSONString());
							output.flush();
							if(jsonMsg.containsKey("resultSize")){
								System.out.println("End by size read");
								unfinishFlag = false;
							}
						}

					}

					
					
					
					
				}	//if 

				if(null!=command){
					// judge whether the subcribeList contains
					// the id of the client in this thread
					// if no, close the clientsocket and close the thread
					if (command.containsKey("id")&&null!=command.get("id")) {
						id = command.get("id").toString();
						if (command.get("command").equals("SUBSCRIBE")) {
							if(null == cclient){
//								System.out.println("subscribe class create");
								cclient = new connectedClient(resultSize,id);
								notifyList.put(id, cclient);
							}
							else{
//								System.out.println("subscribe class already exist");
								if (null!= cclient && cclient.id != null) {
//									System.out.println("subscribe class valid");
									if(cclient.resources.size()>0){
										System.out.println("subscribe class recieve new msg");
										JSONObject jsonMsg = new JSONObject();
										ArrayList<Resource> resources = cclient.getandRefreshResources();
										for(Resource resouce: resources){
											output.writeUTF(resourcePack(resouce).toJSONString());
											output.flush();										
										}
									}
//									System.out.println("subscribe routine loop");
								}

							}
							unfinishFlag = subscribeList.containsKey(id);

						} 
					}
				}			
			}//while loop end
			clientSocket.close();
			System.out.println(id + " close");
		} catch (IOException | ParseException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}

	private ArrayList<JSONObject> handleSubscribe(JSONObject jsonObject, DataOutputStream output) {
		ArrayList<JSONObject> results = new ArrayList<>();
		ArrayList<Resource> resultResources = new ArrayList<>();
		Boolean relayMode = DEFAULT_RELAY_MODE;
		if (jsonObject.get("id") != null && !jsonObject.get("id").equals("")) {
			String id = jsonObject.get("id").toString();
			// TODO: to judge the valid of the jsonObject
			try {
				IResourceTemplate resource = ServerOperationHandler.subscribe(jsonObject);
				subscribeList.put(id, resource);
				if (!jsonObject.containsKey("resourceTemplate")) {
					JSONObject responseMsg = new JSONObject();
					responseMsg.put("response", "error");
					responseMsg.put("errorMessage", "missing resourceTemplate");
					results.add(responseMsg);
				} else {
					if (null != jsonObject.get("relay")) {
						relayMode =(jsonObject.get("relay").equals("true")) ? true : false;
						System.out.println("Subscribe: init Relay " + (relayMode ? "On" : "Off"));
					}
					// TODO: @yuchao handle query function
					Resource[] hitResources = this.resourceWarehouse.FindReource(resource);
					if (null != hitResources) {
						String serverInfo = ServerHost.getHostAddress() + ":" + this.cmds.port;
						for (Resource hitresource : hitResources) {
							// if (!hitresource.getOwner().equals(null) &&
							// !hitresource.getOwner().equals("")) {
							hitresource.setOwner("*");
							hitresource.setEZServer(serverInfo);
							// }
							resultResources.add(hitresource);
						}
					}
					if (relayMode) {
						JSONObject relayjsonObject = (JSONObject) jsonObject.clone();
						relayjsonObject.replace("relay", "false");
						if (ServerDebugModel) {
							logger.setLevel(Level.INFO);
							logger.info("Start Realy");
						}
						ArrayList<Resource> relayResources = queryOtherServers(jsonObject, Servers);
						if (ServerDebugModel) {
							logger.setLevel(Level.INFO);
							logger.info("relayResources Total Number: " + relayResources.size());
							logger.info("End Relay");
						}
						resultResources.addAll(relayResources);
					}

									
					
					JSONObject responseMsg = new JSONObject();
					responseMsg.put("response", "success");
					responseMsg.put("id", id);
					results.add(responseMsg);
					
					if (null != resultResources && resultResources.size() > 0) {
						for (Resource resultResource : resultResources) {
							results.add(resourcePack(resultResource));
						}
					}
				}
			} catch (OperationRunningException e) {
				JSONObject responseMsg = new JSONObject();
				responseMsg.put("response", "error");
				responseMsg.put("errorMessage", e.toString());
				results.add(responseMsg);
			}
		} else {
			JSONObject responseMsg = new JSONObject();
			responseMsg.put("response", "error");
			responseMsg.put("errorMessage", "missing id");
			results.add(responseMsg);
		}
		return results;
	}

	private JSONObject handleUnsubscribe(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		String id = jsonObject.get("id").toString();
		// remove id and resource into the subscribeList
		subscribeList.remove(id);
		int size = getSubListSize(subscribeList);
		//System.out.println("the current size =" + size);
		connectedClient cclient = notifyList.get(id);
		int resultSize = cclient.resultSize;
		if (size == 0) {
			results.put("resultSize", resultSize);
		}
		return results;
	}

	private JSONObject handlePublish(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		try {
			Resource resource = ServerOperationHandler.publish(jsonObject);
			if (resourceWarehouse.AddResource(resource)) {
				results.put("response", "success");
				notifyAllSubscribe(resource);
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

	private ArrayList<JSONObject> handleQuery(JSONObject jsonObject, DataOutputStream output) {
		ArrayList<JSONObject> results = new ArrayList<>();
		ArrayList<Resource> resultResources = new ArrayList<>();
		Boolean relayMode = DEFAULT_RELAY_MODE;
		if (null != jsonObject.get("relay")) {
			relayMode =
					// false;
					(jsonObject.get("relay").equals("true")) ? true : false;
			System.out.println("Query Relay " + (relayMode ? "On" : "Off"));
		}
		try {
			IResourceTemplate resource = ServerOperationHandler.query(jsonObject);
			Resource[] hitResources = this.resourceWarehouse.FindReource(resource);
			if (null != hitResources) {
				String serverInfo = ServerHost.getHostAddress() + ":" + this.cmds.port;
				for (Resource hitresource : hitResources) {
					// if (!hitresource.getOwner().equals(null) &&
					// !hitresource.getOwner().equals("")) {
					hitresource.setOwner("*");
					hitresource.setEZServer(serverInfo);
					// }
					resultResources.add(hitresource);
				}
			}
			if (relayMode) {
				JSONObject relayjsonObject = (JSONObject) jsonObject.clone();
				relayjsonObject.replace("relay", "false");
				if (ServerDebugModel) {
					logger.setLevel(Level.INFO);
					logger.info("Start Realy");
				}
				ArrayList<Resource> relayResources = queryOtherServers(jsonObject, Servers);
				if (ServerDebugModel) {
					logger.setLevel(Level.INFO);
					logger.info("relayResources Total Number: " + relayResources.size());
					logger.info("End Relay");
				}
				resultResources.addAll(relayResources);
			}
			JSONObject successHead = new JSONObject();
			successHead.put("response", "success");
			results.add(successHead);
			if (null != resultResources && resultResources.size() > 0) {
				for (Resource resultResource : resultResources) {
					results.add(resourcePack(resultResource));
				}
				JSONObject sizeEnd = new JSONObject();
				sizeEnd.put("resultSize", resultResources.size());
				results.add(sizeEnd);
			} else {
				JSONObject sizeEnd = new JSONObject();
				sizeEnd.put("resultSize", 0);
				results.add(sizeEnd);

			}

		} catch (OperationRunningException e) {
			JSONObject errorResponse = new JSONObject();
			// TODO Auto-generated catch block
			errorResponse.put("response", "error");
			errorResponse.put("errorMessage", e.toString());
			results.add(errorResponse);
		} finally {
			if (ServerDebugModel) {
				logger.info(results.get(0).toJSONString());
			}
			return results;
		}

	}

	private ArrayList<Resource> queryOtherServers(JSONObject jsonObject, Set<String> serverList) {
		Callable<ArrayList<Resource>> run = new Callable<ArrayList<Resource>>() {
			@Override
			public ArrayList<Resource> call() throws Exception {
				ArrayList<Resource> relayQueryResources = new ArrayList<>();

				// your code to be timed

				for (String string : serverList) {
					// not query the server self
					if (!string.equals(ServerHost.getHostAddress() + ":" + cmds.port)) {
						System.out.println("Query server: " + string);
						ArrayList<JSONObject> queryResults = queryRelay(jsonObject, string);
						System.out.println("queryRelay size: "+queryResults.size());

							for (int i = 0; i < queryResults.size(); i++) {

								relayQueryResources.add(ServerOperationHandler
										.convertJSONOBjectResourceToResource(queryResults.get(i)));

							}


					}
				}
				if (ServerDebugModel) {
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
		try {
			try {
				foundResources = (ArrayList<Resource>) future.get(DEFAULT_QUERY_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (TimeoutException ex) {
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
					notifyAllSubscribe(resource);
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
			System.out.println(index);
			String selectedServer = "";
			for (int i = 0; i < index; i++) {
				if (Servers.iterator().hasNext()) {
					selectedServer = Servers.iterator().next();
					System.out.println(selectedServer);
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
	private static JSONObject subscribeRelay(JSONObject jsonObject, String server) throws ParseException{
		//TODO: @risheng achieve the function
		return jsonObject;
		
	}
	private static ArrayList<JSONObject> queryRelay(JSONObject jsonObject, String server) throws ParseException {
		ArrayList<JSONObject> resourceJSONList = new ArrayList<>();
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
					if(command.containsKey("response")){
						if(!command.get("response").equals("success")){
							unfinish = false;
						}
					}
					else if(command.containsKey("resultSize")){
						unfinish = false;
					}
					else{
						resourceJSONList.add(command);
					}

				}
			}
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		}
		return resourceJSONList;
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

	private static void printSubList(HashMap<String, IResourceTemplate> subscribeList) {
		Map<String, IResourceTemplate> map = subscribeList;
		Iterator<Map.Entry<String, IResourceTemplate>> entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, IResourceTemplate> entry = entries.next();
			System.out.println("id: " + entry.getKey());
			System.out.println("ResourceTemplate's uri: " + entry.getValue().getURI().toString());
		}
	}

	private static int getSubListSize(HashMap<String, IResourceTemplate> subscribeList) {
		Map<String, IResourceTemplate> map = subscribeList;
		return map.size();
	}

	private static void notifyAllSubscribe(Resource resource) {
		Map<String, IResourceTemplate> map = subscribeList;
		Iterator<Map.Entry<String, IResourceTemplate>> entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, IResourceTemplate> entry = entries.next();
			if (findResourceMatch(resource, entry.getValue())) {
				System.out.println("notify match!");
				// find the selected connectedClient object, and add resource
				// into its resoucelist
				System.out.println("id: " + entry.getKey());
				connectedClient cclient = notifyList.get(entry.getKey());
				cclient.addResource(resource);
				System.out.println(entry.getKey() + "have size of: " +cclient.resources.size());
			}
			;
		}
	}

	private static boolean findResourceMatch(Resource resource, IResourceTemplate resourceTemplate) {
		ResourceWarehouse resourceWarehouse = new ResourceWarehouse();
		resourceWarehouse.AddResource(resource);
		return (null!=resourceWarehouse.FindReource(resourceTemplate));
	}

	private static String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}