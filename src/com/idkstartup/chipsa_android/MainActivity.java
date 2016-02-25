package com.idkstartup.chipsa_android;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MainActivity extends AppCompatActivity {
	private ListView obj;
	private ArrayAdapter arrayAdapter;
	private Users users;
	private Chispas chispas;
	private CurrentUser currentUser;
	
	//need it to keep track which row index corresponds to which _id in chispas
	ArrayList<String> rowIndexTo_idMap = new ArrayList<String>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        users = new Users().getInstance();
        chispas = new Chispas().getInstance();
        currentUser = new CurrentUser().getInstance();
        
        //TODO: remove that
        try {
			currentUser.user.put("_id", "adjkasjfakjfa");
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        
        ScrollView t = (ScrollView)findViewById(R.id.scrollView1);
        t.setFillViewport(true);
        int i;
        ArrayList<String> array_list = new ArrayList<String>();
        
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list);

        obj = (ListView)findViewById(R.id.listView1);
        obj.setAdapter(arrayAdapter);
        obj.setOnItemClickListener(new OnItemClickListener(){
          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) {
              Bundle dataBundle = new Bundle();
              dataBundle.putString("_id",rowIndexTo_idMap.get(position));
              Intent intent = new Intent(getApplicationContext(),DisplayChispa.class);
              intent.putExtras(dataBundle);
              startActivity(intent);
          }
        });//end OnItemClickListener
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
         getAllEventHittups();
		return false;
    }//end onCreateOptionsMenu
    
    public void getAllEventHittups() {
        AsyncHttpClient client = new AsyncHttpClient();    

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
        	JSONArray arr= new JSONArray();
        	arr.put(20000);//TODO add a constant here
        	arr.put(20000);
        
            jsonParams.put("timeInterval", arr);
            entity = new StringEntity(jsonParams.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();            
        }
        
        client.post(null, "http://hittup.idkstartup.xyz/eventhittups/getallhittups", entity,"application/json", new JsonHttpResponseHandler() {
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
                Toast.makeText(getApplicationContext(), "success download!", Toast.LENGTH_LONG).show();
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
