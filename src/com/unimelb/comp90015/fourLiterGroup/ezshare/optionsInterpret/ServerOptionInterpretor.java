package com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ServerOptionInterpretor implements OptionInterpretor {

	@Override
	public Cmds interpret(String[] args) throws ParseException {
		Options options = new Options();
		options.addOption("advertisedhostname", true, "advertised hostname");
		options.addOption("connectionintervallimit", true, "connection interval limit in seconds");
		options.addOption("exchangeinterval", true, "exchange interval in seconds");
		options.addOption("port", true, "server port, an integers");
		options.addOption("secret", true, "secret");
		options.addOption("debug", false, "print debug information");
		
		CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        
        //May cause exception: ParseException
      	cmd = parser.parse(options,args);
      	
      	ServerCmds serverCmds = new ServerCmds();
        if(cmd.hasOption("advertisedhostname"))
        	serverCmds.advertisedhostname = cmd.getOptionValue("advertisedhostname");
        if(cmd.hasOption("connectionintervallimit")){
        	//if the string does not contain a parsable integer, throw a NumberFormatException
        	serverCmds.connectionintervallimit = Integer.parseInt(cmd.getOptionValue("connectionintervallimit"));
        }
        if(cmd.hasOption("exchangeinterval")){
        	//if the string does not contain a parsable integer, throw a NumberFormatException
        	serverCmds.exchangeinterval = Integer.parseInt(cmd.getOptionValue("exchangeinterval"));
        }
        if(cmd.hasOption("port")){
        	//if the string does not contain a parsable integer, throw a NumberFormatException
        	serverCmds.port = Integer.parseInt(cmd.getOptionValue("port"));
        }
        if(cmd.hasOption("secret"))
        	serverCmds.secret = cmd.getOptionValue("secret");
        if(cmd.hasOption("debug"))
        	serverCmds.debug = true;
		
		
		return serverCmds;
	}

}
