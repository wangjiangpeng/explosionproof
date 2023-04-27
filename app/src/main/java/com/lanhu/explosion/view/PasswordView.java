package com.lanhu.explosion.view;

import android.app.AlertDialog;
import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lanhu.explosion.R;
import com.lanhu.explosion.misc.MToast;
import com.lanhu.explosion.utils.LockPatternUtils;

import ZtlApi.ZtlManager;

public class PasswordView extends FrameLayout {

    private EditText mCurrentET, mNewET, mAgainET;

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
        mCurrentET = findViewById(R.id.password_view_current_edit);
        mNewET = findViewById(R.id.password_view_new_edit);
        mAgainET = findViewById(R.id.password_view_again_edit);

        LockPatternUtils.mo();

        findViewById(R.id.password_view_again_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ZtlManager.GetInstance().setContext(getContext());
                ZtlManager.GetInstance().lockScreenSettings(true, "123456");
            }
        });
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
