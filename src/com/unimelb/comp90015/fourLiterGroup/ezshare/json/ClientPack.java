package com.unimelb.comp90015.fourLiterGroup.ezshare.json;

import org.json.simple.JSONObject;

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.Cmds;

import java.util.HashMap;
import java.util.Map;

public class ClientPack implements JSONPack {

	@Override
	public JSONObject Pack(Cmds cmds) {
		ClientCmds clientcmds = (ClientCmds) cmds;
		JSONObject jsonObject = new JSONObject();

		if (clientcmds.publish) {// pack publish command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			jsonObject1.put("tags", clientcmds.tags);
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.host);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("command", "PUBLISH");
		} else if (clientcmds.query) {// pack query command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			jsonObject1.put("tags", clientcmds.tags);
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.host);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("relay", "true");
			jsonObject.put("command", "QUERY");
		} else if (clientcmds.remove) {// pack remove command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			jsonObject1.put("tags", clientcmds.tags);
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.host);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("command", "REMOVE");
		} else if (clientcmds.share) {// pack share command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			jsonObject1.put("tags", clientcmds.tags);
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.host);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("secret", clientcmds.secret);
			jsonObject.put("command", "SHARE");
		} else if (clientcmds.fetch) {// pack fetch command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			jsonObject1.put("tags", clientcmds.tags);
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.host);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("command", "FETCH");
		} else if (clientcmds.exchange) {// pack exchange command in json
			Map map = new HashMap();
			
			
			jsonObject.put("command", "Exchange");
		}
		return jsonObject;
	}

}
