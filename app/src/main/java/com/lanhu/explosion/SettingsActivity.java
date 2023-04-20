package com.lanhu.explosion;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lanhu.explosion.view.CommonView;
import com.lanhu.explosion.view.PictureView;
import com.lanhu.explosion.view.RecordView;

public class SettingsActivity extends Activity {

    private ViewGroup mCurrentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        mCurrentLayout = findViewById(R.id.settings_current_layout);

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
                replaceConnectView(new PictureView(SettingsActivity.this));
            }
        });

        findViewById(R.id.settings_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceConnectView(new RecordView(SettingsActivity.this));
            }
        });

        replaceConnectView(new PictureView(SettingsActivity.this));

    }

    private void replaceConnectView(View view){
        mCurrentLayout.removeAllViews();
        mCurrentLayout.addView(view);
    }


}
