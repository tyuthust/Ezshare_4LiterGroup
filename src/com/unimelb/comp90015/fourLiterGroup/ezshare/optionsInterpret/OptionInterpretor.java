package com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret;

import org.apache.commons.cli.ParseException;

public interface OptionInterpretor {

	public Cmds interpret(String[] args) throws ParseException;
}
