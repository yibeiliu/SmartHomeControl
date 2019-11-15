package com.smartlab.control;

import android.os.Bundle;

import com.smartlab.R;
import com.smartlab.base.BaseActivity;

public class AirPurifierActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_purifier);


    }

//    private void mqttConnect() {
//        MqttManager.getInstance(this).connect(new MqttManager.OnMqttConnectionCallback() {
//            @Override
//            public void onConnectionLost() {
//
//            }
//
//            @Override
//            public void onMessageReceived(String topic, MqttMessage message) {
//
//            }
//
//            @Override
//            public void onDeliverComplete(IMqttDeliveryToken token) {
//
//            }
//        });
//    }
}
