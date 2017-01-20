package com.library.adapter.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
/**
 * Created by tengfei.lv on 2017/1/19.
 */
public class SlideInBottomAnimation implements BaseAnimation {
    @Override
    public Animator[] getAnimators(View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0)
        };
    }
}
