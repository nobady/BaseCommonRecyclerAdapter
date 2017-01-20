package com.library.adapter;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import com.library.adapter.animation.AlphaInAnimation;
import com.library.adapter.animation.BaseAnimation;
import com.library.adapter.animation.ScaleInAnimation;
import com.library.adapter.animation.SlideInBottomAnimation;
import com.library.adapter.animation.SlideInLeftAnimation;
import com.library.adapter.animation.SlideInRightAnimation;
import com.library.adapter.view.DefaultLoadMoreView;
import com.library.adapter.view.LoadMoreView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 公共的adapter，支持单布局和多布局、一句代码搞定item加载动画、滑倒底部自动加载更多、添加headView，footView等
 * Created by tengfei.lv on 2017/1/17.
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int HEAD_VIEW = -200;
    private static final int FOOT_VIEW = -201;
    private static final int LOAD_MORE_VIEW = -202;

    private Context mContext;

    private int layoutRes;

    private List<T> mData;

    private LinearLayout headViewLayout;

    private LinearLayout footViewLayout;

    private LoadMoreView loadMoreView;

    private boolean isEnableLoadMore;

    private boolean isLoading;
    private OnLoadMoreListener mLoadMoreListener;
    /*保存item的布局*/
    private SparseIntArray layoutArrays;

    /*动画相关*/
    /*默认的动画*/
    private BaseAnimation mDefaultAnimation = new AlphaInAnimation ();
    /*自定义的动画*/
    private BaseAnimation mCustomAnimation;
    /*加载动画的标志*/
    private boolean isLoadAnimation;

    private int mLastPosition = -1;
    /*控制动画以常量的速率改变*/
    private Interpolator mInterpolator = new LinearInterpolator ();
    /*动画执行的时间*/
    private int mDuration = 300;

    /**
     * Use with {@link #setLoadAnimationType(int)}
     */
    public static final int ALPHAIN = 0x00000001;
    /**
     * Use with {@link #setLoadAnimationType(int)}
     */
    public static final int SCALEIN = 0x00000002;
    /**
     * Use with {@link #setLoadAnimationType(int)}
     */
    public static final int SLIDEIN_BOTTOM = 0x00000003;
    /**
     * Use with {@link #setLoadAnimationType(int)}
     */
    public static final int SLIDEIN_LEFT = 0x00000004;
    /**
     * Use with {@link #setLoadAnimationType(int)}
     */
    public static final int SLIDEIN_RIGHT = 0x00000005;

    @IntDef ({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})
    @Retention (RetentionPolicy.SOURCE)
    public @interface AnimationType {
    }


    public void setDuration (int duration) {
        mDuration = duration;
    }

    public void setLoadAnimation (boolean loadAnimation) {
        isLoadAnimation = loadAnimation;
    }

    public void setLoadMoreListener (OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public BaseAdapter (Context context, int layoutRes) {
        mContext = context;
        mData = new ArrayList<> ();
        this.layoutRes = layoutRes;
        loadMoreView = new DefaultLoadMoreView ();
    }

    public BaseAdapter (Context context) {
        mContext = context;
        mData = new ArrayList<> ();
        loadMoreView = new DefaultLoadMoreView ();
        layoutArrays = new SparseIntArray ();
    }

    public void setCustomAnimation (BaseAnimation customAnimation) {
        mCustomAnimation = customAnimation;
    }

    public boolean isLoading () {
        return isLoading;
    }

    protected void addItemType (int type, @LayoutRes int layoutRes) {
        layoutArrays.put (type, layoutRes);
    }

    public void setEnableLoadMore (boolean enableLoadMore) {
        isEnableLoadMore = enableLoadMore;
    }

    public boolean isEnableLoadMore () {
        return isEnableLoadMore;
    }

    public void addAll (List<T> list) {
        mData.addAll (list);
        notifyItemInserted (mData.size () + getHeaderLayoutCount ());
    }

    public void setData (List<T> list) {
        mData.clear ();
        mData.addAll (list);
        mLastPosition = -1;
        notifyItemInserted (mData.size () + getHeaderLayoutCount ());
    }

    public void addHeadView (View headView, int index, int orientation) {
        if (headViewLayout == null) {
            headViewLayout = new LinearLayout (mContext);
            headViewLayout.setOrientation (orientation);
            if (orientation == LinearLayout.VERTICAL) {
                headViewLayout.setLayoutParams (
                    new RecyclerView.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                headViewLayout.setLayoutParams (
                    new RecyclerView.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
            headViewLayout.setBackgroundColor (Color.RED);
        }

        index = index >= headViewLayout.getChildCount () ? -1 : index;

        headViewLayout.addView (headView, index);

        if (headViewLayout.getChildCount () == 1) {
            notifyItemInserted (0);
        }
    }

    public void addHeadView (View headView, int index) {
        addHeadView (headView, index, 1);
    }

    public void addHeadView (View headView) {
        addHeadView (headView, -1);
    }

    public void setHeaderView (View header) {
        setHeaderView (header, 0, LinearLayout.VERTICAL);
    }

    public void setHeaderView (View header, int index) {
        setHeaderView (header, index, LinearLayout.VERTICAL);
    }

    public void setHeaderView (View header, int index, int orientation) {
        if (headViewLayout == null || headViewLayout.getChildCount () <= index) {
            addHeadView (header, index, orientation);
        } else {
            headViewLayout.removeViewAt (index);
            headViewLayout.addView (header, index);
        }
    }

    public void addFootView (View footView, int index, int orientation) {
        if (footViewLayout == null) {
            footViewLayout = new LinearLayout (mContext);
            footViewLayout.setOrientation (orientation);
            if (orientation == LinearLayout.VERTICAL) {
                footViewLayout.setLayoutParams (
                    new RecyclerView.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                footViewLayout.setLayoutParams (
                    new RecyclerView.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }

        index = index >= footViewLayout.getChildCount () ? -1 : index;

        footViewLayout.addView (footView, index);

        if (footViewLayout.getChildCount () == 1) {
            notifyItemInserted (mData.size () + getHeaderLayoutCount ());
        }
    }

    public void addFootView (View headView, int index) {
        addFootView (headView, index, 1);
    }

    public void addFootView (View headView) {
        addFootView (headView, -1);
    }

    public void setFootView (View footView) {
        setFootView (footView);
    }

    public void setFootView (View footView, int index) {
        setFootView (footView, index, LinearLayout.VERTICAL);
    }

    public void setFootView (View footView, int index, int orientation) {
        if (footViewLayout == null || footViewLayout.getChildCount () <= index) {
            addFootView (footView, index, orientation);
        } else {
            footViewLayout.removeViewAt (index);
            footViewLayout.addView (footView, index);
        }
    }

    public void removeHeadView (View headView) {
        if (getHeaderLayoutCount () != 0) {
            headViewLayout.removeView (headView);

            if (headViewLayout.getChildCount () == 0) {
                notifyItemRemoved (0);
            }
        }
    }

    public void removeAllHeadView () {
        if (getHeaderLayoutCount () != 0) {
            headViewLayout.removeAllViews ();
            notifyItemRemoved (0);
        }
    }

    public void removeFootView (View footView) {
        if (getFooterLayoutCount () != 0) {
            footViewLayout.removeView (footView);
            if (footViewLayout.getChildCount () == 0) {
                notifyItemRemoved (mData.size () + getHeaderLayoutCount ());
            }
        }
    }

    public void removeAllFootView () {
        if (getFooterLayoutCount () != 0) {
            footViewLayout.removeAllViews ();
            notifyItemRemoved (mData.size () + getHeaderLayoutCount ());
        }
    }

    public void setLoadMoreView (LoadMoreView loadMoreView) {
        this.loadMoreView = loadMoreView;
    }

    /**开始加载更多*/
    private void startLoadMore () {
        if (mLoadMoreListener != null
            && isEnableLoadMore
            && loadMoreView.getLoadMoreStatus () == 1) {
            loadMoreView.setLoadMoreStatus (LoadMoreView.STATUS_LOADING);
            isLoading = true;
            mLoadMoreListener.loadMoreRequest ();
        }
    }

    /**加载更多失败时调用*/
    public void loadMoreFail () {
        if (getLoadMoreViewCount () != 0) {
            isLoading = false;
            loadMoreView.setLoadMoreStatus (LoadMoreView.STATUS_FAIL);
            notifyItemChanged (getHeaderLayoutCount () + getFooterLayoutCount () + mData.size ());
        }
    }

    /**加载更多完成之后调用*/
    public void loadMoreComplete () {
        if (getLoadMoreViewCount () != 0) {
            isLoading = false;
            loadMoreView.setLoadMoreStatus (LoadMoreView.STATUS_DEFAULT);
            notifyItemChanged (getHeaderLayoutCount () + getFooterLayoutCount () + mData.size ());
        }
    }

    /**
     * 没有更多数据时调用，默认显示“没有更多数据的字样”
     */
    public void loadMoreEnd () {
        loadMoreEnd (false);
    }

    /**
     * 没有更多数据时调用
     *
     * @param gone
     *     true，不会显示“没有更多数据”的字样，false，则会显示
     */
    public void loadMoreEnd (boolean gone) {
        if (getLoadMoreViewCount () != 0) {
            isLoading = false;
            loadMoreView.setLoadMoreEndGone (gone);
            if (gone) {
                notifyItemRemoved (
                    getHeaderLayoutCount () + getFooterLayoutCount () + mData.size ());
            } else {
                loadMoreView.setLoadMoreStatus (LoadMoreView.STATUS_END);
                notifyItemChanged (
                    getHeaderLayoutCount () + getFooterLayoutCount () + mData.size ());
            }
        }
    }

    public int getHeaderLayoutCount () {
        return this.headViewLayout != null && this.headViewLayout.getChildCount () != 0 ? 1 : 0;
    }

    public int getFooterLayoutCount () {
        return this.footViewLayout != null && this.footViewLayout.getChildCount () != 0 ? 1 : 0;
    }

    /*获取加载更多的view，并且对加载失败时设置点击事件*/
    private BaseViewHolder getLoadMoreViewHolder (ViewGroup parent) {
        BaseViewHolder baseViewHolder = createBaseViewHolder (
            LayoutInflater.from (mContext).inflate (loadMoreView.getLayoutId (), parent, false));
        baseViewHolder.itemView.setOnClickListener (new View.OnClickListener () {
            @Override public void onClick (View v) {
                if (loadMoreView.getLoadMoreStatus () == LoadMoreView.STATUS_FAIL) {
                    loadMoreView.setLoadMoreStatus (LoadMoreView.STATUS_DEFAULT);
                    notifyItemChanged (
                        mData.size () + getHeaderLayoutCount () + getFooterLayoutCount ());
                }
            }
        });
        return baseViewHolder;
    }

    @Override public BaseViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEAD_VIEW:
                return createBaseViewHolder (headViewLayout);
            case FOOT_VIEW:
                return createBaseViewHolder (footViewLayout);
            case LOAD_MORE_VIEW:
                return getLoadMoreViewHolder (parent);
            default:
                int layout = layoutRes;
                if (layoutArrays != null) {
                    layout = layoutArrays.get (viewType);
                }
                return createBaseViewHolder (
                    LayoutInflater.from (mContext).inflate (layout, parent, false));
        }
    }

    @Override public void onBindViewHolder (BaseViewHolder holder, int position) {
        switch (holder.getItemViewType ()) {
            case HEAD_VIEW:
            case FOOT_VIEW:
                break;
            case LOAD_MORE_VIEW:
                loadMoreView.convert (holder);
                break;
            default:
                convert (holder, mData.get (position - getHeaderLayoutCount ()));
        }
    }

    /**
     * 根据position区分item type
     *
     * @param position
     *     如果小于headView的数量，那么就返回{@link #HEAD_VIEW};如果支持加载更多，并且不小于
     *     headViewLayout的数量+footViewLayout的数量+mData的大小，那么就返回{@link #LOAD_MORE_VIEW};
     *     如果不支持加载更多，并且不小于headViewLayout的数量+mData的大小，那么就返回{@link #FOOT_VIEW};
     *     否则就返回{@link #getUseItemViewType(int)}
     *
     * @return
     */
    @Override public int getItemViewType (int position) {
        if (headViewLayout != null) {
            if (position < getHeaderLayoutCount ()) {
                return HEAD_VIEW;
            }
        }
        if (footViewLayout != null) {
            if (getLoadMoreViewCount () != 0
                && position >= mData.size () + getFooterLayoutCount () + getHeaderLayoutCount ()) {
                startLoadMore ();
                return LOAD_MORE_VIEW;
            }
            if (position >= mData.size () + getHeaderLayoutCount ()) {
                return FOOT_VIEW;
            }
        }
        int adsPosition = position - getHeaderLayoutCount ();
        return getUseItemViewType (adsPosition);
    }

    /**如果是多布局，那么重写此方法，返回多个type即可*/
    protected int getUseItemViewType (int position) {
        return 0;
    }

    protected abstract void convert (BaseViewHolder holder, T bean);

    private BaseViewHolder createBaseViewHolder (View view) {
        return new BaseViewHolder (view);
    }

    @Override public int getItemCount () {
        int count = 0;
        if (headViewLayout != null && headViewLayout.getChildCount () > 0) {
            ++count;
        }
        if (footViewLayout != null && footViewLayout.getChildCount () > 0) {
            ++count;
        }
        return count + mData.size () + getLoadMoreViewCount ();
    }

    /*获取加载更多view 的数量*/
    private int getLoadMoreViewCount () {
        return mLoadMoreListener != null
            && isEnableLoadMore
            && !loadMoreView.isLoadEndMoreGone ()
            && mData.size () > 0 ? 1 : 0;
    }

    private GridLayoutManager.SpanSizeLookup mSpanSizeLookup;

    @Override public void onAttachedToRecyclerView (final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView (recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager ();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup (new GridLayoutManager.SpanSizeLookup () {
                @Override public int getSpanSize (int position) {
                    int type = getItemViewType (position);
                    if (mSpanSizeLookup == null) {
                        /*如果是头view、尾部view、加载更多view，那么每个列宽就是manager设置的列宽，
                        * 如果是正常的item，那么列宽就为1*/
                        return (type == HEAD_VIEW || type == FOOT_VIEW || type == LOAD_MORE_VIEW)
                            ? gridManager.getSpanCount () : 1;
                    } else {
                        return (type == HEAD_VIEW || type == FOOT_VIEW || type == LOAD_MORE_VIEW)
                            ? gridManager.getSpanCount ()
                            : mSpanSizeLookup.getSpanSize (position - getHeaderLayoutCount ());
                    }
                }
            });
        }
    }

    @Override public void onViewAttachedToWindow (BaseViewHolder holder) {
        super.onViewAttachedToWindow (holder);
        int itemViewType = holder.getItemViewType ();
        if (itemViewType == HEAD_VIEW
            || itemViewType == FOOT_VIEW
            || itemViewType == LOAD_MORE_VIEW) {
            setFullSpan (holder);
        } else {
            addAnimation (holder);
        }
    }

    private void setFullSpan (BaseViewHolder holder) {
        if (holder.itemView.getLayoutParams () instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams ();
            layoutParams.setFullSpan (true);
        }
    }

    private void addAnimation (BaseViewHolder holder) {
        if (isLoadAnimation) {
            if (holder.getLayoutPosition () > mLastPosition) {
                BaseAnimation animation = mCustomAnimation;
                if (mCustomAnimation == null) {
                    animation = mDefaultAnimation;
                }
                for (Animator anim : animation.getAnimators (holder.itemView)) {
                    startAnimator (anim);
                }
                mLastPosition = holder.getLayoutPosition ();
            }
        }
    }

    /**
     * 执行动画
     */
    private void startAnimator (Animator anim) {
        anim.setDuration (mDuration).start ();
        anim.setInterpolator (mInterpolator);
    }

    /**
     * 设置动画类型
     * @param type
     */
    public void setLoadAnimationType(@AnimationType int type){
        isLoadAnimation = true;
        switch (type){
            case ALPHAIN:
                mDefaultAnimation = new AlphaInAnimation ();
                break;
            case SCALEIN:
                mDefaultAnimation = new ScaleInAnimation ();
                break;
            case SLIDEIN_BOTTOM:
                mDefaultAnimation = new SlideInBottomAnimation ();
                break;
            case SLIDEIN_LEFT:
                mDefaultAnimation = new SlideInLeftAnimation ();
                break;
            case SLIDEIN_RIGHT:
                mDefaultAnimation = new SlideInRightAnimation ();
                break;
        }
    }

    /**
     * 加载更多监听器
     */
    public interface OnLoadMoreListener {
        void loadMoreRequest ();
    }
}
