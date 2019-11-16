package com.smartlab.control;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartlab.R;
import com.smartlab.data.mqtt.ProtocolData;
import com.smartlab.data.mqtt.ProtocolDeviceStatus;
import com.smartlab.model.Constants;
import com.smartlab.mqtt.BaseMqttActivity;
import com.smartlab.mqtt.ErrorCode;
import com.smartlab.uicomponent.CXToggleButton;

import org.jetbrains.annotations.Nullable;

public class AirPurifierActivity extends BaseMqttActivity {

    private LinearLayout llPowerState;
    private TextView tvPowerText;
    private CXToggleButton tbPowerButton;

    private LinearLayout llFilterState;
    private TextView tvFilterText;
    private CXToggleButton tbFilterButton;

    private LinearLayout llMaintain;
    private TextView tvMaintainText;
    private CXToggleButton tbMaintainButton;

    private static final String POWER_TAG = "POWER_TAG";
    private static final String FIlTER_TAG = "FIlTER_TAG";
    private static final String MAINTAIN_TAG = "MAINTAIN_TAG";

    private CXToggleButton.OnToggleClickListener listener = new CXToggleButton.OnToggleClickListener() {
        @Override
        public void onToggleClick(@Nullable View view) {
            String tag = (String) view.getTag();
            switch (tag) {
                case POWER_TAG:
                    sendRequest(0, 2, 2, 1);
                    break;
                case FIlTER_TAG:
                    break;
                case MAINTAIN_TAG:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_purifier);

        initView();
    }

    private void initView() {
        llPowerState = findViewById(R.id.llPowerState);
        tvPowerText = llPowerState.findViewById(R.id.tvContent);
        tbPowerButton = llPowerState.findViewById(R.id.toggleButton);
        tbPowerButton.setTag(POWER_TAG);
        tbPowerButton.setOnToggleClickListener(listener);

        llFilterState = findViewById(R.id.llFilterCore);
        tvFilterText = llFilterState.findViewById(R.id.tvContent);
        tbFilterButton = llFilterState.findViewById(R.id.toggleButton);
        tbFilterButton.setTag(FIlTER_TAG);

        llMaintain = findViewById(R.id.llMaintain);
        tvMaintainText = llMaintain.findViewById(R.id.tvContent);
        tbMaintainButton = llMaintain.findViewById(R.id.toggleButton);
        tbFilterButton.setTag(MAINTAIN_TAG);

        tvPowerText.setText("开机状态");
        tvFilterText.setText("滤芯状态");
        tvMaintainText.setText("维护");
    }

    @Override
    protected void notifySubscribeSuccess() {
        Toast.makeText(this, "净水器连接成功", Toast.LENGTH_SHORT).show();
        sendRequest(0, 1, 14, 0);
    }

    @Override
    protected void notifyMessageReceived(ProtocolData protocolData) {
        Toast.makeText(this, "收到消息", Toast.LENGTH_SHORT).show();

        if (protocolData.getDeviceType() == Constants.DEVICE_TYPE.AIR_CLEANER.value()) {

            if (protocolData.getProtocolDeviceStatuses().get(0).getProtocolType() == Constants.PROTOCOL_TYPE.POWER_STATE.value()) {
                ProtocolDeviceStatus protocolDeviceStatus = protocolData.getProtocolDeviceStatuses().get(0);
                if ("1".equals(protocolDeviceStatus.getProtocolContent())) {
                    tbPowerButton.setToggleButtonState(true);
                } else {
                    tbPowerButton.setToggleButtonState(false);
                }
            }
        }
    }

    @Override
    protected void notifyConnectFail(ErrorCode errorCode) {

    }

    @Override
    protected int currentDeviceType() {
        return Constants.DEVICE_TYPE.AIR_CLEANER.value();
    }
}
