package com.lanhu.explosion.encoder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import com.io.rtmp.RTMPMuxer;
import com.lanhu.explosion.AApplication;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VideoEncoder extends AbsEncoder {

    private static final String TAG = "VideoEncoder";

    private final static int FRAME_RATE = 20; // fps
    private final static int IFRAME_INTERVAL = 2;//关键帧间隔2s
    private final static int BYTE_RATE = 256000;

    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private Surface mSurface;
    private int mCameraId;
    private int mWidth, mHeight;
    private boolean end = false;

    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;

    private long timeStamp;
    private boolean cameraErr = false;

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
            if(mCaptureSession != null){
                try {
                    mCaptureSession.stopRepeating();
                    mCaptureSession.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cameraDevice.close();
            cameraErr = true;
        }

    };

    public VideoEncoder(int cameraId, int width, int height) {
        this.mCameraId = cameraId;
        this.mWidth = width;
        this.mHeight = height;
    }

    private void createCameraPreviewSession(CameraDevice cameraDevice) {
        try {
            List<Surface> surfaceList = new ArrayList<>();
            CaptureRequest.Builder mPreviewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mSurface);
            surfaceList.add(mSurface);

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
    public void start() {
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
//        format.setInteger(MediaFormat.KEY_BIT_RATE, mWidth * mHeight * FRAME_RATE);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BYTE_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mMediaCodec.createInputSurface();
        mMediaCodec.start();

        try {
            CameraManager cameraManager = (CameraManager) AApplication.getInstance().getSystemService(Context.CAMERA_SERVICE);
            cameraManager.openCamera(String.valueOf(mCameraId), mStateCallback, new Handler(Looper.getMainLooper()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        try {
            mCaptureSession.stopRepeating();
            mCaptureSession.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mCameraDevice.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMediaCodec.stop();
        mMediaCodec.release();
    }

    @Override
    public void queueInputBuffer() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeSampleData(MediaMuxer mediaMuxer, RTMPMuxer rtmpMuxer) {
        if (timeStamp != 0) {//2000毫秒后
            if (System.currentTimeMillis() - timeStamp >= 2_000) {
                Bundle params = new Bundle();
                //立即刷新 让下一帧是关键帧
                params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                mMediaCodec.setParameters(params);
                timeStamp = System.currentTimeMillis();
            }
        } else {
            timeStamp = System.currentTimeMillis();
        }
        int index = mMediaCodec.dequeueOutputBuffer(bufferInfo, -1);
        if (index >= 0) {
            if (bufferInfo.flags != MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                ByteBuffer buffer = mMediaCodec.getOutputBuffer(index);
                bufferInfo.presentationTimeUs = getPTSUs();
                mediaMuxer.writeSampleData(track, buffer, bufferInfo);
                prevOutputPTSUs = bufferInfo.presentationTimeUs;

                if (rtmpMuxer != null && rtmpMuxer.isConnected()) {
                    byte[] outData = new byte[bufferInfo.size];
                    buffer.get(outData);
                    rtmpMuxer.writeVideo(outData, 0, outData.length, System.currentTimeMillis());
                }
            } else {
                Log.e(TAG, "BUFFER_FLAG_END_OF_STREAM");
                end = true;
            }
            mMediaCodec.releaseOutputBuffer(index, false);
        }
    }

    @Override
    public void endOfStream() {
        mMediaCodec.signalEndOfInputStream();
    }

    @Override
    public boolean isEnd() {
        return end;
    }

    @Override
    public boolean isError() {
        return cameraErr;
    }

}
