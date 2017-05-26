package EZShare;

import java.net.UnknownHostException;

import org.apache.commons.cli.ParseException;

import com.unimelb.comp90015.fourLiterGroup.ezshare.ServerClass;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.OptionInterpretor;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerOptionInterpretor;

public class Server {

	public static void main(String[] args) throws ParseException, UnknownHostException {
		OptionInterpretor interpretor;
		interpretor = new ServerOptionInterpretor();
		ServerCmds cmds = (ServerCmds) interpretor.interpret(args);
		ServerClass server = new ServerClass(cmds);
		//server.run();
		Thread thread = new Thread(() -> {server.setup();});
		thread.start();

	}

}
