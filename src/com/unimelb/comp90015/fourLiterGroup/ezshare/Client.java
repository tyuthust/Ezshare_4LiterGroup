package com.unimelb.comp90015.fourLiterGroup.ezshare;

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;

public class Client {
	private ClientCmds cmds;
	
	public Client(ClientCmds cmds){
		this.cmds = cmds;
	}
	
	public void run(){
		for (String string : cmds.servers) {
			System.out.println("server: "+ string);
		}
	}

}
