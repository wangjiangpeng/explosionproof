package com.lanhu.explosion.bean;

public class GasInfo {

    public static final int STATUS_OK = 0;
    public static final int STATUS_WARN = 1;

//    public static final float STANDARD_O2 = 20.9f;
//    public static final int STANDARD_CO = 20.9f;

    public int CO;
    public int H2S;//硫化氢
    public int O2;
    public int CH4;//甲烷

    public int status_CO;
    public int status_H2S;//硫化氢
    public int status_O2;
    public int status_CH4;//甲烷

    public long time;

    public GasInfo(){
    }

    public static GasInfo sInfo = new GasInfo();

}
