package com.lanhu.explosion.misc;

import android.content.Context;
import android.widget.Toast;

import com.lanhu.explosion.AApplication;

public class MToast {

    private static Toast toast = Toast.makeText(AApplication.getInstance(), "", Toast.LENGTH_SHORT);

    public static Toast makeText(CharSequence text, int duration) {
        toast.cancel();
        toast = Toast.makeText(AApplication.getInstance(), text, duration);
        return toast;
    }

    public static Toast makeText(int resId, int duration) {
        toast.cancel();
        toast = Toast.makeText(AApplication.getInstance(), resId, duration);
        return toast;
    }

}
