package com.unimelb.comp90015.fourLiterGroup.ezshare;

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;

public class Server {
	private ServerCmds cmds;
	
	public Server(ServerCmds cmds){
		this.cmds = cmds;
	}
	
	public void run(){
		System.out.println(cmds.port);
	}

}
