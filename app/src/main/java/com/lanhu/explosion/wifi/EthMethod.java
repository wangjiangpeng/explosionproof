package com.lanhu.explosion.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class EthMethod {

    private static Method isAvailableMethod;
    private static Method getIpAddressMethod;
    private static Method getNetmaskMethod;
    private static Method getGatewayMethod;
    private static Method getDnsMethod;
    private boolean defaultMode = true;

    public EthMethod() {
        try {
            Class eth = Class.forName("android.net.EthernetManager");
            Method[] methods = eth.getMethods();
            for (Method m : methods) {
                Log.e("WJP", m.toString());
            }
            if (isAvailableMethod == null) {
                isAvailableMethod = eth.getMethod("isAvailable");
            }
            if (getIpAddressMethod == null) {
                try {
                    getIpAddressMethod = eth.getMethod("getIpAddress");
                    getIpAddressMethod = eth.getMethod("getIpAddress");
                    getNetmaskMethod = eth.getMethod("getNetmask");
                    getGatewayMethod = eth.getMethod("getGateway");
                    getDnsMethod = eth.getMethod("getDns");

                    defaultMode = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    getIpAddressMethod = eth.getMethod("getIpAddress", String.class);
                    getIpAddressMethod = eth.getMethod("getIpAddress", String.class);
                    getNetmaskMethod = eth.getMethod("getNetmask", String.class);
                    getGatewayMethod = eth.getMethod("getGateway", String.class);
                    getDnsMethod = eth.getMethod("getDns", String.class);
                    defaultMode = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAvailable;
    public String ipAddress;
    public String netmask;
    public String gateway;
    public String dns;

    public Object getEthernetManager(Context context) {
        try {
            Class<?> ethernetManagerCls = Class.forName("android.net.EthernetManager");
            Object ethManager = context.getSystemService("ethernet");
            return ethManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean scanInfo(Context context) {
        Object ethManager = getEthernetManager(context);
        if (ethManager != null) {
            return getInfo(ethManager);
        }
        return false;
    }

    public boolean getInfo(Object ethManager) {
        try {
            String eth0 = "eth0";
            isAvailable = (boolean) isAvailableMethod.invoke(ethManager);
            if (defaultMode) {
                ipAddress = (String) getIpAddressMethod.invoke(ethManager);
                netmask = (String) getNetmaskMethod.invoke(ethManager);
                gateway = (String) getGatewayMethod.invoke(ethManager);
                dns = (String) getDnsMethod.invoke(ethManager);
            } else {
                ipAddress = (String) getIpAddressMethod.invoke(ethManager, eth0);
                netmask = (String) getNetmaskMethod.invoke(ethManager, eth0);
                gateway = (String) getGatewayMethod.invoke(ethManager, eth0);
                dns = (String) getDnsMethod.invoke(ethManager, eth0);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return "EthMethod{" +
                "defaultMode=" + defaultMode +
                ", isAvailable=" + isAvailable +
                ", ipAddress='" + ipAddress + '\'' +
                ", netmask='" + netmask + '\'' +
                ", gateway='" + gateway + '\'' +
                ", dns='" + dns + '\'' +
                '}';
    }


    public static boolean setDynamicIp(Context context) {
        try {
            //创建EthernetManager实例
            Class ethernetManagerClass = Class.forName("android.net.EthernetManager");
            Object ethernetManager = context.getSystemService("ethernet");
            //创建IpConfiguration
            Class ipConfigurationClass = Class.forName("android.net.IpConfiguration");
            Object ipConfiguration = ipConfigurationClass.newInstance();
            //获取所有枚举常量
            Map<String, Object> enumMap = getIpConfigurationEnumMap(ipConfigurationClass);
            //设置ipAssignment
            Field ipAssignment = ipConfigurationClass.getField("ipAssignment");
            ipAssignment.set(ipConfiguration, enumMap.get("IpAssignment.DHCP"));
            //设置proxySettings
            Field proxySettings = ipConfigurationClass.getField("proxySettings");
            proxySettings.set(ipConfiguration, enumMap.get("ProxySettings.NONE"));
            //设置Configuration
            Method setConfigurationMethod = ethernetManagerClass.getDeclaredMethod("setConfiguration", ipConfiguration.getClass());
            setConfigurationMethod.invoke(ethernetManager, ipConfiguration);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean setDynamicIp(WifiManager manager, WifiConfiguration config) {
        try {
            Log.e("WJP", "setDynamicIp");
            Class ipConfigurationClass = Class.forName("android.net.IpConfiguration");
//            Method[] fs = ipConfigurationClass.getMethods();
//            for(Method f :fs){
//                f.toString();
//                Log.e("WJP", f.toString());
//            }
            Object ipConfiguration = ipConfigurationClass.newInstance();
            //获取所有枚举常量
            Map<String, Object> enumMap = getIpConfigurationEnumMap(ipConfigurationClass);
            Class<Enum> IpAssignmentClass = (Class<Enum>) Class.forName("android.net.IpConfiguration$IpAssignment");
            Method setIpAssignmentMethod = ipConfigurationClass.getMethod("setIpAssignment", IpAssignmentClass);
            setIpAssignmentMethod.invoke(ipConfiguration, enumMap.get("IpAssignment.DHCP"));

            Class<Enum> ProxySettingsClass = (Class<Enum>) Class.forName("android.net.IpConfiguration$ProxySettings");
            Method setProxySettingsMethod = ipConfigurationClass.getMethod("setProxySettings", ProxySettingsClass);
            setProxySettingsMethod.invoke(ipConfiguration, enumMap.get("ProxySettings.NONE"));

            setIpConfiguration(config, ipConfiguration);
            manager.updateNetwork(config);
            manager.saveConfiguration();
            manager.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setIpConfiguration(WifiConfiguration config, Object ipConfiguration) {
        try {
            Class ipConfigurationClass = Class.forName("android.net.IpConfiguration");
            Method m = WifiConfiguration.class.getMethod("setIpConfiguration", ipConfigurationClass);
            m.invoke(config, ipConfiguration);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //获取IpConfiguration类中的所有枚举常量
    protected static Map<String, Object> getIpConfigurationEnumMap(Class ipConfigurationClass) {
        Map<String, Object> enumMap = new LinkedHashMap();
        Class[] classes = ipConfigurationClass.getDeclaredClasses();
        for (Class clazz : classes) {
            Object[] enumConstants = clazz.getEnumConstants();
            if (enumConstants == null)
                continue;
            for (Object constant : enumConstants)
                enumMap.put(clazz.getSimpleName() + "." + constant.toString(), constant);
        }
        return enumMap;
    }

    @SuppressWarnings("unchecked")
    public static void setStaticIpConfiguration(WifiManager manager, WifiConfiguration config, InetAddress ipAddress, int prefixLength, InetAddress gateway, InetAddress[] dns) throws Exception {
        // First set up IpAssignment to STATIC.
        Object ipAssignment = getEnumValue("android.net.IpConfiguration$IpAssignment", "STATIC");
        callMethod(config, "setIpAssignment", new String[]{"android.net.IpConfiguration$IpAssignment"}, new Object[]{ipAssignment});

        // Then set properties in StaticIpConfiguration.
        Object staticIpConfig = newInstance("android.net.StaticIpConfiguration");
        Object linkAddress = newInstance("android.net.LinkAddress", new Class<?>[]{InetAddress.class, int.class}, new Object[]{ipAddress, prefixLength});

        setField(staticIpConfig, "ipAddress", linkAddress);
        setField(staticIpConfig, "gateway", gateway);
        getField(staticIpConfig, "dnsServers", ArrayList.class).clear();
        for (int i = 0; i < dns.length; i++)
            getField(staticIpConfig, "dnsServers", ArrayList.class).add(dns[i]);

        callMethod(config, "setStaticIpConfiguration", new String[]{"android.net.StaticIpConfiguration"}, new Object[]{staticIpConfig});
        manager.updateNetwork(config);
        manager.saveConfiguration();
        manager.disconnect();
    }

    private static Object newInstance(String className) throws Exception {
        return newInstance(className, new Class<?>[0], new Object[0]);
    }

    private static Object newInstance(String className, Class<?>[] parameterClasses, Object[] parameterValues) throws Exception {
        Class<?> clz = Class.forName(className);
        Constructor<?> constructor = clz.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object getEnumValue(String enumClassName, String enumValue) throws ClassNotFoundException {
        Class<Enum> enumClz = (Class<Enum>) Class.forName(enumClassName);
        return Enum.valueOf(enumClz, enumValue);
    }

    private static void setField(Object object, String fieldName, Object value) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }

    private static <T> T getField(Object object, String fieldName, Class<T> type) throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        return type.cast(field.get(object));
    }

    private static void callMethod(Object object, String methodName, String[] parameterTypes, Object[] parameterValues) throws Exception {
        Class<?>[] parameterClasses = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++)
            parameterClasses[i] = Class.forName(parameterTypes[i]);

        Method method = object.getClass().getDeclaredMethod(methodName, parameterClasses);
        method.invoke(object, parameterValues);
    }

//
//    调用:
//            try{
//        setStaticIpConfiguration(mWifiManager, wifiConfig,
//                InetAddress.getByName("192.168.1.44"), 24,
//                InetAddress.getByName("192.168.1.45"),
//                new InetAddress[] { InetAddress.getByName("192.168.1.46"), InetAddress.getByName("192.168.1.47") });
//        Log.e(TAG,"setStaticIpConfiguration成功 ");
//    }catch (Exception e){
//        e.printStackTrace();
//        Log.e(TAG,"setStaticIpConfiguration失败: "+e.toString());
//    }
}
