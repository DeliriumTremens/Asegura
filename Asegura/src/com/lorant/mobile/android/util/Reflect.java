package com.lorant.mobile.android.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Reflect {
	
	
  public static List<Method> getGetters(Class <?>  lclsClass){
    List<Method> llisGetters = new ArrayList<Method>();
	Method [] lmtdAllMethods = lclsClass.getDeclaredMethods();
	for(int i=0;i<lmtdAllMethods.length;i++){
		if(lmtdAllMethods[i].getName().startsWith("get")){
			llisGetters.add(lmtdAllMethods[i]);
		}
	}
	return llisGetters;
  }
	
  public static HashMap<String,String> getGettersValues(Object lclsClass
			                      , List<Method> llisGetters) throws Exception {
    HashMap<String,String> lhsmGettersValues = new HashMap<String,String>();
	Method lmtdGetter=null;
	Object lobjInvocationResponse = null;
	for(int i=0;i<llisGetters.size();i++){
		lmtdGetter = llisGetters.get(i);
		lobjInvocationResponse = lmtdGetter.invoke(lclsClass, new Object[0]);
		if(lobjInvocationResponse==null)
			lobjInvocationResponse ="null";
		    lhsmGettersValues.put(lmtdGetter.getName().substring(3)
					            ,lobjInvocationResponse.toString());
		}
	return lhsmGettersValues;
  }

}
