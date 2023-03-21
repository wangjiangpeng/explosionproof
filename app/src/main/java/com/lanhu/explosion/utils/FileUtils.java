package com.lanhu.explosion.utils;

import android.content.Context;

public class FileUtils {

    public static String getPicturePath(Context context){
        return context.getFileStreamPath("picture").getAbsolutePath();
    }
}
