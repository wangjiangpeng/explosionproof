package com.lanhu.explosion.utils;

import com.lanhu.explosion.store.SharedPrefManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DeviceUtils {

    private static String sMac;
    private static String sDeviceId;

    public static String getMac() {
        if(sMac == null){
            String mac = FileUtils.getFileFirstLine("/sys/class/net/wlan0/address");
            if(mac == null){
                mac = SharedPrefManager.getInstance().getMac();
            }
            sMac = mac;
        }
        return sMac;
    }

    public static String getDeviceID() {
        if(sDeviceId == null){
            BufferedReader bre = null;
            File cpuInfo = new File("/proc/cpuinfo");
            if (!cpuInfo.exists()) {
                return null;
            }
            try {
                bre = new BufferedReader(new FileReader(cpuInfo));
                String lineInfo;
                while ((lineInfo = bre.readLine()) != null) {
                    if (lineInfo.contains("Serial")) {
                        sDeviceId = lineInfo.substring(lineInfo.indexOf(":") + 2);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    bre.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return sDeviceId;
    }


}
