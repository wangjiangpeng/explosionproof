package com.lanhu.explosion.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.bean.GasStandardItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 存储
 * <p>
 * Created by Administrator on 2017/9/13.
 */

public class SharedPrefManager {

    private static final String PREF_NAME = "shared_manager";
    private static final String GAS_STANDARD = "gas_standard";
    private static final String MAC = "mac";
    private static final String SERVER_ADDRESS = "server_address";

    private SharedPreferences resultPref;

    private static SharedPrefManager sSharedPrefManager;

    public static SharedPrefManager getInstance() {
        if (sSharedPrefManager == null) {
            sSharedPrefManager = new SharedPrefManager();
        }
        return sSharedPrefManager;
    }

    private SharedPrefManager() {
        resultPref = AApplication.getInstance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized void saveGasStandard(HashMap<Integer, GasStandardItem> map) {
        Set<Integer> keys = map.keySet();
        StringBuilder sb = new StringBuilder();
        for(Integer key : keys){
            GasStandardItem item = map.get(key);
            sb.append(item.getType());
            sb.append(",");
            sb.append(item.getStandard());
            sb.append(",");
            sb.append(item.getChannel());
            sb.append(";");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        SharedPreferences.Editor editor = resultPref.edit();
        editor.putString(GAS_STANDARD, sb.toString());
        editor.commit();
    }

    public synchronized boolean getGasStandard(HashMap<Integer, GasStandardItem> map) {
        String str = resultPref.getString(GAS_STANDARD, "");
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        String[] sp = str.split(";");
        if (sp == null || sp.length == 0) {
            return false;
        }

        for (String s : sp) {
            String[] ss = s.split(",");
            if (ss == null || ss.length != 3) {
                continue;
            }
            map.put(Integer.valueOf(ss[0]), new GasStandardItem(Integer.valueOf(ss[0]), Integer.valueOf(ss[1]), Integer.valueOf(ss[2])));
        }
        return true;
    }

    public void saveMac(String mac){
        SharedPreferences.Editor editor = resultPref.edit();
        editor.putString(MAC, mac);
        editor.commit();
    }

    public String getMac(){
        return resultPref.getString(MAC, null);
    }

    public void saveServerAddress(String address){
        SharedPreferences.Editor editor = resultPref.edit();
        editor.putString(SERVER_ADDRESS, address);
        editor.commit();
    }

    public String getServerAddress(){
        return resultPref.getString(SERVER_ADDRESS, null);
    }

}
