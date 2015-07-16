package com.lorant.mobile.android.vo;

import java.io.Serializable;
import java.util.HashMap;

import com.lorant.mobile.android.util.Reflect;


public abstract class AbstractVO implements Serializable {
	

  private static final long serialVersionUID = -5915594167741335811L;
  protected Integer id;
  
  
  public final Integer getId() {
    return id;
  }
  public final void setId(Integer id) {
    this.id = id;
  }
  
  @Override
  public String toString(){
	HashMap<String,String> values = null;
	try{
		values = Reflect.getGettersValues(this, Reflect.getGetters(getClass()));
	} catch(Exception e){
		e.printStackTrace();
	}
    return getClass().getCanonicalName() + " " + values;
  }
  
  @Override
  public boolean equals(Object object){
    boolean isEqual = false;
    AbstractVO toCompare = null;
    if(object != null){
	  if(object instanceof AbstractVO){
		toCompare = (AbstractVO) object;
	    if((toCompare.getId().equals(id))){
		  isEqual = toCompare.toString().equals(toString());
	    }
	  }
    }
	return isEqual;
  }
  
  @Override
  public int hashCode() {
	return toString().length();
  }

}
