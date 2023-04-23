package com.lanhu.explosion;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.lanhu.explosion.view.CommonView;
import com.lanhu.explosion.view.DeviceInfoView;
import com.lanhu.explosion.view.GasView;
import com.lanhu.explosion.view.PasswordView;
import com.lanhu.explosion.view.PictureView;
import com.lanhu.explosion.view.RecordView;
import com.lanhu.explosion.view.ServerView;

public class SettingsActivity extends Activity {

    private ViewGroup mCurrentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);
        mCurrentLayout = findViewById(R.id.settings_current_layout);

        mCurrentLayout.addView(new PictureView(SettingsActivity.this));

        findViewById(R.id.settings_back).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.settings_picture).setOnClickListener(v -> {
            replaceConnectView(new PictureView(SettingsActivity.this));
        });

        findViewById(R.id.settings_video).setOnClickListener(v -> {
            replaceConnectView(new RecordView(SettingsActivity.this));
        });

        findViewById(R.id.settings_collect).setOnClickListener(v -> {
            replaceConnectView(new GasView(SettingsActivity.this));
        });

        findViewById(R.id.settings_common).setOnClickListener(v -> {
            replaceConnectView(new CommonView(SettingsActivity.this));
        });

        findViewById(R.id.settings_device_info).setOnClickListener(v -> {
            replaceConnectView(new DeviceInfoView(SettingsActivity.this));
        });

        findViewById(R.id.settings_server).setOnClickListener(v -> {
            replaceConnectView(new ServerView(SettingsActivity.this));
        });

        findViewById(R.id.settings_password).setOnClickListener(v -> {
            replaceConnectView(new PasswordView(SettingsActivity.this));
        });

        replaceConnectView(new PictureView(SettingsActivity.this));
    }

    private void replaceConnectView(View view) {
        mCurrentLayout.removeAllViews();
        mCurrentLayout.addView(view);
    }

}
