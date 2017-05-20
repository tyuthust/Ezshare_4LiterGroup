package com.unimelb.comp90015.fourLiterGroup.ezshare;

import com.unimelb.comp90015.fourLiterGroup.ezshare.json.ClientPack;
import com.unimelb.comp90015.fourLiterGroup.ezshare.json.CommandInvalidException;
import com.unimelb.comp90015.fourLiterGroup.ezshare.json.JSONPack;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;

import jdk.internal.dynalink.beans.StaticClass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.logging.*;

public class ClientClass {
	private ClientCmds cmds;
	private static String DEFAULT_HOST = "127.0.0.1";
	private static int DEFAULT_PORT = 3000;
	private boolean flag = true;

	private static Logger logger = Logger.getLogger(ClientClass.class.getName());

	public ClientClass(ClientCmds cmds) {
		this.cmds = cmds;
	}

	public void run() {
		for (String string : cmds.servers) {
			System.out.println("server: " + string);
		}
	}

	public void connect() throws IOException, CommandInvalidException {
		logger.setLevel(Level.INFO);
		if (cmds.debug) {
			logger.info("setting client debug on. " + "The port: " + cmds.port);
		}
		if (null == this.cmds.host || this.cmds.host.isEmpty()) {
			this.cmds.host = DEFAULT_HOST;
		}
		if (-1 == this.cmds.port) {
			this.cmds.port = DEFAULT_PORT;
		}
		try (Socket socket = new Socket(this.cmds.host, this.cmds.port);) {
			// Output and Input Stream
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

			if (cmds.debug) {
				logger.info("[sent] I want to connect!");
			}
			JSONPack jsonPack = new ClientPack();

			// Send RMI to Server
			try {
				String JsonCmdsString = jsonPack.Pack(this.cmds).toJSONString();
				if (cmds.debug) {
					logger.info("[sent] " + JsonCmdsString);
				}
				output.writeUTF(JsonCmdsString);
				output.flush();
				JSONParser parser = new JSONParser();

				// Print out results received from server..
				while (true == flag) {
					if (input.available() > 0) {

						String result = input.readUTF();
						System.out.println("Received from server: " + result);

						JSONObject command = (JSONObject) parser.parse(result);

						// Check the command name
						if (command.containsKey("command_name")) {
							if (command.get("command_name").toString().equals("SENDING_FILE")) {

								// The file location
								String fileName = "client_files/" + command.get("file_name");
								if (cmds.debug) {
									logger.info("[sent] " + fileName);
								}
								// Create a RandomAccessFile to read and write
								// the output file.
								RandomAccessFile downloadingFile = new RandomAccessFile(fileName, "rw");

								// Find out how much size is remaining to get
								// from the server.
								long fileSizeRemaining = (Long) command.get("file_size");

								int chunkSize = setChunkSize(fileSizeRemaining);

								// Represents the receiving buffer
								byte[] receiveBuffer = new byte[chunkSize];

								// Variable used to read if there are remaining
								// size left to read.
								int num;

								System.out.println("Downloading " + fileName + " of size " + fileSizeRemaining);
								while ((num = input.read(receiveBuffer)) > 0) {
									// Write the received bytes into the
									// RandomAccessFile
									downloadingFile.write(Arrays.copyOf(receiveBuffer, num));

									// Reduce the file size left to read..
									fileSizeRemaining -= num;

									// Set the chunkSize again
									chunkSize = setChunkSize(fileSizeRemaining);
									receiveBuffer = new byte[chunkSize];

									// If you're done then break
									if (fileSizeRemaining == 0) {
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

	public static int setChunkSize(long fileSizeRemaining) {
		// Determine the chunkSize
		int chunkSize = 1024 * 1024;

		// If the file size remaining is less than the chunk size
		// then set the chunk size to be equal to the file size.
		if (fileSizeRemaining < chunkSize) {
			chunkSize = (int) fileSizeRemaining;
		}

		return chunkSize;
	}

}
