package com.lorant.mobile.android.ui.view;

import android.webkit.WebView;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.ui.AbstractUI;

public class About extends AbstractUI {

  private WebView wbAbout = null;
  
  public About(){
    super(R.layout.about);
  }
	
  @Override
  public void bind(){
	wbAbout = (WebView) rootView.findViewById(R.id.wbAbout);
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.about);
	wbAbout.getSettings().setJavaScriptEnabled(true);
	wbAbout.loadUrl("http://www.lorantms.com/");
  }
}
