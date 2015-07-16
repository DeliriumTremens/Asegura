package com.lorant.mobile.android.util.swipe;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewHelper.setTranslationX;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;


public class UndoTouchListener implements View.OnTouchListener {
  private int mSlop;
  private int mMinFlingVelocity;
  private int mMaxFlingVelocity;
  private long mAnimationTime;
  private AbsListView mListView;
  private Callback mCallback;
  private int mViewWidth = 1; 
  private float mDownX;
  private boolean mSwiping;
  private VelocityTracker mVelocityTracker;
  private int mDownPosition;
  private View mDownView;
  private boolean mPaused;

  public interface Callback {
    void onViewSwiped(View dismissView, int dismissPosition);
    void onListScrolled();
  }

  public UndoTouchListener(AbsListView listView, Callback callback) {
    ViewConfiguration vc = ViewConfiguration.get(listView.getContext());
    mSlop = vc.getScaledTouchSlop();
    mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
    mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
    mAnimationTime = listView.getContext().getResources().getInteger(
                             android.R.integer.config_shortAnimTime);
    mListView = listView;
    mCallback = callback;
  }

  public void setEnabled(boolean enabled) {
    mPaused = !enabled;
  }

  public AbsListView.OnScrollListener makeScrollListener() {
    return new AbsListView.OnScrollListener() {
                 @Override
                 public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                    setEnabled(scrollState != AbsListView.OnScrollListener
                    		                   .SCROLL_STATE_TOUCH_SCROLL);
                    if(mPaused) {
                      mCallback.onListScrolled();
                    }
                  }

                  @Override
                  public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                  }
    };
  }

  @Override
  public boolean onTouch(View view, MotionEvent motionEvent) {
    if(mViewWidth < 2) {
       mViewWidth = mListView.getWidth();
    }
    switch(motionEvent.getActionMasked()) {
      case MotionEvent.ACTION_DOWN: 
    	     Rect rect = new Rect();
             int childCount = mListView.getChildCount();
             int[] listViewCoords = new int[2];
             View child;
             int x, y;
             if(mPaused){
               return false;
             }
             mListView.getLocationOnScreen(listViewCoords);
             x = (int) motionEvent.getRawX() - listViewCoords[0];
             y = (int) motionEvent.getRawY() - listViewCoords[1];
             for(int i = 0; i < childCount; i++) {
                child = mListView.getChildAt(i);
                child.getHitRect(rect);
                if(rect.contains(x, y)) {
                   mDownView = child;
                   break;
                }
              }
              if(mDownView != null) {
                mDownX = motionEvent.getRawX();
                mDownPosition = mListView.getPositionForView(mDownView);
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(motionEvent);
              }
              view.onTouchEvent(motionEvent);
              return true;
       case MotionEvent.ACTION_UP: 
    	      float deltaX, velocityX, velocityY;
    	      boolean dismiss = false;
              boolean dismissRight = false;
              final View downView;
              final int downPosition;
              if(mVelocityTracker == null) {
                 break;
              }
              deltaX = motionEvent.getRawX() - mDownX;
              mVelocityTracker.addMovement(motionEvent);
              mVelocityTracker.computeCurrentVelocity(1000);
              velocityX = Math.abs(mVelocityTracker.getXVelocity());
              velocityY = Math.abs(mVelocityTracker.getYVelocity()); 
              if(Math.abs(deltaX) > mViewWidth / 2) {
                dismiss = true;
                dismissRight = deltaX > 0;
              } else if (mMinFlingVelocity <= velocityX 
            		&& velocityX <= mMaxFlingVelocity&& velocityY < velocityX) {
                  dismiss = true;
                  dismissRight = mVelocityTracker.getXVelocity() > 0;
              }
              if(dismiss) {
                downView = mDownView;
                downPosition = mDownPosition;
                animate(mDownView)
                          .translationX(dismissRight ? mViewWidth : -mViewWidth)
                          .alpha(0)
                          .setDuration(mAnimationTime)
                          .setListener(new AnimatorListenerAdapter() {
                              @Override
                              public void onAnimationEnd(Animator animation) {
                                mCallback.onViewSwiped(downView, downPosition);
                              }
                            });
              } else {
                  animate(mDownView).translationX(0)
                                    .alpha(1)
                                    .setDuration(mAnimationTime)
                                    .setListener(null);
              }
              mVelocityTracker = null;
              mDownX = 0;
              mDownView = null;
              mDownPosition = ListView.INVALID_POSITION;
              mSwiping = false;
              break;
       case MotionEvent.ACTION_MOVE: 
    	      MotionEvent cancelEvent = null;
              if(mVelocityTracker == null || mPaused) {
                  break;
              }
              mVelocityTracker.addMovement(motionEvent);
              deltaX = motionEvent.getRawX() - mDownX;
              if(Math.abs(deltaX) > mSlop) {
                mSwiping = true;
                mListView.requestDisallowInterceptTouchEvent(true);
                cancelEvent = MotionEvent.obtain(motionEvent);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex()
                                    << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                mListView.onTouchEvent(cancelEvent);
              }
              if(mSwiping) {
                setTranslationX(mDownView, deltaX);
                setAlpha(mDownView, Math.max(0f, Math.min(1f,1f - 2f * Math
                		                      .abs(deltaX) / mViewWidth)));
                return true;
              }
              break;
     }
        
     return false;
   }
}