package com.smartlab.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.smartlab.R;
import com.smartlab.Utils.SharePre;
import com.smartlab.Utils.StaticValues;
import com.smartlab.base.BaseActivity;
import com.smartlab.home.MainActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

import static com.smartlab.Utils.StaticValues.USERNAME;

public class LoginActivity extends BaseActivity {

    private EditText usernameEt, passwordEt;
    private QMUIRoundButton confirmBtn;
    private TextView reFindPwdTv, loginUpTv;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RxPermissions rxPermissions = new RxPermissions(this);
        initView();
        initData();
        rxPermissions.request(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.CAMERA, Manifest.permission.VIBRATE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            confirmBtn.setEnabled(true);
                        } else {
                            showDialog(LoginActivity.this, DialogType.FAIL, true, "请打开定位和蓝牙权限！", 1500);
                            confirmBtn.setEnabled(false);
                        }
                    }
                });
    }

    private void initView() {
        usernameEt = findViewById(R.id.login_username_et);
        passwordEt = findViewById(R.id.login_password_et);
        confirmBtn = findViewById(R.id.login_confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        reFindPwdTv = findViewById(R.id.login_find_pwd_tv);
        loginUpTv = findViewById(R.id.login_login_up_tv);
        reFindPwdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RefindPwdActivity.class);
                startActivity(intent);
            }
        });
        loginUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(LoginActivity.this, DialogType.INFO, false, "在线注册尚未开放，请联系管理员", 2000);
            }
        });
    }

    private void initData() {
        //如果是第一次登陆
        String localUsername = SharePre.getLoginUsername(getApplicationContext());
        String localPassword = SharePre.getLoginPassword(getApplicationContext());
        if (!TextUtils.isEmpty(localUsername)) {
            usernameEt.setText(localUsername);
        } else {
            //证明第一次登陆
            SharePre.setLoginUsername(getApplicationContext(), USERNAME);
            SharePre.setLoginPassword(getApplicationContext(), StaticValues.PASSWORD);
        }
        if (!TextUtils.isEmpty(localPassword)) {
            passwordEt.setText(localPassword);
        }
    }

    private void doLogin() {
        showDialog(this, DialogType.LOADING, true, "正在加载", 0);
        LoginModel.getInstance().doLogin(this, usernameEt.getText().toString(), passwordEt.getText().toString(), new LoginModel.onLoginStatusListener() {
            @Override
            public void onSuccess() {
                dismissDialog();
                SharePre.setLoginUsername(getApplicationContext(), USERNAME);
                SharePre.setLoginPassword(getApplicationContext(), passwordEt.getText().toString());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);
                finish();
            }

            @Override
            public void OnFailure(String msg) {
                showDialog(LoginActivity.this, BaseActivity.DialogType.FAIL, true, msg, 1000);
            }
        });
    }
}
