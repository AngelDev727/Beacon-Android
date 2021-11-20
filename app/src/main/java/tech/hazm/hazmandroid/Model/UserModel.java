package tech.hazm.hazmandroid.Model;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class UserModel implements Serializable {

    public String time = "", userName = "", phoneNumber = "", pwd = "", os = "", osVersion= "", manufacture = "", phoneModel = "", appVersion = "";


    public UserModel(String userName, String phoneNumber, String pwd) {
        time = String.valueOf(System.currentTimeMillis());
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.pwd = pwd;
        os = "android";
        osVersion = Build.VERSION.RELEASE;
        manufacture = Build.MANUFACTURER;
        phoneModel = Build.MODEL;
        appVersion = "1.0";
    }

    public String getJSONString(){

        JSONObject object = new JSONObject();
        try {
            object.put("time", time);
            object.put("phone", phoneNumber);
            object.put("uname", userName);
            object.put("pwd", pwd);
            object.put("os", os);
            object.put("osVersion", osVersion);
            object.put("manufacture", manufacture);
            object.put("appVersion", appVersion);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }
}
