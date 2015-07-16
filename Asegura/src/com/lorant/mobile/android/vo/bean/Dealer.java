package com.lorant.mobile.android.vo.bean;

import com.google.android.gms.maps.model.LatLng;

public class Dealer {
	
  private String name;
  private LatLng latLng;
  private String addres;
  private String zipCode;
  private String phone;
  private String mail;
  
  public String getName() {
	return name;
  }
  public void setName(String name) {
	this.name = name;
  }
  public LatLng getLatLng() {
	return latLng;
  }
  public void setLatLng(LatLng latLng) {
	this.latLng = latLng;
  }
  public void setLatLng(String latLngStr) {
    String [] coordinates = latLngStr.split(",");
    if(coordinates.length == 2){
      latLng = new LatLng(Float.valueOf(coordinates[0])
    		        , Float.valueOf(coordinates[1]) );
    }
  }
  public String getAddres() {
	return addres;
  }
  public void setAddres(String addres) {
	this.addres = addres;
  }
  public String getZipCode() {
	return zipCode;
  }
  public void setZipCode(String zipCode) {
	this.zipCode = zipCode;
  }
  public String getPhone() {
	return phone;
  }
  public void setPhone(String phone) {
	this.phone = phone;
  }
  public String getMail() {
	return mail;
  }
  public void setMail(String mail) {
	this.mail = mail;
  }
  
}
