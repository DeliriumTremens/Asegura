package com.lorant.mobile.android.ui.view;

import android.content.Intent;
import android.view.View;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.ui.AbstractUI;

public class Tips extends AbstractUI {

  public Tips(){
	super(R.layout.tips);
  }
			  
  @Override
  public void populate(){
	setActionBarTitle(R.string.tips);
  }
  
  
  public void onClickTip(View clickedView){
	final int groupId = Config.GROUP_MAPPING.get(clickedView.getTag().toString());
	Intent toTipDescription = new Intent(this, TipDescription.class); 
	toTipDescription.putExtra("groupId", groupId);
	startActivity(toTipDescription);
  }
}
