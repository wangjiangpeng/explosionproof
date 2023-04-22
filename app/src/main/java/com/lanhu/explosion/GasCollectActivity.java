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
    GasCollectTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.gas_collect);

        mGasSb = findViewById(R.id.gas_collect_progress);
        mReadBtn = findViewById(R.id.gas_collect_read);
        mReadBtn.setBackgroundResource(R.drawable.round_blue);

        mTask = TaskService.getInstance().getTask(GasCollectTask.class);
        mTask.addTaskCallback(GasCollectActivity.this);
        mTask.setTaskProgress(GasCollectActivity.this);

        if (mTask.isRunning()) {
            mGasSb.setSecondaryProgress(mTask.getProcess());
            mReadBtn.setText(R.string.gas_collect_collecting);

        } else if (mTask.isFinished()) {
            mGasSb.setSecondaryProgress(mTask.getProcess());
            mReadBtn.setBackgroundResource(R.drawable.round_green);
            mReadBtn.setText(R.string.gas_collect_finish);

        } else {
            mReadBtn.setText(R.string.gas_collect_start);
        }


        mReadBtn.setOnClickListener(v -> {
            if (!mTask.isRunning()) {
                mReadBtn.setText(R.string.gas_collect_collecting);
                mTask.reExecute();
            }
        });

        findViewById(R.id.gas_collect_close).setOnClickListener(v -> {
                finish();
        });

        findViewById(R.id.gas_collect_progress).setEnabled(false);
    }

    @Override
    public void onFinished(ATask task, Object result) {
        if (task instanceof GasCollectTask) {
            boolean suc = (boolean) result;
            mGasSb.setSecondaryProgress(100);
            if (suc) {
                mReadBtn.setBackgroundResource(R.drawable.round_green);
                mReadBtn.setText(R.string.gas_collect_finish);
            } else {
                mReadBtn.setText(R.string.gas_collect_err);
            }
        }
    }

    @Override
    public void onProgressUpdate(ATask task, Object value) {
        mGasSb.setProgress((int) value);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mTask.removeTaskCallback(this);
    }
}
