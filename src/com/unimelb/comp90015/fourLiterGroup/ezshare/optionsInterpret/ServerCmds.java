package com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret;

import com.sun.glass.ui.TouchInputSupport;

public class ServerCmds extends Cmds {
	public static int DEFAULT_CONNECTION_INTERVAL_LIMIT_SECONDS = 0;
	public static int DEFAULT_EXCHANGE_INTERVAL_SECONDS = 0;

//	The server must work exactly with the following command line options:
//		-advertisedhostname <arg> advertised hostname
//		-connectionintervallimit <arg> connection interval limit in seconds
//		-exchangeinterval <arg> exchange interval in seconds
//		-port <arg> server port, an integer
//		-secret <arg> secret
//		-debug print debug information
	
	public String advertisedhostname = null;
	public int connectionintervallimit = DEFAULT_CONNECTION_INTERVAL_LIMIT_SECONDS;
	public int exchangeinterval = DEFAULT_EXCHANGE_INTERVAL_SECONDS;
//	public int port;		//Base class
//	public String secret;	//Base class
//	public boolean debug;	//Base class
	
	
}
