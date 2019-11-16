package com.smartlab.mqtt;

import android.os.Bundle;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.smartlab.data.mqtt.ProtocolData;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseMqttActivity extends AppCompatActivity {
    private MqttManager mqttManager;
    private MqttManager.OnMqttActionListener mqttActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mqttActionListener = new MqttManager.OnMqttActionListener() {
            @Override
            public void onSubscribeSuccess() {
                notifySubscribeSuccess();
            }

            @Override
            public void onMessageReceived(String msg) {
                ProtocolData protocolData = ProtocalAnalyst.analyze(msg, currentDeviceType());
                notifyMessageReceived(protocolData);
            }

            @Override
            public void onFail(ErrorCode errorCode) {
                notifyConnectFail(errorCode);
                switch (errorCode) {
                    case CONNECT_FAIL:
                        showRetryConnectDialog();
                        break;
                    case TIME_OUT:
                        break;
                    case SEND_FAIL:
                        break;
                }
            }

            @Override
            public void onPublishSuccess() {

            }
        };
        mqttManager = MqttManager.getInstance(getApplicationContext());
        mqttManager.connect(mqttActionListener);
    }

    private void showRetryConnectDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("连接失败")
                .setMessage("是否重试？")
                .addAction(0, "退出", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .addAction("重试", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        mqttManager.connect(mqttActionListener);
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttManager.unConnect();
    }

    protected abstract void notifySubscribeSuccess();

    protected abstract void notifyMessageReceived(ProtocolData protocolData);

    protected abstract void notifyConnectFail(ErrorCode errorCode);

    protected abstract int currentDeviceType();


    protected void sendRequest(int requestType, int deviceType, int protocolType, int data) {
        mqttManager.sendRequest(requestType, deviceType, protocolType, data);
    }
}
