package com.smartlab.mqtt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.smartlab.R;

public class TestMqttActivity extends AppCompatActivity {

    private MqttSimple mqttSimple;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mqtt);

        mqttSimple = MqttSimple.getInstance(getApplicationContext());
        mqttSimple.connect(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttSimple.unregisterResources();
    }
}