package com.lanhu.explosion.utils;

import java.util.HashMap;

public class ButtonUtils {

    private static final long MAX_TIME = 5000;

    private static HashMap<Integer, Long> sMaps = new HashMap<>();

    public static boolean isFastDoubleClick(int buttonId) {
        Long time = sMaps.get(buttonId);
        long now = System.currentTimeMillis();
        if(time == null){
            sMaps.put(buttonId, now);
            return false;
        }

        long diff = now - time;
        if (diff < MAX_TIME) {
            return true;
        }
        sMaps.put(buttonId, now);
        return false;
    }
}
