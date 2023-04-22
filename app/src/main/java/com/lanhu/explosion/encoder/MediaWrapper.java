package com.lanhu.explosion.encoder;

import android.media.MediaMuxer;
import android.util.Log;

import com.io.rtmp.RTMPMuxer;

public class MediaWrapper {

    private static final String TAG = "MediaWrapper";

    private ReadThread mReadThread;
    private String savePath;
    private String url;
    private int width, height;
    private boolean isConnected = false;
    private boolean isRunning = false;
    private Object mLock = new Object();
    private boolean videoError = false;


    private static MediaWrapper sMediaWrapper;

    public static MediaWrapper getInstance() {
        if (sMediaWrapper == null) {
            sMediaWrapper = new MediaWrapper();
        }
        return sMediaWrapper;
    }

    private MediaWrapper() {

    }

    public void setSavePath(String path) {
        this.savePath = path;
    }

    public void setRtmpUrl(String url) {
        this.url = url;
    }

    public void setVideoSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void startRecord() {
        if (mReadThread == null) {
            isRunning = true;
            isConnected = false;
            videoError = false;
            mReadThread = new ReadThread();
            mReadThread.start();
        }
    }

    public void stopRecord() {
        if (mReadThread != null) {
            mReadThread.recording = false;
            mReadThread = null;
        }
    }

    public void waitStop() {
        if (isRunning) {
            synchronized (mLock) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isVideoError() {
        return videoError;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public class ReadThread extends Thread {

        boolean recording = true;

        @Override
        public void run() {
            super.run();

            Log.e(TAG, "ReadThread start");
            try {
                AbsEncoder audioEncoder = new AudioEncoder();
                audioEncoder.start();
                AbsEncoder videoEncoder = new VideoEncoder(0, width, height);
                videoEncoder.start();

                RTMPMuxer rtmpMuxer = new RTMPMuxer();
                int ret = rtmpMuxer.open(url, width, height);
                isConnected = (ret >= 0);
                Log.e(TAG, "rtmpMuxer open ret:" + ret);

                MediaMuxer mediaMuxer = null;
                try {
                    mediaMuxer = new MediaMuxer(savePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean audioSuc = audioEncoder.addTrack(mediaMuxer);
                if (!audioSuc) {
                    Log.e(TAG, "audioTrack err");
                    return;
                }
                boolean videoSuc = videoEncoder.addTrack(mediaMuxer);
                if (!videoSuc) {
                    Log.e(TAG, "videoTrack err");
                    return;
                }

                mediaMuxer.start();
                MuxerThread audioThread = new MuxerThread(audioEncoder, mediaMuxer, isConnected ? rtmpMuxer : null);
                audioThread.start();
                MuxerThread videoThread = new MuxerThread(videoEncoder, mediaMuxer, isConnected ? rtmpMuxer : null);
                videoThread.start();

                while (recording) {
                    audioEncoder.queueInputBuffer();
                    // video do not queue, because it is auto queue
                }
                audioEncoder.endOfStream();
                videoEncoder.endOfStream();

                audioThread.join();
                videoThread.join();

                if (isConnected) {
                    rtmpMuxer.close();
                }

                mediaMuxer.stop();
                mediaMuxer.release();
                audioEncoder.stop();
                videoEncoder.stop();

                videoError = videoEncoder.isError();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRunning = false;
            synchronized (mLock) {
                mLock.notify();
            }
            Log.e(TAG, "ReadThread stop");
        }
    }

    public class MuxerThread extends Thread {

        AbsEncoder mEncoder;
        MediaMuxer mMediaMuxer;
        RTMPMuxer mRtmpMuxer;

        MuxerThread(AbsEncoder encoder, MediaMuxer mediaMuxer, RTMPMuxer rtmpMuxer) {
            mEncoder = encoder;
            mMediaMuxer = mediaMuxer;
            mRtmpMuxer = rtmpMuxer;
        }

        @Override
        public void run() {
            super.run();

            while (!mEncoder.isEnd()) {
                mEncoder.writeSampleData(mMediaMuxer, mRtmpMuxer);
            }
        }
    }

}
