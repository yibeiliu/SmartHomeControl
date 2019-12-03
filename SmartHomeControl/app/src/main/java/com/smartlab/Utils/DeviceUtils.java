package com.smartlab.Utils;

import com.smartlab.data.SmartDevice;

public class DeviceUtils {

    /**
     * 检查欲绑定设备是否是指定的设备
     *
     * @param smartDevice
     * @return
     */
    public static boolean isDeviceInThreeList(SmartDevice smartDevice) {
        return StaticValues.AIR_PURIFIER.equals(smartDevice.getDeviceName())
                || StaticValues.HUMIDIFIER.equals(smartDevice.getDeviceName())
                || StaticValues.WATER_PURIFIER.equals(smartDevice.getDeviceName());
    }
}
