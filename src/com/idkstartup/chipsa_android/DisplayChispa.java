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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DisplayChispa extends AppCompatActivity {
    private Chispas chispas;
    private JSONObject chispa;
    private JSONObject owner;
    private CurrentUser currentUser;
    
    private ListView usersListView;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_chispa);
        ////////////////////////////////////////////////////////
        chispas = new Chispas().getInstance();
        currentUser = new CurrentUser().getInstance();

        Button viewInvitedbtn = (Button)findViewById(R.id.viewInvitedbtn);
        Button viewJoinedbtn = (Button)findViewById(R.id.viewJoinedbtn);
        TextView ownerTextView = (TextView)findViewById(R.id.ownerNametxt);
        ImageView ownerProfileimg = (ImageView)findViewById(R.id.ownerProfileimg);
        Button deletebtn = (Button)findViewById(R.id.deletebtn);
        
        /////////////////////////////////////////////////////////
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
        try {
			initializeScrollViews();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
//                  jsonParams.put("ownerName", (String)owner.get("firstName")+(String)owner.get("lastName"));
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
                
            }
        });
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
        tmp = (JSONArray)chispa.get("usersCheckedIn");
        for(i=0;i<tmp.length();i++){
            JSONObject user = (JSONObject) tmp.get(i);
            timeAgo.add((Double)user.get("date"));
            user=user.getJSONObject("user");
            userNames.add((String)user.get("firstName"));
            userfbids.add((String)user.get("fbid"));
        }
        lengthOfCheckedIn=userNames.size();
        
        tmp = (JSONArray)chispa.get("usersJoined");
        for(i=0;i<tmp.length();i++){
            JSONObject user = (JSONObject) tmp.get(i);
            timeAgo.add((Double)user.get("date"));
            user=user.getJSONObject("user");
            userNames.add((String)user.get("firstName"));
            userfbids.add((String)user.get("fbid"));
        }
           
        lengthOfCheckedInAndJoined=userNames.size();
        tmp = (JSONArray)chispa.get("usersInvited");
        for(i=0;i<tmp.length();i++){
            JSONObject user = (JSONObject) tmp.get(i);
            timeAgo.add((Double)user.get("date"));
            user=user.getJSONObject("user");
            userNames.add((String)user.get("firstName"));
            userfbids.add((String)user.get("fbid"));
        }
        ////////////////////////
        
        usersListView= (ListView)findViewById(R.id.usersListView);
        UsersListViewAdapter myAdapter=  new UsersListViewAdapter(this, userNames, userfbids, timeAgo, lengthOfCheckedIn,lengthOfCheckedInAndJoined);
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
//        setOnItemClickListener(this);
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