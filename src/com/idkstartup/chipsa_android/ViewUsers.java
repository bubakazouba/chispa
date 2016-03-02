package com.idkstartup.chipsa_android;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * extras
 * 
 * takes:
 *  String title: title of view
 *  String mode: valid values: ["SELECT"] determines mode of view
 *  ArrayList<string> selecteduids: selected uids 
 *  
 *  returns:
 *  "uids", an array of strings of users selected
 */

class modifiedList extends ListView {

	public modifiedList(Context context) {
		super(context);
		
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		return parent;
	}
	
}

public class ViewUsers extends AppCompatActivity {
    private CurrentUser currentUser;
    
    private ListView usersList;
    Button submitbtn;
    
    private ArrayAdapter usersAdapter;
    private String mode;
    
    ArrayList<String> usersuidsArrayList = new ArrayList<String>();
    ArrayList<String> userNamesArrayList = new ArrayList<String>();
    ArrayList<Boolean> usersSelectedArrayList = new ArrayList<Boolean>(); //for keeping track when list items are clicked
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        currentUser = new CurrentUser().getInstance();
        
        Bundle extras = getIntent().getExtras(); 
        mode = extras.getString("mode");
        ArrayList<String> selecteduids = extras.getStringArrayList("selecteduids"); //when we loop through the users to add them to the ListView, this list is used to check if they are selected already
        String title = extras.getString("title"); 
        setTitle(title);

        
        ///////////////////////////////////////////////////
        usersList = (ListView)findViewById(R.id.listView1);
        submitbtn = (Button)findViewById(R.id.submitbtn);
        ///////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////
        //usersList, get users, put them in list, initialize usersSelectedArrayList, implements usersList.OnItemClickListener
        try {
            JSONArray users = currentUser.user.getJSONArray("fbFriends");
            int i;
            JSONObject user;
            for(i=0;i<users.length();i++){
                user = (JSONObject) users.get(i);
                String useruid = user.getString("_id");
                userNamesArrayList.add(user.getString("firstName")+user.getString("lastName"));
                usersuidsArrayList.add(useruid);
                int j;
                boolean exists=false;
                for(j=0;j<selecteduids.size();j++)
                	if(selecteduids.get(j).equals(useruid))
                		exists=true;
//                if(selecteduids.indexOf(useruid)!=-1)
                if(exists)
                	usersSelectedArrayList.add(true); 	
                else
                	usersSelectedArrayList.add(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        usersAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, userNamesArrayList);
        usersList.setAdapter(usersAdapter);
        //I need this so that "WHEN" the list loads, I can get all the views(the items) and set the ones that are selected to be selected visually
        usersList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int i;
                for(i=0;i<usersSelectedArrayList.size();i++){
                    View v = getViewByPosition(i, usersList);
//                    Log.d("HTTP","bool="+usersSelectedArrayList.get(i)+"id="+v.getId());
                    if(usersSelectedArrayList.get(i))
                        v.setBackgroundColor(0xFF00FF00);
                }
                // unregister listener (this is important)
                usersList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        
        usersList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
//				Log.d("HTTP","onItemClick id="+view.getId());
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
        //////////////////////////////////////////////////////////////////////////////////////////
        
        submitbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int numberOfSelectedUsers,i;
				
				ArrayList<String> selectedUsersuids = new ArrayList<String>();
				for(i=0;i<userNamesArrayList.size();i++)
					if(usersSelectedArrayList.get(i))
						selectedUsersuids.add(usersuidsArrayList.get(i));
					
				Intent returnIntent = new Intent();				
				returnIntent.putExtra("uids", selectedUsersuids);
				setResult(Activity.RESULT_OK,returnIntent);
				finish();	
			}
		});
        
        
    }//end onCreate

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
    
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        
        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
//        	Log.d("HTTP","failure pos="+pos);
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
//            Log.d("HTTP","success pos="+pos);
            return listView.getChildAt(childIndex);
        }
    }
}

