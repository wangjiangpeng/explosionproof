package com.lanhu.explosion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.CommActivity;
import com.lanhu.explosion.ExplosionActivity;
import com.lanhu.explosion.GasCollectActivity;
import com.lanhu.explosion.R;
import com.lanhu.explosion.SettingsActivity;
import com.lanhu.explosion.bean.GasItem;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.task.TaskCallback;
import com.lanhu.explosion.task.TaskProgress;
import com.lanhu.explosion.task.TaskService;
import com.lanhu.explosion.task.impl.CameraPictureTask;
import com.lanhu.explosion.task.impl.CameraRecordTask;
import com.lanhu.explosion.task.impl.GasCollectTask;
import com.lanhu.explosion.utils.ButtonUtils;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.MemUtils;
import com.lanhu.explosion.widget.GasRecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeView extends LinearLayout implements TaskCallback {

    TextView mMemTV;
    TextView mStatusTV;
    GasRecyclerView mGasRV;
    RecyclerView mSettingsRV;
    TextureView sv;
    Button mRecordBtn;
    TextView mRecordTV;

    CameraRecordTask mCameraRecordTask;
    CameraPictureTask mCameraPictureTask;

    private SettingsItem[] mSetItems = {
            new SettingsItem(R.drawable.comm_dw, R.string.explosion_comm, CommActivity.class),
            new SettingsItem(R.drawable.collect_dw, R.string.explosion_collect, GasCollectActivity.class),
            new SettingsItem(R.drawable.setting_dw, R.string.explosion_setting, SettingsActivity.class)};

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

    @SuppressLint("WrongViewCast")
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.home_view, this);

        mMemTV = findViewById(R.id.home_view_men);
        mStatusTV = findViewById(R.id.home_view_status);
        mGasRV = findViewById(R.id.home_view_gas);
        mSettingsRV = findViewById(R.id.home_view_settings);
        sv = findViewById(R.id.home_full_grid_gas_tv);
        mRecordBtn = findViewById(R.id.home_view_open_camera);
        mRecordTV = findViewById(R.id.home_view_record_text);

        findViewById(R.id.home_view_fullscreen).setOnClickListener(v -> {
            ExplosionActivity act = (ExplosionActivity) context;
            act.replaceFullHome();
        });

        findViewById(R.id.home_view_take_picture).setOnClickListener(v -> {
            if (mCameraPictureTask.isRunning()){
                MToast.makeText(R.string.toast_take_picture_busy, Toast.LENGTH_SHORT).show();
            } else {
                mCameraPictureTask.reExecute(0, sv.getSurfaceTexture());
            }
        });

        mRecordBtn.setOnClickListener(v -> {
            if(mCameraRecordTask.isStopping()){
                MToast.makeText(R.string.toast_record_busy, Toast.LENGTH_SHORT).show();

            } else if (mCameraRecordTask.isRunning()){
                    mCameraRecordTask.stop();
                    mRecordTV.setText(R.string.explosion_open_camera);
            } else {
                mCameraRecordTask.reExecute();
                mRecordTV.setText(R.string.explosion_recording);
            }
        });

        mCameraRecordTask = TaskService.getInstance().getTask(CameraRecordTask.class);
        mCameraRecordTask.setTaskCallback(this);
        mCameraPictureTask = TaskService.getInstance().getTask(CameraPictureTask.class);
        mCameraPictureTask.setTaskCallback(this);

        GridLayoutManager settingsManager = new GridLayoutManager(getContext(), mSetItems.length, RecyclerView.VERTICAL, false);
        mSettingsRV.setAdapter(new SettingsRecyclerAdapter());
        mSettingsRV.setLayoutManager(settingsManager);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        String sizeStr = DataUtils.getSizeName(MemUtils.getAvailableSize("/data"));
        mMemTV.setText(getContext().getString(R.string.explosion_mem, sizeStr));
        updateGasView();

        TaskService.getInstance().getTask(GasCollectTask.class).setTaskCallback(this);
    }

    private void updateGasView(){
        if(GasItem.isAllStatusOK()){
            mStatusTV.setTextColor(getResources().getColor(R.color.depth_blue, null));
            mStatusTV.setText(R.string.explosion_normal);
        } else {
            mStatusTV.setTextColor(getResources().getColor(R.color.red, null));
            mStatusTV.setText(R.string.explosion_warn);
        }
        mGasRV.notifyDataSetChanged();
    }

    @Override
    public void onFinished(ATask task, Object result) {
        if (task instanceof GasCollectTask) {
            boolean suc = (boolean) result;
            if (suc) {
                updateGasView();
            }
        }
    }

    private class SettingsVH extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;
        View layout;

        public SettingsVH(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.home_settings_item_icon);
            name = itemView.findViewById(R.id.home_settings_item_name);
            layout = itemView.findViewById(R.id.home_settings_layout);
        }
    }

    private class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsVH> {

        @Override
        public SettingsVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SettingsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_settings_item, parent, false));
        }

        @Override
        public void onBindViewHolder(HomeView.SettingsVH holder, int position) {
            SettingsItem item = mSetItems[position];
            holder.icon.setImageResource(item.iconId);
            holder.name.setText(item.nameId);

            holder.layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContext().startActivity(new Intent(getContext(), item.jumpClass));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mSetItems.length;
        }
    }

    private class SettingsItem {
        int iconId;
        int nameId;
        Class jumpClass;

        SettingsItem(int iconId, int nameId, Class jumpClass) {
            this.iconId = iconId;
            this.nameId = nameId;
            this.jumpClass = jumpClass;
        }
    }

}
