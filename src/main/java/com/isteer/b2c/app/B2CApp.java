package com.isteer.b2c.app;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import java.util.Calendar;
import java.util.Date;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.isteer.b2c.config.B2CAppConfig;
import com.isteer.b2c.config.DSRAppConfig;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.preference.B2CPreference;
import com.isteer.b2c.receiver.AlarmReceiver;
import com.isteer.b2c.receiver.B2CConnectivityReceiver;
import com.isteer.b2c.receiver.SEMTVReceiver;
import com.isteer.b2c.room.RoomDB;
import com.isteer.b2c.utility.B2CBasicUtils;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.utility.TypefaceUtil;
import com.isteer.b2c.utility.ValidationUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class B2CApp extends Application implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "B2CApp";

    public static LocationData userLocData = new LocationData();
    public static B2CPreference b2cPreference;
    public static B2CBasicUtils b2cUtils;
    public static ValidationUtils validationUtils;

    public static Context b2cContext;
    private LocationManager mLocManager;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public static boolean isLocationProviderDisabled = true;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    public static Context dsrContext;
    private RoomDB roomDB;
    private static B2CApp INSTANCE;
    private RequestQueue requestQueue;
    private B2CConnectivityReceiver receiver;
    private Location mLocation1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {

        super.onCreate();


        try {
            if (getPackageManager().getPackageInfo(getPackageName(), 0).versionName.contains("L")) {
                Log.e("logic", "Logicbuild");

                Stetho.initializeWithDefaults(this);
            } else {
                Fabric.with(this, new Crashlytics());
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "font/raleway.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        dsrContext = this;
        b2cContext = this;
        b2cPreference = new B2CPreference(this);
        b2cUtils = new B2CBasicUtils(this);
        initRoomDB();
        receiverForOreo();

        Log.e("App", "Resume");

        Log.e("isDaystoppedAuto", "" + B2CApp.b2cPreference.isDaystoppedAuto());
        Log.e("isDayStarted", "" + B2CApp.b2cPreference.isDayStarted());

        if (B2CApp.b2cPreference.isDayStarted()) {
            Logger.LogError("isDaystoppedAuto", "" + B2CApp.b2cPreference.isDaystoppedAuto());
            initDefaultForStopDay();
            createLocationRequest();
            startLocationUpdates();
            startAlarm();

        }
        //Toast.makeText(b2cContext, "isHighAccuracyLocationEnabled : "+b2cUtils.isHighAccuracyLocationEnabled(), Toast.LENGTH_SHORT).show();;

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static B2CApp getINSTANCE() {
        return INSTANCE;
    }

    public RequestQueue getRequestQueue() {

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this/*, new OkHttpStack()*/);
        }
        return requestQueue;

    }

    public void addRequestQueue(JsonObjectRequest jsonObjectRequest) {
        getRequestQueue().add(jsonObjectRequest);
    }

    public RoomDB getRoomDB() {
        return roomDB;
    }

    private void initRoomDB() {
        roomDB = Room.databaseBuilder(getApplicationContext(), RoomDB.class, "amshuhu_b2c_new.db")
                .allowMainThreadQueries()
                .addMigrations(RoomDB.MIGRATION_1_2, RoomDB.MIGRATION_2_3, RoomDB.MIGRATION_3_4,
                        RoomDB.MIGRATION_4_5, RoomDB.MIGRATION_5_6, RoomDB.MIGRATION_6_7, RoomDB.MIGRATION_7_8
                        , RoomDB.MIGRATION_8_9, RoomDB.MIGRATION_9_10)
                .build();
        INSTANCE = this;

    }


    private void initTokenValidator() {
        Log.e("initTokenValidator", "called");

        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this.getApplicationContext(), SEMTVReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime() + 10 * 1000, B2CAppConfig.LOGIN_CHECKUP_TIME, pi);

    }


    public void initAlarmForStartDay() {

        Intent alarmIntent = new Intent(dsrContext, AlarmReceiver.class);
        alarmIntent.setAction("startdayAlarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(dsrContext, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 00);
        time.set(Calendar.MINUTE, 00);
        time.set(Calendar.SECOND, 00);
        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }

    public void initAlarmForStopDay() {


        Intent alarmIntent = new Intent(dsrContext, AlarmReceiver.class);
        alarmIntent.setAction("stopdayAlarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(dsrContext, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 00);
        time.set(Calendar.MINUTE, 00);
        time.set(Calendar.SECOND, 00);
        manager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
    }

    public void initDefaultEightForStopDay() {

        Intent alarmIntent = new Intent(dsrContext, AlarmReceiver.class);
        alarmIntent.setAction("stopdayAlarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(dsrContext, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 23);
        time.set(Calendar.MINUTE, 55);
        time.set(Calendar.SECOND, 00);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
    }

    public void initGoogleAPIClient() {

        Log.e("initGoogleAPIClient", "called");

        if (checkPlayServices()) {

            buildGoogleApiClient();


            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } else
            Log.e("checkPlayServices", "not exists");

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
/*            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }*/
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(B2CAppConfig.LOC_TIME_INTERVAL);
        mLocationRequest.setFastestInterval(B2CAppConfig.LOC_TIME_INTERVAL_FASTEST);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        mLocationRequest.setSmallestDisplacement(B2CAppConfig.LOC_DISPLACEMENT_INTERVAL);

    }

    public void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {


                mLocation1 = location;
                if (mLocation1 != null) {
//                    Log.e("lat", "" + location.getLatitude());
//                    Log.e("lon", "" + location.getLongitude());
                    userLocData.setLatitude(mLocation1.getLatitude());
                    userLocData.setLongitude(mLocation1.getLongitude());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    mLocation1 = location;

                    if (mLocation1 != null) {
//                        Log.e("Location.getLatitude changes B2capp", "" + location.getLatitude());
//                        Log.e("Location.getLongitude changes B2capp", "" + location.getLongitude());
                        userLocData.setLatitude(mLocation1.getLatitude());
                        userLocData.setLongitude(mLocation1.getLongitude());
                    }
                }

            }
        };
        startRequestLocationUpdates();
        Logger.LogError("startLocationUpdates", "startLocationUpdates");

        if (!B2CApp.b2cPreference.isDayStarted()) {
            Logger.LogError("DaystopB2cApp", "" + B2CApp.b2cPreference.isDayStarted());
            stopRemoveLocationUpdates();
        }
    }


    public void stopRemoveLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startRequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.e("onConnectionFailed", "ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        Log.e("onConnected", "called");


    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation1 = location;
        if (mLocation1 != null && mLocation1.getLatitude() != 0 && mLocation1.getLongitude() != 0) {
            Logger.LogError("B2c DaystartB2cApp -> Latitude:Longitude", +mLocation1.getLatitude() + " " + mLocation1.getLongitude());
            //  Logger.LogError("mLocation1.getLatitude()",""+mLocation1.getLatitude());
            //  Logger.LogError("mLocation1.getLongitude()",""+mLocation1.getLongitude());
            userLocData.setLatitude(mLocation1.getLatitude());
            userLocData.setLongitude(mLocation1.getLongitude());

        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {

        Log.e("onConnectionSuspended", "called");

        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //  initLocationListner();
            } else {
                askPermission();
            }
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // initLocationListner();
            } else {
                askPermission();
            }
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions((Activity) dsrContext,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    public void initDefaultForStopDay() {

        Logger.LogError("isDaystoppedAuto", "" + B2CApp.b2cPreference.isDaystoppedAuto());
        if ((!B2CApp.b2cPreference.isDaystoppedAuto()) && B2CApp.b2cPreference.isDayStarted() && B2CApp.b2cPreference.getUserId() != null) {
            B2CApp.b2cPreference.setIsDaystoppedAuto(true);
            Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
            alarmIntent.setAction("defaultStopdayAlarm");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, 23);
            time.set(Calendar.MINUTE, 58);
            time.set(Calendar.SECOND, 00);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
        }
    }

    public void startAlarm() {

        Intent alarmIntent = new Intent(b2cContext, AlarmReceiver.class);
        alarmIntent.setAction("logLocationAlarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(b2cContext, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) b2cContext.getSystemService(Context.ALARM_SERVICE);

        long locationUpdateTime = B2CApp.b2cPreference.getAttTrackTime();
        //long locationUpdateTime = 1;
        long interval = locationUpdateTime == 0 ? DSRAppConfig.DEFAULT_ATTENDENCE_INTERVAL : (1000 * 60 * locationUpdateTime);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime(), interval, pendingIntent);

    }

    public void receiverForOreo() {
        receiver = new B2CConnectivityReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(receiver, filter);
    }
}
