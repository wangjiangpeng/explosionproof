package com.lanhu.explosion.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import com.lanhu.explosion.R;

@SuppressLint("AppCompatCustomView")
public class GasSeekBar extends SeekBar {

    public GasSeekBar(Context context) {
        super(context);
        init();
    }

    public GasSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GasSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setThumb(getResources().getDrawable(R.drawable.cycle_prepare));
        setProgressDrawable(getResources().getDrawable(R.drawable.progress_color_horizontal));
        //setThumbOffset(0);
        setPadding(20, 0, 20, 0);

        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0){
                    setThumb(getResources().getDrawable(R.drawable.cycle_prepare));
                } else if(progress == getMax()){
                    setThumb(getResources().getDrawable(R.drawable.cycle_finish));
                } else {
                    setThumb(getResources().getDrawable(R.drawable.cycle_waiting));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
