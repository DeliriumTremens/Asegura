package com.lorant.mobile.android.ui.view;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.db.dao.core.NotificationDAO;
import com.lorant.mobile.android.util.swipe.Adapter;
import com.lorant.mobile.android.util.swipe.EnhancedListActivity;
import com.lorant.mobile.android.vo.bean.Notification;

public class NotificationsList extends EnhancedListActivity{

	
  private List<Notification> notifications = null;
	  
  public NotificationsList(){
	super(R.layout.notifications_list);
  }
	  
  @Override
  public void populate(){
	setActionBarTitle(R.string.notifications);
	loadNotifications();
  }
  
  @Override 
  public void setBehavior(){
	createEnhancedList();
  }

  public  List<Notification> getItems() {
	return notifications;
  }
	  
  @Override
  public Integer getLayoutId(){
	return R.layout.notifications_list;
  }

  @Override
  public Adapter<Notification> createListAdapter() {
	return new ListAdapterImpl(this, notifications);
  }
	  
  @Override
  public void onDelete(int position){
	Notification notification = notifications.get(position);
	new NotificationDAO(ctx).delete(notification.getPolicyId()
			                        , notification.getType());
	notifications.remove(notification);
  }
	    
  private class ListAdapterImpl extends Adapter<Notification> {
	private Context mContext;
	public ListAdapterImpl(Context context, List<Notification> items) {
	  super(items);
	  mContext = context;
	}
		
	@Override
	public long getItemId(int position) {
	  return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  TextView tvNotification = null;
	  if(convertView == null){
		  convertView = (LinearLayout) LayoutInflater.from(mContext)
				 .inflate(R.layout.notification_row, parent, false);
	  }
	  tvNotification = (TextView) convertView.findViewById(R.id.tvNotification);
	  tvNotification.setText(notifications.get(position).getName());
	  return convertView;
	}
  }
  
  private void loadNotifications(){
	notifications = new NotificationDAO(ctx).findActive();
  }
}
