package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class Company extends AbstractVO{
	
  private String name;
  private Integer groupId;
  
  public Company(){
  }
  
  public Company(Integer id, String name){
	this.id = id;
	this.name = name;
  }
  
  public String getName() {
	return name;
  }
  public void setName(String name) {
	this.name = name;
  }
  public Integer getGroupId() {
	return groupId;
  }
  public void setGroupId(Integer groupId) {
	this.groupId = groupId;
  }
  
  @Override
  public String toString() {
	return name;
  }
  
}
