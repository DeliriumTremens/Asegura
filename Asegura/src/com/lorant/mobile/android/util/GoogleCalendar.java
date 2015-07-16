package com.lorant.mobile.android.util;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import com.lorant.mobile.android.db.dao.core.AlertDAO;
import com.lorant.mobile.android.vo.bean.Alert;
import com.lorant.mobile.android.vo.bean.Policy;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;

public class GoogleCalendar {
	
	private static final String TAG = GoogleCalendar.class.getName();

  private Context ctx = null;
  
  public GoogleCalendar(Context ctx){
	this.ctx = ctx;
  }
	
  public void switchEvent(String name, String description, Calendar date
               , Boolean isChecked, Policy policy, Integer typeId, String rrule){
	AlertDAO alertDAO = new AlertDAO(ctx);
	Alert alert = alertDAO.find(policy.getId(), typeId);
	if(isChecked){
	  if(alert == null){
		alert = new Alert(policy.getId(), typeId);
	  }
	  alert.setEventId(addEvent(name, description, date, true, rrule));
	  alertDAO.upsert(alert);  
	} else {
	     if(alert != null){
	       deleteEvent(alert.getEventId());
	       alertDAO.delete(alert.getId());
	     }
	}
  }
  
  public void deleteEvent(Long eventId){
	Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI
			                                                           , eventId);
	ctx.getContentResolver().delete(deleteUri, null, null);
	Log.d(TAG, "Event deleted: " + eventId);
  }
  
  public void deleteFromPolicy(Integer policyId){
	List<Alert> alerts = new AlertDAO(ctx).findByPolicy(policyId);
	for(Alert alert : alerts){
		deleteEvent(alert.getEventId());
	}
  }
  
  public Long addEvent(String name, String description, Calendar cal
		                          , boolean alarmed, String rrule) {
	long eventId = insertEvent(name, description, cal, rrule);
	if(alarmed){
	  insertAlarm(eventId, name);
	}
	return eventId;
  }
  
  private long insertEvent(String name, String description, Calendar cal
		                                               , String rrule) {
    Uri uri = null;
    long start = 0L;
	long calId = getCalendarId();
	if (calId == -1) {
	   return -1;
	}
	cal.setTimeZone(TimeZone.getTimeZone("UTC"));
	cal.set(Calendar.HOUR, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);
	start = cal.getTimeInMillis();
	ContentValues values = new ContentValues();
	values.put(Events.RRULE, rrule); 
	values.put(Events.DTSTART, start);
	values.put(Events.DTEND, start);
	values.put(Events.TITLE, name);
	values.put(Events.CALENDAR_ID, calId);
	values.put(Events.EVENT_TIMEZONE, "America/Mexico_City");
	values.put(Events.DESCRIPTION, description);
	values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
	values.put(Events.SELF_ATTENDEE_STATUS, Events.STATUS_CONFIRMED);
	values.put(Events.ALL_DAY, 1);
	values.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);
	values.put(Events.GUESTS_CAN_MODIFY, 1);
	values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
	uri = ctx.getContentResolver().insert(Events.CONTENT_URI, values);
	return Long.valueOf(uri.getLastPathSegment());
  }
  
  private void insertAlarm(long eventId, String name){
	ContentValues values = new ContentValues();
	values.put(Attendees.EVENT_ID, eventId);
	values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_REQUIRED);
	values.put(Attendees.ATTENDEE_NAME, name);
	ctx.getContentResolver().insert(Attendees.CONTENT_URI, values);
	values.clear();
	values.put(Reminders.EVENT_ID, eventId);
	values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
	values.put(Reminders.MINUTES, 30);
	ctx.getContentResolver().insert(Reminders.CONTENT_URI, values);
  }
  
  public long getCalendarId() {
    String[] projection = new String[]{Calendars._ID};
    String selection = Calendars.ACCOUNT_NAME + " = ? and " 
                     + Calendars.ACCOUNT_TYPE + " = ? ";
    String[] selArgs = new String[]{ getUsername()
    		  , "com.google"};
	Cursor cursor = ctx.getContentResolver().query(Calendars.CONTENT_URI,
		                                            projection, 
		                                            selection,
		                                            selArgs,
		                                            null);
	if(cursor.moveToFirst()) {
	  return cursor.getLong(0);
	}
	return -1;
  }
	
	
  public String getUsername() {
	AccountManager manager = AccountManager.get(ctx); 
	Account[] accounts = manager.getAccountsByType("com.google"); 
	List<String> possibleEmails = new LinkedList<String>();
	for(Account account : accounts) {
	  possibleEmails.add(account.name);
	}
	if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
	  return possibleEmails.get(0);
	}
	return null;
  }
}
