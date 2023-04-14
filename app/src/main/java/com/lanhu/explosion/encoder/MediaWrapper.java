package com.lanhu.explosion.encoder;

import android.media.MediaCodec;
import android.media.MediaMuxer;
import android.util.Log;

import com.io.rtmp.RTMPMuxer;
import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.utils.FileUtils;

import java.io.File;
import java.nio.ByteBuffer;

public class MediaWrapper {

    private static final String TAG = "MediaWrapper";

    private ReadThread mReadThread;
    private String mediaPath;
    private String url;
    private int width, height;

    public MediaWrapper() {

    }

    public void setMediaPath(String path) {
        this.mediaPath = path;
    }

    public void setRtmpUrl(String url) {
        this.url = url;
    }

    public void setVideoSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void startRecord() {
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    public void stopRecord() {
        mReadThread.recording = false;
    }

    public class ReadThread extends Thread {

        boolean recording = true;

        @Override
        public void run() {
            super.run();

            Log.e(TAG, "ReadThread start");
            AbsEncoder audioEncoder = new AudioEncoder();
            audioEncoder.start();
            AbsEncoder videoEncoder = new VideoEncoder(0, width, height);
            videoEncoder.start();

            RTMPMuxer rtmpMuxer = new RTMPMuxer();
            int ret = rtmpMuxer.open(url, width, height);
            Log.e(TAG, "rtmp ret:" + ret);

            MediaMuxer mediaMuxer = null;
            try {
                mediaMuxer = new MediaMuxer(mediaPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean suc = audioEncoder.addTrack(mediaMuxer);
            if (!suc) {
                Log.e(TAG, "audioTrack err");
                return;
            }
            suc = videoEncoder.addTrack(mediaMuxer);
            if (!suc) {
                Log.e(TAG, "videoTrack err");
                return;
            }

            mediaMuxer.start();

            MuxerThread audioThread = new MuxerThread(audioEncoder, mediaMuxer, rtmpMuxer);
            audioThread.start();

            MuxerThread videoThread = new MuxerThread(videoEncoder, mediaMuxer, rtmpMuxer);
            videoThread.start();

            while (recording) {
                audioEncoder.queueInputBuffer();
                // video do not queue, because it is auto queue
            }
            audioEncoder.endOfStream();
            videoEncoder.endOfStream();

            try {
                audioThread.join();
                videoThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (rtmpMuxer.isConnected()) {
                rtmpMuxer.close();
            }

            mediaMuxer.stop();
            mediaMuxer.release();

            audioEncoder.stop();
            videoEncoder.stop();
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

            Log.e(TAG, "MuxerThread start");

            while (!mEncoder.isEnd()) {
                mEncoder.writeSampleData(mMediaMuxer, mRtmpMuxer);
            }
            Log.e(TAG, "MuxerThread stop");
        }
    }

}
