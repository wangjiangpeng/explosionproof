package com.lanhu.explosion.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lanhu.explosion.R;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.utils.DeviceUtils;
import com.lanhu.explosion.utils.FileUtils;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

public class DeviceInfoView extends FrameLayout {

    private TextView mDeviceNameTV;

    public DeviceInfoView(Context context) {
        super(context);
        init(context);
    }

    public DeviceInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DeviceInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DeviceInfoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.device_info_view, this, true);

        TextView deviceTitleTV = findViewById(R.id.device_info_item_name);
        mDeviceNameTV = findViewById(R.id.device_info_item_value);
        ListView listView = findViewById(R.id.gas_view_list);

        deviceTitleTV.setText(R.string.settings_device_info_name);
        mDeviceNameTV.setText(Settings.Global.getString(getContext().getContentResolver(), Settings.Global.DEVICE_NAME));

        listView.setAdapter(new ViewAdapter());

        findViewById(R.id.device_info_name_layout).setOnClickListener(v -> {
            showDialog();
        });
    }

    public void showDialog() {
        EditText edit = new EditText(getContext());
        edit.setText(mDeviceNameTV.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.settings_device_info_name);
        builder.setPositiveButton(R.string.ok, (d, w) -> {
            String str = edit.getText().toString();
            if (str == null || str.length() == 0) {
                MToast.makeText(R.string.settings_device_info_name_not_empty, Toast.LENGTH_SHORT).show();
            } else {
                Settings.Global.putString(getContext().getContentResolver(), Settings.Global.DEVICE_NAME, str);
                mDeviceNameTV.setText(str);
                d.cancel();
            }
        });

        builder.setNegativeButton(R.string.cancel, (d, w) -> {
            d.cancel();
        });
        builder.setView(edit);
        builder.show();
    }

    private class ViewAdapter extends BaseAdapter {

        private int[] nameIds = new int[]{
                R.string.settings_device_info_mac,
                R.string.settings_device_info_serial,
                R.string.settings_device_info_vendor,
                R.string.settings_device_info_model,
                R.string.settings_device_info_firmware,
                R.string.settings_device_info_software};
        private String[] values = new String[nameIds.length];

        ViewAdapter() {
            values[0] = DeviceUtils.getMac();
            values[1] = DeviceUtils.getDeviceID();
            values[2] = Build.MANUFACTURER;
            values[3] = Build.MODEL;
            values[4] = Build.DISPLAY;
            values[5] = String.valueOf(Build.VERSION.SDK_INT);
        }

        @Override
        public int getCount() {
            return nameIds.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_info_item, null);
            TextView name = convertView.findViewById(R.id.device_info_item_name);
            TextView value = convertView.findViewById(R.id.device_info_item_value);
            name.setText(nameIds[position]);
            value.setText(values[position]);
            return convertView;
        }
    }

}
