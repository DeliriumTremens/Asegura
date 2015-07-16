package com.lorant.mobile.android.util.swipe;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.nineoldandroids.view.ViewHelper.setAlpha;
import static com.nineoldandroids.view.ViewHelper.setTranslationX;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class UndoAdapter extends Decorator implements UndoTouchListener.Callback {

  private final int mUndoLayoutId;
  private final int actionId;
  private final int mAnimationTime = 150;
  private UndoView mCurrentRemovedView;
  private long mCurrentRemovedId;
  private Map<View, Animator> mActiveAnimators = new ConcurrentHashMap<View, Animator>();
  private DeleteItemCallback mDeleteItemCallback;
      
  public UndoAdapter(BaseAdapter baseAdapter, int undoLayoutId
		                                 , int undoActionId) {
    super(baseAdapter);
    mUndoLayoutId = undoLayoutId;
    actionId = undoActionId;
    mCurrentRemovedId = -1;
  }

  @Override
  public final View getView(int position, View convertView, ViewGroup parent) {
    UndoView undoView = (UndoView) convertView;
    View contentView = null;
    long itemId = 0L;
    if(undoView == null) {
       undoView = new UndoView(parent.getContext() , mUndoLayoutId);
       undoView.findViewById(actionId).setOnClickListener(new UndoListener(undoView));
    }
    contentView = super.getView(position, undoView.getContentView(), parent);
    undoView.updateContentView(contentView);
    itemId = getItemId(position);
    if(itemId == mCurrentRemovedId) {
       undoView.displayUndo();
       mCurrentRemovedView = undoView;
    } else {
        undoView.displayContentView();
    }
    undoView.setItemId(itemId);
    return undoView;
  }

  @Override
  public void setAbsListView(AbsListView listView) {
    super.setAbsListView(listView);
    UndoTouchListener contextualUndoListViewTouchListener = new UndoTouchListener(listView, this);
    listView.setOnTouchListener(contextualUndoListViewTouchListener);
    listView.setOnScrollListener(contextualUndoListViewTouchListener.makeScrollListener());
    listView.setRecyclerListener(new RecycleViewListener());
  }

  @Override
  public void onViewSwiped(View dismissView, int dismissPosition) {
    UndoView contextualUndoView = (UndoView) dismissView;
    if(contextualUndoView.isContentDisplayed()) {
      restoreViewPosition(contextualUndoView);
      contextualUndoView.displayUndo();
      removePreviousContextualUndoIfPresent();
      setCurrentRemovedView(contextualUndoView);
    } else {
        if(mCurrentRemovedView != null) {
          performRemoval();
        }
    }
  }

  private void restoreViewPosition(View view) {
    setAlpha(view, 1f);
    setTranslationX(view, 0);
  }

  private void removePreviousContextualUndoIfPresent() {
    if(mCurrentRemovedView != null) {
      performRemoval();
    }
  }

  private void setCurrentRemovedView(UndoView currentRemovedView) {
    mCurrentRemovedView = currentRemovedView;
    mCurrentRemovedId = currentRemovedView.getItemId();
  }

  private void clearCurrentRemovedView() {
    mCurrentRemovedView = null;
    mCurrentRemovedId = -1;
  }

  @Override
  public void onListScrolled() {
    if(mCurrentRemovedView != null) {
      performRemoval();
    }
  }

  private void performRemoval() {
    ValueAnimator animator = ValueAnimator.ofInt(mCurrentRemovedView.getHeight()
    		                                   , 1).setDuration(mAnimationTime);
    animator.addListener(new RemoveViewAnimatorListenerAdapter(mCurrentRemovedView));
    animator.addUpdateListener(new RemoveViewAnimatorUpdateListener(mCurrentRemovedView));
    animator.start();
    mActiveAnimators.put(mCurrentRemovedView, animator);
    clearCurrentRemovedView();
  }

  public void setDeleteItemCallback(DeleteItemCallback deleteItemCallback) {
    mDeleteItemCallback = deleteItemCallback;
  }

  public Parcelable onSaveInstanceState() {
    Bundle bundle = new Bundle();
    bundle.putLong("mCurrentRemovedId", mCurrentRemovedId);
    return bundle;
  }

  public void onRestoreInstanceState(Parcelable state) {
    Bundle bundle = (Bundle) state;
    mCurrentRemovedId = bundle.getLong("mCurrentRemovedId", -1);
  }

  public interface DeleteItemCallback {
    public void deleteItem(int position);
  }

  private class RemoveViewAnimatorListenerAdapter extends AnimatorListenerAdapter {
    private final View mDismissView;
    private final int mOriginalHeight;
    public RemoveViewAnimatorListenerAdapter(View dismissView) {
      mDismissView = dismissView;
      mOriginalHeight = dismissView.getHeight();
    }
    
    @Override
    public void onAnimationEnd(Animator animation) {
      mActiveAnimators.remove(mDismissView);
      restoreViewPosition(mDismissView);
      restoreViewDimension(mDismissView);
      deleteCurrentItem();
    }

    private void restoreViewDimension(View view) {
      ViewGroup.LayoutParams lp;
      lp = view.getLayoutParams();
      lp.height = mOriginalHeight;
      view.setLayoutParams(lp);
    }

    private void deleteCurrentItem() {
      UndoView contextualUndoView = (UndoView) mDismissView;
      int position = 0;
      try{
          position = getAbsListView().getPositionForView(contextualUndoView);
      } catch(NullPointerException ignored){}
      mDeleteItemCallback.deleteItem(position);
    }
  }

  private class RemoveViewAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    private final View mDismissView;
    private final ViewGroup.LayoutParams mLayoutParams;
    public RemoveViewAnimatorUpdateListener(View dismissView) {
      mDismissView = dismissView;
      mLayoutParams = dismissView.getLayoutParams();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
      mLayoutParams.height = (Integer) valueAnimator.getAnimatedValue();
      mDismissView.setLayoutParams(mLayoutParams);
    }
  }

  private class UndoListener implements View.OnClickListener {
    private final UndoView mContextualUndoView;
    public UndoListener(UndoView contextualUndoView) {
       mContextualUndoView = contextualUndoView;
    }

    @Override
    public void onClick(View v) {
      clearCurrentRemovedView();
      mContextualUndoView.displayContentView();
      moveViewOffScreen();
      animateViewComingBack();
    }

    private void moveViewOffScreen() {
      ViewHelper.setTranslationX(mContextualUndoView, mContextualUndoView.getWidth());
    }

    private void animateViewComingBack() {
      animate(mContextualUndoView).translationX(0).setDuration(mAnimationTime)
                                                           .setListener(null);
    }
  }

  private class RecycleViewListener implements AbsListView.RecyclerListener {
    @Override
    public void onMovedToScrapHeap(View view) {
      Animator animator = mActiveAnimators.get(view);
      if(animator != null) {
         animator.cancel();
      }
    }
  }
}