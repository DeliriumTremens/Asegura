package com.lorant.mobile.android.vo.bean;

import com.lorant.mobile.android.vo.AbstractVO;

public class InsuranceGroup extends AbstractVO {
	
  private String name;
  private Integer resourceId;
  
  public InsuranceGroup(Integer id){
	this.id = id;
  }
  
  public InsuranceGroup(Integer id, String name, Integer resourceId){
	this.id = id;
	this.name = name;
	this.resourceId = resourceId;
  }
  
  public String getName() {
	return name;
  }
  public void setName(String name) {
	this.name = name;
  }
  public Integer getResourceId() {
	return resourceId;
  }
  public void setResourceId(Integer resourceId) {
	this.resourceId = resourceId;
  }

  @Override
  public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	return result;
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (getClass() != obj.getClass())
		return false;
	InsuranceGroup other = (InsuranceGroup) obj;
	if (id == null) {
		if (other.id != null)
			return false;
	} else if (!id.equals(other.id))
		return false;
	return true;
  }
  
}
