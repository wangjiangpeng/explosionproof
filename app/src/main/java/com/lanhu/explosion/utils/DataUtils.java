package com.lanhu.explosion.utils;

public class DataUtils {

    public static String getSizeName(long size) {
        String result;
        if (size > 1024 * 1024 * 1024) {
            double d = size / (double) (1024 * 1024 * 1024);
            result = String.format("%.2f", d) + "GB";

        } else if (size > 1024 * 1024) {
            double d = size / (double) (1024 * 1024);
            result = String.format("%.2f", d) + "MB";

        } else if (size > 1024) {
            double d = size / (double) 1024;
            result = String.format("%.2f", d) + "KB";
        } else {
            result = size + "B";
        }
        return result;
    }

}
