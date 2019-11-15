package com.smartlab.data.mqtt;

import java.util.List;

public class ProtocolData {
    private String header;
    private int deviceType;
    private List<ProtocolDeviceStatus> protocolDeviceStatuses;
    private int totalSize;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public List<ProtocolDeviceStatus> getProtocolDeviceStatuses() {
        return protocolDeviceStatuses;
    }

    public void setProtocolDeviceStatuses(List<ProtocolDeviceStatus> protocolDeviceStatuses) {
        this.protocolDeviceStatuses = protocolDeviceStatuses;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }
}
