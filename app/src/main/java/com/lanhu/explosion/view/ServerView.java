package com.lanhu.explosion.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lanhu.explosion.R;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.store.SharedPrefManager;

public class ServerView extends FrameLayout {

    TextView mValueTV;

    public ServerView(Context context) {
        super(context);
        init(context);
    }

    public ServerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ServerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ServerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.server_view, this, true);
        TextView nameTV = findViewById(R.id.device_info_item_name);
        mValueTV = findViewById(R.id.device_info_item_value);

        nameTV.setText(R.string.settings_server_addr);
        mValueTV.setText(SharedPrefManager.getInstance().getServerAddress());

        findViewById(R.id.device_info_name_layout).setOnClickListener(v -> {
            showDialog();
        });
    }

    public void showDialog() {
        EditText edit = new EditText(getContext());
        edit.setText(mValueTV.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.settings_server_addr);
        builder.setPositiveButton(R.string.ok, (d, w) -> {
            String str = edit.getText().toString();
            if (str == null || str.length() == 0) {
                MToast.makeText(R.string.settings_server_addr_not_empty, Toast.LENGTH_SHORT).show();
            } else {
                SharedPrefManager.getInstance().saveServerAddress(str);
                mValueTV.setText(str);
                d.cancel();
            }
        });

        builder.setNegativeButton(R.string.cancel, (d, w) -> {
            d.cancel();
        });
        builder.setView(edit);
        builder.show();
    }

}
