package com.lanhu.explosion.utils;

import android.content.Context;

import java.io.File;

public class FileUtils {

    public static String getPicturePath(Context context){
        return context.getFileStreamPath("picture").getAbsolutePath();
    }

    public static File getRecordPath(Context context){
        return context.getFileStreamPath("record");
    }
}
