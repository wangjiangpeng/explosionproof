package com.lanhu.explosion.utils;

import android.util.Log;

import java.lang.reflect.Method;

public class LockPatternUtils {

    public static void mo(){
        try{
            Class clazz = Class.forName("com.android.internal.widget.LockPatternChecker");

            Method[] ms = clazz.getMethods();
            for(Method m : ms){
                Log.e("WJP", m.toString());
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


//    public byte[] verifyPassword(String password, long challenge, int userId){
//        return verifyCredential(password, CREDENTIAL_TYPE_PASSWORD, challenge, userId);
//    }
//
//    public void saveLockPassword(String password, String savedPassword, int requestedQuality,
//                                 int userHandle) {
//
//    }

}
