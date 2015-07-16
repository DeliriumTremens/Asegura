package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class PaymentInstrument extends AbstractVO {
	
  private String name;
  
  public PaymentInstrument(){
  }
  
  public PaymentInstrument(Integer id , String name){
	this.id = id;
	this.name = name;
  }
  
  public String getName() {
	return name;
  }
  public void setName(String name) {
	this.name = name;
  }

  @Override
  public String toString() {
	return name;
  }

}
