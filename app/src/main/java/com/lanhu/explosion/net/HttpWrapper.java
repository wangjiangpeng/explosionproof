package com.lanhu.explosion.net;

import com.squareup.okhttp.FormEncodingBuilder;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpWrapper {

    RequestParam param;

    public HttpWrapper(RequestParam param){
        this.param = param;
    }

    public void send(){
        try {
            URL url = new URL(param.getUrl());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            Map<String, String> posts = param.getPosts();
                // post
                if (posts.size() > 0) {
                    conn.setRequestMethod("POST");
                    for (String key : posts.keySet()) {
                        conn.setRequestProperty(key, posts.get(key));
                    }
                }else {
                    conn.setRequestMethod("GET");
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
