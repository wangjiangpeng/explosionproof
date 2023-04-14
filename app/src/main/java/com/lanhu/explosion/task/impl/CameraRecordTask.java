package com.lanhu.explosion.task.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import com.io.rtmp.RTMPManager;
import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.encoder.MediaWrapper;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraRecordTask extends ATask {

    private static final String TAG = "CameraRecordTask";

    private String url = "rtmp://192.168.10.29/live/20";

    private int width = 640;
    private int height = 480;

    private RTMPManager mRTMPManager;
//    MediaRecorder mRecorder;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;

    boolean isRuned = false;

    MediaWrapper mMediaWrapper;

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            createCameraPreviewSession(cameraDevice);
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
        }

    };

    private void createCameraPreviewSession(CameraDevice cameraDevice) {
        try {
            List<Surface> surfaceList = new ArrayList<>();
            CaptureRequest.Builder mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Surface s = mRTMPManager.getSurface();
            mPreviewRequestBuilder.addTarget(s);
            surfaceList.add(s);

            cameraDevice.createCaptureSession(surfaceList,
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            if (null == cameraDevice) {
                                return;
                            }
                            try {
                                mCaptureSession = cameraCaptureSession;
                                cameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {

                        }

                    }, null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected Object doInBackground(Object... objs) {
        Log.e(TAG, "CameraRecordTask");
        File mediaFile = FileUtils.getMediaFilePath(AApplication.getInstance());

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
