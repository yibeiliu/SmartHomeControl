package com.smartlab.mqtt;

import android.os.Bundle;

import com.smartlab.R;
import com.smartlab.data.mqtt.ProtocolData;
import com.smartlab.model.Constants;

public class TestMqttActivity extends BaseMqttActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mqtt);

    }

    @Override
    protected void notifySubscribeSuccess() {
//        Toast.makeText(this, "notifySubscribeSuccess", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void notifyMessageReceived(ProtocolData protocolData) {
//        Toast.makeText(this, "notifyMessageReceived", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyConnectFail(ErrorCode errorCode) {
//        Toast.makeText(this, "notifyConnectFail", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyMsgTimeOut(int currentWaitMsgProtocolType) {

    }

    @Override
    protected int currentDeviceType() {
        return Constants.DEVICE_WATER_PURIFIER;
    }

}