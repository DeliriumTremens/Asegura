package com.lorant.mobile.android.vo.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.vo.AbstractVO;

public class Policy extends AbstractVO {
	
  private static final long serialVersionUID = 2666771716604200968L;
  private String name = null;
  private String insuranceNumber = null;
  private Integer insuranceId = null;
  private Integer groupId;
  private Date endDate = null;
  private Date startDate = null;
  private String ownerName = null;
  private String ownerMail = null;
  private String ownerPhone = null;
  private String paymentType = null;
  private String sellerName = null;
  private String product = null;
  private String servicePhone = null;
  private Integer systemPolicyId = null;
  private Integer systemId = null;
  private Boolean canReport = null;
  private Integer paymentDay = null;
  private Integer startAlertDay = null;
  private Integer endAlertDay = null;
  private String comment = null;
  private Integer paymentInstrumentId = 0;
  private Integer bankId = 0;
  private Boolean remember = false;
  private Boolean rememberPayment = false;
  private String imageUri = null;
  private String serialNumber = null;
  private String plates = null;
  private Object tag = null;
  private String companyName = null;
  private String description = null;
  private String agentPhone = null;
  
  private List<Feature> details = new ArrayList<Feature>();
  private List<Feature> coverages = new ArrayList<Feature>();
  
  public Policy(){
  }
  
  public Policy(Integer id, String insuranceName){
	this.id = id;
	this.name = insuranceName;
  }
  
  public String getName() {
	return name;
  }
  public void setName(String name) {
	this.name = name;
  }
  public String getInsuranceNumber() {
	return insuranceNumber;
  }
  public void setInsuranceNumber(String insuranceNumber) {
	this.insuranceNumber = insuranceNumber;
  }
  public Integer getInsuranceId() {
	return insuranceId;
  }
  public void setInsuranceId(Integer insuranceId) {
	this.insuranceId = insuranceId;
  }
  public Integer getGroupId() {
	return groupId;
  }
  public void setGroupId(Integer groupId) {
	this.groupId = groupId;
  }
  public String getOwnerName() {
	return ownerName;
  }
  public void setOwnerName(String ownerName) {
	this.ownerName = ownerName;
  }
  public String getOwnerMail() {
	return ownerMail;
  }
  public void setOwnerMail(String ownerMail) {
	this.ownerMail = ownerMail;
  }
  public String getOwnerPhone() {
	return ownerPhone;
  }
  public void setOwnerPhone(String ownerPhone) {
	this.ownerPhone = ownerPhone;
  }
  public String getPaymentType() {
	return paymentType;
  }
  public void setPaymentType(String paymentType) {
	this.paymentType = paymentType;
  }
  public String getSellerName() {
	return sellerName;
  }
  public void setSellerName(String sellerName) {
	this.sellerName = sellerName;
  }
  public String getProduct() {
	return product;
  }
  public void setProduct(String product) {
	this.product = product;
  }
  public List<Feature> getDetails() {
	return details;
  }
  
  public Feature getDetail(String name, String ... alternative){
	Feature detail = new Feature();
	for(Feature feature : details){
	  if(feature.getName().equals(name) || ((alternative.length > 0) && (feature
			                               .getName().equals(alternative[0])))){
		detail = feature;
		break;
	  }
	}
	return detail;
  }
  
  public void setDetails(List<Feature> details) {
	this.details = details;
  }
  public List<Feature> getCoverages() {
	return coverages;
  }
  public void setCoverages(List<Feature> coverages) {
	this.coverages = coverages;
  }
  
  public void addDetail(String name, String value){
	details.add(new Feature(name, value));
  }
  
  public void replaceDetail(String name, String value){
	boolean found = false;
	for(Feature feature :details){
	  if(feature.getName().equals(name)){
		feature.setValue(value);	
		found = true;
	  }
	}
	if(!found){
		addDetail(name, value);
	}
  }
  
  public void addCoverage(String name, String value){
    coverages.add(new Feature(name, value));
  }
  public Date getEndDate() {
	return endDate;
  }
  public void setEndDate(Date endDate) {
	this.endDate = endDate;
  }
  public Date getStartDate() {
	return startDate;
  }
  public void setStartDate(Date startDate) {
	this.startDate = startDate;
  }
  public Integer getSystemPolicyId() {
	return systemPolicyId;
  }
  public void setSystemPolicyId(Integer systemPolicyId) {
	this.systemPolicyId = systemPolicyId;
  }
  public Integer getSystemId() {
	return systemId;
  }
  public void setSystemId(Integer systemId) {
	this.systemId = systemId;
  }

  public String getServicePhone() {
	return servicePhone;
  }

  public void setServicePhone(String servicePhone) {
	this.servicePhone = servicePhone;
  }
	
  @Override
  public String toString() {
	return name;
  }

  public Boolean getCanReport() {
  	return canReport;
  }

  public void setCanReport(Boolean canReport) {
	this.canReport = canReport;
  }

  public Integer getPaymentDay() {
	return paymentDay;
  }

  public void setPaymentDay(Integer paymentDay) {
	this.paymentDay = paymentDay;
  }

  public Integer getStartAlertDay() {
	return startAlertDay;
  }

  public void setStartAlertDay(Integer startAlertDay) {
	this.startAlertDay = startAlertDay;
  }

  public Integer getEndAlertDay() {
	return endAlertDay;
  }

  public void setEndAlertDay(Integer endAlertDay) {
	this.endAlertDay = endAlertDay;
  }

  public String getComment() {
	return comment;
  }

  public void setComment(String comment) {
	this.comment = comment;
  }

  public Integer getPaymentInstrumentId() {
	return paymentInstrumentId;
  }

  public void setPaymentInstrumentId(Integer paymentInstrumentId) {
	this.paymentInstrumentId = paymentInstrumentId;
  }

  public Boolean getRemember() {
	return remember;
  }

  public void setRemember(Boolean remember) {
	this.remember = remember;
  }

  public Integer getBankId() {
	return bankId;
  }

  public void setBankId(Integer bankId) {
	this.bankId = bankId;
  }

  public String getImageUri() {
	return imageUri;
  }

  public void setImageUri(String imageUri) {
	this.imageUri = imageUri;
  }
  
  public boolean isInternal(){
	return sellerName != null && sellerName.equals(Config.LORANT_SELLER_NAME);
  }

  public String getSerialNumber() {
	return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
	this.serialNumber = serialNumber;
  }

  public Boolean getRememberPayment() {
	return rememberPayment;
  }

  public void setRememberPayment(Boolean rememberPayment) {
	this.rememberPayment = rememberPayment;
  }

  public String getPlates() {
	return plates;
  }

  public void setPlates(String plates) {
	this.plates = plates;
  }

  public Object getTag() {
	return tag;
  }

  public void setTag(Object tag) {
	this.tag = tag;
  }

  public String getCompanyName() {
	return companyName;
  }

  public void setCompanyName(String companyName) {
	this.companyName = companyName;
  }

  public String getDescription() {
	return description;
  }

  public void setDescription(String description) {
	this.description = description;
  }

  public String getAgentPhone() {
	return agentPhone;
  }

  public void setAgentPhone(String agentPhone) {
	this.agentPhone = agentPhone;
  }
 
}
