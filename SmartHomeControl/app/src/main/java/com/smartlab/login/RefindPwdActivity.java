package com.smartlab.login;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlab.R;
import com.smartlab.Utils.SharePre;
import com.smartlab.base.BaseActivity;

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
                    SharePre.setLoginPassword(RefindPwdActivity.this, newConfirmEt.getText().toString());
                    Toast.makeText(RefindPwdActivity.this, "修改完成，请重新登录", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private boolean checkPwd(String newPwd) {
        if (!oldPwd.equals(oldPwdEt.getText().toString())) {
            showDialog(RefindPwdActivity.this, DialogType.FAIL, true, "旧密码输入错误", 2000);
            return false;
        }
        if (oldPwd.equals(newPwd)) {
            showDialog(RefindPwdActivity.this, DialogType.FAIL, true, "新旧密码不能相同", 2000);
            return false;
        }
        return true;
    }
}
