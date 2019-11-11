package com.smarthomecontroldemo.mqtt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.smarthomecontroldemo.R;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class TestMqttActivity extends AppCompatActivity {

    private MqttAndroidClient mqttAndroidClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mqtt);

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), Config.serverUri, Config.clientId);
        MqttSimple mqttSimple = new MqttSimple(mqttAndroidClient);
        mqttSimple.test();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttAndroidClient.unregisterResources();
    }
}