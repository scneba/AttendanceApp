package com.example.neba.cool;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Criteria;

import com.example.neba.receiver.BootAndUpdateReceiver;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Manifest;

/**
 * Created by NEBA on 05-Aug-16.
 */
public class UploadActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private  String url =  MainActivity.tempUrl+"savetrack";
    private Bundle bundle;
    private final int MY_PERMISSION_COARSE_LOCATION=1;
    private BootAndUpdateReceiver updateReceiver;
    private final int MY_PERMISSION_CAMERA=2;
    private static Handler UIHandler;
    public static Button btnWiFi;

    private TextView textView;
   private   WifiManager mainWifi;
    private String ssid="stk";
    private String key="thestkthestk";
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        UIHandler = new Handler(Looper.getMainLooper());
        dbHelper = new DBHelper(getApplicationContext());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnWiFi = (Button)findViewById(R.id.btnWifi);


        //register receiver
        updateReceiver = new BootAndUpdateReceiver();
        IntentFilter intentFilter = new
                IntentFilter("com.example.attendance");
        registerReceiver(updateReceiver, intentFilter);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_COARSE_LOCATION);
        }




        btnWiFi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendBroadCast();

            }
        });
        bundle = new Bundle();
        sendBroadCast();
    }

     public void ConnectToWiFi(String ssid ,String key,Context ctx) {

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(ctx.WIFI_SERVICE);

         List<ScanResult> list = wifiManager.getScanResults();
         for( ScanResult scanResult : list ) {

             if (scanResult.SSID != null) {
                 if (scanResult.SSID.equals(ssid)) {
                     int netId = wifiManager.addNetwork(wifiConfig);
                     wifiManager.disconnect();
                     wifiManager.enableNetwork(netId, true);
                     break;
                 }
             }
         }


    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.MainActivity.class);
        startActivity(intent);
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
                break;
            }
            case R.id.logout:
            {
                MainActivity.dbHelper.logoutUser();
                Intent intent = new Intent(getApplicationContext(), com.example.neba.cool.LoginActivity.class);
                bundle.putInt("key",MainActivity.AcType.TRACK.value);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;

            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver);
    }





    private class  SendGPS extends AsyncTask<String, String, String> {


        Date date= new Date();

        String emcode=MainActivity.dbHelper.getUserId().trim();
        String message = "";

        JSONObject output;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Inserting the new user
         * */
        protected String doInBackground(String... args) {


            HashMap<String,String> hm = new HashMap<String,String>();
            hm.put("emcode",emcode);

            try {
                HttpRequest req = new HttpRequest(url);
                output=req.prepare(HttpRequest.Method.POST).withData(hm).sendAndReadJSON();
                Log.e("data",output.toString());

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

        }



    }

 public void sendBroadCast(){
     Intent intnet = new Intent();
     intnet.setAction("com.example.attendance");
     sendBroadcast(intnet);
 }


    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }


}
