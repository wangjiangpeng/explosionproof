package com.lanhu.explosion.task.impl;

import android.annotation.SuppressLint;
import android.util.Log;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.bean.BaseInfo;
import com.lanhu.explosion.bean.PictureInfo;
import com.lanhu.explosion.bean.RecordInfo;
import com.lanhu.explosion.encoder.MediaWrapper;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.FileUtils;

import java.io.File;

public class CameraRecordTask extends ATask {

    private static final String TAG = "CameraRecordTask";

    private String url = "rtmp://192.168.10.29/live/20";
    private int width = 640;
    private int height = 480;

    MediaWrapper mMediaWrapper;

    @SuppressLint("MissingPermission")
    @Override
    protected Object doInBackground(Object... objs) {
        Log.e(TAG, "CameraRecordTask");
        File mediaFile = FileUtils.createMediaFile(AApplication.getInstance());

        mMediaWrapper = MediaWrapper.getInstance();
        if (mMediaWrapper.isRunning()) {
            return false;

        } else {
            mMediaWrapper.setSavePath(mediaFile.getAbsolutePath());
            mMediaWrapper.setRtmpUrl(url);
            mMediaWrapper.setVideoSize(width, height);
            mMediaWrapper.startRecord();
            mMediaWrapper.waitStop();

            DBManager db = DBManager.getInstance();
            db.insertRecord(new RecordInfo(mediaFile.getAbsolutePath(), mMediaWrapper.isConnected() ? BaseInfo.STATUS_UPLOAD_OK : BaseInfo.STATUS_UPLOAD_NO));
            return true;
        }
    }

    public void stop() {
        mMediaWrapper.stopRecord();
    }

}
