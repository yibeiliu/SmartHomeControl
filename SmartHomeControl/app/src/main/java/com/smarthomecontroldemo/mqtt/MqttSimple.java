package com.smarthomecontroldemo.mqtt;

import android.content.Context;
import android.util.Log;

import com.smarthomecontroldemo.model.Constants;

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
    private static MqttSimple mqttSimple;

    private static final String SPLIT = ",";
    private static final String ENDING = "*HH";

    public interface OnMqttRequestCallback {
        void onSuccess(IMqttToken asyncActionToken);

        void onFailure(IMqttToken asyncActionToken);
    }

    public interface OnMqttConnectionCallback {
        void onConnectionLost();

        void onMessageReceived(String topic, MqttMessage message);

        void onDeliverComplete(IMqttDeliveryToken token);
    }

    private MqttSimple(Context context) {
        this.mqttAndroidClient = new MqttAndroidClient(context,Config.serverUri, Config.clientId);
    }

    public static MqttSimple getInstance(Context context) {
        if (mqttSimple == null) {
            synchronized (MqttSimple.class) {
                if (mqttSimple == null) {
                    mqttSimple = new MqttSimple(context);
                }
            }
        }

        return mqttSimple;
    }

    public void unregisterResources() {
        mqttAndroidClient.unregisterResources();
    }

    public void connect(final OnMqttConnectionCallback callback) {
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("lpy", "connectionLost", cause);
                if (callback != null) {
                    callback.onConnectionLost();
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String body = new String(message.getPayload());
                Log.w("lpy", "messageArrived " + body);

                if (callback != null) {
                    callback.onMessageReceived(topic, message);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                if (callback != null) {
                    callback.onDeliverComplete(token);
                }
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
                    subscribeToTopic(null);
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

    public void subscribeToTopic(final OnMqttRequestCallback callback) {
        try {
            final String[] topicFilter = {Config.TOPIC_SEND, Config.TOPIC_RECEIVE};
            final int[] qos = {1, 1, 1, 1};
            mqttAndroidClient.subscribe(topicFilter, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if (callback != null) {
                        callback.onSuccess(asyncActionToken);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (callback != null) {
                        callback.onFailure(asyncActionToken);
                    }
                }
            });

        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void publishMessage(final String request) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(request.getBytes());
            mqttAndroidClient.publish(Config.TOPIC_SEND, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(int requestType, int deviceType, int protocolType, int data) {
        String request = buildRequest(requestType, deviceType, protocolType, data);
        mqttSimple.publishMessage(request);
    }

    private String buildRequest(int requestType, int deviceType, int protocolType, int data) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(requestType == 0 ? Constants.HEADER_ONE : Constants.HEADER_ALL)
                .append(deviceType).append(SPLIT)
                .append(protocolType).append(SPLIT)
                .append(data).append(SPLIT)
                .append(1).append(SPLIT)
                .append(ENDING);

        return stringBuilder.toString();
    }
}
