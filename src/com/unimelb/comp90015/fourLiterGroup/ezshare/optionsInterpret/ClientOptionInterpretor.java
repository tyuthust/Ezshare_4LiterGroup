package com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ClientOptionInterpretor implements OptionInterpretor {
	
	private static int MAX_PORT_VALUE = 65535;

	@Override
	public Cmds interpret(String[] args) throws ParseException,NumberFormatException {
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

        //May cause exception: ParseException
		cmd = parser.parse(options,args);

        ClientCmds clientCmds = new ClientCmds();
        if(cmd.hasOption("channel"))
        	clientCmds.channel = cmd.getOptionValue("channel");
        if(cmd.hasOption("debug"))
        	clientCmds.debug = true;
        if(cmd.hasOption("description"))
        	clientCmds.description = cmd.getOptionValue("description");
        if(cmd.hasOption("exchange"))
        	clientCmds.exchange = true;
        if(cmd.hasOption("fetch"))
        	clientCmds.fetch = true;
        if(cmd.hasOption("host")){//TODO: judge whether the host is legal
        	String hostpara = cmd.getOptionValue("host");
        	if(isStringDomain(hostpara)||isStringIPAddr(hostpara)){
        		clientCmds.host = hostpara;
        	}
        }
        if(cmd.hasOption("name"))
        	clientCmds.name = cmd.getOptionValue("name");
        if(cmd.hasOption("owner"))
        	clientCmds.owner = cmd.getOptionValue("owner");
        if(cmd.hasOption("port")){
        	//if the string does not contain a parsable integer, throw a NumberFormatException
        	clientCmds.port = Integer.parseInt(cmd.getOptionValue("port"));
        }
        if(cmd.hasOption("publish"))
        	clientCmds.publish = true;
        if(cmd.hasOption("query"))
        	clientCmds.query = true;
        if(cmd.hasOption("remove"))
        	clientCmds.remove = true;
        if(cmd.hasOption("secret"))
        	clientCmds.secret = cmd.getOptionValue("secret");
        if(cmd.hasOption("servers")){
        	ArrayList<String> validServerArray = new ArrayList<>();

        	String[] servers = cmd.getOptionValue("servers").split(",");
        	for (String string : servers) {
				String[] DomainAndPort = string.split(":");
				//the string is correct in server syntax
				//separated with ":" there should be two parts
				//first part should be valid domain or IP address
				//second part should be valid port number
				if( 
						(isStringDomain(DomainAndPort[0])||isStringIPAddr(DomainAndPort[0]))&&
						(isStringPort(DomainAndPort[1]))&&
						2==DomainAndPort.length
						){
					if(isStringIPAddr(DomainAndPort[0])){
						//remove all heading zero of each number
						String[] tempNums = (DomainAndPort[0]).split("\\.");
						String refinedIP = "";
						for (int i = 0; i < tempNums.length; i++) {
							refinedIP += Integer.parseInt(tempNums[i]);
							if(i<tempNums.length-1){
								//add "." unless last part of the number
								refinedIP +=".";
							}
							
						}
						DomainAndPort[0] = refinedIP;
						//DomainAndPort[1] = DomainAndPort[1].replaceFirst("0+(?!$)", "");
						DomainAndPort[1] = DomainAndPort[1].replaceAll("^(0+)", "");;
					}
					validServerArray.add(DomainAndPort[0]+":"+DomainAndPort[1]);
				}
			}
        	if(!validServerArray.isEmpty()){
        		clientCmds.servers = validServerArray.toArray(new String[validServerArray.size()]);
        	}
        	
        }
        if(cmd.hasOption("share"))
        	clientCmds.share = true;
        if(cmd.hasOption("tags")){
        	clientCmds.tags = cmd.getOptionValue("tags").split(",");
        	
        }
        if(cmd.hasOption("uri"))
        	clientCmds.uri = cmd.getOptionValue("uri");
        
		return clientCmds;
	}
	
	private boolean isStringDomain(String string){
		String regex = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$";
		return Pattern.matches(regex, string);
	}
	
	private boolean isStringIPAddr(String string){
		String IPADDRESS_PATTERN =
					"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		return Pattern.compile(IPADDRESS_PATTERN).matcher(string).matches();
	}
	
	private boolean isStringPort(String string){
		boolean isPort = false;
		try {
			int portNum = Integer.parseInt(string);
			if(portNum>0&&portNum<=MAX_PORT_VALUE){
				isPort = true;
			}
		} catch (Exception e) {
		}
		return isPort;
	}

}
