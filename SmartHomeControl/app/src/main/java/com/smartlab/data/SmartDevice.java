package com.smartlab.data;

import androidx.annotation.Nullable;

/**
 * @Author peiyi.liu
 * @Date 11/4/2019 1:59 PM
 */
public class SmartDevice extends HomeMultiItem {

    public SmartDevice(String deviceName, String deviceMacAddress,
                       int icon, String deviceChineseName, String deviceWifiMacAddress, String ipAddress) {
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.icon = icon;
        this.deviceChineseName = deviceChineseName;
        this.deviceWifiMacAddress = deviceWifiMacAddress;
        this.ipAddress = ipAddress;
    }

    private String deviceName;
    private String deviceMacAddress;
    private int icon;
    private String deviceChineseName;
    private String deviceWifiMacAddress;
    private String ipAddress;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    @Override
    public int getItemType() {
        return HomeMultiItem.DEVICE;
    }

    public String getDeviceChineseName() {
        return deviceChineseName;
    }

    public void setDeviceChineseName(String deviceChineseName) {
        this.deviceChineseName = deviceChineseName;
    }

    public String getDeviceWifiMacAddress() {
        return deviceWifiMacAddress;
    }

    public void setDeviceWifiMacAddress(String deviceWifiMacAddress) {
        this.deviceWifiMacAddress = deviceWifiMacAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
