package com.unimelb.comp90015.fourLiterGroup.ezshare;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.Resource;

public class Server {
	public static int MAX_THREADS = 20;
	private ServerCmds cmds;
	// Identifies the user number connected
	private static int counter = 0;

	public Server(ServerCmds cmds) {
		this.cmds = cmds;
	}

	public void run() {
		System.out.println(cmds.port);
	}

	public void setup() {
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		//start a Thread Pool with fixed quantity of threads
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(MAX_THREADS);
		try (ServerSocket server = factory.createServerSocket(this.cmds.port)) {
			System.out.println("Waiting for client connection..");

			// Wait for connections.
			while (true) {
				Socket client = server.accept();
				counter++;
				System.out.println("Client " + counter + ": Applying for connection!");

				// Start a new thread for a connection in the thread pool
				Thread t = new Thread(() -> serveClient(client));
				t.start();
				fixedThreadPool.execute(t);
				//need to be shutdown after C/S connection done.
				//fixedThreadPool.shutdown(); 
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void serveClient(Socket client) {
		try (Socket clientSocket = client) {

			// The JSON Parser
			JSONParser parser = new JSONParser();
			// Input stream
			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
			// Output Stream
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("CLIENT: " + input.readUTF());
			output.writeUTF("Server: Hi Client " + counter + " !!!");

			// Receive more data..
			while (true) {
				if (input.available() > 0) {
					// Attempt to convert read data to JSON
					JSONObject command = (JSONObject) parser.parse(input.readUTF());
					System.out.println("COMMAND RECEIVED: " + command.toJSONString());

					JSONObject results = new JSONObject();// return json pack
					if (command.get("command").equals("PUBLISH")) {//
						results = publish(command);
					} else if (command.get("command").equals("QUERY")) {
						results = query(command);
					} else if (command.get("command").equals("REMOVE")) {
						results = remove(command);
					} else if (command.get("command").equals("SHARE")) {
						results = share(command);
					} else if (command.get("command").equals("FETCH")) {
						results = fetch(command);
					} else if (command.get("command").equals("EXCHANGE")) {
						results = exchange(command);
					}
					output.writeUTF(results.toJSONString());
				}
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private static JSONObject publish(JSONObject jsonObject) {// publish
																// function:
																// need to be
																// achieved
		System.out.println("Publish function");

		// create a jsonobject to save the map in resource
		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.putAll((Map) jsonObject.get("resource"));
		System.out.println(jsonObject1);

		// create a new resource and set its value
		Resource resource = new Resource();
		resource.setName(jsonObject1.get("name").toString());
		System.out.println("The resource name:" + resource.getName());

		// clone the jsonobject to a hashmap
		Map map = new HashMap();
		map = (Map) jsonObject1.clone();

		if (map.get("channel") != null) {// otherwise, there is an exception
											// when channel is null
			resource.setChannel(jsonObject1.get("channel").toString());
		}
		System.out.println("The resource channel:" + resource.getChannel());

		resource.setDescription(jsonObject1.get("description").toString());
		System.out.println("The resource description:" + resource.getDescription());

		if (map.get("owner") != null) {
			resource.setOwner(jsonObject1.get("owner").toString());
		}
		System.out.println("The resource owner:" + resource.getOwner());

		resource.setURI(jsonObject1.get("uri").toString());
		System.out.println("The resource uri:" + resource.getURI());

		// ezserver will not be transported when using publish command
		System.out.println("The resource ezserver:" + "null");

		if (map.get("tags") != null) {
			JSONArray jsonArray = new JSONArray();
			jsonArray = (JSONArray) jsonObject1.get("tags");
			String[] tags = new String[jsonArray.size()];
			for (int i = 0; i < jsonArray.size(); i++) {
				String r = jsonArray.get(i).toString();
				tags[i] = r;
			}
			resource.setTags(tags);
		}

		List<String> tagList = new ArrayList<String>();
		for (String string : resource.getTags()) {
			tagList.add(string);
		}
		System.out.println("The resource:" + tagList.toString());

		JSONObject result = new JSONObject();
		if (true) {
			result.put("response", "successful");
		}

		return result;
	}

	private static JSONObject query(JSONObject jsonObject) {// query function:
															// need to be
															// achieved
		JSONObject result = new JSONObject();
		System.out.println("Query function");
		if (true) {
			result.put("response", "successful");
		}
		return result;
	}

	private static JSONObject remove(JSONObject jsonObject) {// remove function:
																// need to be
																// achieved
		JSONObject result = new JSONObject();
		System.out.println("Remove function");
		if (true) {
			result.put("response", "successful");
		}
		return result;
	}

	private static JSONObject share(JSONObject jsonObject) {// share function:
															// need to be
															// achieved
		JSONObject result = new JSONObject();
		System.out.println("Share function");
		if (true) {
			result.put("response", "successful");
		}
		return result;
	}

	private static JSONObject fetch(JSONObject jsonObject) {// share function:
															// need to be
															// achieved
		JSONObject result = new JSONObject();
		System.out.println("Fetch function");
		if (true) {
			result.put("response", "successful");
		}
		return result;
	}

	private static JSONObject exchange(JSONObject jsonObject) {// share
																// function:
																// need to be
																// achieved
		JSONObject result = new JSONObject();
		System.out.println("Exchange function");
		Resource resource = new Resource();

		int counter=0;
		JSONArray jsonArray = new JSONArray();
		jsonArray = (JSONArray) jsonObject.get("serverList");
		List<JSONObject> jsonobjectList = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonobjectList.add((JSONObject) jsonArray.get(i));
		}
		String[] ezservers=new String[jsonArray.size()];
		for(JSONObject jsonOb: jsonobjectList){
			ezservers[counter]=jsonOb.get("hostname").toString()+":"+jsonOb.get("port").toString();
			counter++;
		}
		resource.setEZServer(ezservers);
		List<String> resList = new ArrayList<String>();
		for(String string:resource.getEZShare()){
			resList.add(string); 
		}
		System.out.println("The resource ezservers:" + resList.toString());
		if (true) {
			result.put("response", "successful");
		}
		return result;
	}
}
