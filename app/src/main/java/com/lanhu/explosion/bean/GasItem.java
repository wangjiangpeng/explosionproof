package com.lanhu.explosion.bean;

import com.lanhu.explosion.R;

import java.util.ArrayList;

public class GasItem extends BaseInfo {

    public static final int STATUS_OK = 0;
    public static final int STATUS_WARN = 1;


    public static final int TYPE_CO = 0;
    public static final int TYPE_H2S = 1;
    public static final int TYPE_O2 = 2;
    public static final int TYPE_CH4 = 3;

    private int type;
    private int status;
    private int value;
    private String valueUnit;
    private int standard;
    private String standardUnit;
    private int nameId;
    private int iconId;
    private int channel;
    private long time;

    public GasItem() {

    }

    public GasItem(int type) {
        this.type = type;
        computerValue();
    }

    public GasItem(int type, GasStandardItem item) {
        this.type = type;
        adjustStandard(item);
        computerValue();
    }

    public void adjustStandard(GasStandardItem item) {
        if (item.getType() != type) {
            return;
        }
        setStandard(item.getStandard());
        channel = item.getChannel();
    }

    public void computerValue(){
        switch (type) {
            case TYPE_CO:
                nameId = R.string.explosion_co;
                iconId = R.mipmap.co;

                valueUnit = value + "PPM";
                valueUnit = value + "PPM";
                standardUnit = "<" +standard + "PPM";
                standardUnit = "<" +standard + "PPM";
                break;

            case TYPE_H2S:
                nameId = R.string.explosion_hydrogen;
                iconId = R.mipmap.hydrogen;

                valueUnit = value + "PPM";
                valueUnit = value + "PPM";
                standardUnit = "<" +standard + "PPM";
                standardUnit = "<" +standard + "PPM";
                break;

            case TYPE_O2:
                nameId = R.string.explosion_oxygen;
                iconId = R.mipmap.oxygen;

                valueUnit = (value * 0.1f) + "%VOL";
                standardUnit = ">" +(standard * 0.1f) + "%VOL";
                break;

            case TYPE_CH4:
                nameId = R.string.explosion_burn_gas;
                iconId = R.mipmap.burn_gas;

                valueUnit = value + "%LEL";
                standardUnit = "<" +standard + "%LEL";
                break;
        }

        status = value > standard ? STATUS_WARN : STATUS_OK;
        if(type == TYPE_O2){
            status = value > standard ? STATUS_OK : STATUS_WARN;
        } else {
            status = value > standard ? STATUS_WARN : STATUS_OK;
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        computerValue();
    }

    public String getValueUnit() {
        return valueUnit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        computerValue();
    }

    public int getStatus() {
        return status;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
        computerValue();
    }

    public String getStandardUnit() {
        return standardUnit;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isDisable() {
        return time == 0;
    }

    public static ArrayList<GasItem> mList = new ArrayList<>();

    public static boolean isAllStatusOK(){
        for(GasItem item : mList){
            if(!item.isDisable() && item.getStatus() != STATUS_OK){
                return false;
            }
        }
        return true;
    }
}
