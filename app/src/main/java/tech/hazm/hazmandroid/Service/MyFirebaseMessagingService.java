package tech.hazm.hazmandroid.Service;

import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Utils.LogUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        sendMyNotification(message.getNotification().getBody());
    }


    private void sendMyNotification(String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Common.notificationHelper.getManager().notify(100, Common.notificationHelper.getNotification1(message, true).build());
        }else {
            Common.notificationHelper.getManager().notify(100, Common.notificationHelper.getNotification2(message, true).build());
        }
    }
}
