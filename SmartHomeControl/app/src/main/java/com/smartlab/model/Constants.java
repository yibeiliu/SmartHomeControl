package com.smartlab.model;

public class Constants {
    public static final String HEADER_ALL = "#SMARTALL";

    public static final String HEADER_ONE = "#SMARTEMAL";

    public static final String SPLIT = ",";
    public static final String ENDING = "*HH";

    public static final int DEVICE_WATER_PURIFIER = 1;
    public static final int DEVICE_AIR_CLEANER = 2;
    public static final int DEVICE_MOISTURIZER = 3;

    public class ChineseName {
        public static final String WATER_PURIFIER = "富氢净水机";
        public static final String AIR_CLEANER = "尚清空气净化器";
        public static final String MOISTURIZER = "富氢杀菌加湿器";
    }

    public class WifiMac {
        public static final String WATER_PURIFIER_WIFI = "50:02:91:C7:FC:1E";
        public static final String AIR_CLEANER_WIFI = "50:02:91:B9:25:28";
        public static final String MOISTURIZER_WIFI = "50:02:91:C8:3D:6C";
    }

    public class IpAddress {
        public static final String WATER_PURIFIER_IP = "192.168.11.103";
        public static final String AIR_CLEANER_IP = "192.168.11.104";
        public static final String MOISTURIZER_IP = "192.168.11.105";
    }


    public enum DEVICE_TYPE {
        WATER_PURIFIER(1),
        AIR_CLEANER(2),
        MOISTURIZER(3);

        int value;

        public int value() {
            return value;
        }

        DEVICE_TYPE(int value) {
            this.value = value;
        }
    }

    public enum PROTOCOL_TYPE {
        TIME_STATE(1),
        POWER_STATE(2),
        WATER_STOP(3),
        WATER_MAKING(4),
        HEATING(5),
        TEMPERATURE_HOLD(6),
        WIND_LEVEL(7),
        WIND_DIRECTION(8),
        TEMPERATURE(9),
        MALFUNCTION(10),
        FILTER_CORE(11),
        MAINTAINENCE(12),
        BLUETOOTH(13),
        WHOLSE_STATE_INQUIRY(14);


        int value;

        PROTOCOL_TYPE(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    public class QRCode {
        public static final String COMMA = ",";
    }
}
