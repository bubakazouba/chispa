package com.idkstartup.chipsa_android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Chispa {
	public JSONObject chispa;
	
	public Chispa(JSONObject chispa){
		this.chispa = chispa;
	}
	
	public JSONArray getUsersJoined(){
		try {
			return chispa.getJSONArray("usersJoined");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/*
	 * type is "usersJoiend" or "usersInvited" or "usersCheckedIn"
	 */
	private ArrayList<String> getUsersuids(String type) {
		ArrayList<String> usersJoineduids = new ArrayList<String>();
		try {
			JSONArray usersJoined = chispa.getJSONArray(type);
			int i;
			for(i=0;i<usersJoined.length();i++){
        		JSONObject thisUser = (JSONObject) usersJoined.get(i);
        		thisUser=(JSONObject) thisUser.get("user");
        		usersJoineduids.add((String) thisUser.get("_id"));
        	}
			return usersJoineduids;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String> getUsersJoineduids(){
		return getUsersuids("usersJoined");
	}
	
	public ArrayList<String> getUsersCheckedInuids(){
		return getUsersuids("usersCheckedIn");
	}

	public ArrayList<String> getUsersInviteduids(){
		return getUsersuids("usersInvited");
	}

	
	
}
