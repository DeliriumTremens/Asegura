package com.lorant.mobile.android.util;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.ui.view.Launcher;

public class PushNotificationUtil {
	
  protected static final String TAG = "PushNotificationUtil";

  private GoogleCloudMessaging gcm;
  private AtomicInteger msgId = new AtomicInteger();
  private SharedPreferences prefs;
  private String regid;
  private Activity caller = null;
	
	
  public void registerService(Activity caller){
	this.caller = caller;
   	if(checkPlayServices()) {
       gcm = GoogleCloudMessaging.getInstance(caller);
       regid = getRegistrationId(caller.getApplicationContext());
       if(regid.isEmpty()) {
          registerInBackground();
       }
    } else {
      Log.i(TAG, "No valid Google Play Services APK found.");
    }
  }
	
  private boolean checkPlayServices() {
	int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(caller);
	if(resultCode != ConnectionResult.SUCCESS) {
	  if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	    GooglePlayServicesUtil.getErrorDialog(resultCode, caller,
	             Config.PLAY_SERVICES_RESOLUTION_REQUEST).show();
	  } else {
	     Log.i(TAG, "This device is not supported.");
	  }
	  return false;
	}
	return true;
  }
	  
  private String getRegistrationId(Context context) {
	SharedPreferences prefs = getGCMPreferences(context);
	String registrationId = prefs.getString("registration_id", "");
	int registeredVersion, currentVersion;
	if(registrationId.isEmpty()) {
	  Log.i(TAG, "Registration not found.");
      return "";
	}
	registeredVersion = prefs.getInt("appVersion", Integer.MIN_VALUE);
	currentVersion = getAppVersion(context);
	if(registeredVersion != currentVersion) {
	  Log.i(TAG, "App version changed.");
	  return "";
	}
	return registrationId;
  }
	  
  private SharedPreferences getGCMPreferences(Context context) {
	return caller.getSharedPreferences(Launcher.class.getSimpleName(),
		                                        Context.MODE_PRIVATE);
  }
	  
  private int getAppVersion(Context context) {
    try {
		 PackageInfo packageInfo = context.getPackageManager()
		         .getPackageInfo(context.getPackageName(), 0);
		 return packageInfo.versionCode;
     } catch (NameNotFoundException e) {
		  throw new RuntimeException("Could not get package name: " + e);
     }
  }
	  
  @SuppressWarnings("unchecked")
  private void registerInBackground() {
    new AsyncTask() {
		@Override
		protected String doInBackground(Object... params) {
		  String msg = "";
		  try{
		      if(gcm == null) {
		        gcm = GoogleCloudMessaging.getInstance(caller
		        		           .getApplicationContext());
		      }
		      regid = gcm.register(Config.PUSH_SENDER_ID);
		      msg = "Device registered, registration ID=" + regid;
		      sendRegistrationIdToBackend();
		      storeRegistrationId(caller.getApplicationContext(), regid);
		  } catch (IOException ex) {
		        msg = "Error :" + ex.getMessage();
		  }
		  return msg;
		}
	}.execute(null, null, null);
  }
	  
  private void sendRegistrationIdToBackend() {
  }
	    
  private void storeRegistrationId(Context context, String regId) {
	SharedPreferences prefs = getGCMPreferences(context);
	int appVersion = getAppVersion(context);
	Log.i(TAG, "Saving regId on app version " + appVersion);
	SharedPreferences.Editor editor = prefs.edit();
	editor.putString("registration_id", regId);
	editor.putInt("appVersion", appVersion);
	editor.commit();
  }

}
