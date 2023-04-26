package com.lanhu.explosion.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.lanhu.explosion.R;

public class WifiDialog extends AlertDialog {
    protected WifiDialog(Context context) {
        super(context);
    }

    protected WifiDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected WifiDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View mView = getLayoutInflater().inflate(R.layout.wifi_dialog, null);
        setView(mView);

        Log.e("WJP", "onCreate wifi");
    }
}
