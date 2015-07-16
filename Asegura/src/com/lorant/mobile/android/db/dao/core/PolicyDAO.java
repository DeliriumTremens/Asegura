package com.lorant.mobile.android.db.dao.core;

import java.util.ArrayList;
import java.util.List;

import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.AbstractDAO;
import com.lorant.mobile.android.util.Utilities;
import com.lorant.mobile.android.vo.bean.Policy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import static com.lorant.mobile.android.constant.Literals.*;

public class PolicyDAO extends AbstractDAO<Policy> {
  
  public PolicyDAO (Context ctx){
	super(ctx);
  }
	
  @Override
  public List<Policy> parse(Cursor result){
	List<Policy> policies = new ArrayList<Policy> ();
	Policy policy = null;
    try {
    	if(result.moveToFirst()){
    	  do{
    		 policy = new Policy();
    		 policy.setId(result.getInt(0));
    		 policy.setInsuranceNumber(result.getString(1));
    		 policy.setName(result.getString(2));
    		 policy.setPaymentDay(result.getInt(3));
    		 policy.setComment(result.getString(4));
    		 policy.setRemember(Utilities.intToBoolean(result.getInt(5)));
    		 policy.setStartAlertDay(result.getInt(6));
    		 policy.setEndAlertDay(result.getInt(7));
    		 policy.setPaymentInstrumentId(result.getInt(8));
    		 policy.setImageUri(result.getString(9));
    		 policy.setBankId(result.getInt(10));
    		 policy.setRememberPayment(Utilities.intToBoolean(result.getInt(11)));
    		 policy.setPlates(result.getString(12));
    		 policies.add(policy);
    	  } while (result.moveToNext());
    	}	
	} finally {
		result.close();
	}
    return policies;
  }
  
  @Override
  public ContentValues convert (Policy policy){
    ContentValues values = new ContentValues();
	values.put(Config.DEFAULT_TABLE_ID_FIELD, policy.getId());
    values.put(NUMBER, policy.getInsuranceNumber());
    values.put(NAME, policy.getName());
    values.put(PAYMENT_DAY, policy.getPaymentDay());
    values.put(COMMENT, policy.getComment());
    values.put(REMEMBER, policy.getRemember());
    values.put(START_REMEMBER_DAY, policy.getStartAlertDay());
    values.put(END_REMEMBER_DAY, policy.getEndAlertDay());
    values.put(PAYMENT_INSTRUMENTS_ID, policy.getPaymentInstrumentId());
    values.put(IMAGE_URI, policy.getImageUri());
    values.put(BANK_ID, policy.getBankId());
    values.put(REMEMBER_PAYMENT, policy.getRememberPayment());
    values.put(REMEMBER_PAYMENT, policy.getPlates());
	return values;
  }
  
  public Policy findByPolicyNumber(String policyNumber){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append("SELECT * FROM " + tableName + " WHERE NUMBER = ? ");
	return queryForObject(sqlQuery, policyNumber);
  }
  
}
