package com.lorant.mobile.android.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;







import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.DBManager;
import com.lorant.mobile.android.vo.AbstractVO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static com.lorant.mobile.android.constant.Literals.*;

public abstract class AbstractDAO <T extends AbstractVO> {
	
  public abstract List<T> parse (Cursor result);
  public abstract ContentValues convert(T vo);
	  
  private static SQLiteDatabase db;
  protected String tableName = null;
		  
  public AbstractDAO(Context ctx){
	tableName = Config.DAO_TABLE_MAPPING.get(this.getClass());
	if(db == null){
	  db = new DBManager(ctx).getWritableDatabase();
	}
  }

  public Map<String, String> parse (Cursor result, int key, int value){
	Map<String, String> map = new HashMap<String,String>();
	try{
	    if(result.moveToFirst()) {
		  do{
			 map.put(result.getString(key), result.getString(value));
		  } while (result.moveToNext());
		}
	} catch(Exception e){
	    e.printStackTrace();
	} finally {
		result.close();
	}
	return map;  
  }
		  
  public final T find(Integer id){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append("SELECT * FROM " + tableName + " WHERE _ID = ? ");
	return queryForObject(sqlQuery, id);
  }
	  
  public final void truncate(){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append("DELETE FROM " + tableName);
	execute(sqlQuery);
  }
	  
  public final List<T> getAll(){
	StringBuilder sqlQuery = new StringBuilder ();	  
	sqlQuery.append("SELECT * FROM " + tableName);
	return queryForObjectList(sqlQuery);
  }
	  
  public final T parseForUnique (Cursor result){
	List<T> list = (List<T>) parse(result);
	T vo = null;
	if(list.size() > 0){
	   	vo = list.get(0);
	} 
	return vo;
  }  
		  
  protected final List <Integer> parseIDs (Cursor result){
	List<Integer> ids = new ArrayList<Integer> ();
	try{
	    if(result.moveToFirst()) {
		  do{
			 ids.add(result.getInt(0));  
		  } while (result.moveToNext());
		}
	} catch(Exception e){
	    e.printStackTrace();
	} finally {
		result.close();
	}
    return ids;
  }  
		  
  protected final Integer parseForUniqueInteger(Cursor result){
	Integer value = null;
	try{
		if(result.moveToFirst()){
		  value = result.getInt(0);
	    }
	} catch(Exception e){
		e.printStackTrace();
	}  finally {
		result.close();
	}
	return value;
  }
		  
  public final Integer getNewID(){
	StringBuilder sqlQuery = new StringBuilder("SELECT MAX(" + Config
			    .DEFAULT_TABLE_ID_FIELD + ") + 1 FROM " + tableName);
	Integer newId = null;
	try {
		 newId = queryForInt(sqlQuery);
		 if(newId == 0){
		   newId = 1;
		 }
	} catch(Exception e){
		e.printStackTrace();
	}  
	return newId;
  } 
		  
  public final void insert(T vo){
	if(vo.getId() == null || vo.getId() < 0){
	  vo.setId(getNewID());	
	}
	db.insertOrThrow(tableName, null, convert(vo));
  }
		  
  public final void upsert(T vo){
	if((vo.getId() == null) || (find(vo.getId()) == null)){
	  insert(vo);
	} else{
	  update(vo);
	}
  }
			  
  public final void update (T vo){
	ContentValues values = convert(vo);
	db.update(tableName, values, Config.DEFAULT_TABLE_ID_FIELD + " = " 
	               + values.get(Config.DEFAULT_TABLE_ID_FIELD), null);
  }
								  
  public final void delete(Integer id){
	db.delete(tableName, Config.DEFAULT_TABLE_ID_FIELD + " = " + id, null);
  } 
		  
  protected final void execute(StringBuilder sqlQuery, Object ... params) { 
	db.execSQL(sqlQuery.toString(), parseParams(params));
  }
		  
  private final Cursor query(StringBuilder sqlQuery, Object ... params){
	Cursor result = null;  
	result = db.rawQuery(sqlQuery.toString(), parseParams(params));
	return result;
  }
		  
  protected final T queryForObject(StringBuilder sqlQuery, Object ... params) {
	return parseForUnique(query(sqlQuery, params));
  }
		  
  protected final Integer queryForInt(StringBuilder sqlQuery
				                      , Object ... params) {
	return parseForUniqueInteger(query(sqlQuery, params));
  }
		  
  protected final List<Integer> queryForIntList(StringBuilder sqlQuery
		                                        , Object ... params) {
	return parseIDs(query(sqlQuery, params));
  }
		  
  protected final List<T> queryForObjectList(StringBuilder sqlQuery
				                             , Object ... params) {
	return parse(query(sqlQuery, params));
  }
		  
  protected final Map<String, String> queryForMap(StringBuilder sqlQuery
				    , int keyIndex, int valueIndex, Object ... params) {
	return parse(query(sqlQuery, params), keyIndex, valueIndex);
  }
		  
  private String [] parseParams(Object [] params){
	List<String> strParams = new ArrayList<String>(); 
	if(params != null){
	  for(Object param : params){
		if(param == null){
		  strParams.add(STR_EMPTY);
		} else {
		  strParams.add(param.toString());
		}
	  }
	}
	return strParams.toArray(new String[]{});
  }

 	
}
