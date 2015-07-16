package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class AppConfig extends AbstractVO {

  private static final long serialVersionUID = -3563415091129103583L;
  
  private String name;
  private String value;
  
  public AppConfig(){
  }
  
  public AppConfig(String name, String value){
	this.name = name;
	this.value = value;
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

  public String getValue() {
	return value;
  }

  public void setValue(String value) {
	this.value = value;
  }

}
