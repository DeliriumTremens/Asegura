package com.lorant.mobile.android.ui;

import java.util.ArrayList;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.db.dao.core.AlertDAO;
import com.lorant.mobile.android.db.dao.core.NotificationDAO;
import com.lorant.mobile.android.db.dao.core.PolicyDAO;
import com.lorant.mobile.android.ui.adapter.DrawerListAdapter;
import com.lorant.mobile.android.ui.view.About;
import com.lorant.mobile.android.ui.view.Login;
import com.lorant.mobile.android.ui.view.PolicyList;
import com.lorant.mobile.android.ui.view.CalendarTracker;
import com.lorant.mobile.android.ui.view.Dealers;
import com.lorant.mobile.android.ui.view.Menu;
import com.lorant.mobile.android.ui.view.NotificationsList;
import com.lorant.mobile.android.ui.view.Quotation;
import com.lorant.mobile.android.ui.view.ReportDetail;
import com.lorant.mobile.android.ui.view.Tips;
import com.lorant.mobile.android.util.calligraphy.CalligraphyConfig;
import com.lorant.mobile.android.util.calligraphy.CalligraphyContextWrapper;
import com.lorant.mobile.android.vo.bean.DrawerItem;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class AbstractUI extends Activity {
 
  protected static final String TAG = "LorantAsegura";
  protected DrawerLayout drawerLayout = null;
  protected static RelativeLayout rootView = null;
  private ActionBar actionBar = null;
  protected Context ctx = null;
  protected View loader = null;
  protected Bundle extras = null;
  private static int rootViewResourceId = 0;
  private SharedPreferences sharedPreferences = null;
  protected boolean isRestarted = false;
  
  //public abstract View getLayout(LayoutInflater inflater, ViewGroup container);
  
  public void bind() {}
  public void populate() {}
  public void init(){}
  public void setBehavior(){}
  protected int validateForm(){
	return 0;
  }
	
  static{
	     CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
	                          .setDefaultFontPath("fonts/Oswald-Regular.ttf")
	                          .setFontAttrId(R.attr.fontPath)
	                          .build()
	     );
  }
	
  public AbstractUI(int rootViewResourceId){
	this();
	AbstractUI.rootViewResourceId = rootViewResourceId;
  }
  
  public AbstractUI(){
    super();
    ctx = this;
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.container);
	boolean isActiveDrawer = isActiveDrawer();
	isRestarted = false;
	loader = findViewById(R.id.loader);
	extras = getIntent().getExtras();
	sharedPreferences = getSharedPreferences(Config.PREFS_NAME, MODE_PRIVATE);
	ContentManager contentManager = new ContentManager();
	setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	getFragmentManager().beginTransaction().replace(R.id.content_frame
			                               , contentManager).commit();
	getFragmentManager().executePendingTransactions();
	drawerLayout = (DrawerLayout) findViewById(R.id.container);
	setActionBar(isActiveDrawer);
    if(isActiveDrawer){
       createMenu();
    } else {
    	drawerLayout.removeView(drawerLayout.findViewById(R.id.drawer));
    }
    init();
  }
  
  @Override
  public void onStart(){
	super.onStart();
	if(! isRestarted){
	  bind();
	  populate();
	  setBehavior();
	}
  }
  
  @Override
  protected void onRestart() {
    super.onRestart();
    isRestarted = true;
  }
  
  public View getLoader(){
	return loader;
  }
  
  protected void setActionButtonBackground(int resId){
	ImageButton actionButton = null;
	if(actionBar != null){
	  actionButton = (ImageButton) actionBar.getCustomView().findViewById(R.id
			                                                    .imgBnAction);
	  actionButton.setBackgroundResource(resId);
	}
  }
  
  protected void setActionBarTitle(Integer titleResId){
	TextView tv = null;
	if(actionBar != null){
	  tv = (TextView) actionBar.getCustomView().findViewById(R.id.tvTitle);
	  tv.setText(titleResId);
	}
  }
  
  public void onToggleButtonClickEvent(View v) {
    if(drawerLayout.isDrawerOpen(Gravity.START)){
      drawerLayout.closeDrawer(Gravity.START);
    } else {
    	drawerLayout.openDrawer(Gravity.START);
    }
  }
  
  public void onActionButtonClickEvent(View v) {
  }
  
  private void setActionBar(boolean isActiveDrawer){
	View view = null;
	actionBar = getActionBar();
	if(actionBar != null){
  	  actionBar.setDisplayShowHomeEnabled(false); 
  	  actionBar.setDisplayShowCustomEnabled(true); 
  	  actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setCustomView(R.layout.common_action_bar);
      drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
      if(! isActiveDrawer){
    	view = actionBar.getCustomView();
    	((RelativeLayout)view.findViewById(R.id.rlActionBar)).removeView(view
    			                            .findViewById(R.id.imgBnToggle));
      }
	}
  }
  
  private void createMenu(){
	String[] tagTitles = getResources().getStringArray(R.array.MenuTags);
	ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();
	ListView drawerList = (ListView) findViewById(R.id.drawer);
	items.add(new DrawerItem(tagTitles[0],R.drawable.drawer_icon_home));
	items.add(new DrawerItem(tagTitles[1],R.drawable.drawer_icon_assurance));
	items.add(new DrawerItem(tagTitles[2],R.drawable.drawer_icon_report));
	items.add(new DrawerItem(tagTitles[3],R.drawable.drawer_icon_dealers));
	items.add(new DrawerItem(tagTitles[4],R.drawable.drawer_icon_calendar));
	items.add(new DrawerItem(tagTitles[5],R.drawable.drawer_icon_notifications));
	items.add(new DrawerItem(tagTitles[6],R.drawable.drawer_icon_quote));
	items.add(new DrawerItem(tagTitles[7],R.drawable.drawer_icon_tips));
	items.add(new DrawerItem(tagTitles[8],R.drawable.drawer_icon_about));
	items.add(new DrawerItem(tagTitles[9],R.drawable.drawer_icon_exit));
	drawerList.setAdapter(new DrawerListAdapter(this, items)); 
	drawerList.setOnItemClickListener(new DrawerItemClickListener());
	drawerList.bringToFront();
	drawerList.requestLayout();
  }
  
  private Boolean isActiveDrawer(){
	Boolean isActiveMenu = false;
    ActivityInfo ai = null;
	try {
		ai = getPackageManager().getActivityInfo(getComponentName()
				  , PackageManager.GET_ACTIVITIES|PackageManager.GET_META_DATA);
		if(ai.metaData != null){
		  isActiveMenu = ai.metaData.getBoolean(Config.DRAWER_META_DATA_NAME);
		}
	} catch(NameNotFoundException ignored){}
	return isActiveMenu;
  }
  
  public static class ContentManager extends Fragment {
	 public ContentManager(){
		 
	 }
	 
	 @Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                                           Bundle savedInstanceState) {
		 return inflater.inflate(rootViewResourceId, container, false);
	 }
	 
	 @Override
	 public void onViewCreated(View view, Bundle savedInstanceState){
		 rootView = (RelativeLayout) view;
	 }
	 
  }
  
  private class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
      switch(position){
        case 0: startActivity(new Intent(v.getContext(), Menu.class)); 
                    break;
        case 1: startActivity(new Intent(v.getContext(), PolicyList.class)); 
                break;
        case 2: startActivity(new Intent(v.getContext(), ReportDetail.class)); 
                break;
        case 3: startActivity(new Intent(v.getContext(), Dealers.class)); 
                break;
        case 4: startActivity(new Intent(v.getContext(), CalendarTracker.class)); 
                break;
        case 5: startActivity(new Intent(v.getContext(), NotificationsList.class)); 
                break;
        case 6: startActivity(new Intent(v.getContext(), Quotation.class)); 
                break;
        case 7: startActivity(new Intent(v.getContext(), Tips.class)); 
                break;
        case 8: startActivity(new Intent(v.getContext(), About.class)); 
                break;
        case 9: new PolicyDAO(ctx).truncate();
                new AlertDAO(ctx).truncate();
                new NotificationDAO(ctx).truncate();
                new PolicyDAO(ctx).truncate();
                setStringParam(Config.PREF_USERNAME, null);
        		setStringParam(Config.PREF_PHONE, null); 
                startActivity(new Intent(v.getContext(), Login.class)); 
                break;                
      }
      drawerLayout.closeDrawer(Gravity.START);
    }
  }
  
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }
  
  protected void setFormBehavior(RelativeLayout rootView){
	int rootCounter = rootView.getChildCount();
	int parentCounter = 0;
	View parent = null, child = null;
	for(int i = 0; i <= rootCounter; i++) {
	  parent = rootView.getChildAt(i);
	  if(parent instanceof RelativeLayout) {
		parentCounter = ((RelativeLayout) parent).getChildCount();
	    for(int j = 0; j <= parentCounter; j++) {
		  child = ((RelativeLayout) parent).getChildAt(j);
		  if(child instanceof EditText) {
			child.setOnFocusChangeListener(new OnFocusChangeListener(){
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
				  if(hasFocus){
					v.setBackgroundResource(0);
					((View)v.getParent()).setBackgroundResource(R.color
							    		                   .themeWhite);
		          } else {
					  v.setBackgroundColor(Color.TRANSPARENT);
					  ((View)v.getParent()).setBackgroundResource(R.color
										                 .themeLightGrey);
				  }
				}
			 });	 
		   }
		 }
	   }
	 }
   }
  
    protected String getStringParam(String name){
	  return sharedPreferences.getString(name, null);
    }
    protected Integer getIntParam(String name){
     return Integer.valueOf(sharedPreferences.getString(name, "0"));
    }
    protected void setStringParam(String name, String value){
	  sharedPreferences.edit().putString(name, value).commit();
    }
    protected void setIntParam(String name, Integer value){
	  sharedPreferences.edit().putInt(name, value).commit();
    }
    
  protected void showCaptureImageDialog(){
   	AlertDialog.Builder getImageFrom = null;
   	CharSequence[] opsChars = {getResources().getString(R.string.camera)
   			                , getResources().getString(R.string.gallery)};
   	getImageFrom = new AlertDialog.Builder(this);
   	getImageFrom.setItems(opsChars, new OnClickListener(){
   	  @Override
   	  public void onClick(DialogInterface dialog, int which) {
   		Intent imageIntent = null;
   	    if(which == 0){
   	      imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
   	      startActivityForResult(imageIntent, Config.ACTIVITY_RESULT_PHOTO);
   	    } else if(which == 1){
   	        imageIntent = new Intent(Intent.ACTION_PICK);
   	        imageIntent.setType("image/*");
   	        startActivityForResult(imageIntent, Config.ACTIVITY_RESULT_GALLERY);
   	    }
   	    dialog.dismiss();
   	  }
   	});
   	getImageFrom.show();
  }
}
