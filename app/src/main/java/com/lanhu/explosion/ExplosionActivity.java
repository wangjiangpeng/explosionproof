package com.lanhu.explosion;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.MemUtils;

public class ExplosionActivity extends Activity {

    TextView mMemTV;
    TextView mStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.explosion);

        mMemTV = findViewById(R.id.explosion_men);
        mStatusTV = findViewById(R.id.explosion_status);



    }


    @Override
    protected void onResume() {
        super.onResume();

        String sizeStr = DataUtils.getSizeName(MemUtils.getAvailableSize("/data"));
        mMemTV.setText(getString(R.string.explpsion_mem, sizeStr));
        mStatusTV.setText("正常");
    }


}
