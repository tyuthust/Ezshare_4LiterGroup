package com.unimelb.comp90015.fourLiterGroup.ezshare;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.logging.log4j.*;

import com.sun.glass.ui.TouchInputSupport;
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
	//TODO: to config logger
	private static Logger logger = LogManager.getLogger();
	// Resource Map
	private ResourceWarehouse resourceWarehouse;
	// Server List
	private String[] Servers = null;

	public Server(ServerCmds cmds) {
		resourceWarehouse = new ResourceWarehouse();
		this.cmds = cmds;
		if (null == this.cmds.secret) {
			this.cmds.generateSecret();
		}
	}

	public void run() {
		System.out.println(cmds.port);
	}

	public void setup() {
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		// start a Thread Pool. Threads that have not been used for more than
		// sixty seconds are terminated and removed from the cache.
		ExecutorService ThreadPool = Executors.newCachedThreadPool();

		try (ServerSocket server = factory.createServerSocket(this.cmds.port)) {
			if(cmds.debug){
				logger.info("setting server debug on. ");
				logger.info("The port is: " + cmds.port);
			}
			System.out.println("Waiting for client connection..");

			// Wait for connections.
			while (true) {
				Socket client = server.accept();
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
			if(cmds.debug){
				logger.info("[sent]"+"Server: Hi Client " + counter + " !!!");
			}
			output.writeUTF("Server: Hi Client " + counter + " !!!");

			// Receive more data..
			while (true) {
				if (input.available() > 0) {
					// Attempt to convert read data to JSON
					JSONObject command = (JSONObject) parser.parse(input.readUTF());
					System.out.println("COMMAND RECEIVED: " + command.toJSONString());

					JSONObject results = new JSONObject();// return json pack

					// TODO: change to ServerOperationHandler
					if (command.get("command").equals("PUBLISH")) {
						results = handlePublish(command,output);
						// results = publish(command);
					} else if (command.get("command").equals("QUERY")) {
						// results = query(command);
					} else if (command.get("command").equals("REMOVE")) {
						// results = remove(command);
					} else if (command.get("command").equals("SHARE")) {
						results = handleShare(command,output);
						// results = share(command);
					} else if (command.get("command").equals("FETCH")) {
						results = handleFetch(command, output);

						// results = fetch(command);
					} else if (command.get("command").equals("EXCHANGE")) {
						results = handleExchange(command,output);
						// results = exchange(command);
					}
					//output.writeUTF(results.toJSONString());
				}
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private JSONObject handlePublish(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		try {
			Resource resource = ServerOperationHandler.publish(jsonObject);
			if (resourceWarehouse.AddResource(resource)) {
				results.put("response", "success");
			} else {
				results.put("response", "error");
				results.put("errorMessage", "invalid resource");

			}
		} catch (OperationRunningException e) {
			results.put("response", "error");
			results.put("errorMessage", e.toString());
		}
		return results;
	}
	
	private JSONObject handleQuery(JSONObject jsonObject, DataOutputStream output){
		JSONObject results = new JSONObject();
		ArrayList<Resource> resultResources = new ArrayList<>();
		Boolean relayMode = DEFAULT_RELAY_MODE;
		if(null!=  jsonObject.get("relay")){
			relayMode = jsonObject.get("relay") == "false" ? false:true;
		}
		try {
			IResourceTemplate resource = ServerOperationHandler.query(jsonObject);
			Resource[] hitResources = this.resourceWarehouse.FindReource(resource);
			if(null != hitResources){
				for (Resource hitresource : hitResources) {
					if(!hitresource.getOwner().equals(null)&&!hitresource.getOwner().equals("")){
						hitresource.setOwner("*");
					}
					resultResources.add(hitresource);
				}
			}
			
			if(relayMode){
				//TODO: Ask servers in ServerList for query
					//Change query jsonobject 
					//send to servers
					//set timeout
					//collectAllResource
					//modify all named owner with "*"
					//package in JSONObject
			}
			
			results.put("response", "success");
			if(null!= hitResources&&hitResources.length>0 ){
				for (Resource hitResource : hitResources) {
					results.put("resource", resourcePack(hitResource));
				}
				results.put("resultSize", hitResources.length);
			}
			else {
				results.put("resultSize", 0);
			}

			
			
		} catch (OperationRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
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
				// TODO: check resource
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

		return results;

	}

	private JSONObject handleFetch(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();

		try {
			Resource resource = ServerOperationHandler.fetch(jsonObject);
			// TODO: download function
			// Just for test
			String uri = resource.getURI();
			String filename = uri.replaceFirst("file:///", "");
			File f = new File(filename);

			if (f.exists()) {

				// Send this back to client so that they know what the file is.
				results.put("response", "success");
				results.put("resource", resourcePack(resource));
				results.put("resultSize", resultSize);
				System.out.println(results.toJSONString());
				try {

					// Start sending file
					RandomAccessFile byteFile = new RandomAccessFile(f, "r");
					byte[] sendingBuffer = new byte[1024 * 1024];
					int num;
					// While there are still bytes to send..
					while ((num = byteFile.read(sendingBuffer)) > 0) {
						System.out.println(num);
						output.write(Arrays.copyOf(sendingBuffer, num));
					}
					byteFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				// Throw an error here..
			}

			/*
			 * if (resourceWarehouse.FindResource(resource.getChannel(),
			 * resource.getURI())) { results.put("response", "success");
			 * results.put("resource", resourcePack(resource));
			 * results.put("resultSize", resultSize);
			 * 
			 * //TODO: download function } else { results.put("response",
			 * "error"); results.put("errorMessage",
			 * "invalid resourceTemplate"); }
			 */
		} catch (OperationRunningException e) {
			results.put("response", "error");
			results.put("errorMessage", e.toString());

		}
		return results;
	}

	private JSONObject handleExchange(JSONObject jsonObject, DataOutputStream output) {
		JSONObject results = new JSONObject();
		try {
			Servers = ServerOperationHandler.exchange(jsonObject).clone();
			if (cmds.debug) {
				for (String string : Servers) {
					logger.info("Server list: "+ string);
				}
			}

			results.put("response", "success");
		} catch (OperationRunningException e) {
			results.put("response", "error");
			results.put("errorMessage", e.toString());

		}
		return results;

	}

	private JSONObject resourcePack(Resource resource) {
		JSONObject results = new JSONObject();
		results.put("name", resource.getName());
		results.put("tags", resource.getTags());
		results.put("description", resource.getDescription());
		results.put("uri", resource.getURI());
		results.put("channel", resource.getChannel());
		results.put("owner", resource.getOwner());
		results.put("ezserver", resource.getEzserver());
		results.put("resourceSize", resource.getSize());

		return results;
	}
	/*
	 * private static JSONObject publish(JSONObject jsonObject) {// publish //
	 * function: // need to be // achieved
	 * System.out.println("Publish function");
	 * 
	 * // create a jsonobject to save the map in resource JSONObject jsonObject1
	 * = new JSONObject(); jsonObject1.putAll((Map) jsonObject.get("resource"));
	 * System.out.println(jsonObject1);
	 * 
	 * // create a new resource and set its value Resource resource = new
	 * Resource(); resource.setName(jsonObject1.get("name").toString());
	 * System.out.println("The resource name:" + resource.getName());
	 * 
	 * // clone the jsonobject to a hashmap Map map = new HashMap(); map = (Map)
	 * jsonObject1.clone();
	 * 
	 * if (map.get("channel") != null) {// otherwise, there is an exception //
	 * when channel is null
	 * resource.setChannel(jsonObject1.get("channel").toString()); }
	 * System.out.println("The resource channel:" + resource.getChannel());
	 * 
	 * resource.setDescription(jsonObject1.get("description").toString());
	 * System.out.println("The resource description:" +
	 * resource.getDescription());
	 * 
	 * if (map.get("owner") != null) {
	 * resource.setOwner(jsonObject1.get("owner").toString()); }
	 * System.out.println("The resource owner:" + resource.getOwner());
	 * 
	 * resource.setURI(jsonObject1.get("uri").toString());
	 * System.out.println("The resource uri:" + resource.getURI());
	 * 
	 * // ezserver will not be transported when using publish command
	 * System.out.println("The resource ezserver:" + "null");
	 * 
	 * if (map.get("tags") != null) { JSONArray jsonArray = new JSONArray();
	 * jsonArray = (JSONArray) jsonObject1.get("tags"); String[] tags = new
	 * String[jsonArray.size()]; for (int i = 0; i < jsonArray.size(); i++) {
	 * String r = jsonArray.get(i).toString(); tags[i] = r; }
	 * resource.setTags(tags); }
	 * 
	 * List<String> tagList = new ArrayList<String>(); for (String string :
	 * resource.getTags()) { tagList.add(string); }
	 * System.out.println("The resource:" + tagList.toString());
	 * 
	 * JSONObject result = new JSONObject(); if (true) { result.put("response",
	 * "successful"); }
	 * 
	 * return result; }
	 * 
	 * 
	 * private static JSONObject query(JSONObject jsonObject) {// query
	 * function: // need to be // achieved JSONObject result = new JSONObject();
	 * System.out.println("Query function"); if (true) { result.put("response",
	 * "successful"); } return result; }
	 * 
	 * private static JSONObject remove(JSONObject jsonObject) {// remove
	 * function: // need to be // achieved JSONObject result = new JSONObject();
	 * System.out.println("Remove function"); if (true) { result.put("response",
	 * "successful"); } return result; }
	 * 
	 * private static JSONObject share(JSONObject jsonObject) {// share
	 * function: // need to be // achieved JSONObject result = new JSONObject();
	 * System.out.println("Share function"); if (true) { result.put("response",
	 * "successful"); } return result; }
	 * 
	 * private static JSONObject fetch(JSONObject jsonObject) {// share
	 * function: // need to be // achieved JSONObject result = new JSONObject();
	 * System.out.println("Fetch function"); if (true) { result.put("response",
	 * "successful"); } return result; }
	 * 
	 */
}
