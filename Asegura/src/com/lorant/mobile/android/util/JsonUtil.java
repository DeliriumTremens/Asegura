package com.lorant.mobile.android.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lorant.mobile.android.constant.Config;
import com.lorant.mobile.android.vo.bean.Brand;
import com.lorant.mobile.android.vo.bean.Company;
import com.lorant.mobile.android.vo.bean.Dealer;
import com.lorant.mobile.android.vo.bean.EventType;
import com.lorant.mobile.android.vo.bean.Historical;
import com.lorant.mobile.android.vo.bean.Policy;



public class JsonUtil {
	
  
  public static List<Company> getCompaniesProfile(JSONArray jsonArray) throws JSONException {
	List<Company> companies = new ArrayList<Company>();
	JSONObject jsonCompany = null;
	Company company = null;
	for (int i = 0; i < jsonArray.length(); i++){
	  try{
	      company = new Company();
	      jsonCompany = jsonArray.getJSONObject(i);
	      company.setId(jsonCompany.getInt("ID_ASEGURADORA"));
	      company.setGroupId(jsonCompany.getInt("ID_RAMO"));
	      company.setName(jsonCompany.getString("ASEGURADORA"));
	      companies.add(company);
	  } catch(Exception unparsable){}
	}
	return companies;
  }
  
  public static Policy getPolicyDetail(JSONObject jsonObject) throws JSONException {
	Policy policy = new Policy();
	JSONArray jsonFeatures = null;
	JSONObject jsonFeature = null;
	try{ 
	    policy.setInsuranceNumber(jsonObject.getString("insuranceNumber"));
	    policy.setOwnerName(jsonObject.getString("ownerName"));
	    try {
		  policy.setEndDate(Config.DATE_FORMAT.parse(jsonObject.getString("endDate")));
	    } catch (ParseException ignored) {}
	    try {
		  policy.setStartDate(Config.DATE_FORMAT.parse(jsonObject.getString("startDate")));
		} catch (ParseException ignored) {}
	    policy.setOwnerMail(jsonObject.getString("contactMail"));
	    policy.setOwnerPhone(jsonObject.getString("contactPhoneNumber"));
	    policy.setGroupId(jsonObject.getInt("idRamo"));
	    policy.setInsuranceId(jsonObject.getInt("idAseguradora"));
	    policy.setPaymentType(jsonObject.getString("FormaPago"));
	    policy.setProduct(jsonObject.getString("Paquete"));
	    policy.setSellerName(jsonObject.getString("ContratadoCon"));
	    policy.setSystemId(jsonObject.getInt("iSistema"));
	    policy.setServicePhone(jsonObject.getString("TelefonoCabina"));
	    policy.setCanReport(jsonObject.getBoolean("ReportaSiniestro"));
	    policy.setName(jsonObject.getString("insuranceAlias"));
	    policy.setPlates(jsonObject.getString("placas"));
	    policy.setAgentPhone(jsonObject.getString("TelefonoCabina"));
	    try{
	    	policy.setId(jsonObject.getInt("idPolizaSistema"));
	    } catch(Exception e){}
	    try{
	    	policy.setId(jsonObject.getInt("IdPolizaM"));
	    } catch(Exception e){}
	    jsonFeatures = jsonObject.getJSONArray("productDetail");
	    for(int i = 0; i < jsonFeatures.length() ; i++){
	      jsonFeature = jsonFeatures.getJSONObject(i);
	      policy.addDetail(jsonFeature.getString("label")
			           , jsonFeature.getString("valor"));
	   }
	   jsonFeatures = jsonObject.getJSONArray("coberturasDetail");
	   for(int i = 0; i < jsonFeatures.length() ; i++){
	     jsonFeature = jsonFeatures.getJSONObject(i);
	     policy.addCoverage(jsonFeature.getString("label")
			            , jsonFeature.getString("valor"));
	   }
	} catch(Exception e){
		policy = null;
	}
	return policy;
  }
  
  public static ArrayList<Policy> getPolicyList(JSONArray jsonArray) throws JSONException {
	ArrayList<Policy> policies = new ArrayList<Policy>();
	JSONObject jsonPolicy = null;
	Policy policy = null;
	try{
	  for (int i = 0; i < jsonArray.length(); i++){
	    policy = new Policy();
	    jsonPolicy = jsonArray.getJSONObject(i);
	    policy.setName(jsonPolicy.getString("insuranceName"));
	    policy.setInsuranceNumber(jsonPolicy.getString("insuranceNumber"));
	    policy.setGroupId(jsonPolicy.getInt("idRamo"));
	    policy.setInsuranceId(jsonPolicy.getInt("idAseguradora"));
	    policy.setSerialNumber(jsonPolicy.getString("NoSerie"));
	    policy.setPlates(jsonPolicy.getString("NoPlacas"));
	    policy.setCompanyName(jsonPolicy.getString("Aseguradora"));
	    try{
	    	policy.setId(jsonPolicy.getInt("idPolizaSistema"));
	    } catch(Exception e){}
	    try{
	    	policy.setId(jsonPolicy.getInt("IdPolizaM"));
	    } catch(Exception e){}
	    try {
  		     policy.setEndDate(Config.DATE_FORMAT_ALT.parse(jsonPolicy
  		    		                   .getString("FechaHasta")));
	    } catch (ParseException ignored) {}
	    //policy.setPlates(jsonPolicy.getString("NoPlacas"));
	    policies.add(policy);
	  }
	} catch(Exception e){
		e.printStackTrace();
	}
	return policies;
  }
  
  public static ArrayList<EventType> getEventTypeList(JSONArray jsonArray) 
		                                            throws JSONException {
	ArrayList<EventType> eventTypes = new ArrayList<EventType>();
	JSONObject jsonEventType = null;
	EventType eventType = null;
	try{
	  for (int i = 0; i < jsonArray.length(); i++){
		eventType = new EventType();
		jsonEventType = jsonArray.getJSONObject(i);
		eventType.setId(jsonEventType.getInt("ID_TIPO_SINIESTRO"));
		eventType.setName(jsonEventType.getString("SINIESTRO"));
		eventTypes.add(eventType);
	  }
	} catch(Exception e){
		e.printStackTrace();
	}
	return eventTypes;
  }
  
  public static ArrayList<Brand> getBrandList(JSONArray jsonArray) 
                                            throws JSONException {
    ArrayList<Brand> brands = new ArrayList<Brand>();
    JSONObject jsonBrand = null;
    Brand brand = null;
    try{
        for(int i = 0; i < jsonArray.length(); i++){
          brand = new Brand();
          jsonBrand = jsonArray.getJSONObject(i);
          brand.setId(jsonBrand.getInt("ID_MARCA"));
          brand.setName(jsonBrand.getString("MARCA"));
          brands.add(brand);
        }
    } catch(Exception e){
       e.printStackTrace();
    }
    return brands;
  }
  
  public static ArrayList<Dealer> getDealers(JSONArray jsonArray) 
                                           throws JSONException {
    ArrayList<Dealer> dealers = new ArrayList<Dealer>();
    JSONObject jsonDealer = null;
    Dealer dealer = null;
    try{
        for(int i = 0; i < jsonArray.length(); i++){
        	dealer = new Dealer();
        	jsonDealer = jsonArray.getJSONObject(i);
        	dealer.setName(jsonDealer.getString("AGENCIA"));
        	dealer.setLatLng(jsonDealer.getString("COORDENADA"));
        	dealer.setAddres(jsonDealer.getString("DOMICILIO"));
        	dealer.setZipCode(jsonDealer.getString("CP"));
        	dealer.setPhone(jsonDealer.getString("TELEFONO"));
        	dealer.setMail(jsonDealer.getString("CORREO"));
        	dealers.add(dealer);
        }
    } catch(Exception e){
        e.printStackTrace();
    }
    return dealers;
  }
  
  public static ArrayList<Historical> getHistorical(JSONArray jsonArray) 
                                                  throws JSONException {
    ArrayList<Historical> historical = new ArrayList<Historical>();
    JSONObject jsonHistorical = null;
    Historical entry = null;
    String reportDate = null;
    try{
        for(int i = 0; i < jsonArray.length(); i++){
          entry = new Historical();
          jsonHistorical = jsonArray.getJSONObject(i);
          reportDate = jsonHistorical.getString("FECHA_REGISTRO");
          entry.setInformation(jsonHistorical.getString("INFORMACION"));
          entry.setLatitude(Float.valueOf(jsonHistorical.getString("LATITUD")));
          entry.setLongitude(Float.valueOf(jsonHistorical.getString("LONGITUD")));
          entry.setPhone(jsonHistorical.getString("NO_TEL"));
          entry.setPolicyId(jsonHistorical.getInt("ID_POLIZA_M"));
          entry.setPolicyNumber(jsonHistorical.getString("NO_POLIZA"));
          try {
        	  entry.setRegistrationDate(Config.DATE_FORMAT_HISTORICAL
        			  .parse(reportDate.substring(0, reportDate.indexOf("T"))));
 	      } catch (ParseException ignored) {}
          entry.setRegistrationTime(reportDate.substring(reportDate.indexOf("T") 
        		                                     + 1, reportDate.length()));
          entry.setSinesterType(jsonHistorical.getString("SINIESTRO"));
          historical.add(entry);
        }
    } catch(Exception e){
        e.printStackTrace();
    }
    return historical;
  }
  

}
