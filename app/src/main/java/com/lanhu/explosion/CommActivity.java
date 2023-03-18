package com.lanhu.explosion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;


public class CommActivity extends Activity {

    View mEditLayout,mSpeakLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.comm);

        mEditLayout = findViewById(R.id.comm_edit_layout);
        mSpeakLayout = findViewById(R.id.comm_speak_layout);


        findViewById(R.id.comm_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.comm_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditLayout.setVisibility(View.GONE);
                mSpeakLayout.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.comm_keyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditLayout.setVisibility(View.VISIBLE);
                mSpeakLayout.setVisibility(View.GONE);
            }
        });

    }


}
