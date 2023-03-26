package com.lanhu.explosion;

import android.app.Application;

public class AApplication extends Application {

    private static AApplication sApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        sApplication = this;
    }

    public static AApplication getInstance(){
        return sApplication;
    }


}
