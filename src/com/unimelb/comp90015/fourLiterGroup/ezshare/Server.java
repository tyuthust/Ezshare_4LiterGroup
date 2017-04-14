package com.unimelb.comp90015.fourLiterGroup.ezshare;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps.Resource;

public class Server {
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
		try (ServerSocket server = factory.createServerSocket(this.cmds.port)) {
			System.out.println("Waiting for client connection..");

			// Wait for connections.
			while (true) {
				Socket client = server.accept();
				counter++;
				System.out.println("Client " + counter + ": Applying for connection!");

				// Start a new thread for a connection
				Thread t = new Thread(() -> serveClient(client));
				t.start();
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

					// Operate based on the JSON command

					// Response to the client

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
		JSONObject result = new JSONObject();
		System.out.println("Publish function");
		//Resource resource = new Resource();
		//resource.jsonObject.get("name").toString()
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
		System.out.println("Fetch function");
		if (true) {
			result.put("response", "successful");
		}
		return result;
	}
}
