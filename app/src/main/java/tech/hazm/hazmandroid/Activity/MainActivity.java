package tech.hazm.hazmandroid.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import tech.hazm.hazmandroid.Base.BaseActivity;
import tech.hazm.hazmandroid.Fragment.HelpFragment;
import tech.hazm.hazmandroid.Fragment.HomeFragment;
import tech.hazm.hazmandroid.Fragment.InfoFragment;

import tech.hazm.hazmandroid.R;
import tech.hazm.hazmandroid.Service.BleScan;
import tech.hazm.hazmandroid.Service.GpsService;
import tech.hazm.hazmandroid.Utils.LocationRequestHelper;
import tech.hazm.hazmandroid.Utils.LogUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.location.Location;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest.Builder;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends BaseActivity implements  BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.bottomBar) BottomNavigationView bottomBar;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private BluetoothAdapter btAdapter;
    private BluetoothManager btManager;
    LocationRequest mLocationRequest;
    private static final long FASTEST_UPDATE_INTERVAL = 5000;

    private static final long MAX_WAIT_TIME = 30000;
    private static final long UPDATE_INTERVAL = 10000;

    Context mContext;
    private FusedLocationProviderClient mFusedLocationClient;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.mLocationRequest = new LocationRequest();
        this.mContext = this;

        settingBlueTooth();

        loadLayout();

        Intent gpsService = new Intent(this, GpsService.class);
        startService(gpsService);

        if (Build.VERSION.SDK_INT >= 23) {
            checkLocationPermission();
        } else {
            checkLocationEnabled();
        }
    }

    private void checkLocationEnabled() {
        try {
            LocationRequest create = LocationRequest.create();
            this.mLocationRequest = create;
            create.setInterval(UPDATE_INTERVAL);
            this.mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
            this.mLocationRequest.setPriority(100);
            this.mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
            Builder addLocationRequest = new Builder().addLocationRequest(this.mLocationRequest);
            addLocationRequest.setAlwaysShow(true);
            addLocationRequest.setNeedBle(true);
            Task checkLocationSettings = LocationServices.getSettingsClient(this.mContext).checkLocationSettings(addLocationRequest.build());
            checkLocationSettings.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    MainActivity.this.getCurrentLocation();
                }
            });
            checkLocationSettings.addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception exc) {
                    ((ApiException) exc).getStatusCode();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLayout() {
        bottomBar.setOnNavigationItemSelectedListener(this);
        showFragment(new HomeFragment(this));
        checkLocationPermission();
    }

    public void getCurrentLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
                LocationRequestHelper.setRequesting(this, true);
                this.mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String str = "Mock Location Detected";
                            String str2 = "Mock Location not Detected";
                            if (VERSION.SDK_INT < 18) {
                                LogUtil.e("GET______MOCK____1");
                                if (isMockLocationEnabled(MainActivity.this.mContext).booleanValue()) {
                                    LogUtil.e("GET______MOCK____2");
                                    return;
                                }
                                LogUtil.e("GET______MOCK____3");
                            } else if (MainActivity.isMockLocation(location)) {
                                LogUtil.e("GET______MOCK____4");
                            } else {
                                LogUtil.e("GET______MOCK____5");
                            }
                        } else {
                            LogUtil.e("GET_____MOCK____LOCATION____6");
                            MainActivity.this.getCurrentLocation();
                        }
                    }
                });
            }
        } catch (Exception e) {
            LocationRequestHelper.setRequesting(this, false);
            e.printStackTrace();
        }
    }

    public static Boolean isMockLocationEnabled(Context context) {
        return Boolean.valueOf(!Secure.getString(context.getContentResolver(), "mock_location").equals("0"));
    }

    public static boolean isMockLocation(Location location) {
        return VERSION.SDK_INT >= 18 && location != null && location.isFromMockProvider();
    }


    @SuppressLint("NewApi")
    private void settingBlueTooth() {
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            LogUtil.e("permission granted");
            startService();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (!isBlueToothOn()) {
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, 1);
                        }
                    }

                    startService();
                }
            }
        }
    }

    private boolean isBlueToothOn() {
        return btAdapter != null && btAdapter.isEnabled();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            startService();
        }
    }

    private void startService(){
        Intent intent = new Intent(this, BleScan.class);
        startService(intent);
    }

    private void showFragment(Fragment frm){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frmContainer, frm).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuHome:
                showFragment(new HomeFragment(this));
                break;
            case R.id.menuInfo:
                showFragment(new InfoFragment(this));
                break;
            case R.id.menuHelp:
                showFragment(new HelpFragment(this));
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
