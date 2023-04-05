package com.lanhu.explosion.task.impl;

import android.content.Context;

import com.lanhu.explosion.bean.GasInfo;
import com.lanhu.explosion.bean.GasStandardInfo;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.store.SharedPrefManager;
import com.lanhu.explosion.task.ATask;

import java.util.ArrayList;

public class GasStandardTask extends ATask {

    @Override
    protected Object doInBackground(Object... objs) {
        // if request success
        {
            GasStandardInfo.sInfo.CO = 24;
            GasStandardInfo.sInfo.H2S = 10;
            GasStandardInfo.sInfo.O2 = 209;
            GasStandardInfo.sInfo.CH4 = 5;

            SharedPrefManager.getInstance().setGasStandard(GasStandardInfo.sInfo);
        }

        // if request fail
        /*
        {
            GasStandardInfo info = SharedPrefManager.getInstance().getGasStandard();
            if(info == null){
                GasStandardInfo.sInfo.CO = 24;
                GasStandardInfo.sInfo.H2S = 10;
                GasStandardInfo.sInfo.O2 = 209;
                GasStandardInfo.sInfo.CH4 = 5;
            } else {
                GasStandardInfo.sInfo = info;
            }
        }
        */

        // first boot, from db read last time info
        if(GasInfo.sInfo.time == 0){
            ArrayList<GasInfo> list = DBManager.getInstance().queryGas();
            for(GasInfo info : list){
                if(info.time > GasInfo.sInfo.time){
                    GasInfo.sInfo = info;
                }
            }
        }

        GasStandardInfo.checkGas(GasStandardInfo.sInfo, GasInfo.sInfo);
        return GasStandardInfo.sInfo;
    }

}
