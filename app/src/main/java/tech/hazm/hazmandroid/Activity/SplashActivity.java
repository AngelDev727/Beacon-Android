package tech.hazm.hazmandroid.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.iamhabib.easy_preference.EasyPreference;
import tech.hazm.hazmandroid.Base.BaseActivity;
import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Constant.Constant;
import tech.hazm.hazmandroid.Constant.PrefConst;
import tech.hazm.hazmandroid.Model.UserModel;
import tech.hazm.hazmandroid.R;
import tech.hazm.hazmandroid.Service.GpsService;
import tech.hazm.hazmandroid.Utils.LogUtil;
import tech.hazm.hazmandroid.Utils.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SplashActivity extends BaseActivity {

    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private final int PERMISSION_REQ_CODE = 102;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Common.name = EasyPreference.with(this).getString(PrefConst.NAME, "");
        Common.uuid = EasyPreference.with(this).getString(PrefConst.UUID, "");
        Common.userModel = new UserModel(
                EasyPreference.with(this).getString(PrefConst.USER_NAME,""),
                EasyPreference.with(this).getString(PrefConst.PHONE,""),
                EasyPreference.with(this).getString(PrefConst.PWD, "")
        );

        if (Common.userModel.userName.length() != 0 ){

            // wait for mqtt connection
            showProgress(getString(R.string.mqtt_connecting));

            MqttHelper.username = Common.userModel.userName;
            MqttHelper.password = Common.userModel.pwd;

            Common.mqttHelper = new MqttHelper(this);
            Common.mqttHelper.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    LogUtil.e("Mqtt connected ----- SplashActivity");
                    closeProgress();

                    MqttHelper.subscriptionTopicWithUsername = Constant.SUBSCRIBE_BASE_API + Common.userModel.userName;
                    MqttHelper.publishTopicWithUsername = Constant.PUBLIC_BASE_API + Common.userModel.userName;

                    Common.mqttHelper.subscribeToTopicWithUsername();

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    LogUtil.e("Msg arrived ===  Splash Activity");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

        }else {
            startActivity(new Intent(SplashActivity.this, UserInfoActivity.class));
            finish();
        }

        checkPermissions();
    }

    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){
//            gotoLoginActivity();
        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) { }
//        gotoLoginActivity();
    }
}
