package com.lorant.mobile.android.ui.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;
import com.lorant.mobile.android.util.ImageUtils;
import com.lorant.mobile.android.util.JsonUtil;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;
import com.lorant.mobile.android.vo.bean.EventType;
import com.lorant.mobile.android.vo.bean.Historical;
import com.lorant.mobile.android.vo.bean.Policy;
import com.lorant.mobile.android.vo.bean.Report;

public class ReportDetail extends AbstractUI 
                    implements GoogleMap.OnMyLocationChangeListener {

  private MapFragment mapFragment = null;
  private GoogleMap googleMap = null;
  private ArrayList<Policy> policies = null;
  private ArrayList<EventType> eventTypes = new ArrayList<EventType>();
  
  private Spinner srPolicyNick = null;
  private EditText etPhone = null;
  private EditText etInformation = null;
  private Spinner spCause = null;
  private Uri imageUri = null;
  private Report report = null;
  private LatLng latLng = null;
  private boolean sent = false;
  
  private ArrayAdapter<EventType> eventTypeAdapter = null;
  
  private Policy activePolicy = null;
  
  
  public ReportDetail(){
	super(R.layout.report_detail);
  }
	  
  @Override
  public void populate(){
	setActionBarTitle(R.string.report);
	setActionButtonBackground(R.drawable.report_historical);
	callWSGetPolicyList();
	eventTypes.add(0, new EventType(-1, "SELECCIONE"));
	eventTypeAdapter.notifyDataSetChanged();
  }
  
  @Override
  public void onBackPressed() {
	startActivity(new Intent(this, Menu.class));
  }
  
  @Override
  public void onActionButtonClickEvent(View v) {
	startActivity(new Intent(this, ReportHistorical.class));
  }
  
  
  @Override
  public void bind(){
	srPolicyNick = (Spinner) rootView.findViewById(R.id.srPolicyNick);
	etPhone = (EditText) rootView.findViewById(R.id.etPhone);
	etInformation = (EditText) rootView.findViewById(R.id.etInformation);
	spCause = (Spinner) rootView.findViewById(R.id.spCause);
  }
	  
  @Override
  public void init(){
	mapFragment = MapFragment.newInstance();
	FragmentTransaction fragmentTransaction =getFragmentManager()
			                                 .beginTransaction();
	fragmentTransaction.add(R.id.mapContainer, mapFragment);
	fragmentTransaction.commit();
	if(extras!= null){
	  report = (Report) extras.get("report");
	}
	eventTypeAdapter =new ArrayAdapter<EventType>(ctx, android.R.layout
	                                .simple_spinner_item, eventTypes);
  }
  
  @Override
  public void setBehavior(){
	setFormBehavior((RelativeLayout) rootView);
	srPolicyNick.setOnItemSelectedListener(new OnItemSelectedListener(){
	  @Override
	  public void onItemSelected(AdapterView<?> parent, View view,
				                         int position, long id) {
		if(position > 0){
		  activePolicy = policies.get(position);
		  callWSGetPolicyDetail();
		  callWSGetEventTypes();
		  sent = false;
		} else {
			activePolicy = null;
		}
	  }
	  @Override
	  public void onNothingSelected(AdapterView<?> parent) {
	  }
		
	});
	eventTypeAdapter.setDropDownViewResource(android.R.layout
                               .simple_spinner_dropdown_item);
    spCause.setAdapter(eventTypeAdapter);
  }
  
  public void onStart() {
    super.onStart();
    int psStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
    if(psStatusCode == ConnectionResult.SUCCESS){
	  googleMap= mapFragment.getMap();
	  googleMap.setMyLocationEnabled(true);
	  googleMap.setOnMyLocationChangeListener(this);
    } else{
    	GooglePlayServicesUtil.getErrorDialog(2, this,1).show();
    }
    etPhone.setText(getStringParam(Config.PREF_PHONE));
  }
  
  public void onClickBnSendLocation(View clickedView){
	  
  }
  
  public void onClickBnShare(View clickedView){
		Intent share = null;
		String imagePath = null;
		File imageFileToShare = null;
		Uri uri = null;
		if(imageUri == null){
		  MessageManager.show(R.string.errSelectAnImage, this);
		  return;
		}
		share = new Intent(Intent.ACTION_SEND);
		imagePath = ImageUtils.getRealPathFromURI(imageUri, ctx);
		imageFileToShare = new File(imagePath);
		uri = Uri.fromFile(imageFileToShare);
		share.setType("image/*");
		share.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(share, getResources().getString(R.string
				                                                         .share)));
	  }
  
  @Override
  public void onMyLocationChange (Location location) {
    latLng = new LatLng(location.getLatitude(), location.getLongitude());
    Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
    EditText etAddress = (EditText) findViewById(R.id.etAddres);
    List<Address> addresses = null;
    Address address = null;
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    try {
		 addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
		 if(addresses.size() > 0) {
		   address = addresses.get(0);
		   etAddress.setText(address.getThoroughfare() + " #"+ address
				    .getFeatureName() +  ", " + this.getResources().getString(R
				    	  .string.poblation) + ". " + address.getSubLocality());
	     }
	} catch (IOException e) {
		e.printStackTrace();
		return;
	}
  }
  
  public void onClickBnTakePhoto(View clickedView) {
	 showCaptureImageDialog();
  }
  
  public void onClickBnCall(View clickedView) {
	if(sent){
	  callWSGetHistorical();
	} else {
      callWSSendReport();
	}
  }
  
  
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	Bitmap bp = null;
	ByteArrayOutputStream bytes = null;
	File destination = null;
	FileOutputStream fo = null;
	if(resultCode == RESULT_OK){ 
      if((imageUri = data.getData()) == null){
       	 bp = (Bitmap) data.getExtras().get("data");
       	 bytes = new ByteArrayOutputStream();
       	 bp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
       	 destination = new File(Config.IMG_LOCAL_PATH, System
       			              .currentTimeMillis() + ".jpg");
       	 try {
              destination.createNewFile();
              fo = new FileOutputStream(destination);
              fo.write(bytes.toByteArray());
              fo.close();
         } catch (Exception e) {
               e.printStackTrace();
         }
         imageUri = Uri.fromFile(destination);
      } 
	} 
  }
  
  public void onClickEtInformation(View clickedView){
	 Intent toNotes = new Intent(this, ReportNotes.class);
	 report = new Report();
	 if(spCause.getSelectedItem() != null){
	   report.setCauseId(((EventType)spCause.getSelectedItem()).getId());
	 }
	 report.setImageUri(imageUri);
	 report.setInformation(etInformation.getText().toString());
	 report.setPhone(etPhone.getText().toString());
	 if(srPolicyNick.getSelectedItem() != null){
	   report.setPolicyId(((Policy) srPolicyNick.getSelectedItem()).getId());
	 }
	 toNotes.putExtra("report", report);
	 startActivity(toNotes);
  }
  
  private void callWSGetPolicyList(){
	RequestParams params = new RequestParams(); 
	params.add("nickName", getStringParam(Config.PREF_USERNAME));
	RestClient.get("getInsuranceListWS", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONArray response) throws JSONException {
		ArrayAdapter<Policy> dataAdapter = null;
		policies = JsonUtil.getPolicyList(response);
		policies.add(0, new Policy(-1, "SELECCIONE"));
		dataAdapter = new ArrayAdapter<Policy>(ctx, android.R.layout
                                    .simple_spinner_item, policies);
        dataAdapter.setDropDownViewResource(android.R.layout
                             .simple_spinner_dropdown_item);
        srPolicyNick.setAdapter(dataAdapter);
        populateFromExtra();
	  }    
	});
  }
  
  private void populateFromExtra(){
	if(report != null){
      for(int i = 0; i < policies.size();i++){
    	if(policies.get(i).getId().equals(report.getPolicyId())){
    	  srPolicyNick.setSelection(i);
    	  break;
    	}
       }
      etPhone.setText(report.getPhone());
      etInformation.setText(report.getInformation());
      imageUri = report.getImageUri();
    } 
  }
  
  private void callWSSendReport(){
    RequestParams params = new RequestParams(); 
	params.put("IdPolizaM", ((Policy) srPolicyNick.getSelectedItem()).getId());
	params.put("idSiniestro", ((EventType)spCause.getSelectedItem()).getId());
	params.put("numeroTel", etPhone.getText().toString());
	params.put("latitude", latLng.latitude);
	params.put("longitud", latLng.longitude);
	params.put("idUsuario", getStringParam(Config.PREF_USERNAME));
	params.put("informacion", etInformation.getText().toString());
	RestClient.get("ms_RegistroSiniestro", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		sent = true;
		if(activePolicy != null){
			startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" 
				                       + activePolicy.getAgentPhone())));
		}
	  }    
	});
  }
  
  private void callWSGetHistorical(){
	RequestParams params = new RequestParams();
	params.add("nickName", getStringParam(Config.PREF_USERNAME));
	RestClient.get("getRecuperaSiniestro", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONArray response) throws JSONException {
		List<Historical> items = JsonUtil.getHistorical(response);
		for(Historical item : items){
		  if(item.getPolicyId().equals(activePolicy.getId())){
			try{
				Long.parseLong(item.getPhone());
				startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" 
				                                       + item.getPhone())));
			} catch(NumberFormatException ignored){
				MessageManager.show(R.string.errServicePhoneNotFound, (AbstractUI)ctx);
			}
			break;
		  }
		}
	  }    
	});
  }
  
  private void callWSGetPolicyDetail(){
	RequestParams params = new RequestParams(); 
	params.put("insuranceNumber", activePolicy.getInsuranceNumber());
	params.put("serialNumberSuffix", activePolicy.getSerialNumber());
	RestClient.get("getInsuranceDetailWS", params, new RestResponseHandler(this, true
			                                                          , false) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		 activePolicy = JsonUtil.getPolicyDetail(response);
	  }    
	});
  }
  
  private void callWSGetEventTypes(){
	RequestParams params = new RequestParams(); 
	params.put("idAseguradora", activePolicy.getInsuranceId());
	RestClient.get("geTtipoSiniestro", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONArray response) throws JSONException {
		eventTypes.clear();
		eventTypes.addAll(JsonUtil.getEventTypeList(response));
		eventTypes.add(0, new EventType(-1, "SELECCIONE"));
		if(report != null){
		  for(int i = 0; i < eventTypes.size();i++){
		    if(eventTypes.get(i).getId().equals(report.getCauseId())){
		      spCause.setSelection(i);
		      break;
		    } 
		  }
		}
		eventTypeAdapter.notifyDataSetChanged();
	  }    
	});
  }
	  	
}
