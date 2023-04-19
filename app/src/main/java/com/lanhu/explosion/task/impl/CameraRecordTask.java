package com.lanhu.explosion.task.impl;

import android.annotation.SuppressLint;
import android.util.Log;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.encoder.MediaWrapper;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.FileUtils;

import java.io.File;

public class CameraRecordTask extends ATask {

    private static final String TAG = "CameraRecordTask";

    private String url = "rtmp://192.168.10.29/live/20";
    private int width = 640;
    private int height = 480;

    boolean isRuned = false;

    MediaWrapper mMediaWrapper;

    @SuppressLint("MissingPermission")
    @Override
    protected Object doInBackground(Object... objs) {
        Log.e(TAG, "CameraRecordTask");
        File mediaFile = FileUtils.createMediaFile(AApplication.getInstance());

        mMediaWrapper = new MediaWrapper();
        mMediaWrapper.setMediaPath(mediaFile.getAbsolutePath());
        mMediaWrapper.setRtmpUrl(url);
        mMediaWrapper.setVideoSize(width, height);
        mMediaWrapper.startRecord();
        isRuned = true;

        return null;
    }

    public void stop() {
        isRuned = false;
        mMediaWrapper.stopRecord();
    }

    public boolean isRuned(){
        return isRuned;
    }

}
