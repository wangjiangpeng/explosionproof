package com.lanhu.explosion.utils;

import android.os.StatFs;

public class MemUtils {

    public static long getTotalSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    public static long getAvailableSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }




}
