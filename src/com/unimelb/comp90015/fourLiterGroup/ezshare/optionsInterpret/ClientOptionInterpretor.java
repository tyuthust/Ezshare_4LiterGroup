package com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ClientOptionInterpretor implements OptionInterpretor {

	@Override
	public Cmds interpret(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		Options options = new Options();
		options.addOption("channel", true, "channel");
		options.addOption("debug",false,"print debug information");
		options.addOption("description", true, "resource description");
		options.addOption("exchange",false,"exchange server list with server");
		options.addOption("fetch",false,"fetch resources from server");
		options.addOption("host", true, "server host, a domain name or IP address");
		options.addOption("name",true,"resource name");
		options.addOption("owner", true, "owner");
		options.addOption("port", true, "server port, an integer");
		options.addOption("publish",false,"publish resource on server");
		options.addOption("query",false,"query for resources from server");
		options.addOption("remove",false,"remove resource from server");
		options.addOption("secret", true, "secret");
		options.addOption("servers", true, "server list, host1:port1,host2:port2,...");
		options.addOption("share",false,"share resource on server");
		options.addOption("tags", true, "resource tags, tag1,tag2,tag3,...");
		options.addOption("uri", true, "resource URI");
		
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        //May cause exception!
		cmd = parser.parse(options,args);

        ClientCmds clientCmds = new ClientCmds();
        if(cmd.hasOption("channel")){
        	clientCmds.channel = cmd.getOptionValue("channel");
        }
		return clientCmds;
	}

}
