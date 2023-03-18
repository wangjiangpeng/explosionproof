package com.lanhu.explosion.task.impl;

import com.lanhu.explosion.bean.GasInfo;
import com.lanhu.explosion.task.ATask;

public class GasCollectTask extends ATask {

    @Override
    protected Object doInBackground(Object... objs) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long time = System.currentTimeMillis();
        GasInfo info = new GasInfo();
        GasInfo.Item item = new GasInfo.Item(GasInfo.TYPE_OXYGEN, 100, GasInfo.STATUS_OK, time);
        info.list.add(item);
        item = new GasInfo.Item(GasInfo.TYPE_CO, 100, GasInfo.STATUS_OK, time);
        info.list.add(item);
        item = new GasInfo.Item(GasInfo.TYPE_BURN_GAS, 100, GasInfo.STATUS_OK, time);
        info.list.add(item);
        item = new GasInfo.Item(GasInfo.TYPE_HYDROGEN, 100, GasInfo.STATUS_OK, time);
        info.list.add(item);
        return info;
    }

}
