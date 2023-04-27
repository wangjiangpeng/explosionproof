package com.lanhu.explosion.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.lanhu.explosion.bean.AccessPoint;
import com.lanhu.explosion.utils.DataUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class WifiTracker {

    public interface WifiListener{
        void onStateChange();
        void onNetworkChange();
    }

    private Context mContext;
    private NetworkConnectChangedReceiver mReceiver;
    private WifiListener mWifiListener;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mLastNetworkInfo;
    private WifiInfo mLastInfo;
    private ArrayList<AccessPoint> mAccessPointList = new ArrayList<>();

    public WifiTracker(Context context){
        mContext = context;

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = context.getSystemService(ConnectivityManager.class);
    }

    public void start(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mReceiver = new NetworkConnectChangedReceiver();
        mContext.registerReceiver(mReceiver, filter);
    }

    public void stop(){
        mContext.unregisterReceiver(mReceiver);
    }

    public void setListener(WifiListener listener){
        mWifiListener = listener;
    }

    private void updateNetworkInfo(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            mLastNetworkInfo = networkInfo;
        } else {
            Network network = getCurrentNetwork(mWifiManager);
            mLastNetworkInfo = mConnectivityManager.getNetworkInfo(network);
        }
        mLastInfo = mWifiManager.getConnectionInfo();

        mAccessPointList.clear();
        List<ScanResult> scanList = mWifiManager.getScanResults();
        List<ScanResult> newList = filterScanResultsByCapabilities(scanList);
        transformationAccessPoint(mContext, newList, mAccessPointList, mLastInfo, mLastNetworkInfo);

        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        loadConfiguration(mAccessPointList, list);
    }

    public AccessPoint getConnectAccessPoint(){
        for(AccessPoint ap : mAccessPointList) {
            if (ap.isConnect()) {
                return ap;
            }
        }
        return null;
    }

    public ArrayList<AccessPoint> getOtherAccessPoints(){
        ArrayList<AccessPoint> list = new ArrayList<>();
        for(AccessPoint ap : mAccessPointList) {
            if (!ap.isConnect()) {
                list.add(ap);
            }
        }
        return list;
    }

    private static void loadConfiguration(ArrayList<AccessPoint> apList, List<WifiConfiguration> list) {
        for (AccessPoint ap : apList) {
            for(WifiConfiguration config : list){
                ap.loadConfiguration(config);
            }
        }
    }

    private static void transformationAccessPoint(Context context,List<ScanResult> scanResults, ArrayList<AccessPoint> mAccessPointList,
                                                  WifiInfo wifiInfo, NetworkInfo networkInfo) {
        for (ScanResult result : scanResults) {
            AccessPoint ap = new AccessPoint(context, result, wifiInfo, networkInfo);
            mAccessPointList.add(ap);
        }
    }

    private static List<ScanResult> filterScanResultsByCapabilities(List<ScanResult> scanResults) {
        List<ScanResult> list = new ArrayList<>();
        if (scanResults == null) {
            return list;
        }
        for (ScanResult result : scanResults) {
            if (TextUtils.isEmpty(result.SSID)) {
                continue;
            }

            boolean match = false;
            for (ScanResult r : list) {
                if (result.SSID.equals(r.SSID)) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                list.add(result);
            }
        }
        return list;
    }

    class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {

                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    updateNetworkInfo(null);
                }

                mWifiListener.onStateChange();
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                updateNetworkInfo(null);
                mWifiListener.onNetworkChange();
//            } else if (WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action){
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                updateNetworkInfo(info);
                mWifiListener.onNetworkChange();

            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                updateNetworkInfo(null);
                mWifiListener.onNetworkChange();
            }
        }
    }


    private static Method getIpAssignmentMode;
    public static boolean isStaticIp(WifiConfiguration config) {
        try {
            if (getIpAssignmentMode == null) {
                getIpAssignmentMode = WifiConfiguration.class.getMethod("getIpAssignment");
            }
            Object ipAssignmentStatic = getEnumValue("android.net.IpConfiguration$IpAssignment", "STATIC");
            Object ipAssignment = getIpAssignmentMode.invoke(config);
            if(ipAssignmentStatic == ipAssignment){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Object getEnumValue(String enumClassName, String enumValue) throws ClassNotFoundException
    {
        Class<Enum> enumClz = (Class<Enum>)Class.forName(enumClassName);
        return Enum.valueOf(enumClz, enumValue);
    }

    private static Method getCurrentNetworkMode;
    public static Network getCurrentNetwork(WifiManager wifiManager) {
        try {
            if (getCurrentNetworkMode == null) {
                getCurrentNetworkMode = WifiManager.class.getMethod("getCurrentNetwork");
            }
            return (Network) getCurrentNetworkMode.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method connectMethod;
    public static Network connect(WifiManager wifiManager, WifiConfiguration config) {
        try {
            if (connectMethod == null) {
                Class ActionListener = Class.forName("android.net.wifi.WifiManager$ActionListener");
                connectMethod = WifiManager.class.getMethod("connect", WifiConfiguration.class, ActionListener);
            }
            return (Network) connectMethod.invoke(wifiManager, config, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method saveMethod;
    public static Network save(WifiManager wifiManager, WifiConfiguration config) {
        try {
            if (saveMethod == null) {
                Class ActionListener = Class.forName("android.net.wifi.WifiManager$ActionListener");
                saveMethod = WifiManager.class.getMethod("save", WifiConfiguration.class, ActionListener);
            }
            return (Network) saveMethod.invoke(wifiManager, config, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method forgetMethod;
    public static Network forget(WifiManager wifiManager, int networkId) {
        try {
            if (forgetMethod == null) {
                Class ActionListener = Class.forName("android.net.wifi.WifiManager$ActionListener");
                forgetMethod = WifiManager.class.getMethod("forget", int.class, ActionListener);
            }
            return (Network) forgetMethod.invoke(wifiManager, networkId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
