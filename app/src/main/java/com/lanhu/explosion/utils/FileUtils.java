package com.lanhu.explosion.utils;

import android.content.Context;
import android.os.Environment;

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
//        File recordPath = FileUtils.getRecordPath(context);
//        if(!recordPath.exists()){
//            recordPath.mkdirs();
//        }
//        //File videoFile = new File(recordPath, String.valueOf(System.currentTimeMillis()) + ".mp4");
//        File videoFile = new File(recordPath, "test.mp4");
//        if(videoFile.exists()){
//            videoFile.delete();
//        }

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "roof");
        dir.mkdirs();
        File videoFile = new File(dir, "test.mp4");
        if(videoFile.exists()){
            videoFile.delete();
        }

        return videoFile;
    }
}
