package tech.hazm.hazmandroid.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class FcmTokenPacket implements Serializable {

    String time, user_name, fcm_token;

    public FcmTokenPacket(String user_name, String fcm_token) {
        this.time = String.valueOf(System.currentTimeMillis());;
        this.user_name = user_name;
        this.fcm_token = fcm_token;
    }

    public String getJSONString(){

        JSONObject object = new JSONObject();
        try {
            object.put("time", time);
            object.put("username", user_name);
            object.put("fcmToken", fcm_token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }
}
