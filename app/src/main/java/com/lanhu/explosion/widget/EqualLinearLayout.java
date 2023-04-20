package com.lanhu.explosion.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class EqualLinearLayout extends LinearLayout {
    public EqualLinearLayout(Context context) {
        super(context);
    }

    public EqualLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EqualLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EqualLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        ViewGroup.LayoutParams params = getLayoutParams();
        if(params.width == ViewGroup.LayoutParams.MATCH_PARENT){
            heightMeasureSpec = widthMeasureSpec;
        }
        if(params.height == ViewGroup.LayoutParams.MATCH_PARENT){
            widthMeasureSpec = heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
