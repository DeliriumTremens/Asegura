package com.lorant.mobile.android.db.dao.core;

import java.util.ArrayList;
import java.util.List;






import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.AbstractDAO;
import com.lorant.mobile.android.vo.bean.AppConfig;
import com.lorant.mobile.android.vo.bean.Policy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import static com.lorant.mobile.android.constant.Literals.*;

public class ConfigDAO extends AbstractDAO<AppConfig> {
  
  public ConfigDAO (Context ctx){
	super(ctx);
  }
	
  @Override
  public List<AppConfig> parse(Cursor result){
	List<AppConfig> appConfigs = new ArrayList<AppConfig> ();
	AppConfig appConfig = null;
    try {
    	if(result.moveToFirst()){
    	  do{
    		 appConfig = new AppConfig();
    		 appConfig.setId(result.getInt(0));
    		 appConfig.setName(result.getString(1));
    		 appConfig.setValue(result.getString(2));
    		 appConfigs.add(appConfig);
    	  } while (result.moveToNext());
    	}	
	} finally {
		result.close();
	}
    return appConfigs;
  }
  
  @Override
  public ContentValues convert (AppConfig appConfig){
    ContentValues values = new ContentValues();
	values.put(Config.DEFAULT_TABLE_ID_FIELD, appConfig.getId());
    values.put(NAME, appConfig.getName());
    values.put(VALUE, appConfig.getValue());
	return values;
  }
  
  public AppConfig find(String name){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append("SELECT * FROM " + tableName + " WHERE NAME = ? ");
	return queryForObject(sqlQuery, name);
  }
  
}
