package com.isteer.b2c.activity.counter_details;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.Logger;

public class B2CLocateScreen extends AppCompatActivity implements OnMapClickListener,
        OnMapLongClickListener, OnMarkerDragListener, OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static boolean toRefresh = false;
    private static ProgressDialog pdialog;
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    final int RQS_GooglePlayServices = 1;
    protected LocationManager locationManager;
    Location location; // location
    double latitude; // latitude
    double longitude;
    //private GoogleMap mGoogleMap;
    private Location myLocation;
    private PolygonOptions polygonOptions;
    private Polygon polygon;
    private ImageView btn_header_right;
    private Marker marker;
    private boolean markerClicked;
    private TextView header_title;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latLng;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scr_b2c_locate);
        initVar();
       /* if (checkGooglePlayServices()) {
            buildGoogleApiClient();

            //prepare connection request

        }*/
        createLocationRequest();
        startLocationUpdates();

    }

    @Override
    protected void onStart() {
        super.onStart();



        /*if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null) {
            stopRemoveLocationUpdates();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();


        B2CApp.b2cUtils.ShowToast(this, "Longclick on map to add a marker");

        //  setinitLocation();

    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);
        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Location");

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);

        ((Button) findViewById(R.id.btnChangeLoc)).setOnClickListener(this);

        //FragmentManager myFragmentManager = getFragmentManager();
        //MapFragment mapFragment = (MapFragment) myFragmentManager.findFragmentById(R.id.mapview);
        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapview));
        mapFragment.getMapAsync(this);

        //mGoogleMap = myMapFragment.getMap();


        markerClicked = false;
    }

    private boolean checkGooglePlayServices() {

        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            /*
             * google play services is missing or update is required
             *  return code could be
             * SUCCESS,
             * SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
             * SERVICE_DISABLED, SERVICE_INVALID.
             */
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RECOVER_PLAY_SERVICES) {

            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Google Play Services must be installed.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(20000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapClick(LatLng point) {
        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 20));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        markerClicked = false;
    }

    @Override
    public void onMapLongClick(LatLng point) {

        Logger.LogError("onMapLongClick", point.toString());
        mGoogleMap.clear();
        marker = mGoogleMap.addMarker(new MarkerOptions().position(point).draggable(true));

        markerClicked = false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker cMarker) {

        if (cMarker != null && cMarker.getPosition() != null) {
            B2CCounterDetailScreen.currentCustomerLoc.setLatitude(cMarker.getPosition().latitude);
            B2CCounterDetailScreen.currentCustomerLoc.setLongitude(cMarker.getPosition().longitude);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnMarkerDragListener(this);


        setinitLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Logger.LogError("Update -> Latitude:Longitude locate", +mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
       /* if (mLastLocation != null) {
            if (marker != null)
            marker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        marker = mGoogleMap.addMarker(markerOptions);*/


    }

    public void setinitLocation() {
        Logger.LogError("locationPointstart", "" + B2CCounterDetailScreen.currentCustomerLoc.getLatitude());
        Logger.LogError("locationPointstart", "" + B2CCounterDetailScreen.currentCustomerLoc.getLongitude());
        //  Logger.LogError("mLastLocation11", "" + mLastLocation.getLongitude());
        //  Logger.LogError("mLastLocation22", "" + mLastLocation.getLongitude());

       /* if (mLastLocation != null) {
            Logger.LogError("Latitude:Longitude locate", +mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
            //place marker at current position
            mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            *//*MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            marker = mGoogleMap.addMarker(markerOptions);*//*
        }*/


        try {
            //mGoogleMap.clear();
            if (B2CCounterDetailScreen.currentCustomerLoc.getLatitude() != 0 && B2CCounterDetailScreen.currentCustomerLoc.getLongitude() != 0) {

                final LatLng locationPoint = new LatLng(B2CCounterDetailScreen.currentCustomerLoc.getLatitude(), B2CCounterDetailScreen.currentCustomerLoc.getLongitude());
                Logger.LogError("locationPoint11", "" + locationPoint);
                mGoogleMap.addMarker(new MarkerOptions().position(locationPoint).draggable(true));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 20));
            } else if (mLastLocation != null && mLastLocation.getLatitude() != 0 && mLastLocation.getLongitude() != 0) {
                final LatLng locationPoint = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                Logger.LogError("locationPoint122", "" + locationPoint);
                mGoogleMap.addMarker(new MarkerOptions().position(locationPoint).draggable(true));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 20));
            } else {
                Toast.makeText(this, "Long press on map to add a marker", Toast.LENGTH_SHORT).show();
                // alertUserP(this, "Alert", "Longclick on map to add a marker", "OK", "longclick");

            }
        } catch (Exception e) {

            Toast.makeText(this, "Long press on map to add a marker", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {


                mLastLocation = location;
                //   Log.e("Location.getLatitude start locate", "" + mLastLocation.getLatitude());
                //  Log.e("Location.getLongitude start locate", "" + mLastLocation.getLongitude());
                setinitLocation();
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
                    mLastLocation = location;
                    //  Log.e("Location.getLatitude changes locate", "" + mLastLocation.getLatitude());
                    //  Log.e("Location.getLongitude changes locate", "" + mLastLocation.getLongitude());

                }

            }
        };

        startRequestLocationUpdates();
        Logger.LogError("startLocationUpdates", "startLocationUpdates");

    }

    protected void stopLocationUpdates() {
        Logger.LogError("stopLocationUpdates", "stopLocationUpdates");
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public void stopRemoveLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void startRequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
    }

    private void updateLoc() {
        if (mLastLocation != null) {
            if (marker != null && marker.getPosition() != null) {
                Logger.LogError("marker.getPosition().latitude", "" + marker.getPosition().latitude);
                Logger.LogError("marker.getPosition().longitude", "" + marker.getPosition().longitude);
                setAlertForMarkerLocation();
            } else {
                if (mLastLocation.getLatitude() != 0 && mLastLocation.getLongitude() != 0) {
                    Logger.LogError("mLastLocation.getLatitude()", "" + mLastLocation.getLatitude());
                    Logger.LogError("mLastLocation.getLongitude()", "" + mLastLocation.getLongitude());
                    setAlertForCurrentLocation();

                } else
                    Toast.makeText(this, "Please Long press to add a customer location", Toast.LENGTH_SHORT).show();
                //   alertUserP(B2CLocateScreen.this, "Alert !", "Please Long click to add a customer location", "OK", "Error");
            }
        } else {
            if (B2CApp.b2cUtils.isGPSEnabled(this))
                startLocationUpdates();
            if (mLastLocation == null) {
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            } else {
                alertGPSSwitch();
            }
        }
    }

    public void setAlertForMarkerLocation() {

        String title = "Alert !";
        String message = "Are you sure change Marker Location as Customer location";
        String left_btn = "Cancel";
        String right_btn = "Ok";
        AlertPopupDialog alertPopupDialog = new AlertPopupDialog(this, title, message, left_btn, right_btn,
                new AlertPopupDialog.myOnClickListenerLeft() {
                    @Override
                    public void onButtonClickLeft() {

                        AlertPopupDialog.dialogDismiss();


                    }
                }, new AlertPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight() {
                setCusLocation(marker.getPosition().latitude, marker.getPosition().longitude);
                AlertPopupDialog.dialogDismiss();


            }
        });
    }

    public void setAlertForCurrentLocation() {

        String title = "Alert !";
        String message = "Are you sure change Current Location as Customer location";
        String left_btn = "Cancel";
        String right_btn = "Ok";
        AlertPopupDialog alertPopupDialog = new AlertPopupDialog(this, title, message, left_btn, right_btn,
                new AlertPopupDialog.myOnClickListenerLeft() {
                    @Override
                    public void onButtonClickLeft() {

                        AlertPopupDialog.dialogDismiss();


                    }
                }, new AlertPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight() {
                if (mLastLocation != null) {
                    setCusLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                } else {
                    setCusLocation(B2CApp.userLocData.getLatitude(), B2CApp.userLocData.getLongitude());
                }
                AlertPopupDialog.dialogDismiss();


            }
        });
    }

    public void setCusLocation(double latitude, double longitude) {
        LocationData loc = new LocationData();
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        loc.setAltitude(0.0);
        Logger.LogError("B2CCounterDetailScreen.currentContactIndKey", "" + B2CCounterDetailScreen.currentContactIndKey);
        Logger.LogError("B2CCounterDetailScreen.currentCustomer.getCmkey()", "" + B2CCounterDetailScreen.currentCustomer.getCmkey());
			/*int intupdateCusLoc = B2CApp.getINSTANCE().getRoomDB().customerindividual_dao().updateCustomerLocation("" + B2CCounterDetailScreen.currentContactIndKey,
					"" + B2CCounterDetailScreen.currentCustomer.getCmkey(), loc.getLatitude(),
					loc.getLongitude());*/

        Logger.LogError("loc.getLatitude()insert", "" + loc.getLatitude());
        Logger.LogError("loc.getLongitude()insert", "" + loc.getLongitude());
        int intupdateCusLoc = B2CApp.getINSTANCE().getRoomDB().customerData_dao().updateCustomerLocation(
                "" + B2CCounterDetailScreen.currentCustomer.getCmkey(), loc.getLatitude(),
                loc.getLongitude());
			/*if(B2CApp.b2cLdbs.updateCustomerLocation(this, ""+B2CCounterDetailScreen.currentContactIndKey, ""+B2CCounterDetailScreen.currentCustomer.getCmkey(),
					loc))*/
        Logger.LogError("intupdateCusLoc", "" + intupdateCusLoc);
        if (intupdateCusLoc > 0) {
            B2CCounterDetailScreen.isLocationsUpdated = true;

            B2CCounterDetailScreen.currentCustomerLoc.setLatitude(loc.getLatitude());
            B2CCounterDetailScreen.currentCustomerLoc.setLongitude(loc.getLongitude());

            if (B2CApp.b2cUtils.isNetAvailable()) {
                new PostRequestManager().execute(B2CApp.b2cPreference.getBaseUrl2() + DSRAppConstant.METHOD_UPDATE_CUST_LOC);
            } else {
                //B2CApp.b2cPreference.setIsUpdatedlocation
                Toast.makeText(this, "Saved locally", Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(this, "Location update failed in a timely manner", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View pView) {

        switch (pView.getId()) {
            case R.id.img_back:
                goBack();
                break;
            case R.id.img_home:
                gotoB2CMenuScreen();
                break;
            case R.id.btn_header_right:
                gotoB2CProductsCatalogue();
                break;

            case R.id.btnChangeLoc:
                changeCustomerLocation();
                break;

        }
    }

    private void changeCustomerLocation() {
        String message = this.getResources().getString(R.string.internetForlocation);
        if (B2CApp.b2cUtils.isNetAvailable() && (mLastLocation != null)) {
            AlertPopupDialog.dialogDismiss();
            updateLoc();
        } else if (!B2CApp.b2cUtils.isNetAvailable()) {
            AlertPopupDialog.noInternetAlert(this, message, new AlertPopupDialog.myOnClickListenerRight() {
                @Override
                public void onButtonClickRight() {
                    if (B2CApp.b2cUtils.isNetAvailable()) {
                        AlertPopupDialog.dialogDismiss();
                        updateLoc();
                    }
                }
            });
        } else if (B2CApp.b2cUtils.isGPSEnabled(this)) {
            startLocationUpdates();
        } else if (!B2CApp.b2cUtils.isGPSEnabled(this)) {
            alertGPSSwitch();
        }
    }

    @Override
    public void onBackPressed() {

        goBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            goBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBack() {
        gotoB2CCounterDetailScreen();
    }

    private void gotoGPSSwitch() {
        Intent intent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void switchGPS(boolean switch_on) {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");

        if (switch_on && provider.contains("gps")) {
            intent.putExtra("enabled", true);
        } else {
            intent.putExtra("enabled", false);
        }

        sendBroadcast(intent);
    }

    private void updateProgress(String message) {
        if (pdialog != null && pdialog.isShowing())
            pdialog.setMessage(message);
        else
            pdialog = ProgressDialog.show(this, "", message, true);
    }

    private void dismissProgress() {
        if (pdialog != null && pdialog.isShowing())
            pdialog.dismiss();
    }

    public void alertUserP(Context context, String title, String msg, String btn, final String btnlistener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setTitle(title).setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (btnlistener.equalsIgnoreCase("longclick")) {
                            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                            LatLng locationPoint = new LatLng(latitude, longitude);
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationPoint, 20));
                        } else if (btnlistener.equalsIgnoreCase("success")) {
                            gotoB2CCounterDetailScreen();
                        }
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void gotoB2CProductsCatalogue() {
        finish();
        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_LOCATE;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCounterDetailScreen() {
        finish();
		/*startActivity(new Intent(this, B2CCounterDetailScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));*/
    }

    private void gotoB2CMenuScreen() {
        finish();
        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    class PostRequestManager extends AsyncTask<String, String, String> {

        //private int operationType;
        private String uriInProgress;

        protected String doInBackground(String... urls) {

            uriInProgress = urls[0];
            Logger.LogError("postUrl : ", uriInProgress);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uriInProgress);
            try {

                //operationType = getOperationType(uriInProgress);

                String jsonString = "";

                try {

                    JSONObject tJson = new JSONObject();
                    tJson.put(B2CAppConstant.KEY_CMKEY, "" + B2CCounterDetailScreen.currentCustomer.getCmkey());
                    //tJson.put(B2CAppConstant.KEY_CON_KEY, "" + B2CCounterDetailScreen.currentContactIndKey);

                    if (B2CCounterDetailScreen.currentCustomerLoc.getLatitude() != 0 && B2CCounterDetailScreen.currentCustomerLoc.getLongitude() != 0) {
                        Logger.LogError(".currentCustomerLoc.getLatitude()", "" + B2CCounterDetailScreen.currentCustomerLoc.getLatitude());
                        Logger.LogError(".currentCustomerLoc.getLongitude()", "" + B2CCounterDetailScreen.currentCustomerLoc.getLongitude());
                        tJson.put(B2CAppConstant.KEY_LATITUDE, "" + B2CCounterDetailScreen.currentCustomerLoc.getLatitude());
                        tJson.put(B2CAppConstant.KEY_LONGITUDE, "" + B2CCounterDetailScreen.currentCustomerLoc.getLongitude());
                        tJson.put(B2CAppConstant.KEY_ALTITUDE, "" + 0.0);
                    }

                    jsonString = tJson.toString();
                    Logger.LogError("jsonString : ", jsonString);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Logger.LogError("JSONException : ", e.toString());
                }

                StringEntity se = new StringEntity(jsonString);
                httppost.setEntity(se);
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");

                ResponseHandler<String> responseHandler = new BasicResponseHandler();

                return httpclient.execute(httppost, responseHandler);

            } catch (ClientProtocolException e) {
                Logger.LogError("ClientProtocolException : ", e.toString());
            } catch (IOException e) {
                Logger.LogError("IOException : ", e.toString());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

            B2CApp.b2cUtils.updateMaterialProgress(B2CLocateScreen.this, "Updating...");
        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);

            B2CApp.b2cUtils.dismissMaterialProgress();

            if (responseString == null) {
                alertUserP(B2CLocateScreen.this, "Failed", "Location update failed in a timely manner", "OK", "Failed");
                return;
            }

            try {

                JSONObject responseJson = new JSONObject(responseString);

                if (responseJson.has(B2CAppConstant.KEY_STATUS) && responseJson.getInt(B2CAppConstant.KEY_STATUS) == 1) {

                    alertUserP(B2CLocateScreen.this, "Success", "Location update successfully", "OK", "Success");
                    B2CApp.getINSTANCE().getRoomDB().customerData_dao().updateCustomerIndSynctoserver("" + B2CCounterDetailScreen.currentCustomer.getCmkey());
                    return;
                } else {
                    alertUserP(B2CLocateScreen.this, "Alert !", responseJson.getString(B2CAppConstant.KEY_MSG),
                            "Ok", "failed");
                    return;
                }

            } catch (JSONException e) {

                e.printStackTrace();
                Logger.LogError("JSONException : ", e.toString());
            }

            alertUserP(B2CLocateScreen.this, "Failed", "Location update failed in a timely manner", "OK", "updateFailed");

        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    public void alertGPSSwitch() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Show location settings?").setTitle("GPS is disabled").setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            gotoGPSSwitch();
                        }
                    });
            alertDialog = builder.create();
        }

        if (alertDialog != null && !alertDialog.isShowing())
            alertDialog.show();
    }

}