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
import android.widget.Button;
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
import com.lanhu.explosion.task.impl.CameraRecordTask;
import com.lanhu.explosion.task.impl.GasCollectTask;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.FileUtils;
import com.lanhu.explosion.utils.MemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeView extends LinearLayout implements TaskCallback {

    TextView mMemTV;
    TextView mStatusTV;
    GridView mGasGV;
    TextureView sv;

    GasInfo mInfo;
    Button mRecordBtn;
    TextView mRecordTV;

    public HomeView(Context context) {
        super(context);
        init(context);
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.home_view, this);

        mMemTV = findViewById(R.id.home_view_men);
        mStatusTV = findViewById(R.id.home_view_status);
        mGasGV = findViewById(R.id.home_view_grid_gas);
        sv = findViewById(R.id.home_full_grid_gas_tv);
        mRecordBtn = findViewById(R.id.home_view_open_camera);
        mRecordTV = findViewById(R.id.home_view_record_text);

        findViewById(R.id.home_view_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExplosionActivity act = (ExplosionActivity) context;
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
                task.addTaskCallback(HomeView.this);
                task.execute(0, FileUtils.getPicturePath(getContext()), sv.getSurfaceTexture());
            }
        });

        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraRecordTask task = TaskService.getInstance().getTask(CameraRecordTask.class);
                if(!task.isRuned()){
                    task.reExecute();
                    mRecordTV.setText(R.string.explosion_recording);
                } else {
                    task.stop();
                    mRecordTV.setText(R.string.explosion_open_camera);
                }
            }
        });

        mInfo = GasInfo.sInfo;
        mGasGV.setAdapter(new GasAdapter(mInfo));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        String sizeStr = DataUtils.getSizeName(MemUtils.getAvailableSize("/data"));
        mMemTV.setText(getContext().getString(R.string.explosion_mem, sizeStr));
        mStatusTV.setText("正常");

        TaskService.getInstance().getTask(GasCollectTask.class).addTaskCallback(this);
    }

    @Override
    public void onFinished(ATask task, Object result) {
        if (task instanceof CameraPickureTask) {
            boolean suc = (boolean) result;
            Log.e("WJP", "onFinished:" + result);

        } else if (task instanceof GasCollectTask) {
            if (result != null) {
                mInfo = (GasInfo) result;
                mGasGV.setAdapter(new GasAdapter(mInfo));
            }
        }
    }

    private class GasAdapter extends BaseAdapter {

        GasInfo info;

        GasAdapter(GasInfo info) {
            this.info = info;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return info;
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

            switch (position) {
                case 0:
                    icon.setImageResource(R.mipmap.oxygen);
                    name.setText(R.string.explosion_hydrogen);
                    value.setText(String.valueOf(info.O2));
                    if (info.status_O2 == GasInfo.STATUS_OK) {
                        status.setTextColor(getResources().getColor(R.color.green, null));
                        status.setText(R.string.explosion_qualified);
                    } else {
                        status.setTextColor(getResources().getColor(R.color.red, null));
                        status.setText(R.string.explosion_warn);
                    }
                    break;
                case 1:
                    icon.setImageResource(R.mipmap.co);
                    name.setText(R.string.explosion_co);
                    value.setText(String.valueOf(info.CO));
                    if (info.status_CO == GasInfo.STATUS_OK) {
                        status.setTextColor(getResources().getColor(R.color.green, null));
                        status.setText(R.string.explosion_qualified);
                    } else {
                        status.setTextColor(getResources().getColor(R.color.red, null));
                        status.setText(R.string.explosion_warn);
                    }
                    break;
                case 2:
                    icon.setImageResource(R.mipmap.burn_gas);
                    name.setText(R.string.explosion_burn_gas);
                    value.setText(String.valueOf(info.CH4));
                    if (info.status_CH4 == GasInfo.STATUS_OK) {
                        status.setTextColor(getResources().getColor(R.color.green, null));
                        status.setText(R.string.explosion_qualified);
                    } else {
                        status.setTextColor(getResources().getColor(R.color.red, null));
                        status.setText(R.string.explosion_warn);
                    }
                    break;
                case 3:
                    icon.setImageResource(R.mipmap.hydrogen);
                    name.setText(R.string.explosion_hydrogen);
                    value.setText(String.valueOf(info.H2S));
                    if (info.status_H2S == GasInfo.STATUS_OK) {
                        status.setTextColor(getResources().getColor(R.color.green, null));
                        status.setText(R.string.explosion_qualified);
                    } else {
                        status.setTextColor(getResources().getColor(R.color.red, null));
                        status.setText(R.string.explosion_warn);
                    }
                    break;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            data.setText(dateFormat.format(new Date(info.time)));

            return convertView;
        }
    }

}
