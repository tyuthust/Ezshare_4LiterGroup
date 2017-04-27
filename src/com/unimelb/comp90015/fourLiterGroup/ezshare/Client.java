package com.unimelb.comp90015.fourLiterGroup.ezshare;

import com.unimelb.comp90015.fourLiterGroup.ezshare.json.ClientPack;
import com.unimelb.comp90015.fourLiterGroup.ezshare.json.CommandInvalidException;
import com.unimelb.comp90015.fourLiterGroup.ezshare.json.JSONPack;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class Client {
	private ClientCmds cmds;

	public Client(ClientCmds cmds) {
		this.cmds = cmds;
	}

	public void run() {
		for (String string : cmds.servers) {
			System.out.println("server: " + string);
		}
		
		// System.out.println(cmds.host);
		// System.out.println(cmds.port);
	}

	public void connect() throws IOException, CommandInvalidException {
		try (Socket socket = new Socket(this.cmds.host, this.cmds.port);) {
			// Output and Input Stream
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

			output.writeUTF("I want to connect!");
			System.out.println("I want to connect!");
			output.flush();

			JSONPack jsonPack = new ClientPack();

			String message = input.readUTF();
			System.out.println(message);

			// Send RMI to Server
			try {
				output.writeUTF(jsonPack.Pack(this.cmds).toJSONString());
				output.flush();
				JSONParser parser = new JSONParser();
	    		
	    		// Print out results received from server..
	    		while(true){
	    			if(input.available() > 0){

	    	    		String result = input.readUTF();
	    	    		System.out.println("Received from server: "+result);
	    	    		
	    	    		JSONObject command = (JSONObject) parser.parse(result);
	    	    		

	    	    		// Check the command name
	    	    		if(command.containsKey("command_name")){
	    	    			if(command.get("command_name").toString().equals("SENDING_FILE")){
	    	    				
	    	    				// The file location
	    						String fileName = "client_files/"+command.get("file_name");
	    						
	    						// Create a RandomAccessFile to read and write the output file.
	    						RandomAccessFile downloadingFile = new RandomAccessFile(fileName, "rw");
	    						
	    						// Find out how much size is remaining to get from the server.
	    						long fileSizeRemaining = (Long) command.get("file_size");
	    						
	    						int chunkSize = setChunkSize(fileSizeRemaining);
	    						
	    						// Represents the receiving buffer
	    						byte[] receiveBuffer = new byte[chunkSize];
	    						
	    						// Variable used to read if there are remaining size left to read.
	    						int num;
	    						
	    						System.out.println("Downloading "+fileName+" of size "+fileSizeRemaining);
	    						while((num=input.read(receiveBuffer))>0){
	    							// Write the received bytes into the RandomAccessFile
	    							downloadingFile.write(Arrays.copyOf(receiveBuffer, num));
	    							
	    							// Reduce the file size left to read..
	    							fileSizeRemaining-=num;
	    							
	    							// Set the chunkSize again
	    							chunkSize = setChunkSize(fileSizeRemaining);
	    							receiveBuffer = new byte[chunkSize];
	    							
	    							// If you're done then break
	    							if(fileSizeRemaining==0){
	    								break;
	    							}
	    						}
	    						System.out.println("File received!");
	    						downloadingFile.close();
	    	    			}
	    	    		}
	    			}
	    		}
			    
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int setChunkSize(long fileSizeRemaining){
		// Determine the chunkSize
		int chunkSize=1024*1024;
		
		// If the file size remaining is less than the chunk size
		// then set the chunk size to be equal to the file size.
		if(fileSizeRemaining<chunkSize){
			chunkSize=(int) fileSizeRemaining;
		}
		
		return chunkSize;
	}

}
