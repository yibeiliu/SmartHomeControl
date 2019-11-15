package com.smartlab.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.smartlab.data.UserAndDevice;

/**
 * @Author peiyi.liu
 * @Date 10/30/2019 4:05 PM
 */
public class SharePre {
    private final static String KEY_SP_LOGIN_USERNAME = "key_sp_login_username";
    private final static String KEY_SP_LOGIN_PASSWORD = "key_sp_login_password";
    private final static String KEY_SP_USER_DEVICES = "key_sp_user_devices";

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

    public static void setUserAndDevice(Context context, UserAndDevice userAndDevice) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(KEY_SP_USER_DEVICES, new Gson().toJson(userAndDevice));
        edit.apply();
    }

    public static UserAndDevice getUserAndDevices(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String a = sp.getString(KEY_SP_USER_DEVICES, null);
        Log.d("lpy","getUserAndDevices() = " + a);
        return new Gson().fromJson(a, UserAndDevice.class);
    }
}
