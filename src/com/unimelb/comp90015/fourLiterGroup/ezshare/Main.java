package com.unimelb.comp90015.fourLiterGroup.ezshare;

import org.apache.commons.cli.Options;

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
			if(null!= cmds.channel){
				System.out.println("Channel is "+ cmds.channel);
			}
		}
		else if(args[0].equals("EZShare.Server")){
			interpretor = new ServerOptionInterpretor();
			ServerCmds cmds = (ServerCmds) interpretor.interpret(args);
		}
		else {
			throw new Exception();
		}
		
	}

}
