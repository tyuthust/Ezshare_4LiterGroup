package com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret;

public class ClientCmds extends Cmds {
//	
//	The client must work exactly with the following command line options:
//		-channel <arg> channel
//		-debug print debug information
//		-description <arg> resource description
//		-exchange exchange server list with server
//		-fetch fetch resources from server
//		-host <arg> server host, a domain name or IP address
//		-name <arg> resource name
//		-owner <arg> owner
//		-port <arg> server port, an integer
//		-publish publish resource on server
//		-query query for resources from server
//		-remove remove resource from server
//		-secret <arg> secret
//		-servers <arg> server list, host1:port1,host2:port2,...
//		-share share resource on server
//		-tags <arg> resource tags, tag1,tag2,tag3,...
//		-uri <arg> resource URI
	
	public String channel = null;
//	public boolean debug;			//Base class
	public String description = null;
	public boolean exchange = false;
	public boolean fetch = false;
	public String host = null;
	public String name = null;
	public String owner = null;
//	public int port;				//Base class
	public boolean publish = false;
	public boolean query = false;
	public boolean remove = false;
//	public String secret;			//Base class
	public String[] servers = null;	// each element in servers means one server host:port
	public boolean share = false;
	public String[] tags = null;	// each element in tags means one tag
	public String uri = null;
	
	// Project2 
	public String id;
	public boolean subscribe = false;
	public boolean unsubscribe = false;
}
