package com.example.neba.cool;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NEBA on 05-Aug-16.
 */
public class MessageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ProgressDialog pDialog;
    private  String url =  MainActivity.tempUrl+"getmessage";
    private Bundle bundle;

    private EditText etcode;
    private EditText etpassword;
    private  static Handler UIHandler ;
    public static ListView  listView;
    public static MessageAdapter messageAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView =(ListView)findViewById(R.id.msgList);
        listView.setOnItemClickListener(this);
        dbHelper = new DBHelper(getApplicationContext());

        bundle = new Bundle();//create bundle
        UIHandler = new android.os.Handler(Looper.getMainLooper());//handler to run background activities

        if(!dbHelper.MessageDBEmpty()){
            messageAdapter=new MessageAdapter(getApplication(),new ArrayList<Option>(dbHelper.getAllMessages()));
            listView.setAdapter(messageAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.logout:{
                MainActivity.dbHelper.logoutUser();
                Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.LoginActivity.class);
                bundle.putInt("key",MainActivity.AcType.TRACK.value);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static void RunOnUI(Runnable runnable){
        UIHandler.post(runnable);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Option option =(Option)messageAdapter.getItem(position);
        try{
            dbHelper.UpdateSeenStatus(option.getId(),1);

                // Create Notification Manager
                NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Build Notification with Notification Manager
                notificationmanager.cancel(0);

        }catch (Exception e){}

        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.MessageView.class);
        bundle.putString("title", option.getTitle());
        bundle.putString("message",option.getMessage() );
        bundle.putString("date",option.getDate() );
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }




}
