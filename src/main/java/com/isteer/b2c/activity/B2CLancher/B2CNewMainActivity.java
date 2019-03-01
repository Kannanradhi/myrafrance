package com.isteer.b2c.activity.B2CLancher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.action.B2CAddTodaysBeat;
import com.isteer.b2c.activity.action.B2CCollectionEntryScreen;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.activity.reports.B2CAllOrderScreen;
import com.isteer.b2c.activity.reports.B2CCollectionSumScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.activity.reports.B2c_Report_visit;
import com.isteer.b2c.adapter.ItemClickListener;
import com.isteer.b2c.adapter.RCV_DSRCustAdapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.gson.Gson_CustomerDetails;
import com.isteer.b2c.model.AttendenceData;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.utility.SlideAnimationUtil;
import com.isteer.b2c.volley.VolleyHttpRequest;
import com.isteer.b2c.volley.VolleyTaskListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by rnows on 16-Feb-18.
 */

public class B2CNewMainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener, VolleyTaskListener {
    private FrameLayout fl_main_screen;
    private Typeface raleway;
    private TextView txt_admin;
    private ImageView img_calender, img_catalogue, img_admin;
    private int height, phone_width;
    LinearLayout layoutOfPopup;
    private PopupWindow popupAdmin;
    private Dialog dialog;
    private AlertDialog alertDialog;
    public static boolean isGPSAlertShown = false;
    public static LocationData userLocData = new LocationData();
    private PopupWindow popupList;
    private EditText et_search_hint;
    private TextView txt_tit_actions, txt_payments, txt_orders, txt_beats, txt_visits, txt_tit_reports, txt_pipeline,
            txt_sales_report, txt_collection_report, txt_rep_visits, txt_sales_mtd,
            txt_val_sales_mtd, txt_collection_mtd, txt_val_collection_mtd, txt_visits_mtd, txt_val_visits_mtd,
            txt_pipeline_value, txt_val_pipeline_value;
    private TextView txt_total_object, val_visit, val_sale, val_collection, txt_month_till_date;
    private ImageView img_today_object, img_visit, img_sale, img_collection;
    private EditText et_visit, et_sale, et_collection;
    private String str_visit, str_sale, str_collection;
    private String currentDate;
    private OrderNewData orderNewData;
    public static boolean isFromMenu = false;
    private SwitchCompat sw_startday;
    private String message;
    private String left_btn;
    private TextView tv_opening, tv_visit, tv_orders, tv_collection, tv_closing;
    private String versionName;
    private String start_day;
    private SwipeRefreshLayout srl_refresh;
    private Disposable disposableCustomer;
    private ArrayList<CustomerData> customerDataArrayList;
    private RCV_DSRCustAdapter rcv_dsrcustadapter;
    private LinearLayout lt_empty;
    private RecyclerView rcv_nameEntryList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2c_new_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initVar();
        syncDayMonthReportFromServer();
     /*   FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fl_main_screen,new B2C_New_Menu_Frag()).commit();*/
    }

    @Override
    public void onBackPressed() {
        B2CApp.b2cUtils.alertDialog(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // stopDayDefaultEight();
        checkDayStartorStop();
        if (!(isGPSEnabled()) && B2CApp.b2cPreference.isDayStarted() && B2CApp.isLocationProviderDisabled) {
            alertGPSSwitch();
        }
        //  txt_val_collection_mtd.setText("" + B2CApp.b2cUtils.setGroupSeparaterEditText("" + getColectionSum()));
        //  txt_val_visits_mtd.setText("" + getVisitCount());


        currentDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());
        //setTodayObj();
        //  setTodayValues();
        // setSale();
        // setTodayPoints();
    }

    private void checkDayStartorStop() {
     /* String  started_day = B2CApp.getINSTANCE().getRoomDB().attendence_dao().start_date();
        if (!ValidationUtils.isEmpty(started_day)) {
            start_day = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                    B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat, started_day));
            String current_day = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date());
            Logger.LogError("start_day1", "" + start_day);
            Logger.LogError("current_day", "" + current_day);
            if (!start_day.equalsIgnoreCase(current_day)) {
                stopDay(started_day);
            }
        }*/

        if (B2CApp.b2cPreference.isDayStarted())
            sw_startday.setChecked(true);
        else
            sw_startday.setChecked(false);
    }

    @Override
    protected void onDestroy() {
        if (disposableCustomer != null && !(disposableCustomer.isDisposed())) {
            disposableCustomer.dispose();
        }
        super.onDestroy();
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void alertGPSSwitch() {
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Show location settings?").setTitle("GPS is disabled").setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //isFromGPS=true;
                            gotoGPSSwitch();
                        }
                    });
            alertDialog = builder.create();
        }

        if (alertDialog != null && !alertDialog.isShowing())
            alertDialog.show();
    }

    private void initVar() {
        srl_refresh = findViewById(R.id.srl_refresh);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (B2CApp.b2cUtils.isNetAvailable())
                    syncDayMonthReportFromServer();
                else
                    srl_refresh.setRefreshing(false);
            }
        });


        sw_startday = findViewById(R.id.sw_startday);
        sw_startday.setClickable(false);
        findViewById(R.id.cv_startday).setOnClickListener(this);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        phone_width = displayMetrics.widthPixels;

        //     fl_main_screen = (FrameLayout) findViewById(R.id.fl_main_screen);

        txt_admin = (TextView) findViewById(R.id.txt_admin);

        img_calender = (ImageView) findViewById(R.id.img_calender);
        img_calender.setOnClickListener(this);
        findViewById(R.id.img_admin).setOnClickListener(this);
        findViewById(R.id.img_catalogue).setOnClickListener(this);
        txt_admin = (TextView) findViewById(R.id.txt_admin);
        txt_admin.setOnClickListener(this);

        findViewById(R.id.lt_search_hint).setOnClickListener(this);


        findViewById(R.id.lt_payments).setOnClickListener(this);
        findViewById(R.id.lt_orders).setOnClickListener(this);
        findViewById(R.id.lt_beats).setOnClickListener(this);
        findViewById(R.id.lt_visits).setOnClickListener(this);
        findViewById(R.id.lt_pipeline).setOnClickListener(this);
        findViewById(R.id.lt_sales_report).setOnClickListener(this);
        findViewById(R.id.lt_collection_report).setOnClickListener(this);
        findViewById(R.id.lt_visits2).setOnClickListener(this);

        et_search_hint = (EditText) findViewById(R.id.et_search_hint);
        //  et_search_hint.setTypeface(raleway);
        et_search_hint.setClickable(false);
        et_search_hint.setFocusable(false);
        et_search_hint.setOnClickListener(this);

        txt_tit_actions = (TextView) findViewById(R.id.txt_tit_actions);
        txt_payments = (TextView) findViewById(R.id.txt_payments);
        txt_orders = (TextView) findViewById(R.id.txt_orders);
        txt_beats = (TextView) findViewById(R.id.txt_beats);
        txt_visits = (TextView) findViewById(R.id.txt_visits);
        txt_tit_reports = (TextView) findViewById(R.id.txt_tit_reports);
        txt_pipeline = (TextView) findViewById(R.id.txt_pipeline);
        txt_sales_report = (TextView) findViewById(R.id.txt_sales_report);
        txt_collection_report = (TextView) findViewById(R.id.txt_collection_report);
        txt_rep_visits = (TextView) findViewById(R.id.txt_rep_visits);
        txt_sales_mtd = (TextView) findViewById(R.id.txt_sales_mtd);
        txt_val_sales_mtd = (TextView) findViewById(R.id.txt_val_sales_mtd);
        txt_collection_mtd = (TextView) findViewById(R.id.txt_collection_mtd);
        txt_val_collection_mtd = (TextView) findViewById(R.id.txt_val_collection_mtd);
        txt_val_visits_mtd = (TextView) findViewById(R.id.txt_val_visits_mtd);
        txt_val_visits_mtd.setOnClickListener(this);
        findViewById(R.id.lt_visits_mtd).setOnClickListener(this);
        txt_visits_mtd = (TextView) findViewById(R.id.txt_visits_mtd);
        txt_pipeline_value = (TextView) findViewById(R.id.txt_pipeline_value);
        txt_val_pipeline_value = (TextView) findViewById(R.id.txt_val_pipeline_value);

        txt_val_sales_mtd = (TextView) findViewById(R.id.txt_val_sales_mtd);
        txt_val_collection_mtd = (TextView) findViewById(R.id.txt_val_collection_mtd);
        txt_val_visits_mtd = (TextView) findViewById(R.id.txt_val_visits_mtd);
        txt_val_pipeline_value = (TextView) findViewById(R.id.txt_val_pipeline_value);


        txt_total_object = (TextView) findViewById(R.id.txt_total_object);
        val_visit = (TextView) findViewById(R.id.val_visit);
        val_sale = (TextView) findViewById(R.id.val_sale);
        val_collection = (TextView) findViewById(R.id.val_collection);
        txt_month_till_date = (TextView) findViewById(R.id.txt_month_till_date);

        img_visit = (ImageView) findViewById(R.id.img_visit);
        img_sale = (ImageView) findViewById(R.id.img_sale);
        img_collection = (ImageView) findViewById(R.id.img_collection);

        findViewById(R.id.img_today_object).setOnClickListener(this);

        tv_opening = (TextView) findViewById(R.id.tv_opening);
        tv_visit = (TextView) findViewById(R.id.tv_visit);
        tv_orders = (TextView) findViewById(R.id.tv_orders);
        tv_collection = (TextView) findViewById(R.id.tv_collection);
        tv_closing = (TextView) findViewById(R.id.tv_closing);

    }

    private void gotoStartStopDay() {
        if (sw_startday.isChecked()) {
            message = getResources().getString(R.string.startday);
            left_btn = getResources().getString(R.string.start);
            startDayAlert(message, left_btn);

        } else {
            message = getResources().getString(R.string.stopday);
            left_btn = getResources().getString(R.string.stop);
            startDayAlert(message, left_btn);
        }
    }

    private void gotoStartStopDayText() {
        if (!sw_startday.isChecked()) {
            message = getResources().getString(R.string.startday);
            left_btn = getResources().getString(R.string.start);
            startDayAlert(message, left_btn);

        } else {
            message = getResources().getString(R.string.stopday);
            left_btn = getResources().getString(R.string.stop);
            startDayAlert(message, left_btn);
        }
    }

    private void gotoDSRDayScreen() {

        startActivity(new Intent(this, DSRDayScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CProductsCatalogue() {

        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCounterDetailScreen() {

        startActivity(new Intent(this, B2CCounterDetailScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void gotoGPSSwitch() {

        Intent intent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        isGPSAlertShown = true;
    }

    public void gotoCloseApp() {
        Intent main = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(main);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_calender:
                gotoDSRDayScreen();
                break;

            case R.id.cv_startday:
                gotoStartStopDayText();
                break;
            case R.id.sw_startday:
                //   gotoStartStopDay();
                break;
            case R.id.img_catalogue:
                gotoB2CProductsCatalogue();
                break;
            case R.id.lt_visits_mtd:
            case R.id.txt_val_visits_mtd:
                gotoUnvisitedB2CCustListScreen();
                break;
            case R.id.img_admin:
            case R.id.txt_admin:

                //popUpWindowAdmin();

                if (popupList != null) {
                    if (popupList.isShowing()) {
                        popupList.dismiss();
                    } else {
                        popupListDescription1(findViewById(R.id.txt_admin));
                    }
                } else {
                    popupListDescription1(findViewById(R.id.txt_admin));
                }
                break;
            case R.id.lt_payments:
                gotoB2CCollectionEntryScreen();
                break;
            case R.id.lt_orders:
                gotoB2CPendingOrders();

                break;
            case R.id.lt_beats:
                gotoB2CAddTodaysBeat();

                break;
            case R.id.lt_visits:
                gotoB2CCountersScreen();
                break;
            case R.id.lt_pipeline:
                B2CCollectionSumScreen.isFromMenu = true;
                gotoB2CCollectionSumScreen();
                break;
            case R.id.lt_sales_report:
                gotoB2CAllOrderScreen();
                break;
            case R.id.lt_collection_report:
                gotoB2CSyncScreen();
                break;
            case R.id.lt_visits2:
                gotoB2c_Report_visit();
                break;
            case R.id.lt_search_hint:
            case R.id.et_search_hint:
                isFromMenu = true;
                gotoB2CCustListScreen();
                break;

            case R.id.img_today_object:
                popUpWindowTodayobj();

                break;
            default:
                break;
        }
    }

    private void popUpWindowAdmin() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.popupwindowadmin);

        TextView txt_username = ((TextView) dialog.findViewById(R.id.txt_username));
        txt_username.setText(B2CApp.b2cPreference.getUserName());
        LinearLayout lt_logout = ((LinearLayout) dialog.findViewById(R.id.lt_logout));
        lt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoLogout();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog.dismiss();
            }
        });


    }

    private void popupListDescription1(View anchor) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwindowadmin, (ViewGroup) anchor.findViewById(R.id.rootLayout));
        int width = anchor.getWidth();
        popupList = new PopupWindow(view, FrameLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        //    popupList.setOutsideTouchable(true);
        popupList.setAnimationStyle(1);
        popupList.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupList.setTouchable(true);
        popupList.setFocusable(true);
        popupList.setOutsideTouchable(true);
        try {
            popupList.showAsDropDown(anchor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView txt_username = ((TextView) view.findViewById(R.id.txt_username));
        TextView txt_version = ((TextView) view.findViewById(R.id.txt_version));
        // Logger.LogDebug("B2CApp.b2cPreference.getUserName()",""+B2CApp.b2cPreference.getUserName());
        txt_username.setText(B2CApp.b2cPreference.getUserName());
        try {
            versionName = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionName = "-";
        }
        txt_version.setText("V-" + versionName);
        LinearLayout lt_logout = ((LinearLayout) view.findViewById(R.id.lt_logout));
        lt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    showConfirmationAlert();
                } else {
                    gotoLogout();
                }

                popupList.dismiss();
            }
        });


        popupList.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupList.dismiss();
            }
        });

        popupList.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (popupList != null) {
                        if (popupList.isShowing()) {
                            popupList.dismiss();
                        }
                    }

                    return true;
                }
                return false;
            }
        });


    }

    private void gotoLogout() {
        String message = this.getResources().getString(R.string.internetForLogout);
        AlertPopupDialog.noInternetAlert(this, message, new AlertPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight() {
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    AlertPopupDialog.dialogDismiss();
                    showConfirmationAlert();
                }
            }
        });


    }

    private void showConfirmationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Are you sure to logout the app? Please sync all the data before logout !")
                .setTitle("Alert!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                Logger.LogError("getFULLYSYNCEDTOSERVER()", "" + B2CApp.b2cPreference.getFULLYSYNCEDTOSERVER());
                                if (B2CApp.b2cPreference.getFULLYSYNCEDTOSERVER()) {
                                    clearPreferences();
                                    clearDatabase();

                                    gotoB2CLoginScreen();
                                } else {
                                    Toast.makeText(B2CNewMainActivity.this, "Data is syncing please wait...", Toast.LENGTH_SHORT).show();
                                }
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

    public void clearPreferences() {

        //  B2CApp.b2cPreference.setIsRegistered(false);

        //  B2CApp.b2cPreference.setBaseUrl("");
        // B2CApp.b2cPreference.setBaseUrl3("");        // B2CApp.b2cPreference.setBaseUrl2("");
        //  B2CApp.b2cPreference.setBaseUrl4("");
        //  B2CApp.b2cPreference.setBaseUrl5("");
        B2CApp.b2cPreference.setIsLoggedIn(false);
        B2CApp.b2cPreference.setUserId(null);
        B2CApp.b2cPreference.setUnitKey("");
        B2CApp.b2cPreference.setSekey("");
        B2CApp.b2cPreference.setToken("");


        B2CApp.b2cPreference.setIsFilledCustomers(false);
        B2CApp.b2cPreference.setIsFilledCollection(false);
        B2CApp.b2cPreference.setIsFilledPendingBills(false);
        B2CApp.b2cPreference.setIsFilledPendingOrders(false);
        B2CApp.b2cPreference.setLastIndexFilled(0);
        B2CApp.b2cPreference.setIsDbFilled(false);
        B2CApp.b2cPreference.setDBFilledTime("");
        B2CApp.b2cPreference.setIsUpdatedCollections(false);
        B2CApp.b2cPreference.setIsFilledProducts(false);


        B2CApp.b2cPreference.setUserName("");
        B2CApp.b2cPreference.setUserPass("");
        B2CApp.b2cPreference.setNewEntryCount(0);
        B2CApp.b2cPreference.setNewEntryAttendance(0);
        B2CApp.b2cPreference.setLastIndexFilled(0);
        B2CApp.b2cPreference.setIsDayStarted(false);
        B2CApp.b2cPreference.setCheckedIn(false);
        B2CApp.b2cPreference.setCHECKEDINCMKEY(0);
        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(0);
        B2CApp.b2cPreference.setCHECKEDINCUSKEY(0);
        B2CApp.b2cPreference.setTodayobjVisit(0);
        B2CApp.b2cPreference.setTodayobjCurrentdate(0);

        B2CSyncScreen.isSyncInProgress = false;


    }

    public void clearDatabase() {
        B2CApp.getINSTANCE().getRoomDB().attendence_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().billData_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().creditData_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().customerData_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().customerindividual_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().eventdata_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().locationData_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().productData_dao().clearTable();
        B2CApp.getINSTANCE().getRoomDB().spancopdata_dao().clearTable();


    }

    private void gotoB2CLoginScreen() {

        startActivity(new Intent(this, B2CLoginScreen.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.LogDebug("location", "" + location);
        if (location != null) {
            Logger.LogDebug("getAltitude", "" + location.getAltitude());
            Logger.LogDebug("getLongitude", "" + location.getLongitude());
            Logger.LogDebug("getLatitude", "" + location.getLatitude());
            userLocData.setAltitude(location.getAltitude());
            userLocData.setLongitude(location.getLongitude());
            userLocData.setLatitude(location.getLatitude());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public double getColectionSum() {

        String start_date = "" + DateFormat.format(B2CAppConstant.dateFormat5, new Date().getTime());
        currentDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());

        Double collectionSum = B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().getCollectionSum(start_date + "01", currentDate);


        //    Log.e("collectionSum", "" + collectionSum);
        return collectionSum == null ? 0 : collectionSum;
    }

    public double getColectiontodaysSum() {
        String todaysDate = "" + ((DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime())));
        //  todaysDate = "'2018%'";
        Double collectionTodaySum = B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().getColectiontodaysSum("" + todaysDate);
        //    Log.e("collectionTodaySum", "" + collectionTodaySum);
        /*Cursor mCursor = B2CApp.b2cLdbs.fetchCollectionSumByDate(getActivity(),
                todaysDate);*/


        return collectionTodaySum == null ? 0 : collectionTodaySum;
    }

    public long getVisitCount() {
        String start_date = "" + DateFormat.format(B2CAppConstant.dateFormat5, new Date().getTime());
        currentDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());

        Long getVisitCount = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().getMTDVisitCount(start_date + "01", currentDate);


        return getVisitCount == null ? 0 : getVisitCount;
    }

    public long getTodayVisitCount() {
        String todateDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());
        //   Log.e("todateDate", "" + todateDate);
        Long getVisitCount = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().getVisitCount(todateDate + "%");


        return getVisitCount == null ? 0 : getVisitCount;
    }

    public long getAllEventsCount() {
        String start_date = "" + DateFormat.format(B2CAppConstant.dateFormat5, new Date().getTime());
        currentDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());
        Long getVisitCount = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().getAllEventsCount(start_date + "01", currentDate);


        return getVisitCount == null ? 0 : getVisitCount;
    }


    private void gotoB2CCollectionSumScreen() {

        startActivity(new Intent(this, B2CCollectionSumScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCountersScreen() {

        startActivity(new Intent(this, B2CCountersScreen.class)
                .putExtra(B2CAppConstant.BACKTOCOUNTERSCREEN, B2CAppConstant.MENU_SCREEN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void gotoB2CAddTodaysBeat() {

        B2CAddTodaysBeat.toRefresh = true;
        startActivity(new Intent(this, B2CAddTodaysBeat.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CPendingOrders() {

        startActivity(new Intent(this, B2CPendingOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        SlideAnimationUtil.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void gotoB2CCustListScreen() {

        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoUnvisitedB2CCustListScreen() {
        //    popUpUnvisitedCustomer();
        syncUnvisitCustomersFromServer();

    }

    private void gotoB2c_Report_visit() {

        startActivity(new Intent(this, B2c_Report_visit.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CAllOrderScreen() {

        startActivity(new Intent(this, B2CAllOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCollectionEntryScreen() {

        startActivity(new Intent(this, B2CCollectionEntryScreen.class)
                .putExtra("backPress", B2CAppConstant.MENU_SCREEN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void gotoB2CSyncScreen() {

        startActivity(new Intent(this, B2CSyncScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }


    private JSONObject getJsonInput() {
        JSONObject json = new JSONObject();
        try {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(DSRAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(DSRAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void popUpWindowTodayobj() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialog.setContentView(R.layout.popupwindowtodaysobj);

        et_visit = ((EditText) dialog.findViewById(R.id.et_visit));
        et_sale = ((EditText) dialog.findViewById(R.id.et_sale));
        et_collection = ((EditText) dialog.findViewById(R.id.et_collection));
        if (B2CApp.b2cPreference.getTodayobjVisit() != 0 || B2CApp.b2cPreference.getTodayobjSale() != 0
                || B2CApp.b2cPreference.getTodayobjCollection() != 0) {
            et_visit.setText("" + B2CApp.b2cPreference.getTodayobjVisit());
            et_sale.setText("" + B2CApp.b2cPreference.getTodayobjSale());
            et_collection.setText("" + B2CApp.b2cPreference.getTodayobjCollection());
        }
        ImageView btn_pop_close = ((ImageView) dialog.findViewById(R.id.btn_pop_close));
        btn_pop_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        Button btn_pop_done = ((Button) dialog.findViewById(R.id.btn_pop_done));
        btn_pop_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todayObjValidation();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog.dismiss();
            }
        });


    }

    private void todayObjValidation() {
        str_visit = et_visit.getText().toString();
        str_sale = et_sale.getText().toString();
        str_collection = et_collection.getText().toString();
        String todaysDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());

        B2CApp.b2cPreference.setTodayobjVisit(Integer.parseInt(str_visit.isEmpty() ? "0" : str_visit));
        B2CApp.b2cPreference.setTodayobjSale(Integer.parseInt(str_sale.isEmpty() ? "0" : str_sale));
        B2CApp.b2cPreference.setTodayobjCollection(Integer.parseInt(str_collection.isEmpty() ? "0" : str_collection));
        //  B2CApp.b2cPreference.setTodayobjCurrentdate(Integer.parseInt(todaysDate));


        //  setTodayObj();


    }

    private void setTodayValues() {
        //    Log.e("getColectiontodaysSum()", "" + getColectiontodaysSum());
        // val_visit.setText("" + getTodayVisitCount() + "/" + B2CApp.b2cPreference.getTodayobjVisit());
        //  val_sale.setText("" + B2CApp.b2cUtils.setGroupSeparaterEditText(setTodaySale()) + "/" + B2CApp.b2cUtils.setGroupSeparaterEditText("" + B2CApp.b2cPreference.getTodayobjSale()));
        // val_collection.setText("" + B2CApp.b2cUtils.setGroupSeparaterEditText("" + getColectiontodaysSum()) + "/" + B2CApp.b2cUtils.setGroupSeparaterEditText("" + B2CApp.b2cPreference.getTodayobjCollection()));
    }

    private void setTodayPoints() {
        tv_opening.setText("" + B2CApp.b2cPreference.getOpeningPoints());
        tv_visit.setText("" + getTodayVisitCount());
        tv_orders.setText("" + setTodaySale());
        tv_collection.setText("" + getColectiontodaysSum());
        tv_closing.setText("0");
    }

    private void setTodayObj() {

        //   Log.e("currentDate", "" + currentDate);
        //   Log.e("B2CApp.b2cPreference.getTodayobjCurrentdate()", "" + B2CApp.b2cPreference.getTodayobjCurrentdate());
        if (B2CApp.b2cPreference.getTodayobjCurrentdate() == Integer.parseInt(currentDate)) {
            setTodayValues();
            //      Log.e("getTodayVisitCount()", "" + getTodayVisitCount());
            //      Log.e("currentDate", "" + currentDate);
            //      Log.e(" B2CApp.b2cPreference.getTodayobjVisit()", "" + B2CApp.b2cPreference.getTodayobjVisit());
            if (getTodayVisitCount() >= B2CApp.b2cPreference.getTodayobjVisit()) {
                img_visit.setVisibility(View.VISIBLE);
            } else {
                img_visit.setVisibility(View.INVISIBLE);
            }
            if (Double.parseDouble((setTodaySale())) >= B2CApp.b2cPreference.getTodayobjSale()) {
                img_sale.setVisibility(View.VISIBLE);
            } else {
                img_sale.setVisibility(View.INVISIBLE);
            }
            if (getColectiontodaysSum() >= B2CApp.b2cPreference.getTodayobjCollection()) {
                img_collection.setVisibility(View.VISIBLE);
            } else {
                img_collection.setVisibility(View.INVISIBLE);
            }
        } else {
            B2CApp.b2cPreference.setTodayobjVisit(0);
            B2CApp.b2cPreference.setTodayobjSale(0);
            B2CApp.b2cPreference.setTodayobjCollection(0);
            val_visit.setText("0" + "/" + B2CApp.b2cPreference.getTodayobjVisit());
            val_sale.setText("0" + "/" + B2CApp.b2cPreference.getTodayobjSale());
            val_collection.setText("0" + "/" + B2CApp.b2cPreference.getTodayobjCollection());
            img_visit.setVisibility(View.INVISIBLE);
            img_sale.setVisibility(View.INVISIBLE);
            img_collection.setVisibility(View.INVISIBLE);
        }
    }

    private void setSale() {
        // Cursor mCursor = B2CApp.b2cLdbs.fetchtoalSale(getActivity(), "");
        String start_date = "" + DateFormat.format(B2CAppConstant.dateFormat5, new Date().getTime());
        currentDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());
        ArrayList<OrderNewData> orderNewDataArrayList = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().getMTDSale(start_date + "01", currentDate);
        if (orderNewDataArrayList.size() > 0) {
            //   Log.e("orderNewDataArrayList", "" + orderNewDataArrayList.size());
            txt_val_sales_mtd.setText("" + B2CApp.b2cUtils.setGroupSeparaterEditText("" + calculateOrderValue(orderNewDataArrayList)));
            Logger.LogError(" (start_date)", "" + (start_date));
            Logger.LogError(" (orderNewDataArrayList)", "" + (orderNewDataArrayList));
            Logger.LogError(" calculateOrderValue(orderNewDataArrayList)", "" + calculateOrderValue(orderNewDataArrayList));

        }


    }

    private String setTodaySale() {
        ArrayList<OrderNewData> orderNewDataArrayList = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().gettotalSale("" + currentDate + "%");
        //   Log.e("orderNewDataArrayList", "" + orderNewDataArrayList.size());
        return orderNewDataArrayList.size() <= 0 ? "0" : ("" + calculateOrderValue(orderNewDataArrayList));

    }


    public String calculateOrderValue(ArrayList<OrderNewData> mCursor) {
        double orderValue = 0;

        if (mCursor == null)
            return "0";
        for (int i = 0; i < mCursor.size(); i++) {
            orderNewData = mCursor.get(i);


            String qtyStr = orderNewData.getQuantity() != null ? orderNewData.getQuantity() : "0";
            String lpStr = orderNewData.getList_price() != null ? orderNewData.getList_price() : "0.0";
            String tpStr = orderNewData.getList_price() != null ? orderNewData.getTaxPercent() : "0.0";

            int qty = Integer.parseInt(qtyStr);
            double lp = Double.parseDouble(lpStr);
            double tp = Double.parseDouble(tpStr);

            //  Log.e("calculateOrderValue", " pending_qty : " + qty + " -list_price : " + lp + " -taxPercent : " + tp);

            orderValue += qty * lp;
            //   Log.e("orderValue", " : " + orderValue);
            //   orderValue += qty * lp * ((100 + tp) / 100);

            //  Log.e("orderValue", " : " + orderValue);


        }
        return ("" + orderValue);
    }

    public void startDayAlert(String message, String left_btn) {

        String title = B2CAppConstant.alert;

        String right_btn = getResources().getString(R.string.cancel);


        AlertPopupDialog alertPopupDialog = new AlertPopupDialog(this, title, message, left_btn, right_btn,
                new AlertPopupDialog.myOnClickListenerLeft() {
                    @Override
                    public void onButtonClickLeft() {
                        if (message == getResources().getString(R.string.startday)) {
                            sw_startday.setChecked(true);
                            initAlarmForStartDay();

                        } else {
                            sw_startday.setChecked(false);
                            initAlarmForStopDay();
                        }
                        AlertPopupDialog.dialogDismiss();


                    }
                }, new AlertPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight() {
                if (B2CApp.b2cPreference.isDayStarted()) {
                    sw_startday.setChecked(true);
                } else
                    sw_startday.setChecked(false);
                AlertPopupDialog.dialogDismiss();


            }
        });
    }


    private void stopDay(String start_day) {
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
            Toast.makeText(this, "Day stopped", Toast.LENGTH_LONG).show();
            Logger.LogError("auto", "Day stopped");

        } else {
            B2CApp.b2cPreference.setIsDayStarted(false);
            Logger.LogError("auto", "Day Not stopped");
        }


    }


    @Override
    public void handleResult(String method_name, JSONObject response) throws JSONException {
        if (method_name == DSRAppConstant.METHOD_GET_DAYANDMONTH_REPORT) {
            onPostDayMonthReport(response);
        } else if (method_name == DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG) {
            onPostATTENDENCE_LOG(response.toString());
        } else if (method_name == B2CAppConstant.METHOD_GETUNVISITEDCUSTOMER) {
            onPostUnvisitedCustomer(response.toString());
            B2CApp.b2cUtils.dismissMaterialProgress();
        }

    }


    @Override
    public void handleError(VolleyError e) {

        B2CApp.b2cUtils.dismissMaterialProgress();
        Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();

    }

    public void syncAttenToServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                    + DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG, getJsonParams(DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG), DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncDayMonthReportFromServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                    + DSRAppConstant.METHOD_GET_DAYANDMONTH_REPORT, getJsonParams(DSRAppConstant.METHOD_GET_DAYANDMONTH_REPORT), DSRAppConstant.METHOD_GET_DAYANDMONTH_REPORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncUnvisitCustomersFromServer() {
        //
        B2CApp.b2cUtils.updateMaterialProgress(this, "Loading Unvisited Customers");


        VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_GETUNVISITEDCUSTOMER, getJsonInput(), B2CAppConstant.METHOD_GETUNVISITEDCUSTOMER);
    }

    private JSONObject getJsonParams(String method_name) throws JSONException {

        JSONObject ojson = new JSONObject();

        if (method_name == DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG) {
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
        } else if (method_name == DSRAppConstant.METHOD_GET_DAYANDMONTH_REPORT) {
            ojson.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            ojson.put(DSRAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            ojson.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());
        }


        return ojson;

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
                Toast.makeText(this, "" + responseJson.getString(DSRAppConstant.KEY_MSG), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPostDayMonthReport(JSONObject response) {
        try {


            if (response.getInt(B2CAppConstant.KEY_STATUS) == 1 && response.has(B2CAppConstant.KEY_DATA)) {
                JSONArray jsonArray = response.getJSONArray(B2CAppConstant.KEY_DATA);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String today_visit = jsonObject.getString(B2CAppConstant.TODAY_VISIT);
                String monthly_visit = jsonObject.getString(B2CAppConstant.MONTHLY_VISIT);
                String today_collection = jsonObject.getString(B2CAppConstant.TODAY_COLLECTION);
                String monthly_collection = jsonObject.getString(B2CAppConstant.MONTHLY_COLLECTION);
                String today_sales = jsonObject.getString(B2CAppConstant.TODAY_SALES);
                String monthly_sales = jsonObject.getString(B2CAppConstant.MONTHLY_SALES);

                val_visit.setText(today_visit + "/" + B2CApp.b2cPreference.getTodayobjVisit());
                val_sale.setText(B2CApp.b2cUtils.setGroupSeparaterEditText(today_sales) + "/" + B2CApp.b2cPreference.getTodayobjSale());
                val_collection.setText(B2CApp.b2cUtils.setGroupSeparaterEditText(today_collection) + "/" + B2CApp.b2cPreference.getTodayobjCollection());
                txt_val_sales_mtd.setText(B2CApp.b2cUtils.setGroupSeparaterEditText(monthly_sales));
                txt_val_collection_mtd.setText(B2CApp.b2cUtils.setGroupSeparaterEditText(monthly_collection));
                txt_val_visits_mtd.setText((monthly_visit));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        srl_refresh.setRefreshing(false);
    }

    private void onPostUnvisitedCustomer(String response) {

        Gson_CustomerDetails gson_customerDetails = new Gson().fromJson(response, Gson_CustomerDetails.class);
        if (gson_customerDetails.getCustomerDataList().size() == 0) {

            Toast.makeText(this, "" + gson_customerDetails.getMsg(), Toast.LENGTH_SHORT).show();
        } else {

            Observable observable = Observable.fromArray(gson_customerDetails)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());


            disposableCustomer = observable.subscribe(new Consumer() {
                @Override
                public void accept(Object o) throws Exception {
                    customerDataArrayList = ((Gson_CustomerDetails) o).getCustomerDataList();
                    popUpUnvisitedCustomer();
                }

            });
        }
    }

    private void suggestList() {


        if (customerDataArrayList.size() < 1) {
            lt_empty.setVisibility(View.VISIBLE);
        } else {
            lt_empty.setVisibility(View.GONE);
        }
        rcv_dsrcustadapter = new RCV_DSRCustAdapter(this, customerDataArrayList, new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                B2CApp.b2cUtils.updateMaterialProgress(B2CNewMainActivity.this, B2CAppConstant.LOADING);
                dialog.dismiss();
                CustomerData customerData = customerDataArrayList.get(position);
                OrderNewData orderNewData = new OrderNewData();
                Logger.LogError("customerDataArrayList", "" + customerDataArrayList.size());
                Logger.LogError("getCus_key", "" + customerData.getCus_key());
                Logger.LogError("getCmkey", "" + customerData.getCmkey());
                orderNewData.setCmkey("" + customerData.getCmkey());
                orderNewData.setCus_key(customerData.getCus_key());
                B2CCounterDetailScreen.currentMap = orderNewData;
                B2CCounterDetailScreen.toUpdateCounterDetail = true;
                gotoB2CCounterDetailScreen();
            }
        });

        Logger.LogError("rcv_dsrcustadapter", "" + rcv_dsrcustadapter.getItemCount());
        rcv_dsrcustadapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Logger.LogError("test", "adpterchanged");
            }
        });


        rcv_nameEntryList.setAdapter(rcv_dsrcustadapter);


        B2CApp.b2cUtils.dismissMaterialProgress();

    }

    private void popUpUnvisitedCustomer() {

        dialog = new Dialog(B2CNewMainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_area_name);
        dialog.setCanceledOnTouchOutside(false);

        lt_empty = dialog.findViewById(R.id.lt_empty);
        rcv_nameEntryList = dialog.findViewById(R.id.rcv_area_name);
        rcv_nameEntryList.setLayoutManager(new LinearLayoutManager(this));

        suggestList();
        //syncUnvisitCustomersFromServer();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        dialog.show();
    }

    public void initAlarmForStartDay() {
        if (B2CApp.b2cPreference.getUserId() != null && B2CApp.b2cPreference.getBaseUrl() != null)

        {

            Logger.LogError("AlarmReceiver", "startdayAlarm");

            startDay();

        }


    }

    private void initAlarmForStopDay() {
        if (B2CApp.b2cPreference.getBaseUrl() != null
                && B2CApp.b2cPreference.getUserId() != null) {

            Logger.LogError("AlarmReceiver", "stopdayAlarm");
            stopDay();


        }
    }

    private void startDay() {
        B2CApp.getINSTANCE().startLocationUpdates();
        B2CApp.getINSTANCE().startAlarm();
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
                syncAttenToServer();
                // new AlarmReceiver.SyncTaskToServer().execute(B2CApp.b2cPreference.getBaseUrl() + DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG);
            }
            B2CApp.b2cPreference.setStartDateKey(longinsertatten);
            B2CApp.b2cPreference.setIsDayStarted(true);
            Toast.makeText(this, "Day Started", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Day Not Started Try After Some Time", Toast.LENGTH_LONG).show();
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
                syncAttenToServer();
                // new AlarmReceiver.SyncTaskToServer().execute(B2CApp.b2cPreference.getBaseUrl() + DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG);
            }
            B2CApp.b2cPreference.setIsDayStarted(false);
            Toast.makeText(this, "Day stopped", Toast.LENGTH_LONG).show();
            Logger.LogError("AlarmReceiver", "Day stopped");

        } else {
            B2CApp.b2cPreference.setIsDayStarted(false);
            Logger.LogError("AlarmReceiver", "Day Not stopped");
        }

        B2CApp.getINSTANCE().stopRemoveLocationUpdates();
    }

    public void gotoB2capp() {
        Intent in = new Intent(this, B2CApp.class);
        startActivity(in);
    }


}
