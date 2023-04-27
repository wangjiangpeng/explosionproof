package com.lanhu.explosion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lanhu.explosion.ExplosionActivity;
import com.lanhu.explosion.R;
import com.lanhu.explosion.bean.GasItem;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.task.TaskCallback;
import com.lanhu.explosion.task.TaskProgress;
import com.lanhu.explosion.task.TaskService;
import com.lanhu.explosion.task.impl.CameraPictureTask;
import com.lanhu.explosion.task.impl.CameraRecordTask;
import com.lanhu.explosion.task.impl.GasCollectTask;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.widget.GasRecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HomeFullView extends RelativeLayout implements TaskCallback, TaskProgress {

    TextView mStatusTV;
    GasRecyclerView mGasRV;
    Button mRecordBtn;
    TextView mRecordTV;
    TextureView sv;
    TextView mRecordTime;

    CameraRecordTask mCameraRecordTask;
    CameraPictureTask mCameraPictureTask;

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
        mStatusTV = findViewById(R.id.home_full_status);
        mGasRV = findViewById(R.id.home_full_grid_gas);
        mRecordBtn = findViewById(R.id.home_full_open_camera);
        mRecordTV = findViewById(R.id.home_view_record_text);
        sv = findViewById(R.id.home_full_grid_gas_tv);
        mRecordTime = findViewById(R.id.home_view_record_time);

        findViewById(R.id.home_full_small_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExplosionActivity act = (ExplosionActivity)context;
                act.replaceHome();
            }
        });

        findViewById(R.id.home_full_take_picture).setOnClickListener(v -> {
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
        mCameraRecordTask.setTaskProgress(this);
        mCameraPictureTask = TaskService.getInstance().getTask(CameraPictureTask.class);
        mCameraPictureTask.setTaskCallback(this);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

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
        } else if (task instanceof CameraRecordTask) {
            mRecordTime.setText("");
        }
    }

    @Override
    public void onProgressUpdate(ATask task, Object value) {
        if (task instanceof CameraRecordTask) {
            long time = (long)value;
            time += 16 * 3600 * 1000;
            String timeStr = DataUtils.getTime("HH:mm:ss", time);
            mRecordTime.setText(getResources().getString(R.string.explosion_record_time, timeStr));
        }
    }
}
