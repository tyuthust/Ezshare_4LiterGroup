package com.unimelb.comp90015.fourLiterGroup.ezshare.optionsInterpret;

import org.json.simple.JSONObject;

public class ClientPack implements JSONPack {

	@Override
	public JSONObject Pack(Cmds cmds) {
		ClientCmds clientcmds = (ClientCmds) cmds;
		JSONObject jsonObject = new JSONObject();
		if(clientcmds.publish){
			jsonObject.put("command", "PUBLISH");
			System.out.println(jsonObject.toJSONString());
		}else {
			jsonObject.put("command", "PUBLISH");
			System.out.println(jsonObject.toJSONString());
		}
		return jsonObject;
	}

}
