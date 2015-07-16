package com.lorant.mobile.android.db.dao.core;

import java.util.ArrayList;
import java.util.List;




import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.AbstractDAO;
import com.lorant.mobile.android.vo.bean.Bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import static com.lorant.mobile.android.constant.Literals.*;

public class BankDAO extends AbstractDAO<Bank> {
  
  public BankDAO (Context ctx){
	super(ctx);
  }
	
  @Override
  public List<Bank> parse(Cursor result){
	List<Bank> banks = new ArrayList<Bank> ();
	Bank bank = null;
    try {
    	if(result.moveToFirst()){
    	  do{
    		 bank = new Bank();
    		 bank.setId(result.getInt(0));
    		 bank.setName(result.getString(1));
    		 banks.add(bank);
    	  } while (result.moveToNext());
    	}	
	} finally {
		result.close();
	}
    return banks;
  }
  
  @Override
  public ContentValues convert (Bank bank){
    ContentValues values = new ContentValues();
	values.put(Config.DEFAULT_TABLE_ID_FIELD, bank.getId());
    values.put(NAME, bank.getName());
	return values;
  }
  
}
