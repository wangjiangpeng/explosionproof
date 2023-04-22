package com.lanhu.explosion;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.lanhu.explosion.view.HomeFullView;
import com.lanhu.explosion.view.HomeView;

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
