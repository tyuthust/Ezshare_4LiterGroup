package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import java.util.List;

import com.unimelb.comp90015.fourLiterGroup.ezshare.utils.utils;

public class Resource {
	private String Name = null;
	private String Description = null;
	private String[] Tags = null;
	private String URI = null;
	private String Channel = null;
	private String Owner = null;
	private String[] EZserver = null;
	private int resourceSize=0;
	public Resource() {// Constructor
	}

	public void setName(String name) {
		this.Name = name;
		formatStringOfResource(this.Name);
	}

	public void setDescription(String description) {
		this.Description = description;
		formatStringOfResource(this.Description);
	}

	public void setTags(String[] tags) {
		this.Tags=tags.clone();
		if(null != this.Tags){
			for (String string : this.Tags) {
				formatStringOfResource(string);
			}
		}
	}

	public void setURI(String uri) {
		this.URI = uri;
		formatStringOfResource(this.URI);
	}

	public void setChannel(String channel) {
		this.Channel = channel;
		formatStringOfResource(this.Channel);
	}

	public void setOwner(String owner) {
		this.Owner = owner;
		formatStringOfResource(this.Owner);
		
	}

	public void setEZServer(String[] ezservers) {
		this.EZserver = ezservers.clone();
		if(null != this.EZserver){
			for (String string : this.EZserver) {
				formatStringOfResource(string);
			}
		}
	}
	
	public void setResourceSize(int size){
		this.resourceSize=size;
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
	
	public String[] getEzserver(){
		return this.EZserver;
	}
	
	public String getURI(){
		return this.URI;
	}
	
	public String[] getTags(){
		return this.Tags;
	}
	
	public int getSize(){
		return this.resourceSize;
	}
	
	private static void formatStringOfResource(String unformatedString) {

		// String values must not contain the "\0" character,
		// nor start or end with whitespace.
		// The server may silently remove such characters

		// remove all start and end whitespace
		// remove "\0"

		utils.trimFirstAndLastChar(unformatedString, " ");
		if (null != unformatedString) {
			((String) unformatedString).replaceAll("\0", "");
			unformatedString += "\0";
		}
	}
}
