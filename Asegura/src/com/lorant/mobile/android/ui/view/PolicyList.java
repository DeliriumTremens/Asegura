package com.lorant.mobile.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.core.PolicyDAO;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;
import com.lorant.mobile.android.util.JsonUtil;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;
import com.lorant.mobile.android.vo.bean.InsuranceGroup;
import com.lorant.mobile.android.vo.bean.Policy;

public class PolicyList extends AbstractUI {

  private List<Policy> policies = new ArrayList<Policy>();
  private ListView lvPolicies = null;
  private SelectorAdapter adapter = null;
  
  
  public PolicyList(){
	super(R.layout.assurance_list);
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.myAssurance);
	setActionButtonBackground(R.drawable.common_button_add);
  }
  
  @Override
  public void init(){
	adapter = new SelectorAdapter(this);
  }
  
  @Override
  public void onBackPressed() {
	startActivity(new Intent(this, Menu.class));
  }
  
  @Override
  public void bind(){
	lvPolicies = (ListView) rootView.findViewById(R.id.lvPolicies);
  }
  
  @Override
  public void setBehavior(){
	  lvPolicies.setAdapter(adapter);
	  lvPolicies.setOnItemClickListener(new OnItemClickListener(){
	  @Override
	  public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
        Policy policy = policies.get(position);
        Intent toNewAssurance = new Intent(ctx, PolicyDetail.class);
		toNewAssurance.putExtra("groupId", policy.getGroupId());
		toNewAssurance.putExtra("policyNumber", policy.getInsuranceNumber());
		toNewAssurance.putExtra("serialNumber",policy.getSerialNumber());
		toNewAssurance.putExtra("forNew", false);
		startActivity(toNewAssurance);
	  }
	});
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	callWSGetPolicyList();
  }
  
  @Override
  public void onStart(){
	super.onStart();
	Integer messageResource = null;
	if((extras != null) && ((messageResource = extras.getInt("message")) != null) 
			                                           && messageResource > 0) {
	  MessageManager.show(messageResource, this);
	  extras.remove("message");
	}
  }
  
  @Override
  public void onActionButtonClickEvent(View v) {
	startActivity(new Intent(this, PolicyMenu.class));
  }

  private class SelectorAdapter extends ArrayAdapter<Policy> {
	private Context mContext;

	private SelectorAdapter(Activity context) {
	  super(context, R.layout.assurance_row, policies);
	  this.mContext = context;
	}
	
	@Override
	public long getItemId(int position) {
	  return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  LinearLayout ll = (LinearLayout)convertView;
	  TextView tvAlias = null;
	  TextView tvEndDate = null;
	  ImageView ivType = null;
	  TextView tvInsurance = null;
	  ImageButton ibTrash = null;
	  final Policy policy = policies.get(position);
	  if(ll == null) {
		 ll = (LinearLayout) LayoutInflater.from(mContext).inflate( R.layout
		    		                          .assurance_row, parent, false);
	  }
	  tvAlias = (TextView) ll.findViewById(R.id.tvAlias);
	  tvEndDate = (TextView) ll.findViewById(R.id.tvEndDate);
	  ivType = (ImageView) ll.findViewById(R.id.ivType);
	  tvInsurance = (TextView) ll.findViewById(R.id.tvInsurance);
	  ibTrash = (ImageButton) ll.findViewById(R.id.ibTrash);
	  ivType.setBackgroundResource(Config.INSURANCE_GROUP_MAPPING.get(Config
			      .INSURANCE_GROUP_MAPPING.indexOf(new InsuranceGroup(policy
					                      .getGroupId()))).getResourceId());
      tvAlias.setText(policy.getName());
      tvEndDate.setText(Config.DATE_FORMAT_ALT.format(policy.getEndDate()));
      tvInsurance.setText(policy.getCompanyName());
      ibTrash.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			showDeleteDialog(policy);
		}
      });
	  return ll;
	}
  }
  
  private void callWSGetPolicyList(){
	RequestParams params = new RequestParams(); 
	params.add("nickName", getStringParam(Config.PREF_USERNAME));
	RestClient.get("getInsuranceListWS", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONArray response) throws JSONException {
		policies.addAll(JsonUtil.getPolicyList(response));
		adapter.notifyDataSetChanged();
	  }    
	});
  }
  
  private void callWSDeletePolicy(final Policy policy){
	RequestParams params = new RequestParams(); 
	params.put("nickName", getStringParam(Config.PREF_USERNAME));
	params.put("insuranceNumber",policy.getInsuranceNumber());
	RestClient.get("deleteInsuranceWS", params, new RestResponseHandler(this, false) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		System.out.println("Policy deleted => " + policy.getInsuranceNumber());
		new PolicyDAO(ctx).delete(policy.getId());
		startActivity(new Intent(ctx, PolicyList.class));
	  }    
	});
  }
  
  private void showDeleteDialog(final Policy policy){
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	alertDialogBuilder.setTitle(R.string.message);
	alertDialogBuilder.setMessage(R.string.policyDelete);
	alertDialogBuilder.setPositiveButton(R.string.yes
		     , new DialogInterface.OnClickListener(){
	  @Override
      public void onClick(DialogInterface dialog, int which) {
	    callWSDeletePolicy(policy);
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
