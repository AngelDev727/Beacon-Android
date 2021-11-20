package tech.hazm.hazmandroid.Service;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Constant.Constant;
import tech.hazm.hazmandroid.Database.DatabaseQueryClass;
import tech.hazm.hazmandroid.Model.HeartBeatModel;
import tech.hazm.hazmandroid.Model.MqttMsgModel;

import tech.hazm.hazmandroid.R;
import tech.hazm.hazmandroid.Utils.BleUtil;
import tech.hazm.hazmandroid.Utils.LogUtil;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BleScan extends LifecycleService implements InternetConnectivityListener {

    private static final Intent[] POWERMANAGER_INTENTS = {
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
            new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
    };


    public static int isBeaconDetected = -1;
    public static boolean isAppInBackground = false;
    private boolean isShownLinkUpNoti = false;
    private boolean isShownLinkDownNoti = false;
    private boolean isBeaconCorruptedNoti = false;
    private boolean isBleEnable = false;
    private boolean isLocationEnable = false;
    public static String gpsEnable = "";
    public static String bleEnable = "";
    private boolean isEven = false;

    public static int tgs = 0;

    private DatabaseQueryClass databaseQueryClass;
    private ArrayList<HeartBeatModel> offlinePacketList = new ArrayList<>();

    BleManager bleManager;
    Timer timer;

    int cnt = 60, cnt_5min = 30, cnt_2s = 2;

    InternetAvailabilityChecker mInternetAvailabilityChecker;

    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        // init SDK
        bleManager = BleManager.getInstance();
        bleManager.init(getApplication());

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(5000).build();
        bleManager.initScanRule(scanRuleConfig);

        IntentFilter bleStateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateListener, bleStateFilter);

        IntentFilter gpsStateFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        gpsStateFilter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        registerReceiver(gpsStateReceiver, gpsStateFilter);

        isBleEnable = bleManager.isBlueEnable();
        if (isBleEnable){
            bleEnable = "1";
        }else {
            bleEnable = "0";
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isLocationEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        setTimer();

        databaseQueryClass = new DatabaseQueryClass(this);
        offlinePacketList.clear();

        InternetAvailabilityChecker.init(this);
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        Common.mqttHelper.setCallback(mqttCallbackExtended);

        for (Intent intent : POWERMANAGER_INTENTS)
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                // show dialog to ask user action
                break;
            }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        showNotification(getString(R.string.app_name) + "is Running", false);
        if (checkGPSIsOpen()){
            gpsEnable = "1";
            startScan();
        }else {
            gpsEnable = "0";
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification(String msg, boolean isSound) {
        LogUtil.e("Notification msg === " + msg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            notificationHelper.getManager().notify(NOTI_ACHIVE100, notificationHelper.getNotification1(msg).build());
            startForeground(6, Common.notificationHelper.getNotification1(msg, isSound).build());
        }else {
//            notificationHelper.getManager().notify(NOTI_ACHIVE100, notificationHelper.getNotification2(msg).build());
            startForeground(6, Common.notificationHelper.getNotification2(msg, isSound).build());
        }
    }

    private void sleep10Sec(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startScan();
            }
        }, 10000);
    }

    @SuppressLint("NewApi")
    private void startScan() {
        bleManager.scan(bleScanCallback);
    }

    private void setTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cnt --;

                if (cnt == 0){
                    cnt = 60;

                    if (isBeaconDetected != Constant.BEACON_FOUNDED && isLocationEnable && isBleEnable){
                        sendBroadcastMsg(Constant.LINK_DOWN); tgs = 0;
                        if (/*isAppInBackground &&*/ !isShownLinkDownNoti){
                            showNotification(getString(R.string.please_stay_near_your_phone), true);
                            callAPI("Beacon Exit");
                            isShownLinkDownNoti = true;
                            isShownLinkUpNoti = false;
                            isBeaconCorruptedNoti = false;
                        }
                    }

                }

                cnt_5min --;
                if (cnt_5min == 0){
                    cnt_5min = 30;
                    HeartBeatModel model = new HeartBeatModel(BleScan.this);

                    isEven = !isEven;
                    if (isEven){
                        model.type = "1";
                    }else {
                        model.type = "2";
                    }

                    sendMqttMsg(model);
                }

                cnt_2s --;

                if (cnt_2s == 0 ){
                    cnt_2s = 2;
                    if (Common.isInternetAvailable && offlinePacketList.size() > 0){

                        HeartBeatModel model = offlinePacketList.get(0);

                        databaseQueryClass.deleteAllMsg(offlinePacketList.get(0).time);
                        offlinePacketList.remove(0);


                        model.time = String.valueOf(System.currentTimeMillis());

                        sendMqttMsg(model);
                    }

                }
            }
        }, 1000, 1000);
    }

    private BleScanCallback bleScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            sleep10Sec();
        }

        @Override
        public void onScanStarted(boolean success) {
            isBeaconDetected = -1;
        }

        @Override
        public void onScanning(BleDevice bleDevice) { }

        @Override
        public void onLeScan(BleDevice bleDevice) {
            super.onLeScan(bleDevice);
            // scanned device is shown in here
            isBeaconDetected = BleUtil.findBeaconPattern(bleDevice);
            if (isBeaconDetected == Constant.BEACON_FOUNDED && isBleEnable && isLocationEnable){

                sendBroadcastMsg(Constant.LINK_UP); tgs = 1;

                if (isAppInBackground && !isShownLinkUpNoti){

                    showNotification(getString(R.string.the_link_with_the_bracelet_is_up), false);

                    callAPI("Beacon entered");

                    isShownLinkUpNoti = true;
                    isShownLinkDownNoti = false;
                    isBeaconCorruptedNoti = false;
                }

                cnt = 60;

                if (Common.strMajor.equals("-1") || Common.strMinor.equals("-1") || Common.strTxPower.equals("-1")){
                    HeartBeatModel model = new HeartBeatModel(BleScan.this);
                    model.tgs = "2";
                    sendMqttMsg(model);
                }
            }

            if (isBeaconDetected == Constant.BEACON_CORRUPTED){

                sendBroadcastMsg(Constant.LINK_CORRUPTED); tgs = 2;

                if (isAppInBackground && !isBeaconCorruptedNoti){
                    showNotification(getString(R.string.app_name) + getString(R.string.beacon_corrupted), true);
                    callAPI("Tag corrupted");

                    isBeaconCorruptedNoti = true;
                    isShownLinkUpNoti = false;
                    isShownLinkDownNoti = false;
                }

                cnt = 60;
            }
        }
    };

    private void callAPI(String msg){

//        String packetMsg = new PacketModel(this).getJSONString();
//        Common.mqttHelper.publicToTopicWithUsername(packetMsg);
    }

    private void sendMqttMsg(HeartBeatModel heartBeatModel){
        if (Common.isInternetAvailable){
            Common.mqttHelper.publicToTopicWithUsername(heartBeatModel.getJSONString());
        }else {
            databaseQueryClass.insertMsg(heartBeatModel);
        }
    }

    private boolean checkGPSIsOpen(){
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null){
            return  false;
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private final BroadcastReceiver bluetoothStateListener = new BroadcastReceiver() {
        @SuppressLint("NewApi")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        isBleEnable = true;
                        bleEnable = "1";
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        showNotification(getString(R.string.turn_on_bluetooth), true);
                        callAPI("Turn on Bluetooth");
                        isBleEnable = false;
                        bleEnable = "0";
                        sendBroadcastMsg(Constant.BLE_OFF);
                        break;
                }
            }
        }
    };

    private BroadcastReceiver gpsStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (isGpsEnabled || isNetworkEnabled) {
                    // Handle Location turned ON
                    isLocationEnable = true;
                    gpsEnable = "1";
                } else {
                    // Handle Location turned OFF
                    isLocationEnable = false;
                    gpsEnable = "0";
                    sendBroadcastMsg(Constant.GPS_OFF);
                    showNotification(getString(R.string.turn_on_location) , true);
                    callAPI("Turn on Location service");
                }
            }
        }
    };

    private void sendBroadcastMsg(String msg){
        Intent intent = new Intent(Constant.BROADCAST_MSG_HOME_FRAGMENT);
        intent.putExtra(Constant.MSG_HOME_FRAGMENT, msg);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer.purge();
        super.onDestroy();
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected){
            LogUtil.e("Internet connected");
            Common.isInternetAvailable = true;
            offlinePacketList.clear();
            offlinePacketList.addAll(databaseQueryClass.getAllMsg());
        }else {
            LogUtil.e("Internet disconnected");
            Common.isInternetAvailable = false;
        }
    }

    private MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) { }

        @Override
        public void connectionLost(Throwable cause) { }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            LogUtil.e("msg arrived --- BleScan" );
            try {
                JSONObject jsonObject = new JSONObject(message.toString());
//                int Common.loc_ack = jsonObject.getInt(Constant.LOC_ACK);

                Common.loc_ack = jsonObject.getInt(Constant.LOC_ACK);
                if (Common.loc_ack == 2){
                    showNotification(getString(R.string.you_are_outside_your_quarantine_area), true);
                    sendBroadcastMsg(Constant.USER_OUT_SIDE);
                }
            }catch (JSONException e){
                LogUtil.e("API Error");
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) { }
    };
}
