package com.lorant.mobile.android.util.swipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

@SuppressLint("ViewConstructor")
public class UndoView extends FrameLayout {

  private View mUndoView;
  private View mContentView;
  private long mItemId;

  public UndoView(Context context, int undoLayoutResourceId) {
	super(context);
	initUndo(undoLayoutResourceId);
  }

  private void initUndo(int undoLayoutResourceId) {
	mUndoView = View.inflate(getContext(), undoLayoutResourceId, null);
	addView(mUndoView);
  }

  public void updateContentView(View contentView) {
	if(mContentView == null) {
	  addView(contentView);
	}
	mContentView = contentView;
  }

  public View getContentView() {
	return mContentView;
  }

  public void setItemId(long itemId) {
	this.mItemId = itemId;
  }

  public long getItemId() {
	return mItemId;
  }

  public boolean isContentDisplayed() {
	return mContentView.getVisibility() == View.VISIBLE;
  }

  public void displayUndo() {
	mContentView.setVisibility(View.GONE);
	mUndoView.setVisibility(View.VISIBLE);
  }

  public void displayContentView() {
	mContentView.setVisibility(View.VISIBLE);
	mUndoView.setVisibility(View.GONE);
  }
}