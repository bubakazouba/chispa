package com.idkstartup.chipsa_android;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DisplayChispaActivity extends AppCompatActivity {
    private Chispas chispas;
    private Chispa chispa;
    private JSONObject owner;
    private CurrentUser currentUser;
    private UsersListViewAdapter myAdapter;
    private ListView usersListView;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chispa);
        Bundle extras = getIntent().getExtras(); 
        String _id = (String) extras.get("_id");

        chispas = new Chispas().getInstance();
        currentUser = new CurrentUser().getInstance();

        ////////////////////////////////////////////////////////
        Button joinbtn = (Button)findViewById(R.id.joinbtn);
        Button checkinbtn = (Button)findViewById(R.id.checkinbtn);
        TextView ownerTextView = (TextView)findViewById(R.id.ownerNametxt);
        TextView timeChispaPostedAgotxt = (TextView)findViewById(R.id.timeChispaPostedAgotxt);
        ImageView ownerProfileimg = (ImageView)findViewById(R.id.ownerProfileimg);
        Button deletebtn = (Button)findViewById(R.id.deletebtn);
        TableRow ownerRow = (TableRow)findViewById(R.id.ownerRow);
        TableRow notOwnerRow= (TableRow)findViewById(R.id.notOwnerRow);
        /////////////////////////////////////////////////////////
        
        try {
            chispa = new Chispa(chispas.chispas.get(_id));
            owner = (JSONObject) chispa.chispa.get("owner");

            ownerTextView.setText((String)owner.getString("firstName")+(String)owner.getString("lastName"));
            
            int seconds = (int) (System.currentTimeMillis()/1000.0-chispa.chispa.getDouble("dateStarts"));
            int minutes = seconds/60;
            timeChispaPostedAgotxt.setText(Integer.toString(minutes)+"m");
            
            String fbid = (String) owner.get("fbid");
            String url="http://graph.facebook.com/"+fbid+"/picture?type=square";
            Picasso.with(getApplicationContext()).load(url).into(ownerProfileimg);
            
            //hide both rows, then check if the user is the owner to see which one should I show
            ownerRow.setVisibility(View.GONE);
            notOwnerRow.setVisibility(View.GONE);
            if(currentUser.user.get("_id").equals(owner.get("_id"))){//if currentUser is the owner, show the ownerRow
            	ownerRow.setVisibility(View.VISIBLE);
            }
            else {
            	notOwnerRow.setVisibility(View.VISIBLE);
            	
            	joinbtn.setVisibility(View.GONE);
            	checkinbtn.setVisibility(View.GONE);
            	
            	if(chispa.getUsersJoineduids().indexOf(currentUser.user.get("_id"))==-1 && chispa.getUsersCheckedInuids().indexOf(currentUser.user.get("_id")) ==-1){
            		joinbtn.setVisibility(View.VISIBLE);
            	}
            	//need to know if user has joined or checkedin
            	if(chispa.getUsersJoineduids().indexOf(currentUser.user.get("_id"))!=-1){
            		checkinbtn.setVisibility(View.VISIBLE);
            	}
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
			initializeScrollViews();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        ////////////////////////////////////////////////buttons listener/////////////////////////////////
        	/*
        	 * button listener for join/check in/delete
        	 */
          OnClickListener allbtnsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                JSONObject jsonParams = new JSONObject();
                StringEntity entity = null;
                try {
                	Button currentBtn = (Button)v; 
                	if(v.getId()==R.id.deletebtn){
                		jsonParams.put("owneruid", owner.get("_id"));
                        jsonParams.put("ownerName", (String)owner.get("firstName")+(String)owner.get("lastName"));
                	}
                	else {
                		jsonParams.put("owneruid", owner.get("_id"));
                        jsonParams.put("ownerName", (String)owner.get("firstName")+(String)owner.get("lastName"));                		
                	}
                	
                	String urlPath="http://chispa.idkstartup.xyz/FriendHittups/";
                	
                	switch(currentBtn.getId()){
                	case R.id.deletebtn:
                		urlPath+="RemoveHittup";
                		break;
                	case R.id.joinbtn:
                		urlPath+="JoinHittup";
                		myAdapter.addUserToJoined(currentUser.user.getString("firstName"), currentUser.user.getString("fbid"));
                		currentBtn.setVisibility(View.GONE);
                		((Button)findViewById(R.id.checkinbtn)).setVisibility(View.VISIBLE);
                		break;
                	case R.id.checkinbtn:
                		currentBtn.setVisibility(View.GONE);
                		myAdapter.addUserToCheckedIn(currentUser.user.getString("firstName"), currentUser.user.getString("fbid"));
                		urlPath+="checkInHittup";
                		break;
                	}
                    jsonParams.put("hittupuid", chispa.chispa.get("_id"));
                    
                    
                    entity = new StringEntity(jsonParams.toString());
                    client.post(null, urlPath, entity,"application/json", new JsonHttpResponseHandler() {
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
                	if(currentBtn.getId()==R.id.deletebtn)
                		finish();//go back to previous activity
                	
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();            
                }
                
            }
        };
        
        deletebtn.setOnClickListener(allbtnsClickListener);
        joinbtn.setOnClickListener(allbtnsClickListener);
        checkinbtn.setOnClickListener(allbtnsClickListener);

    }
    
    public void initializeScrollViews() throws JSONException{
    	ArrayList<String> userNames,userfbids;
    	ArrayList<Double> timeAgo = new ArrayList<Double>();
        userNames =  new ArrayList<String>();
        userfbids =  new ArrayList<String>();
        int i;
        int lengthOfCheckedIn=0,lengthOfCheckedInAndJoined=0;
        JSONArray tmp;
        
        ////////////////////////
        tmp = (JSONArray)chispa.chispa.get("usersCheckedIn");
        for(i=0;i<tmp.length();i++){
            JSONObject user = (JSONObject) tmp.get(i);
            timeAgo.add((Double)user.get("date"));
            user=user.getJSONObject("user");
            userNames.add((String)user.get("firstName"));
            userfbids.add((String)user.get("fbid"));
        }
        lengthOfCheckedIn=userNames.size();
        
        tmp = (JSONArray)chispa.chispa.get("usersJoined");
        for(i=0;i<tmp.length();i++){
            JSONObject user = (JSONObject) tmp.get(i);
            timeAgo.add((Double)user.get("date"));
            user=user.getJSONObject("user");
            userNames.add((String)user.get("firstName"));
            userfbids.add((String)user.get("fbid"));
        }
           
        lengthOfCheckedInAndJoined=userNames.size();
        tmp = (JSONArray)chispa.chispa.get("usersInvited");
        for(i=0;i<tmp.length();i++){
            JSONObject user = (JSONObject) tmp.get(i);
            timeAgo.add((Double)user.get("date"));
            user=user.getJSONObject("user");
            userNames.add((String)user.get("firstName"));
            userfbids.add((String)user.get("fbid"));
        }
        ////////////////////////
        
        usersListView= (ListView)findViewById(R.id.usersListView);
        myAdapter=  new UsersListViewAdapter(this, userNames, userfbids, timeAgo, lengthOfCheckedIn,lengthOfCheckedInAndJoined);
        usersListView.setAdapter(myAdapter);
        usersListView.setOnItemClickListener(myAdapter);
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


class UsersListViewAdapter extends BaseAdapter implements OnItemClickListener {

    Context context;
    //userNames doesn't change
    //usersArrayList contains the current value for the textViews
    ArrayList<String> usersArrayList,userNames,userfbids;
    ArrayList<Double> usersTimeAgo;
    int lengthOfCheckedIn,lengthOfCheckedInAndJoined;//checkedin come first then joined then invited
    final private String CHECKED_IN_STR = "Checked in";
    final private String JOINED_STR = "On their way!";
    final private String INVITED_STR = "Invited";

    private static LayoutInflater inflater = null;

    public UsersListViewAdapter(Context context, ArrayList<String> userNames, ArrayList<String> userfbids,ArrayList<Double> usersTimeAgo, int lengthOfCheckedIn, int lengthOfCheckedInAndJoined) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.userNames=userNames;
        int i=0;//deep copy
        usersArrayList=new ArrayList<String>();
        for(i=0;i<userNames.size();i++)
        	usersArrayList.add(userNames.get(i));
        this.userfbids=userfbids;
        this.usersTimeAgo=usersTimeAgo;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        this.lengthOfCheckedIn=lengthOfCheckedIn;
        this.lengthOfCheckedInAndJoined=lengthOfCheckedInAndJoined;
    }
    
    @Override
    public int getCount() {
        return userNames.size();
    }

    @Override
    public Object getItem(int position) {
    	//not sure why i need this
    	return userNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addUserToJoined(String name, String fbid) {
    	int index = -1,i;
    	for(i=0;i<userfbids.size();i++){if(fbid==userfbids.get(i)) index=i;}
    	Log.d("HTTP", "index="+index);
    	if(index!=-1){
    		userNames.remove(index);
    		usersArrayList.remove(index);
    		userfbids.remove(index);
    	}
    	
    	userNames.add(lengthOfCheckedIn, name);
    	usersArrayList.add(lengthOfCheckedIn, name);
    	userfbids.add(lengthOfCheckedIn, fbid);
    	usersTimeAgo.add(lengthOfCheckedIn,(double) (System.currentTimeMillis()/1000));
    	
    	lengthOfCheckedInAndJoined++;
    	notifyDataSetChanged();
    }
    
    public void addUserToCheckedIn(String name, String fbid) {
    	int index=-1,i;
    	for(i=0;i<userfbids.size();i++){if(fbid==userfbids.get(i)) index=i;}
    	Log.d("HTTP", "index="+index);
    	if(index!=-1){
    		userNames.remove(index);
    		usersArrayList.remove(index);
    		userfbids.remove(index);
    	}
    	
    	userNames.add(0, name);
    	usersArrayList.add(0, name);
    	userfbids.add(0, fbid);
    	usersTimeAgo.add(lengthOfCheckedIn,(double) (System.currentTimeMillis()/1000));
    	
    	lengthOfCheckedIn++;
    	notifyDataSetChanged();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.users_list_item, null);
        
        
        TextView text = (TextView) vi.findViewById(R.id.text);
        ImageView profileImage = (ImageView) vi.findViewById(R.id.profileImg);
        TextView timeAgoView = (TextView) vi.findViewById(R.id.timeAgo);
        
        String url="https://graph.facebook.com/"+userfbids.get(position)+"/picture?type=large";
        Picasso.with(context).load(url).into(profileImage);
        
        text.setText(usersArrayList.get(position));
        
        int seconds = (int) (System.currentTimeMillis()/1000.0-usersTimeAgo.get(position));
        int minutes = seconds/60;
        timeAgoView.setText(Integer.toString(minutes)+"m");
        
        
        return vi;
    }
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		String str;
		if(position>=lengthOfCheckedInAndJoined){
			str=INVITED_STR;
		}
		else {
			str=position>=lengthOfCheckedIn?JOINED_STR:CHECKED_IN_STR;;
		}
        if(usersArrayList.get(position).equals(str)){
            usersArrayList.set(position, userNames.get(position));
        }
        else {
            usersArrayList.set(position, str);
        }
         notifyDataSetChanged();
	}
    
}