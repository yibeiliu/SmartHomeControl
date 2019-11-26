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
import com.smartlab.mqtt.ProtocalAnalyst;
import com.smartlab.uicomponent.CXToggleButton;

import java.util.List;

public class WaterPurifierActivity extends BaseMqttActivity {

    private LinearLayout llPowerState;
    private TextView tvPowerText;
    private CXToggleButton tbPowerButton;

    private LinearLayout llFilterState;
    private TextView tvFilterText;
    private CXToggleButton tbFilterButton;

    private LinearLayout llMaintain;
    private TextView tvMaintainText;
    private CXToggleButton tbMaintainButton;

    private LinearLayout llBluetooth;
    private TextView tvBluetoothText;
    private CXToggleButton tbBluetoothButton;

    private LinearLayout llControlLayout;
    private TextView tvTimeStatus;
    private SegmentControl scWaterStatus;
    private SegmentControl scWaterHeatingStatus;

    private static final String POWER_TAG = "POWER_TAG";
    private static final String FIlTER_TAG = "FIlTER_TAG";
    private static final String MAINTAIN_TAG = "MAINTAIN_TAG";
    private static final String BLUETOOTH_TAG = "BLUETOOTH_TAG";

    private CXToggleButton.OnToggleClickListener listener = new CXToggleButton.OnToggleClickListener() {
        @Override
        public void onToggleClick(View view) {
            String tag = (String) view.getTag();
            switch (tag) {
                case POWER_TAG:
                    sendRequest(0, Constants.DEVICE_TYPE.WATER_PURIFIER.value(),
                            Constants.PROTOCOL_TYPE.POWER_STATE.value(), tbPowerButton.isChecked() ? 0 : 1);
                    break;
                case FIlTER_TAG:
                    sendRequest(0, Constants.DEVICE_TYPE.WATER_PURIFIER.value(),
                            Constants.PROTOCOL_TYPE.FILTER_CORE.value(), tbFilterButton.isChecked() ? 0 : 1);
                    break;
                case MAINTAIN_TAG:
                    sendRequest(0, Constants.DEVICE_TYPE.WATER_PURIFIER.value(),
                            Constants.PROTOCOL_TYPE.MAINTAINENCE.value(), tbMaintainButton.isChecked() ? 0 : 1);
                    break;
                case BLUETOOTH_TAG:
                    sendRequest(0, Constants.DEVICE_TYPE.WATER_PURIFIER.value(),
                            Constants.PROTOCOL_TYPE.BLUETOOTH.value(), tbBluetoothButton.isChecked() ? 0 : 1);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_water_purifier);
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        llControlLayout = findViewById(R.id.water_purifier_control_ll);
        tvTimeStatus = findViewById(R.id.water_purifier_time_tv);
        scWaterStatus = findViewById(R.id.water_purifier_water_status_sc);
        scWaterStatus.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                if (index == 0) {
                    //停水
                    sendRequest(0, Constants.DEVICE_TYPE.WATER_PURIFIER.value(),
                            Constants.PROTOCOL_TYPE.WATER_STOP.value(), index + 1);
                } else {
                    //制水
                    sendRequest(0, Constants.DEVICE_TYPE.WATER_PURIFIER.value(),
                            Constants.PROTOCOL_TYPE.WATER_MAKING.value(), index + 1);
                }
            }
        });
        scWaterHeatingStatus = findViewById(R.id.water_purifier_heating_status_sc);
        scWaterHeatingStatus.setOnSegmentControlClickListener(new SegmentControl.OnSegmentControlClickListener() {
            @Override
            public void onSegmentControlClick(int index) {
                if (index == 0) {
                    //保温
                    sendRequest(0, Constants.DEVICE_TYPE.WATER_PURIFIER.value(),
                            Constants.PROTOCOL_TYPE.TEMPERATURE_HOLD.value(), 1);
                } else {
                    //加热
                    sendRequest(0, Constants.DEVICE_TYPE.WATER_PURIFIER.value(),
                            Constants.PROTOCOL_TYPE.HEATING.value(), 1);
                }
            }
        });

        llPowerState = findViewById(R.id.llPowerState);
        tvPowerText = llPowerState.findViewById(R.id.tvContent);
        tbPowerButton = llPowerState.findViewById(R.id.toggleButton);
        tbPowerButton.setTag(POWER_TAG);
        tbPowerButton.setOnToggleClickListener(listener);

        llFilterState = findViewById(R.id.llFilterCore);
        tvFilterText = llFilterState.findViewById(R.id.tvContent);
        tbFilterButton = llFilterState.findViewById(R.id.toggleButton);
        tbFilterButton.setTag(FIlTER_TAG);
        tbFilterButton.setOnToggleClickListener(listener);

        llBluetooth = findViewById(R.id.llBluetooth);
        tvBluetoothText = llBluetooth.findViewById(R.id.tvContent);
        tbBluetoothButton = llBluetooth.findViewById(R.id.toggleButton);
        tbBluetoothButton.setTag(FIlTER_TAG);
        tbBluetoothButton.setOnToggleClickListener(listener);

        llMaintain = findViewById(R.id.llMaintain);
        tvMaintainText = llMaintain.findViewById(R.id.tvContent);
        tbMaintainButton = llMaintain.findViewById(R.id.toggleButton);
        tbMaintainButton.setTag(MAINTAIN_TAG);
        tbMaintainButton.setOnToggleClickListener(listener);
    }

    @Override
    protected void notifySubscribeSuccess() {
//        Toast.makeText(this, "净水器连接成功", Toast.LENGTH_SHORT).show();
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
                } else {
                    //关机状态
                    tvPowerText.setText("关机中");
                    llControlLayout.setVisibility(View.INVISIBLE);
                    tbPowerButton.setToggleButtonState(false);
                }
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.TIME_STATE.value()) {
                tvTimeStatus.setText(ProtocalAnalyst.formatTime(item.getProtocolContent()));
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.HEATING.value()
                    || item.getProtocolType() == Constants.PROTOCOL_TYPE.TEMPERATURE_HOLD.value()) {//0-停止 1-开始 2-默认
                int currentHeatingStatus = Integer.parseInt(item.getProtocolContent());
                currentDeviceStatusMap.put(Constants.PROTOCOL_TYPE.HEATING.value(), currentHeatingStatus);
                scWaterHeatingStatus.setSelectedIndex(currentHeatingStatus);
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.WATER_MAKING.value()
                    || item.getProtocolType() == Constants.PROTOCOL_TYPE.WATER_STOP.value()) {//0-停止 1-开始 2-默认
                currentDeviceStatusMap.put(Constants.PROTOCOL_TYPE.WATER_MAKING.value(), Integer.parseInt(item.getProtocolContent()));
                scWaterStatus.setSelectedIndex(Integer.parseInt(item.getProtocolContent()));
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.FILTER_CORE.value()) {
                currentDeviceStatusMap.put(Constants.PROTOCOL_TYPE.FILTER_CORE.value(), Integer.parseInt(item.getProtocolContent()));
                if (Integer.parseInt(item.getProtocolContent()) == 1) {
                    tvFilterText.setText("滤芯维护中...");
                    tbFilterButton.setToggleButtonState(true);
                } else {
                    tvFilterText.setText("滤芯不在维护中");
                    tbFilterButton.setToggleButtonState(false);
                }
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.MAINTAINENCE.value()) {
                currentDeviceStatusMap.put(Constants.PROTOCOL_TYPE.MAINTAINENCE.value(), Integer.parseInt(item.getProtocolContent()));
                if (Integer.parseInt(item.getProtocolContent()) == 1) {
                    tvMaintainText.setText("维护中...");
                    tbMaintainButton.setToggleButtonState(true);
                } else {
                    tvMaintainText.setText("维护停止中");
                    tbMaintainButton.setToggleButtonState(false);
                }
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
        } else if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.HEATING.value()
                || currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.TEMPERATURE_HOLD.value()) {//0-停止 1-开始 2-默认
            int currentHeatingStatus = currentDeviceStatusMap.get(Constants.PROTOCOL_TYPE.HEATING.value());
            scWaterHeatingStatus.setSelectedIndex(currentHeatingStatus);
        } else if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.WATER_STOP.value()
                || currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.WATER_MAKING.value()) {//0-停止 1-开始 2-默认
            int currentWaterStatus = currentDeviceStatusMap.get(Constants.PROTOCOL_TYPE.WATER_MAKING.value());
            scWaterStatus.setSelectedIndex(currentWaterStatus);
        } else if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.FILTER_CORE.value()) {
            int currentFilterCoreStatus = currentDeviceStatusMap.get(Constants.PROTOCOL_TYPE.FILTER_CORE.value());
            if (currentFilterCoreStatus == 1) {
                tvFilterText.setText("滤芯维护中...");
                tbFilterButton.setToggleButtonState(true);
            } else {
                tvFilterText.setText("滤芯不在维护中");
                tbFilterButton.setToggleButtonState(false);
            }
        } else if (currentWaitMsgProtocolType == Constants.PROTOCOL_TYPE.MAINTAINENCE.value()) {
            int currentMaintainenceStatus = currentDeviceStatusMap.get(Constants.PROTOCOL_TYPE.MAINTAINENCE.value());
            if (currentMaintainenceStatus == 1) {
                tvMaintainText.setText("维护中...");
                tbMaintainButton.setToggleButtonState(true);
            } else {
                tvMaintainText.setText("维护停止中");
                tbMaintainButton.setToggleButtonState(false);
            }
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
        return Constants.DEVICE_TYPE.WATER_PURIFIER.value();
    }
}
