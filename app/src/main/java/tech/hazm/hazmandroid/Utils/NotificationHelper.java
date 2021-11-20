package tech.hazm.hazmandroid.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import tech.hazm.hazmandroid.R;

public class NotificationHelper extends ContextWrapper {

    private NotificationManager manager;
    public String CHANNEL_ID = "com.sts.beacon";
    CharSequence name = getString(R.string.app_name);
    NotificationCompat.Builder notificationCompat_1, notificationCompat_2;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context ctx) {
        super(ctx);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel chan1 = new NotificationChannel(CHANNEL_ID,name, NotificationManager.IMPORTANCE_HIGH);
            chan1.setLightColor(Color.GREEN);
            chan1.enableLights(true);
            chan1.setLightColor(Color.RED);
            chan1.setShowBadge(true);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            chan1.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            chan1.enableVibration(true);
            getManager().createNotificationChannel(chan1);
        }
    }
    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationCompat.Builder getNotification1(String body, boolean isSound) {

        if (isSound){
            return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(body)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(getSmallIcon())
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setAutoCancel(true);
        }else {
            return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(getSmallIcon())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setAutoCancel(true);
        }
    }

    public NotificationCompat.Builder getNotification2(String body , boolean isSound) {

        if (isSound){
            return new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(body)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(getSmallIcon())
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setAutoCancel(true);
        }else {
            return new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(body)
                    .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(getSmallIcon())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                    .setAutoCancel(true);
        }
    }

    public NotificationCompat.Builder getNotification3(String title, String body) {
        int color= Color.BLUE;
        return notificationCompat_1.setContentTitle(title).setContentText(body).setColor(color).setAutoCancel(false);
    }

    public NotificationCompat.Builder getNotification4(String title, String body) {
        int color= Color.BLUE;
        return notificationCompat_2.setContentTitle(title).setContentText(body).setColor(color).setAutoCancel(false);
    }

    private int getSmallIcon() {
        return R.mipmap.ic_launcher;
    }

}
