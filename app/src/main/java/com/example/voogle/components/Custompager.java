package com.example.voogle.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class Custompager extends ViewPager {
    public boolean sideSwipeEnabled;

    public Custompager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        sideSwipeEnabled = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.sideSwipeEnabled)
            return super.onInterceptTouchEvent(ev);
        else return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.sideSwipeEnabled)
            return super.onTouchEvent(ev);
        else return false;
    }
}
