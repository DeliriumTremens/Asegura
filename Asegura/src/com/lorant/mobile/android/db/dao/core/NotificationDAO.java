package com.lorant.mobile.android.db.dao.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.AbstractDAO;
import com.lorant.mobile.android.vo.bean.Notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import static com.lorant.mobile.android.constant.Literals.*;

public class NotificationDAO extends AbstractDAO<Notification> {
  
  public NotificationDAO (Context ctx){
	super(ctx);
  }
	
  @Override
  public List<Notification> parse(Cursor result){
	List<Notification> notifications = new ArrayList<Notification> ();
	Notification notification = null;
    try {
    	if(result.moveToFirst()){
    	  do{
    		 notification = new Notification();
    		 notification.setId(result.getInt(0));
    		 notification.setName(result.getString(1));
    		 notification.setMonth(result.getInt(2));
    		 notification.setDay(result.getInt(3));
    		 notification.setType(result.getInt(4));
    		 notification.setPolicyId(result.getInt(5));
    		 notifications.add(notification);
    	  } while (result.moveToNext());
    	}	
	} finally {
		result.close();
	}
    return notifications;
  }
  
  @Override
  public ContentValues convert (Notification notification){
    ContentValues values = new ContentValues();
	values.put(Config.DEFAULT_TABLE_ID_FIELD, notification.getId());
    values.put(NAME, notification.getName());
    values.put(MONTH, notification.getMonth());
    values.put(DAY, notification.getDay());
    values.put(TYPE, notification.getType());
    values.put(POLICY_ID, notification.getPolicyId());
	return values;
  }
  
  public void delete(Integer policyId, Integer typeId){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append(" DELETE FROM " + tableName)
	        .append(" WHERE POLICY_ID = ? AND TYPE = ?");
	execute(sqlQuery, new Object[]{policyId, typeId});
  }
  
  public void deleteForType(Integer typeId){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append(" DELETE FROM " + tableName)
		    .append(" WHERE TYPE = ?");
	execute(sqlQuery, new Object[]{typeId});
  }
  
  public void deleteForPolicy(Integer policyId){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append(" DELETE FROM " + tableName)
		    .append(" WHERE POLICY_ID = ?");
	execute(sqlQuery, new Object[]{policyId});
  }
  
  public final List<Notification> findActive(){
	StringBuilder sqlQuery = new StringBuilder ();
	Calendar calendar = new GregorianCalendar();
	sqlQuery.append("SELECT * FROM " + tableName)
	        .append(" WHERE DAY = ? AND (MONTH = ? OR MONTH = 0)");
	return queryForObjectList(sqlQuery, new Object[]{calendar.get(Calendar
			               .DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1});
  }
  
  
}
