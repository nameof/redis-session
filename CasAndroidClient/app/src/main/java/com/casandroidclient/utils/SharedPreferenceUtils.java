package com.casandroidclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kenderson on 2015/3/19.
 */
public class SharedPreferenceUtils {
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    public static void initSharedPreference(Context context) {
        settings = context.getSharedPreferences("StoreManager", context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void putBoolean(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putInt(String key, int value) {

        editor.putInt(key, value);
        editor.commit();
    }

    public static void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }


    /**
     * @param key the key to use for get the value
     * @param def if can not get the value then return the a default value
     * @return
     */
    public static String getString(String key, String def) {
        return settings.getString(key, def);
    }

    public static int getInt(String key, int def) {
        return settings.getInt(key, def);
    }

    public static float getFloat(String key, float def) {
        return settings.getFloat(key, def);
    }

    public static boolean getBoolean(String key, boolean def) {
        return settings.getBoolean(key, def);
    }

    public static long getLong(String key, long def) {
        return settings.getLong(key, def);
    }


}
