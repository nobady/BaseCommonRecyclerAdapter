package com.library.adapter.animation;

import android.animation.Animator;
import android.view.View;

/**
 * 如果需要自定义动画，需要实现此接口
 * Created by tengfei.lv on 2017/1/19.
 */
public interface BaseAnimation {
    Animator[] getAnimators(View view);
}
