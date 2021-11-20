package tech.hazm.hazmandroid.Model;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;

import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Service.BleScan;
import tech.hazm.hazmandroid.Utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

import static android.content.Context.BATTERY_SERVICE;

public class HeartBeatModel implements Serializable {
    public String time = "", ddt = "", id = "", lat = "", lon = "", sec = "", tgs = "", d_bat = "", gps = "", ble = "", p_bat = "", loc_access = "", type= ""
            ,  speed, bearing, locAcc, locMode;

    @SuppressLint("NewApi")
    public HeartBeatModel(Context context) {
        time = String.valueOf(System.currentTimeMillis());
        ddt = time;

        id = Common.userModel.userName;

        lat = Common.lat;
        lon = Common.lng;
        sec = Common.strMajor + Common.strMinor;
        tgs = String.valueOf(BleScan.tgs);
        d_bat = Common.strTxPower;

        gps = BleScan.gpsEnable;
        if (Common.isMockOn){
            LogUtil.e("mock location is on");
            gps = "2";
        }

        ble = BleScan.bleEnable;

        BatteryManager bm = (BatteryManager)context.getSystemService(BATTERY_SERVICE);
        p_bat = String.valueOf(bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));

        loc_access = "1";
        type = "2";

        speed = Common.speed;
        bearing = Common.bearing;
        locAcc = Common.locAcc;
        locMode = Common.locMode;
    }

    public HeartBeatModel(String time, String ddt, String id, String lat, String lon, String sec, String tgs, String d_bat, String gps,
                          String ble, String p_bat, String loc_access, String type, String speed, String bearing, String locAcc, String locMode) {
        this.time = time;
        this.ddt = ddt;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.sec = sec;
        this.tgs = tgs;
        this.d_bat = d_bat;
        this.gps = gps;
        this.ble = ble;
        this.p_bat = p_bat;
        this.loc_access = loc_access;
        this.type = type;
        this.speed = speed;
        this.bearing = bearing;
        this.locAcc = locAcc;
        this.locMode = locMode;
    }

    public String getJSONString(){

        JSONObject object = new JSONObject();
        try {
            object.put("time", time);
            object.put("ddt", ddt);
            object.put("id", id);
            object.put("lat", lat);
            object.put("lon", lon);
            object.put("sec", sec);
            object.put("tgs", tgs);
            object.put("d_bat", d_bat);
            object.put("gps",gps);
            object.put("ble", ble);
            object.put("p_bat", p_bat);
            object.put("loc_access", loc_access);
            object.put("type", type);
            object.put("speed", speed);
            object.put("bearing", bearing);
            object.put("locAcc", locAcc);
            object.put("locMode", locMode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }


    @SuppressLint("MissingPermission")
    public boolean isMockLocationOn() {

        Context context = Common.myApp.getApplicationContext();

        LocationManager myLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            if (location == null){
                return false;
            }else {
                return Objects.requireNonNull(location).isFromMockProvider();
            }

        } else {

            String mockLocation = "0";
            try {
                mockLocation = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return !mockLocation.equals("0");
        }
    }
}
