package com.smartlab.data;

import androidx.annotation.Nullable;

/**
 * @Author peiyi.liu
 * @Date 11/4/2019 1:59 PM
 */
public class SmartDevice extends HomeMultiItem {
    public SmartDevice(String deviceName, String deviceMacAddress, int icon) {
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceMacAddress;
        this.icon = icon;
    }

    private String deviceName;
    private String deviceMacAddress;
    private int icon;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
