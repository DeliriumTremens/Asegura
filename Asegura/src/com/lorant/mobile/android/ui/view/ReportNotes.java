package com.lorant.mobile.android.ui.view;

import android.content.Intent;
import android.widget.EditText;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.vo.bean.Report;

public class ReportNotes extends AbstractUI {
	
  private EditText etInformation = null;
  
  private Report report = null;

  public ReportNotes(){
	super(R.layout.report_notes);
  }
  
  @Override
  public void onBackPressed() {
	Intent toReport = new Intent(this, ReportDetail.class);
	report.setInformation(etInformation.getText().toString());
	toReport.putExtra("report", report);
	startActivity(toReport);
  }
  
  @Override
  public void bind(){
	etInformation = (EditText) rootView.findViewById(R.id.etInformation);
  }
  
  @Override
  public void init(){  
	report = (Report) extras.get("report");
  }
  
  @Override
  public void populate(){
	etInformation.setText(report.getInformation());  
  }
}
