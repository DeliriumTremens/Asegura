package com.lorant.mobile.android.service;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

public class CalendarReminderReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
	ContentResolver contentResolver = context.getContentResolver();
	Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/reminders"),
            new String[] {CalendarContract.Reminders._ID}
	               , null, null, null);
    cursor.moveToFirst();
    String CNames[] = new String[cursor.getCount()];
    for (int i = 0; i < CNames.length; i++) {
      System.out.println(cursor.getString(0));
      cursor.moveToNext();
    }
  }

}
