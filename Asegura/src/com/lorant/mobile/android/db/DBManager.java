package com.lorant.mobile.android.db;

import java.io.InputStreamReader;
import java.util.Scanner;





import com.lorant.mobile.android.constant.Config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
	
  private static final String LOG_TAG = DBManager.class.getSimpleName();
  private Context ctx = null;
	  
  public DBManager (Context context) {
	super(context, Config.DATABASE_NAME , null, Config.DATABASE_VERSION);
	Log.d(LOG_TAG, "New DBOpenHelper Instance:");
	ctx = context;
  }
    
  @Override
  public void onCreate(SQLiteDatabase db) {
	Scanner reader = null;
	String sentence = null;
	try {
		reader = new Scanner(new InputStreamReader((ctx.getAssets().open(Config
		                      .DATABASE_SCRIPT_CREATE_LOCATION, 0)), "ISO-8859-1"));
        reader.useDelimiter(Config.SQL_SCRIPT_SEPARATOR);
        while(reader.hasNext()) {
          sentence = reader.next();
          Log.d(LOG_TAG, "Running script : " + sentence);
          db.execSQL(sentence);
        }
        reader.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
  }
	
  @Override
  public void onOpen(SQLiteDatabase db){
	Log.d(LOG_TAG, "Opening Data Base "  + Config.DATABASE_NAME);
	try {
		 super.onOpen(db);
	} catch (Exception e){
		 Log.e(LOG_TAG, "DataBase fatal error", e);
		 System.exit(0);
	}
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
		  
}
