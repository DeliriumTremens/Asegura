package com.lorant.mobile.android.vo.bean;

import java.util.Date;

import com.lorant.mobile.android.vo.AbstractVO;

public class Historical extends AbstractVO {
	
  private static final long serialVersionUID = 1314808661336703783L;
	
  private Integer policyId;
  private String policyNumber;
  private String sinesterType;
  private String phone;
  private Float latitude;
  private Float longitude;
  private String information;
  private Date startDate;
  private Date eventDate;
  private Date registrationDate;
  private String registrationTime;
  
  public Integer getPolicyId() {
	return policyId;
  }
  public void setPolicyId(Integer policyId) {
	this.policyId = policyId;
  }
  public String getPolicyNumber() {
	return policyNumber;
  }
  public void setPolicyNumber(String policyNumber) {
	this.policyNumber = policyNumber;
  }
  public String getSinesterType() {
	return sinesterType;
  }
  public void setSinesterType(String sinesterType) {
	this.sinesterType = sinesterType;
  }
  public String getPhone() {
	return phone;
  }
  public void setPhone(String phone) {
	this.phone = phone;
  }
  public Float getLatitude() {
	return latitude;
  }
  public void setLatitude(Float latitude) {
	this.latitude = latitude;
  }
  public Float getLongitude() {
	return longitude;
  }
  public void setLongitude(Float longitude) {
	this.longitude = longitude;
  }
  public String getInformation() {
	return information;
  }
  public void setInformation(String information) {
	this.information = information;
  }
  public Date getStartDate() {
	return startDate;
  }
  public void setStartDate(Date startDate) {
	this.startDate = startDate;
  }
  public Date getEventDate() {
	return eventDate;
  }
  public void setEventDate(Date eventDate) {
	this.eventDate = eventDate;
  }
  public Date getRegistrationDate() {
	return registrationDate;
  }
  public void setRegistrationDate(Date registrationDate) {
	this.registrationDate = registrationDate;
  }
  public String getRegistrationTime() {
	return registrationTime;
  }
  public void setRegistrationTime(String registrationTime) {
	this.registrationTime = registrationTime;
  }
	
}
