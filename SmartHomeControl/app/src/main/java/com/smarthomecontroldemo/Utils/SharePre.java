package com.smarthomecontroldemo.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @Author peiyi.liu
 * @Date 10/30/2019 4:05 PM
 */
public class SharePre {
    private final static String KEY_SP_LOGIN_USERNAME = "key_sp_login_username";
    private final static String KEY_SP_LOGIN_PASSWORD = "key_sp_login_password";

    public static void setLoginUsername(Context context, String username) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(KEY_SP_LOGIN_USERNAME, username);
        edit.apply();
    }

    public static void setLoginPassword(Context context, String password) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(KEY_SP_LOGIN_PASSWORD, password);
        edit.apply();
    }

    public static String getLoginUsername(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(KEY_SP_LOGIN_USERNAME, null);
    }

    public static String getLoginPassword(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(KEY_SP_LOGIN_PASSWORD, null);
    }
}
