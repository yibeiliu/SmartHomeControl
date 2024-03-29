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
    private Context context;

    public interface OnMqttActionListener {
        void onSubscribeSuccess();

        void onMessageReceived(String msg);

        void onFail(ErrorCode errorCode);

        void onPublishSuccess();
    }

    private MqttManager(Context context) {
        this.context = context;
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
        mqttAndroidClient = null;
    }

    public void connect(final OnMqttActionListener listener) {
        this.listener = listener;
        mqttAndroidClient = new MqttAndroidClient(context, Config.serverUri, Config.clientId);
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
            mqttConnectOptions.setPassword(Config.pwd.toCharArray());

            /**
             * Token方式
             *  mqttConnectOptions.setUserName("Token|" + Config.accessKey + "|" + Config.instanceId);
             *  mqttConnectOptions.setPassword("RW|xxx");
             */
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

        try {
            mqttAndroidClient.registerResources(context);
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
            final int[] qos = {1};
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
                    Log.e(TAG, "publishMessage onSuccess");
                    listener.onPublishSuccess();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG, "publishMessage onFailure");
                    listener.onFail(ErrorCode.SEND_FAIL);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(int requestType, int deviceType, int protocolType, int data) {
        String request = buildRequest(requestType, deviceType, protocolType, data);
        Log.d("lpy", "[sendRequest] " + request);
        mqttManager.publishMessage(request);
    }

    private String buildRequest(int requestType, int deviceType, int protocolType, int data) {
        String result = (requestType == 0 ? Constants.HEADER_ONE : Constants.HEADER_ALL) + Constants.SPLIT +
                deviceType + Constants.SPLIT +
                protocolType + Constants.SPLIT +
                data + Constants.SPLIT +
                "{}" + Constants.SPLIT +
                Constants.ENDING;
        int totalLength = result.length() - 2;

        return result.replace("{}", "" + totalLength);
    }
}
