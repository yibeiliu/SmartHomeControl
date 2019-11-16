package com.smartlab.mqtt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.smartlab.base.BaseActivity;
import com.smartlab.data.mqtt.ProtocolData;
import com.smartlab.data.mqtt.ProtocolDeviceStatus;
import com.smartlab.model.Constants;

public abstract class BaseMqttActivity extends BaseActivity {
    private static final int HANDLER_MSG_WAIT = 1000;
    private static final int OVER_TIME_THRESHOLD = 200000;//todo

    private MqttManager mqttManager;
    private MqttManager.OnMqttActionListener mqttActionListener;
    private int currentWaitMsgProtocolType = -1;
    private boolean isShowLinkingDialog = false;

    @SuppressLint("HandlerLeak")
    private Handler overTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //消息超时
            currentWaitMsgProtocolType = -1;
            dismissDialog();
            showDialog(BaseMqttActivity.this, DialogType.FAIL, true, "消息发送超时", 1500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShowLinkingDialog = true;
        showDialog(this, DialogType.LOADING, true, "设备连接中，请稍等", -1);
        mqttActionListener = new MqttManager.OnMqttActionListener() {
            @Override
            public void onSubscribeSuccess() {
                sendRequest(1, currentDeviceType(),
                        Constants.PROTOCOL_TYPE.WHOLSE_STATE_INQUIRY.value(), 1);
                notifySubscribeSuccess();
            }

            @Override
            public void onMessageReceived(String msg) {
                ProtocolData protocolData = ProtocalAnalyst.analyze(msg, currentDeviceType());
                if (protocolData == null) {
                    return;
                }
                if (isShowLinkingDialog && protocolData.getHeader().equals(Constants.HEADER_ALL)) {
                    isShowLinkingDialog = false;
                    dismissDialog();
                }

                for (ProtocolDeviceStatus item : protocolData.getProtocolDeviceStatuses()) {
                    if (item.getProtocolType() == currentWaitMsgProtocolType) {
                        currentWaitMsgProtocolType = -1;
                        overTimeHandler.removeCallbacksAndMessages(null);
                        dismissDialog();
                        break;
                    }
                }

                notifyMessageReceived(protocolData);
            }

            @Override
            public void onFail(ErrorCode errorCode) {
                notifyConnectFail(errorCode);
                switch (errorCode) {
                    case CONNECT_FAIL:
                        dismissDialog();
                        showRetryConnectDialog();
                        break;
                    case TIME_OUT:
                        break;
                    case SEND_FAIL:
                        currentWaitMsgProtocolType = -1;
                        dismissDialog();
                        showDialog(BaseMqttActivity.this, DialogType.FAIL, true, "消息发送失败", 1500);
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
        overTimeHandler.removeCallbacksAndMessages(null);
        overTimeHandler = null;
    }

    protected abstract void notifySubscribeSuccess();

    protected abstract void notifyMessageReceived(ProtocolData protocolData);

    protected abstract void notifyConnectFail(ErrorCode errorCode);

    protected abstract int currentDeviceType();

    protected void sendRequest(int requestType, int deviceType, int protocolType, int data) {
        currentWaitMsgProtocolType = protocolType;
        Message msg = Message.obtain();
        msg.what = HANDLER_MSG_WAIT;
        overTimeHandler.sendMessageDelayed(msg, OVER_TIME_THRESHOLD);
        showDialog(this, DialogType.LOADING, true, "", -1);
        mqttManager.sendRequest(requestType, deviceType, protocolType, data);
    }
}