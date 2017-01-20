package com.library.adapter.view;

import com.library.adapter.R;

/**
 * Created by tengfei.lv on 2017/1/18.
 */
public class DefaultLoadMoreView extends LoadMoreView {


    @Override public int getLayoutId () {
        return R.layout.default_load_more_view;
    }

    @Override protected int getLoadingViewId () {
        return R.id.load_more_loading_view;
    }

    @Override protected int getLoadFailViewId () {
        return R.id.load_more_load_fail_view;
    }

    @Override protected int getLoadEndViewId () {
        return R.id.load_more_load_end_view;
    }
}
