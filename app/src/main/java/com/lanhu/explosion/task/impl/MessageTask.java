package com.lanhu.explosion.task.impl;

import android.util.Log;

import com.lanhu.explosion.net.HttpClient;
import com.lanhu.explosion.net.RequestParamFactory;
import com.lanhu.explosion.net.ResponseData;
import com.lanhu.explosion.task.ATask;

public class MessageTask extends ATask {
    @Override
    protected Object doInBackground(Object... objs) {
        HttpClient request = new HttpClient();
        ResponseData data = request.request(RequestParamFactory.createTestParam());
        if (data.isSuccessful()) {
            String str = new String(data.getData());
            Log.e("TestTask", str);

        } else {
            Log.e("TestTask", "error");
        }
        return null;
    }
}
