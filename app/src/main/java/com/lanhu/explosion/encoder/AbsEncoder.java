package com.lanhu.explosion.encoder;

import android.media.MediaMuxer;

import com.io.rtmp.RTMPMuxer;

public abstract class AbsEncoder {


    public abstract void start();
    public abstract void stop();
    public abstract void queueInputBuffer();
    public abstract void writeSampleData(int trackIndex, MediaMuxer mediaMuxer, RTMPMuxer rtmpMuxer);
    public abstract void endOfStream();
    public abstract boolean isEnd();
    public abstract int addTrack(MediaMuxer mediaMuxer);

    protected long prevOutputPTSUs = 0;
    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        if (result < prevOutputPTSUs)
            result = prevOutputPTSUs;
        return result;
    }

}
