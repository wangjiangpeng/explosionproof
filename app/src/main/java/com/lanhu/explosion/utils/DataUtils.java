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

    public static String bytesToString(byte[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder(array.length * 6);
        sb.append('[');
        sb.append(Integer.toHexString(array[0] & 0xFF));
        for (int i = 1; i < array.length; i++) {
            sb.append(", ");
            sb.append(Integer.toHexString(array[i] & 0xFF));
        }
        sb.append(']');
        return sb.toString();
    }

    public static short byteArrayToShort(byte[] b, int index) {
        short value = (short) (b[index + 1] & 0xFF | (b[index] & 0xFF) << 8);
        return value;
    }

}
