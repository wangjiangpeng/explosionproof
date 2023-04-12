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

    public void setMediaPath(String path){
        this.mediaPath = path;
    }

    public void setRtmpUrl(String url){
        this.url = url;
    }

    public void setVideoSize(int width, int height){
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
            AudioEncoder audioEncoder = new AudioEncoder();
            audioEncoder.start();
            MuxerThread muxerThread = new MuxerThread(audioEncoder);
            muxerThread.start();
            while (recording) {
                audioEncoder.queueInputBuffer();
            }
            audioEncoder.endOfStream();

            try {
                muxerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            audioEncoder.stop();
            Log.e(TAG, "ReadThread stop");
        }
    }

    public class MuxerThread extends Thread {

        AudioEncoder mAudioEncoder;

        MuxerThread(AudioEncoder audioEncoder){
            mAudioEncoder = audioEncoder;
        }

        @Override
        public void run() {
            super.run();

            Log.e(TAG, "MuxerThread start");
            RTMPMuxer rtmpMuxer = new RTMPMuxer();
            int ret = rtmpMuxer.open(url, width, height);
            Log.e(TAG, "rtmp ret:"+ret);

            MediaMuxer mediaMuxer = null;
            try {
                mediaMuxer = new MediaMuxer(mediaPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int audioTrack = mAudioEncoder.addTrack(mediaMuxer);
            if(audioTrack == -1){
                Log.e(TAG, "addTrack err");
                return;
            }

            mediaMuxer.start();
            while (true) {
                if(!mAudioEncoder.isEnd()){
                    mAudioEncoder.writeSampleData(audioTrack, mediaMuxer, rtmpMuxer);
                }
                if(mAudioEncoder.isEnd()){
                    break;
                }
            }

            mediaMuxer.stop();
            mediaMuxer.release();

            if(rtmpMuxer.isConnected()) {
                rtmpMuxer.close();
            }
            Log.e(TAG, "MuxerThread stop");
        }
    }

}
