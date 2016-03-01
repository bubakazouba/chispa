package com.idkstartup.chipsa_android;

import java.text.ChoiceFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.AbsListView;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * extras:
 *  String title: title of view
 *  String mode: valid values: ["SELECT"] determines mode of view
 */
public class ViewUsers extends AppCompatActivity {
    private CurrentUser currentUser;
    
    private ListView usersList;
    Button submitbtn;
    
    private ArrayAdapter usersAdapter;
    private String mode;
    
    ArrayList<String> userNamesArrayList = new ArrayList<String>();
    ArrayList<Boolean> usersSelectedArrayList = new ArrayList<Boolean>();
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        currentUser = new CurrentUser().getInstance();
        
        
        ///////////////////////////////////////////////////
        usersList = (ListView)findViewById(R.id.listView1);
        submitbtn = (Button)findViewById(R.id.submitbtn);
        ///////////////////////////////////////////////////
        
        
        usersAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, userNamesArrayList);
        
        usersList.setAdapter(usersAdapter);
        usersList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				if(!usersSelectedArrayList.get(position)){
					view.setBackgroundColor(0xFF00FF00);
					usersSelectedArrayList.set(position, true);
				}
				else {
					view.setBackgroundColor(0x0000FF00);
					usersSelectedArrayList.set(position, false);
				}
			}
		});
        
        
        Bundle extras = getIntent().getExtras(); 
        mode = (String) extras.get("mode");
        setTitle((String) extras.get("title"));

        try {
            JSONArray users = currentUser.user.getJSONArray("fbFriends");
            int i;
            JSONObject user;
            for(i=0;i<users.length();i++){
                user = (JSONObject) users.get(i);
                userNamesArrayList.add((String)user.get("firstName")+(String)user.get("lastName"));
                usersSelectedArrayList.add(false);
            }
            usersAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_invited, menu);
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

