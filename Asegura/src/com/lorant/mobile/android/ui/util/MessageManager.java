package com.lorant.mobile.android.ui.util;

import com.lorant.mobile.android.ui.AbstractUI;

import android.view.Gravity;
import android.widget.Toast;

public class MessageManager { 
  
  public static void show(Integer resId, AbstractUI ctx, Object ... var){
	show(ctx.getResources().getString(resId), ctx, var);
  }
	  
  public static void show(String message, AbstractUI ctx, Object ... var){
	Toast toast = Toast.makeText(ctx, String.format(message, var)
			                              , Toast.LENGTH_SHORT);
	toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
	toast.show();
  }

}
