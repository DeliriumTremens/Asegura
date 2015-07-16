package com.lorant.mobile.android.ui.view;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.core.AlertDAO;
import com.lorant.mobile.android.db.dao.core.ConfigDAO;
import com.lorant.mobile.android.db.dao.core.NotificationDAO;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.util.GoogleCalendar;
import com.lorant.mobile.android.util.JsonUtil;
import com.lorant.mobile.android.util.Utilities;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;
import com.lorant.mobile.android.vo.bean.Alert;
import com.lorant.mobile.android.vo.bean.AppConfig;
import com.lorant.mobile.android.vo.bean.Notification;
import com.lorant.mobile.android.vo.bean.Policy;
import com.lorant.mobile.android.vo.bean.Verification;

public class CalendarTracker extends AbstractUI{

  protected ListView lvEventContainer = null;
  private List<Policy> policies = new ArrayList<Policy>();
  private SelectorAdapter adapter = null;
  private Switch swRemember = null;
  private AppConfig cfgRemember = null;
  private List<Verification> verifications = new ArrayList<Verification>();
	 
  public CalendarTracker(){
	super(R.layout.calendar);
  }
  
  @Override
  public void init(){
	adapter = new SelectorAdapter(this);
	cfgRemember = new ConfigDAO(this).find(Config.REMEMBER_VERIFICATION_PARAM_NAME);
  }
  
  @Override
  public void bind() {
	lvEventContainer = (ListView) findViewById(R.id.lvEventContainer);
	swRemember = (Switch) findViewById(R.id.swRemember);
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.calendar);
	callWSGetPolicyList();
	swRemember.setChecked((cfgRemember != null) && (Utilities
			      .stringToBoolean(cfgRemember.getValue())));
  }
  
  @Override
  public void setBehavior() {
	lvEventContainer.setAdapter(adapter);
	swRemember.setOnCheckedChangeListener(new OnCheckedChangeListener(){
	  @Override
	  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		GoogleCalendar googleCalendar = new GoogleCalendar(ctx);
		List<GregorianCalendar> rememberDates = null;
		List<Alert> alerts = null;
		AlertDAO alertDAO = new AlertDAO(ctx);
		Long eventId = 0L;
		if(isChecked){
		  for(Policy policy : policies){
			rememberDates = Utilities.getRememberDatesForVerification((Verification) 
					                                              policy.getTag());
			for(GregorianCalendar date : rememberDates){
			  eventId = googleCalendar.addEvent(policy.getName(), ctx.getResources()
					  .getString(R.string.verification), date, true, "FREQ=YEARLY");	
			  alertDAO.insert(new Alert(policy.getId(), eventId, Config.ALERT_TYPES
					                                  .VERIFICATION.getId()));
				
			}
		  }
		  updateLocalReminders();
		} else{
			alerts = alertDAO.findByType(Config.ALERT_TYPES.VERIFICATION.getId());
			for(Alert alert : alerts){
			  googleCalendar.deleteEvent(alert.getEventId());
			  alertDAO.delete(alert.getId());
			}
		}
		commit(isChecked);
	  }
	});
  }
  
  private void commit(Boolean value){
	 if(cfgRemember == null){
		 cfgRemember = new AppConfig(Config.REMEMBER_VERIFICATION_PARAM_NAME
				                                   , String.valueOf(value));
	 }
	 cfgRemember.setValue(String.valueOf(value));
	 new ConfigDAO(ctx).upsert(cfgRemember);
  }
  
  private void setBehavior(View feed, Policy policy){
	TextView tvPlates = (TextView) feed.findViewById(R.id.tvPlates);
	Verification verification = null;
	LinearLayout llStickerColor = (LinearLayout) feed.findViewById(R.id.llStickerColor);
	TextView tvStartMonths = (TextView) feed.findViewById(R.id.tvStartMonths);
	TextView tvFinalMonths = (TextView) feed.findViewById(R.id.tvFinalMonths);
	String plates = policy.getPlates();
	Integer digit = null;
	Pattern p = Pattern.compile("\\d");
	Matcher m = p.matcher(plates); 
	while (m.find()) {
	  digit = Integer.valueOf(m.group());
	}
	if(digit != null){
	  verification = Config.VERIFICATION.get(digit);
	  verification.setName(policy.getName());
	  llStickerColor.setBackgroundColor(verification.getColor());
	  tvStartMonths.setText(Utilities.getMonth(verification.getFirstPeriodMonth()) 
			 + " y " + Utilities.getMonth(verification.getFirstPeriodMonth() + 1));
	  tvFinalMonths.setText(Utilities.getMonth(verification.getSecondPeriodMonth()) 
				 + " y " + Utilities.getMonth(verification.getSecondPeriodMonth() + 1));
	  tvPlates.setText(plates);
	  policy.setTag(verification);
	  verification.setTag(policy);
	  verifications.add(verification);
	}
	
  }
  
  private void callWSGetPolicyList(){
	RequestParams params = new RequestParams(); 
	params.add("nickName", getStringParam(Config.PREF_USERNAME));
	RestClient.get("getInsuranceListWS", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONArray response) throws JSONException {
		List<Policy> carPolicies = new ArrayList<Policy>();
		policies = JsonUtil.getPolicyList(response);
		for(Policy policy : policies){
		  if(policy.getGroupId() == 1){
			 carPolicies.add(policy);
		  }
		}
		policies.clear();
		policies.addAll(carPolicies);
		adapter.notifyDataSetChanged();
	  }    
	});
  }
  
  class SelectorAdapter extends ArrayAdapter <Policy> {
	private Activity context;
				    
	private SelectorAdapter(Activity context) {
	  super(context, R.layout.calendar_event_ft, policies);
	  this.context = context;
	}
							
	public View getView(int position, View convertView, ViewGroup parent) {
	  Policy policy = policies.get(position);
	  if(convertView == null){
		convertView = context.getLayoutInflater().inflate(R.layout
						                .calendar_event_ft, null);
	  }
	  setBehavior(convertView, policy);
	  return convertView;
    }
	
	@Override
	public int getCount() {
	   return policies.size();
	}
  }
  
  private void updateLocalReminders(){
	NotificationDAO notificationDAO = new NotificationDAO(this);
	Notification notification = null;
	Policy policy = null;
	notificationDAO.deleteForType(Config.ALERT_TYPES.VERIFICATION.getId());
	if(swRemember.isChecked()){
	  for(Verification verification: verifications){
		notification = new Notification();
		policy = (Policy) verification.getTag(); 
		notification.setName(getResources().getString(R.string
					   .policyPaymentDescription, policy.getInsuranceNumber()));
	    notification.setMonth(verification.getFirstPeriodMonth());
	    notification.setDay(1);
	    notification.setPolicyId(policy.getId());
	    notification.setType(Config.ALERT_TYPES.PAYMENT.getId());
	    notificationDAO.insert(notification);
	    notification.setId(null);
	    notification.setMonth(verification.getSecondPeriodMonth());
	    notificationDAO.insert(notification);
	  }	 
		
	}
  }

  
}
