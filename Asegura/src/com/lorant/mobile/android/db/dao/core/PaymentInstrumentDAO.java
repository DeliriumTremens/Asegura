package com.lorant.mobile.android.db.dao.core;

import java.util.ArrayList;
import java.util.List;




import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.AbstractDAO;
import com.lorant.mobile.android.vo.bean.PaymentInstrument;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import static com.lorant.mobile.android.constant.Literals.*;

public class PaymentInstrumentDAO extends AbstractDAO<PaymentInstrument> {
  
  public PaymentInstrumentDAO (Context ctx){
	super(ctx);
  }
	
  @Override
  public List<PaymentInstrument> parse(Cursor result){
	List<PaymentInstrument> paymentInstruments = new ArrayList<PaymentInstrument> ();
	PaymentInstrument paymentInstrument = null;
    try {
    	if(result.moveToFirst()){
    	  do{
    		 paymentInstrument = new PaymentInstrument();
    		 paymentInstrument.setId(result.getInt(0));
    		 paymentInstrument.setName(result.getString(1));
    		 paymentInstruments.add(paymentInstrument);
    	  } while (result.moveToNext());
    	}	
	} finally {
		result.close();
	}
    return paymentInstruments;
  }
  
  @Override
  public ContentValues convert (PaymentInstrument paymentInstrument){
    ContentValues values = new ContentValues();
	values.put(Config.DEFAULT_TABLE_ID_FIELD, paymentInstrument.getId());
    values.put(NAME, paymentInstrument.getName());
	return values;
  }
  
}
