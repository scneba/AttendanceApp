package com.example.neba.cool;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.neba.receiver.BootAndUpdateReceiver;


public class BackgroundService extends Service {

	Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private boolean isRunning=false;
    private Context context;
	private Thread backgroundThread;
	private DBHelper dbhelper;
	private Cursor cursor;
	private String keyword,country;
	 private static String url="http://192.168.173.1/laranew/public/getmsg";
	 @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
 
    @Override
    public void onCreate() {
		    this.backgroundThread = new Thread(myTask);
			this.context = getApplicationContext();
			PendingIntent pendingIntent = null;
			AlarmManager alarmManager = null;
			dbhelper = new DBHelper(context);
			//alarm intent to use in whole background
			Intent alarmIntent = new Intent(this.context, BootAndUpdateReceiver.class);
		    alarmIntent.setAction("com.example.Alarm");
			//check if alarm is running
			boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);

			//if no jobs in database yet and alarm not yet started make alarm repeat faster
			if (!alarmRunning) {
				pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarmIntent, 0);
				alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

				alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 30000, pendingIntent);
			}


	}
 
    private Runnable myTask = new Runnable() {
        public void run() {
     
    	 try{
    		
    	      new getJobs().execute();
    	 }
    	 catch(Exception e){}
     
           stopSelf();
        }
    };
 
    @Override
    public void onDestroy() { 
        this.isRunning = false;
      
    }
 
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		//if user is logged in
	if(dbhelper.isLoggedIn()) {
			//if there is internet connection
			if(new checkNet().checkInternetConnection(context))
			{
				//if task is not already running
				if (!this.isRunning) {
					this.isRunning = true;
					this.backgroundThread.start();
			}
		}
		}
        return START_STICKY;
    }
    

    
    private class  getJobs extends AsyncTask<String, String, String> {
    	  String msg=null;
    	  String message="";
		private JSONArray jsonArray;
 
        
        @Override
        protected void onPreExecute() {


        }
 
        /**
         * authenticating the new user
         * */
        @SuppressWarnings("deprecation")
		protected String doInBackground(String... args) {
			try {
				String userid = dbhelper.getUserId();
				HashMap<String,String> hm = new HashMap<String,String>();
				hm.put("userid", userid);
				HttpRequest req = new HttpRequest(url);
				jsonArray=req.prepare(HttpRequest.Method.POST).withData(hm).sendAndReadJSONArray();

			}catch (Exception e){
				message="Unable to connect to database";

			}
            

            //return null;
            return null;
        }
        
 
        protected void onPostExecute(String file_url) {

			Integer numNewMessages=0;
			if(message=="")
			{
				try {
					//boolean value to indicate if there is new job
					Boolean check=false;
					//loop through jobs to check if any is new
					for(int i=0;i<jsonArray.length();i++) {
						check = false;
						JSONObject jo = jsonArray.getJSONObject(i);

						//get check ids
						ArrayList<String> checklist = dbhelper.getCheckIds();

						//compare check ids to incoming job
						for (int j = 0; j < checklist.size(); j++) {
							String a = checklist.get(j).toString().trim();


							//check if job already exist in database, if true then break;
							if (a.equals(jo.getString("id").toString().trim())) {
								check = true;
								break;
							}

						}//end inner for

						if (!check) {
							numNewMessages++;
							if (!dbhelper.InsertMessages(jo.getString("title"), jo.getString("message"), jo.getString("created_at"))) {


								ShowNotification("Etrack Error", "Error Inserting data to database");
                            }else{
								dbhelper.InsertCheckId(jo.getString("id").trim());
							}

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}


                 if(numNewMessages>0) {
					 if (isForeground("com.example.neba.cool.MessageActivity")) {
						 MessageActivity.RunOnUI(new Runnable() {
							 @Override
							 public void run() {
								 MessageActivity.messageAdapter = new MessageAdapter(context, dbhelper.getAllMessages());
								 MessageActivity.listView.setAdapter(MessageActivity.messageAdapter);
							 }
						 });
					 }else{
						 ShowNotification(numNewMessages+" new Message(s)",numNewMessages+" new Message(s)");
					 }

				 }
				}
			}
      
        }
    
    private boolean isForeground(String myClass) {
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
	private void ShowNotification(String ticker,String content){

		Intent intent = new Intent(getApplicationContext(),com.example.neba.cool.MessageActivity.class);

		// Open NotificationView.java Activity
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		//Create Notification using NotificationCompat.Builder
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				// Set Icon
				.setSmallIcon(R.mipmap.ic_launcher)
						// Set Ticker Message
				.setTicker(ticker)
						// Set Title
				.setContentTitle(getString(R.string.app_name))
						// Set Text
				.setContentText(content)
						// Set PendingIntent into Notification
				.setContentIntent(pIntent)
				.setSound(soundUri)
						// Dismiss Notification
				.setAutoCancel(true);


		// Create Notification Manager
		NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Build Notification with Notification Manager
		notificationmanager.notify(0, builder.build());

	}
}
