package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ServerOperationHandler {

	public static JSONObject publish(JSONObject jsonObject) {

		System.out.println("Publish function");

		// create a jsonobject to save the map in resource
		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.putAll((Map) jsonObject.get("resource"));
		System.out.println(jsonObject1);

		// create a new resource and set its value
		Resource resource = new Resource();
		resource.setName(jsonObject1.get("name").toString());
		System.out.println("The resource name:" + resource.getName());

		// clone the jsonobject to a hashmap
		Map map = new HashMap();
		map = (Map) jsonObject1.clone();

		if (map.get("channel") != null) {// otherwise, there is an exception
			// when channel is null
			resource.setChannel(jsonObject1.get("channel").toString());
		}
		System.out.println("The resource channel:" + resource.getChannel());

		resource.setDescription(jsonObject1.get("description").toString());
		System.out.println("The resource description:" + resource.getDescription());

		if (map.get("owner") != null) {
			resource.setOwner(jsonObject1.get("owner").toString());
		}
		System.out.println("The resource owner:" + resource.getOwner());

		resource.setURI(jsonObject1.get("uri").toString());
		System.out.println("The resource uri:" + resource.getURI());

		// ezserver will not be transported when using publish command
		System.out.println("The resource ezserver:" + "null");

		if (map.get("tags") != null) {
			JSONArray jsonArray = new JSONArray();
			jsonArray = (JSONArray) jsonObject1.get("tags");
			String[] tags = new String[jsonArray.size()];
			for (int i = 0; i < jsonArray.size(); i++) {
				String r = jsonArray.get(i).toString();
				tags[i] = r;
			}
			resource.setTags(tags);
		}

		List<String> tagList = new ArrayList<String>();
		for (String string : resource.getTags()) {
			tagList.add(string);
		}
		System.out.println("The resource:" + tagList.toString());

		JSONObject result = new JSONObject();
		if (true) {
			result.put("response", "successful");
		}

		return result;
	}

	public static JSONObject share(JSONObject jsonObject) {
		JSONObject result = new JSONObject();
		System.out.println("Share function");
		// TODO: Check rules breaker
		if (!jsonObject.containsKey("command")) {
			result.put("response", "error");
			//result.put("errorMessage", "adva");
		}

		// Do share
		try {
			shareHandler(jsonObject);
			result.put("response", "successful");
		} catch (Exception e) {
			// TODO: handle exception
			result.put("response", "error");
			result.put("errorMessage", e.toString());
		}
		return result;
	}
	
	private static JSONObject publishHander(JSONObject jsonObject) throws OperationRunningException {
		// TODO: actual share function
		return null;
	}

	private static JSONObject shareHandler(JSONObject jsonObject) throws OperationRunningException {
		// TODO: actual share function
		return null;
	}
	


}
