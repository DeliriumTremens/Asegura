package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class City extends AbstractVO {
	
  private String name;
  
  public City(){
  }
  
  public City(Integer id , String name){
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
