package com.lanhu.explosion.bean;

import java.util.HashMap;

public class GasStandardItem {

    private int type;
    private int standard;
    private int channel;

    public GasStandardItem() {
    }

    public GasStandardItem(int type, int standard, int channel) {
        this.type = type;
        this.standard = standard;
        this.channel = channel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public static HashMap<Integer, GasStandardItem> sMap = new HashMap<>();
}
