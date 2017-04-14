package com.unimelb.comp90015.fourLiterGroup.ezshare;

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientOptionInterpretor;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.OptionInterpretor;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerOptionInterpretor;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		OptionInterpretor interpretor;
		if(args[0].equals("EZShare.Client")){
			interpretor = new ClientOptionInterpretor();
			ClientCmds cmds = (ClientCmds) interpretor.interpret(args);
			Client client = new Client(cmds);
			//client.run();
			client.connect();
		}
		else if(args[0].equals("EZShare.Server")){
			interpretor = new ServerOptionInterpretor();
			ServerCmds cmds = (ServerCmds) interpretor.interpret(args);
			Server server = new Server(cmds);
			//server.run();
			server.setup();
		}
		else {
			throw new Exception();
		}
		
	}

}
