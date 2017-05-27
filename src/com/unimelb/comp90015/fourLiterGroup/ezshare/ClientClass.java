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
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.logging.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

public class ClientClass {
	private ClientCmds cmds;
	private static String DEFAULT_HOST = "127.0.0.1";
	private static int DEFAULT_PORT = 3000;
	private static int DEFAULT_SPORT = 3781;
	private boolean endWhileLoopFlag = true;
	private static boolean pressEnterFlag = false;

	private SSLSocket sslSocket;

	private static Logger logger = Logger.getLogger(ClientClass.class.getName());
	private static String CLIENT_KEY_STORE_PASSWORD = "4litre";
	private static String CLIENT_TRUST_KEY_STORE_PASSWORD = "4litre";

	public ClientClass(ClientCmds cmds) {
		this.cmds = cmds;
	}

	public void sconnect() {
		int port = DEFAULT_SPORT;
		if (this.cmds.secure) {
			if (-1 != this.cmds.port) {
				port = this.cmds.port;
			}
		}
		if (null == this.cmds.host || this.cmds.host.isEmpty()) {
			this.cmds.host = DEFAULT_HOST;
		}
		if ("localhost" == this.cmds.host) {
			this.cmds.host = DEFAULT_HOST;
		}
		try {
			SSLContext ctx = SSLContext.getInstance("SSL");

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

			KeyStore ks = KeyStore.getInstance("JKS");
			KeyStore tks = KeyStore.getInstance("JKS");

//			InputStream serverkeystoreInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("serverKeystore/serverkeystore");
//			InputStream clientKeystoreInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("clientKeystore/clientkeystore");
//			System.out.println(null!=serverkeystoreInput? "serverkeystoreInput is not null":"serverkeystoreInput is null");
//			System.out.println(null!=clientKeystoreInput? "clientKeystoreInput is not null":"clientKeystoreInput is null");
//			
//			ks.load(clientKeystoreInput, CLIENT_KEY_STORE_PASSWORD.toCharArray());
//			tks.load(serverkeystoreInput,
//					CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
			
			ks.load(new FileInputStream("clientKeystore/clientkeystore.jks"), CLIENT_KEY_STORE_PASSWORD.toCharArray());
			tks.load(new FileInputStream("serverKeystore/serverkeystore.jks"),
					CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());

			kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
			tmf.init(tks);

			ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			sslSocket = (SSLSocket) ctx.getSocketFactory().createSocket(DEFAULT_HOST, port);
		} catch (Exception e) {
			System.out.println(e);
		}
		if (sslSocket == null) {
			System.out.println("ERROR");
			return;
		}
		try {
			InputStream input = sslSocket.getInputStream();
			OutputStream output = sslSocket.getOutputStream();

			BufferedInputStream bis = new BufferedInputStream(input);
			BufferedOutputStream bos = new BufferedOutputStream(output);
			// TODO: ADD JSON PACK
			JSONPack jsonPack = new ClientPack();
			String JsonCmdsString = jsonPack.Pack(this.cmds).toJSONString();
			byte[] jsonB = JsonCmdsString.getBytes();
			//bos.write(jsonB);
			//bos.flush();
			output.write(jsonB);
			output.flush();
			
			JSONObject command = new JSONObject();
			JSONParser parser = new JSONParser();
			/*
			 * byte[] buffer = new byte[2000]; bis.read(buffer);
			 * System.out.println(new String(buffer));
			 */

			// get info form server
			if (this.cmds.subscribe) {
				this.startListen();
				while (!pressEnterFlag) {
					if(bis.read()!= -1){
						byte[] buffer = new byte[bis.available()];
						bis.read(buffer);
						String jsonString = new String(buffer);
						command = (JSONObject) parser.parse(jsonString.trim());
						if (command.containsKey("errorMessage")) {
							pressEnterFlag = true;
						}
						System.out.println(jsonString);
					}
				}
			} else {
				boolean unfinish = true;
				while (unfinish) {
					byte[] buffer = new byte[2048];
					bis.read(buffer);
					String jsonString = new String(buffer);
					command = (JSONObject) parser.parse(jsonString.trim());
					System.out.println(jsonString);

					// find the end of the connection
					if (command.containsKey("response")) {
						if (command.get("response").toString().equals("success") && !this.cmds.fetch
								&& !this.cmds.query) {
							unfinish = false;
						} else if (command.get("response").toString().equals("error")) {
							if (command.containsKey("errorMessage")) {
								unfinish = false;
							}
						}
					} else if (command.containsKey("resultSize") && (this.cmds.unsubscribe || this.cmds.query)) {
						unfinish = false;
					}
				}
				sslSocket.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		} catch (CommandInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connect() throws IOException, CommandInvalidException {
		String id = this.cmds.id;

		logger.setLevel(Level.INFO);
		if (cmds.debug) {
			logger.info("setting client debug on. ");
			logger.info("The IP:" + cmds.host + "\n" + "The port:" + cmds.port);
		}
		if (null == this.cmds.host || this.cmds.host.isEmpty()) {
			this.cmds.host = DEFAULT_HOST;
		}
		if ("localhost" == this.cmds.host) {
			this.cmds.host = DEFAULT_HOST;
		}
		if (-1 == this.cmds.port) {
			this.cmds.port = DEFAULT_PORT;
		}
		try (Socket socket = new Socket(this.cmds.host, this.cmds.port);) {
			// Output and Input Stream
			DataInputStream input = new DataInputStream(socket.getInputStream());
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

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

				// if subscribe function, use startListern to listen console
				// input
				// and if there is any input from console, end while loop
				if (this.cmds.subscribe) {
					this.startListen();
					while (!pressEnterFlag) {
						if (input.available() > 0) {
							String result = input.readUTF();
							System.out.println(result);
							JSONObject command = (JSONObject) parser.parse(result);
							if (command.containsKey("errorMessage")) {
								pressEnterFlag = true;
							}
						}
					}
					/*
					 * JSONObject jsonObject = new JSONObject();
					 * jsonObject.put("command", "UNSUBSCRIBE");
					 * jsonObject.put("id", id);
					 * output.writeUTF(jsonObject.toJSONString());
					 * output.flush();
					 */
					socket.close();
				} else {
					// Print out results received from server..
					while (endWhileLoopFlag) {
						if (input.available() > 0) {

							String result = input.readUTF();
							System.out.println(result);

							JSONObject command = (JSONObject) parser.parse(result);

							// find the end of the connection
							if (command.containsKey("response")) {
								if (command.get("response").toString().equals("success") && !this.cmds.fetch
										&& !this.cmds.query) {
									endWhileLoopFlag = !endWhileLoopFlag;
								} else if (command.get("response").toString().equals("error")) {
									if (command.containsKey("errorMessage")) {
										endWhileLoopFlag = !endWhileLoopFlag;
									}
								}
							} else if (command.containsKey("resultSize")
									&& (this.cmds.unsubscribe || this.cmds.query)) {
								endWhileLoopFlag = !endWhileLoopFlag;
							}

							// Check the command name
							if (command.containsKey("command_name")) {
								if (command.get("command_name").toString().equals("SENDING_FILE")) {

									// The file location
									String fileName = "client_files/" + command.get("file_name");
									if (cmds.debug) {
										logger.info("[sent] " + fileName);
									}
									// Create a RandomAccessFile to read and
									// write
									// the output file.
									RandomAccessFile downloadingFile = new RandomAccessFile(fileName, "rw");

									// Find out how much size is remaining to
									// get
									// from the server.
									long fileSizeRemaining = (Long) command.get("file_size");

									int chunkSize = setChunkSize(fileSizeRemaining);

									// Represents the receiving buffer
									byte[] receiveBuffer = new byte[chunkSize];

									// Variable used to read if there are
									// remaining
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
									endWhileLoopFlag = false;
									System.out.println("File received!");
									downloadingFile.close();
								}
							}
						}
					}
					socket.close();
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

	// Listen the enter from console
	public void startListen() {
		// create a specific thread to listen the consle input
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				scanner.nextLine();
				pressEnterFlag = true;
				scanner.close();
			}
		});
		thread.start();
	}

}
