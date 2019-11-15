package com.smartlab.model;

public class Constants {
    public static final String HEADER_ALL = "#SMARTALL";

    public static final String HEADER_ONE = "#SMARTEMAL";


    public static final int DEVICE_WATER_PURIFIER = 1;
    public static final int DEVICE_AIR_CLEANER = 2;
    public static final int DEVICE_MOISTURIZER = 3;

    public enum DEVICE_TYPE {
        WATER_PURIFIER(1),
        AIR_CLEANER(2),
        MOISTURIZER(3);

        int value;

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
}
