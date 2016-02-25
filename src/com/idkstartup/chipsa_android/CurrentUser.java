package com.idkstartup.chipsa_android;

import org.json.JSONObject;

/*
 * if user is logged in then "_id" is set and all the JSONObject
 * 
 * data in user:
 * {
    _id,fbid,firstName:,lastName,fbToken,
    fbFriends[_id],blockedFriends:[_id]
    loc:{state,city,lastUpdatedTime,coordinates:[]},
	}
 */

public class CurrentUser {
	public JSONObject user = new JSONObject();
	
	private static CurrentUser instance = null;
    protected CurrentUser() {
        
     }
     public static CurrentUser getInstance() {
        if(instance == null) {
           instance = new CurrentUser();
        }
        return instance;
     }

}
