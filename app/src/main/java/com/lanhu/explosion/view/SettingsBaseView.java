package com.lanhu.explosion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lanhu.explosion.R;


public class SettingsBaseView extends LinearLayout {

    private TextView mTitleTV;
    protected ViewGroup mCurrentView;

    public SettingsBaseView(Context context) {
        super(context);
        init(context);
    }

    public SettingsBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SettingsBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.settings_base, this);
        mTitleTV = findViewById(R.id.setting_base_title);
        mCurrentView = findViewById(R.id.setting_base_layout);
    }

    public void setTitle(int id){
        mTitleTV.setText(id);
    }

    public void setCurrentView(View view){
        mCurrentView.addView(view);
    }

}
