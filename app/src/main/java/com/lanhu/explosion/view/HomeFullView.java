package com.lanhu.explosion.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lanhu.explosion.CommActivity;
import com.lanhu.explosion.ExplosionActivity;
import com.lanhu.explosion.GasCollectActivity;
import com.lanhu.explosion.R;
import com.lanhu.explosion.SettingsActivity;
import com.lanhu.explosion.bean.GasInfo;
import com.lanhu.explosion.utils.DataUtils;
import com.lanhu.explosion.utils.MemUtils;

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
