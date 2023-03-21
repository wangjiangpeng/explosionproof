package com.lanhu.explosion.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lanhu.explosion.CommActivity;
import com.lanhu.explosion.ExplosionActivity;
import com.lanhu.explosion.GasCollectActivity;
import com.lanhu.explosion.R;
import com.lanhu.explosion.SettingsActivity;
import com.lanhu.explosion.bean.GasInfo;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.task.TaskCallback;
import com.lanhu.explosion.task.TaskService;
import com.lanhu.explosion.task.impl.CameraPickureTask;
import com.lanhu.explosion.task.impl.GasCollectTask;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.FileUtils;
import com.lanhu.explosion.utils.MemUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeView extends LinearLayout implements TaskCallback {

    TextView mMemTV;
    TextView mStatusTV;
    GridView mGasGV;
    TextureView sv;

    public HomeView(Context context) {
        super(context);
        init(context);
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.home_view, this);

        mMemTV = findViewById(R.id.home_view_men);
        mStatusTV = findViewById(R.id.home_view_status);
        mGasGV = findViewById(R.id.home_view_grid_gas);
        sv = findViewById(R.id.home_full_grid_gas_tv);

        findViewById(R.id.home_view_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExplosionActivity act = (ExplosionActivity)context;
                act.replaceFullHome();
            }
        });

        findViewById(R.id.home_view_comm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, CommActivity.class));
            }
        });

        findViewById(R.id.home_view_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, GasCollectActivity.class));
            }
        });

        findViewById(R.id.home_view_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SettingsActivity.class));
            }
        });

        findViewById(R.id.home_view_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPickureTask task = new CameraPickureTask();
                task.setTaskCallback(HomeView.this);
                task.execute(0, FileUtils.getPicturePath(getContext()), sv.getSurfaceTexture());
            }
        });

        //mGasGV.setAdapter(new GasAdapter(info));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        String sizeStr = DataUtils.getSizeName(MemUtils.getAvailableSize("/data"));
        mMemTV.setText(getContext().getString(R.string.explosion_mem, sizeStr));
        mStatusTV.setText("正常");

//        GasCollectTask task = TaskService.getInstance().getTask(GasCollectTask.class);
//        task.setTaskCallback(this);
//        task.execute();
    }

    @Override
    public void onFinished(ATask task, Object result) {
        if(task instanceof CameraPickureTask){
            boolean suc = (boolean) result;
            Log.e("WJP", "onFinished:"+result);
        }
    }

    private class GasAdapter extends BaseAdapter {

        GasInfo info;

        GasAdapter(GasInfo info) {
            this.info = info;
        }

        @Override
        public int getCount() {
            return info.list.size();
        }

        @Override
        public GasInfo.Item getItem(int position) {
            return info.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.explosion_item_gas, parent, false);
            }

            ImageView icon = convertView.findViewById(R.id.explosion_item_gas_icon);
            TextView name = convertView.findViewById(R.id.explosion_item_gas_name);
            TextView value = convertView.findViewById(R.id.explosion_item_gas_value);
            TextView status = convertView.findViewById(R.id.explosion_item_gas_status);
            TextView data = convertView.findViewById(R.id.explosion_item_gas_date);

            GasInfo.Item item = getItem(position);
            switch (item.type) {
                case GasInfo.TYPE_OXYGEN:
                    icon.setImageResource(R.mipmap.oxygen);
                    name.setText(R.string.explosion_hydrogen);
                    break;
                case GasInfo.TYPE_CO:
                    icon.setImageResource(R.mipmap.co);
                    name.setText(R.string.explosion_co);
                    break;
                case GasInfo.TYPE_BURN_GAS:
                    icon.setImageResource(R.mipmap.burn_gas);
                    name.setText(R.string.explosion_burn_gas);
                    break;
                case GasInfo.TYPE_HYDROGEN:
                    icon.setImageResource(R.mipmap.hydrogen);
                    name.setText(R.string.explosion_hydrogen);
                    break;
            }

            value.setText(String.valueOf(item.value));
            if (item.status == GasInfo.STATUS_OK) {
                status.setText(R.string.explosion_qualified);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            data.setText(dateFormat.format(new Date(item.time)));

            return convertView;
        }
    }

}
