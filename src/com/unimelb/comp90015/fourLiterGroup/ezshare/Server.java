package com.unimelb.comp90015.fourLiterGroup.ezshare;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;

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
	
	private static void serveClient(Socket client){
		try(Socket clientSocket = client){
			
			// The JSON Parser
			JSONParser parser = new JSONParser();
			// Input stream
			DataInputStream input = new DataInputStream(clientSocket.
					getInputStream());
			// Output Stream
		    DataOutputStream output = new DataOutputStream(clientSocket.
		    		getOutputStream());
		    System.out.println("CLIENT: "+input.readUTF());
		    output.writeUTF("Server: Hi Client "+counter+" !!!");
		    
		    // Receive more data..
		    while(true){
		    	if(input.available() > 0){
		    		// Attempt to convert read data to JSON
		    		JSONObject command = (JSONObject) parser.parse(input.readUTF());
		    		System.out.println("COMMAND RECEIVED: "+command.toJSONString());

		    		JSONObject results = new JSONObject();
		    		results.put("response", "successful");
		    		output.writeUTF(results.toJSONString());
		    	}
		    }
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
}
