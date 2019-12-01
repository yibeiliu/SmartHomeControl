package com.smartlab.mqtt;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.smartlab.R;
import com.smartlab.Utils.WxShareUtils;
import com.smartlab.base.BaseActivity;
import com.smartlab.data.mqtt.ProtocolData;
import com.smartlab.data.mqtt.ProtocolDeviceStatus;
import com.smartlab.model.Constants;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseMqttActivity extends BaseActivity {
    private static final int HANDLER_MSG_WAIT = 1000;
    private static final int OVER_TIME_THRESHOLD = 2000;//todo

    private static final int HANDLER_MSG_TIME_WAIT = 1001;
    private static final int TIME_OVER_TIME_THRESHOLD = 3000;
    private MqttManager mqttManager;
    private MqttManager.OnMqttActionListener mqttActionListener;
    private int currentWaitMsgProtocolType = -1;
    private boolean isShowLinkingDialog = false;
    private boolean isDeviceOpen = false;

    @SuppressLint("UseSparseArrays")
    protected Map<Integer, Integer> currentDeviceStatusMap = new HashMap<>();


    @SuppressLint("HandlerLeak")
    private Handler overTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissDialog();
            if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.WHOLSE_STATE_INQUIRY.value()) {
                showRetryConnectDialog();
            } else {
                notifyMsgTimeOut(currentWaitMsgProtocolType);
                //消息超时
                showDialog(BaseMqttActivity.this, DialogType.FAIL, true, "消息发送超时", 1500);
            }
            currentWaitMsgProtocolType = -1;
        }
    };

    /*
    该 Handler 用于检测底层是否按时发时间戳，如果超过阈值，提示与设备断开连接
     */
    @SuppressLint("HandlerLeak")
    private Handler timeOverTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            overTimeHandler.removeCallbacksAndMessages(null);
            dismissDialog();
            showDeviceQuitDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureToolbar();
        isShowLinkingDialog = true;
        showDialog(this, DialogType.LOADING, true, "设备连接中，请稍等", -1);
        mqttActionListener = new MqttManager.OnMqttActionListener() {
            @Override
            public void onSubscribeSuccess() {
                if (!overTimeHandler.hasMessages(HANDLER_MSG_WAIT)) {
                    sendRequest(1, currentDeviceType(),
                            Constants.PROTOCOL_TYPE.WHOLSE_STATE_INQUIRY.value(), 1);
                    return;
                }
                notifySubscribeSuccess();
            }

            @Override
            public void onMessageReceived(String msg) {
                ProtocolData protocolData = ProtocalAnalyst.analyze(msg, currentDeviceType());
                if (protocolData == null) {
                    return;
                }

                for (ProtocolDeviceStatus item : protocolData.getProtocolDeviceStatuses()) {
                    if (item.getProtocolType() == Constants.PROTOCOL_TYPE.MALFUNCTION.value()) {
                        currentWaitMsgProtocolType = -1;
                        overTimeHandler.removeCallbacksAndMessages(null);
                        dismissDialog();
                        showMalfunctionDialog();
                        break;
                    }
                }

                if (isShowLinkingDialog && protocolData.getHeader().equals(Constants.HEADER_ALL)) {
                    isShowLinkingDialog = false;
                    currentWaitMsgProtocolType = -1;
                    overTimeHandler.removeCallbacksAndMessages(null);
                    dismissDialog();
                }

                //根据底层发的时间戳判断 device 是否掉线
                for (ProtocolDeviceStatus item : protocolData.getProtocolDeviceStatuses()) {
                    //开机且收到时间戳
                    if (isDeviceOpen && item.getProtocolType() == Constants.PROTOCOL_TYPE.TIME_STATE.value()) {
                        timeOverTimeHandler.removeCallbacksAndMessages(null);
                        Message timeMsg = Message.obtain();
                        timeMsg.what = HANDLER_MSG_TIME_WAIT;
                        timeOverTimeHandler.sendMessageDelayed(timeMsg, TIME_OVER_TIME_THRESHOLD);
                        break;
                    }
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

    private void showMalfunctionDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("故障")
                .setMessage("设备出现故障，请查看设备")
                .addAction(0, "退出", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
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
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .addAction("重试", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        mqttManager.unConnect();
                        mqttManager.connect(mqttActionListener);
                        sendRequest(1, currentDeviceType(),
                                Constants.PROTOCOL_TYPE.WHOLSE_STATE_INQUIRY.value(), 1);
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    private void showDeviceQuitDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("掉线")
                .setMessage("与设备连接断开")
                .addAction(0, "退出", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttManager.unConnect();
        overTimeHandler.removeCallbacksAndMessages(null);
        overTimeHandler = null;
        timeOverTimeHandler.removeCallbacksAndMessages(null);
        timeOverTimeHandler = null;
    }

    protected abstract void notifySubscribeSuccess();

    protected abstract void notifyMessageReceived(ProtocolData protocolData);

    protected abstract void notifyConnectFail(ErrorCode errorCode);

    protected abstract void notifyMsgTimeOut(int currentWaitMsgProtocolType);

    protected abstract int currentDeviceType();


    protected void sendRequest(int requestType, int deviceType, int protocolType, int data) {
        currentWaitMsgProtocolType = protocolType;
        if (protocolType != Constants.PROTOCOL_TYPE.BLUETOOTH.value()) {
            Message msg = Message.obtain();
            msg.what = HANDLER_MSG_WAIT;
            overTimeHandler.sendMessageDelayed(msg, OVER_TIME_THRESHOLD);
        } else {
            overTimeHandler.removeCallbacksAndMessages(null);
        }
        showDialog(this, DialogType.LOADING, true, "", -1);
        mqttManager.sendRequest(requestType, deviceType, protocolType, data);
    }

    private void configureToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = findViewById(R.id.toolbar_title_tv);
        if (currentDeviceType() == Constants.DEVICE_TYPE.AIR_CLEANER.value()) {
            title.setText(Constants.ChineseName.AIR_CLEANER);
        } else if (currentDeviceType() == Constants.DEVICE_TYPE.WATER_PURIFIER.value()) {
            title.setText(Constants.ChineseName.WATER_PURIFIER);
        } else {
            title.setText(Constants.ChineseName.MOISTURIZER);
        }
        ImageButton shareIconBtn = findViewById(R.id.toolbar_menu_ib);
        shareIconBtn.setImageResource(R.drawable.ic_share);
        shareIconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"分享到朋友圈", "分享给好友"};
                new QMUIDialog.MenuDialogBuilder(v.getContext())
                        .addItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                switch (which) {
                                    case 0:
                                        WxShareUtils.shareImage(BaseMqttActivity.this, WxShareUtils.getImageFromView(BaseMqttActivity.this), WxShareUtils.WxShareType.TO_FRIEND_CIRCLE);
                                        break;
                                    case 1:
                                        WxShareUtils.shareImage(BaseMqttActivity.this, WxShareUtils.getImageFromView(BaseMqttActivity.this), WxShareUtils.WxShareType.TO_PEOPLE);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
            }
        });
    }

    protected void setDeviceOpen(boolean isOpen) {
        isDeviceOpen = isOpen;
        if (!isDeviceOpen) {
            timeOverTimeHandler.removeCallbacksAndMessages(null);
        }
    }
}