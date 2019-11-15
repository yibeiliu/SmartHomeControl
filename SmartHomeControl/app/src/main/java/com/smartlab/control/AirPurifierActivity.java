package com.smartlab.control;

import android.os.Bundle;

import com.smartlab.R;
import com.smartlab.base.BaseActivity;
import com.smartlab.mqtt.MqttSimple;

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
