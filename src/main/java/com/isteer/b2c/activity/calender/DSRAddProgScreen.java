package com.isteer.b2c.activity.calender;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.DSRDayScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.picker.StringPickerDialog;
import com.isteer.b2c.utility.B2CBasicUtils;
import com.isteer.b2c.R;
import com.isteer.b2c.utility.Logger;

public class DSRAddProgScreen extends AppCompatActivity implements OnClickListener, StringPickerDialog.OnClickListener, StringPickerDialog.OnDismissListener, LocationListener {

    public static String eventKey = null;

    private LinearLayout wrapper_edit;
    private View modify_programs_scroll;

    public static boolean skipForNow = false;
    public static boolean isShowingView = true;

    public static boolean isListLogin = true;
    public static boolean isListInited = true;

    public static boolean toRefresh = false;
    public static boolean toUpdateCustomerName = false;
    public static boolean toCloneFromExisting = false;
    public static boolean backToDayScreen = true;

    private B2CBasicUtils isdUtils;

    public LocationManager mLocManager;
    public static LocationData locData = new LocationData();

    private static int currentStatus = 0;

    private Cursor syncCursor;
    private Cursor syncCursorCount;
    private Cursor syncJsonString;

    private String TAG = "ISRAddProgScreen";

    private static ProgressDialog pdialog;
    private static String PROGRESS_MSG = "Loading...";

    private String uriInProgress;

    public static boolean isPickerActive = false;
    public static boolean dontShowCityPicker = false;

    private static final int OPTYPE_TYPES = 30;
    private static final int OPTYPE_CUSTOMERS = 31;

    public static int requestType = -1;

    private SimpleDateFormat simpleFormat;

    private static final String[] planList = new String[]{"Pending", "Visited", "Completed", "Cancelled"};
    //private static final String[] customerList = new String[]{"UNIVERSAL PHARMACEUTICALS LIMITED", "Kleanzone Devices (India) Private Ltd", "SEM ", "Gujarat Ambuja Export Limited"};

    private TextView header_title, tvb_plan_status, et_competition_pricing, et_feedback;
    private View btn_header_left;
    private LinearLayout btnAddProg;

    public static ArrayList<HashMap<String, String>> entries = new ArrayList<HashMap<String, String>>();

    private SearchView mSearchView;

    private EditText et_location, et_objective, et_purpose, et_plan, et_strategy, et_title, et_description, et_anticipate;
    private TextView et_customername, et_type, et_starttime, et_endtime, et_startdate, et_enddate;

    private final String datetimeFormat = "yyyy-MM-dd kk:mm:ss";
    private final String dateFormat = "yyyy-MM-dd";
    private final String timeFormat = "kk:mm:ss";
    private final String timeFormat1 = "kk:mm";
    private String currentEventKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.scr_isr_add_programs);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initVar();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (toRefresh) {
            toRefresh = false;

            clearFields();
            et_customername.setText(DSREditProgScreen.currentEvent.getCustomer_name());
            et_location.setText(DSREditProgScreen.currentEvent.getArea());

            String[] s_date_time = DSREditProgScreen.currentEvent.getFrom_date_time().split(" ");
            //  String[] e_date_time = DSREditProgScreen.currentEvent.getTo_date_time().split(" ");

            et_startdate.setText(s_date_time[0]);
            et_starttime.setText(s_date_time[1]);

            // et_enddate.setText(e_date_time[0]);
            //  et_endtime.setText(e_date_time[1]);
        } else if (toUpdateCustomerName) {
            toUpdateCustomerName = false;
            et_customername.setText(DSREditProgScreen.currentEvent.getCustomer_name());
            et_location.setText(DSREditProgScreen.currentEvent.getArea());
        } else if (toCloneFromExisting) {
            toCloneFromExisting = false;

            et_title.setText(DSREditProgScreen.currentEvent.getEvent_title());
            et_description.setText(DSREditProgScreen.currentEvent.getEvent_description());
            et_customername.setText(DSREditProgScreen.currentEvent.getCustomer_name());
            et_type.setText(DSREditProgScreen.currentEvent.getEvent_type());
            et_location.setText(DSREditProgScreen.currentEvent.getArea());
            et_purpose.setText(DSREditProgScreen.currentEvent.getPreparation());
            et_plan.setText(DSREditProgScreen.currentEvent.getPlan());
            et_objective.setText(DSREditProgScreen.currentEvent.getObjective());
            et_strategy.setText(DSREditProgScreen.currentEvent.getStrategy());
            et_anticipate.setText(DSREditProgScreen.currentEvent.getAnticipate());

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);

        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Add Plan");

        btnAddProg = (LinearLayout) findViewById(R.id.btnAddProg);
        btnAddProg.setOnClickListener(this);

        isdUtils = new B2CBasicUtils(this);

        //setBackButton(false);

        modify_programs_scroll = (View) findViewById(R.id.modify_programs_scroll);

        wrapper_edit = (LinearLayout) findViewById(R.id.wrapper_edit);

        et_title = (EditText) findViewById(R.id.et_title);
        et_description = (EditText) findViewById(R.id.et_description);
        et_customername = (TextView) findViewById(R.id.et_customername);
        et_type = (TextView) findViewById(R.id.et_type);
        et_starttime = (TextView) findViewById(R.id.et_starttime);
        et_endtime = (TextView) findViewById(R.id.et_endtime);
        et_location = (EditText) findViewById(R.id.et_location);
        et_purpose = (EditText) findViewById(R.id.et_purpose);
        et_plan = (EditText) findViewById(R.id.et_plan);
        et_objective = (EditText) findViewById(R.id.et_objective);
        et_strategy = (EditText) findViewById(R.id.et_strategy);
        et_anticipate = (EditText) findViewById(R.id.et_anticipate);
        et_startdate = (TextView) findViewById(R.id.et_startdate);
        et_enddate = (TextView) findViewById(R.id.et_enddate);

        tvb_plan_status = (TextView) findViewById(R.id.tvb_plan_status);
        et_competition_pricing = (TextView) findViewById(R.id.et_competition_pricing);
        et_feedback = (TextView) findViewById(R.id.et_feedback);

        btn_header_left = (View) findViewById(R.id.btn_header_left);
        btn_header_left.setOnClickListener(this);

        simpleFormat = new SimpleDateFormat("MM-dd-yyyy");
        simpleFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));

        ((View) findViewById(R.id.ll_starttime)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_startdate)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_endtime)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_enddate)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_type)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_cname)).setOnClickListener(this);

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
        return true;

    }

    private void goBack() {
        if (backToDayScreen)
            gotoISRDayScreen();

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

    @Override
    public void onClick(boolean isPositive, String value, int opt) {

        isPickerActive = false;
        if (!isPositive)
            return;

        if (opt == OPTYPE_TYPES)
            et_type.setText("" + value);
        else if (opt == OPTYPE_CUSTOMERS) {
            et_customername.setText("" + value);
        }

    }

    @Override
    public void onDismiss(final int type) {

        isPickerActive = false;

    }

    public boolean onQueryTextChange(String newText) {

        return false;
    }

    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    public boolean onClose() {

        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public void onClick(View pView) {

        switch (pView.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_home:
                gotoB2CMenuScreen();
                break;

            case R.id.btnAddProg:

                validatePlan();

                break;

            case R.id.ll_type:

                showStringPicker(OPTYPE_TYPES);

                break;

            case R.id.ll_cname:
                //showStringPicker(OPTYPE_CUSTOMERS);
                B2CCustSearchScreen.isFromAddProg = true;
                gotoDSRCustListScreen();
                break;

            case R.id.ll_starttime:
                showTimePicker(true);
                break;


            case R.id.ll_startdate:
                showDatePicker(true);
                break;
          /*  case R.id.ll_endtime:
                showTimePicker(false);
                break;
            case R.id.ll_enddate:
                showDatePicker(false);
                break;*/

            default:

                break;
        }

    }

    private void showTimePicker(final boolean isStart) {
        String cTime;
        final String cDate;

        if (isStart)
            cTime = DSREditProgScreen.currentEvent.getFrom_date_time();
        else
            cTime = DSREditProgScreen.currentEvent.getTo_date_time();

        String[] particsOuter = cTime.split(" ");

        cDate = particsOuter[0];
        cTime = particsOuter[1];

        String[] partics = cTime.split(":");
        int mHour = Integer.parseInt(partics[0]);
        int mMinute = Integer.parseInt(partics[1]);

        TimePickerDialog tpd = new TimePickerDialog(DSRAddProgScreen.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar tCalendar = Calendar.getInstance();
                tCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                tCalendar.set(Calendar.MINUTE, minute);
                tCalendar.set(Calendar.SECOND, 0);

                String ttime = "" + DateFormat.format(timeFormat1, tCalendar.getTime().getTime());

                if (isStart) {
                    DSREditProgScreen.currentEvent.setFrom_date_time(cDate + " " + ttime);
                    et_starttime.setText(ttime);
                } else {
                    //   DSREditProgScreen.currentEvent.setTo_date_time(cDate + " " + ttime);
                    et_endtime.setText(ttime);
                }

            }
        }, mHour, mMinute, false);
        tpd.show();

    }

    private void showDatePicker(final boolean isStart) {
        String cDate;
        final String cTime;

        if (isStart) {
            cDate = DSREditProgScreen.currentEvent.getFrom_date_time();
        } else {
            cDate = DSREditProgScreen.currentEvent.getTo_date_time();
        }
        String[] particsOuter = cDate.split(" ");

        cDate = particsOuter[0];
        cTime = particsOuter[1];

        String[] partics = cDate.split("-");
        int mYear = Integer.parseInt(partics[0]);
        int mMonth = Integer.parseInt(partics[1]) - 1;
        int mDay = Integer.parseInt(partics[2]);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar tCalendar = Calendar.getInstance();
                tCalendar.set(year, monthOfYear, dayOfMonth);

                String ttime = "" + DateFormat.format(dateFormat, tCalendar.getTime().getTime());

                if (isStart) {
                    DSREditProgScreen.currentEvent.setFrom_date_time(ttime + " " + cTime);
                    et_startdate.setText(ttime);
                } else {
                    //   DSREditProgScreen.currentEvent.setTo_date_time(ttime + " " + cTime);
                    et_enddate.setText(ttime);
                }

            }

        }, mYear, mMonth, mDay);

        dpd.show();
    }

    private void validatePlan() {
        setErrorEmpty();
        final String title = "" + et_title.getText();
        final String type = "" + et_type.getText();
        final String desc = "" + et_description.getText();
        final String c_name = "" + et_customername.getText();
        final String s_time = "" + et_starttime.getText();
        //  final String e_time = "" + et_endtime.getText();
        final String s_date = "" + et_startdate.getText();
        //   final String e_date = "" + et_enddate.getText();
        final String loc = "" + et_location.getText();
        final String purp = "" + et_purpose.getText();
        final String obje = "" + et_objective.getText();
        final String plan = "" + et_plan.getText();
        final String stra = "" + et_strategy.getText();
        final String anticipate = "" + et_anticipate.getText();
        final String compotition_posting = "" + et_competition_pricing.getText();
        final String feedback = "" + et_feedback.getText();

        if (title.equalsIgnoreCase("")) {
            et_title.setError("");

            return;
        } else if (type.equalsIgnoreCase("")) {
            et_type.setError("");
            return;
        } else if (desc.equalsIgnoreCase("")) {
            et_description.setError("");
            return;
        } else if (c_name.equalsIgnoreCase("")) {
            et_customername.setError("");
            return;
        } else if (loc.equalsIgnoreCase("")) {
            et_location.setError("");
            return;
        } else if (s_time.equalsIgnoreCase("")) {
            et_starttime.setError("");
            return;
        } else if (s_date.equalsIgnoreCase("")) {
            et_startdate.setError("");
            return;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    DSRAddProgScreen.this);
            builder.setMessage(
                    "Are you sure to ADD this event ?")
                    .setTitle("Alert!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                    Logger.LogError("DSREditProgScreen.currentEvent.getEvent_key()", "" + DSREditProgScreen.currentEvent.getEvent_key());
                                    Logger.LogError("DSREditProgScreen.currentEvent.getCmkey()", "" + DSREditProgScreen.currentEvent.getCmkey());
                                    Logger.LogError("DSREditProgScreen.currentEvent.getCus_key()()", "" + DSREditProgScreen.currentEvent.getCus_key());
                                    Logger.LogError("DSREditProgScreen.currentEvent.getCustomer_name()", "" + DSREditProgScreen.currentEvent.getCustomer_name());
                                    int oldEvent_key = DSREditProgScreen.currentEvent.getEvent_key();
                                    int newEntryCount = B2CApp.b2cPreference.getNewEntryCount() - 1;

                                    DSREditProgScreen.currentEvent.setEvent_key(newEntryCount);
                                    B2CApp.b2cPreference.setNewEntryCount(newEntryCount);
                                    DSREditProgScreen.currentEvent.setEvent_user_key(B2CApp.b2cPreference.getUserId());
                                    DSREditProgScreen.currentEvent.setEvent_title(title);
                                    DSREditProgScreen.currentEvent.setEvent_type(type);
                                    DSREditProgScreen.currentEvent.setEvent_description(desc);
                                    DSREditProgScreen.currentEvent.setArea(loc);
                                    DSREditProgScreen.currentEvent.setPreparation(purp);
                                    DSREditProgScreen.currentEvent.setObjective(obje);
                                    DSREditProgScreen.currentEvent.setPlan(plan);
                                    DSREditProgScreen.currentEvent.setStrategy(stra);
                                    DSREditProgScreen.currentEvent.setAnticipate(anticipate);

                                    DSREditProgScreen.currentEvent.setCompetition_pricing(compotition_posting);
                                    DSREditProgScreen.currentEvent.setFeedback(feedback);

                                    DSREditProgScreen.currentEvent.setFrom_date_time(s_date + " " + s_time);
                                    //   DSREditProgScreen.currentEvent.setTo_date_time(e_date + " " + e_time);
                                    DSREditProgScreen.currentEvent.setStatus(DSRAppConstant.EVENT_STATUS.Pending.name());
                                    DSREditProgScreen.currentEvent.setCmkey("" + DSREditProgScreen.currentEvent.getCmkey());
                                    DSREditProgScreen.currentEvent.setCus_key("" + DSREditProgScreen.currentEvent.getCus_key());
                                    DSREditProgScreen.currentEvent.setCustomer_name("" + DSREditProgScreen.currentEvent.getCustomer_name());
                                    DSREditProgScreen.currentEvent.setEvent_month(s_date.substring(0, 7));
                                    String eventDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                                            (B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat, "" + s_date)));
                                    DSREditProgScreen.currentEvent.setEvent_date(eventDate);
                                    Logger.LogError("s_date", "" + s_date);

                                    DSREditProgScreen.currentEvent.setEvent_date_absolute(s_date.substring(8));
                                    DSREditProgScreen.currentEvent.setIs_synced_to_server("0");

                                    String tVisitedTime = "" + DateFormat.format(B2CAppConstant.datetimeFormat1, new Date().getTime());
                                    String entered_onDate = "" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime());
                                    DSREditProgScreen.currentEvent.setEntered_on(entered_onDate);
                                    Logger.LogError("oldEvent_key", "" + oldEvent_key);
                                    Logger.LogError(" B2CApp.b2cPreference.getSekey()", "" + B2CApp.b2cPreference.getSekey());
                                    Logger.LogError("DSRAppConstant.EVENT_STATUS.Visited.name()", "" + DSRAppConstant.EVENT_STATUS.Visited.name());
                                    Logger.LogError("tVisitedTime", "" + tVisitedTime);
                                    Logger.LogError("oldEvent_key", "" + oldEvent_key);
                                    Logger.LogError("oldEvent_key", "" + oldEvent_key);
                                    Logger.LogError("B2CApp.userLocData.getLatitude()", "" + B2CApp.userLocData.getLatitude());
                                    int intupdateOldEvent = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateStatus("" + oldEvent_key,
                                            B2CApp.b2cPreference.getUserId(), DSRAppConstant.EVENT_STATUS.Visited.name(), tVisitedTime, "", "",
                                            "", "", "" + B2CApp.userLocData.getLatitude(),
                                            "" + B2CApp.userLocData.getLongitude());


                                    B2CApp.b2cPreference.setCHECKEDINCMKEY(0);
                                    B2CApp.b2cPreference.setCHECKEDINCUSKEY(0);
                                    B2CApp.b2cPreference.setCHECKEDINEVENTKEY(0);
                                    B2CApp.b2cPreference.setCheckedIn(false);
                                    long longKey = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().insertEventData(DSREditProgScreen.currentEvent);
                                    Logger.LogError("intupdateOldEvent", "" + intupdateOldEvent);
                                    Logger.LogError("longKey", "" + longKey);
                                    if (longKey < 0) {

                                        clearFields();
                                        DSREditProgScreen.toRefresh = true;
                                        DSREditProgScreen.backToDayScreen = backToDayScreen;

                                        if (B2CApp.b2cUtils.isNetAvailable())
                                            new SyncToServerTask().execute(B2CApp.b2cPreference.getBaseUrl2() + DSRAppConstant.METHOD_ADDALL_NEW_EVENTS);
                                        else {
                                            Toast.makeText(DSRAddProgScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
                                            DSREditProgScreen.toRefresh = true;
                                            DSREditProgScreen.eventKey = "" + DSREditProgScreen.currentEvent.getEvent_key();
                                            gotoDSREditProgScreen();
                                        }

                                    } else
                                        Toast.makeText(DSRAddProgScreen.this, "Operation failed in a timely manner, please try again", Toast.LENGTH_LONG).show();

                                }

                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void setErrorEmpty() {
        et_title.setError(null);
        et_type.setError(null);
        et_description.setError(null);
        et_customername.setError(null);
        et_location.setError(null);
        et_starttime.setError(null);
        et_endtime.setError(null);
        et_startdate.setError(null);
        et_enddate.setError(null);
    }

    private void showStringPicker(int opType) {
        if (isPickerActive) {
            return;
        }

        Bundle bundle = new Bundle();

        if (opType == OPTYPE_TYPES) {
            bundle.putStringArray(getString(R.string.string_picker_dialog_values), DSRAppConstant.VISIT_TYPES);
            bundle.putString(getString(R.string.string_picker_title), "Select type");
        } else if (opType == OPTYPE_CUSTOMERS) {
            bundle.putStringArray(getString(R.string.string_picker_dialog_values), B2CApp.b2cPreference.getTCustomerList().toArray(new String[0]));
            bundle.putString(getString(R.string.string_picker_title), "Select customer");
        }

        bundle.putInt(getString(R.string.string_picker_type), opType);
        StringPickerDialog dialog = new StringPickerDialog();
        dialog.setArguments(bundle);
        dialog.show(this.getFragmentManager(), TAG);

        isPickerActive = true;
    }


    private void clearFields() {

        et_title.setText("");
        et_description.setText("");
        et_customername.setText("");
        et_type.setText("");
        et_starttime.setText("");
        et_endtime.setText("");
        et_location.setText("");
        et_purpose.setText("");
        et_plan.setText("");
        et_objective.setText("");
        et_strategy.setText("");
        et_startdate.setText("");
        et_enddate.setText("");
        et_anticipate.setText("");
        et_competition_pricing.setText("");
        et_feedback.setText("");

    }

    class SyncToServerTask extends AsyncTask<String, String, String> {

        private int operationType;

        protected String doInBackground(String... urls) {

            uriInProgress = urls[0];
            Logger.LogError("postUrl : ", uriInProgress);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uriInProgress);
            try {

                //operationType = getOperationType(uriInProgress);

                String jsonString = "";

                try {

                    jsonString = (new JSONObject().put(DSRAppConstant.KEY_DATA, getAllEventsArray(B2CApp.b2cPreference.getNewEntryCount()))).toString();

                    Logger.LogError("jsonString : ", jsonString);

/*					if(operationType==OPTYPE_UPDATEALLDATA)
					{
						jsonString = "[]";
						jsonString = getAllEventsArray().toString();
					}
					else
					{
						jsonString = "{}";
						jsonString = getJsonParams(operationType).toString();
					}*/

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

            B2CApp.b2cUtils.updateMaterialProgress(DSRAddProgScreen.this, PROGRESS_MSG);

        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);

            B2CApp.b2cUtils.dismissMaterialProgress();

            if (responseString == null) {

                Toast.makeText(DSRAddProgScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
                DSREditProgScreen.toRefresh = true;
                DSREditProgScreen.eventKey = "" + DSREditProgScreen.currentEvent.getEvent_key();
                //   DSREditProgScreen.eventKey = "" + B2CApp.b2cPreference.getNewEntryCount();
                gotoDSREditProgScreen();
                return;
            }

            try {

                JSONObject responseJson;
                JSONArray responseArray;

                responseJson = new JSONObject(responseString);

                if (responseJson.has(DSRAppConstant.KEY_STATUS) &&
                        responseJson.getInt(DSRAppConstant.KEY_STATUS) == 1) {
                    responseArray = responseJson.getJSONArray(B2CAppConstant.KEY_DATA);
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject innerjson = responseArray.getJSONObject(i);

                        B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateEventKey(innerjson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                                innerjson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()),
                                innerjson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));

                        currentEventKey = "" + innerjson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name());
						/*if (DSRApp.dsrLdbs.updateEventKey(DSRAddProgScreen.this, innerjson.getString(DSRAppConstant.KEY_DUMMY_KEY),
								innerjson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()),
								innerjson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()))) {


						}*/
                    }
                    DSREditProgScreen.eventKey = currentEventKey;
                    gotoDSREditProgScreen();
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Logger.LogError("JSONException : ", e.toString());
            }

            //  Toast.makeText(DSRAddProgScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
            DSREditProgScreen.eventKey = "" + B2CApp.b2cPreference.getNewEntryCount();
            gotoDSREditProgScreen();
        }
    }

    private JSONArray getAllEventsArray(int eventKey) throws JSONException {

        JSONArray tJsonArray = new JSONArray();


        ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchnonSynedData();
        if (eventDataArrayList.size() <= 0) {
            return tJsonArray;
        }
        for (int i = 0; i < eventDataArrayList.size(); i++) {
            EventData eventData = eventDataArrayList.get(i);

            JSONObject json = new JSONObject();

            if (eventData.getEvent_key() < 0) {
                json.put(DSRAppConstant.KEY_DUMMY_KEY, eventData.getEvent_key());
                json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), "");
            } else {
                json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), eventData.getEvent_key());
                json.put(DSRAppConstant.KEY_DUMMY_KEY, "");
            }

            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name(), eventData.getEvent_user_key());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type.name(), eventData.getEvent_type());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title.name(), eventData.getEvent_title());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name(), eventData.getEvent_date());
            json.put(B2CAppConstant.KEY_CUST_KEY, eventData.getCus_key());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name(), eventData.getFrom_date_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name(), eventData.getTo_date_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description.name(), eventData.getEvent_description());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), eventData.getStatus());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.entered_on.name(), eventData.getEntered_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on.name(), eventData.getCompleted_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on.name(), eventData.getCancelled_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name(), eventData.getCmkey());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name(), eventData.getArea());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name(), eventData.getLatitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name(), eventData.getLongitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude.name(), eventData.getAltitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time.name(), eventData.getVisit_update_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response.name(), eventData.getAction_response());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name(), eventData.getPlan());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name(), eventData.getObjective());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name(), eventData.getStrategy());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), eventData.getCustomer_name());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation.name(), eventData.getPreparation());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude.name(), eventData.getEvent_visited_latitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude.name(), eventData.getEvent_visited_longitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude.name(), eventData.getEvent_visited_altitude());

            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name(), eventData.getEvent_date());

            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.anticipate.name(), eventData.getAnticipate());
            json.put("event_repeat_config", "ONE_TIME_EVENT");
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.mins_of_meet.name(), eventData.getMinutes_of_meet());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.competition_pricing.name(), eventData.getCompetition_pricing());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.feedback.name(), eventData.getFeedback());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.order_taken.name(), eventData.getOrder_taken());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.product_display.name(), eventData.getProduct_display());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.promotion_activated.name(), eventData.getPromotion_activated());
            tJsonArray.put(json);

        }
        return tJsonArray;
    }


    public void alertUserP(Context context, String title, String msg, String btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setTitle(title).setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            locData.setAltitude(location.getAltitude());
            locData.setLongitude(location.getLongitude());
            locData.setLatitude(location.getLatitude());

            Toast.makeText(this, TAG + " location updated : " + "Alt" + location.getAltitude() + " Lat "
                    + location.getLatitude() + " Lng " + location.getLongitude(), Toast.LENGTH_SHORT).show();

        } else
            Toast.makeText(this, TAG + " location updated : null is updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            Toast.makeText(this,
                    provider + "LocationProvider.TEMPORARILY_UNAVAILABLE",
                    Toast.LENGTH_SHORT).show();
        } else if (status == LocationProvider.OUT_OF_SERVICE) {
            Toast.makeText(this,
                    provider + "LocationProvider.OUT_OF_SERVICE",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(this, "Gps Enabled", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onProviderDisabled(String provider) {

        Toast.makeText(this, "Gps Disabled", Toast.LENGTH_SHORT)
                .show();
    }

    private void gotoGPSSwitch() {
        Intent intent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void gotoDSREditProgScreen() {

        startActivity(new Intent(this, DSREditProgScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoISRDayScreen() {

        startActivity(new Intent(this, DSRDayScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }


    private void gotoDSRCustListScreen() {
        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .putExtra(B2CAppConstant.BACKTOCUSTOMERSEARCH, B2CAppConstant.ADDPROGSCREEN)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }
}
