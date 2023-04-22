package com.lanhu.explosion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lanhu.explosion.ExplosionActivity;
import com.lanhu.explosion.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFullView extends RelativeLayout {

    TextView mFullStatusTV;
    GridView mFullGasGV;

    public HomeFullView(Context context) {
        super(context);
        init(context);
    }

    public HomeFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeFullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.home_full_view, this);

        findViewById(R.id.home_full_small_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExplosionActivity act = (ExplosionActivity)context;
                act.replaceHome();
            }
        });

        //mGasGV.setAdapter(new GasAdapter(info));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

//        String sizeStr = DataUtils.getSizeName(MemUtils.getAvailableSize("/data"));
//        mMemTV.setText(getContext().getString(R.string.explosion_mem, sizeStr));
//        mStatusTV.setText("正常");

//        GasCollectTask task = TaskService.getInstance().getTask(GasCollectTask.class);
//        task.setTaskCallback(this);
//        task.execute();
    }


}
