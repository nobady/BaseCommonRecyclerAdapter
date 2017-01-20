package com.library.adapter.view;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import com.library.adapter.BaseViewHolder;

/**
 * Created by tengfei.lv on 2017/1/18.
 */
public abstract class LoadMoreView {
    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_LOADING = 2;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_END = 4;
    private int mLoadMoreStatus = 1;
    private boolean mLoadMoreEndGone = false;

    public LoadMoreView() {
    }

    public void setLoadMoreStatus(int loadMoreStatus) {
        mLoadMoreStatus = loadMoreStatus;
    }

    public int getLoadMoreStatus() {
        return mLoadMoreStatus;
    }

    public void convert(BaseViewHolder holder) {
        switch(mLoadMoreStatus) {
            case STATUS_LOADING:
                visibleItem(holder,true);
                visibleLoading(holder, true);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, false);
                break;
            case STATUS_FAIL:
                visibleItem(holder,true);
                visibleLoading(holder, false);
                visibleLoadFail(holder, true);
                visibleLoadEnd(holder, false);
                break;
            case STATUS_END:
                visibleItem(holder,true);
                visibleLoading(holder, false);
                visibleLoadFail(holder, false);
                visibleLoadEnd(holder, true);
                break;
            case STATUS_DEFAULT:
                visibleItem(holder,false);
                break;
        }

    }

    private void visibleItem (BaseViewHolder holder, boolean visible) {
        holder.itemView.setVisibility (visible? View.VISIBLE:View.GONE);
    }

    private void visibleLoading(BaseViewHolder holder, boolean visible) {
        holder.setVisible(getLoadingViewId(), visible);
    }

    private void visibleLoadFail(BaseViewHolder holder, boolean visible) {
        holder.setVisible(getLoadFailViewId(), visible);
    }

    private void visibleLoadEnd(BaseViewHolder holder, boolean visible) {
        int loadEndViewId = getLoadEndViewId();
        if(loadEndViewId != 0) {
            holder.setVisible(loadEndViewId, visible);
        }

    }

    public final void setLoadMoreEndGone(boolean loadMoreEndGone) {
        mLoadMoreEndGone = loadMoreEndGone;
    }

    public final boolean isLoadEndMoreGone() {
        return getLoadEndViewId() == 0?true:mLoadMoreEndGone;
    }

    @LayoutRes
    public abstract int getLayoutId();

    @IdRes
    protected abstract int getLoadingViewId();

    @IdRes
    protected abstract int getLoadFailViewId();

    @IdRes
    protected abstract int getLoadEndViewId();
}
