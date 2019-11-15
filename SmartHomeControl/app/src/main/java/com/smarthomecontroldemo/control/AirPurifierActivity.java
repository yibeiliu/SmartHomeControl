package com.smarthomecontroldemo.control;

import android.os.Bundle;

import com.smarthomecontroldemo.R;
import com.smarthomecontroldemo.base.BaseActivity;
import com.smarthomecontroldemo.mqtt.MqttSimple;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class AirPurifierActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_purifier);


    }

    private void mqttConnect() {
        MqttSimple.getInstance(this).connect(new MqttSimple.OnMqttConnectionCallback() {
            @Override
            public void onConnectionLost() {

            }

            @Override
            public void onMessageReceived(String topic, MqttMessage message) {

            }

            @Override
            public void onDeliverComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
