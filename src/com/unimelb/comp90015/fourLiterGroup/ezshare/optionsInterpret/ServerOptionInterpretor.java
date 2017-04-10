package com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret;

public class ServerOptionInterpretor implements OptionInterpretor {

	@Override
	public Cmds interpret(String[] args) {
		// TODO Auto-generated method stub
		
		return new ServerCmds();
	}

}
