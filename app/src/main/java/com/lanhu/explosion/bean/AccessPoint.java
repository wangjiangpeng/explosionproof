package com.lanhu.explosion.bean;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;

import com.lanhu.explosion.R;
import com.lanhu.explosion.utils.DataUtils;


public class AccessPoint {

    public static final int INVALID_NETWORK_ID = -1;

    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;
    public static final int SECURITY_OWE = 4;
    public static final int SECURITY_SAE = 5;
    public static final int SECURITY_DPP = 6;

    public ScanResult result;
    public WifiInfo wifiInfo;
    public WifiConfiguration configuration;
    public NetworkInfo networkInfo;
    private Context mContext;

    int networkId = INVALID_NETWORK_ID;


    public AccessPoint(Context context, ScanResult result, WifiInfo wifiInfo, NetworkInfo networkInfo){
        this.mContext = context;
        this.result = result;
        this.networkInfo = networkInfo;

        if(result.SSID != null && wifiInfo != null && result.SSID.equals(DataUtils.removeDoubleQuotes(wifiInfo.getSSID()))){
            this.wifiInfo = wifiInfo;
            networkId = wifiInfo.getNetworkId();
        }
    }

    public void loadConfiguration(WifiConfiguration configuration){
        if(configuration != null){
            if(result.SSID != null && result.SSID.equals(DataUtils.removeDoubleQuotes(configuration.SSID))){
                this.configuration = configuration;
//                networkId = configuration.networkId;
            }
        }
    }

    public boolean isSaved() {
        return networkId != INVALID_NETWORK_ID;
    }

    public boolean isConnect() {
        return wifiInfo != null;
    }

    public int getNetworkId() {
        return networkId;
    }

    public int getSecurity() {
        if (result.capabilities.contains("DPP")) {
            return SECURITY_DPP;
        } else if (result.capabilities.contains("SAE")) {
            return SECURITY_SAE;
        } else if (result.capabilities.contains("OWE")) {
            return SECURITY_OWE;
        } else if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    public String getSummary() {
        if(isConnect() && networkInfo != null){
            NetworkInfo.DetailedState state = networkInfo.getDetailedState();
            String[] formats = mContext.getResources().getStringArray(R.array.wifi_status);
            int index = state.ordinal();
            if (index < formats.length && formats[index].length() != 0) {
                return formats[index];
            }
        }
        return "";
    }

}
