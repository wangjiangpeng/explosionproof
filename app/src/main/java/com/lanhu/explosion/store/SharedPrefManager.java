package com.lanhu.explosion.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lanhu.explosion.AApplication;
import com.lanhu.explosion.bean.GasStandardInfo;

/**
 * 存储
 * <p>
 * Created by Administrator on 2017/9/13.
 */

public class SharedPrefManager {

    private static final String PREF_NAME = "shared_manager";
    private static final String GAS_STANDARD = "gas_standard";

    private SharedPreferences resultPref;

    private static SharedPrefManager sSharedPrefManager;
    public static SharedPrefManager getInstance(){
        if(sSharedPrefManager == null){
            sSharedPrefManager = new SharedPrefManager();
        }
        return sSharedPrefManager;
    }

    private SharedPrefManager() {
        resultPref = AApplication.getInstance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized void setGasStandard(GasStandardInfo info) {
        SharedPreferences.Editor editor = resultPref.edit();
        StringBuilder sb = new StringBuilder();
        sb.append(info.CO);
        sb.append(";");
        sb.append(info.H2S);
        sb.append(";");
        sb.append(info.O2);
        sb.append(";");
        sb.append(info.CH4);
        editor.putString(GAS_STANDARD, sb.toString());
        editor.commit();
    }

    public synchronized GasStandardInfo getGasStandard() {
        String str = resultPref.getString(GAS_STANDARD, "");
        if(TextUtils.isEmpty(str)){
            return null;
        }

        String[] sp = str.split(";");
        if(sp.length != 4){
            return null;
        }

        GasStandardInfo info = new GasStandardInfo();
        info.CO = Integer.valueOf(sp[0]);
        info.H2S = Integer.valueOf(sp[1]);
        info.O2 = Float.valueOf(sp[2]);
        info.CH4 = Integer.valueOf(sp[3]);
        return info;
    }


}
