package com.lanhu.explosion.task.impl;

import com.lanhu.explosion.bean.GasItem;
import com.lanhu.explosion.bean.GasStandardItem;
import com.lanhu.explosion.store.DBManager;
import com.lanhu.explosion.store.SharedPrefManager;
import com.lanhu.explosion.task.ATask;
import com.lanhu.explosion.utils.FileUtils;

public class InitDataTask extends ATask {

    private static final String DEFAULT_ADDRESS = "https://test";

    @Override
    protected Object doInBackground(Object... objs) {
        // todo request service
        boolean request = false;
        if(request){


        } else {
            GasStandardItem.sMap.clear();
            GasStandardItem.sMap.put(GasItem.TYPE_CO, new GasStandardItem(GasItem.TYPE_CO, 24, 1));
            GasStandardItem.sMap.put(GasItem.TYPE_H2S, new GasStandardItem(GasItem.TYPE_H2S, 10, 2));
            GasStandardItem.sMap.put(GasItem.TYPE_O2, new GasStandardItem(GasItem.TYPE_O2, 209, 3));
            GasStandardItem.sMap.put(GasItem.TYPE_CH4, new GasStandardItem(GasItem.TYPE_CH4, 5, 4));
            SharedPrefManager.getInstance().saveGasStandard(GasStandardItem.sMap);
        }

        // first boot, from database read last time info
        if(GasItem.mList.size() == 0){
            DBManager db = DBManager.getInstance();
            int[] types = new int[]{GasItem.TYPE_O2, GasItem.TYPE_CO, GasItem.TYPE_CH4, GasItem.TYPE_H2S};
            for(int type : types){
                GasItem item = db.queryGasLastTime(type);
                if(item != null){
                    GasItem.mList.add(item);
                }
            }
        }

        // database empty, set default
        if(GasItem.mList.size() == 0){
            GasItem.mList.add(new GasItem(GasItem.TYPE_CO));
            GasItem.mList.add(new GasItem(GasItem.TYPE_O2));
            GasItem.mList.add(new GasItem(GasItem.TYPE_CH4));
            GasItem.mList.add(new GasItem(GasItem.TYPE_H2S));
        }

        /*-----------------------------MAC------------------------------------*/
        String mac = FileUtils.getFileFirstLine("/sys/class/net/wlan0/address");
        if(mac != null){
            SharedPrefManager.getInstance().saveMac(mac);
        }

        /*-----------------------------address------------------------------------*/
        SharedPrefManager spf = SharedPrefManager.getInstance();
        String address = spf.getServerAddress();
        if(address == null){
            spf.saveServerAddress(DEFAULT_ADDRESS);
        }
        return null;
    }

}
