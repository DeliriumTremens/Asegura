package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class Feature extends AbstractVO {
	
  private static final long serialVersionUID = -2149699459203490186L;
  
  private String name;
  private String value;
  
  public Feature(){
  }
  
  public Feature(String name, String value){
	this.name = name;
	this.value = value;
  }
  
  public String getName() {
	return name;
  }
  public void setName(String name) {
	this.name = name;
  }
  public String getValue() {
	return value;
  }
  public void setValue(String value) {
	this.value = value;
  }

}
