package com.lorant.mobile.android.ui.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.core.NotificationDAO;
import com.lorant.mobile.android.db.dao.core.PolicyDAO;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;
import com.lorant.mobile.android.util.GoogleCalendar;
import com.lorant.mobile.android.util.JsonUtil;
import com.lorant.mobile.android.util.Utilities;
import com.lorant.mobile.android.util.ws.RestClient;
import com.lorant.mobile.android.util.ws.RestResponseHandler;
import com.lorant.mobile.android.vo.bean.Company;
import com.lorant.mobile.android.vo.bean.Feature;
import com.lorant.mobile.android.vo.bean.Notification;
import com.lorant.mobile.android.vo.bean.Policy;

public class PolicyDetail extends AbstractUI{
	
  private DatePickerDialog toStartDatePickerDialog;
  private DatePickerDialog toEndDatePickerDialog;
  
	
  private EditText etAssuranceNumber = null;
  private EditText etSerialNumber = null;
  private Spinner srAssuranceName = null;
  private EditText etPlates = null;
  private EditText etOwnerName = null;
  private EditText etOwnerMail = null;
  private EditText etEndDate = null;
  private EditText etStartDate = null;
  private EditText etPaymentType = null;
  private EditText etSellerName = null;
  private EditText etOwnerPhone = null;
  private EditText etProduct = null;
  private EditText etDescription = null;
  private EditText etPolicyName = null;
  private Switch swRemember = null;
  private RelativeLayout rlSerialNumber = null;
  private RelativeLayout rlPlates = null;
  private RelativeLayout rlDescription = null;
  private LinearLayout llCoverages = null;
  private LinearLayout llDetails = null;
  private Uri maskURI = null;
  private Policy policy = null;
  private boolean forNew = true;
  boolean isEditable = true;
  private Button bnPhoto = null;
  
  private List<Company> companies = null;
  private PolicyDAO dao = null;
  
  public PolicyDetail(){
	super(R.layout.assurance_add_new);
  }
  
  @Override
  public void populate(){
	int titleResource = 0;
	Calendar newDate = Calendar.getInstance();
	switch (policy.getGroupId()){
	  case 1: titleResource = forNew ? R.string.addCarInssurance
			                         : R.string.carInssurance;
	          break;
	  case 2: titleResource = forNew ? R.string.addHomeInssurance
			                         : R.string.homeInssurance;
              break;
	  case 3: titleResource = forNew ? R.string.addLifeInssurance
			                         : R.string.lifeInssurance;
              break;
	  case 4: titleResource = forNew ? R.string.addMedicalInssurance
			                         : R.string.medicalInssurance;
              break;
	  case 5: titleResource = forNew ? R.string.addEducationInssurance
			                         : R.string.educationInssurance;
              break;
	  case 6: titleResource = forNew ? R.string.addPetInssurance
			                         : R.string.petInssurance;
              break;
	  case 7: titleResource = forNew ? R.string.addOtherInssurance
			                         : R.string.otherInssurance;
              break;
	}
	setActionBarTitle(titleResource);
	if(!forNew){
	  setActionButtonBackground(R.drawable.common_button_edit); 
	}
	callGetCompaniesService(); 
	if(forNew){
        etStartDate.setText(Config.DATE_FORMAT.format(newDate.getTime()));
        newDate.add(Calendar.YEAR, 1);
        etEndDate.setText(Config.DATE_FORMAT.format(newDate.getTime()));
	}
  }
  
  @Override
  public void onBackPressed() {
	if(forNew){
	    startActivity(new Intent(this, PolicyMenu.class));
	} else {
		startActivity(new Intent(this, PolicyList.class));
	}
  }
  
  @Override
  public void onActionButtonClickEvent(View v) {
	MessageManager.show(R.string.updateEnabled, (AbstractUI) ctx);
	setEditableInput(isEditable = true);
  }
  
  @Override
  public void init(){
	setDateDialog();
	dao = new PolicyDAO(ctx);
	if((policy = (Policy) extras.get("policy")) == null){
		policy = new Policy();
		setFieldsFromExtras();
	} 
	forNew = extras.getBoolean("forNew");
	if(extras.get("isEditable") == null){
		isEditable = forNew;
	} else {
		isEditable = extras.getBoolean("isEditable");
	}
  }

  
  @Override
  public void bind(){
	etAssuranceNumber = (EditText) rootView.findViewById(R.id.etAssuranceNumber);
	etSerialNumber = (EditText) rootView.findViewById(R.id.etSerialNumber);
	srAssuranceName = (Spinner) rootView.findViewById(R.id.srAssuranceName);
	etPlates = (EditText) rootView.findViewById(R.id.etPlates);
	etOwnerName = (EditText) rootView.findViewById(R.id.etOwnerName);
	etOwnerMail = (EditText) rootView.findViewById(R.id.etOwnerMail);
	etEndDate = (EditText) rootView.findViewById(R.id.etEndDate);
	etStartDate = (EditText) rootView.findViewById(R.id.etStartDate);
	etPaymentType = (EditText) rootView.findViewById(R.id.etPaymentType);
	etSellerName = (EditText) rootView.findViewById(R.id.etSellerName);
	etOwnerPhone = (EditText) rootView.findViewById(R.id.etOwnerPhone);
	etProduct = (EditText) rootView.findViewById(R.id.etProduct);
	etPolicyName = (EditText) rootView.findViewById(R.id.etPolicyName);
	etDescription = (EditText) rootView.findViewById(R.id.etDescription);
	rlSerialNumber = (RelativeLayout) rootView.findViewById(R.id.rlSerialNumber);
	rlPlates = (RelativeLayout) rootView.findViewById(R.id.rlPlates);
	llCoverages = (LinearLayout) rootView.findViewById(R.id.llCoverages);
	llDetails = (LinearLayout) rootView.findViewById(R.id.llDetails);
	rlDescription = (RelativeLayout) rootView.findViewById(R.id.rlDescription);
	swRemember = (Switch) rootView.findViewById(R.id.swRemember);
	bnPhoto = (Button) rootView.findViewById(R.id.bnPhoto);
	if(policy.getGroupId() != 1){
	  rlSerialNumber.setVisibility(View.GONE);
	  rlPlates.setVisibility(View.GONE);
	} if (policy.isInternal()){
		rlSerialNumber.setVisibility(View.GONE);
		rlDescription.setVisibility(View.GONE);
	}
  }
  
  public void onClickEtEndDate (View view){
	toEndDatePickerDialog.show();
  }
  
  public void onClickEtStartDate (View view){
    toStartDatePickerDialog.show();
  }
  
  @Override
  protected int validateForm(){
	int errMessageId = 0;
	if(policy.isInternal()){
		if(etOwnerPhone.getText().toString().isEmpty()){
			errMessageId = R.string.errPolicyOwnerPhoneRequired;
		} else if(etOwnerMail.getText().toString().isEmpty()){
			errMessageId = R.string.errPolicyOwnerMailRequired;
		} 
	} else {
	  if(etPolicyName.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyNameRequired;
	  } else if(etAssuranceNumber.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyNumberRequired;
	  } else if(srAssuranceName.getSelectedItemPosition() == 0){
		errMessageId = R.string.errPolicyCompanyRequired;
	  } else if(etOwnerName.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyOwnerNameRequired;
	  } else if(etOwnerMail.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyOwnerMailRequired;
	  } else if(etEndDate.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyEndDateRequired;
	  } else if(etStartDate.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyStartDateRequired;
	  } else if(etOwnerPhone.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyOwnerPhoneRequired;
	  } else if(etDescription.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyDescriptionRequired;
	  } else if(etProduct.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyProductRequired;
	  } else if(etPaymentType.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicyPaymentTypeRequired;
	  } else if(etSellerName.getText().toString().isEmpty()){
		errMessageId = R.string.errPolicySellerNameRequired;
	  }  
	  if(policy.getGroupId() == 1){
		if(etPlates.getText().toString().isEmpty()){
			errMessageId = R.string.errPolicyPlatesRequiredRequired;
		} else if(etSerialNumber.getText().toString().isEmpty()){
			errMessageId = R.string.errPolicySerialNumberRequiredRequired;
		}
	  }
	  try {
		if((Config.DATE_FORMAT.parse(etEndDate.getText().toString()).compareTo(Config
				           .DATE_FORMAT.parse(etStartDate.getText().toString())) < 0)){
			errMessageId = R.string.errPolicyWrongDates;
		}
	  } catch (ParseException unparsableDates) {} 
	}
	return errMessageId;
  }
  
  public void setBehavior(){
	setFormBehavior((RelativeLayout) rootView);
	etEndDate.setOnFocusChangeListener(new OnFocusChangeListener(){
	  @Override
	  public void onFocusChange(View v, boolean hasFocus) {
	    if(hasFocus){
		  toEndDatePickerDialog.show();
		}
	  }  
	});
	etStartDate.setOnFocusChangeListener(new OnFocusChangeListener(){
	  @Override
	  public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
		  toStartDatePickerDialog.show();
		}
	  }  
	});
	setEditableInput(isEditable);
	if(! forNew){
	  bnPhoto.setText(R.string.viewPolicyImage);
	}
  }
  
  private void setDateDialog() {
    Calendar newCalendar = Calendar.getInstance();      
    toStartDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
      public void onDateSet(DatePicker view, int year, int monthOfYear
    		                                       , int dayOfMonth) {
        Calendar newDate = Calendar.getInstance();
        newDate.set(year, monthOfYear, dayOfMonth);
        etStartDate.setText(Config.DATE_FORMAT.format(newDate.getTime()));
      }

    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH)
                           , newCalendar.get(Calendar.DAY_OF_MONTH));
    toEndDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear
                , int dayOfMonth) {
           Calendar newDate = Calendar.getInstance();
           newDate.set(year, monthOfYear, dayOfMonth);
           etEndDate.setText(Config.DATE_FORMAT.format(newDate.getTime()));
         }

     },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH)
                          , newCalendar.get(Calendar.DAY_OF_MONTH));
  }
  
  public void onClickBnMoreInformation(View clickedView){
	Intent toAdditionInformation = new Intent(this, PolicyAdditional.class);
	setFields();
	toAdditionInformation.putExtra("policy", policy);
	toAdditionInformation.putExtra("isEditable", isEditable);
	toAdditionInformation.putExtra("forNew", forNew);
	startActivity(toAdditionInformation);
  }
  
  private void callGetCompaniesService(){
	RequestParams params = new RequestParams();
	params.put("_iIdRamo", policy.getGroupId());
	RestClient.post("getAseguradoras", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONArray response) throws JSONException {
		 ArrayAdapter<Company> dataAdapter = null;
		 companies = JsonUtil.getCompaniesProfile(response);
		 companies.add(0, new Company(-1, "SELECCIONE"));
		 dataAdapter = new ArrayAdapter<Company>(ctx, android.R.layout
                  .simple_spinner_item, companies);
         dataAdapter.setDropDownViewResource(android.R.layout
                              .simple_spinner_dropdown_item);
         srAssuranceName.setAdapter(dataAdapter);
         if(extras.get("policy") == null){
       	  callSearchInsuranceService();
       	 } else {
 		   setFieldsFromPolicy();
 		  isEditable = forNew;
       	 }
	  }
	});
  }
  
  private void callSearchInsuranceService(){
	RequestParams params = new RequestParams();
	params.put("insuranceNumber", policy.getInsuranceNumber()
			              .toUpperCase(Locale.getDefault()));
	params.put("serialNumberSuffix", policy.getSerialNumber()
			              .toUpperCase(Locale.getDefault()));
	params.put("_iIdRamo", policy.getGroupId());
	RestClient.post("searchInsurance", params, new RestResponseHandler(this
			                                               , true, false) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		String errorCode = response.getString(Config.WS_CODE_NAME);
		if(errorCode.equals("ER0007")){
		  callInsuranceDetailService();
		} else if(errorCode.equals("ER0008") || errorCode.equals("ER0001") 
				                         || errorCode.equals("ER0010")){
			policy = JsonUtil.getPolicyDetail(response);
			setEditableInput(isEditable);
			if(errorCode.equals("ER0001")){
			   setFieldsFromPolicy();
			   isEditable = forNew;
			   if(forNew){
			     etPolicyName.setEnabled(true);
			     etPolicyName.setFocusable(true);
			 	 etPolicyName.setFocusableInTouchMode(true);
			     etPolicyName.setText("");
			     etPolicyName.requestFocus();
			   }
			} else {
				policy = new Policy();
				setFieldsFromExtras();
				etAssuranceNumber.setText(policy.getInsuranceNumber());
				etSerialNumber.setText(policy.getSerialNumber());
				isEditable = true;
			}
		} else {
			MessageManager.show(response.getString("ErrorMessage")
					                          , (AbstractUI) ctx);
		}
	  }
	});  	
  }
  
  
  private void findAdditionalInformation(){
	Policy addInformation = dao.findByPolicyNumber(policy.getInsuranceNumber());
	if(addInformation != null){
	  policy.setPaymentDay(addInformation.getPaymentDay());
	  policy.setComment(addInformation.getComment());
	  policy.setRemember(addInformation.getRemember());
	  policy.setStartAlertDay(addInformation.getStartAlertDay());
	  policy.setEndAlertDay(addInformation.getEndAlertDay());
	  policy.setPaymentInstrumentId(addInformation.getPaymentInstrumentId());
	  policy.setImageUri(addInformation.getImageUri());
	  policy.setRememberPayment(addInformation.getRememberPayment());
	  policy.setBankId(addInformation.getBankId());
	}
  }
  
  private void setFieldsFromExtras(){
	 policy.setInsuranceNumber(extras.getString("policyNumber"));
	 policy.setSerialNumber(extras.getString("serialNumber"));
	 policy.setGroupId(extras.getInt("groupId"));
  }
  
  public void onClickBnPhoto(View clickedView){
	Intent toShowPolicy = null;
	if(forNew){
	  showCaptureImageDialog();
	}else {
		if(policy.getImageUri() != null){
	      toShowPolicy = new Intent();
	      toShowPolicy.setAction(Intent.ACTION_VIEW);
	      toShowPolicy.setDataAndType(Uri.parse(policy.getImageUri()), "image/*");
	      startActivity(toShowPolicy);
		} else {
			MessageManager.show(R.string.imageNotFound, this);
		}
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
      if((maskURI = data.getData()) == null){
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
         maskURI = Uri.fromFile(destination);
      } 
      policy.setImageUri(maskURI.toString());
	} 
  }
  
  private void callInsuranceDetailService(){
	RequestParams params = new RequestParams();
	params.put("insuranceNumber", policy.getInsuranceNumber()
			              .toUpperCase(Locale.getDefault()));
	params.put("serialNumberSuffix", policy.getSerialNumber()
			              .toUpperCase(Locale.getDefault()));
	RestClient.post("getInsuranceDetailWS", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		Intent toPolicyList = null;
		policy = JsonUtil.getPolicyDetail(response);
		if(policy.getDetails().size() == 0){
		  policy.addDetail(ctx.getResources().getString(R.string.noInformation), "");
		}
		if(policy.getCoverages().size() == 0){
		  policy.addCoverage(ctx.getResources().getString(R.string.noInformation), "");
		}
		if(forNew){
		  toPolicyList = new Intent(ctx, PolicyList.class);
		  toPolicyList.putExtra("message", R.string.policyAreadyExists);
		  startActivity(toPolicyList);
		} else {
		   isEditable = false;
		   findAdditionalInformation();
		   setFieldsFromPolicy();
		}
		setEditableInput(isEditable);
	  }
	});  	
  }
  
  private void refreshPolicy(){
	RequestParams params = new RequestParams();
	params.put("insuranceNumber", policy.getInsuranceNumber());
	params.put("serialNumberSuffix", policy.getSerialNumber());
	RestClient.post("getInsuranceDetailWS", params, new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		Intent toPolicyList = null;
		Policy serverPolicy = JsonUtil.getPolicyDetail(response);
		policy.setId(serverPolicy.getId());
		policy.setPlates(etPlates.getText().toString());
		toPolicyList = new Intent(ctx, PolicyList.class);
		dao.upsert(policy);
		insertCalendarAvents();
		updateLocalReminders();
		startActivity(toPolicyList);
	  }
	});  	
  }
  
  public void onClickBnSave(View clickedView){
	int errMessageId = 0;
	if((errMessageId = validateForm()) > 0){
		MessageManager.show(errMessageId, this);
		return;
	}
	callUpsertService();
  }
  
  private void callUpsertService(){
	RequestParams params = new RequestParams();
	params.put("alias", etPolicyName.getText().toString());
	params.put("insuranceNumber", etAssuranceNumber.getText().toString());
	params.put("serialNumberSuffix", etSerialNumber.getText().toString());
	params.put("nickName", getStringParam(Config.PREF_USERNAME));
	params.put("name", etOwnerName.getText().toString());
	params.put("startDate", etStartDate.getText().toString());
	params.put("endDate", etEndDate.getText().toString());
	params.put("contactMail", etOwnerMail.getText().toString());
	params.put("contactPhoneNumber", etOwnerPhone.getText().toString());
	params.put("_iIdRamo", policy.getGroupId());
	params.put("idAseguradora", ((Company)srAssuranceName.getSelectedItem()).getId());
	params.put("idSistema", "1");
	params.put("idPolizaSistema", "0");
	params.put("Placas", etPlates.getText().toString());
	params.put("FormaPago", etPaymentType.getText().toString());
	params.put("Paquete", etProduct.getText().toString());
	params.put("Descripcion", etDescription.getText().toString());
	params.put("Agente", etSellerName.getText().toString());
	RestClient.post(forNew ? "addInsurance" : "updateInsurance", params
			                   , new RestResponseHandler(this, true) {
	  @Override
	  public void onSuccess(JSONObject response) throws JSONException {
		 setFields();
		 refreshPolicy();
	  }
	});  	
  }
  
  private void setFieldsFromPolicy(){
	etAssuranceNumber.setText(policy.getInsuranceNumber());
	etOwnerName.setText(policy.getOwnerName());
	etEndDate.setText(Utilities.dateToString(policy.getEndDate()));
	etStartDate.setText(Utilities.dateToString(policy.getStartDate()));
	etOwnerMail.setText(policy.getOwnerMail());
	etOwnerPhone.setText(policy.getOwnerPhone());
	etPaymentType.setText(policy.getPaymentType());
	etSellerName.setText(policy.getSellerName());
	etProduct.setText(policy.getProduct());
	etPolicyName.setText(policy.getName());
	swRemember.setChecked(policy.getRemember());
	etDescription.setText(policy.getDetail("Descripción").getValue());
	for(int i = 0; i < companies.size() ; i++){
	  if(companies.get(i).getId().equals(policy.getInsuranceId())){
		srAssuranceName.setSelection(i);
		break;
	  }
	}
	if(policy.getGroupId() == 1){
	   etPlates.setText(policy.getPlates());
	   etSerialNumber.setText(policy.getDetail("Número de Serie", "Serie").getValue());
	} else {
		etPlates.setVisibility(View.GONE);
		etSerialNumber.setVisibility(View.GONE);
	}
	for(int i = 0; i< policy.getDetails().size(); i++){
		addFeature(policy.getDetails().get(i), llDetails,(i%2==0));
	}
	for(int i = 0; i< policy.getCoverages().size(); i++){
	  addFeature(policy.getCoverages().get(i), llCoverages,(i%2==0));
	}
  }  
  
  private void setFields(){
	policy.setInsuranceNumber(etAssuranceNumber.getText().toString());
	policy.setOwnerName(etOwnerName.getText().toString());
	policy.setEndDate(Utilities.stringToDate(etEndDate.getText().toString()));
	policy.setStartDate(Utilities.stringToDate(etStartDate.getText().toString()));
	policy.setOwnerMail(etOwnerMail.getText().toString());
	policy.setOwnerPhone(etOwnerPhone.getText().toString());
	policy.setPaymentType(etPaymentType.getText().toString());
	policy.setSellerName(etSellerName.getText().toString());
	policy.setProduct(etProduct.getText().toString());
	policy.setName(etPolicyName.getText().toString());
	policy.setInsuranceId(((Company)srAssuranceName.getSelectedItem()).getId());
	policy.replaceDetail("Descripción", etDescription.getText().toString());
	if(policy.getGroupId() == 1){
	  policy.setPlates(etPlates.getText().toString());
	  policy.replaceDetail("Número de Serie", etSerialNumber.getText().toString());
	  policy.setSerialNumber(etSerialNumber.getText().toString());
	}
	policy.setRemember(swRemember.isChecked());
	policy.setImageUri(maskURI== null ? null : maskURI.toString());
  }
  
  private void setEditableInput(boolean isEditable){
	boolean defaulMode = isEditable && !policy.isInternal();
	etAssuranceNumber.setFocusable(defaulMode);
	etAssuranceNumber.setFocusableInTouchMode(defaulMode);
	etPolicyName.setFocusable(isEditable);
	etPolicyName.setFocusableInTouchMode(defaulMode);
	srAssuranceName.setEnabled(defaulMode);
	etSerialNumber.setFocusable(defaulMode);
	etSerialNumber.setFocusableInTouchMode(defaulMode);
	etPlates.setFocusable(defaulMode);
	etPlates.setFocusableInTouchMode(defaulMode);
	etOwnerName.setFocusable(defaulMode);
	etOwnerName.setFocusableInTouchMode(defaulMode);
	etOwnerPhone.setFocusable(isEditable);
	etOwnerPhone.setFocusableInTouchMode(isEditable);
	etDescription.setFocusable(isEditable);
	etDescription.setFocusableInTouchMode(isEditable);
	etOwnerMail.setFocusable(isEditable);
	etOwnerMail.setFocusableInTouchMode(isEditable);
	etEndDate.setFocusable(defaulMode);
	etEndDate.setFocusableInTouchMode(defaulMode);
	etStartDate.setFocusable(defaulMode);
	etStartDate.setFocusableInTouchMode(defaulMode);
	swRemember.setEnabled(isEditable);
	etPaymentType.setFocusable(defaulMode);
	etPaymentType.setFocusableInTouchMode(defaulMode);
	etSellerName.setFocusable(defaulMode);
	etSellerName.setFocusableInTouchMode(defaulMode);
	etProduct.setFocusable(defaulMode); 
	etProduct.setFocusableInTouchMode(defaulMode);
  }
  
  
  private void addFeature(Feature feature, LinearLayout container, boolean odd){
	LinearLayout llfeature = null;
	TextView tvLabel = null;
	LayoutParams entryParams = new LinearLayout.LayoutParams(LayoutParams
                               .MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	llfeature = new LinearLayout(ctx);
	llfeature.setOrientation(LinearLayout.HORIZONTAL);
	llfeature.setLayoutParams(entryParams);
	llfeature.setWeightSum(2f);
	llfeature.setBackgroundColor(odd ? Color.WHITE : Color.LTGRAY);
	tvLabel = new TextView(ctx);
	tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0, 110, 1f));
	tvLabel.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
	tvLabel.setText(feature.getName());
	tvLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);
	tvLabel.setPadding(20, 0, 0, 0);
	llfeature.addView(tvLabel);
	tvLabel = new TextView(ctx);
	tvLabel.setLayoutParams(new LinearLayout.LayoutParams(0, 110, 1f));
	tvLabel.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
	tvLabel.setPadding(80, 0, 0, 0);
	tvLabel.setText(feature.getValue());
	tvLabel.setTextAppearance(this, android.R.style.TextAppearance_Medium);
	llfeature.addView(tvLabel);
	container.addView(llfeature);
  }
  
  private void insertCalendarAvents(){
	GoogleCalendar googleCalendar = new GoogleCalendar(ctx);
	GregorianCalendar date = new GregorianCalendar();
	
	googleCalendar.switchEvent(getResources().getString(R.string.policyExpiry)
			        , getResources().getString(R.string.policyExpiryDescription
			        		                       , policy.getInsuranceNumber())
			        , date, policy.getRemember(), policy, Config.ALERT_TYPES.EXPIRY.getId()
			        , "FREQ=DAILY;COUNT=1");
	if(policy.getStartAlertDay() != null){
	  date = new GregorianCalendar();
	  date.set(Calendar.DAY_OF_MONTH, policy.getStartAlertDay());
	  googleCalendar.switchEvent(getResources().getString(R.string.policyPayment)
	        , getResources().getString(R.string.policyPaymentDescription
	        		                       , policy.getInsuranceNumber())
	        , date, policy.getRememberPayment(), policy, Config.ALERT_TYPES.PAYMENT.getId()
	        , "FREQ=DAILY;COUNT=" + (policy.getEndAlertDay() - policy.getStartAlertDay()));
	}
  }
  
  private void updateLocalReminders(){
	NotificationDAO notificationDAO = new NotificationDAO(this);
    Notification notification = null;
	Calendar calendar = new GregorianCalendar();
	calendar.setTime(policy.getEndDate());
	calendar.add(Calendar.DAY_OF_YEAR, -1 * Config.EXPIRY_REMINDER);
	notificationDAO.delete(policy.getId(), Config.ALERT_TYPES.EXPIRY.getId());
	if(policy.getRemember()){
	  for(int i = 0; i<  Config.EXPIRY_REMINDER ; i++){
	    calendar.add(Calendar.DAY_OF_YEAR, 1);
	    notification = new Notification();
	    notification.setName(getResources().getString(R.string.policyExpiryDescription
                                                     , policy.getInsuranceNumber()));
        notification.setMonth(calendar.get(Calendar.MONTH) + 1);
        notification.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        notification.setPolicyId(policy.getId());
        notification.setType(Config.ALERT_TYPES.EXPIRY.getId());
        notificationDAO.insert(notification);
	  }
	}
	notificationDAO.delete(policy.getId(), Config.ALERT_TYPES.PAYMENT.getId());
	if(policy.getRememberPayment()){
	  for(int i = policy.getStartAlertDay(); i <=  policy.getEndAlertDay(); i++){
		notification = new Notification();
		notification.setName(getResources().getString(R.string
				       .policyPaymentDescription, policy.getInsuranceNumber()));
        notification.setMonth(0);
        notification.setDay(i);
        notification.setPolicyId(policy.getId());
        notification.setType(Config.ALERT_TYPES.PAYMENT.getId());
        notificationDAO.insert(notification);
	  }
	}
	
  }
}
