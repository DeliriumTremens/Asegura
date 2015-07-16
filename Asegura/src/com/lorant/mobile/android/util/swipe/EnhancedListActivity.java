package com.lorant.mobile.android.util.swipe;

import com.lorant.mobile.android.R;
import com.lorant.mobile.android.ui.AbstractUI;
import com.lorant.mobile.android.vo.AbstractVO;

import android.widget.ListView;


public abstract class EnhancedListActivity extends AbstractUI {

  private Adapter<? extends AbstractVO> mAdapter = null;
  public abstract  Adapter<? extends AbstractVO> createListAdapter();
  public abstract Integer getLayoutId();
  public abstract void onDelete(int position);

  public EnhancedListActivity(int resourceId){
	super(resourceId);
  }
  
  @Override
  public void onStart() {
	super.onStart();
  }
  
  
  public void createEnhancedList() {
	mAdapter = createListAdapter();
	UndoAdapter contextualUndoAdapter = new UndoAdapter(mAdapter, R.layout
			                          .common_undo_row, R.id.undo_row_undobutton);
	contextualUndoAdapter.setAbsListView(getListView());
	getListView().setAdapter(contextualUndoAdapter);
	contextualUndoAdapter.setDeleteItemCallback(new DeleteItemCallbackImpl());
	getListView().setDivider(null);
  }
  
  protected ListView getListView(){
	return (ListView) rootView.findViewById(R.id.lvEnhanced);
  }
  
  private class DeleteItemCallbackImpl implements com.lorant.mobile.android
                               .util.swipe.UndoAdapter.DeleteItemCallback {
	@Override
	public void deleteItem(int position) {
	  onDelete(position);
	  mAdapter.remove(position);
	  mAdapter.notifyDataSetChanged();
	}
  }
}