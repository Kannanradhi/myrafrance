package com.isteer.b2c.activity.calender;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.DSRDayScreen;
import com.isteer.b2c.R;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.picker.StringPickerDialog;
import com.isteer.b2c.utility.B2CBasicUtils;
import com.isteer.b2c.utility.ColorCodes;
import com.isteer.b2c.utility.CustomPopupDialog;
import com.isteer.b2c.utility.Logger;

public class DSREditProgScreen extends AppCompatActivity implements OnClickListener, StringPickerDialog.OnClickListener, StringPickerDialog.OnDismissListener, CompoundButton.OnCheckedChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private String TAG = "DSREditProgScreen";

    public static String eventKey = null;

    public static boolean toRefresh = false;
    public static boolean backToDayScreen = true;
    public static boolean skipForNow = false;
    public static boolean isShowingView = true;
    public static boolean isListLogin = true;
    public static boolean isListInited = true;
    public static boolean isPickerActive = false;
    public static boolean dontShowCityPicker = false;

    private static boolean isVisitedEnabled;

    private LinearLayout wrapper_edit, wrapper_view, wrapper2_viewplan_buttons;
    private View modify_programs_scroll;

    private B2CBasicUtils isdUtils;

    private static final int OPTYPE_UNKNOWN = -1;
    private static final int OPTYPE_SETASCOMPLETED = 51;
    private static final int OPTYPE_SETASCANCELLED = 52;
    private static final int OPTYPE_SETASVISITED = 53;
    private static final int OPTYPE_UPDATEALLDATA = 54;

    public LocationManager mLocManager;

    private static int currentStatus = 0;
    public static EventData currentEvent;

    private static ProgressDialog pdialog;
    private static String PROGRESS_MSG = "Loading...";

    private String uriInProgress;

    private static final int OPTYPE_PLAN = 30;

    public static int requestType = -1;

    private SimpleDateFormat simpleFormat;

    private static final String[] planList = new String[]{"Pending", "Visited", "Completed", "Cancelled", "CheckIn"};

    private TextView header_title, tvb_plan_status;
    //private View btn_header_left, btn_header_right;
    private LinearLayout btnVisited;
    private LinearLayout btnEditProg;
    private TextView btnCompleteProg;

    //public static ArrayList<HashMap<String, String>> entries = new ArrayList<HashMap<String, String>>();

    private SearchView mSearchView;

    private TextView tv_customername, tv_datetime, tv_location, tv_purpose, tv_objective, tv_plan, tv_strategy, tv_anticipate,
            tv_title, tv_action_new, tv_momeet, tv_competition_pricing, tv_feedback;
    private EditText et_customername, et_title, et_starttime, et_endtime, et_date, et_location, et_objective, et_purpose,
            et_plan, et_strategy, et_anticipate, et_action_new, et_momeet, et_competition_pricing, et_feedback;

    private String datetimeFormat = "yyyy-MM-dd kk:mm:ss";
    private String dateFormat = "yyyy-MM-dd";
    private LinearLayout lt_visited_click;
    private TextView txt_btnEditProg;
    private LinearLayout lt_btnCompleteProg;
    private ImageView img_btnEditProg, img_btnCompleteProg;

    private Switch sw_order_taken, sw_product_display, sw_promotion_activated;
    private Dialog dialog;
    private CustomPopupDialog customPopupDialog;
    private TextView tv_visit;
    private EventData eventData;
    private LocationCallback locationCallback;
    private Location mLastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.scr_isr_edit_programs);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initVar();
        createLocationRequest();
        startLocationUpdates();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {

        super.onResume();

        if (toRefresh) {
            toRefresh = false;
            queryPlanDetails();
            //  tryEnableVisitedButton();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRemoveLocationUpdates();
    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);

        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Visit Plan");

        lt_btnCompleteProg = (LinearLayout) findViewById(R.id.lt_btnCompleteProg);
        btnCompleteProg = (TextView) findViewById(R.id.btnCompleteProg);
        btnEditProg = (LinearLayout) findViewById(R.id.btnEditProg);
        txt_btnEditProg = (TextView) findViewById(R.id.txt_btnEditProg);
        img_btnEditProg = (ImageView) findViewById(R.id.img_btnEditProg);
        img_btnCompleteProg = (ImageView) findViewById(R.id.img_btnCompleteProg);

        btnCompleteProg.setText("Cancel Visit");
        img_btnCompleteProg.setImageResource(R.drawable.ic_clear_black_24dp);
        btnCompleteProg.setOnClickListener(this);


        txt_btnEditProg.setOnClickListener(this);
        txt_btnEditProg.setText("Edit");
        img_btnEditProg.setImageResource(R.drawable.ic_edit_black_24dp);
        btnEditProg.setOnClickListener(this);

        btnVisited = findViewById(R.id.btnVisited);
        btnVisited.setOnClickListener(this);

        isdUtils = new B2CBasicUtils(this);

        //setBackButton(false);

        modify_programs_scroll = (View) findViewById(R.id.modify_programs_scroll);

        wrapper_edit = (LinearLayout) findViewById(R.id.wrapper_edit);
        wrapper_view = (LinearLayout) findViewById(R.id.wrapper_view);
        //wrapper2_viewplan_buttons = (LinearLayout) findViewById(R.id.wrapper2_viewplan_buttons);

        tv_customername = (TextView) findViewById(R.id.tv_customername);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_datetime = (TextView) findViewById(R.id.tv_datetime);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_purpose = (TextView) findViewById(R.id.tv_purpose);
        tv_objective = (TextView) findViewById(R.id.tv_objective);
        tv_plan = (TextView) findViewById(R.id.tv_plan);
        tv_strategy = (TextView) findViewById(R.id.tv_strategy);
        tv_anticipate = (TextView) findViewById(R.id.tv_anticipate);
        tv_action_new = (TextView) findViewById(R.id.tv_action_new);
        tv_momeet = (TextView) findViewById(R.id.tv_momeet);
        tv_competition_pricing = (TextView) findViewById(R.id.tv_competition_pricing);
        tv_feedback = (TextView) findViewById(R.id.tv_feedback);

        et_customername = (EditText) findViewById(R.id.et_customername);
        et_title = (EditText) findViewById(R.id.et_title);
        et_starttime = (EditText) findViewById(R.id.et_starttime);
        et_endtime = (EditText) findViewById(R.id.et_endtime);
        et_date = (EditText) findViewById(R.id.et_date);
        et_location = (EditText) findViewById(R.id.et_location);
        et_purpose = (EditText) findViewById(R.id.et_purpose);
        et_plan = (EditText) findViewById(R.id.et_plan);
        et_objective = (EditText) findViewById(R.id.et_objective);
        et_strategy = (EditText) findViewById(R.id.et_strategy);
        et_anticipate = (EditText) findViewById(R.id.et_anticipate);
        et_action_new = (EditText) findViewById(R.id.et_action_new);
        et_momeet = (EditText) findViewById(R.id.et_momeet);
        et_competition_pricing = (EditText) findViewById(R.id.et_competition_pricing);
        et_feedback = (EditText) findViewById(R.id.et_feedback);

        tvb_plan_status = (TextView) findViewById(R.id.tvb_plan_status);
        tv_visit = (TextView) findViewById(R.id.tv_visit);

        ((LinearLayout) findViewById(R.id.btnCancel)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.btnClone)).setOnClickListener(this);


        sw_order_taken = (Switch) findViewById(R.id.sw_order_taken);
        sw_order_taken.setOnCheckedChangeListener(this);
        sw_product_display = findViewById(R.id.sw_product_display);
        sw_product_display.setOnCheckedChangeListener(this);
        sw_promotion_activated = findViewById(R.id.sw_promotion_activated);
        sw_promotion_activated.setOnCheckedChangeListener(this);

        //setCustomStatus(planList[0]);
        showViewLayer(true);

/*		btn_header_left = (View) findViewById(R.id.btn_header_left);
        btn_header_left.setOnClickListener(this);

		isRegistered = ISRAddProgScreen.this.getApplicationContext().getSharedPreferences(
				"MyPref", 0).getBoolean(FFAppConstant.PREF_IS_REGISTERED, false);
		
		if(isRegistered)
		{
			registeredUid = ISRAddProgScreen.this.getApplicationContext().getSharedPreferences(
					"MyPref", 0).getString(FFAppConstant.PREF_REGISTERED_UID, FFAppConstant.DEFAULT_UID);
			registeredPid = ISRAddProgScreen.this.getApplicationContext().getSharedPreferences(
					"MyPref", 0).getString(FFAppConstant.PREF_REGISTERED_PID, "");
			registeredName = ISRAddProgScreen.this.getApplicationContext().getSharedPreferences(
					"MyPref", 0).getString(FFAppConstant.PREF_REGISTERED_NAME, "");
		}*/

        simpleFormat = new SimpleDateFormat("MM-dd-yyyy");
        simpleFormat.setTimeZone(TimeZone.getTimeZone("GMT-5:00"));

        lt_visited_click = (LinearLayout) findViewById(R.id.lt_visited_click);

    }

    private void queryPlanDetails() {
        lt_btnCompleteProg.setVisibility(View.INVISIBLE);
        //  showCompleteButton(false);
//        Cursor syncCursor = DSRApp.dsrLdbs.fetchSelected(this, DSRTableCreate.TABLE_AERP_EVENT_MASTER, null,
// DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), DSRLocalDBStorage.SELECTION_OPERATION_LIKE, new String[]{eventKey});
        currentEvent = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().getEventByeventkey("" + eventKey);
        if (currentEvent == null) {
            Toast.makeText(this, "Unable to fetch data in a timely manner", Toast.LENGTH_LONG);
        } else {
            updatePlanDetails(currentEvent);
        }
    }

    private void updatePlanDetails(EventData currentEvent) {


        tv_customername.setText("" + currentEvent.getCustomer_name());
        et_customername.setText("" + currentEvent.getCustomer_name());

        tv_title.setText("" + currentEvent.getEvent_title());
        et_title.setText("" + currentEvent.getEvent_title());

        setCustomStatus(currentEvent.getStatus());
        if (currentEvent.getVisit_update_time() == null) {
            tv_datetime.setText("" + currentEvent.getFrom_date_time());
        } else {
            tv_datetime.setText("" + currentEvent.getFrom_date_time() + " to " + currentEvent.getVisit_update_time());
        }
        et_starttime.setText("" + currentEvent.getFrom_date_time());
        et_endtime.setText("" + currentEvent.getTo_date_time());
        String eventDate = "" + DateFormat.format(B2CAppConstant.dateFormat3, B2CApp.b2cUtils.getTimestamp(
                B2CAppConstant.dateFormat2, "" + currentEvent.getEvent_date()));
        et_date.setText("" + eventDate);

        tv_location.setText("" + currentEvent.getArea());
        et_location.setText("" + currentEvent.getArea());
        Logger.LogError("currentEvent.getPreparation()", "" + currentEvent.getPreparation());
        tv_purpose.setText("" + (currentEvent.getPreparation() == null ? "" : currentEvent.getPreparation()));
        et_purpose.setText("" + (currentEvent.getPreparation() == null ? "" : currentEvent.getPreparation()));

        tv_objective.setText("" + (currentEvent.getObjective() == null ? "" : currentEvent.getObjective()));
        et_objective.setText("" + (currentEvent.getObjective() == null ? "" : currentEvent.getObjective()));

        tv_plan.setText("" + (currentEvent.getPlan() == null ? "" : currentEvent.getPlan()));
        et_plan.setText("" + (currentEvent.getPlan() == null ? "" : currentEvent.getPlan()));

        tv_strategy.setText("" + (currentEvent.getStrategy() == null ? "" : currentEvent.getStrategy()));
        et_strategy.setText("" + (currentEvent.getStrategy() == null ? "" : currentEvent.getStrategy()));

        tv_anticipate.setText("" + (currentEvent.getAnticipate() == null ? "" : currentEvent.getAnticipate()));
        et_anticipate.setText("" + (currentEvent.getAnticipate() == null ? "" : currentEvent.getAnticipate()));

        tv_action_new.setText("" + (currentEvent.getAction_response() == null ? "" : currentEvent.getAction_response()));
        et_action_new.setText("" + (currentEvent.getAction_response() == null ? "" : currentEvent.getAction_response()));
        Logger.LogError("currentEvent.getMinutes_of_meet()", "" + currentEvent.getMinutes_of_meet());
        tv_momeet.setText("" + (currentEvent.getMinutes_of_meet() == null ||
                currentEvent.getMinutes_of_meet().equalsIgnoreCase("null") ? "" : currentEvent.getMinutes_of_meet()));
        et_momeet.setText("" + (currentEvent.getMinutes_of_meet() == null ? "" : currentEvent.getMinutes_of_meet()));
        tv_competition_pricing.setText("" + (currentEvent.getCompetition_pricing().isEmpty() ? "" : currentEvent.getCompetition_pricing()));
        et_competition_pricing.setText("" + (currentEvent.getCompetition_pricing().isEmpty() ? "" : currentEvent.getCompetition_pricing()));
        tv_feedback.setText("" + (currentEvent.getFeedback().isEmpty() ? "" : currentEvent.getFeedback()));
        et_feedback.setText("" + (currentEvent.getFeedback().isEmpty() ? "" : currentEvent.getFeedback()));

        if (currentEvent.getOrder_taken().equalsIgnoreCase(getResources().getString(R.string.yes))) {
            sw_order_taken.setChecked(true);
        } else {
            sw_order_taken.setChecked(false);
        }
        if (currentEvent.getProduct_display().equalsIgnoreCase(getResources().getString(R.string.good))) {
            sw_product_display.setChecked(true);
        } else {
            sw_product_display.setChecked(false);
        }
        if (currentEvent.getPromotion_activated().equalsIgnoreCase(getResources().getString(R.string.yes))) {
            sw_promotion_activated.setChecked(true);
        } else {
            sw_promotion_activated.setChecked(false);
        }

    }

    private void updateNewPlanDetails() {

        tv_customername.setText("" + currentEvent.getCustomer_name());
        et_customername.setText("" + currentEvent.getCustomer_name());

        tv_title.setText("" + currentEvent.getEvent_title());
        et_title.setText("" + currentEvent.getEvent_title());

        setCustomStatus(currentEvent.getStatus());

        tv_datetime.setText("" + currentEvent.getFrom_date_time() + " to " + currentEvent.getTo_date_time());
        et_starttime.setText("" + currentEvent.getFrom_date_time());
        et_endtime.setText("" + currentEvent.getTo_date_time());
        et_date.setText("" + currentEvent.getEvent_date());

        tv_location.setText("" + currentEvent.getArea());
        et_location.setText("" + currentEvent.getArea());

        tv_purpose.setText("" + (currentEvent.getPreparation() == (null) ? "" : currentEvent.getPreparation()));
        et_purpose.setText("" + currentEvent.getPreparation());

        tv_objective.setText("" + currentEvent.getObjective());
        et_objective.setText("" + currentEvent.getObjective());

        tv_plan.setText("" + currentEvent.getPlan());
        et_plan.setText("" + currentEvent.getPlan());

        tv_strategy.setText("" + currentEvent.getStrategy());
        et_strategy.setText("" + currentEvent.getStrategy());

        tv_anticipate.setText("" + currentEvent.getAnticipate());
        et_anticipate.setText("" + currentEvent.getAnticipate());

        tv_action_new.setText("" + currentEvent.getAction_response());
        et_action_new.setText("" + currentEvent.getAction_response());
        Logger.LogError("currentEvent.getMinutes_of_meet()", "" + currentEvent.getMinutes_of_meet());
        Logger.LogError("currentEvent.getCompetition_pricing()", "" + currentEvent.getCompetition_pricing());
        Logger.LogError("currentEvent.getFeedback()", "" + currentEvent.getFeedback());
        tv_momeet.setText("" + currentEvent.getMinutes_of_meet());
        et_momeet.setText("" + currentEvent.getMinutes_of_meet());

        tv_competition_pricing.setText("" + currentEvent.getCompetition_pricing());
        et_competition_pricing.setText("" + currentEvent.getCompetition_pricing());

        tv_competition_pricing.setText("" + currentEvent.getFeedback());
        et_competition_pricing.setText("" + currentEvent.getFeedback());

    }

    private void setCustomStatus(String textToSet) {

        if (textToSet.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Pending.name())) {
            tvb_plan_status.setTextColor(getResources().getColor(R.color.red));
            tvb_plan_status.setText(planList[0]);
            btnVisited.setVisibility(View.VISIBLE);
            findViewById(R.id.btnCancel).setVisibility(View.VISIBLE);
            currentStatus = 0;
            setButtonset(true);
            return;
        } else if (textToSet.equalsIgnoreCase(planList[1])) {
            findViewById(R.id.btnCancel).setVisibility(View.GONE);
            findViewById(R.id.btnVisited).setVisibility(View.GONE);
            tvb_plan_status.setTextColor(getResources().getColor(R.color.very_dark_yellow));
            currentStatus = 1;
        } else if (textToSet.equalsIgnoreCase(planList[2])) {
            findViewById(R.id.btnCancel).setVisibility(View.GONE);
            findViewById(R.id.btnVisited).setVisibility(View.GONE);
            tvb_plan_status.setTextColor(Color.parseColor(ColorCodes.COLOR_GREEN));
            currentStatus = 2;
        } else if (textToSet.equalsIgnoreCase(planList[3])) {
            findViewById(R.id.btnCancel).setVisibility(View.GONE);
            findViewById(R.id.btnVisited).setVisibility(View.GONE);
            tvb_plan_status.setTextColor(getResources().getColor(R.color.LightGray));
            currentStatus = 3;
        } else if (textToSet.equalsIgnoreCase(planList[4])) {
            tvb_plan_status.setTextColor(this.getResources().getColor(R.color.checkin_color));
            tvb_plan_status.setText(planList[4]);
            btnVisited.setVisibility(View.VISIBLE);
            findViewById(R.id.btnCancel).setVisibility(View.VISIBLE);
            currentStatus = 0;
            setButtonset(true);
            return;
        }

        tvb_plan_status.setText(textToSet);
        btnVisited.setVisibility(View.GONE);
        setButtonset(true);

    }

    private void showCompleteButton(boolean show) {

        if (show) {
            lt_btnCompleteProg.setVisibility(View.VISIBLE);
            img_btnCompleteProg.setImageResource(R.drawable.ic_check_black_24dp);
            LayoutParams lprams1 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 8);
            modify_programs_scroll.setLayoutParams(lprams1);
        } else {
            lt_btnCompleteProg.setVisibility(View.INVISIBLE);
            LayoutParams lprams1 = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 8);
            modify_programs_scroll.setLayoutParams(lprams1);
        }

    }

    private void setButtonset(boolean isView) {
        //   showCompleteButton(!isView);

        if (isView) {

            switch (currentStatus) {

                case DSRAppConstant.STATUS_CHECKIN:
                case DSRAppConstant.STATUS_PENDING:
                case DSRAppConstant.STATUS_VISITED:
                    txt_btnEditProg.setText("Edit");
                    img_btnEditProg.setImageResource(R.drawable.ic_edit_black_24dp);
                    break;

                case DSRAppConstant.STATUS_CONMPLETED:
                case DSRAppConstant.STATUS_CANCELLED:
                    txt_btnEditProg.setText("Back");
                    img_btnEditProg.setImageResource(R.drawable.ic_arrow_back_black_24dp);
                    break;

                default:
                    break;
            }
        } else {

            switch (currentStatus) {

                case DSRAppConstant.STATUS_CHECKIN:
                case DSRAppConstant.STATUS_PENDING:
                    txt_btnEditProg.setText("Save");
                    img_btnEditProg.setImageResource(R.drawable.ic_sd_storage_black_24dp);
                    btnCompleteProg.setText("Cancel Visit");
                    img_btnCompleteProg.setImageResource(R.drawable.ic_clear_black_24dp);
                    break;

                case DSRAppConstant.STATUS_VISITED:
                    txt_btnEditProg.setText("Save");
                    img_btnEditProg.setImageResource(R.drawable.ic_sd_storage_black_24dp);
                    btnCompleteProg.setText("Complete POPSA");
                    img_btnCompleteProg.setImageResource(R.drawable.ic_check_black_24dp);
                    break;

                default:
                    break;
            }
        }

    }
	
/*	private void setBackButton(boolean toBack)
	{
		if(toBack)
		{		
			//((ImageView)findViewById(R.id.btn_header_left)).setImageResource(R.drawable.back);
			((ImageView)findViewById(R.id.btn_header_left)).setScaleX(0.7f);
			((ImageView)findViewById(R.id.btn_header_left)).setScaleY(0.7f);
		}
		else
		{		
			//((ImageView)findViewById(R.id.btn_header_left)).setImageResource(R.drawable.icon_user);
			((ImageView)findViewById(R.id.btn_header_left)).setScaleX(1.1f);
			((ImageView)findViewById(R.id.btn_header_left)).setScaleY(1.1f);
		}
		
	}*/

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
        if (!isShowingView)
            showViewLayer(true);
        else {
            if (backToDayScreen) {
                backToDayScreen = false;
                gotoDSRDayScreen();
            } else
                finish();

        }
    }

    private void showViewLayer(boolean showView) {

        setButtonset(showView);

        if (showView) {
            wrapper_edit.setVisibility(View.GONE);
            wrapper_view.setVisibility(View.VISIBLE);
            header_title.setText("Visit Plan");
            //setBackButton(false);
            //btn_header_right.setVisibility(View.VISIBLE);
            isShowingView = true;
        } else {
            wrapper_edit.setVisibility(View.VISIBLE);
            wrapper_view.setVisibility(View.GONE);
            //setBackButton(true);
            header_title.setText("Edit plan");
            //btn_header_right.setVisibility(View.INVISIBLE);
            isShowingView = false;
        }

    }

    private void setStatusVisited() {

        String tVisitedTime = "" + DateFormat.format(B2CAppConstant.datetimeFormat1, new Date().getTime());
        Logger.LogError("currentEvent.getEvent_key()", "" + currentEvent.getEvent_key());
        Logger.LogError("currentEvent.getEvent_user_key()", "" + currentEvent.getEvent_user_key());
        Logger.LogError("DSRAppConstant.EVENT_STATUS.Visited.name()", "" + DSRAppConstant.EVENT_STATUS.Visited.name());
        Logger.LogError("tVisitedTime", "" + tVisitedTime);
        int inupdatestatius = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateStatus("" + currentEvent.getEvent_key(),
                currentEvent.getEvent_user_key(), DSRAppConstant.EVENT_STATUS.Visited.name(), tVisitedTime, "", "",
                currentEvent.getAction_response(), currentEvent.getMinutes_of_meet(), "" + mLastLocation.getLatitude(),
                "" + mLastLocation.getLongitude());
        Logger.LogError("inupdatestatius", "" + inupdatestatius);
        /*if (DSRApp.dsrLdbs.updateStatus(DSREditProgScreen.this, "" + currentEvent.getEvent_key(),
                currentEvent.getEvent_user_key(),
                DSRAppConstant.EVENT_STATUS.Visited.name(), tVisitedTime, currentEvent.getAction_response(), currentEvent.getMinutes_of_meet())) {
*/
        if (inupdatestatius > 0) {
            currentEvent.setVisit_update_time(tVisitedTime);
            currentEvent.setTo_date_time(tVisitedTime);
            //    B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateEventData(currentEvent);
            btnVisited.setVisibility(View.GONE);
            currentStatus = DSRAppConstant.STATUS_VISITED;
            currentEvent.setStatus(DSRAppConstant.EVENT_STATUS.Visited.name());
            //  currentEvent.setVisit_update_time(tVisitedTime);
           /* currentEvent.setLatitude("" + B2CApp.userLocData.getLatitude());
            currentEvent.setLongitude("" + B2CApp.userLocData.getLongitude());*/
            currentEvent.setLatitude("" + mLastLocation.getLatitude());
            currentEvent.setLongitude("" + mLastLocation.getLongitude());
            setCustomStatus(DSRAppConstant.EVENT_STATUS.Visited.name());

            if (isdUtils.isNetAvailable())
                // if (currentEvent.getEvent_key() < 0) {
                new SyncToServerTask().execute(B2CApp.b2cPreference.getBaseUrl2() + DSRAppConstant.METHOD_ADDALL_NEW_EVENTS);
                /*} else {
                    new SyncToServerTask().execute(B2CApp.b2cPreference.getBaseUrl2() + DSRAppConstant.METHOD_SETASVISITED);
                }*/

            else {
                Toast.makeText(DSREditProgScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
                gotoB2CMenuScreen();
            }
        } else {
            gotoB2CMenuScreen();
            Toast.makeText(DSREditProgScreen.this, "Updation failed in a timely manner, please try again", Toast.LENGTH_LONG).show();
        }

    }

    private void saveButtonClicked() {

        EventData tEvent = new EventData();

        tEvent.setEvent_key(currentEvent.getEvent_key());
        tEvent.setEvent_user_key(B2CApp.b2cPreference.getUserId());
        tEvent.setEvent_title("" + et_title.getText());
        tEvent.setEvent_title("" + et_title.getText());
        tEvent.setArea("" + et_location.getText());
        tEvent.setStatus(currentEvent.getStatus());
        tEvent.setPreparation("" + et_purpose.getText());
        tEvent.setObjective("" + et_objective.getText());
        tEvent.setPlan("" + et_plan.getText());
        tEvent.setStrategy("" + et_strategy.getText());
        tEvent.setAnticipate("" + et_anticipate.getText());
        tEvent.setAction_response("" + et_action_new.getText());
        tEvent.setMinutes_of_meet("" + et_momeet.getText());
        tEvent.setCompetition_pricing("" + et_competition_pricing.getText());
        tEvent.setFeedback("" + et_feedback.getText());


        int intupdatedetails = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateData(tEvent.getPreparation(), tEvent.getObjective(),
                tEvent.getPlan(), tEvent.getStrategy(), tEvent.getAction_response(), tEvent.getAnticipate(), tEvent.getMinutes_of_meet(),
                tEvent.getCompetition_pricing(), tEvent.getFeedback(), "" + tEvent.getEvent_key(), tEvent.getEvent_user_key());
        //  if (DSRApp.dsrLdbs.updateData(DSREditProgScreen.this, tEvent)) {
        Logger.LogError("intupdatedetails", "" + intupdatedetails);
        if (intupdatedetails > 0) {
            currentEvent.setEvent_title(tEvent.getEvent_title());
            currentEvent.setArea(tEvent.getArea());
            currentEvent.setPreparation(tEvent.getPreparation());
            currentEvent.setObjective(tEvent.getObjective());
            currentEvent.setPlan(tEvent.getPlan());
            currentEvent.setStrategy(tEvent.getStrategy());
            currentEvent.setAction_response(tEvent.getAction_response());
            currentEvent.setAnticipate(tEvent.getAnticipate());
            currentEvent.setMinutes_of_meet(tEvent.getMinutes_of_meet());
            currentEvent.setCompetition_pricing(tEvent.getCompetition_pricing());
            currentEvent.setFeedback(tEvent.getFeedback());

            tv_location.setText(tEvent.getArea());
            tv_title.setText(tEvent.getEvent_title());
            tv_purpose.setText(tEvent.getPreparation());
            tv_objective.setText(tEvent.getObjective());
            tv_plan.setText(tEvent.getPlan());
            tv_strategy.setText(tEvent.getStrategy());
            tv_anticipate.setText(tEvent.getAnticipate());
            tv_action_new.setText(tEvent.getAction_response());
            tv_momeet.setText(tEvent.getMinutes_of_meet());
            tv_competition_pricing.setText(tEvent.getCompetition_pricing());
            tv_feedback.setText(tEvent.getFeedback());

            showViewLayer(true);

            if (B2CApp.b2cUtils.isNetAvailable())
                new SyncToServerTask().execute(B2CApp.b2cPreference.getBaseUrl2() + DSRAppConstant.METHOD_ADDALL_NEW_EVENTS);
            else {
                gotoB2CMenuScreen();
                Toast.makeText(DSREditProgScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(DSREditProgScreen.this, "Updation failed in a timely manner, please try again", Toast.LENGTH_LONG).show();
    }

    private void exitApp() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }

    private void showDatePicker() {
        String cDate = simpleFormat.format(new Date().getTime());

        String[] partics = cDate.split("-");
        int mMonth = Integer.parseInt(partics[0]) - 1;
        int mDay = Integer.parseInt(partics[1]);
        int mYear = Integer.parseInt(partics[2]);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {/*

				FFAppConstant.MONTH = monthOfYear+1;
				FFAppConstant.DAY = dayOfMonth;
				FFAppConstant.YEAR = year;

				header_title.setText(FFAppConstant.MONTH+"/"+FFAppConstant.DAY+"/"+FFAppConstant.YEAR);

				pageLoaded=0;
				totalItemCountPrev = 0;
											
			*/
            }

        }, mYear, mMonth, mDay);

        dpd.show();
    }

    private void updateProgress(String message) {
        if (pdialog != null && pdialog.isShowing())
            pdialog.setMessage(message);
        else
            try {
                pdialog = ProgressDialog.show(this, "", message, true);
            } catch (Exception e) {
                e.printStackTrace();
            }

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

        if (opt == OPTYPE_PLAN) {/*
			tv_plan_status.setText(""+value);
        	setTextCustom(value);
        */
        }

    }

    @Override
    public void onDismiss(final int type) {

        isPickerActive = false;

    }

    private void SearchViewShow(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

        //mSearchView.setOnQueryTextListener(this);
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
            case R.id.btnEditProg:
            case R.id.txt_btnEditProg:

                if (((txt_btnEditProg.getText().toString().equalsIgnoreCase("Back"))))
                    gotoDSRDayScreen();
                else {

                    if (isShowingView) {
                        showViewLayer(false);
                    } else {
                        saveButtonClicked();
                    }
                }

                break;

            case R.id.btnVisited:
                Logger.LogError("mLastLocation", "" + mLastLocation);
                if (mLastLocation != null) {
                    lt_visited_click.setVisibility(View.VISIBLE);
                    txt_btnEditProg.setText("Save");
                    img_btnEditProg.setImageResource(R.drawable.ic_sd_storage_black_24dp);
                    visitButtonClick();
                } else if (!B2CApp.b2cUtils.isGPSEnabled(this)) {
                    alertGPSSwitch();
                } else {
                    startLocationUpdates();
                    startRequestLocationUpdates();
                    Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
                }
                //   visitedButtonClicked();
			/*if(isVisitedEnabled)
			{
				lt_visited_click.setVisibility(View.VISIBLE);
				btnEditProg.setText("Save");
				visitedButtonClicked();
			}
			else
			{
				if(!tryEnableVisitedButton())
					Toast.makeText(this, "Your location is too far to mark it as 'Visited' right now", Toast.LENGTH_SHORT).show();
			}*/

                break;

            case R.id.btnClone:

                cloneButtonClicked();

                break;

            case R.id.btnCancel:

                //cancelButtonClicked();
                cancelButtonPopup();

                break;

            case R.id.btnCompleteProg:

                String statusSelected = btnCompleteProg.getText().toString();

                if (statusSelected.equalsIgnoreCase("Complete POPSA")) {
                    completeButtonClicked();
                } else if (statusSelected.equalsIgnoreCase("Save")) {
                    updateOrderProduct();
                } else {
                    cancelButtonClicked();
                }

                break;

            default:
                break;
        }

    }

    private void updateOrderProduct() {

        B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateYesOrNo(currentEvent.getOrder_taken(), currentEvent.getProduct_display(),
                currentEvent.getPromotion_activated(), currentEvent.getEvent_key(), currentEvent.getEvent_user_key());

    }

    private void cloneButtonClicked() {

        DSRAddProgScreen.toCloneFromExisting = true;
        gotoDSRAddProgScreen();
    }

    private void completeButtonClicked() {
        final String tPurpose = "" + et_purpose.getText();
        final String tObjective = "" + et_objective.getText();
        final String tPlan = "" + et_plan.getText();
        final String tStrategy = "" + et_strategy.getText();
        final String tAnticipate = "" + et_anticipate.getText();
        final String tActionNew = "" + et_action_new.getText();
        final String tMOMeet = "" + et_momeet.getText();
        Logger.LogError("et_competition_pricing.getText()", "" + et_competition_pricing.getText());
        Logger.LogError("feedback)", "" + et_feedback.getText());
        final String competition_pricing = "" + et_competition_pricing.getText();
        final String feedback = "" + et_feedback.getText();

        if (tPurpose.equals("") || tObjective.equals("") || tPlan.equals("") || tStrategy.equals("") || tAnticipate.equals("") || tActionNew.equals("") || tMOMeet.equals("")) {
            alertUserP(this, "Alert", "Please enter valid data to mark it as COMPLETED", "OK");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(
                DSREditProgScreen.this);
        builder.setMessage(
                "Are you sure to mark it as COMPLETED ?")
                .setTitle("Alert!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();

                                String tCompletedTime = "" + DateFormat.format(B2CAppConstant.datetimeFormat1, new Date().getTime());

                                int intupdatestatus = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateStatus("" + currentEvent.getEvent_key(),
                                        currentEvent.getEvent_user_key(), DSRAppConstant.EVENT_STATUS.Completed.name(), "", tCompletedTime, "",
                                        "", "", "" + mLastLocation.getLatitude(),
                                        "" + mLastLocation.getLongitude());
                              /*  if (DSRApp.dsrLdbs.updateStatus(DSREditProgScreen.this, "" + currentEvent.getEvent_key(),
                                        currentEvent.getEvent_user_key(),
                                        DSRAppConstant.EVENT_STATUS.Completed.name(), tCompletedTime, "", "")) {*/
                                Logger.LogError("intupdatestatus", "" + intupdatestatus);
                                if (intupdatestatus > 0) {
                                    currentStatus = DSRAppConstant.STATUS_CONMPLETED;
                                    currentEvent.setStatus(DSRAppConstant.EVENT_STATUS.Completed.name());
                                    currentEvent.setCompleted_on(tCompletedTime);
                                    currentEvent.setPlan(tPlan);
                                    currentEvent.setObjective(tObjective);
                                    currentEvent.setPreparation(tPurpose);
                                    currentEvent.setStrategy(tStrategy);
                                    currentEvent.setAnticipate(tAnticipate);
                                    currentEvent.setAction_response(tActionNew);
                                    currentEvent.setMinutes_of_meet(tMOMeet);
                                    currentEvent.setCompetition_pricing(competition_pricing);
                                    currentEvent.setFeedback(feedback);

                                    setCustomStatus(DSRAppConstant.EVENT_STATUS.Completed.name());
									
/*									if(isdUtils.isNetAvailable())
										new SyncToServerTask().execute(isdPreference.getBaseUrl()+DSRAppConstant.METHOD_SETASCOMPLETED);
									else
										Toast.makeText(DSREditProgScreen.this, "Saved locally", Toast.LENGTH_LONG).show();*/

                                    saveButtonClicked();
                                } else
                                    Toast.makeText(DSREditProgScreen.this, "Updation failed in a timely manner, please try again", Toast.LENGTH_LONG).show();
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

    private void visitedButtonClicked() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DSREditProgScreen.this);

        alert.setTitle("Setting as Visited");
        alert.setMessage("Enter the future action required after the visit");
        alert.setCancelable(false);

        final EditText input1 = new EditText(DSREditProgScreen.this);
        input1.setHint("Next Action");
        alert.setView(input1);
	
	/*			final EditText input2 = new EditText(DSREditProgScreen.this);
		input2.setHint("Minutes of Meet");
		alert.(input2);*/

        alert.setPositiveButton("Plan Next Visit", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                String nextAction = "";
                nextAction = input1.getText().toString();
                //String minsOfMeet = input2.getText().toString();

                if (nextAction != null && !nextAction.equalsIgnoreCase("")) {
                    currentEvent.setAction_response(nextAction);
                    //currentEvent.setMinutes_of_meet(minsOfMeet);
                    Logger.LogError("c.getCmkey()", "" + currentEvent.getCmkey());
                    Logger.LogError("B2CApp.b2cPreference.getCHECKEDINCMKEY()", "" + B2CApp.b2cPreference.getCHECKEDINCMKEY());
                    if (currentEvent.getCmkey().equalsIgnoreCase("" + B2CApp.b2cPreference.getCHECKEDINCMKEY())) {
                        B2CApp.b2cPreference.setCHECKEDINCMKEY(0);
                        B2CApp.b2cPreference.setCHECKEDINCUSKEY(0);
                        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(0);
                        B2CApp.b2cPreference.setCheckedIn(false);
                    }
                    //  setStatusVisited();
                } else
                    alertUserP(DSREditProgScreen.this, "Alert", "Enter the future action required after the visit", "OK");
            }
        });

        alert.setNegativeButton("No Next Visit", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                String nextAction = input1.getText().toString();
                //String minsOfMeet = input2.getText().toString();

                if (nextAction != null && !nextAction.equalsIgnoreCase("")) {
                    currentEvent.setAction_response(nextAction);
                    //currentEvent.setMinutes_of_meet(minsOfMeet);
                    Logger.LogError("currentEvent.getCmkey()", "" + currentEvent.getCmkey());
                    Logger.LogError("B2CApp.b2cPreference.getCHECKEDINCMKEY()", "" + B2CApp.b2cPreference.getCHECKEDINCMKEY());
                    if (currentEvent.getCmkey().equalsIgnoreCase("" + B2CApp.b2cPreference.getCHECKEDINCMKEY())) {
                        B2CApp.b2cPreference.setCHECKEDINCMKEY(0);
                        B2CApp.b2cPreference.setCHECKEDINCUSKEY(0);
                        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(0);
                        B2CApp.b2cPreference.setCheckedIn(false);
                    }
                    setStatusVisited();
                } else
                    alertUserP(DSREditProgScreen.this, "Alert", "Enter the future action required after the visit", "OK");
            }
        });

        alert.show();
    }

    private void visitButtonClick() {
        String title = getResources().getString(R.string.settings_as_visited);
        String message = getResources().getString(R.string.visited_message);
        String edittext = getResources().getString(R.string.visited_edittext);
        String visited_yes = getResources().getString(R.string.visited_yes);
        String visited_no = getResources().getString(R.string.visited_no);
        customPopupDialog = new CustomPopupDialog(this, title, message, edittext, visited_no, visited_yes,
                new CustomPopupDialog.myOnClickListenerLeft() {
                    @Override
                    public void onButtonClickLeft(String s) {
                        String nextAction = "";
                        nextAction = s;
                        //String minsOfMeet = input2.getText().toString();

                        if (nextAction != null && !nextAction.equalsIgnoreCase("")) {
                            currentEvent.setAction_response(nextAction);
                            //currentEvent.setMinutes_of_meet(minsOfMeet);
                            Logger.LogError("currentEvent.getCmkey()", "" + currentEvent.getCmkey());
                            Logger.LogError("B2CApp.b2cPreference.getCHECKEDINCMKEY()", "" + B2CApp.b2cPreference.getCHECKEDINCMKEY());
                            if (currentEvent.getCmkey().equalsIgnoreCase("" + B2CApp.b2cPreference.getCHECKEDINCMKEY())) {
                                B2CApp.b2cPreference.setCHECKEDINCMKEY(0);
                                B2CApp.b2cPreference.setCHECKEDINCUSKEY(0);
                                B2CApp.b2cPreference.setCHECKEDINEVENTKEY(0);
                                B2CApp.b2cPreference.setCheckedIn(false);
                            }
                            setStatusVisited();
                        } else

                            alertUserP(DSREditProgScreen.this, "Alert", "Enter the future action required after the visit", "OK");
                    }
                }, new CustomPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight(String s) {
                String nextAction = "";
                nextAction = s;
                //String minsOfMeet = input2.getText().toString();

                if (nextAction != null && !nextAction.equalsIgnoreCase("")) {
                    currentEvent.setAction_response(nextAction);
                    //currentEvent.setMinutes_of_meet(minsOfMeet);
                    Logger.LogError("currentEvent.getCmkey()", "" + currentEvent.getCmkey());
                    Logger.LogError("B2CApp.b2cPreference.getCHECKEDINCMKEY()", "" + B2CApp.b2cPreference.getCHECKEDINCMKEY());
                    if (currentEvent.getCmkey().equalsIgnoreCase("" + B2CApp.b2cPreference.getCHECKEDINCMKEY())) {
                        B2CApp.b2cPreference.setCHECKEDINCMKEY(0);
                        B2CApp.b2cPreference.setCHECKEDINCUSKEY(0);
                        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(0);
                        B2CApp.b2cPreference.setCheckedIn(false);
                    }
                    setStatusVisited();
                } else
                    alertUserP(DSREditProgScreen.this, "Alert", "Enter the future action required after the visit", "OK");
            }
        }, new CustomPopupDialog.myOnClickListenerThird() {
            @Override
            public void onButtonClickThird(String s) {

            }
        });

    }

    private void cancelButtonClicked() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                DSREditProgScreen.this);
        builder.setMessage(
                "Are you sure to mark it as CANCELLED ?")
                .setTitle("Alert!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();

                                String tCancelledTime = "" + DateFormat.format(B2CAppConstant.datetimeFormat1, new Date().getTime());
                                Logger.LogError("tCancelledTime", "" + tCancelledTime);

                                int intupdatestatus = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateStatus("" + currentEvent.getEvent_key(),
                                        currentEvent.getEvent_user_key(), DSRAppConstant.EVENT_STATUS.Cancelled.name(), tCancelledTime, "", tCancelledTime,
                                        "", "", "" + mLastLocation.getLatitude(),
                                        "" + mLastLocation.getLongitude());

                               /* if (DSRApp.dsrLdbs.updateStatus(DSREditProgScreen.this, "" + currentEvent.getEvent_key(), currentEvent.getEvent_user_key(),
                                        DSRAppConstant.EVENT_STATUS.Cancelled.name(), tCancelledTime, "", "")) {*/
                                if (intupdatestatus < 0) {
                                    currentStatus = DSRAppConstant.STATUS_CANCELLED;
                                    currentEvent.setStatus(DSRAppConstant.EVENT_STATUS.Cancelled.name());
                                    currentEvent.setCancelled_on(tCancelledTime);
                                    setCustomStatus(DSRAppConstant.EVENT_STATUS.Cancelled.name());
                                    showViewLayer(!isShowingView);
                                    Logger.LogError("getEvent_key", "" + currentEvent.getEvent_key());
                                    if (currentEvent.getCmkey().equalsIgnoreCase("" + B2CApp.b2cPreference.getCHECKEDINCMKEY())) {
                                        B2CApp.b2cPreference.setCHECKEDINCMKEY(0);
                                        B2CApp.b2cPreference.setCHECKEDINCUSKEY(0);
                                        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(0);
                                        B2CApp.b2cPreference.setCheckedIn(false);
                                    }
                                    if (isdUtils.isNetAvailable())
                                        new SyncToServerTask().execute(B2CApp.b2cPreference.getBaseUrl() + DSRAppConstant.METHOD_ADDALL_NEW_EVENTS);
                                    else {
                                        Toast.makeText(DSREditProgScreen.this, "Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                        gotoB2CMenuScreen();
                                    }
                                } else
                                    Toast.makeText(DSREditProgScreen.this, "Updation failed in a timely manner, please try again", Toast.LENGTH_LONG).show();
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

    private void cancelButtonPopup() {
        String title = getResources().getString(R.string.cancel_visit);
        String message = getResources().getString(R.string.cancel_message);
        customPopupDialog = new CustomPopupDialog(this, title, message, "", "No", "Yes", new CustomPopupDialog.myOnClickListenerLeft() {
            @Override
            public void onButtonClickLeft(String s) {

            }
        }, new CustomPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight(String s) {


                String tCancelledTime = "" + DateFormat.format(B2CAppConstant.datetimeFormat1, new Date().getTime());

                int intupdatestatus = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateStatus("" + currentEvent.getEvent_key(),
                        currentEvent.getEvent_user_key(), DSRAppConstant.EVENT_STATUS.Cancelled.name(), tCancelledTime, "", tCancelledTime,
                        "", "", "" + mLastLocation.getLatitude(),
                        "" + mLastLocation.getLongitude());

              /*  if (DSRApp.dsrLdbs.updateStatus(DSREditProgScreen.this, "" + currentEvent.getEvent_key(),
                        currentEvent.getEvent_user_key(),DSRAppConstant.EVENT_STATUS.Cancelled.name(), tCancelledTime, "", "")) {*/
                Logger.LogError("intupdatestatus", "" + intupdatestatus);
                if (intupdatestatus > 0) {
                    currentStatus = DSRAppConstant.STATUS_CANCELLED;
                    currentEvent.setStatus(DSRAppConstant.EVENT_STATUS.Cancelled.name());
                    currentEvent.setCancelled_on(tCancelledTime);
                    currentEvent.setVisit_update_time(tCancelledTime);
                    currentEvent.setTo_date_time(tCancelledTime);
                    setCustomStatus(DSRAppConstant.EVENT_STATUS.Cancelled.name());
                    showViewLayer(!isShowingView);
                    Logger.LogError("getEvent_key", "" + currentEvent.getEvent_key());
                    if (currentEvent.getCmkey().equalsIgnoreCase("" + B2CApp.b2cPreference.getCHECKEDINCMKEY())) {
                        B2CApp.b2cPreference.setCHECKEDINCMKEY(0);
                        B2CApp.b2cPreference.setCHECKEDINCUSKEY(0);
                        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(0);
                        B2CApp.b2cPreference.setCheckedIn(false);
                    }
                    if (isdUtils.isNetAvailable())
                        //  if (currentEvent.getEvent_key() < 0) {
                        new SyncToServerTask().execute(B2CApp.b2cPreference.getBaseUrl2() + DSRAppConstant.METHOD_ADDALL_NEW_EVENTS);
                        /*} else {
                            new SyncToServerTask().execute(B2CApp.b2cPreference.getBaseUrl22() + DSRAppConstant.METHOD_SETASCANCELLED);
                        }*/

                    else {
                        gotoB2CMenuScreen();
                        Toast.makeText(DSREditProgScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DSREditProgScreen.this, "Updation failed in a timely manner, please try again", Toast.LENGTH_LONG).show();
                }

            }
        }, new CustomPopupDialog.myOnClickListenerThird() {
            @Override
            public void onButtonClickThird(String s) {

            }
        });
    }

    private void showStringPicker(int opType) {
        if (isPickerActive) {
            return;
        }

        Bundle bundle = new Bundle();

        if (opType == OPTYPE_PLAN) {
            bundle.putStringArray(getString(R.string.string_picker_dialog_values), planList);
            bundle.putString(getString(R.string.string_picker_title), "Select status");
        }

        bundle.putInt(getString(R.string.string_picker_type), opType);
        StringPickerDialog dialog = new StringPickerDialog();
        dialog.setArguments(bundle);
        dialog.show(this.getFragmentManager(), TAG);

        isPickerActive = true;
    }

    private int getOperationType(String tUri) {

        int operation = OPTYPE_UNKNOWN;

        if (tUri.contains(DSRAppConstant.METHOD_SETASCANCELLED))
            operation = OPTYPE_SETASCANCELLED;
        else if (tUri.contains(DSRAppConstant.METHOD_SETASCOMPLETED))
            operation = OPTYPE_SETASCOMPLETED;
        else if (tUri.contains(DSRAppConstant.METHOD_SETASVISITED))
            operation = OPTYPE_SETASVISITED;
        else if (tUri.contains(DSRAppConstant.METHOD_ADDALL_NEW_EVENTS))
            operation = OPTYPE_UPDATEALLDATA;
        else
            operation = OPTYPE_UNKNOWN;

        return operation;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {

            case R.id.sw_order_taken:
                if (sw_order_taken.isChecked()) {
                    Logger.LogError("currentEvent", "" + currentEvent.getEvent_key());
                    Logger.LogError("currentEvent", "" + currentEvent.getEvent_user_key());
                    currentEvent.setOrder_taken(getResources().getString(R.string.yes));
                    updateOrderProduct();

                } else {
                    currentEvent.setOrder_taken(getResources().getString(R.string.no));
                    updateOrderProduct();

                }

                break;

            case R.id.sw_product_display:
                if (sw_product_display.isChecked()) {
                    currentEvent.setProduct_display(getResources().getString(R.string.good));
                    updateOrderProduct();

                } else {
                    currentEvent.setProduct_display(getResources().getString(R.string.not_good));
                    updateOrderProduct();

                }

                break;

            case R.id.sw_promotion_activated:
                if (sw_promotion_activated.isChecked()) {
                    currentEvent.setPromotion_activated(getResources().getString(R.string.yes));
                    updateOrderProduct();

                } else {
                    currentEvent.setPromotion_activated(getResources().getString(R.string.no));
                    updateOrderProduct();

                }
                break;
        }
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

    }


    class SyncToServerTask extends AsyncTask<String, String, String> {

        private int operationType;

        protected String doInBackground(String... urls) {

            uriInProgress = urls[0];
            Logger.LogError("postUrl : ", uriInProgress);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uriInProgress);
            try {

                operationType = getOperationType(uriInProgress);

                String jsonString = "";

                try {

                    if (operationType == OPTYPE_UPDATEALLDATA) {
                        jsonString = getAllEventsArray().toString();
                    } else {
                        jsonString = getJsonParams(operationType).toString();
                    }

                    Logger.LogError("jsonString", jsonString);

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

            B2CApp.b2cUtils.updateMaterialProgress(DSREditProgScreen.this, PROGRESS_MSG);


        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);

            B2CApp.b2cUtils.dismissMaterialProgress();

            if (responseString == null) {
                gotoB2CMenuScreen();
                Toast.makeText(DSREditProgScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                JSONObject responseJson;
                JSONObject outerresponseJson;

                if (operationType == OPTYPE_UPDATEALLDATA) {
                    outerresponseJson = new JSONObject(responseString);
                    if (outerresponseJson.has(DSRAppConstant.KEY_STATUS) &&
                            (outerresponseJson.getInt(DSRAppConstant.KEY_STATUS) == 1)) {
                        JSONArray responseArray = outerresponseJson.getJSONArray(B2CAppConstant.KEY_DATA);
                        for (int i = 0; i < responseArray.length(); i++) {
                            responseJson = responseArray.getJSONObject(i);


                            B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateEventKey(
                                    responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                                    responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()),
                                    responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));
                            currentEvent.setEvent_key(Integer.parseInt(responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name())));
                            /*if (DSRApp.dsrLdbs.updateEventKey(DSREditProgScreen.this, responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                                    responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()),
                                    responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name())))
                            {*/
                            DSREditProgScreen.eventKey = "" + responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name());

                            int dummy_key = Integer.valueOf(responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY));
                            int event_key = Integer.valueOf(responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));
                            Logger.LogError("dummy_key", "" + dummy_key);
                            Logger.LogError("event_key", "" + event_key);
                            if (B2CApp.b2cPreference.getCHECKEDINEVENTKEY() == dummy_key) {
                                B2CApp.b2cPreference.setCHECKEDINEVENTKEY(event_key);
                            }

                        }
                        B2CApp.getINSTANCE().startAlarm();
                        Toast.makeText(DSREditProgScreen.this, "Updated successfull", Toast.LENGTH_SHORT).show();
                        gotoB2CMenuScreen();
                        //  alertUserP(DSREditProgScreen.this, "Success", "Updated successfully", "OK");

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Logger.LogError("JSONException : ", e.toString());

            }


        }
    }

    private JSONObject getJsonParams(int opType) throws JSONException {

        JSONObject json = new JSONObject();

        json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), currentEvent.getEvent_key());
        json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name(), currentEvent.getEvent_user_key());

        if (opType == OPTYPE_SETASCOMPLETED) {
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on.name(), currentEvent.getCompleted_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name(), currentEvent.getPlan());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name(), currentEvent.getObjective());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation.name(), currentEvent.getPreparation());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name(), currentEvent.getStrategy());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response.name(), currentEvent.getAction_response());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.anticipate.name(), currentEvent.getAnticipate());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.mins_of_meet.name(), currentEvent.getMinutes_of_meet());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.competition_pricing.name(), currentEvent.getCompetition_pricing());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.feedback.name(), currentEvent.getFeedback());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.order_taken.name(), currentEvent.getOrder_taken());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.product_display.name(), currentEvent.getProduct_display());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.promotion_activated.name(), currentEvent.getPromotion_activated());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), currentEvent.getStatus());
        } else if (opType == OPTYPE_SETASCANCELLED) {
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on.name(), currentEvent.getCancelled_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), currentEvent.getStatus());
        } else if (opType == OPTYPE_SETASVISITED) {
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time.name(), currentEvent.getVisit_update_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name(), currentEvent.getLatitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name(), currentEvent.getLongitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude.name(), currentEvent.getEvent_visited_altitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response.name(), currentEvent.getAction_response());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.mins_of_meet.name(), currentEvent.getMinutes_of_meet());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.competition_pricing.name(), currentEvent.getCompetition_pricing());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.feedback.name(), currentEvent.getFeedback());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.order_taken.name(), currentEvent.getOrder_taken());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.product_display.name(), currentEvent.getProduct_display());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.promotion_activated.name(), currentEvent.getPromotion_activated());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), currentEvent.getStatus());
        }
/*		else if(opType==OPTYPE_UPDATEALLDATA)
		{
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type.name(), currentEvent.getEvent_type());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title.name(), currentEvent.getEvent_title());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name(), currentEvent.getFrom_date_time());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name(), currentEvent.getTo_date_time());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description.name(), currentEvent.getEvent_description());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), currentEvent.getStatus());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name(), currentEvent.getCmkey());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name(), currentEvent.getArea());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name(), currentEvent.getLatitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name(), currentEvent.getLongitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude.name(), currentEvent.getAltitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time.name(), currentEvent.getVisit_update_time());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response.name(), currentEvent.getAction_response());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name(), currentEvent.getPlan());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name(), currentEvent.getObjective());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name(), currentEvent.getStrategy());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), currentEvent.getCustomer_name());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation.name(), currentEvent.getPreparation());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude.name(), currentEvent.getEvent_visited_latitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude.name(), currentEvent.getEvent_visited_longitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude.name(), currentEvent.getEvent_visited_altitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on.name(), currentEvent.getCompleted_on());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on.name(), currentEvent.getCancelled_on());
			
		}*/

        return json;
    }

    private JSONObject getAllEventsArray() throws JSONException {
        JSONArray tJsonArray = new JSONArray();
        JSONObject jsonObject;

        ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().
                getRoomDB().eventdata_dao().fetchnonSynedData();
        Logger.LogError("eventDataArrayList", "" + eventDataArrayList);
        if (eventDataArrayList.size() < 1) {
            return new JSONObject().put(DSRAppConstant.KEY_DATA, tJsonArray);
        }
        for (int i = 0; i < eventDataArrayList.size(); i++) {
            EventData mCursor = eventDataArrayList.get(i);


            JSONObject json = new JSONObject();
            if (mCursor.getEvent_key() < 0) {
                json.put(DSRAppConstant.KEY_DUMMY_KEY, mCursor.getEvent_key());
                json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), "");
            } else {
                json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), mCursor.getEvent_key());
                json.put(DSRAppConstant.KEY_DUMMY_KEY, "");
            }
            // json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), mCursor.getString(columnIndexKey));
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name(), mCursor.getEvent_user_key());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type.name(), mCursor.getEvent_type());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title.name(), mCursor.getEvent_title());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name(), mCursor.getEvent_date());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.entered_on.name(), mCursor.getEntered_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name(), mCursor.getFrom_date_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name(), mCursor.getTo_date_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description.name(), mCursor.getEvent_description());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), mCursor.getStatus());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name(), mCursor.getCmkey());
            json.put(B2CAppConstant.KEY_CUST_KEY, mCursor.getCus_key());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name(), mCursor.getArea());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name(), mCursor.getLatitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name(), mCursor.getLongitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude.name(), mCursor.getAltitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time.name(), mCursor.getVisit_update_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response.name(), mCursor.getAction_response());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name(), mCursor.getPlan());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name(), mCursor.getObjective());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name(), mCursor.getStrategy());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), mCursor.getCustomer_name());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation.name(), mCursor.getPreparation());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude.name(), mCursor.getEvent_visited_latitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude.name(), mCursor.getEvent_visited_longitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude.name(), mCursor.getEvent_visited_altitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on.name(), mCursor.getCompleted_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on.name(), mCursor.getCancelled_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.anticipate.name(), mCursor.getAnticipate());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.mins_of_meet.name(), mCursor.getMinutes_of_meet());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.competition_pricing.name(), mCursor.getCompetition_pricing());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.feedback.name(), mCursor.getFeedback());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.order_taken.name(), mCursor.getOrder_taken());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.product_display.name(), mCursor.getProduct_display());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.promotion_activated.name(), mCursor.getPromotion_activated());

            Logger.LogError("mCursor.getString(columnIndexCMKey)", "" + mCursor.getCmkey());

            tJsonArray.put(json);

        }
        jsonObject = new JSONObject().put(DSRAppConstant.KEY_DATA, tJsonArray);
        return jsonObject;
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

    private boolean tryEnableVisitedButton() {
        double currentDistance = calcDistance(UNIT_METER, B2CApp.userLocData.getLatitude(), B2CApp.userLocData.getLongitude(),
                Double.parseDouble(currentEvent.getLatitude()), Double.parseDouble(currentEvent.getLongitude()));

        if (currentDistance <= 0.0 || currentDistance > 30.0) {
            tv_visit.setTextColor(getResources().getColor(R.color.DrakGray));
            isVisitedEnabled = false;
        } else {
            tv_visit.setTextColor(getResources().getColor(R.color.DrakGray));
            isVisitedEnabled = true;
        }

        return isVisitedEnabled;
    }

    private static Double _MilesToKilometers = 1.609344;
    private static Double _MilesToNautical = 0.8684;
    private final byte UNIT_MILE = 0;
    private final byte UNIT_KM = 1;
    private final byte UNIT_NMILE = 2;
    private final byte UNIT_METER = 3;

    private Double calcDistance(byte unit, double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double distance = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.cos(deg2rad(theta));

        distance = Math.acos(distance);
        distance = rad2deg(distance);
        distance = distance * 60 * 1.1515;

        if (unit == UNIT_KM)
            distance = distance * _MilesToKilometers;
        else if (unit == UNIT_NMILE)
            distance = distance * _MilesToNautical;
        else if (unit == UNIT_METER)
            distance = distance * _MilesToKilometers * 1000;

        return (distance);

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void gotoGPSSwitch() {
        Intent intent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void gotoDSRDayScreen() {

        startActivity(new Intent(this, DSRDayScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRAddProgScreen() {

        startActivity(new Intent(this, DSRAddProgScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }


    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    /*	private void syncAllToServer()
    {

        if(syncCursor==null)
        {
            syncCursor = DSRApp.dsrLdbs.fetchSelected(this, DSRTableCreate.TABLE_AERP_EVENT_MASTER, null, DSRTableCreate.COLOUMNS_EVENT_MASTER.is_synced_to_server.name(), new String[]{"0"});
            if(syncCursor==null)
            {
                Toast.makeText(this, "Server is upto date", Toast.LENGTH_LONG).show();
                return;
            }
        }

        int columnIndexKey					= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name());
        int columnIndexUserKey				= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name());
        int columnIndexType 				= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type.name());
        int columnIndexTitle				= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title.name());
        int columnIndexFromDatetime 		= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name());
        int columnIndexToDateTime 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name());
        int columnIndexEventDesc 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description.name());
        int columnIndexEventStatus 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name());
        int columnIndexCMKey 				= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name());
        int columnIndexLocation 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.location.name());
        int columnIndexLatitude 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name());
        int columnIndexLongitude 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name());
        int columnIndexAltitude 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude.name());
        int columnIndexVisitUpdateTime 		= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time.name());
        int columnIndexActionDesc 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_description.name());
        int columnIndexPlan 				= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name());
        int columnIndexObjective 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name());
        int columnIndexStrategy 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name());
        int columnIndexCustomerName 		= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name());
        int columnIndexPurpose 				= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.purpose.name());
        int columnIndexvisitedLatitude 		= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude.name());
        int columnIndexvisitedLongitude 	= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude.name());
        int columnIndexvisitedAltitude 		= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude.name());
        int columnIndexCompletedOn 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on.name());
        int columnIndexCancelledOn 			= syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on.name());

        do {


        } while (syncCursor.moveToNext());

        syncCursor.close();
        syncCursor = null;

    }*/
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {


                mLastLocation = location;
                if (mLastLocation != null) {
                    Logger.LogError("mLastLocation", "" + mLastLocation);
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

                    mLastLocation = location;
                    if (mLastLocation != null) {
                        Logger.LogError("mLastLocation edit", "" + mLastLocation);
                    }

                }

            }
        };

        startRequestLocationUpdates();
        Logger.LogError("startLocationUpdates", "startLocationUpdates");
    }

    public void stopRemoveLocationUpdates() {
        Logger.LogError("stopRemoveLocationUpdates", "stopRemoveLocationUpdates");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void startRequestLocationUpdates() {
        Logger.LogError("startRequestLocationUpdates", "startRequestLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
    }

    protected void createLocationRequest() {
        Logger.LogError("createLocationRequest", "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this,
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
                101);
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
