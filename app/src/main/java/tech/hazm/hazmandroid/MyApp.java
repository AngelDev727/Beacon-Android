package tech.hazm.hazmandroid;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.location.LocationRequest;

import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Service.BleScan;
import tech.hazm.hazmandroid.Utils.LogUtil;

import mobi.inthepocket.android.beacons.ibeaconscanner.IBeaconScanner;
import tech.hazm.hazmandroid.Utils.NotificationHelper;

public class MyApp extends Application implements LifecycleObserver {

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e("App started up");
        IBeaconScanner.initialize(IBeaconScanner.newInitializer(this).build());

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        Common.myApp = this;

        Common.notificationHelper =  new NotificationHelper(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        BleScan.isAppInBackground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        BleScan.isAppInBackground = false;
    }
}
