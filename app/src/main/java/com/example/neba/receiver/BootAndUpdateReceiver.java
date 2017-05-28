package com.example.neba.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootAndUpdateReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e("reach onreceive", "reach onreceive");
    		//if broadcast has no action do nothing
    	if(intent.getAction()==null||intent.getAction()==""){
            return;
        }

        if( intent.getAction().toString().trim().equals("com.example.attendance")||intent.getAction().toString().trim().equals("android.net.wifi.WIFI_STATE_CHANGED") ){
            Intent background = new Intent(context, com.example.neba.cool.GpsUpdateService.class);
            context.startService(background);
        }

       else  if (intent.getAction().toString().trim().equals("android.intent.action.BOOT_COMPLETED") ||
                    intent.getAction().toString().trim().equals("android.intent.action.MY_PACKAGE_REPLACED")||
                intent.getAction().toString().trim().equals("android.net.conn.CONNECTIVITY_CHANGE")||
                    intent.getAction().toString().trim().equals("com.example.njorku.REFRESH_DATA")||
                    intent.getAction().toString().trim().equals("com.example.Alarm")){
            Log.e("action", intent.getAction());
            Intent background = new Intent(context, com.example.neba.cool.BackgroundService.class);
            context.startService(background);
    		}


    }
   
}