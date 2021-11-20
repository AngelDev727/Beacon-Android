package tech.hazm.hazmandroid.Constant;

import android.content.ComponentName;
import android.content.Intent;

public class Constant {
    public static final String BROADCAST_MSG_HOME_FRAGMENT = "filter_home_fragment";
    public static final String MSG_HOME_FRAGMENT = "msg_home_fragment";
    public static final int LOCATION_UPDATE_TIME = 3000;

    public static final String LINK_DOWN = "LinkDown";
    public static final String LINK_UP = "LinkUp";
    public static final String LINK_CORRUPTED = "Corrupted";
    public static final String USER_OUT_SIDE = "user_out_side";
    public static final int BEACON_FOUNDED = 1;
    public static final int UNKNOWN_DEVICE_FOUNDED = 2;
    public static final int BEACON_CORRUPTED = 3;
    public static final String BLE_OFF = "bleOffed";
    public static final String GPS_OFF = "gpsOffed";

    public static final String MQTT_CONNECT_FAILURE = "mqtt_connect_failure";
    public static final String SUBSCRIBE_BASE_API = "tech/hazm/tech/mobile/app/";
    public static final String PUBLIC_BASE_API = "tech/hazm/mobile/backend/";

    public static final int MAX_TRIGER_1 = 255;

    public static final String FILE_PATH = "file_path";

    //// parameters of API
    public static final String ACK = "ack";
    public static final String MSG = "msg";
    public static final String NAME = "name";
    public static final String UUID = "uuid";
    public static final String LOC_ACK = "loc_ack";
    public static final String LOC_ACC = "locAcc";
}
