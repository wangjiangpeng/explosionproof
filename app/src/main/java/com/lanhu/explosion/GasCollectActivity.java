package com.lanhu.explosion;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

public class GasCollectActivity extends Activity {

    SeekBar mGasSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.gas_collect);

        mGasSb = findViewById(R.id.gas_collect_progress);
        mGasSb.setMax(100);
        mGasSb.setProgress(80);

    }
}
