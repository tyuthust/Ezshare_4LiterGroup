package com.unimelb.comp90015.fourLiterGroup.ezshare.serverOps;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.unimelb.comp90015.fourLiterGroup.ezshare.utils.utils;

public class ServerOperationHandler {

	public static Resource publish(JSONObject jsonObject) throws OperationRunningException {
		//TODO: add to logger
		System.out.println("Publish function");

		// create a json object to save the map in resource
		JSONObject shareResourceJsonObj = new JSONObject();
		shareResourceJsonObj.putAll((Map) jsonObject.get("resource"));

		// If the resource field was not given or not of the correct type
		if (shareResourceJsonObj.isEmpty()) {
			throw new OperationRunningException("missing resource");
		}

		Map shareResourceMap = new HashMap();
		shareResourceMap = (Map) shareResourceJsonObj.clone();
		// TODO: add to logger
		System.out.println(shareResourceMap);

		// check if the the json data break the rule
		// The URI must be present, must be absolute and cannot be a file
		// scheme.

		// URI The URI must be present
		if (shareResourceJsonObj.get("uri") != null) {
			String uriString = shareResourceJsonObj.get("uri").toString();

			if (uriString != "") {
				URI resourceUri = URI.create(uriString);
				// cannot be a file scheme and must be an absolute path
				if (resourceUri.isAbsolute()) {
					if (resourceUri.getScheme().contains("file")) {
						throw new OperationRunningException("cannot publish resource");
					}
				} else {
					throw new OperationRunningException("cannot publish resource");
				}

				// The Owner field must not be the single character "*".
				if (shareResourceJsonObj.get("owner") == null) {
					shareResourceJsonObj.replace("onwer", "");
				} else if (shareResourceJsonObj.get("owner") == ("*")) {
					throw new OperationRunningException("cannot publish resource");
				}

				// The owner field
				if (shareResourceJsonObj.get("channel") == null) {
					shareResourceJsonObj.replace("channel", "");
				}
			} else {
				throw new OperationRunningException("cannot publish resource");
			}
		} else {
			throw new OperationRunningException("cannot publish resource");
		}

		return generatingResourceHandler(shareResourceJsonObj);
	}

	public static String[] exchange(JSONObject jsonObject) throws OperationRunningException {
		//TODO: add to logger
		System.out.println("Exchange function");
		// create a json object to save the map in resource
		JSONArray jsonArray = new JSONArray();
		jsonArray = (JSONArray) jsonObject.get("serverList");

		String[] ezservers = null;

		// a pointer which is used to assign
		int counter = 0;

		if (jsonArray != null) {
			ezservers = new String[jsonArray.size()];
			// do exchange function
			List<JSONObject> jsonobjectList = new ArrayList<JSONObject>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonobjectList.add((JSONObject) jsonArray.get(i));
			}
			// Looking at each element in the jsonobjectList
			for (JSONObject jsonOb : jsonobjectList) {
				ezservers[counter] = jsonOb.get("hostname").toString() + ":" + jsonOb.get("port").toString();
				counter++;
			}
			// invalid server list
			for (String string : ezservers) {
				if (!utils.isIPandPort(string)) {
					throw new OperationRunningException("missing or invalide server List");
				}
			}
		} else {
			// server list is missing
			throw new OperationRunningException("missing or invalide server List");
		}
		return ezservers;
	}

	public static Resource remove(JSONObject jsonObject) throws OperationRunningException {
		//TODO: add to logger
		System.out.println("Remove function");
		JSONObject removeResourceJsonObj = new JSONObject();
		removeResourceJsonObj.putAll((Map) jsonObject.get("resource"));

		// If the resource field was not given or not of the correct type
		System.out.println(removeResourceJsonObj);// TODO: add logger

		if (!removeResourceJsonObj.isEmpty()) {
			if (removeResourceJsonObj.get("uri") == null || removeResourceJsonObj.get("uri").equals("")) {
				throw new OperationRunningException("invalide resource1");
			}
			if (removeResourceJsonObj.get("channel") == null || removeResourceJsonObj.get("owner") == null) {
				throw new OperationRunningException("invalide resource2");
			}
			String uriString = removeResourceJsonObj.get("uri").toString();
			URI resourceUri = URI.create(uriString);
			// cannot be a file scheme and must be an absolute path
			if (!resourceUri.isAbsolute()) {
				throw new OperationRunningException("cannot publish resource");
			}
		} else {
			throw new OperationRunningException("missing resource");
		}

		return generatingResourceHandler(removeResourceJsonObj);
	}

	public static Resource share(JSONObject jsonObject) throws OperationRunningException {
		//TODO: add to logger
		System.out.println("Share function");

		JSONObject shareResourceJsonObj = new JSONObject();
		// If the resource field was not given or not of the correct type
		if (null == jsonObject.get("resource") || null == jsonObject.get("secret")) {
			throw new OperationRunningException("missing resource and/or secret");
		}
		shareResourceJsonObj.putAll((Map) jsonObject.get("resource"));
		System.out.println(shareResourceJsonObj);

		// check if the the json data break the rule

		// The URI must be present, must be absolute and must be a file scheme.
		String uriString = shareResourceJsonObj.get("uri").toString();

		// URI The URI must be present
		if (null == uriString || uriString.equals("")) {
			throw new OperationRunningException("cannot share resource");
		}
		URI resourceUri = URI.create(uriString);

		// must be absolute and must be a file scheme
		if (resourceUri.isAbsolute()) {
			if (!resourceUri.getScheme().contains("file")) {
				throw new OperationRunningException("cannot share resource");
			}
		} else {
			throw new OperationRunningException("cannot share resource");
		}

		// The Owner field must not be the single character "*".
		if (shareResourceJsonObj.get("owner").toString().equals("*")) {
			throw new OperationRunningException("cannot share resource");
		}

		return generatingResourceHandler(shareResourceJsonObj);
	}

	public static Resource query(JSONObject jsonObject) throws OperationRunningException {
		//TODO: add to logger
		System.out.println("query function");

		JSONObject queryResourceJsonObj = new JSONObject();

		queryResourceJsonObj.putAll((Map) jsonObject.get("resourceTemplate"));

		if (queryResourceJsonObj.isEmpty()) {
			throw new OperationRunningException("missing resourceTemplate");
		}

		// there are other stuff for checking
		// The URI must be present, must be absolute and must be a file scheme.
		String uriString = queryResourceJsonObj.get("uri").toString();
		String chanString = queryResourceJsonObj.get("channel").toString();

		// URI The URI must be present
		if (null == uriString) {
			throw new OperationRunningException("missing resourceTemplate");
		}
		if (null == chanString) {
			throw new OperationRunningException("missing resourceTemplate");
		}
		return generatingResourceHandler(queryResourceJsonObj);
	}

	public static Resource fetch(JSONObject jsonObject) throws OperationRunningException {
		//TODO: add to logger
		System.out.println("fetch function");

		JSONObject fetchResourceJsonObj = new JSONObject();
		fetchResourceJsonObj.putAll((Map) jsonObject.get("resourceTemplate"));

		if (fetchResourceJsonObj.isEmpty()) {
			throw new OperationRunningException("missing resourceTemplate");
		}
		// The URI must be present, must be absolute and must be a file scheme.
		String uriString = fetchResourceJsonObj.get("uri").toString();
		String chanString = fetchResourceJsonObj.get("channel").toString();

		// URI The URI must be present
		if (null == uriString || uriString.equals("")) {
			throw new OperationRunningException("missing resourceTemplate");
		}
		if (null == chanString || chanString.equals("")) {
			throw new OperationRunningException("missing resourceTemplate");
		}
		return generatingResourceHandler(fetchResourceJsonObj);

	}

	private static Resource generatingResourceHandler(JSONObject ResourceJsonObj) throws OperationRunningException {

		// create a new resource and set its value
		Resource resource = new Resource();

		if(null == ResourceJsonObj){
			throw new OperationRunningException("missing resource");
		}
		// set name
		if (null != ResourceJsonObj.get("name")) {

			if (!ResourceJsonObj.get("name").equals("")) {
				resource.setName(ResourceJsonObj.get("name").toString());
			}else{
				resource.setName("");
			}
			//TODO: add to logger
			System.out.println("The resource name:" + resource.getName());
		}else{
			throw new OperationRunningException("missing resource name");
		}
		
		// set channel
		if (null != ResourceJsonObj.get("channel")) {
			String chanString = ResourceJsonObj.get("channel").toString();
			if (!chanString.equals("")) {
				resource.setChannel(chanString);
			}else{
				resource.setChannel("");
			}
			// TODO: add to logger
			System.out.println("The resource channel:" + resource.getChannel());
		}else{
			throw new OperationRunningException("missing resource channel");
		}
		//TODO: add to logger
													
		// set description
		if (null != ResourceJsonObj.get("description")) {
			String desString = ResourceJsonObj.get("description").toString();
			if (!desString.equals("")) {
				resource.setDescription(desString);
			}else{
				resource.setDescription("");
			}
			//TODO: add to logger
			System.out.println("The resource description:" + resource.getDescription());
		} else{
			throw new OperationRunningException("missing resource description");
		}

		// set owner
		if (null != ResourceJsonObj.get("owner")) {
			String ownerString = ResourceJsonObj.get("owner").toString();
			if (!ownerString.equals("")) {
				resource.setOwner(ownerString);
			}else{
				resource.setOwner("");
			}
			//TODO: add to logger
			System.out.println("The resource owner:" + resource.getOwner());
		}else{
			throw new OperationRunningException("missing resource owner");
		}
		
		// set uri
		if (null == ResourceJsonObj.get("uri")) {
			throw new OperationRunningException("missing resource uri");
		}
		String uriString = ResourceJsonObj.get("uri").toString();
		if (!uriString.equals("")) {
			resource.setURI(uriString);
		}else{
			throw new OperationRunningException("missing resource uri");
		}
		// TODO: add to logger
		System.out.println("The resource uri:" + resource.getURI());

		// ezserver will not be transported when using publish command
		
		// TODO: add to logger
		System.out.println("The resource ezserver: null");

		// set tags
		if (null != ResourceJsonObj.get("tags")) {
			JSONArray jsonArray = new JSONArray();
			jsonArray = (JSONArray) ResourceJsonObj.get("tags");
			String[] tags = new String[jsonArray.size()];
			for (int i = 0; i < jsonArray.size(); i++) {
				String r = jsonArray.get(i).toString();
				tags[i] = r;
			}
			resource.setTags(tags);

			List<String> tagList = new ArrayList<String>();
			for (String string : resource.getTags()) {
				tagList.add(string);
			}

			// TODO: add to logger
			System.out.println("The resource tags:" + tagList.toString());
		}else{
			// TODO: add to logger
			System.out.println("The resource tags:" + "null");
		}
		return resource;
	}
}
