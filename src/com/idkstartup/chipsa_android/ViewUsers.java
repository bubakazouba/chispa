package com.idkstartup.chipsa_android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * extras:
 * 	String title: title of view
 *  String mode: valid values: ["SELECT"] determines mode of view
 */
public class ViewUsers extends AppCompatActivity {
	private CurrentUser currentUser;
	
	
	private ArrayAdapter arrayAdapter;
	private ListView usersList;
	private String mode;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_users);
		currentUser = new CurrentUser().getInstance();
		
        ArrayList<String> array_list = new ArrayList<String>();
        
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);

        usersList = (ListView)findViewById(R.id.listView1);
        usersList.setAdapter(arrayAdapter);

		Bundle extras = getIntent().getExtras(); 
		mode = (String) extras.get("mode");
		setTitle((String) extras.get("title"));
		Toast.makeText(getApplicationContext(), "Grabbed User's data="+currentUser.user.toString(), Toast.LENGTH_LONG).show();
		try {
			JSONArray users = currentUser.user.getJSONArray("fbFriends");
			int i;
			JSONObject user;
			for(i=0;i<users.length();i++){
				user = (JSONObject) users.get(i);
				arrayAdapter.add((String)user.get("firstName")+(String)user.get("lastName"));
			}
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
