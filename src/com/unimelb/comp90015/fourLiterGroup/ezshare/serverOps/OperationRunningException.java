package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

public class OperationRunningException extends Exception {
	
	private String description;
	public OperationRunningException(String description) {
		// TODO Auto-generated constructor stub
		super(description);
		this.description = description;
	}
	
	public String toString(){
		return this.description;
	}
}
