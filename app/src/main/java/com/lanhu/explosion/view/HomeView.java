package com.lanhu.explosion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
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

import com.lanhu.explosion.CommActivity;
import com.lanhu.explosion.ExplosionActivity;
import com.lanhu.explosion.GasCollectActivity;
import com.lanhu.explosion.R;
import com.lanhu.explosion.SettingsActivity;
import com.lanhu.explosion.bean.GasItem;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.task.TaskCallback;
import com.lanhu.explosion.task.TaskService;
import com.lanhu.explosion.task.impl.CameraPictureTask;
import com.lanhu.explosion.task.impl.CameraRecordTask;
import com.lanhu.explosion.task.impl.GasCollectTask;
import com.lanhu.explosion.utils.ButtonUtils;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.MemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeView extends LinearLayout implements TaskCallback {

    TextView mMemTV;
    TextView mStatusTV;
    RecyclerView mGasRV;
    RecyclerView mSettingsRV;
    TextureView sv;

    Button mRecordBtn;
    TextView mRecordTV;

    GasRecyclerAdapter mAdapter;

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
            if (ButtonUtils.isFastDoubleClick(R.id.home_view_take_picture)) {
                MToast.makeText(R.string.toast_time_short, Toast.LENGTH_SHORT).show();
            } else {
                CameraPictureTask task = new CameraPictureTask();
                task.setTaskCallback(HomeView.this);
                task.reExecute(0, sv.getSurfaceTexture());
            }
        });

        mRecordBtn.setOnClickListener(v -> {
            if (ButtonUtils.isFastDoubleClick(R.id.home_view_take_picture)) {
                MToast.makeText(R.string.toast_time_short, Toast.LENGTH_SHORT).show();
            } else {
                CameraRecordTask task = TaskService.getInstance().getTask(CameraRecordTask.class);
                if (!task.isRunning()) {
                    task.setTaskCallback(HomeView.this);
                    task.reExecute();
                    mRecordTV.setText(R.string.explosion_recording);
                } else {
                    task.stop();
                    mRecordTV.setText(R.string.explosion_open_camera);
                }
            }
        });

        GridLayoutManager gasManager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);
        mGasRV.setAdapter(mAdapter = new GasRecyclerAdapter());
        mGasRV.setLayoutManager(gasManager);

        GridLayoutManager settingsManager = new GridLayoutManager(getContext(), mSetItems.length, RecyclerView.VERTICAL, false);
        mSettingsRV.setAdapter(new SettingsRecyclerAdapter());
        mSettingsRV.setLayoutManager(settingsManager);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        String sizeStr = DataUtils.getSizeName(MemUtils.getAvailableSize("/data"));
        mMemTV.setText(getContext().getString(R.string.explosion_mem, sizeStr));
        mStatusTV.setText("正常");

        TaskService.getInstance().getTask(GasCollectTask.class).setTaskCallback(this);
    }

    @Override
    public void onFinished(ATask task, Object result) {
        if (task instanceof GasCollectTask) {
            boolean suc = (boolean) result;
            if (suc) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class GasVH extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name;
        TextView value;
        TextView status;
        TextView data;

        public GasVH(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.explosion_item_gas_icon);
            name = itemView.findViewById(R.id.explosion_item_gas_name);
            value = itemView.findViewById(R.id.explosion_item_gas_value);
            status = itemView.findViewById(R.id.explosion_item_gas_status);
            data = itemView.findViewById(R.id.explosion_item_gas_date);
        }
    }

    private class GasRecyclerAdapter extends RecyclerView.Adapter<GasVH> {

        GasRecyclerAdapter() {
        }

        @Override
        public GasVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GasVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.explosion_item_gas, parent, false));
        }

        @Override
        public void onBindViewHolder(GasVH holder, int position) {
            GasItem item = GasItem.mList.get(position);
            holder.icon.setImageResource(item.getIconId());
            holder.name.setText(item.getNameId());
            holder.value.setText(String.valueOf(item.getValueUnit()));
            if (item.getStatus() == GasItem.STATUS_OK) {
                holder.status.setTextColor(getResources().getColor(R.color.green, null));
                holder.status.setText(R.string.explosion_qualified);
            } else {
                holder.status.setTextColor(getResources().getColor(R.color.red, null));
                holder.status.setText(R.string.explosion_warn);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holder.data.setText(dateFormat.format(new Date(item.getTime())));
        }

        @Override
        public int getItemCount() {
            return 4;
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
