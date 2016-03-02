package com.idkstartup.chipsa_android;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateChispaActivity extends AppCompatActivity {
	final private String SERVER_URL="http://chispa.idkstartup.xyz/";//TODO have this
	
	final private int SELECT_USERS_INVITED = 1, SELECT_USERS_THERE = 2;
	
	private CurrentUser currentUser;
	
	
    Button cancelbtn,submitbtn,invitebtn,checkedInbtn;
    EditText titletxt;
    
    
    ///////////////////////////
    //this values are accessed from the submit button to put it in the post request, they are modified from the buttons that access them
    ArrayList<String> usersInviteduids = new ArrayList<String>();
    ArrayList<String> usersCheckedInuids = new ArrayList<String>();
    boolean isOnlyfbFriends = false;//publicbtn and specificbtn make it false, fbFriends makes it true
    boolean isPrivate = false;//publicbtn and fbFriendsbtn make it false, specific makes it true
    ////////////////////////////
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chispa);
        currentUser = new CurrentUser().getInstance();
        
        //////////////////////////////////////////////////
        titletxt = (EditText)findViewById(R.id.titletxt);
        cancelbtn = (Button)findViewById(R.id.cancelbtn);
        submitbtn = (Button)findViewById(R.id.submitbtn);
        invitebtn = (Button)findViewById(R.id.invitebtn);
        checkedInbtn = (Button)findViewById(R.id.checkedInbtn);
        //////////////////////////////////////////////////
        
        setbtnsListeners();
    }

    
    public void setbtnsListeners() {
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        submitbtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                JSONObject jsonParams = new JSONObject();
                StringEntity entity = null;
                try {
                	//convert the ListArrays to JSONArray
                	JSONArray usersCheckedInuidsJSONArray = new JSONArray();
                	JSONArray usersInviteduidsJSONArray = new JSONArray();
                	int i;
                	for(i=0;i<usersInviteduids.size();i++) usersInviteduidsJSONArray.put(usersInviteduids.get(i));
                	for(i=0;i<usersCheckedInuids.size();i++) usersCheckedInuidsJSONArray.put(usersCheckedInuids.get(i));
                	////////////
                	
                    jsonParams.put("coordinates", currentUser.getCoordinates());
                    jsonParams.put("duration", 60*60);
                    jsonParams.put("uid", currentUser.user.get("_id"));
                    jsonParams.put("usersInviteduids", usersInviteduidsJSONArray);
                    jsonParams.put("usersCheckedInuids", usersCheckedInuidsJSONArray);
                    jsonParams.put("ownerName", currentUser.user.get("firstName"));
                    // jsonParams.put("image", image);

                    if(isPrivate) {
                        jsonParams.put("isPrivate", true);
                    }
                    else if(isOnlyfbFriends) {
                        jsonParams.put("isOnlyfbFriends", true);
                    }
                    else {
                        jsonParams.put("isPrivate",false);
                        jsonParams.put("isOnlyfbFriends",false);
                    }
                    
                     jsonParams.put("title", titletxt.getText());
                    
                    
                    entity = new StringEntity(jsonParams.toString());
                    client.post(null, SERVER_URL+"FriendHittups/PostHittup", entity,"application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            //TODO add error handling here
                            try {                               
                                Toast.makeText(getApplicationContext(), "{\"success\":"+response.get("success")+"}", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            Log.d("HTTP","success: "+response.toString());
                            Toast.makeText(getApplicationContext(), "upload, error", Toast.LENGTH_LONG).show();
                        }

                    });
                    
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();            
                }
            }
        });
        
        
        OnClickListener selectusersClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String title;
				ArrayList<String> usersSelecteduids=new ArrayList<String>();
				int i;
				int requestCode;
				if(v.getId()==R.id.invitebtn){
					requestCode=SELECT_USERS_INVITED;
					title="invite your friends";
		            usersSelecteduids=usersInviteduids;
				}
				else {
					requestCode=SELECT_USERS_THERE;
					title="who's there";
					usersSelecteduids=usersCheckedInuids;
				}
				
				Bundle dataBundle = new Bundle();
	            dataBundle.putString("title", title);
	            dataBundle.putString("mode", "SELECT");
	            
	            dataBundle.putStringArrayList("selecteduids", usersSelecteduids);
	            Intent intent = new Intent(getApplicationContext(),ViewUsers.class);
	            intent.putExtras(dataBundle);
	            startActivityForResult(intent, requestCode);	
				
			}
		};
        invitebtn.setOnClickListener(selectusersClickListener);
        checkedInbtn.setOnClickListener(selectusersClickListener);
    }//end setbtnsListeners
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_chispa, menu);
        return true;
    }

    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == Activity.RESULT_OK){
    	    if (requestCode == SELECT_USERS_INVITED) 
    	        usersInviteduids = data.getStringArrayListExtra("uids");
    	    else 
    	        usersCheckedInuids = data.getStringArrayListExtra("uids");   
    	    
    	}//end if RESULT_OK
    	if (resultCode == Activity.RESULT_CANCELED) {
    	    //Write your code if there's no result
    	}
    }//onActivityResult
    
    
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
