package com.smartlab.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class BaseActivity extends AppCompatActivity {

    private QMUITipDialog dialog;
    private Handler dialogHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected final void showDialog(Context context, DialogType type, boolean needIcon, String msg, int dismissTimeMills) {
        dismissDialog();
        QMUITipDialog.Builder builder = new QMUITipDialog.Builder(context);
        switch (type) {
            case LOADING:
                if (needIcon) {
                    builder.setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING);
                }
                if (!TextUtils.isEmpty(msg)) {
                    builder.setTipWord(msg);
                }
                break;
            case SUCCESS:
                if (needIcon) {
                    builder.setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS);
                }
                if (!TextUtils.isEmpty(msg)) {
                    builder.setTipWord(msg);
                }
                break;
            case FAIL:
                if (needIcon) {
                    builder.setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL);
                }
                if (!TextUtils.isEmpty(msg)) {
                    builder.setTipWord(msg);
                }
                break;
            case INFO:
                if (needIcon) {
                    builder.setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO);
                }
                if (!TextUtils.isEmpty(msg)) {
                    builder.setTipWord(msg);
                }
                break;
        }
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        if (dismissTimeMills > 0) {
            dialogHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                }
            }, dismissTimeMills);
        }
    }

    protected final void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected enum DialogType {
        LOADING, SUCCESS, FAIL, INFO
    }
}
