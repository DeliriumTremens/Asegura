package com.lorant.mobile.android.ui.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.db.dao.core.CityDAO;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;
import com.lorant.mobile.android.util.JsonUtil;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;
import com.lorant.mobile.android.vo.bean.Brand;
import com.lorant.mobile.android.vo.bean.City;
import com.lorant.mobile.android.vo.bean.Dealer;

public class Dealers extends AbstractUI 
                     implements GoogleMap.OnMyLocationChangeListener {
	
  private MapFragment mapFragment = null;
  private GoogleMap googleMap = null;
  private AlertDialog cityDialog = null;
  
  private Button bnSearchByLocation = null;
  private Button bnSearchByCity = null;
  private Button bnCall = null;
  private Spinner spBrand = null;
  private LatLng latLng = null;
  private City city = null;
  private Map<Marker, Dealer> markers = new HashMap<Marker, Dealer>();
  private Dealer activeDealer = null;
  private TextView tvDealerName = null;
  private TextView tvDealerAddress = null;
  private TextView tvDealerPhone = null;
  private TextView tvDealerMail = null;
  private TextView tvDealerPhoneLb = null;
  private TextView tvDealerMailLb = null;
  
  private List<Brand> brands = null;
  
  public Dealers(){
	super(R.layout.dealers);
  }
  
  @Override
  public void bind(){
	bnSearchByLocation = (Button) rootView.findViewById(R.id.bnSearchByLocation);
	bnSearchByCity = (Button) rootView.findViewById(R.id.bnSearchByCity);
	bnCall = (Button) rootView.findViewById(R.id.bnCall);
	spBrand = (Spinner) rootView.findViewById(R.id.spBrand);
	tvDealerName = (TextView) rootView.findViewById(R.id.tvDealerName);
	tvDealerAddress = (TextView) rootView.findViewById(R.id.tvDealerAddress);
	tvDealerPhone = (TextView) rootView.findViewById(R.id.tvDealerPhone);
	tvDealerMail = (TextView) rootView.findViewById(R.id.tvDealerMail);
	tvDealerPhoneLb = (TextView) rootView.findViewById(R.id.tvDealerPhoneLb);
	tvDealerMailLb = (TextView) rootView.findViewById(R.id.tvDealerMailLb);
  }
  
  @Override
  public void setBehavior(){
	spBrand.setOnItemSelectedListener(new OnItemSelectedListener(){
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				                            int position, long id) {
		  tvDealerName.setText("");
		  tvDealerAddress.setText("");
		  tvDealerPhone.setText("");
		  tvDealerMail.setText("");
		  tvDealerPhoneLb.setVisibility(View.INVISIBLE);
		  tvDealerMailLb.setVisibility(View.INVISIBLE);
		  bnCall.setEnabled(false);
		  bnSearchByCity.setBackgroundResource(R.drawable.common_button_border_red);
		  bnSearchByCity.setTextColor(getResources().getColor(R.color.themeRed));
		  bnSearchByLocation.setBackgroundResource(R.drawable.common_button_border_red);
		  bnSearchByLocation.setTextColor(getResources().getColor(R.color.themeRed));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {	
		}
	}); 
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.dealers);
	callWSGetBrandList();
  }
  
  @Override
  public void init(){
	mapFragment = MapFragment.newInstance();
	FragmentTransaction fragmentTransaction =getFragmentManager()
			                                 .beginTransaction();
	fragmentTransaction.add(R.id.mapContainer, mapFragment);
	fragmentTransaction.commit();
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
  }
  
  public void onClickBnSearchByLocation(View clickedView){
	int errMessageId = 0;
	if(latLng != null){
	  if((errMessageId = validateForm()) > 0){
		MessageManager.show(errMessageId, this);
	  } else {
		  bnSearchByLocation.setBackgroundColor(getResources().getColor(R.color.themeRed));
		  bnSearchByLocation.setTextColor(getResources().getColor(R.color.themeWhite));
		  bnSearchByCity.setBackgroundResource(R.drawable.common_button_border_red);
		  bnSearchByCity.setTextColor(getResources().getColor(R.color.themeRed));
	      callWSGetDealersService(true);
	  }
	}
  }
  
  public void onClickBnSearchByCity(View clickedView){
	int errMessageId = 0;
	if((errMessageId = validateForm()) > 0){
	  MessageManager.show(errMessageId, this);
	} else {
	    bnSearchByCity.setBackgroundColor(getResources().getColor(R.color.themeRed));
		bnSearchByCity.setTextColor(getResources().getColor(R.color.themeWhite));
		bnSearchByLocation.setBackgroundResource(R.drawable.common_button_border_red);
		bnSearchByLocation.setTextColor(getResources().getColor(R.color.themeRed));
		showCitySelector();
	}
  }

  @Override
  public void onMyLocationChange (Location location) {
    latLng = new LatLng(location.getLatitude(), location.getLongitude());
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    googleMap.setOnMyLocationChangeListener(null);
  }
  
  public void onClickBnCall(View clickedView) {
	if(activeDealer == null || activeDealer.getPhone() == null || activeDealer
			                                           .getPhone().isEmpty()){
		MessageManager.show(R.string.errPhoneNumberNotFound, this);
	} else {
		startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" 
		                               + activeDealer.getPhone())));
	}
  }
  
  @Override
  protected int validateForm(){
	int errMessageId = 0;
	if(spBrand.getSelectedItemPosition() == 0){
		errMessageId = R.string.errBrandRequired;
	}
	return errMessageId;
  }
  
  private void callWSGetBrandList(){
	RequestParams params = new RequestParams(); 
	RestClient.get("getMarcas", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONArray response) throws JSONException {
		ArrayAdapter<Brand> dataAdapter = null;
		brands = JsonUtil.getBrandList(response);
		brands.add(0, new Brand(-1, "SELECCIONE"));
		dataAdapter = new ArrayAdapter<Brand>(ctx, android.R.layout
	                                   .simple_spinner_item, brands);
	    dataAdapter.setDropDownViewResource(android.R.layout
	                         .simple_spinner_dropdown_item);
	    spBrand.setAdapter(dataAdapter);
	  }    
	});
  }
  
  public void onClickTvDealerMail(View clickedView){
	Intent intent = new Intent(Intent.ACTION_SENDTO);
	intent.setType("text/plain");
	intent.setData(Uri.parse("mailto:" + ((TextView) clickedView).getText())); 
	intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string
			                                                    .contact));
	startActivity(intent);
  }
  
  private void callWSGetDealersService(final boolean byLocation){
	RequestParams params = new RequestParams();
	params.put("Coordenada", latLng.latitude + "," + latLng.longitude);		
	params.put("Estado", city != null ? city.getId(): 0);
	params.put("IDMarca", ((Brand) spBrand.getSelectedItem()).getId());
	params.put("IdTipoBusqueda", byLocation ? 0 : 1);
	RestClient.get("getAgencias", params, new RestResponseHandler(this, true) {
		  @Override
		  public void onSuccess(JSONArray response) throws JSONException {
			List<Dealer> dealers = JsonUtil.getDealers(response);
			Marker marker = null;
			googleMap.clear();
			markers.clear();
			for(Dealer dealer : dealers){
			  marker = googleMap.addMarker(new MarkerOptions()
		               .position(dealer.getLatLng()));
			  googleMap.setOnMarkerClickListener(new OnMarkerClickListener(){
				@Override
				public boolean onMarkerClick(Marker marker) {
			      activeDealer = markers.get(marker);
			      tvDealerName.setText(activeDealer.getName());
			      tvDealerAddress.setText(activeDealer.getAddres());
			      tvDealerPhone.setText(activeDealer.getPhone());
			      tvDealerMail.setText(activeDealer.getMail());
			      tvDealerPhoneLb.setVisibility(View.VISIBLE);
			      tvDealerMailLb.setVisibility(View.VISIBLE);
			      bnCall.setEnabled(true);
				  return false;
				}
				  
			  });
			  markers.put(marker, dealer);
			}
			if(dealers.isEmpty()){
			  MessageManager.show(R.string.errDealersNotFound, (AbstractUI) ctx);
			} 
			if(!byLocation && dealers.size() > 0){
			    googleMap.moveCamera(CameraUpdateFactory.newLatLng(dealers.get(0)
			    		                                         .getLatLng()));
			    googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
			}
			
			if(byLocation){
			  googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			  googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
			}
		  }    
		});
	  }
  
  public void showCitySelector(){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	View dialogView = getLayoutInflater().inflate(R.layout
			                .report_cities_list_ft, null);
	final ListView lvCities = (ListView) dialogView.findViewById(R.id.lvCities);
	final List<City> cities = new CityDAO(ctx).getAll();
	lvCities.setAdapter(new ArrayAdapter<City>(this, android.R.layout
		   .simple_list_item_1, android.R.id.text1, cities)); 
	builder.setView(dialogView);
	cityDialog = builder.create();
	cityDialog.show();
	lvCities.setOnItemClickListener(new OnItemClickListener() {
	      @Override
	      public void onItemClick(AdapterView<?> parent, View view,
	        int position, long id) {
	    	  int errMessageId = 0;
	    	  cityDialog.cancel();
	    	  city = cities.get(position);
	    	  if((errMessageId = validateForm()) > 0){
	    		  MessageManager.show(errMessageId, (AbstractUI) ctx);
	    	  } else {
	    		  callWSGetDealersService(false);
	    	  }
	      }
	   });
  }

}
