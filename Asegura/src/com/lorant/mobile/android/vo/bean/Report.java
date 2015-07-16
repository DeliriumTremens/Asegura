package com.lorant.mobile.android.vo.bean;

import android.net.Uri;

import com.lorant.mobile.android.vo.AbstractVO;

public class Report extends AbstractVO {

  private static final long serialVersionUID = -149100192016634354L;
  
  private Integer policyId = null;
  private String phone = null;
  private String information = null;
  private Integer causeId = null;
  private Uri imageUri = null;
  
  public Integer getPolicyId() {
	return policyId;
  }
  public void setPolicyId(Integer policyId) {
	this.policyId = policyId;
  }
  public String getPhone() {
	return phone;
  }
  public void setPhone(String phone) {
	this.phone = phone;
  }
  public String getInformation() {
	return information;
  }
  public void setInformation(String information) {
	this.information = information;
  }
  public Integer getCauseId() {
	return causeId;
  }
  public void setCauseId(Integer causeId) {
	this.causeId = causeId;
  }
  public Uri getImageUri() {
 	return imageUri;
  }
  public void setImageUri(Uri imageUri) {
	this.imageUri = imageUri;
  }
  
}
