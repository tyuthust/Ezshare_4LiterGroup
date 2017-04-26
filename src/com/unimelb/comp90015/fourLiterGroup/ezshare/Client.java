package com.unimelb.comp90015.fourLiterGroup.ezshare;

import com.unimelb.comp90015.fourLiterGroup.ezshare.json.ClientPack;
import com.unimelb.comp90015.fourLiterGroup.ezshare.json.CommandInvalidException;
import com.unimelb.comp90015.fourLiterGroup.ezshare.json.JSONPack;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;
import java.net.UnknownHostException;



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

	public void connect() throws IOException {
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
				
				// Print out results received from server..
				String result = input.readUTF();
				System.out.println("Received from server: " + result);
			} catch (CommandInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}




		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {

		}
	}

}
