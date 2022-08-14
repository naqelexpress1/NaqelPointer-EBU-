package com.naqelexpress.naqelpointer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedHelper {


    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public static void putKeyString(Context context, String Key, String Value) {
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value).apply();

    }

    public static String getKeyString(Context contextGetKey, String Key) {
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(contextGetKey);
        String Value = sharedPreferences.getString(Key, "");
        return Value;

    }

    public static void putKeyInteger(Context context, String Key, int Value) {
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putInt(Key, Value).apply();
    }

    public static int getKeyInteger(Context contextGetKey, String Key) {
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(contextGetKey);
        int Value = sharedPreferences.getInt(Key, 0);
        return Value;
    }

    public static boolean getKeyBoolean(Context contextGetKey, String Key) {
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(contextGetKey);
        boolean Value = sharedPreferences.getBoolean(Key, false);
        return Value;

    }

    public static void putKeyBoolean(Context context, String Key, Boolean Value) {
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putBoolean(Key, Value).apply();
//        editor.commit();

    }


    public static void flashPreferences(Context context){
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.clear().apply();
    }


}
