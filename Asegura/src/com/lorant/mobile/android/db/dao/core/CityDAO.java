package com.lorant.mobile.android.db.dao.core;

import java.util.ArrayList;
import java.util.List;




import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.AbstractDAO;
import com.lorant.mobile.android.vo.bean.City;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import static com.lorant.mobile.android.constant.Literals.*;

public class CityDAO extends AbstractDAO<City> {
  
  public CityDAO (Context ctx){
	super(ctx);
  }
	
  @Override
  public List<City> parse(Cursor result){
	List<City> cities = new ArrayList<City> ();
	City city = null;
    try {
    	if(result.moveToFirst()){
    	  do{
    		 city = new City();
    		 city.setId(result.getInt(0));
    		 city.setName(result.getString(1));
    		 cities.add(city);
    	  } while (result.moveToNext());
    	}	
	} finally {
		result.close();
	}
    return cities;
  }
  
  @Override
  public ContentValues convert (City city){
    ContentValues values = new ContentValues();
	values.put(Config.DEFAULT_TABLE_ID_FIELD, city.getId());
    values.put(NAME, city.getName());
	return values;
  }
  
}
