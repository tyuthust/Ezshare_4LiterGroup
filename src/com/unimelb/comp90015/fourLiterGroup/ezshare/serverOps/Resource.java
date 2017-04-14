package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

public class Resource {
	private String Name = null;
	private String Description = null;
	private String[] Tags = null;
	private String URI = null;
	private String Channel = null;
	private String Owner = null;
	private String EZserver = null;

	public Resource() {// Constructor
	}

	public void setName(String name) {
		this.Name = name;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public void setTags(String[] tags) {
		this.Tags = tags.clone();
		System.out.println(tags.toString());
	}

	public void setURI(String uri) {
		this.URI = uri;
	}

	public void setChannel(String channel) {
		this.Channel = channel;
	}

	public void setOwner(String owner) {
		this.Owner = owner;
	}

	public void setEZServer(String ezserver) {
		this.EZserver = ezserver;
	}
	
	
	public String getName(){
		return this.Name;
	}
	
	public String getDescription(){
		return this.Description;
	}
	
	public String getChannel(){
		return this.Channel;
	}
	
	public String getOwner(){
		return this.Owner;
	}
	
	public String getEZShare(){
		return this.EZserver;
	}
	
	public String getURI(){
		return this.URI;
	}
}
