package com.idkstartup.chipsa_android;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Chispas {
    public Map<String, JSONObject> chispas= new HashMap<String, JSONObject>();
    private static Chispas instance = null;
    protected Chispas() {
       // Exists only to defeat instantiation.
    }
    public static Chispas getInstance() {
       if(instance == null) {
          instance = new Chispas();
       }
       return instance;
    }
    public void addChispas(JSONArray tchispas) {
        int i;
        for(i=0;i<tchispas.length();i++){
            JSONObject cc;
            try {
                cc = (JSONObject) tchispas.get(i);
                this.chispas.put(cc.getString("_id"), cc);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
