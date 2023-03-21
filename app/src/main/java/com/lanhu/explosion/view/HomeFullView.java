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