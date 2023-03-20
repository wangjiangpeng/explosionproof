package com.lanhu.explosion;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lanhu.explosion.view.CommonView;
import com.lanhu.explosion.view.PictureView;

public class SettingsActivity extends Activity {

    private ViewGroup mCurrentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        mCurrentLayout = findViewById(R.id.settings_current_layout);

        mCurrentLayout.addView(new PictureView(SettingsActivity.this));

        findViewById(R.id.settings_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentLayout.removeAllViews();
                mCurrentLayout.addView(new PictureView(SettingsActivity.this));
            }
        });


        findViewById(R.id.settings_common).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("wangjiangpeng", "addView");
                mCurrentLayout.removeAllViews();
                mCurrentLayout.addView(new CommonView(SettingsActivity.this));
            }
        });


    }


}
