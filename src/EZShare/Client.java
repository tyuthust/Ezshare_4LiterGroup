package EZShare;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.commons.cli.ParseException;

import com.unimelb.comp90015.fourLiterGroup.ezshare.ClientClass;
import com.unimelb.comp90015.fourLiterGroup.ezshare.json.CommandInvalidException;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientOptionInterpretor;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.OptionInterpretor;

public class Client {

	public static void main(String[] args) throws ParseException, IOException, CommandInvalidException {
		OptionInterpretor interpretor;

			interpretor = new ClientOptionInterpretor();
			ClientCmds cmds = (ClientCmds) interpretor.interpret(args);
			ClientClass client = new ClientClass(cmds);
			//client.run();
			try {
				client.connect();
			} catch (UnrecoverableKeyException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException
					| CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}

}
