package com.lorant.mobile.android.ui.view;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;

public class PolicyMenu extends AbstractUI {

  private int groupId = 0;
  private EditText etPolicyNumber = null;
  private EditText etSerialNumber = null;
  
  public PolicyMenu(){
	super(R.layout.assurance_menu);
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.addAssurance);
  }

  public void onClickNewAssurance(View clickedView){
	showPolicyNumberInput(clickedView);
  }
  
  public void onClickBnTakeQR(View clickedView){
	Intent intent = null;
	Uri marketUri = null;
	try {
		 intent = new Intent(Config.QR_PACKAGE);
		 intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		 startActivityForResult(intent, Config.ACTIVITY_QR_CB);
    } catch (Exception e) {
		 marketUri = Uri.parse(Config.QR_APP_MARKET);
		 intent = new Intent(Intent.ACTION_VIEW,marketUri);
		 startActivity(intent);
	}
  }
  
  @Override
  public void onBackPressed() {
	startActivity(new Intent(this, PolicyList.class));
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
  }
  
  public void showPolicyNumberInput(final View clickedView){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	View dialogView = getLayoutInflater().inflate(R.layout
			        .assurance_input_number_ft, null);
	groupId = Config.GROUP_MAPPING.get(clickedView.getTag().toString());
	etPolicyNumber = (EditText) dialogView.findViewById(R.id.etPolicyNumber);
	etSerialNumber = (EditText) dialogView.findViewById(R.id.etSerialNumber);
	builder.setTitle(R.string.policySearch);
	if(groupId != 1){
	  etSerialNumber.setVisibility(View.GONE);
	} 
	builder.setView(dialogView);
	builder.setPositiveButton(R.string.send, new OnClickListener(){
	  @Override
	  public void onClick(DialogInterface dialog, int which) {
		if(etPolicyNumber.getText().toString().isEmpty()){
		  MessageManager.show(R.string.errPolicyNumberRequired, (AbstractUI) ctx);
		} else {
		    callSearchInsuranceService();
		    dialog.dismiss();
		}	
	  }
	});
	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
      @Override
      public void onClick(DialogInterface dialog, int which) {
	   dialog.dismiss();
     }
    });
	builder.show();
  }
  
  private void callSearchInsuranceService(){
	RequestParams params = new RequestParams();
	params.put("insuranceNumber", etPolicyNumber.getText().toString()
				                  .toUpperCase(Locale.getDefault()));
	params.put("serialNumberSuffix", etSerialNumber.getText().toString()
				                     .toUpperCase(Locale.getDefault()));
	params.put("_iIdRamo", groupId);
	RestClient.post("searchInsurance", params, new RestResponseHandler(this
				                                           , true, false) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		String errorCode = response.getString(Config.WS_CODE_NAME);
		Intent intent = null;
		if(errorCode.equals("ER0007")){
		  intent = new Intent(ctx, PolicyList.class);
		  intent.putExtra("message", R.string.policyAreadyExists);
		  startActivity(intent);
		} else if(errorCode.equals("ER0001")){
			intent = new Intent(ctx, PolicyDetail.class);
			intent.putExtra("groupId", groupId);
			intent.putExtra("policyNumber", etPolicyNumber.getText().toString());
			intent.putExtra("serialNumber", etSerialNumber.getText()
	                                                       .toString());
		    intent.putExtra("forNew", true);
			startActivity(intent);
		} else {
			showDialog();
		} 
	  }
	});  	
  }
  
  private void showDialog(){
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	alertDialogBuilder.setTitle(R.string.message);
	alertDialogBuilder.setMessage(R.string.policyNotFound);
	alertDialogBuilder.setPositiveButton(R.string.yes
			    , new DialogInterface.OnClickListener(){
      @Override
	  public void onClick(DialogInterface dialog, int which) {
    	Intent toNewAssurance = new Intent(ctx, PolicyDetail.class);
		toNewAssurance.putExtra("groupId", groupId);
		toNewAssurance.putExtra("policyNumber", etPolicyNumber.getText()
						                                   .toString());
		toNewAssurance.putExtra("serialNumber", etSerialNumber.getText()
                                                           .toString());
		toNewAssurance.putExtra("forNew", true);
		startActivity(toNewAssurance);
		dialog.cancel();
	  }
	});
	alertDialogBuilder.setNegativeButton(R.string.no
			    , new DialogInterface.OnClickListener(){
	  @Override
	  public void onClick(DialogInterface dialog, int which) {
		 dialog.dismiss();
	  }
    });
	alertDialogBuilder.show();
  }
  
}
