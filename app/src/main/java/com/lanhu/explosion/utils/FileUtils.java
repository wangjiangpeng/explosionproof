package com.lanhu.explosion.utils;

import android.content.Context;

import com.lanhu.explosion.AApplication;

import java.io.File;

public class FileUtils {

    public static String getPicturePath(Context context){
        return context.getFileStreamPath("picture").getAbsolutePath();
    }

    public static File getRecordPath(Context context){
        return context.getFileStreamPath("record");
    }

    public static File getMediaFilePath(Context context){
        File recordPath = FileUtils.getRecordPath(context);
        if(!recordPath.exists()){
            recordPath.mkdirs();
        }
        File videoFile = new File(recordPath, String.valueOf(System.currentTimeMillis()) + ".mp4");
        return videoFile;
    }
}
