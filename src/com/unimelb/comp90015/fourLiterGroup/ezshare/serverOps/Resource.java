package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import com.unimelb.comp90015.fourLiterGroup.ezshare.utils.utils;

public class Resource implements IResourceTemplate {
	private String Name;
	private String Description;
	private String[] Tags = null;
	private String URI;
	private String Channel;
	private String Owner;
	private String EZserver = null;
	private long resourceSize;

	public Resource() {
		this.Name = null;
		this.Description = null;
		this.URI = null;
		this.Channel = null;
		this.Owner = null;
		this.resourceSize = 0;
	}

	public void setName(String name) {
		this.Name = name;
		//formatStringOfResource(this.Name);
	}

	public void setDescription(String description) {
		this.Description = description;
		//formatStringOfResource(this.Description);
	}

	public void setTags(String[] tags) {
		this.Tags = tags.clone();
		if (null != this.Tags) {
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

	public void setEZServer(String ezserver) {
		this.EZserver = ezserver;

	}

	public void setResourceSize(long size) {
		this.resourceSize = size;
	}

	public String getName() {
		return this.Name;
	}

	public String getDescription() {
		return this.Description;
	}

	public String getChannel() {
		return this.Channel;
	}

	public String getOwner() {
		return this.Owner;
	}

	public String getEzserver() {
		return this.EZserver;
	}

	public String getURI() {
		return this.URI;
	}

	public String[] getTags() {
		return this.Tags;
	}

	public long getSize() {
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
