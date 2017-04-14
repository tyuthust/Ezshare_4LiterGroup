package com.unimelb.comp90015.fourLiterGroup.ezshare.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.Cmds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ClientPack implements JSONPack {

	@Override
	public JSONObject Pack(Cmds cmds) {
		ClientCmds clientcmds = (ClientCmds) cmds;
		JSONObject jsonObject = new JSONObject();

		if (clientcmds.publish) {// pack publish command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			List<String> tagList = new ArrayList<String>();
			for(String string: clientcmds.tags){
				tagList.add(string);
			}
			jsonObject1.put("tags",tagList);
			
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.servers);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("command", "PUBLISH");
		} else if (clientcmds.query) {// pack query command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			List<String> tagList = new ArrayList<String>();
			for(String string: clientcmds.tags){
				tagList.add(string);
			}
			jsonObject1.put("tags",tagList);
			
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.servers);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("relay", "true");
			jsonObject.put("command", "QUERY");
		} else if (clientcmds.remove) {// pack remove command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			List<String> tagList = new ArrayList<String>();
			for(String string: clientcmds.tags){
				tagList.add(string);
			}
			jsonObject1.put("tags",tagList);
			
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.servers);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("command", "REMOVE");
		} else if (clientcmds.share) {// pack share command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			List<String> tagList = new ArrayList<String>();
			for(String string: clientcmds.tags){
				tagList.add(string);
			}
			jsonObject1.put("tags",tagList);
			
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.servers);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("secret", clientcmds.secret);
			jsonObject.put("command", "SHARE");
		} else if (clientcmds.fetch) {// pack fetch command in json
			JSONObject jsonObject1 = new JSONObject();

			jsonObject1.put("name", clientcmds.name);
			List<String> tagList = new ArrayList<String>();
			for(String string: clientcmds.tags){
				tagList.add(string);
			}
			jsonObject1.put("tags",tagList);
			
			jsonObject1.put("description", clientcmds.description);
			jsonObject1.put("uri", clientcmds.uri);
			jsonObject1.put("channel", clientcmds.channel);
			jsonObject1.put("owner", clientcmds.owner);
			jsonObject1.put("ezserver", clientcmds.servers);

			jsonObject.put("resource", jsonObject1);
			jsonObject.put("command", "FETCH");
		} else if (clientcmds.exchange) {// pack exchange command in json
			List<JSONObject> jsonobjectList = new ArrayList<JSONObject>();
			for (String string : clientcmds.servers) {
				String[] DomainAndPort = string.split(":");
				JSONObject jsonObject2 = new JSONObject();
				jsonObject2.put("hostname", DomainAndPort[0]);
				jsonObject2.put("port", DomainAndPort[1]);
				jsonobjectList.add(jsonObject2);
				//jsonObject1.put("serverList", jsonObject2);
			}
			JSONArray jsonMap = new JSONArray();
			jsonMap.add(jsonobjectList);
			jsonObject.put("serverList",jsonMap);
			jsonObject.put("command", "EXCHANGE");
		}
		return jsonObject;
	}

}
