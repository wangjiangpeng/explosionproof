package com.lanhu.explosion.view;

import android.content.Context;
import android.util.AttributeSet;

import com.lanhu.explosion.R;

public class PictureView extends SettingsBaseView {


    public PictureView(Context context) {
        super(context);
    }

    public PictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        setTitle(R.string.settings_picture);
    }

}
