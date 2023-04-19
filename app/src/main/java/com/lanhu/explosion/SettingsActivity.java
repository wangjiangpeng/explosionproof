package com.lanhu.explosion;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lanhu.explosion.view.CommonView;
import com.lanhu.explosion.view.PictureView;

public class SettingsActivity extends Activity {

    private ViewGroup mCurrentLayout;
    private TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        mCurrentLayout = findViewById(R.id.settings_current_layout);
        mTitleTv = findViewById(R.id.settings_current_title);

        mCurrentLayout.addView(new PictureView(SettingsActivity.this));

        findViewById(R.id.settings_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.settings_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceConnectView(R.string.settings_picture, new PictureView(SettingsActivity.this));
            }
        });


        findViewById(R.id.settings_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceConnectView(R.string.settings_video, new PictureView(SettingsActivity.this));
            }
        });

        replaceConnectView(R.string.settings_picture, new PictureView(SettingsActivity.this));

    }

    private void replaceConnectView(int titleId, View view){
        mTitleTv.setText(titleId);
        mCurrentLayout.removeAllViews();
        mCurrentLayout.addView(view);
    }


}
