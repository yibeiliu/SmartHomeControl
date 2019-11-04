package com.smarthomecontroldemo.mqtt;

import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSimple {

    private MqttAndroidClient mqttAndroidClient;

    public MqttSimple(MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }

    public void test() {
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("lpy", "connectionLost", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String body = new String(message.getPayload());
                Log.w("lpy", "messageArrived " + body);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setConnectionTimeout(3000);
        mqttConnectOptions.setKeepAliveInterval(90);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);

        try {
            // 参考 https://help.aliyun.com/document_detail/54225.html

            // Signature 方式
            mqttConnectOptions.setUserName("Signature|" + Config.accessKey + "|" + Config.instanceId);
//            mqttConnectOptions.setPassword(Tool.macSignature(Config.clientId, Config.secretKey).toCharArray());
            mqttConnectOptions.setPassword("jJ13ykgRFPXG0cK5zJ1RVxnHDq4=".toCharArray());

            /**
             * Token方式
             *  mqttConnectOptions.setUserName("Token|" + Config.accessKey + "|" + Config.instanceId);
             *  mqttConnectOptions.setPassword("RW|xxx");
             */
        } catch (Exception e) {
            Log.e("lpy", "setPassword", e);
        }

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("lpy", "connect onSuccess");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("lpy", "connect onFailure", exception);
                }
            });
        } catch (MqttException e) {
            Log.e("lpy", "exception", e);
        }
    }

    public void subscribeToTopic() {
        try {
            final String[] topicFilter = {Config.topic1, Config.topic2, Config.topic3, Config.topic4};
            final int[] qos = {1, 1, 1, 1};
            mqttAndroidClient.subscribe(topicFilter, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("lpy", "subscribe success");
                    publishMessage();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("lpy", "subscribe failed", exception);
                }
            });
//==========================================================================
//            final String[] topicFilter = {Config.topic};
//            final int[] qos = {1};
//            mqttAndroidClient.subscribe(topicFilter, qos, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.w("lpy", "subscribe success");
//                    publishMessage();
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Log.e("lpy", "subscribe failed", exception);
//                }
//            });

        } catch (MqttException ex) {
            Log.e("lpy", "subscribe exception", ex);
        }
    }


    public void publishMessage() {
        try {
            MqttMessage message = new MqttMessage();
            final String msg = "yibeiliu is awesome!";
            message.setPayload(msg.getBytes());
            mqttAndroidClient.publish(Config.topic1, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("lpy", "publish success:" + msg);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("lpy", "publish failed:" + msg);
                }
            });
        } catch (MqttException e) {
            Log.e("lpy", "publish exception", e);
        }
    }

}
