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

	public static String MISSING_OR_INVALID_SERVER_LIST = "missing or invalid server list";

	@Override
	public JSONObject Pack(Cmds cmds) throws CommandInvalidException {
		ClientCmds clientcmds = (ClientCmds) cmds;
		JSONObject jsonObject = new JSONObject();

		// TODO: check each essential element
		// validation with throwable exception!
		if (clientcmds.publish) {// pack publish command in json
			JSONObject publishJsonObjectChild = new JSONObject();

			putNameInJSONObj(publishJsonObjectChild, clientcmds.name);
			putTagsInJSONObj(publishJsonObjectChild, clientcmds.tags);
			putDescriptionInJSONObj(publishJsonObjectChild, clientcmds.description);
			putUriInJSONObj(publishJsonObjectChild, clientcmds.uri);
			putChannelInJSONObj(publishJsonObjectChild, clientcmds.channel);
			putOwnerInJSONObj(publishJsonObjectChild, clientcmds.owner);
			putEzserverInJSONObj(publishJsonObjectChild, null);

			jsonObject.put("resource", publishJsonObjectChild);
			jsonObject.put("command", "PUBLISH");

		} else if (clientcmds.query) {// pack query command in json
			JSONObject queryJsonObjectChild = new JSONObject();

			putNameInJSONObj(queryJsonObjectChild, clientcmds.name);
			putTagsInJSONObj(queryJsonObjectChild, clientcmds.tags);
			putDescriptionInJSONObj(queryJsonObjectChild, clientcmds.description);
			putUriInJSONObj(queryJsonObjectChild, clientcmds.uri);
			putChannelInJSONObj(queryJsonObjectChild, clientcmds.channel);
			putOwnerInJSONObj(queryJsonObjectChild, clientcmds.owner);
			putEzserverInJSONObj(queryJsonObjectChild, clientcmds.servers);

			jsonObject.put("resource", queryJsonObjectChild);
			jsonObject.put("relay", "true");
			jsonObject.put("command", "QUERY");

		} else if (clientcmds.remove) {// pack remove command in json
			JSONObject removeJsonObjectChild = new JSONObject();

			putNameInJSONObj(removeJsonObjectChild, clientcmds.name);
			putTagsInJSONObj(removeJsonObjectChild, clientcmds.tags);
			putDescriptionInJSONObj(removeJsonObjectChild, clientcmds.description);
			putUriInJSONObj(removeJsonObjectChild, clientcmds.uri);
			putChannelInJSONObj(removeJsonObjectChild, clientcmds.channel);
			putOwnerInJSONObj(removeJsonObjectChild, clientcmds.owner);
			putEzserverInJSONObj(removeJsonObjectChild, clientcmds.servers);

			jsonObject.put("resource", removeJsonObjectChild);
			jsonObject.put("command", "REMOVE");

		} else if (clientcmds.share) {// pack share command in json
			JSONObject shareJsonObjectChild = new JSONObject();

			putNameInJSONObj(shareJsonObjectChild, clientcmds.name);
			putTagsInJSONObj(shareJsonObjectChild, clientcmds.tags);
			putDescriptionInJSONObj(shareJsonObjectChild, clientcmds.description);
			putUriInJSONObj(shareJsonObjectChild, clientcmds.uri);
			putChannelInJSONObj(shareJsonObjectChild, clientcmds.channel);
			putOwnerInJSONObj(shareJsonObjectChild, clientcmds.owner);
			putEzserverInJSONObj(shareJsonObjectChild, clientcmds.servers);

			jsonObject.put("resource", shareJsonObjectChild);
			jsonObject.put("secret", clientcmds.secret);// what if null?
			jsonObject.put("command", "SHARE");

		} else if (clientcmds.fetch) {// pack fetch command in json
			JSONObject fetchJsonObjectChild = new JSONObject();

			putNameInJSONObj(fetchJsonObjectChild, clientcmds.name);
			putTagsInJSONObj(fetchJsonObjectChild, clientcmds.tags);
			putDescriptionInJSONObj(fetchJsonObjectChild, clientcmds.description);
			putUriInJSONObj(fetchJsonObjectChild, clientcmds.uri);
			putChannelInJSONObj(fetchJsonObjectChild, clientcmds.channel);
			putOwnerInJSONObj(fetchJsonObjectChild, clientcmds.owner);
			putEzserverInJSONObj(fetchJsonObjectChild, clientcmds.servers);

			jsonObject.put("resourceTemplate", fetchJsonObjectChild);
			jsonObject.put("command", "FETCH");

		} else if (clientcmds.exchange) {// pack exchange command in json
			// List<JSONObject> jsonobjectList = new ArrayList<JSONObject>();
			JSONArray jsonMap = new JSONArray();
			if (clientcmds.servers.equals(null)) {
				throw new CommandInvalidException(MISSING_OR_INVALID_SERVER_LIST);
			}

			for (String string : clientcmds.servers) {
				String[] DomainAndPort = string.split(":");
				JSONObject jsonObject2 = new JSONObject();
				jsonObject2.put("hostname", DomainAndPort[0]);
				jsonObject2.put("port", DomainAndPort[1]);
				jsonMap.add(jsonObject2);
				// jsonobjectList.add(jsonObject2);
				// jsonObject1.put("serverList", jsonObject2);
			}
			// jsonMap.add(jsonobjectList);
			jsonObject.put("serverList", jsonMap);
			jsonObject.put("command", "EXCHANGE");
		}
		return jsonObject;
	}

	private void putNameInJSONObj(JSONObject object, String name) {
		object.put("name", name);
	}

	private void putTagsInJSONObj(JSONObject object, String[] tags) {
		if (!tags.equals(null)) {
			List<String> tagList = new ArrayList<String>();
			for (String string : tags) {
				tagList.add(string);
			}
			object.put("tags", tagList);
		} else {
			object.put("tags", null);
		}
	}

	private void putDescriptionInJSONObj(JSONObject object, String description) {

		if (!description.equals(null)) {
			object.put("description", description);
		}
	}

	private void putUriInJSONObj(JSONObject object, String uri) {

		object.put("uri", uri);
	}

	private void putChannelInJSONObj(JSONObject object, String channel) {

		if (channel != null) {
			object.put("channel", channel);
		} else {
			object.put("channel", "");
		}
	}

	private void putOwnerInJSONObj(JSONObject object, String owner) {

		if (owner != null) {
			object.put("owner", owner);
		} else {
			object.put("owner", "");
		}
	}

	private void putEzserverInJSONObj(JSONObject object, String[] ezserver) {
		object.put("ezserver", ezserver);
	}

}
