package tech.hazm.hazmandroid.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.iamhabib.easy_preference.EasyPreference;
import tech.hazm.hazmandroid.Base.BaseActivity;
import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Constant.Constant;
import tech.hazm.hazmandroid.Constant.PrefConst;
import tech.hazm.hazmandroid.Model.FcmTokenPacket;
import tech.hazm.hazmandroid.Model.UserModel;

import tech.hazm.hazmandroid.R;
import tech.hazm.hazmandroid.Utils.LogUtil;
import tech.hazm.hazmandroid.Utils.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.edtUserName) EditText edtUserName;
    @BindView(R.id.edtPhone) EditText edtPhone;
    @BindView(R.id.edtPwd) EditText edtPwd;

    String mqttMsg = "", token;
    boolean isAcknowledgementRequested = false;

    IntentFilter filter = new IntentFilter();
    final int REQUEST_CHECK_SETTINGS = 1001;
    public static LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ButterKnife.bind(this);

        filter.addAction(Constant.MQTT_CONNECT_FAILURE);
        filter.setPriority(100);
        registerReceiver(receiver, filter);



        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            LogUtil.e("getInstanceId failed === " + task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
                        LogUtil.e("msg ==== " + token);

//                        showToast(token);
                    }
                });

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(UserInfoActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }, 2000);



    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showCredentialErrorMsg();
        }
    };

    @OnClick(R.id.btnSubmit) void clickedSubmit(){

        mqttMsg = "";

        initBottomSheet();

        if (isValid()){

            isAcknowledgementRequested = true;

            MqttHelper.username = edtUserName.getText().toString().trim();
            MqttHelper.password = edtPwd.getText().toString().trim();

            Common.mqttHelper = new MqttHelper(this);
            Common.mqttHelper.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    LogUtil.e("Connect completed --- UserInfoActivity");

                    MqttHelper.subscriptionTopicWithUsername = Constant.SUBSCRIBE_BASE_API + edtUserName.getText().toString().trim();
                    MqttHelper.publishTopicWithUsername = Constant.PUBLIC_BASE_API + edtUserName.getText().toString().trim();

                    Common.mqttHelper.subscribeToTopicWithUsername();

                    UserModel model = new UserModel(
                            edtUserName.getText().toString().trim(),
                            edtPhone.getText().toString().trim(),
                            edtPwd.getText().toString().trim());

                    Common.mqttHelper.publicToTopicWithUsername(model.getJSONString());
                }

                @Override
                public void connectionLost(Throwable cause) { }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    LogUtil.e("Msg arrived ===  User Info Activity");

                    try {
                        JSONObject jsonObject = new JSONObject(message.toString());
                        if (jsonObject.getInt(Constant.ACK) == 1){
                            Common.uuid = jsonObject.getString(Constant.UUID);
                            Common.name = jsonObject.getString(Constant.NAME);
                            Common.userModel = new UserModel(
                                    edtUserName.getText().toString().trim(),
                                    edtPhone.getText().toString().trim(),
                                    edtPwd.getText().toString().trim());

                            FcmTokenPacket packet = new FcmTokenPacket(Common.userModel.userName, token);
                            Common.mqttHelper.publicToTopicWithUsername(packet.getJSONString());

                            int locAcc = jsonObject.getInt(Constant.LOC_ACC);
                            switch (locAcc){
                                case 1:
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                    break;
                                case 2:

                                    break;
                                case 3:

                                    break;
                            }

                            gotoMainActivity();
                        }else {
                            mqttMsg = jsonObject.getString(Constant.MSG);
                            showWarringMsg();
                        }
                    }catch (JSONException e){
                        showAlertDialog(getString(R.string.activation_failed) , mqttMsg.toString());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) { }
            });

            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.show();
        }
    }

    BottomSheetDialog bottomSheetDialog = null;
    Button btnCancel;
    LinearLayout lytWaiting, lytSuccess, lytWarring;
    TextView txvMsg;

    private void initBottomSheet(){

        bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheet = getLayoutInflater().inflate(R.layout.bottom_sheet_wait_for_acknowledgement,null);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.setCancelable(false);

        lytWaiting = (LinearLayout)bottomSheetDialog.findViewById(R.id.lytWaiting);
        lytSuccess = (LinearLayout)bottomSheetDialog.findViewById(R.id.lytSuccess);
        lytWarring = (LinearLayout)bottomSheetDialog.findViewById(R.id.lytWarring);
        txvMsg = (TextView)bottomSheetDialog.findViewById(R.id.txvMsg);

        btnCancel = (Button)bottomSheetDialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void gotoMainActivity() {
        lytWaiting.setVisibility(View.INVISIBLE);
        lytSuccess.setVisibility(View.VISIBLE);
        lytWarring.setVisibility(View.INVISIBLE);

        btnCancel.setVisibility(View.INVISIBLE);

        // save user info in preference
        EasyPreference.with(this).addString(PrefConst.NAME, Common.name).save();
        EasyPreference.with(this).addString(PrefConst.UUID, Common.uuid).save();
        EasyPreference.with(this).addString(PrefConst.USER_NAME, Common.userModel.userName).save();
        EasyPreference.with(this).addString(PrefConst.PHONE, Common.userModel.phoneNumber).save();
        EasyPreference.with(this).addString(PrefConst.PWD, Common.userModel.pwd).save();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);
    }

    private void showWarringMsg() {
        lytWaiting.setVisibility(View.INVISIBLE);
        lytSuccess.setVisibility(View.INVISIBLE);
        lytWarring.setVisibility(View.VISIBLE);
        txvMsg.setText(mqttMsg);
    }

    private void showCredentialErrorMsg(){
        lytWaiting.setVisibility(View.INVISIBLE);
        lytSuccess.setVisibility(View.INVISIBLE);
        lytWarring.setVisibility(View.VISIBLE);
        txvMsg.setText("Wrong Credential Information");
    }

    private boolean isValid() {

        if (edtUserName.getText().toString().trim().length() == 0 ){
            showToast(getString(R.string.input_user_name));
            return false;
        }

        if (edtPhone.getText().toString().trim().length() == 0) {
            showToast(getString(R.string.input_phone));
            return false;
        }

        if (edtPwd.getText().toString().trim().length() == 0 ){
            showToast(getString(R.string.input_pwd));
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}