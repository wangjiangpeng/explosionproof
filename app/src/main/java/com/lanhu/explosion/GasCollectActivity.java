package com.lanhu.explosion;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.task.TaskCallback;
import com.lanhu.explosion.task.TaskProgress;
import com.lanhu.explosion.task.TaskService;
import com.lanhu.explosion.task.impl.GasCollectTask;

public class GasCollectActivity extends Activity implements TaskCallback, TaskProgress {

    SeekBar mGasSb;

    Button mReadBtn;

    ATask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.gas_collect);

        mGasSb = findViewById(R.id.gas_collect_progress);
        mReadBtn = findViewById(R.id.gas_collect_read);

        mReadBtn.setBackgroundResource(R.drawable.round_blue);
        mReadBtn.setText(R.string.gas_collect_start);

        mReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTask.addTaskCallback(GasCollectActivity.this);
                mTask.setTaskProgress(GasCollectActivity.this);
                mTask.reExecute();
            }
        });

        findViewById(R.id.gas_collect_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.gas_collect_progress).setEnabled(false);

        mTask = TaskService.getInstance().getTask(GasCollectTask.class);
    }

    @Override
    public void onFinished(ATask task, Object result) {
        if(result != null){
            mReadBtn.setBackgroundResource(R.drawable.round_green);
            mReadBtn.setText(R.string.gas_collect_start);
            mGasSb.setSecondaryProgress(100);

        } else {
            mReadBtn.setText(R.string.gas_collect_err);
        }
    }

    @Override
    public void onProgressUpdate(ATask task, Object value) {
        mGasSb.setProgress((int)value);
        mReadBtn.setText(R.string.gas_collect_collecting);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mTask.removeTaskCallback(this);
    }
}
