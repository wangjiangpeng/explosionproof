package com.lanhu.explosion.task.impl;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.R;
import com.lanhu.explosion.bean.BaseInfo;
import com.lanhu.explosion.bean.PictureInfo;
import com.lanhu.explosion.bean.RecordInfo;
import com.lanhu.explosion.encoder.MediaWrapper;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.FileUtils;

import java.io.File;

public class CameraRecordTask extends ATask {

    private static final String TAG = "CameraRecordTask";
    private static final int MIN_TIME = 5000;

    private String url = "rtmp://192.168.10.29/live/20";
    private int width = 640;
    private int height = 480;

    private MediaWrapper mMediaWrapper;
    private StopThread mStopThread;
    private long mStartTime;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mStartTime = SystemClock.elapsedRealtime();
    }


    @Override
    protected void onPostExecute(Object result) {
        boolean suc = (boolean) result;
        MToast.makeText(suc ? R.string.toast_record_finish : R.string.toast_record_err, Toast.LENGTH_LONG).show();
    }

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

            if (mMediaWrapper.isVideoError()) {
                if (mediaFile.exists()) {
                    mediaFile.delete();
                }
                return false;
            } else {
                DBManager db = DBManager.getInstance();
                db.insertRecord(new RecordInfo(mediaFile.getAbsolutePath(), mMediaWrapper.isConnected() ? BaseInfo.STATUS_UPLOAD_OK : BaseInfo.STATUS_UPLOAD_NO));
                return true;
            }
        }
    }

    public void stop() {
        if (mStopThread == null) {
            mStopThread = new StopThread();
            mStopThread.start();
        }
    }

    public boolean isStopping() {
        return mStopThread != null;
    }

    private class StopThread extends Thread {
        @Override
        public void run() {
            super.run();

            long diffTime = SystemClock.elapsedRealtime() - mStartTime;
            if (MIN_TIME > diffTime) {
                try {
                    Thread.sleep(MIN_TIME - diffTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mMediaWrapper.stopRecord();
            mStopThread = null;
        }
    }

}
