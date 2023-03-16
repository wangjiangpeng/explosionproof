package com.lanhu.explosion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;


public class MainActivity extends Activity {

    UIHandler mUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mUIHandler = new UIHandler(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mUIHandler.removeMessages(0);
        mUIHandler.sendEmptyMessageDelayed(0,3000);
    }

    private class UIHandler extends Handler{

        WeakReference<MainActivity> weak;

        UIHandler(MainActivity main){
            weak = new WeakReference<>(main);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MainActivity main = weak.get();
            if(main != null){
                startActivity(new Intent(main, ExplosionActivity.class));
                main.finish();
            }
        }
    }
}
