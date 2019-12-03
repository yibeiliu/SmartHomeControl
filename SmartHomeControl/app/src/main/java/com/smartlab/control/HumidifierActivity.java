package com.smartlab.control;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sevenheaven.segmentcontrol.SegmentControl;
import com.smartlab.R;
import com.smartlab.data.mqtt.ProtocolData;
import com.smartlab.data.mqtt.ProtocolDeviceStatus;
import com.smartlab.model.Constants;
import com.smartlab.mqtt.BaseMqttActivity;
import com.smartlab.mqtt.ErrorCode;
import com.smartlab.uicomponent.CXToggleButton;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HumidifierActivity extends BaseMqttActivity {

    private LinearLayout llPowerState;
    private TextView tvPowerText;
    private CXToggleButton tbPowerButton;

    private LinearLayout llControlLayout;
    private SegmentControl scWindLevel;
    private SegmentControl scWindDirection;

    private LinearLayout llBluetooth;
    private TextView tvBluetoothText;
    private CXToggleButton tbBluetoothButton;

    private static final String POWER_TAG = "POWER_TAG";
    private static final String BLUETOOTH_TAG = "BLUETOOTH_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_humidifier);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        llControlLayout = findViewById(R.id.humidifier_control_ll);
        scWindLevel = findViewById(R.id.humidifier_air_level_sc);
        scWindLevel.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                sendRequest(0, Constants.DEVICE_TYPE.MOISTURIZER.value(),
                        Constants.PROTOCOL_TYPE.WIND_LEVEL.value(), index + 1);
            }
        });
        scWindDirection = findViewById(R.id.humidifier_air_direction_sc);
        scWindDirection.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                sendRequest(0, Constants.DEVICE_TYPE.MOISTURIZER.value(),
                        Constants.PROTOCOL_TYPE.WIND_DIRECTION.value(), index + 1);
            }
        });

        llPowerState = findViewById(R.id.humidifier_power_state);
        tvPowerText = llPowerState.findViewById(R.id.tvContent);
        tvPowerText.setText("设备连接中...");
        tbPowerButton = llPowerState.findViewById(R.id.toggleButton);
        tbPowerButton.setOnToggleClickListener(new CXToggleButton.OnToggleClickListener() {
            @Override
            public void onToggleClick(@Nullable View view) {
                sendRequest(0, Constants.DEVICE_TYPE.MOISTURIZER.value(),
                        Constants.PROTOCOL_TYPE.POWER_STATE.value(), tbPowerButton.isChecked() ? 1 : 0);
            }
        });

        llBluetooth = findViewById(R.id.llBluetooth);
        tvBluetoothText = llBluetooth.findViewById(R.id.tvContent);
        tbBluetoothButton = llBluetooth.findViewById(R.id.toggleButton);
        tbBluetoothButton.setOnToggleClickListener(new CXToggleButton.OnToggleClickListener() {
            @Override
            public void onToggleClick(@Nullable View view) {
                sendRequest(0, Constants.DEVICE_TYPE.MOISTURIZER.value(),
                        Constants.PROTOCOL_TYPE.BLUETOOTH.value(), tbBluetoothButton.isChecked() ? 1 : 0);
            }
        });
    }

    @Override
    protected void notifySubscribeSuccess() {
//        Toast.makeText(this, "加湿器连接成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyMessageReceived(ProtocolData protocolData) {
//        Toast.makeText(this, "收到消息", Toast.LENGTH_SHORT).show();

        if (protocolData == null) {
            return;
        }
        //先判断开关机状态
        List<ProtocolDeviceStatus> protocolDeviceStatuses = protocolData.getProtocolDeviceStatuses();
        for (ProtocolDeviceStatus item : protocolDeviceStatuses) {
            if (item.getProtocolType() == Constants.PROTOCOL_TYPE.POWER_STATE.value()) {
                int powerStatus = Integer.parseInt(item.getProtocolContent());
                currentDeviceStatusMap.put(Constants.PROTOCOL_TYPE.POWER_STATE.value(), powerStatus);
                if (powerStatus == 1) {
                    //开机状态
                    tvPowerText.setText("开机中");
                    llControlLayout.setVisibility(View.VISIBLE);
                    tbPowerButton.setToggleButtonState(true);
                    setDeviceOpen(true);
                } else {
                    //关机状态
                    tvPowerText.setText("关机中");
                    llControlLayout.setVisibility(View.INVISIBLE);
                    tbPowerButton.setToggleButtonState(false);
                    setDeviceOpen(false);
                }
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.WIND_LEVEL.value()) {
                currentDeviceStatusMap.put(Constants.PROTOCOL_TYPE.WIND_LEVEL.value(), Integer.parseInt(item.getProtocolContent()) - 1);
                scWindLevel.setSelectedIndex(Integer.parseInt(item.getProtocolContent()) - 1);
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.WIND_DIRECTION.value()) {
                int windDirection = Integer.parseInt(item.getProtocolContent());
                currentDeviceStatusMap.put(Constants.PROTOCOL_TYPE.WIND_DIRECTION.value(), windDirection - 1);
                scWindDirection.setSelectedIndex(windDirection - 1);
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.BLUETOOTH.value()) {
                currentDeviceStatusMap.put(Constants.PROTOCOL_TYPE.BLUETOOTH.value(), Integer.parseInt(item.getProtocolContent()));
                if (Integer.parseInt(item.getProtocolContent()) == 1) {
                    tvBluetoothText.setText("蓝牙已开启");
                    tbBluetoothButton.setToggleButtonState(true);
                } else {
                    tvBluetoothText.setText("蓝牙已关闭");
                    tbBluetoothButton.setToggleButtonState(false);
                }
            }
        }
    }

    @Override
    protected void notifyConnectFail(ErrorCode errorCode) {

    }

    @Override
    protected void notifyMsgTimeOut(int currentWaitMsgProtocolType) {
        if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.POWER_STATE.value()) {
            int currentPowerStatus = currentDeviceStatusMap.get(Constants.PROTOCOL_TYPE.POWER_STATE.value());
            if (currentPowerStatus == 1) {
                //返回开机状态
                tvPowerText.setText("开机中");
                llControlLayout.setVisibility(View.VISIBLE);
                tbPowerButton.setToggleButtonState(true);
            } else {
                //返回关机状态
                tvPowerText.setText("关机中");
                llControlLayout.setVisibility(View.INVISIBLE);
                tbPowerButton.setToggleButtonState(false);
            }
        } else if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.WIND_LEVEL.value()) {
            int currentWindLevelStatus = currentDeviceStatusMap.get(Constants.PROTOCOL_TYPE.WIND_LEVEL.value());
            scWindLevel.setSelectedIndex(currentWindLevelStatus);
        } else if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.WIND_DIRECTION.value()) {
            int currentWindDirectionStatus = currentDeviceStatusMap.get(Constants.PROTOCOL_TYPE.WIND_DIRECTION.value());
            scWindDirection.setSelectedIndex(currentWindDirectionStatus);
        } else if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.BLUETOOTH.value()) {
            int currentBluetoothStatus = currentDeviceStatusMap.get(Constants.PROTOCOL_TYPE.BLUETOOTH.value());
            if (currentBluetoothStatus == 1) {
                tvBluetoothText.setText("蓝牙已开启");
                tbBluetoothButton.setToggleButtonState(true);
            } else {
                tvBluetoothText.setText("蓝牙已关闭");
                tbBluetoothButton.setToggleButtonState(false);
            }
        }
    }

    @Override
    protected int currentDeviceType() {
        return Constants.DEVICE_TYPE.MOISTURIZER.value();
    }
}
