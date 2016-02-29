package com.idkstartup.chipsa_android;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {
	private ListView chispasList;
	private Button createbtn;
	
	
	private ArrayAdapter arrayAdapter;
	private Users users;
	private Chispas chispas;
	private CurrentUser currentUser;
	
	//////////////////////////////////////
	final private String SERVER_URL="http://chispa.idkstartup.xyz/";
	//////////////////////////////////////
	//need it to keep track which row index corresponds to which _id in chispas
	ArrayList<String> rowIndexTo_idMap = new ArrayList<String>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        users = new Users().getInstance();
        chispas = new Chispas().getInstance();
        currentUser = new CurrentUser().getInstance();
        InitializeApp();
        
        getAllEventHittups();
        
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, new ArrayList<String>());

        //////////////////////////
        createbtn = (Button)findViewById(R.id.createbtn);
        chispasList = (ListView)findViewById(R.id.listView1);
        //////////////////////////
        
        
        chispasList.setAdapter(arrayAdapter);
        chispasList.setOnItemClickListener(new OnItemClickListener(){
          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) {
              Bundle dataBundle = new Bundle();
              dataBundle.putString("_id",rowIndexTo_idMap.get(position));
              Intent intent = new Intent(getApplicationContext(),DisplayChispaActivity.class);
              intent.putExtras(dataBundle);
              startActivity(intent);
          }
        });//end OnItemClickListener
        
        
        createbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), CreateChispaActivity.class);
				startActivity(intent);
			}
		});
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
		return false;
    }//end onCreateOptionsMenu
    
    /*
     * grabs user's data from server to update currentUser
     */
    public void InitializeApp() {
        //TODO: remove that
        try {
			currentUser.user.put("_id", "56d3c209c995598b7a4b0505");
			currentUser.user.put("firstName","Abdulrahman");
			currentUser.user.put("lastName","Sahmoud");
			currentUser.user.put("fbid","1987029048189811");
			currentUser.user.put("fbToken", "CAAWQkK154kUBAAUDmy103lrUzQt1TSZAjtXwzmzStvSRHcgLIDJbFa7yAlTLL9uBGp8twvGsmNBA7SeDZB5P7TonLxiJvsCLLiMFCZCEFdVF9eBYmMTxU1XjydTmAnhh04E2J1FQLHB3PKdZAa1QFsBW1gzkWy3nz5mfZC38mYjFaClZBsQwPbZCcJu0ZCtdTuZCqj5xZCqGcPVWvllC4GqITIvBUTxj5Pk7TU4feXoDfk8gZDZD");
			currentUser.user.put("coordinates", new JSONArray().put(-121.73).put(38.55));
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        AsyncHttpClient client = new AsyncHttpClient();    

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("fbid", currentUser.user.get("fbid"));
            jsonParams.put("fbToken", currentUser.user.get("fbToken"));
            entity = new StringEntity(jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();            
        }
        
        client.post(null, SERVER_URL + "Users/AddUser", entity,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            	//TODO add error handling here
            	try {
					currentUser.user=response.getJSONObject("user");
					Toast.makeText(getApplicationContext(), "Grabbed User's data="+currentUser.user.toString(), Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "didnt grab user's data error", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
            }
            //TODO add on failure
        });
    }
    
    public void getAllEventHittups() {
        AsyncHttpClient client = new AsyncHttpClient();    

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
        	JSONArray coordinates= new JSONArray();
        	coordinates.put(-121);//TODO unhardcode
        	coordinates.put(38.5);
            jsonParams.put("coordinates", coordinates);
            jsonParams.put("maxDistance", 200000);
            entity = new StringEntity(jsonParams.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();            
        }
        
        client.post(null, SERVER_URL+"friendhittups/getallhittups", entity,"application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            	//TODO add error handling here
                Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_LONG).show();
            }
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("HTTP","success: "+response.toString());
                int i;
                chispas.addChispas(response);
                arrayAdapter.clear();
                arrayAdapter.notifyDataSetChanged();//notify UI
                rowIndexTo_idMap.clear();
                
                for(i=0;i<response.length();i++){
                	JSONObject a;
					try {
						a = (JSONObject) response.get(i);
						rowIndexTo_idMap.add((String) a.get("_id"));
						arrayAdapter.add(a.get("title"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	
                }
                Toast.makeText(getApplicationContext(), "success download!", Toast.LENGTH_SHORT).show();
            }

        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
