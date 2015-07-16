package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class Notification extends AbstractVO {
	
  private static final long serialVersionUID = 6400404686028548203L;
  
  private String name;
  private Integer month;
  private Integer day;
  private Integer type;
  private Integer policyId;
  
  public String getName() {
	return name;
  }
  public void setName(String name) {
	this.name = name;
  }
  public Integer getMonth() {
	return month;
  }
  public void setMonth(Integer month) {
	this.month = month;
  }
  public Integer getDay() {
	return day;
  }
  public void setDay(Integer day) {
	this.day = day;
  }
  public Integer getType() {
	return type;
  }
  public void setType(Integer type) {
	this.type = type;
  }
  public Integer getPolicyId() {
	return policyId;
  }
  public void setPolicyId(Integer policyId) {
	this.policyId = policyId;
  }
  
}
