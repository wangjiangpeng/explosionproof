package com.lanhu.explosion.encoder;

import android.media.MediaCodec;
import android.media.MediaMuxer;

import com.io.rtmp.RTMPMuxer;

public abstract class AbsEncoder {

    protected int track;
    protected MediaCodec mMediaCodec;

    public abstract void start();
    public abstract void stop();
    public abstract void queueInputBuffer();
    public abstract void writeSampleData(MediaMuxer mediaMuxer, RTMPMuxer rtmpMuxer);
    public abstract void endOfStream();
    public abstract boolean isEnd();
    public abstract boolean isError();

    public boolean addTrack(MediaMuxer mediaMuxer) {
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        int i = 0;
        while(i++ < 10){
            int index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100000);
            if(index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                track =  mediaMuxer.addTrack(mMediaCodec.getOutputFormat());
                return true;
            } else if(index >= 0){
                mMediaCodec.releaseOutputBuffer(index, false);
            }
        }
        return false;
    }

    protected long prevOutputPTSUs = 0;
    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        if (result < prevOutputPTSUs)
            result = prevOutputPTSUs;
        return result;
    }

}
