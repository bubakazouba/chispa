package com.idkstartup.chipsa_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chispa);
        ////////////////////////////////////////////////////////
        Button viewInvitedbtn = (Button)findViewById(R.id.viewInvitedbtn);
        Button viewJoinedbtn = (Button)findViewById(R.id.viewJoinedbtn);
        TextView ownerTextView = (TextView)findViewById(R.id.ownerNametxt);
        ImageView ownerProfileimg = (ImageView)findViewById(R.id.ownerProfileimg);
        ////////////////////////////////////////////////////////
        chispas = new Chispas().getInstance();

        Bundle extras = getIntent().getExtras(); 
        String _id = (String) extras.get("_id");
        
        try {
            chispa = chispas.chispas.get(_id);
            int numberInvited = ((JSONArray) chispa.get("usersInvited")).length(); 
            viewInvitedbtn.setText(viewInvitedbtn.getText()+""+numberInvited);
            
            JSONObject owner = (JSONObject) chispa.get("owner");
            
//            ownerTextView.setText((String)owner.getString("firstName")+(String)owner.getString("lastName"));
            ownerTextView.setText((String)owner.getString("name"));
            
            
//            String fbid = (String) owner.get("fbid");
//            String url="http://graph.facebook.com/"+fbid+"/picture?type=square";
            String url = (String) owner.get("imageurl");
            Picasso.with(getApplicationContext()).load(url).into(ownerProfileimg);

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
