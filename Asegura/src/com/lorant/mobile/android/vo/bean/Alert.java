package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class Alert extends AbstractVO {
	
  private static final long serialVersionUID = -1279823596874363112L;
  
  private Integer policyId = null;
  private Long eventId = null;
  private Integer typeId = null;
  
  public Alert(){  
  }
  
  public Alert(Integer policyId, Integer typeId){
	this.policyId = policyId;
	this.typeId = typeId;
  }
  
  public Alert(Integer policyId, Long eventId, Integer typeId){
	this.policyId = policyId;
	this.eventId = eventId;
	this.typeId = typeId;
  }
  
  public Integer getPolicyId() {
	return policyId;
  }
  public void setPolicyId(Integer policyId) {
	this.policyId = policyId;
  }
  public Long getEventId() {
	return eventId;
  }
  public void setEventId(Long eventId) {
	this.eventId = eventId;
  }
  public Integer getTypeId() {
	return typeId;
  }
  public void setTypeId(Integer typeId) {
	this.typeId = typeId;
  }
  
}
