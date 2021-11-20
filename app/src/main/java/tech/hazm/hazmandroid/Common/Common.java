package tech.hazm.hazmandroid.Common;

import tech.hazm.hazmandroid.Model.UserModel;
import tech.hazm.hazmandroid.MyApp;
import tech.hazm.hazmandroid.Utils.MqttHelper;
import tech.hazm.hazmandroid.Utils.NotificationHelper;

public class Common {

    public static MyApp myApp = null;

    public static String  uuid = "";
    public static String lat = "";
    public static String lng = "";
    public static String homeMsg = "";

    public static String strMinor = "";
    public static String strMajor = "";
    public static String strTxPower = "";

    public static UserModel userModel = null;

    public static MqttHelper mqttHelper = null;

    public static boolean isInternetAvailable = false;
    public static int loc_ack = 0;

    public static String name = "";

    public static NotificationHelper notificationHelper;
    public static String speed = "";
    public static String bearing = "";
    public static String locAcc = "";
    public static boolean isMockOn = false;
    public static String locMode = "";
}
