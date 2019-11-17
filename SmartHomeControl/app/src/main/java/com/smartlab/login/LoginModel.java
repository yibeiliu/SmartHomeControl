package com.smartlab.login;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.smartlab.Utils.SharePre;

/**
 * @Author peiyi.liu
 * @Date 10/30/2019 4:13 PM
 */
public class LoginModel {
    private LoginModel() {
    }

    private volatile static LoginModel LoginModel = null;

    public static LoginModel getInstance() {
        if (LoginModel == null) {
            synchronized (LoginModel.class) {
                LoginModel = new LoginModel();  //xxxxxx
            }
        }
        return LoginModel;
    }

    public void doLogin(final Context context, final String username, final String password, final onLoginStatusListener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkLoginInfo(context, username, password)) {
                    listener.onSuccess();
                } else {
                    listener.OnFailure("Your input a wrong user info!");
                }
            }
        }, 1000);
    }

    private boolean checkLoginInfo(Context context, String username, String password) {
        String pwd = SharePre.getLoginPassword(context);
        if (TextUtils.equals(username, "admin") && TextUtils.equals(password, pwd)) {
            return true;
        }
        return false;
    }

    public interface onLoginStatusListener {
        void onSuccess();

        void OnFailure(String msg);
    }
}