package com.smartlab.mqtt;

import com.smartlab.data.mqtt.ProtocolData;
import com.smartlab.data.mqtt.ProtocolDeviceStatus;
import com.smartlab.model.Constants;

import java.util.ArrayList;
import java.util.List;

public class ProtocalAnalyst {

    public static ProtocolData analyze(String msg, int currentDeviceType) {
        ProtocolData protocolData = null;
        String[] splitResult = msg.split(Constants.SPLIT);
        if (currentDeviceType != Integer.parseInt(splitResult[1])) {
            return null;
        }
        if (msg.startsWith(Constants.HEADER_ONE)) {
            protocolData = new ProtocolData();
            protocolData.setHeader(Constants.HEADER_ONE);
            protocolData.setDeviceType(Integer.parseInt(splitResult[1]));
            ProtocolDeviceStatus status = new ProtocolDeviceStatus();
            status.setProtocolType(Integer.parseInt(splitResult[2]));
            status.setProtocolContent(splitResult[3]);
            List<ProtocolDeviceStatus> protocolDeviceStatusList = new ArrayList<>();
            protocolDeviceStatusList.add(status);
            protocolData.setProtocolDeviceStatuses(protocolDeviceStatusList);
            protocolData.setTotalSize(Integer.parseInt(splitResult[4]));
        } else if (msg.startsWith(Constants.HEADER_ALL)) {
            protocolData = new ProtocolData();
            protocolData.setHeader(Constants.HEADER_ALL);
            protocolData.setDeviceType(Integer.parseInt(splitResult[1]));
            List<ProtocolDeviceStatus> protocolDeviceStatusList = new ArrayList<>();
            for (int i = 0; i < (splitResult.length - 4); i += 2) {
                ProtocolDeviceStatus status = new ProtocolDeviceStatus();
                status.setProtocolType(Integer.parseInt(splitResult[i + 2]));
                status.setProtocolContent(splitResult[i + 3]);
                protocolDeviceStatusList.add(status);
            }
            protocolData.setProtocolDeviceStatuses(protocolDeviceStatusList);
        }
        return protocolData;
    }
}
