package com.lanhu.explosion.bean;

public class GasStandardInfo extends BaseInfo{

    public int CO;
    public int H2S;//硫化氢
    public int O2;
    public int CH4;//甲烷

    public static GasStandardInfo sInfo = new GasStandardInfo();

    public static void checkGas(GasStandardInfo gsInfo, GasInfo info){
        if (info.CO > gsInfo.CO) {
            info.status_CO = GasInfo.STATUS_WARN;
        } else {
            info.status_CO = GasInfo.STATUS_OK;
        }

        if (info.H2S > gsInfo.H2S) {
            info.status_H2S = GasInfo.STATUS_WARN;
        } else {
            info.status_CO = GasInfo.STATUS_OK;
        }

        if (info.O2 < gsInfo.O2) {
            info.status_O2 = GasInfo.STATUS_WARN;
        } else {
            info.status_CO = GasInfo.STATUS_OK;
        }

        if (info.CH4 > gsInfo.CH4) {
            info.status_CH4 = GasInfo.STATUS_WARN;
        } else {
            info.status_CO = GasInfo.STATUS_OK;
        }
    }

}
