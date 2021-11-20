package tech.hazm.hazmandroid.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.hazm.hazmandroid.Activity.MainActivity;
import tech.hazm.hazmandroid.Base.BaseFragment;
import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Constant.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.hazm.hazmandroid.R;

@SuppressLint("NewApi")
public class HomeFragment extends BaseFragment {

    MainActivity mainActivity;

    @BindView(R.id.imvLinkState) ImageView imvLinkState;
    @BindView(R.id.txvAPI) TextView txvAPI;
    @BindView(R.id.txvStatus) TextView txvStatus;
    @BindView(R.id.txvUserName) TextView txvUserName;

    private boolean isShown = false;
    IntentFilter filter = new IntentFilter();

    private static String receivedMsg = "";

    public HomeFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        filter.addAction(Constant.BROADCAST_MSG_HOME_FRAGMENT);
        filter.setPriority(100);
        mainActivity.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // beacon detected
            String action = intent.getAction();
            if (action.equals(Constant.BROADCAST_MSG_HOME_FRAGMENT)){
                Common.homeMsg = intent.getStringExtra(Constant.MSG_HOME_FRAGMENT);
                refresh(Common.homeMsg);
            }
        }
    };

    private void refresh(String msg){
        if (isShown){
            switch (msg){
                case Constant.LINK_UP:
                    if (Common.loc_ack != 2){
                        imvLinkState.setImageResource(R.drawable.link_up);
                        txvStatus.setText("");
                    }
                    break;
                case Constant.LINK_DOWN:
                    if (Common.loc_ack !=2){
                        imvLinkState.setImageResource(R.drawable.link_down);
                        txvStatus.setText(getString(R.string.module_is_out));
                    }
                    break;
                case Constant.LINK_CORRUPTED:
                    if (Common.loc_ack !=2){
                        imvLinkState.setImageResource(R.drawable.link_corrupted);
                        txvStatus.setText(getString(R.string.module_corrupted));
                    }
                    break;
                case Constant.BLE_OFF:
                    imvLinkState.setImageResource(R.drawable.ble_off);
                    txvStatus.setText(getString(R.string.bluetooth_off));
                    break;
                case Constant.GPS_OFF:
                    imvLinkState.setImageResource(R.drawable.location_off);
                    txvStatus.setText(getString(R.string.gps_off));
                    break;
                case Constant.USER_OUT_SIDE:
                    imvLinkState.setImageResource(R.drawable.user_outside_area);
                    txvStatus.setText(getString(R.string.you_are_outside_your_quarantine_area));
                    break;
                default:
                    txvAPI.setText(msg);
                    break;
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frm  = LayoutInflater.from(mainActivity).inflate(R.layout.frm_home, container, false);
        ButterKnife.bind(this, frm);

        txvUserName.setText(Common.name);

        return frm;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isShown = false;
        mainActivity.unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        isShown = true;
        txvAPI.setText(receivedMsg);
        refresh(Common.homeMsg);
    }

    private boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(mainActivity.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

}
