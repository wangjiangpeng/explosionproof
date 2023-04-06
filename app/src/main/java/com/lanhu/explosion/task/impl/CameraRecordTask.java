package com.lanhu.explosion.task.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import com.io.rtmp.RTMPManager;
import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraRecordTask extends ATask {

    private String url = "rtmp://192.168.10.29/live/7";

    private int width = 320;
    private int height = 240;

    private RTMPManager mRTMPManager;
    MediaRecorder mRecorder;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;

    boolean isRuned = false;

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
            mPreviewRequestBuilder.addTarget(mRecorder.getSurface());
            surfaceList.add(s);
            surfaceList.add(mRecorder.getSurface());

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
        Log.e("WJP", "CameraRecordTask");
        try {
            mRTMPManager = RTMPManager.getInstance();
            mRTMPManager.setUrl(url);
            mRTMPManager.setSize(width, height);
            mRTMPManager.getSurface();
            mRTMPManager.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File recordPath = FileUtils.getRecordPath(AApplication.getInstance());
            if(!recordPath.exists()){
                recordPath.mkdirs();
            }

            File mVideoFile = new File(recordPath, String.valueOf(System.currentTimeMillis()) + ".mp4");
            mRecorder = new MediaRecorder();
            mRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setVideoEncodingBitRate(10000000);
            mRecorder.setVideoFrameRate(25);
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mRecorder.setVideoSize(width, height);

            mRecorder.setOutputFile(mVideoFile.getAbsolutePath());
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String cameraId = "0";
            CameraManager cameraManager = (CameraManager) AApplication.getInstance().getSystemService(Context.CAMERA_SERVICE);
            cameraManager.openCamera(cameraId, mStateCallback, new Handler(Looper.getMainLooper()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        isRuned = true;
        return null;
    }

    public void stop() {
        isRuned = false;
        try {
            mRTMPManager.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mCaptureSession.stopRepeating();
            mCaptureSession.close();
            mCameraDevice.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRuned(){
        return isRuned;
    }

}
