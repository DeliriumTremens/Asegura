package com.lorant.mobile.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.util.JsonUtil;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;
import com.lorant.mobile.android.vo.bean.Historical;

public class ReportHistorical extends AbstractUI {
	 
  private static final Integer MAX_MAPS_NUMBER = 10;
  private List<MapFragment> mapFragments = new ArrayList<MapFragment>();
  private List<GoogleMap> googleMaps = new ArrayList<GoogleMap>();
  

  public ReportHistorical(){
	super(R.layout.report_historical);
  }
  
  @Override
  public void init(){
	MapFragment mapFragment = null;
	for(int i = 1; i <= MAX_MAPS_NUMBER ; i++){
	  mapFragment = MapFragment.newInstance();
	  FragmentTransaction fragmentTransaction =getFragmentManager()
				                                 .beginTransaction();
	  fragmentTransaction.add(getResources().getIdentifier("mapContainer_" 
                               + i, "id", getPackageName()), mapFragment);
	  fragmentTransaction.commit();
	  mapFragments.add(mapFragment);
	}
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.historical);
	callWSGetHistorical(); 
  }
  
  @Override
  public void bind(){
  }
  
  public void onStart() {
	super.onStart();
	int psStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
	GoogleMap googleMap = null;
	if(psStatusCode == ConnectionResult.SUCCESS){
	  for(int i = 0; i < MAX_MAPS_NUMBER ; i++){
		  googleMap = mapFragments.get(i).getMap();
		  googleMaps.add(googleMap);
	  }
	} else{
	    GooglePlayServicesUtil.getErrorDialog(2, this,1).show();
	}
  }
  
  @Override
  public void setBehavior() {
  }
  
  private void callWSGetHistorical(){
	RequestParams params = new RequestParams();
	params.add("nickName", getStringParam(Config.PREF_USERNAME));
    RestClient.get("getRecuperaSiniestro", params, new RestResponseHandler(this, false) {
	  @Override
	  public void onSuccess(JSONArray response) throws JSONException {
		List<Historical> items = JsonUtil.getHistorical(response);
		View view = null;
		int i = 0;
		for(; i< items.size() && i < MAX_MAPS_NUMBER ; i++){
		  view = rootView.findViewById(getResources().getIdentifier("container_" 
                                            + (i + 1), "id", getPackageName()));
		  setBehavior(view, items.get(i), i + 1);
		}
		for(; i< MAX_MAPS_NUMBER; i++){
		  view = rootView.findViewById(getResources().getIdentifier("container_" 
                     + (i + 1), "id", getPackageName()));
		  rootView.removeView(view);
		}
	  }    
	});
  }
  
  private void setBehavior(View view, Historical historical, int index){
	GoogleMap googleMap = googleMaps.get(index - 1); 
	LatLng latLng = new LatLng(historical.getLatitude(), historical
			                                      .getLongitude());
	TextView tvDate = (TextView) view.findViewById(getViewId("tvDate", index));
	TextView tvTime = (TextView) view.findViewById(getViewId("tvTime", index));
	TextView tvPolicyNumber = (TextView) view.findViewById(getViewId("tvPolicyNumber", index));
	TextView tvEventType = (TextView) view.findViewById(getViewId("tvEventType", index));
	TextView tvInformation = (TextView) view.findViewById(getViewId("tvInformation", index));
    
	tvDate.setText(Config.DATE_FORMAT_HISTORICAL.format(historical
			                             .getRegistrationDate()));
	tvPolicyNumber.setText(historical.getPolicyNumber());
	tvEventType.setText(historical.getSinesterType());
	tvInformation.setText(historical.getInformation());
	tvTime.setText(historical.getRegistrationTime());
	googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    googleMap.addMarker(new MarkerOptions().position(latLng));
	
  }
  
  private Integer getViewId(String name, Integer index){
	return (getResources().getIdentifier(name + "_" + (index), "id"
			                                  , getPackageName()));
  }
  
  
}
