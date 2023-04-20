package com.lanhu.explosion.utils;

import android.content.Context;
import android.os.Environment;

import com.lanhu.explosion.AApplication;

import java.io.File;

public class FileUtils {

    private static File getPictureDir(Context context){
        return context.getFileStreamPath("picture");
    }

    private static File getRecordDir(Context context){
        return context.getFileStreamPath("record");
    }

    public static File createMediaFile(Context context){
        File dir = getRecordDir(context);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File f;
        while(true){
            f = new File(dir, String.valueOf(System.currentTimeMillis()) + ".mp4");
            if(!f.exists()){
                break;
            }
        }
        return f;
    }

    public static File createPictureFile(Context context){
        File dir = getPictureDir(context);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File f;
        while(true){
            f = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
            if(!f.exists()){
               break;
            }
        }
        return f;
    }

    public static void deleteFile(String path){
        File file = new File(path);
        if(file.exists()){
            if(file.isFile()){
                file.delete();
            } else if(file.isDirectory()){
                String[] fs = file.list();
                for(String f : fs){
                    deleteFile(f);
                }
            }
        }
    }

}
