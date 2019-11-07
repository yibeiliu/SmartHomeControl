package com.smarthomecontroldemo.login;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smarthomecontroldemo.R;
import com.smarthomecontroldemo.Utils.SharePre;
import com.smarthomecontroldemo.base.BaseActivity;

public class RefindPwdActivity extends BaseActivity {

    private TextView newConfirmTv;
    private EditText oldPwdEt, newPwdEt, newConfirmEt;
    private Button confirmBtn;
    private String oldPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refind_pwd);
        initView();
        oldPwd = SharePre.getLoginPassword(RefindPwdActivity.this);
    }

    private void initView() {
        newConfirmTv = findViewById(R.id.login_hint_new_pwd_confirm_tv);
        oldPwdEt = findViewById(R.id.login_old_pwd_et);
        newPwdEt = findViewById(R.id.login_new_pwd_et);
        newConfirmEt = findViewById(R.id.login_new_pwd_confirm_et);
        confirmBtn = findViewById(R.id.login_confirm_btn);
        newConfirmEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && s.toString().equals(newPwdEt.getText().toString())) {
                    newConfirmTv.setTextColor(Color.BLACK);
                    confirmBtn.setEnabled(true);
                } else {
                    newConfirmTv.setTextColor(Color.RED);
                    confirmBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPwd(newConfirmEt.getText().toString())) {
                    Log.d("lpy","666");
                    SharePre.setLoginPassword(RefindPwdActivity.this, confirmBtn.getText().toString());
                    Log.d("lpy","777");
                    Toast.makeText(RefindPwdActivity.this, "修改完成，请重新登录", Toast.LENGTH_SHORT).show();
                    Log.d("lpy","88");
                    finish();
                }
            }
        });
    }

    private boolean checkPwd(String newPwd) {
        Log.d("lpy","111");
        if (!oldPwd.equals(oldPwdEt.getText().toString())) {
            Log.d("lpy","222");
            showDialog(RefindPwdActivity.this, DialogType.FAIL, true, "旧密码输入错误", 2000);
            return false;
        }
        if (oldPwd.equals(newPwd)) {
            Log.d("lpy","333");
            showDialog(RefindPwdActivity.this, DialogType.FAIL, true, "新旧密码不能相同", 2000);
            Log.d("lpy","444");
            return false;
        }
        Log.d("lpy","555");
        return true;
    }
}
