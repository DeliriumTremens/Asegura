package com.lorant.mobile.android.ui.view;

import java.security.MessageDigest;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.ui.AbstractUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lorant.mobile.android.constant.Config;

public class Launcher extends AbstractUI {

  protected static final String TAG = "LorantAsegura";
	
  public Launcher(){
	super(R.layout.launcher);
  }
  
  @Override
  public void setBehavior(){
	final Bitmap bm = BitmapFactory.decodeResource(getResources() , R.drawable
                                                                     .splash);
    final Activity caller = this;
    ImageView imageView = (ImageView) rootView.findViewById(R.id.imageSplash);
    imageView.setImageBitmap(bm);
    imageView.setScaleType(ScaleType.FIT_XY);
    showHashKey(this);
    Thread splashTread = new Thread() {
      public void run() {
        try {
            sleep(Config.SPLASH_PERIOD);
            startActivity(new Intent(caller.getApplicationContext(), Login.class)); 
        } catch(Exception e) {
        Log.e(TAG, "Inicialization failed: " + e);
        } 
      } 
    };
    splashTread.start(); 
  }
  
  private void showHashKey(Context context) {
      try {
          PackageInfo info = context.getPackageManager().getPackageInfo(
                  "com.lorant.mobile.android", PackageManager.GET_SIGNATURES); 
          for (Signature signature : info.signatures) {
              MessageDigest md = MessageDigest.getInstance("SHA");
              md.update(signature.toByteArray());
              Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
              }
      } catch (Exception e) {
      } 
  }
}
