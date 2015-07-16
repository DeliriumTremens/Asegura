package com.lorant.mobile.android.ui.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;
import com.lorant.mobile.android.util.Utilities;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;


public class Login extends AbstractUI 
                   implements Session.StatusCallback, OnErrorListener{
	
  private EditText etUserName = null;
  private EditText etPassword = null;
  private LoginButton authButton = null;
	
  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
  }
  
  @Override
  public void onStart() {
	super.onStart();
	if(getStringParam(Config.PREF_USERNAME) != null){
	  ctx.startActivity(new Intent(ctx, Menu.class)); 
	}
  }
  
  public Login(){
	super(R.layout.login);
  }
  
  @Override
  public void bind(){
	authButton = (LoginButton) rootView.findViewById(R.id.bnFbRegistration);
	etUserName = (EditText) rootView.findViewById(R.id.etUserName);
	etPassword = (EditText) rootView.findViewById(R.id.etPassword);
  }
  
  @Override
  public void setBehavior(){
	authButton.setOnErrorListener(this);
	authButton.setReadPermissions(Config.FB_PERMISSIONS);
	authButton.setSessionStatusCallback(this);
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(this, requestCode, resultCode
    		                                                         , data);
  }
  
  public void onClickBnLogin(View clickedView){
	int errMessageId = 0;
	if((errMessageId = validateForm()) > 0){
		MessageManager.show(errMessageId, this);
		return;
	}
	callWSLogin();
  }
  
  public void onClickBnRegister(View clickedView){
	startActivity(new Intent(this, Registration.class)); 
  }
  
  public void onClickBnForgotPassword(View clickedView){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	View dialogView = getLayoutInflater().inflate(R.layout.login_forgot_pass_ft
			                                                           , null);
	final EditText etDialogMail = (EditText) dialogView.findViewById(R.id
			                                              .etDialogMail);
	builder.setView(dialogView);
	etDialogMail.setText(etUserName.getText().toString());
	builder.setPositiveButton(R.string.accept, new OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(! Utilities.isValidEmail(etDialogMail.getText().toString())){
				MessageManager.show(R.string.errMailWrong, (AbstractUI) ctx);
			} else {
				callWSForgotPassword(etDialogMail.getText().toString());
				dialog.cancel();
			}
			
		}
	});
	builder.show();
  }
  
  @Override
  protected int validateForm(){
	int errMessageId = 0;
	if(etUserName.getText().toString().isEmpty()){
		errMessageId = R.string.errMailRequired;
	} else if(! Utilities.isValidEmail(etUserName.getText().toString())){
		errMessageId = R.string.errMailWrong;
	} else if(etPassword.getText().toString().isEmpty()){
		errMessageId = R.string.errPasswordRequired;
	}
//	} else if(etPassword.getText().toString().length() < 8){
//		errMessageId = R.string.errRepeatPasswordShort;
//	}
	return errMessageId;
  }
  
  private void callWSForgotPassword(String nickName){
	RequestParams params = new RequestParams(); 
	params.add("nickName", nickName);
	RestClient.get("retrievePassword", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		MessageManager.show(ctx.getResources().getString(R.string.retrievePassOK)
				                                           , (AbstractUI) ctx); 
	  }    
	});
  }
  
  private void callWSLogin(){
	RequestParams params = new RequestParams(); 
	params.add("nickName", etUserName.getText().toString());
	params.add("password", etPassword.getText().toString());
	RestClient.get("login", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		setStringParam(Config.PREF_USERNAME, etUserName.getText().toString());
		//setIntParam(Config.PREF_USER_ID, response.getInt("idUser"));
		setStringParam(Config.PREF_PHONE, response.getString("noTelefono")); 
	    ctx.startActivity(new Intent(ctx, Menu.class)); 
	  }    
	});
  }
  
  @Override
  public void call(Session session, SessionState state, Exception exception) {
	if(session.isOpened()) {
	  Log.i(TAG,"Access Token"+ session.getAccessToken());	  
	  Request.newMeRequest(session, new Request.GraphUserCallback() {
		  @Override
		  public void onCompleted(GraphUser user, Response response) {
			Intent intent = new Intent(ctx, Registration.class);
			Bundle extras = new Bundle();
		    if (user != null) {
		      extras.putString("name", user.getFirstName());
		      extras.putString("lastName", user.getLastName());
		      extras.putString("middleName", user.getMiddleName());
		      extras.putString("mail", user.getProperty("email").toString());
		      intent.putExtras(extras);
		      ctx.startActivity(intent); 
		    } else {
		    	MessageManager.show(ctx.getResources().getString(R.string
   			                     .errFBNotAvailable), (AbstractUI)ctx);
            }
		  }
		}).executeAsync();
	  session.closeAndClearTokenInformation();
	}
  }

  @Override
  public void onError(FacebookException error) {
	  MessageManager.show(error.getMessage(), this);
  }
    
}
