package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class Verification extends AbstractVO {

  private static final long serialVersionUID = 5396017396175424843L;
  
  private int color;
  private Integer firstPeriodMonth;
  private Integer secondPeriodMonth;
  private String name;
  private Object tag;
  
  public Verification(int color, Integer firstPeriodMonth, Integer secondPeriodMonth){
	this.color = color;
	this.firstPeriodMonth = firstPeriodMonth;
	this.secondPeriodMonth = secondPeriodMonth;
  }

  public int getColor() {
	return color;
  }

  public void setColor(int color) {
	this.color = color;
  }

  public Integer getFirstPeriodMonth() {
	return firstPeriodMonth;
  }

  public void setFirstPeriodMonth(Integer firstPeriodMonth) {
	this.firstPeriodMonth = firstPeriodMonth;
  }

  public Integer getSecondPeriodMonth() {
	return secondPeriodMonth;
  }

  public void setSecondPeriodMonth(Integer secondPeriodMonth) {
	this.secondPeriodMonth = secondPeriodMonth;
  }

  public String getName() {
	return name;
  }

  public void setName(String name) {
	this.name = name;
  }

  public Object getTag() {
	return tag;
  }

  public void setTag(Object tag) {
	this.tag = tag;
  }
  
}
