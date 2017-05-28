package com.example.neba.cool;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class checkNet {
	private Context context;
	public checkNet() {
		
		
		}
	
	
	   public boolean checkInternetConnection(Context context){
	    	boolean  c = false;
	    	this.context=context;
	    	  try
		    	{
	    	ConnectivityManager check = (ConnectivityManager) this.context.
	    	getSystemService(Context.CONNECTIVITY_SERVICE);
	  
	    	NetworkInfo[] info = check.getAllNetworkInfo();
	    	if (info != null){
	    	for (int i = 0; i <info.length; i++)
	    	if (info[i].getState() == NetworkInfo.State.CONNECTED)
	    	{
	    	c= true;
	    	}
	    	}
	    	}
	    catch(Exception e){
	    	
	    Log.e("internet check error", e.toString());
	    	}
	    return c;
}
	
}
