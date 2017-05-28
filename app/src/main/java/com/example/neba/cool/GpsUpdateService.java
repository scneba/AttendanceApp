package com.example.neba.cool;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.neba.receiver.BootAndUpdateReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NEBA on 16-Aug-16.
 */
public class GpsUpdateService extends Service  {
    private boolean isRunning=false;
    private  Context context;
    private Thread backgroundThread;
    private static DBHelper dbhelper;
    private Cursor cursor;
    private String keyword,country;
    private  String url="http://192.168.173.1/laranew/public/updategps";
    private static String ssid="stk";
    private  static String key="thestkthestk";
    private Handler handler = new Handler(Looper.getMainLooper());
    private   WifiManager mainWifi;
    private static boolean found=false;
    public static  PendingIntent pendingIntent = null;
    public static AlarmManager alarmManager = null;
    private static int netId=0;
    private Integer count;
    checkNet cn = new checkNet();
    boolean isConfigured=false;
    private static Handler UIhandler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        mainWifi = (WifiManager) getSystemService(getApplicationContext().WIFI_SERVICE);
        this.backgroundThread = new Thread(myTask);
        this.context = getApplicationContext();
        dbhelper = new DBHelper(context);
        UIhandler = new Handler(Looper.getMainLooper());
        count=0;
        key=dbhelper.getKey();
        ssid=dbhelper.getSSID();
        //alarm intent to use in whole background
        Intent alarmIntent = new Intent(this.context, BootAndUpdateReceiver.class);
        alarmIntent.setAction("com.example.attendance");
        //check if alarm is running
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 1, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);

        //if no jobs in database yet and alarm not yet started make alarm repeat faster
        if (!alarmRunning) {
            pendingIntent = PendingIntent.getBroadcast(this.context, 1, alarmIntent, 0);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), dbhelper.getRepeatTime()*60*1000, pendingIntent);
        }


    }

    @Override
    public void onDestroy() {
        this.isRunning=false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //if user is logged in
            if (dbhelper.isLoggedIn())
            {
                //get wifi state
                int wifistate = mainWifi.getWifiState();
                    //if task is not already running
                    if (!this.isRunning)
                    {
                        if(wifistate==WifiManager.WIFI_STATE_ENABLED ||(dbhelper.getWifi().equals(0) && wifistate==WifiManager.WIFI_STATE_ENABLED) )
                        {
                                //if permission is granted for coarse location

                                if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                                {

                                    //wait for wifi to turn on completely
                                    if (!mainWifi.isWifiEnabled())
                                    {
                                        mainWifi.setWifiEnabled(true);
                                    }
                                    while(!mainWifi.isWifiEnabled()){}
                                    this.isRunning = true;
                                    this.backgroundThread.start();
                                }

                        }

                   }

            }
        return START_STICKY;
    }

    private Runnable myTask = new Runnable() {
        public void run()
        {

            try{

                if(ConnectedToWiFi())
                {

                    if(cn.checkInternetConnection(context)) {
                        new SaveGpsData().execute();
                    }

                }else
                {
                  ShowInButton("Network not in range\nTap to reload");
                    if(dbhelper.getWifi().equals(0))
                    {
                        mainWifi.setWifiEnabled(false);
                    }
                }
            }
            catch(Exception e){
             ShowInButton("Error in connectto");
            }

            stopSelf();
        }
    };


    private boolean ConnectedToWiFi()
    {
        found=false;
        int networkId=0;
        isConfigured=false;

        if(this.getCurrentSsid(context).trim().equals(ssid))
        {
            isConfigured=true;
           ShowInButton("SSid is already connected");
            return true;
        }


        //check if wifi network is already configured
        List<WifiConfiguration> listwifi= mainWifi.getConfiguredNetworks();
        for(WifiConfiguration config:listwifi){
            final String id="\""+ssid+"\"";
            if(config.SSID!=""&&config.SSID!=null){
                if(config.SSID.trim().equals(id)){
                    ShowInButton("WiFi configured");
                    isConfigured=true;
                    networkId=config.networkId;
                    break;
                }
            }
        }

                    mainWifi.startScan();
                   while(mainWifi.getScanResults()==null){
                   }
                    List<ScanResult> list = mainWifi.getScanResults();
                    for( ScanResult scanResult : list )
                    {

                        if (scanResult.SSID != null&&scanResult.SSID!="")
                        {
                            if (scanResult.SSID.equals(ssid))
                            {

                            ShowInButton("Network found");

                                if(isConfigured){

                                        if(this.getCurrentSsid(context).trim().equals(ssid)){
                                            ShowInButton("Network Configured");
                                            found=true;
                                            break;
                                        }

                                }

                                WifiConfiguration wifiConfig = new WifiConfiguration();
                                wifiConfig.SSID = String.format("\"%s\"", ssid);
                                wifiConfig.preSharedKey = String.format("\"%s\"", key);
                                netId = mainWifi.addNetwork(wifiConfig);
                                mainWifi.disconnect();
                               if(mainWifi.enableNetwork(netId, true)){
                                   postDelay();
                                   while(!cn.checkInternetConnection(context)){
                                   }
                                   found = true;
                               };
                                break;
                            }
                        }
                    }


        return found;
    }


    private class SaveGpsData extends AsyncTask<String,String,String>
    {
        String userid = dbhelper.getUserId();
        String message = "";
        JSONObject output=null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           ShowInButton("Sending Attendance....");
        }


        @Override
        protected String doInBackground(String... params) {
            HashMap<String,String> hm = new HashMap<String,String>();
            hm.put("userid", userid);
              try
              {
                  HttpRequest req = new HttpRequest(url);
                  output = req.prepare(HttpRequest.Method.POST).withData(hm).sendAndReadJSON();

              } catch (Exception e) {
                  message = "Unable to connect to database";
              }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                final String serverMessage=output.getString("msg");
                final String serverStatus=output.getString("status");
                if(serverStatus.equals("success"))
                {
                    Toast.makeText(context,"successfully sent",Toast.LENGTH_SHORT).show();

                   ShowInButton("Successfully Sent");
                }else{

                    Toast.makeText(context,serverMessage,Toast.LENGTH_SHORT).show();
                    ShowInButton(serverMessage);
                }


            } catch (Exception e) {

                ShowInButton(message);
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }

                if (dbhelper.getWifi().equals(0))
                {
                    mainWifi.disconnect();
                    if(!isConfigured)
                    {
                        if (mainWifi.removeNetwork(netId))
                        {
                            mainWifi.saveConfiguration();
                        }
                    }
                    mainWifi.setWifiEnabled(false);
                } else {

                    if(!isConfigured)
                    {
                        mainWifi.disconnect();
                        if (mainWifi.removeNetwork(netId))
                        {
                            mainWifi.saveConfiguration();
                        }
                    }
                }


        }
    }
    private  boolean isForeground(String myClass) {
        Boolean t=false;

        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        for( int i=0;i<runningTaskInfo.size();i++){
            ComponentName componentInfo = runningTaskInfo.get(i).topActivity;
            if( componentInfo.getClassName().toString().equals(myClass)){
                t=true;
                break;
            }

        }
        return t;
    }

    public static String getCurrentSsid(Context context) {
        String ssid = "NOTHING";
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }
    private  void ShowInButton(String text){
        try {
            final String ftext=text;
            if (isForeground("com.example.neba.cool.UploadActivity")) {
                UploadActivity.runOnUI(new Runnable() {
                    @Override
                    public void run() {
                        UploadActivity.btnWiFi.setText(ftext);
                    }
                });
            }
        }catch (Exception e){}
    }

    private void postDelay(){
        try {
            UIhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isRunning = false;
                }
            }, 10000);
        }catch (Exception e){}
    }
}
