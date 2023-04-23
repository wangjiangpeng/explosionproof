package com.lanhu.explosion.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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

    public static String getFileFirstLine(String path) {
        File file = new File(path);
        if(!file.exists()){
            return null;
        }
        BufferedReader br = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            String str = br.readLine();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
