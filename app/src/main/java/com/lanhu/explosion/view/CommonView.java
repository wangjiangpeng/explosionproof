package com.lanhu.explosion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.lanhu.explosion.R;

public class CommonView extends SettingsBaseView {


    public CommonView(Context context) {
        super(context);
    }

    public CommonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        setTitle(R.string.settings_common);

        LayoutInflater.from(context).inflate(R.layout.settings_common, mCurrentView);
    }

}
