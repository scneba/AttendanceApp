package com.example.neba.cool;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.app.ProgressDialog;

import com.example.neba.receiver.BootAndUpdateReceiver;

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
public class LoginActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private  String url =  MainActivity.tempUrl+"clasence";
    private Bundle bundle;

    private EditText etcode;
    private EditText etpassword;
    private BootAndUpdateReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

          etcode = (EditText)findViewById(R.id.etcode);
          etpassword = (EditText)findViewById(R.id.etpassword);
        Button btnlogin = (Button)findViewById(R.id.btnlogin);
        TextView tvforgotpass = (TextView)findViewById(R.id.tvforgot);

        bundle = getIntent().getExtras();

        //redirect if user is loggged in
        if(MainActivity.dbHelper.isLoggedIn()){
            switch(bundle.getInt("key")) {
                case 0:
                {
                    Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.UploadActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                case 1:
                {

                    Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.ViewActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }
                case 2:
                {
                    Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.MessageActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                }

            }

        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation.isCodeWord(etcode, getApplicationContext()) && Validation.isPassword(etpassword, getApplicationContext())) {

                    if (new checkNet().checkInternetConnection(getApplicationContext())) {

                        new Authenticate().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Some fields are invalid", Toast.LENGTH_LONG).show();
                }

            }
        });


        //register receiver
        updateReceiver = new BootAndUpdateReceiver();
        IntentFilter intentFilter = new
                IntentFilter("com.example.njorku.REFRESH_DATA");
        registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class  Authenticate extends AsyncTask<String, String, String> {

         String emcode;
        String password;
        String message = "";

        String output = "0";

        /**
         * Before starting background thread Show Progress Dialog
         * */
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(String.format("%s\n%s", "please wait...", "click on screen to stop"));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            emcode= etcode.getText().toString().trim();
            password = etpassword.getText().toString().trim();
        }

        /**
         * Inserting the new user
         * */
        protected String doInBackground(String... args) {

               HashMap<String,String> hm = new HashMap<String,String>();
            hm.put("emcode",emcode);
            hm.put("password",password);

            try {
                HttpRequest req = new HttpRequest(url);
                output=req.prepare(HttpRequest.Method.POST).withData(hm).sendAndReadString();
                Log.e("data",output);

            }catch (Exception e){
               message="Unable to connect to database";
            }



            //return null;
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
             pDialog.dismiss();
            if(output.trim().equals("1")) {
               MainActivity.dbHelper.createSession(emcode);
                message="Valid User";

                //send broadcast
                Intent intnet = new Intent();
                intnet.setAction("com.example.njorku.REFRESH_DATA");
                sendBroadcast(intnet);
                switch(bundle.getInt("key")) {
                    case 0:
                    {
                        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.UploadActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case 1:
                    {

                        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.ViewActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case 2:
                    {
                        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.MessageActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }

                }
            }else {
                if (message != "") {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } else {
                    message = "Invalid credentials";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }

            }


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver);
    }
}
