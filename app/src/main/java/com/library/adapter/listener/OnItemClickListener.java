package com.library.adapter.listener;

import android.view.View;
import com.library.adapter.BaseAdapter;

/**
 * Created by AllenCoder on 2016/8/03.
 *
 *
 * A convenience class to extend when you only want to OnItemClickListener for a subset
 * of all the SimpleClickListener. This implements all methods in the
 * {@link SimpleClickListener}
 */
public abstract   class OnItemClickListener extends SimpleClickListener {


    @Override
    public void onItemClick(BaseAdapter adapter, View view, int position) {
        onSimpleItemClick(adapter,view,position);
    }

    @Override
    public void onItemLongClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseAdapter adapter, View view, int position) {

    }
    public abstract void onSimpleItemClick(BaseAdapter adapter, View view, int position);
}
