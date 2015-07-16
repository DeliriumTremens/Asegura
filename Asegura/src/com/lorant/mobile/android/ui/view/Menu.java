package com.lorant.mobile.android.ui.view;

import android.content.Intent;
import android.view.View;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.ui.AbstractUI;

public class Menu extends AbstractUI {

  public Menu(){
	 super(R.layout.menu);
  }
  
  @Override
  public void onBackPressed() {
    return;
  }

  
  public void onClickIbReport(View clickedView){
	startActivity(new Intent(this, ReportDetail.class));
  }
  
  public void onClickIbAssurance(View clickedView){
	startActivity(new Intent(this, PolicyList.class));
  }
  
  public void onClickIbDealers(View clickedView){
	startActivity(new Intent(this, Dealers.class));
  }
  
  public void onClickIbNotifications(View clickedView){
	startActivity(new Intent(this, NotificationsList.class));
  }
  
  public void onClickIbCalendar(View clickedView){
	startActivity(new Intent(this, CalendarTracker.class));
  }
  
  public void onClickIbAbout(View clickedView){
	startActivity(new Intent(this, About.class));
  }
  
  public void onClickIbQuotation(View clickedView){
	startActivity(new Intent(this, Quotation.class));
  }
  
  public void onClickIbTips(View clickedView){
    startActivity(new Intent(this, Tips.class));
  }
}
