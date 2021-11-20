package tech.hazm.hazmandroid.Model;

import java.io.Serializable;

public class MqttMsgModel implements Serializable {
    public long id = 0;
    public String msg = "";

    public MqttMsgModel(String msg) {
        this.msg = msg;
    }

    public MqttMsgModel(long id, String msg) {
        this.id = id;
        this.msg = msg;
    }
}
