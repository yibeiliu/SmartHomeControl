package com.smartlab.control;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidifier);
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
        tbPowerButton = llPowerState.findViewById(R.id.toggleButton);
        tbPowerButton.setOnToggleClickListener(new CXToggleButton.OnToggleClickListener() {
            @Override
            public void onToggleClick(@Nullable View view) {
                sendRequest(0, Constants.DEVICE_TYPE.MOISTURIZER.value(),
                        Constants.PROTOCOL_TYPE.POWER_STATE.value(), tbPowerButton.isChecked() ? 0 : 1);
            }
        });
    }

    @Override
    protected void notifySubscribeSuccess() {
        Toast.makeText(this, "加湿器连接成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void notifyMessageReceived(ProtocolData protocolData) {
        Toast.makeText(this, "收到消息", Toast.LENGTH_SHORT).show();

        if (protocolData == null) {
            return;
        }
        //先判断开关机状态
        List<ProtocolDeviceStatus> protocolDeviceStatuses = protocolData.getProtocolDeviceStatuses();
        for (ProtocolDeviceStatus item : protocolDeviceStatuses) {
            if (item.getProtocolType() == Constants.PROTOCOL_TYPE.POWER_STATE.value()) {
                int powerStatus = Integer.parseInt(item.getProtocolContent());
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
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.WIND_LEVEL.value()) {
                scWindLevel.setSelectedIndex(Integer.parseInt(item.getProtocolContent()) - 1);
            } else if (item.getProtocolType() == Constants.PROTOCOL_TYPE.WIND_DIRECTION.value()) {
                int windDirection = Integer.parseInt(item.getProtocolContent());
                scWindDirection.setSelectedIndex(windDirection - 1);
            }
        }
    }

    @Override
    protected void notifyConnectFail(ErrorCode errorCode) {

    }

    @Override
    protected int currentDeviceType() {
        return Constants.DEVICE_TYPE.MOISTURIZER.value();
    }
}
