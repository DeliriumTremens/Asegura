package com.lorant.mobile.android.db.dao.core;

import java.util.ArrayList;
import java.util.List;

import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.AbstractDAO;
import com.lorant.mobile.android.vo.bean.Alert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import static com.lorant.mobile.android.constant.Literals.*;

public class AlertDAO extends AbstractDAO<Alert> {
  
  public AlertDAO (Context ctx){
	super(ctx);
  }
	
  @Override
  public List<Alert> parse(Cursor result){
	List<Alert> alerts = new ArrayList<Alert> ();
	Alert alert = null;
    try {
    	if(result.moveToFirst()){
    	  do{
    		 alert = new Alert();
    		 alert.setId(result.getInt(0));
    		 alert.setPolicyId(result.getInt(1));
    		 alert.setEventId(result.getLong(2));
    		 alert.setTypeId(result.getInt(3));
    		 alerts.add(alert);
    	  } while (result.moveToNext());
    	}	
	} finally {
		result.close();
	}
    return alerts;
  }
  
  @Override
  public ContentValues convert (Alert alert){
    ContentValues values = new ContentValues();
	values.put(Config.DEFAULT_TABLE_ID_FIELD, alert.getId());
    values.put(POLICY_ID, alert.getPolicyId());
    values.put(EVENT_ID, alert.getEventId());
    values.put(TYPE_ID, alert.getTypeId());
	return values;
  }
  
  public List<Alert> findByType(Integer typeId){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append("SELECT * FROM " + tableName)
	        .append(" WHERE TYPE_ID = ?");
	return queryForObjectList(sqlQuery, typeId);
  }
  
  public List<Alert> findByPolicy(Integer policyId){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append("SELECT * FROM " + tableName)
	             .append(" WHERE POLICY_ID = ?");
	return queryForObjectList(sqlQuery, policyId);
  }
  
  public Alert find(Integer policyId, Integer typeId){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append("SELECT * FROM " + tableName)
	        .append(" WHERE POLICY_ID = ? AND TYPE_ID = ?");
	return queryForObject(sqlQuery, policyId, typeId); 
  }
  
  public void deleteForPolicy(Integer policyId){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append(" DELETE FROM " + tableName)
	        .append(" WHERE POLICY_ID = ?");
	execute(sqlQuery, new Object[]{policyId});
  }
  
}
