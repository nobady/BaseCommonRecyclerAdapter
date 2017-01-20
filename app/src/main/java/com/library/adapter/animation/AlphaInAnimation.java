package com.library.adapter.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * 渐变动画
 * Created by tengfei.lv on 2017/1/19.
 */
public class AlphaInAnimation implements BaseAnimation {

    private static float default_from_alpha = 0f;
    private float mFrom;

    public AlphaInAnimation (float mFrom) {
        this.mFrom = mFrom;
    }

    public AlphaInAnimation () {
        this (default_from_alpha);
    }

    @Override public Animator[] getAnimators (View view) {
        return new Animator[] { ObjectAnimator.ofFloat (view, "alpha", mFrom, 1f) };
    }
}
