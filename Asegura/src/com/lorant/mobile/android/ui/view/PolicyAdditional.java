package com.lorant.mobile.android.ui.view;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.db.dao.core.BankDAO;
import com.lorant.mobile.android.db.dao.core.PaymentInstrumentDAO;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.ui.util.MessageManager;
import com.lorant.mobile.android.util.Utilities;
import com.lorant.mobile.android.vo.bean.Bank;
import com.lorant.mobile.android.vo.bean.PaymentInstrument;
import com.lorant.mobile.android.vo.bean.Policy;

public class PolicyAdditional extends AbstractUI{

  private Spinner srPaymentInstrument = null;
  private Spinner srBank = null;
  private EditText etPaymentDay = null;
  private EditText etComments = null;
  private EditText etRememberFrom = null;
  private EditText etRememberTo = null;
  private Switch swRememberPayment = null;
  private Policy policy = null;
  boolean isEditable = true;
  boolean forNew = false;
  
  private List<PaymentInstrument> paymentInstruments = null;
  private List<Bank> banks = null;
  
  public PolicyAdditional(){
	super(R.layout.assurance_add_information);
  }
  
  @Override
  public void init(){
	policy = (Policy) extras.get("policy");
	isEditable = extras.getBoolean("isEditable");
	forNew = extras.getBoolean("forNew");
  }
  
  @Override
  public void bind(){
	srPaymentInstrument = (Spinner) rootView.findViewById(R.id.srPaymentInstrument);
	srBank = (Spinner) rootView.findViewById(R.id.srBank);
	etPaymentDay = (EditText) rootView.findViewById(R.id.etPaymentDay);
	etComments = (EditText) rootView.findViewById(R.id.etComments);
	etRememberFrom = (EditText) rootView.findViewById(R.id.etRememberFrom);
	etRememberTo = (EditText) rootView.findViewById(R.id.etRememberTo);
	swRememberPayment = (Switch) rootView.findViewById(R.id.swRememberPayment);
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.addInssuranceInformation);
	loadPaymentInstruments();
	loadBanks();
	setFieldsFromPolicy();
  }
  
  @Override
  public void setBehavior(){
	setFormBehavior((RelativeLayout) rootView);
	setEnabled(isEditable);
  }
  
  private void setFieldsFromPolicy(){
	if(policy.getPaymentDay() != null && policy.getPaymentDay() > 0){
	  etPaymentDay.setText(policy.getPaymentDay().toString());
	}
	etComments.setText(policy.getComment());
	if(policy.getStartAlertDay() != null && policy.getStartAlertDay() > 0){
	  etRememberFrom.setText(policy.getStartAlertDay().toString());
	}
	if(policy.getEndAlertDay() != null && policy.getEndAlertDay() > 0){
		etRememberTo.setText(policy.getEndAlertDay().toString());
	}
	swRememberPayment.setChecked(policy.getRememberPayment());
	if(policy.getPaymentInstrumentId() > 0){
	  for(int i = 0; i< paymentInstruments.size() ; i++){
		if(paymentInstruments.get(i).getId().equals(policy.getPaymentInstrumentId())){
		  srPaymentInstrument.setSelection(i);
		  break;
		}
	  }
	}
	if(policy.getBankId() > 0){
	  for(int i = 0; i< banks.size() ; i++){
		if(banks.get(i).getId().equals(policy.getBankId())){
		  srBank.setSelection(i);
		  break;
		}
	  }
	}
  }
  
  private void loadPaymentInstruments(){
	ArrayAdapter<PaymentInstrument> dataAdapter = null;
	paymentInstruments = new PaymentInstrumentDAO(ctx).getAll();
	paymentInstruments.add(0, new PaymentInstrument(-1, "SELECCIONE"));
	dataAdapter = new ArrayAdapter<PaymentInstrument>(ctx, android.R.layout
               .simple_spinner_item, paymentInstruments);
    dataAdapter.setDropDownViewResource(android.R.layout
                           .simple_spinner_dropdown_item);
    srPaymentInstrument.setAdapter(dataAdapter);
  }
  
  private void loadBanks(){
	ArrayAdapter<Bank> dataAdapter = null;
	banks = new BankDAO(ctx).getAll();
	banks.add(0, new Bank(-1, "SELECCIONE"));
	dataAdapter = new ArrayAdapter<Bank>(ctx, android.R.layout
	              .simple_spinner_item, banks);
	dataAdapter.setDropDownViewResource(android.R.layout
	                     .simple_spinner_dropdown_item);
	srBank.setAdapter(dataAdapter);
  }
  
  @Override
  protected int validateForm(){
	int errMessageId = 0;
	Integer paymentDay = Utilities.stringToInteger(etPaymentDay.getText().toString());
	Integer from =  Utilities.stringToInteger(etRememberFrom.getText().toString());
	Integer to =  Utilities.stringToInteger(etRememberTo.getText().toString());
	if(swRememberPayment.isChecked()){
	  if(from < 1 || to < 1){
		errMessageId = R.string.errEmptyPeriod;
	  }
	  if(paymentDay < 1){
		 errMessageId = R.string.errPaymentDay;
	  }
	}
	if(paymentDay < 0 || paymentDay > 31){
		errMessageId = R.string.errPaymentDay;
	} else if((from < 0 || from > 31) || (to < 0 || to > 31)){
		errMessageId = R.string.errMaxPeriod;
	} else if(to < from){
	  errMessageId = R.string.errMaxPeriod;
	} else if((from > 0 && from > paymentDay) || (to > 0 && to < paymentDay)){
		errMessageId = R.string.errRememberConfig;
	}
	return errMessageId;
  }
  
  public void onClickIbSave(View clickedView){
	int errMessageId = 0;
	Intent toNewAssurance = null;
	if((errMessageId = validateForm()) > 0){
	  MessageManager.show(errMessageId, this);
	  return;
	}
	policy.setPaymentInstrumentId(((PaymentInstrument) srPaymentInstrument
			                                 .getSelectedItem()).getId());
	policy.setBankId(((Bank) srBank.getSelectedItem()).getId());
	policy.setComment(etComments.getText().toString());
	policy.setRememberPayment(swRememberPayment.isChecked());
	policy.setPaymentDay(Utilities.parseInt(etPaymentDay.getText().toString()));
	policy.setStartAlertDay(Utilities.parseInt(etRememberFrom.getText().toString()));
	policy.setEndAlertDay(Utilities.parseInt(etRememberTo.getText().toString()));
	toNewAssurance = new Intent(ctx, PolicyDetail.class);
	toNewAssurance.putExtra("policy", policy);
	toNewAssurance.putExtra("isEditable", isEditable);
	toNewAssurance.putExtra("forNew", forNew);
	startActivity(toNewAssurance);
  }
  
  private void setEnabled(boolean enabled){
	srPaymentInstrument.setEnabled(enabled);
	srBank.setEnabled(enabled);
	etPaymentDay.setEnabled(enabled);
	etComments.setEnabled(enabled);
	etRememberFrom.setEnabled(enabled);
	etRememberTo.setEnabled(enabled);
	swRememberPayment.setEnabled(enabled);  
  }
  
}
