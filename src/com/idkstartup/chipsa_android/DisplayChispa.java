package com.idkstartup.chipsa_android;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayChispa extends AppCompatActivity {
    private Chispas chispas;
    private JSONObject chispa;
    private JSONObject owner;
    private CurrentUser currentUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chispa);
        ////////////////////////////////////////////////////////
        Button viewInvitedbtn = (Button)findViewById(R.id.viewInvitedbtn);
        Button viewJoinedbtn = (Button)findViewById(R.id.viewJoinedbtn);
        TextView ownerTextView = (TextView)findViewById(R.id.ownerNametxt);
        ImageView ownerProfileimg = (ImageView)findViewById(R.id.ownerProfileimg);
        Button deletebtn = (Button)findViewById(R.id.deletebtn);
        ////////////////////////////////////////////////////////
        chispas = new Chispas().getInstance();
        currentUser = new CurrentUser().getInstance();
        
        Bundle extras = getIntent().getExtras(); 
        String _id = (String) extras.get("_id");
        
        try {
            chispa = chispas.chispas.get(_id);
            int numberInvited = ((JSONArray) chispa.get("usersInvited")).length(); 
            viewInvitedbtn.setText(viewInvitedbtn.getText()+""+numberInvited);
            
            owner = (JSONObject) chispa.get("owner");
            
            //TODO:when I switch the url to get chispas fix this:
//            ownerTextView.setText((String)owner.getString("firstName")+(String)owner.getString("lastName"));
            ownerTextView.setText((String)owner.getString("name"));
            
            
            //TODO: when I switch the url to get chispas fix this:
//            String fbid = (String) owner.get("fbid");
//            String url="http://graph.facebook.com/"+fbid+"/picture?type=square";
            String url = (String) owner.get("imageurl");
            Picasso.with(getApplicationContext()).load(url).into(ownerProfileimg);
            
            deletebtn.setVisibility(View.GONE);
            if(currentUser.user.get("_id").equals(owner.get("_id"))){//if currentUser is the owner, show the delete btn
            	deletebtn.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        OnClickListener btnsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Button currentBtn = (Button)v;
                Bundle dataBundle = new Bundle();
                try {
                  dataBundle.putString("_id",chispa.getString("_id"));
                  dataBundle.putString("mode", currentBtn.getId()==R.id.viewInvitedbtn ? "VIEW_USERS_INVITED" : "VIEW_USERS_JOINED");
                  Intent intent = new Intent(getApplicationContext(),ViewUsers.class);
                  intent.putExtras(dataBundle);
                  startActivity(intent);

              } catch (JSONException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }
            }
          };
          
          viewInvitedbtn.setOnClickListener(btnsOnClickListener);
          viewJoinedbtn.setOnClickListener(btnsOnClickListener);
          
          
          deletebtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
		        AsyncHttpClient client = new AsyncHttpClient();    

		        JSONObject jsonParams = new JSONObject();
		        StringEntity entity = null;
		        try {
		            jsonParams.put("hittupuid", chispa.get("_id"));
		            jsonParams.put("owneruid", owner.get("_id"));
		          //TODO: when I switch the url to get chispas fix this:
//		            jsonParams.put("ownerName", (String)owner.get("firstName")+(String)owner.get("lastName"));
		            jsonParams.put("ownerName", owner.get("name"));
		            
		            entity = new StringEntity(jsonParams.toString());
		        } catch (JSONException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        } catch (UnsupportedEncodingException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();            
		        }
		        
		        client.post(null, "http://hittup.idkstartup.xyz/FriendHittups/RemoveHittup", entity,"application/json", new JsonHttpResponseHandler() {
		            @Override
		            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		            	//TODO add error handling here
		                try {
							Toast.makeText(getApplicationContext(), "{\"success\":"+response.get("success")+"}", Toast.LENGTH_LONG).show();
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
				
			}
		});
    }
    
    public void fetchAndShowOwnerPic(){
    	
    	    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_chispa, menu);
        return true;
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
