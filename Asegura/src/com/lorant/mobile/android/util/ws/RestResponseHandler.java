package com.lorant.mobile.android.util.ws;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;

public abstract class RestResponseHandler extends JsonHttpResponseHandler {
	
  private AbstractUI ctx = null;
  private boolean showLoader = false;
  private View loader = null;
  private boolean validateErrorCode = true;
	
  public RestResponseHandler (AbstractUI ctx, boolean showLoader){
	this(ctx);
	this.showLoader = showLoader;
  }
  
  public RestResponseHandler (AbstractUI ctx, boolean showLoader
		                          , boolean validateErrorCode) {
	this(ctx);
	this.showLoader = showLoader;
	this.validateErrorCode = validateErrorCode;
  }
  
  public RestResponseHandler (AbstractUI ctx){
	this.ctx = ctx;
	loader = ctx.getLoader();
  }
	
  public void onSuccess(JSONObject response)  throws JSONException{}
  public void onSuccess(JSONArray response)  throws JSONException{}
  
  
  @Override
  public final void onStart(){
	if(showLoader){
	  showLoader();  
	}
  }
  
  @Override
  final public void onSuccess(int statusCode, Header[] headers
		                              , JSONObject response) {
	String code = null;
	try {
		 try{
		     code = response.getString("ErrorCode");
		 } catch(JSONException ignored){}
		 if((! validateErrorCode) 
				      || (code == null) || (code.equals(Config.WS_OK_CODE))){
		   onSuccess(response); 
		 } else {
			 MessageManager.show(response.getString("ErrorMessage"), ctx);
		 }
	} catch (Exception e) {
		e.printStackTrace();
		MessageManager.show(R.string.errUnexpected, ctx);
	} finally{
		hideLoader();
	}
  }
  
  @Override
  public final void onSuccess(int statusCode, Header[] headers
		                         , JSONArray response) {
	try{
		onSuccess(response);
	} catch(Exception e) {
		e.printStackTrace();
		MessageManager.show(R.string.errUnexpected, ctx);
	} finally{
		hideLoader();
	}
  }

  @Override
  public final void onFailure(int statusCode, Header[] headers
		       , String responseString, Throwable throwable) {
	try{
		MessageManager.show(R.string.errCommunications, ctx, statusCode);
	} finally{
		hideLoader();
	}
  }  
  
  @Override
  public final void onFailure(int statusCode, Header[] headers
		                 , java.lang.Throwable throwable, JSONObject response) {
	try{
	    if(response != null){
	    	MessageManager.show(response.toString(),ctx , statusCode);
	    } else {
	    	MessageManager.show(R.string.errNull, ctx);
        }
	} finally{
		hideLoader();
	}
  }
  
  @Override
  public final void onFailure(int statusCode, Header[] headers
		                 , java.lang.Throwable throwable, JSONArray  response) {
    try{
	  if(response != null){
		  MessageManager.show(response.toString(), ctx, statusCode);
	  } else {
		MessageManager.show(R.string.errNull, ctx);
	  }
    } finally{
	  hideLoader();
    }
  }
  
  private void hideLoader(){
	if((loader != null)){
	  loader.setVisibility(View.GONE);
	}
  }
  
  private void showLoader(){
	if((loader != null)){
	  loader.setVisibility(View.VISIBLE);
	} 
  }

}
