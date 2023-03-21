package com.lanhu.explosion.task.impl;

import com.lanhu.explosion.task.ATask;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadTask extends ATask {

    @Override
    protected Object doInBackground(Object... objs) {
        String address = (String) objs[0];
        String path = (String) objs[1];
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();

            FileInputStream fis = new FileInputStream(path);
            byte[] bs = new byte[1024 * 10];
            int len;
            while ((len = fis.read(bs)) != -1) {
                out.write(bs, 0, len);
            }

            fis.close();
            out.close();
            conn.getResponseCode();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
