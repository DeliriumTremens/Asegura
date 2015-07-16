package com.lorant.mobile.android.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.ui.AbstractUI;

public class Quotation extends AbstractUI {

	
  public Quotation(){
	super(R.layout.quotation);
  }
  
  @Override
  public void populate(){
	setActionBarTitle(R.string.quotation);
  }
  
  public void onClickTvPhone(View clickedView) {
	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" 
            + getResources().getString(R.string.quoatationPhone)));
	startActivity(intent);
  }
}
