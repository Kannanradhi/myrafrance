package com.isteer.b2c.receiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConfig;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.AttendenceData;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.utility.Logger;

public class AlarmReceiver extends BroadcastReceiver {

    public boolean isStarted;
    public LocationManager mLocManager;
    private String uriInProgress;
    private Context mContext;
    private Long longattenInsert = Long.valueOf(0);

    @Override
    public void onReceive(Context context, Intent intent) {

        Logger.LogError("getLatitude()OPEN", "" + B2CApp.userLocData.getLatitude());
        Logger.LogError("getLongitude()OPEN", "" + B2CApp.userLocData.getLongitude());
        Logger.LogError("AlarmReceiver", "onReceive");
        Logger.LogError("intent.getAction()", "" + intent.getAction());

        mContext = context;


        //  Logger.LogError("B2CApp.b2cPreference.getBaseUrl()", "" + B2CApp.b2cPreference.getBaseUrl());
        Logger.LogError("B2CApp.b2cPreference.getUserId()", "" + B2CApp.b2cPreference.getUserId());
        Logger.LogError("intent.getAction()", "" + intent.getAction());
        Logger.LogError("B2CApp.b2cPreference.isDayStarted()", "" + B2CApp.b2cPreference.isDayStarted());
        /*else if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(
                "startdayAlarm") && !B2CApp.b2cPreference.isDayStarted() &&
                B2CApp.b2cPreference.getUserId() != null && B2CApp.b2cPreference.getBaseUrl() != null ) {

            Logger.LogError("AlarmReceiver", "startdayAlarm");

            startDay();



        } else if (intent.getAction() != null
                && intent.getAction().equalsIgnoreCase(
                "stopdayAlarm")&& B2CApp.b2cPreference.getBaseUrl() != null
                && B2CApp.b2cPreference.getUserId() != null) {

            Logger.LogError("AlarmReceiver", "stopdayAlarm");
            stopDay();


        }*/
        if (intent.getAction() != null && B2CApp.b2cPreference.isDayStarted()
                && intent.getAction().equalsIgnoreCase("defaultStopdayAlarm") &&
                (B2CApp.b2cPreference.getBaseUrl() != null)
                && B2CApp.b2cPreference.getUserId() != null) {

            Logger.LogError("AlarmReceiver", "defaultStopdayAlarm");
            defaultStopDay();


        } else {

            if (B2CApp.b2cPreference.isDayStarted() && B2CApp.b2cPreference.getUserId() != null) {
                System.out.println("Receiver:forced call");
                String startDate = "" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime());
                LocationData locationData = new LocationData();
                locationData.setUser_key(B2CApp.b2cPreference.getUserId());
                locationData.setDate_time(startDate);
                locationData.setLongitude(B2CApp.userLocData.getLongitude());
                locationData.setLatitude(B2CApp.userLocData.getLatitude());
                locationData.setUpdate_status(DSRAppConstant.KEY_UPDATE_STATUS_PENDING);
                locationData.setBattery_level(getBatteryLevel(context));
                Log.e("getBatteryLevel(context)", "" + getBatteryLevel(context));
                if (B2CApp.userLocData.getLatitude() != 0) {
                    longattenInsert = B2CApp.getINSTANCE().getRoomDB().locationData_dao().insertLocationData(locationData);

                    Logger.LogError("longattenInsert", "" + longattenInsert);
                    if (B2CApp.b2cUtils.isNetAvailable()) {
                        new SyncTaskToServer().execute(B2CApp.b2cPreference.getBaseUrl() + DSRAppConstant.METHOD_UPDATE_LOCATION_LOG);
                    }
                }else {

                    System.out.println("**************location not updated********************");
                }

            } else {
                System.out.println("Receiver :Auto Call");
            }
        }
    }


    class SyncTaskToServer extends AsyncTask<String, String, String> {
        private int operationType;

        protected String doInBackground(String... urls) {
            uriInProgress = urls[0];
            Logger.LogError("postUrl : ", uriInProgress);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uriInProgress);
            operationType = getOperationType(uriInProgress);
            try {
                String jsonString = "";
                try {
                    //    jsonString = (new JSONObject().put(DSRAppConstant.KEY_DATA, getAsyncLocationList())).toString();
                    jsonString = getJsonParams(operationType).toString();
                    System.out.println("jsonString" + jsonString);
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
            } catch (Exception e) {
                B2CApp.getINSTANCE().getRoomDB().attendence_dao().clearTable();
                Logger.LogError("ClientProtocolException : ", e.toString());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);


            JSONObject responseJson;
            responseString = (responseString == null || responseString.trim().equalsIgnoreCase("")) ? "" : responseString;
            /*
             * JSONArray responseArray = new JSONArray(responseString);
             * responseJson = responseArray.getJSONObject(0);
             */

            if (operationType == B2CAppConstant.OPTYPE_UPDATE_LOCATION_LOG) {
                onPostUPDATE_LOCATION_LOG(responseString);

            } else if (operationType == B2CAppConstant.OPTYPE_ATTENDENCE_LOG) {

                onPostATTENDENCE_LOG(responseString);
            }


        }

    }

    private void onPostATTENDENCE_LOG(String outerResponseJson) {
        JSONObject responseJson;
        try {
            responseJson = new JSONObject(outerResponseJson);

            if (responseJson.has(DSRAppConstant.KEY_STATUS) && responseJson.getInt(DSRAppConstant.KEY_STATUS) == 1
                    && responseJson.has(B2CAppConstant.KEY_DATA)) {

                JSONArray keys = responseJson.getJSONArray(B2CAppConstant.KEY_DATA);
                for (int i = 0; i < keys.length(); i++) {

                    JSONObject singleEntry = keys.getJSONObject(i);
                    if (singleEntry.has(DSRAppConstant.KEY_STATUS) && (singleEntry.getInt(DSRAppConstant.KEY_STATUS) == 1)) {
                        if (!(singleEntry.getString(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.stop_time.name()).isEmpty())) {
                            if ((singleEntry.getString(DSRAppConstant.KEY_DUMMY_KEY).isEmpty())) {
                                B2CApp.getINSTANCE().getRoomDB().attendence_dao().delete_attendence_log(singleEntry.getString((DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.att_key.name())));
                            } else {
                                B2CApp.getINSTANCE().getRoomDB().attendence_dao().delete_attendence_log(singleEntry.getString(DSRAppConstant.KEY_DUMMY_KEY));
                            }

                        } else {
                            B2CApp.getINSTANCE().getRoomDB().attendence_dao().updateattendence_log(
                                    singleEntry.getString(DSRAppConstant.KEY_DUMMY_KEY),
                                    singleEntry.getString(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.att_key.name()));

                        }
                    }
                }

            } else {
                Toast.makeText(mContext, "" + responseJson.getString(DSRAppConstant.KEY_MSG), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPostUPDATE_LOCATION_LOG(String responseString) {
        JSONObject responseJson;
        try {
            responseJson = new JSONObject(responseString);

            if (responseJson.has(DSRAppConstant.KEY_STATUS) && responseJson.getInt(DSRAppConstant.KEY_STATUS) == 1) {

                JSONArray keys = responseJson.getJSONArray("data");
                for (int i = 0; i < keys.length(); i++) {
                    JSONObject singleEntry = keys.getJSONObject(i);
                    if (singleEntry.getInt(B2CAppConstant.KEY_STATUS) == 1) {
                        B2CApp.getINSTANCE().getRoomDB().locationData_dao().deleteLocationLogUpdateStatus1(singleEntry.getString((DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.loc_key.name())));
                    }
                    //	DSRApp.dsrLdbs.updateattendence_log(mContext, singleEntry.getString((DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.loc_key.name())));

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonParams(int opType) throws JSONException {

        JSONObject ojson = new JSONObject();


        if (opType == B2CAppConstant.OPTYPE_UPDATE_LOCATION_LOG) {
            JSONArray tJsonArray = new JSONArray();
            ArrayList<LocationData> locationDataArrayList = (ArrayList<LocationData>) B2CApp.getINSTANCE().getRoomDB().locationData_dao().getAsyncronizedLocationList();
            //Cursor mCursor = DSRApp.dsrLdbs.getAsyncronizedLocationList(mContext);
            Logger.LogError("locationDataArrayList", "" + locationDataArrayList.size());
            if (locationDataArrayList.size() <= 0) {
                return ojson;
            }


            for (int i = 0; i < locationDataArrayList.size(); i++) {
                JSONObject json = new JSONObject();
                LocationData locationData = locationDataArrayList.get(i);
                json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.loc_key.name(), locationData.getLoc_key());
                json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.user_key.name(), locationData.getUser_key());
                json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.date_time.name(), locationData.getDate_time());
                json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.longitude.name(), locationData.getLongitude());
                json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.latitude.name(), locationData.getLatitude());
                json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.altitude.name(), locationData.getAltitude());
                json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.battery_level.name(), locationData.getBattery_level());
                tJsonArray.put(json);
                //  System.out.println("json string is" + json.toString());

            }
            ojson.put(B2CAppConstant.KEY_DATA, tJsonArray);

        } else if (opType == B2CAppConstant.OPTYPE_ATTENDENCE_LOG) {
            JSONArray tJsonArray = new JSONArray();
            ArrayList<AttendenceData> attendenceDataList = (ArrayList<AttendenceData>) B2CApp.getINSTANCE().getRoomDB().attendence_dao().getAsyncronizedAttendence();
            if (attendenceDataList.size() > 0) {
                for (int i = 0; i < attendenceDataList.size(); i++) {
                    AttendenceData attendenceData = attendenceDataList.get(i);
                    JSONObject json = new JSONObject();
                    if (attendenceData.getAtt_key() < 0) {

                        json.put(DSRAppConstant.KEY_DUMMY_KEY, attendenceData.getAtt_key());
                        json.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.att_key.name(), "");
                    } else {
                        json.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.att_key.name(), attendenceData.getAtt_key());
                        json.put(DSRAppConstant.KEY_DUMMY_KEY, "");
                    }

                    json.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.user_key.name(), attendenceData.getUser_key());
                    json.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.date.name(), attendenceData.getDate());
                    json.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.start_time.name(), attendenceData.getStart_time());
                    json.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.stop_time.name(), attendenceData.getStop_time());
                    json.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.status.name(), attendenceData.getStatus());
                    json.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.update_status.name(), attendenceData.getUpdate_status());
                    json.put(B2CAppConstant.start_latitude, attendenceData.getStart_latitude());
                    json.put(B2CAppConstant.start_longitude, attendenceData.getStart_longitude());
                    json.put(B2CAppConstant.stop_latitude, attendenceData.getStop_latitude());
                    json.put(B2CAppConstant.stop_longitude, attendenceData.getStop_longitude());
                    System.out.println("json string is" + json.toString());
                    tJsonArray.put(json);
                }
                ojson.put(B2CAppConstant.KEY_DATA, tJsonArray);
            }
        }

        return ojson;

    }


    private int getOperationType(String uri) {
        int optype = B2CAppConstant.OPTYPE_UNKNOWN;

        if (uri.contains(DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG))
            optype = B2CAppConstant.OPTYPE_ATTENDENCE_LOG;
        else if (uri.contains(DSRAppConstant.METHOD_UPDATE_LOCATION_LOG))
            optype = B2CAppConstant.OPTYPE_UPDATE_LOCATION_LOG;
        return optype;
    }

    private JSONArray getAsyncLocationList() throws JSONException {

        JSONArray tJsonArray = new JSONArray();
        ArrayList<LocationData> locationDataArrayList = (ArrayList<LocationData>) B2CApp.getINSTANCE().getRoomDB().locationData_dao().getAsyncronizedLocationList();
        //Cursor mCursor = DSRApp.dsrLdbs.getAsyncronizedLocationList(mContext);
        Logger.LogError("locationDataArrayList", "" + locationDataArrayList.size());
        if (locationDataArrayList.size() <= 0)
            return tJsonArray;


        for (int i = 0; i < locationDataArrayList.size(); i++) {

            LocationData locationData = locationDataArrayList.get(i);
            JSONObject json = new JSONObject();
            json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.loc_key.name(), locationData.getLoc_key());
            json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.user_key.name(), locationData.getUser_key());
            json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.date_time.name(), locationData.getDate_time());
            json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.longitude.name(), locationData.getLongitude());
            json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.latitude.name(), locationData.getLatitude());
            json.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.altitude.name(), locationData.getAltitude());
            System.out.println("json string is" + json.toString());
            tJsonArray.put(json);
        }
        System.out.println("jsonqrray string is" + tJsonArray.toString());
        return tJsonArray;
    }

    private void startDay() {
        startAlarm();
        String start_date = "" + DateFormat.format(B2CAppConstant.dateFormat, new Date().getTime());
        String start_time = "" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime());
        AttendenceData attendenceData = new AttendenceData();
        int newEntryCount = B2CApp.b2cPreference.getNewEntryAttendance() - 1;
        attendenceData.setAtt_key(newEntryCount);
        B2CApp.b2cPreference.setNewEntryAttendance(newEntryCount);
        attendenceData.setUser_key(B2CApp.b2cPreference.getUserId());
        attendenceData.setDate(start_date);
        attendenceData.setStart_time(start_time);
        attendenceData.setStop_time("");
        attendenceData.setStatus(DSRAppConstant.KEY_STATUS_STARTED);
        attendenceData.setUpdate_status(DSRAppConstant.KEY_UPDATE_STATUS_PENDING);
        Logger.LogError("getLatitude()START", "" + B2CApp.userLocData.getLatitude());
        Logger.LogError("getLongitude()START", "" + B2CApp.userLocData.getLongitude());
        attendenceData.setStart_latitude(B2CApp.userLocData.getLatitude());
        attendenceData.setStart_longitude(B2CApp.userLocData.getLongitude());

        long longinsertatten = B2CApp.getINSTANCE().getRoomDB().attendence_dao().insertAttendence(attendenceData);
        Logger.LogError("longinsertattenstart", "" + longinsertatten);
        //	 long tKey = DSRApp.dsrLdbs.insertAttendenceLog(mContext,B2CApp.b2cPreference.getUserId());
        if (longinsertatten < 0) {

            if (B2CApp.b2cUtils.isNetAvailable()) {
                new SyncTaskToServer().execute(B2CApp.b2cPreference.getBaseUrl() + DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG);
            }
            B2CApp.b2cPreference.setStartDateKey(longinsertatten);
            B2CApp.b2cPreference.setIsDayStarted(true);
            Toast.makeText(mContext, "Day Started", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "Day Not Started Try After Some Time", Toast.LENGTH_LONG).show();
        }
    }

    private void stopDay() {
        Logger.LogError("getLatitude()STOP", "" + B2CApp.userLocData.getLatitude());
        Logger.LogError("getLongitude()STOP", "" + B2CApp.userLocData.getLongitude());
        String start_date = "" + DateFormat.format(B2CAppConstant.dateFormat, new Date().getTime());
        String stop_time = "" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime());
        double stop_latitude = (B2CApp.userLocData.getLatitude());
        double stop_longitude = (B2CApp.userLocData.getLongitude());
        long updateAttenStopTime = B2CApp.getINSTANCE().getRoomDB().attendence_dao().updateAttendenceLog(stop_time, stop_latitude, stop_longitude);
        Logger.LogError("longinsertattenstop", "" + updateAttenStopTime);
        // long tKey = DSRApp.dsrLdbs.updateAttendenceLog(mContext, B2CApp.b2cPreference.getStartDateKey());
        if (updateAttenStopTime > 0) {
            if (B2CApp.b2cUtils.isNetAvailable()) {
                new SyncTaskToServer().execute(B2CApp.b2cPreference.getBaseUrl() + DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG);
            }
            B2CApp.b2cPreference.setIsDayStarted(false);
            Toast.makeText(mContext, "Day stopped", Toast.LENGTH_LONG).show();
            Logger.LogError("AlarmReceiver", "Day stopped");

        } else {
            B2CApp.b2cPreference.setIsDayStarted(false);
            Logger.LogError("AlarmReceiver", "Day Not stopped");
        }


    }

    public void startAlarm() {

        Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
        alarmIntent.setAction("logLocationAlarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        long locationUpdateTime = B2CApp.b2cPreference.getAttTrackTime();
        long interval = locationUpdateTime == 0 ? DSRAppConfig.DEFAULT_ATTENDENCE_INTERVAL : (1000 * 60);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime() + 5000, 30 * 1000, pendingIntent);

    }

    public int getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);


        return level;
    }

    public void initDefaultForStopDay() {

        Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
        alarmIntent.setAction("defaultStopdayAlarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 13);
        time.set(Calendar.MINUTE, 33);
        time.set(Calendar.SECOND, 00);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), 24 * 60 * 60 * 1000, pendingIntent);
    }

    private void defaultStopDay() {
        String start_day = "" + DateFormat.format(B2CAppConstant.dateFormat, new Date());
        Logger.LogError("getLatitude()STOP", "" + B2CApp.userLocData.getLatitude());
        Logger.LogError("getLongitude()STOP", "" + B2CApp.userLocData.getLongitude());
        Logger.LogError("start_day2", "" + start_day);
        String stop_time = start_day + " 00:00:00";
        double stop_latitude = (B2CApp.userLocData.getLatitude());
        double stop_longitude = (B2CApp.userLocData.getLongitude());
        long updateAttenStopTime = B2CApp.getINSTANCE().getRoomDB().attendence_dao().updateAttendenceLog(stop_time, stop_latitude, stop_longitude);
        Logger.LogError("longinsertattenstop", "" + updateAttenStopTime);
        // long tKey = DSRApp.dsrLdbs.updateAttendenceLog(mContext, B2CApp.b2cPreference.getStartDateKey());
        if (updateAttenStopTime > 0) {

            B2CApp.b2cPreference.setIsDayStarted(false);
            Toast.makeText(mContext, "Day stopped", Toast.LENGTH_LONG).show();
            Logger.LogError("auto", "Day stopped");

        } else {
            B2CApp.b2cPreference.setIsDayStarted(false);
            Logger.LogError("auto", "Day Not stopped");
        }

        B2CApp.getINSTANCE().stopRemoveLocationUpdates();

    }
}
