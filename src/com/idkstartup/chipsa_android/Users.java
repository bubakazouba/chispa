package com.idkstartup.chipsa_android;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Users {
    public Map<String, JSONObject> users= new HashMap<String, JSONObject>();
    private static Users instance = null;
    protected Users() {
       // Exists only to defeat instantiation.
    }
    public static Users getInstance() {
       if(instance == null) {
          instance = new Users();
       }
       return instance;
    }
    public void addUsers(JSONArray tusers) {
        int i;
        for(i=0;i<tusers.length();i++){
            JSONObject cu;
            try {
                cu = (JSONObject) tusers.get(i);
                this.users.put(cu.getString("_id"), cu);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
