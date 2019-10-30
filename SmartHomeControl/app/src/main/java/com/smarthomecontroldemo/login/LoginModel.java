package com.smarthomecontroldemo.login;

import android.text.TextUtils;

import org.w3c.dom.Text;

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

    public void doLogin(final String username,final String password,final onLoginStatusListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (checkLoginInfo(username, password)) {
                        listener.onSuccess();
                    } else {
                        listener.OnFailure("Your input a wrong user info!");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean checkLoginInfo(String username, String password) {
        if (TextUtils.equals(username, "admin") && TextUtils.equals(username, "admin")) {
            return true;
        }
        return false;
    }

    public interface onLoginStatusListener {
        void onSuccess();

        void OnFailure(String msg);
    }
}