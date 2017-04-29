package com.unimelb.comp90015.fourLiterGroup.ezshare.json;

public class CommandInvalidException extends Exception {

	private String description;
	public CommandInvalidException(String des){
		super(des);
		this.description = des;
	}
	
	public String toString(){
		return this.description;
	}
}
