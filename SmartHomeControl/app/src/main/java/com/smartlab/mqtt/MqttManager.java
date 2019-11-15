package com.smartlab.mqtt;

import android.content.Context;
import android.util.Log;

import com.smartlab.model.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttManager {
    public static final String TAG = "mqtt";

    private MqttAndroidClient mqttAndroidClient;
    private static MqttManager mqttManager;
    private OnMqttActionListener listener;


//    public interface OnMqttRequestCallback {
//        void onSuccess(IMqttToken asyncActionToken);
//
//        void onFailure(IMqttToken asyncActionToken);
//    }
//
//    public interface OnMqttConnectionCallback {
//        void onConnectionLost();
//
//        void onMessageReceived(String topic, MqttMessage message);
//
//        void onDeliverComplete(IMqttDeliveryToken token);
//    }

    public interface OnMqttActionListener {
        void onSubscribeSuccess();

        void onMessageReceived(String msg);

        void onFail(ErrorCode errorCode);

        void onPublishSuccess();
    }

    private MqttManager(Context context) {
        this.mqttAndroidClient = new MqttAndroidClient(context, Config.serverUri, Config.clientId);
    }

    public static MqttManager getInstance(Context context) {
        if (mqttManager == null) {
            synchronized (MqttManager.class) {
                if (mqttManager == null) {
                    mqttManager = new MqttManager(context);
                }
            }
        }

        return mqttManager;
    }

    public void unConnect() {
        mqttAndroidClient.unregisterResources();
    }

    public void connect(final OnMqttActionListener listener) {
        this.listener = listener;
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "connectionLost", cause);
                if (listener != null) {
                    listener.onFail(ErrorCode.CONNECT_FAIL);
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String body = new String(message.getPayload());
                Log.w(TAG, "messageArrived " + body);

                if (listener != null) {
                    listener.onMessageReceived(body);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "deliveryComplete");
                if (listener != null) {
                    listener.onFail(ErrorCode.CONNECT_FAIL);
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
            Log.e(TAG, "Exception", e);
        }

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w(TAG, "connect onSuccess");
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "connect onFailure", exception);
                    listener.onFail(ErrorCode.CONNECT_FAIL);
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "exception", e);
        }
    }

    public void subscribeToTopic() {
        if (listener == null) {
            throw new IllegalArgumentException("The listener is null, set the listener first please.");
        }
        try {
            final String[] topicFilter = {Config.TOPIC_RECEIVE};
            final int[] qos = {0};
            mqttAndroidClient.subscribe(topicFilter, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    listener.onSubscribeSuccess();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    listener.onFail(ErrorCode.CONNECT_FAIL);
                }
            });

        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private void publishMessage(final String request) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(request.getBytes());
            mqttAndroidClient.publish(Config.TOPIC_SEND, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    listener.onPublishSuccess();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    listener.onFail(ErrorCode.SEND_FAIL);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(int requestType, int deviceType, int protocolType, int data) {
        String request = buildRequest(requestType, deviceType, protocolType, data);
        mqttManager.publishMessage(request);
    }

    private String buildRequest(int requestType, int deviceType, int protocolType, int data) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(requestType == 0 ? Constants.HEADER_ONE : Constants.HEADER_ALL)
                .append(deviceType).append(Constants.SPLIT)
                .append(protocolType).append(Constants.SPLIT)
                .append(data).append(Constants.SPLIT)
                .append(1).append(Constants.SPLIT)
                .append(Constants.ENDING);

        return stringBuilder.toString();
    }
}
