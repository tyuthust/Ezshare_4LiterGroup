package EZShare;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.commons.cli.ParseException;

import com.unimelb.comp90015.fourLiterGroup.ezshare.ServerClass;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.OptionInterpretor;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ServerOptionInterpretor;

public class Server {

	public static void main(String[] args) throws ParseException, UnknownHostException, CertificateException, UnrecoverableKeyException, IOException {
		OptionInterpretor interpretor;
		interpretor = new ServerOptionInterpretor();
		ServerCmds cmds = (ServerCmds) interpretor.interpret(args);
		ServerClass server = new ServerClass(cmds);
		
		Thread thread = new Thread(() -> {server.setup();});
		thread.start();
		
		Thread SSLthread = new Thread(()->{server.ssetup();});
		SSLthread.start();
	}

}
