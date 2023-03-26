package com.lanhu.explosion.task.impl;

import android.content.Context;

import com.lanhu.explosion.bean.GasStandardInfo;
import com.lanhu.explosion.store.SharedPrefManager;
import com.lanhu.explosion.task.ATask;

public class GasStandardTask  extends ATask {

    @Override
    protected Object doInBackground(Object... objs) {
        GasStandardInfo info = new GasStandardInfo();
        info.CO = 24;
        info.H2S = 10;
        info.O2 = 20.9f;
        info.CH4 = 5;

        SharedPrefManager.getInstance().setGasStandard(info);

        return null;
    }

}
