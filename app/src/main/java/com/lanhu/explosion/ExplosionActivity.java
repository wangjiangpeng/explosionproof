package com.lanhu.explosion;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanhu.explosion.bean.GasInfo;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.task.TaskCallback;
import com.lanhu.explosion.task.TaskService;
import com.lanhu.explosion.task.impl.GasCollectTask;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.MemUtils;
import com.lanhu.explosion.view.HomeFullView;
import com.lanhu.explosion.view.HomeView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExplosionActivity extends Activity{

    FrameLayout mLayout;

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.explosion);
        mLayout = findViewById(R.id.explosion_layout);
        replaceHome();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions()) {
                requestPermissions(NEEDED_PERMISSIONS, 0);
            }
        }
    }

    public void replaceHome(){
        mLayout.removeAllViews();
        mLayout.addView(new HomeView(this));
    }

    public void replaceFullHome(){
        mLayout.removeAllViews();
        mLayout.addView(new HomeFullView(this));
    }

    private boolean checkPermissions() {
        boolean allGranted = true;
        for (String neededPermission : NEEDED_PERMISSIONS) {
            allGranted &= checkSelfPermission(neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (!isAllGranted) {
                finish();
            }
        }
    }

}
