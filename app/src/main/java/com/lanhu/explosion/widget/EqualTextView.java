package com.lanhu.explosion.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class EqualTextView extends TextView {

    public EqualTextView(Context context) {
        super(context);
    }

    public EqualTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EqualTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EqualTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
