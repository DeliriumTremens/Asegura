package com.lorant.mobile.android.constant;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.graphics.Color;
import android.os.Environment;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.db.dao.core.AlertDAO;
import com.lorant.mobile.android.db.dao.core.BankDAO;
import com.lorant.mobile.android.db.dao.core.CityDAO;
import com.lorant.mobile.android.db.dao.core.ConfigDAO;
import com.lorant.mobile.android.db.dao.core.NotificationDAO;
import com.lorant.mobile.android.db.dao.core.PaymentInstrumentDAO;
import com.lorant.mobile.android.db.dao.core.PolicyDAO;
import com.lorant.mobile.android.vo.bean.InsuranceGroup;
import com.lorant.mobile.android.vo.bean.Verification;

public class Config {
	
  public static final Integer SPLASH_PERIOD = 3000;
  public static final Integer PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  public static final String BASE_LOCAL_PATH = Environment.getExternalStorageDirectory() + "/.asegura";
  public static final String IMG_LOCAL_PATH = BASE_LOCAL_PATH + "/img";
  public static final String PUSH_SENDER_ID = "282531411798";
  public static final String DRAWER_META_DATA_NAME = "hasLateralMenu";
  public static final String WS_BASE_PATH = "https://grupo.lmsmexico.com.mx/wsmovil/api/poliza/";
  public static final String WS_CODE_NAME = "ErrorCode";
  public static final String WS_OK_CODE = "ER0001";
  public static final String PREFS_NAME = "AseguraPrefs";
  public static final String PREF_USERNAME = "userName";
  public static final String PREF_PHONE = "phoneNumber";
  public static final String PREF_USER_ID = "userId";
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
  public static final SimpleDateFormat DATE_FORMAT_ALT = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
  public static final SimpleDateFormat DATE_FORMAT_HISTORICAL = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
  public static final List<String> FB_PERMISSIONS = Arrays.asList("public_profile","email");
  public static final String QR_PACKAGE = "com.google.zxing.client.android.SCAN";
  public static final String QR_APP_MARKET = "market://details?id=com.google.zxing.client.android";
  public static final int ACTIVITY_RESULT_GALLERY = 1;
  public static final int ACTIVITY_RESULT_PHOTO = 2;
  public static final int ACTIVITY_FACEBOOK_CB = 64206;
  public static final int ACTIVITY_CUSTOM_CB = 3;
  public static final int MAX_IMAGE_SIZE = 180;
  public final static String SQL_SCRIPT_SEPARATOR = "--sentence";
  public final static String DEFAULT_TABLE_ID_FIELD = "_ID";
  public static final String DATABASE_NAME = "lorantDB";
  public static final String LORANT_SELLER_NAME = "LORANTMMS";
  public static final String DATABASE_SCRIPT_CREATE_LOCATION = "db/db_create.sql";
  public static final String REMEMBER_VERIFICATION_PARAM_NAME = "rememberVerification";
  public static final int DATABASE_VERSION = 1;
  public static final int EXPIRY_REMINDER = 5;
  
  public static final int ACTIVITY_PHOTOGRAPHY_CB = 1;
  public static final int ACTIVITY_QR_CB = 2;
  
  @SuppressWarnings("serial")
  public static final Map<Class <?> , String> DAO_TABLE_MAPPING = new HashMap<Class <?>, String>() {{
	                                                put(CityDAO.class, "CITIES");
	                                                put(BankDAO.class, "BANKS");
	                                                put(PaymentInstrumentDAO.class, "PAYMENT_INSTRUMENTS");
	                                                put(PolicyDAO.class, "POLICIES");
	                                                put(ConfigDAO.class, "CONFIG");
	                                                put(AlertDAO.class, "ALERTS");
	                                                put(NotificationDAO.class, "NOTIFICATIONS");
  }};
  
  @SuppressWarnings("serial")
  public static final Map<String , Integer> GROUP_MAPPING = new HashMap<String , Integer>() {{
	                                put("car", 1);
	                                put("home", 2);
	                                put("life", 3);
	                                put("medical", 4);
	                                put("education", 5);
	                                put("pet", 6);
	                                put("other", 7);
  }};

  public static final List<InsuranceGroup> INSURANCE_GROUP_MAPPING = Arrays.asList(
          new InsuranceGroup(1, "car", R.drawable.assurance_list_icon_car)
        , new InsuranceGroup(2, "home", R.drawable.assurance_list_icon_home)
        , new InsuranceGroup(3, "life", R.drawable.assurance_list_icon_life)
        , new InsuranceGroup(4, "medical", R.drawable.assurance_list_icon_medical)
        , new InsuranceGroup(5, "education", R.drawable.assurance_list_icon_education)
        , new InsuranceGroup(6, "pet", R.drawable.assurance_list_icon_pet)
        , new InsuranceGroup(7, "other", R.drawable.assurance_list_icon_others)
  );
  
  @SuppressWarnings("serial")
  public static final Map<Integer , Verification> VERIFICATION = new HashMap<Integer , Verification>() {{
	          put(1, new Verification(Color.GREEN, 4, 10));
	          put(2, new Verification(Color.GREEN,4, 10));
	          put(3, new Verification(Color.RED, 3 , 9));
	          put(4, new Verification(Color.RED, 3, 9));
	          put(5, new Verification(Color.YELLOW, 1, 7));
	          put(6, new Verification(Color.YELLOW, 1, 7));
	          put(7, new Verification(R.color.themePink, 2, 8));
	          put(8, new Verification(R.color.themePink, 2, 8));
	          put(9, new Verification(Color.BLUE, 5, 11));
	          put(0, new Verification(Color.BLUE, 5, 11));
  }};
  
  public static enum ALERT_TYPES {
	VERIFICATION(1), PAYMENT(2), EXPIRY(3);
	
	private Integer id;
	    
	private ALERT_TYPES(Integer id) {
	  this.id = id;
	}
	 
	public Integer getId() {
	  return id;
	}
  }
}