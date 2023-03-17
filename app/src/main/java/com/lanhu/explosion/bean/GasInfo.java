package com.lanhu.explosion.bean;

import java.util.ArrayList;

public class GasInfo {

    public static final int TYPE_OXYGEN = 0;
    public static final int TYPE_CO = 1;
    public static final int TYPE_BURN_GAS = 2;
    public static final int TYPE_HYDROGEN = 3;

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERR = 1;

    public static final int QUALIFIED_MIN = 0;

    public ArrayList<Item> list = new ArrayList<>();

    public static class Item {
        public int type;
        public int value;
        public int status;
        public long time;

        public Item(int type, int value, int status, long time) {
            this.type = type;
            this.value = value;
            this.status = status;
            this.time = time;
        }

    }

}
