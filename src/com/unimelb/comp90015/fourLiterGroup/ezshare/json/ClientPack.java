package com.unimelb.comp90015.fourLiterGroup.ezshare.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.ClientCmds;
import com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret.Cmds;

import java.util.ArrayList;
import java.util.List;

public class ClientPack implements JSONPack {
	@Override
	public JSONObject Pack(Cmds cmds) throws CommandInvalidException {
		ClientCmds clientcmds = (ClientCmds) cmds;
		JSONObject jsonObject = new JSONObject();

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

			jsonObject.put("resourceTemplate", queryJsonObjectChild);
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
			//TODO: add secure
			JSONArray jsonMap = new JSONArray();
			jsonObject.put("command", "EXCHANGE");
			if (clientcmds.servers != null) {
				for (String string : clientcmds.servers) {
					String[] DomainAndPort = string.split(":");
					JSONObject jsonObject2 = new JSONObject();
					jsonObject2.put("hostname", DomainAndPort[0]);
					int port = Integer.parseInt(DomainAndPort[1]);
					jsonObject2.put("port", port);
					jsonMap.add(jsonObject2);
					// jsonobjectList.add(jsonObject2);
					// jsonObject1.put("serverList", jsonObject2);
				}
				// jsonMap.add(jsonobjectList);
				jsonObject.put("serverList", jsonMap);
			} else {
				jsonObject.put("serverList", null);
			}
		} else if (clientcmds.subscribe) {// pack query command in json
			JSONObject queryJsonObjectChild = new JSONObject();

			putNameInJSONObj(queryJsonObjectChild, clientcmds.name);
			putTagsInJSONObj(queryJsonObjectChild, clientcmds.tags);
			putDescriptionInJSONObj(queryJsonObjectChild, clientcmds.description);
			putUriInJSONObj(queryJsonObjectChild, clientcmds.uri);
			putChannelInJSONObj(queryJsonObjectChild, clientcmds.channel);
			putOwnerInJSONObj(queryJsonObjectChild, clientcmds.owner);
			putEzserverInJSONObj(queryJsonObjectChild, clientcmds.servers);

			jsonObject.put("resourceTemplate", queryJsonObjectChild);
			jsonObject.put("id", clientcmds.id);
			jsonObject.put("relay", "true");
			jsonObject.put("command", "SUBSCRIBE");
		} else if (clientcmds.unsubscribe){
			jsonObject.put("id", clientcmds.id);
			jsonObject.put("command", "UNSUBSCRIBE");
		}
		
		return jsonObject;
	}

	private void putNameInJSONObj(JSONObject object, String name) {
		if(object != null){
			object.put("name", name);
		}else{
			object.put("name", "");
		}
	}

	private void putTagsInJSONObj(JSONObject object, String[] tags) {
		if (null != tags) {
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

		if (null != description) {
			object.put("description", description);
		} else {
			object.put("description", "");
		}
	}

	private void putUriInJSONObj(JSONObject object, String uri) {

		if (uri != null) {
			object.put("uri", uri);
		} else {
			object.put("uri", "");
		}

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
