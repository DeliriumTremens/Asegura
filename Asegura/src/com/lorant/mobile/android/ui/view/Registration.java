package com.lorant.mobile.android.ui.view;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;
import com.lorant.mobile.android.util.Utilities;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class Registration extends AbstractUI {
	
  private EditText etName = null;
  private EditText etLastName = null;
  private EditText etMiddleName = null;
  private EditText etMail = null;
  private EditText etPhone = null;
  private EditText etPassword = null;
  private EditText etRepeatPassword = null;
	
  public Registration(){
	super(R.layout.registration);
  }
  
  @Override
  public void setBehavior(){
	setFormBehavior((RelativeLayout) rootView);
  }
	  
 
  @Override
  public void bind(){
	etName = ((EditText) rootView.findViewById(R.id.etName));
	etLastName = (EditText) rootView.findViewById(R.id.etLastName);
	etMiddleName = (EditText) rootView.findViewById(R.id.etMiddleName);
	etMail = (EditText) rootView.findViewById(R.id.etMail);
	etPhone = (EditText) rootView.findViewById(R.id.etPhone);
	etPassword = (EditText) rootView.findViewById(R.id.etPassword);
	etRepeatPassword = (EditText) rootView.findViewById(R.id.etRepeatPassword);
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.register);
	if(extras != null){
	  etName.setText(extras.getString("name"));
	  etLastName.setText(extras.getString("lastName"));
	  etMiddleName.setText(extras.getString("middleName"));
	  etMail.setText(extras.getString("mail"));
	}
  }
  
  @Override
  protected int validateForm(){
	int errMessageId = 0;
	if(etName.getText().toString().isEmpty()){
		errMessageId = R.string.errNameRequired;
	} else if(etLastName.getText().toString().isEmpty()){
		errMessageId = R.string.errLastNameRequired;
	} else if(etLastName.getText().toString().isEmpty()){
		errMessageId = R.string.errLastNameRequired;
	} else if(etMiddleName.getText().toString().isEmpty()){
		errMessageId = R.string.errMiddleNameRequired;
	} else if(etMail.getText().toString().isEmpty()){
		errMessageId = R.string.errMailRequired;
	} else if(! Utilities.isValidEmail(etMail.getText().toString())){
		errMessageId = R.string.errMailWrong;
	} else if(etPhone.getText().toString().isEmpty()){
		errMessageId = R.string.errPhoneRequired;
	} else if( ! Utilities.isValidPhone(etPhone.getText().toString())){
		errMessageId = R.string.errPhoneWrong;
	} else if(etPassword.getText().toString().isEmpty()){
		errMessageId = R.string.errPasswordRequired;
	} else if(etRepeatPassword.getText().toString().isEmpty()){
		errMessageId = R.string.errRepeatPasswordRequired;
	} else if( ! etPassword.getText().toString().equals(etRepeatPassword
			                                    .getText().toString())){
		errMessageId = R.string.errPasswordIncorrect;
	} else if(etPassword.getText().toString().length() < 8){
		errMessageId = R.string.errRepeatPasswordShort;
	}
	return errMessageId;
  }
  
  public void onClickBnSaveRegistration(View clickedView){
	int errMessageId = 0;
	if((errMessageId = validateForm()) > 0){
		MessageManager.show(errMessageId, this);
		return;
	}
	callWSSaveRegistration();
  }
  
  private void callWSSaveRegistration(){
	RequestParams params = new RequestParams();
	params.add("name", etName.getText().toString());
	params.add("apaterno", etLastName.getText().toString());
	params.add("amaterno", etMiddleName.getText().toString());
	params.add("email", etMail.getText().toString());
	params.add("tel", etPhone.getText().toString());
	params.add("password", etPassword.getText().toString());
	RestClient.get("addUsuario", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		setStringParam(Config.PREF_USERNAME, etMail.getText().toString());
		setIntParam(Config.PREF_USER_ID, response.getInt("userId"));
		setStringParam(Config.PREF_PHONE, etPhone.getText().toString()); 
		ctx.startActivity(new Intent(ctx, Menu.class));  
	  }     
	});
  }

}
