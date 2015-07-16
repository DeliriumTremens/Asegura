package com.lorant.mobile.android.ui.view;

import android.widget.ImageView;
import android.widget.TextView;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.ui.AbstractUI;

public class TipDescription extends AbstractUI {
	
  private Integer groupId = 0;
  private ImageView ivLogoType = null;
  private TextView tvText = null;

  public TipDescription(){
	super(R.layout.tips_detail);
  }
		  
  
  @Override
  public void bind(){
	ivLogoType = (ImageView) findViewById(R.id.ivLogoType);
	tvText = (TextView) findViewById(R.id.tvText);
  }

  @Override
  public void init(){
	 groupId = extras.getInt("groupId");
  }
  
  @Override
  public void populate(){
	int textId = 0, imageId = 0, titleBarId = 0;
	switch(groupId){
	   case 1: imageId = R.drawable.assurance_list_icon_car;
	           textId = R.string.tipsCarList;
	           titleBarId = R.string.tipsCar;
	           break;
	   case 2: imageId = R.drawable.assurance_list_icon_home;
	           textId = R.string.tipsHomeList;
	           titleBarId = R.string.tipsHome;
               break;
	   case 3: imageId = R.drawable.assurance_list_icon_life;
	           textId = R.string.tipsLifeList;
	           titleBarId = R.string.tipsLife;
               break;
	   case 4: imageId = R.drawable.assurance_list_icon_medical;
	           textId = R.string.tipsMedicalList;
	           titleBarId = R.string.tipsMedical;
               break;
	   case 5: imageId = R.drawable.assurance_list_icon_education;
	           textId = R.string.tipsEducationList;
	           titleBarId = R.string.tipsEducation;
               break;
	   case 6: imageId = R.drawable.assurance_list_icon_pet;
	           textId = R.string.tipsPetList;
	           titleBarId = R.string.tipsPet;
               break;
	   case 7: imageId = R.drawable.assurance_list_icon_others;
	           textId = R.string.tipsOtherList;
	           titleBarId = R.string.tipsOther;
               break;         
	}
	ivLogoType.setImageResource(imageId);
    tvText.setText(textId);
    setActionBarTitle(titleBarId);
  }
}
