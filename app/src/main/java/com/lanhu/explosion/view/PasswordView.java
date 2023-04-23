package com.lanhu.explosion.view;

import android.app.AlertDialog;
import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lanhu.explosion.R;
import com.lanhu.explosion.misc.MToast;

public class PasswordView extends FrameLayout {

    public PasswordView(Context context) {
        super(context);
        init(context);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.password_view, this, true);
    }

    public void showDialog(String title, TextView tv) {
        EditText edit = new EditText(getContext());
        edit.setText(tv.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setPositiveButton(R.string.ok, (d, w) -> {
            String str = edit.getText().toString();
            if (str == null || str.length() == 0) {
                MToast.makeText(R.string.settings_device_info_name_not_empty, Toast.LENGTH_SHORT).show();
            } else {
                Settings.Global.putString(getContext().getContentResolver(), Settings.Global.DEVICE_NAME, str);
                tv.setText(str);
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
