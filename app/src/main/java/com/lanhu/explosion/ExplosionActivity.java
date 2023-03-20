package com.lanhu.explosion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lanhu.explosion.bean.GasInfo;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.task.TaskCallback;
import com.lanhu.explosion.task.TaskService;
import com.lanhu.explosion.task.impl.GasCollectTask;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.MemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExplosionActivity extends Activity implements TaskCallback {

    TextView mMemTV;
    TextView mStatusTV,mFullStatusTV;
    GridView mGasGV, mFullGasGV;
    View mSmallView, mFullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.explosion);

        mMemTV = findViewById(R.id.explosion_men);
        mStatusTV = findViewById(R.id.explosion_status);
        mFullStatusTV = findViewById(R.id.explosion_full_status);
        mGasGV = findViewById(R.id.explosion_grid_gas);
        mFullGasGV = findViewById(R.id.explosion_full_grid_gas);
        mSmallView = findViewById(R.id.explosion_small_screen);
        mFullView = findViewById(R.id.explosion_full_screen);

        findViewById(R.id.explosion_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmallView.setVisibility(View.GONE);
                mFullView.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.explosion_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmallView.setVisibility(View.GONE);
                mFullView.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.explosion_full_small_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmallView.setVisibility(View.VISIBLE);
                mFullView.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.explosion_comm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExplosionActivity.this, CommActivity.class));
            }
        });

        findViewById(R.id.explosion_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExplosionActivity.this, GasCollectActivity.class));
            }
        });

        findViewById(R.id.explosion_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExplosionActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sizeStr = DataUtils.getSizeName(MemUtils.getAvailableSize("/data"));
        mMemTV.setText(getString(R.string.explosion_mem, sizeStr));
        mStatusTV.setText("正常");
        mFullStatusTV.setText("正常");

        GasCollectTask task = TaskService.getInstance().getTask(GasCollectTask.class);
        task.setTaskCallback(this);
        task.execute();
    }

    @Override
    public void onFinished(ATask task, Object result) {
        if (task instanceof GasCollectTask) {
            GasInfo info = (GasInfo) result;
            mGasGV.setAdapter(new GasAdapter(info));

            mFullGasGV.setAdapter(new GasAdapter(info));
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
                convertView = LayoutInflater.from(ExplosionActivity.this).inflate(R.layout.explosion_item_gas, parent, false);
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
