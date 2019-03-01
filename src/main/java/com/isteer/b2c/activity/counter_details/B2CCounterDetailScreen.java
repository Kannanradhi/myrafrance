package com.isteer.b2c.activity.counter_details;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.activity.action.B2CCollectionEntryScreen;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.calender.DSRAddProgScreen;
import com.isteer.b2c.activity.calender.DSREditProgScreen;
import com.isteer.b2c.activity.reports.B2CAllOrderScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.adapter.RCVPendingBillList;
import com.isteer.b2c.adapter.RCV_PendingOrderList;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.dao.BillData_DAO;
import com.isteer.b2c.dao.PendingOrderData_DAO;
import com.isteer.b2c.model.BillData;
import com.isteer.b2c.model.CreditData;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;
import com.isteer.b2c.model.StockData;
import com.isteer.b2c.receiver.AppLocationService;
import com.isteer.b2c.repository.CounterPenBillVM;
import com.isteer.b2c.repository.CounterPenOrderVM;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.BitmapConverter;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.utility.ValidationUtils;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class B2CCounterDetailScreen extends AppCompatActivity implements OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private TextView header_title;
    private PopupWindow ppWindowOrder, ppWindowStock;
    private Dialog popupdialog;
    private PopupHolderStock popupHolderStock;
    private FrameLayout bg_dim_layout;
    private ImageView btn_header_right;

    public static boolean toUpdateCounterDetail = false;
    public static boolean isLocationsUpdated = false;
    public static OrderNewData currentMap = new OrderNewData();
    public PendingOrderData currentPendingOrder = new PendingOrderData();

    private TextView tv_counter_name, tv_contact_name, tv_tin_no, tv_contact_phone, tv_counter_address, tv_total_credit;
    public TextView tv_avail_credit, tv_tabmenu4, tv_credit_days, tv_over_due;
    private Button btn_call_conter, btn_sms_counter;
    private ListView pendingBillList, pendingOrderList;
    private RecyclerView rcv_pendingBillList, rcv_pendingOrderList;
    public static CustomerData currentCustomer = new CustomerData();
    public static CreditData currentCredit = new CreditData();
    public static LocationData currentCustomerLoc = new LocationData();
    public static String currentContactIndKey = "";

    public static ArrayList<BillData> billEntries = new ArrayList<BillData>();
    public static ArrayList<PendingOrderData> orderEntries = new ArrayList<PendingOrderData>();

    //	private B2CPendingBillAdapter pendingBillAdaptor;
    private RCVPendingBillList pendingBillAdaptor;
    //private B2CPendingOrderTableAdapter pendingOrderAdaptor;
    private RCV_PendingOrderList pendingOrderAdaptor;
    private PopupHolderOrder popupHolderOrder;

    private static ProgressDialog pdialog;
    private static String PROGRESS_MSG = "Updating order...";

    private static boolean isCounterEnabled;
    private ArrayList<StockData> stocksInBranch = new ArrayList<StockData>();
    public B2CStockAdapter stockAdapter;
    private TextView btn_outstanding, txt_pending_orders;
    private TextView txt_visits, txt_order;
    ImageView iv_check, iv_order;
    private PendingOrderData_DAO pendingOrderData_dao;
    private CounterPenOrderVM counterPenOrderVM;
    private CounterPenBillVM counterPenBillVM;
    private BillData_DAO billData_dao;
    private AppLocationService appLocationService;
    private Double currentDistance;

    private double latitudeNW, longitudeNW;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;

    private AlertDialog alertDialog;
    public static boolean isGPSAlertShown = false;
    private String title, message;
    private BitmapConverter imageConverter = new BitmapConverter();

    private ImageView pending_invoice_share, pending_order_share;
    private String purchase_order_type;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;


    private static class PopupHolderOrder {

        public TextView tv_pop_part_no;
        public TextView tv_pop_manu;
        public TextView tv_pop_description;
        public TextView tv_pop_lprice;
        public TextView tv_pop_mrp;
        public TextView tv_pop_order_qty;
        public TextView tv_pop_pend_qty;
        public TextView tv_pop_date;
        public TextView tv_pop_avail;
        public Button btn_pop_cancel;
        public Button btn_pop_send;
        public ImageView btn_close_popup;

    }

    private static class PopupHolderStock {

        public ListView listview_stock;
        public View btn_pop_done;
        public View btn_pop_close;

    }

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.scr_b2c_counter_detail);
        pendingOrderData_dao = B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao();
        counterPenOrderVM = ViewModelProviders.of(this).get(CounterPenOrderVM.class);
        billData_dao = B2CApp.getINSTANCE().getRoomDB().billData_dao();
        counterPenBillVM = ViewModelProviders.of(this).get(CounterPenBillVM.class);
        initVar();


        createLocationRequest();
    }


    @Override
    protected void onResume() {

        super.onResume();
        B2CApp.b2cUtils.dismissMaterialProgress();

       /* Logger.LogError("getCHECKEDINEVENTKEY()", "" + B2CApp.b2cPreference.getCHECKEDINEVENTKEY());
        Logger.LogError("currentMaparea", "" + currentMap.getArea());
        Logger.LogError("getCus_key", "" + currentMap.getCus_key());
          Logger.LogError("getEvent_key", "" + Integer.parseInt(currentMap.getEvent_key()));
        Logger.LogError("getCustomer_key", "" + currentMap.getCustomer_key());
        Logger.LogError("getCheckineventkey()", "" + getCheckineventkey(currentCustomer.getCmkey()));

        //  tv_counter_name.setText(currentMap.getCustomer_name());*/

        if (toUpdateCounterDetail) {
            toUpdateCounterDetail = false;
            updateCounterDetails();
            //  tryEnableCounterOrderButton();
        }
        purchase_order_type = "Phone Order";
        setCheckinColor();
    }

    private void setCheckinColor() {
        if ((B2CApp.b2cPreference.getCHECKEDINCMKEY() == (currentCustomer.getCmkey())) &&
                (currentCustomer.getCmkey() != null) && (getCheckineventkey(currentCustomer.getCmkey()) != 0)) {
            txt_visits.setText("CheckOut");
            txt_visits.setTextColor(getResources().getColor(R.color.red));
            txt_order.setTextColor(getResources().getColor(R.color.red));

            iv_check.setBackgroundResource(R.drawable.round_red);
            iv_order.setBackgroundResource(R.drawable.round_red);

            purchase_order_type = "Phone Order";


        } else {
            purchase_order_type = "Phone Order";
            txt_visits.setText("CheckIn");

            txt_visits.setTextColor(getResources().getColor(R.color.light_blue));
            txt_order.setTextColor(getResources().getColor(R.color.light_blue));

            iv_check.setBackgroundResource(R.drawable.round_lightblue);
            iv_order.setBackgroundResource(R.drawable.round_lightblue);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);

        pending_invoice_share = findViewById(R.id.pending_invoice_share);
        pending_invoice_share.setOnClickListener(this);

        pending_order_share = findViewById(R.id.pending_order_share);
        pending_order_share.setOnClickListener(this);

        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Counter Detail");

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);

        ((TextView) findViewById(R.id.tv_tabmenu1)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_tabmenu2)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_tabmenu3)).setOnClickListener(this);

        ((LinearLayout) findViewById(R.id.lt_tabmenu1)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.lt_tabmenu2)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.lt_tabmenu3)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.lt_tabmenu4)).setOnClickListener(this);

        tv_tabmenu4 = (TextView) findViewById(R.id.tv_tabmenu4);
        tv_tabmenu4.setOnClickListener(this);

        txt_visits = findViewById(R.id.txt_visits);
        txt_order = findViewById(R.id.txt_order);
        iv_check = findViewById(R.id.iv_check);
        iv_order = findViewById(R.id.iv_order);

        btn_outstanding = ((TextView) findViewById(R.id.btn_outstanding));
        //btn_outstanding.setOnClickListener(this);

        txt_pending_orders = ((TextView) findViewById(R.id.txt_pending_orders));

        //  (findViewById(R.id.btnLocate)).setOnClickListener(this);
        (findViewById(R.id.btn_call_conter)).setOnClickListener(this);
        (findViewById(R.id.btn_sms_counter)).setOnClickListener(this);

        ((View) findViewById(R.id.bottombar_one)).setOnClickListener(this);
        ((View) findViewById(R.id.bottombar_two)).setOnClickListener(this);
        ((View) findViewById(R.id.bottombar_three)).setOnClickListener(this);
        ((View) findViewById(R.id.bottombar_four)).setOnClickListener(this);
        ((View) findViewById(R.id.bottombar_five)).setOnClickListener(this);

        tv_counter_name = (TextView) findViewById(R.id.tv_counter_name);
        tv_contact_name = (TextView) findViewById(R.id.tv_contact_name);
        tv_tin_no = (TextView) findViewById(R.id.tv_tin_no);
        tv_contact_phone = (TextView) findViewById(R.id.tv_contact_phone);
        tv_counter_address = (TextView) findViewById(R.id.tv_counter_address);
        tv_total_credit = (TextView) findViewById(R.id.tv_total_credit);
        tv_avail_credit = (TextView) findViewById(R.id.tv_avail_credit);
        tv_credit_days = (TextView) findViewById(R.id.tv_credit_days);
        tv_over_due = (TextView) findViewById(R.id.tv_over_due);


        //pendingBillList = (ListView)findViewById(R.id.pendingBillList);
        rcv_pendingBillList = (RecyclerView) findViewById(R.id.rcv_pendingBillList);
        rcv_pendingBillList.setLayoutManager(new LinearLayoutManager(this));
        rcv_pendingBillList.setNestedScrollingEnabled(false);
        //	pendingOrderList = (ListView)findViewById(R.id.pendingOrderList);
        rcv_pendingOrderList = (RecyclerView) findViewById(R.id.rcv_pendingOrderList);
        rcv_pendingOrderList.setLayoutManager(new LinearLayoutManager(this));
        rcv_pendingOrderList.setNestedScrollingEnabled(false);

        bg_dim_layout = (FrameLayout) findViewById(R.id.bg_dim_layout);
        bg_dim_layout.getForeground().setAlpha(0);

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
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            int backPress = bundle.getInt(B2CAppConstant.BACKTOCOUNTERDETAILS);
            Logger.LogError("backPress", "" + backPress);
            if (backPress == B2CAppConstant.ADDTOBEATPLAN) {
                gotoB2CCountersScreen();
            } else if (backPress == B2CAppConstant.ACTIONORDER) {
                gotoB2CPendingOrderScreen();
            } else if (backPress == B2CAppConstant.REPORTORDER) {
                gotoB2CAllOrderScreen();
            } else if (backPress == B2CAppConstant.CUSTOMERSEARCH) {
                gotoB2CCustListScreen();
            }
        } else {
            gotoDSRMenuScreen();
        }
    }

    @Override
    public void onClick(View pView) {

        switch (pView.getId()) {

            case R.id.btn_outstanding:
                if (B2CApp.b2cUtils.isNetAvailable()) {

                    // gotoB2CDynamicWebApp(B2CAppConstant.OPTYPE_PENDING_BILLS);
                } else {
                    Toast.makeText(B2CCounterDetailScreen.this, "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_header_right:
                gotoB2CProductsCatalogue();
                break;
            case R.id.img_back:
                goBack();
                break;
            case R.id.img_home:
                gotoB2CMenuScreen();
                break;
            case R.id.lt_tabmenu1:
                B2CLocateScreen.toRefresh = true;


                if ((mLastLocation == null || mLastLocation.getLatitude() + mLastLocation.getLongitude() == 0)) {

                    alertGPSSwitch(1);

                    return;
                } else {

                    gotoB2CLocateScreen();
                }


                break;

            case R.id.lt_tabmenu2:
                collectionEntryClicked(false, "");
                break;

            case R.id.lt_tabmenu3:
                B2CApp.b2cUtils.updateMaterialProgress(this, B2CAppConstant.LOADING);
                B2CPlaceOrderScreen.isCounterOrder = false;
                gotoDSRPlaceOrderScreen();
                break;

            case R.id.lt_tabmenu4:
                // Logger.LogError("Latitude:LongitudeCLICK", +mLastLocation.getLatitude() + "" + mLastLocation.getLongitude());
                if (txt_visits.getText().toString().equalsIgnoreCase("CheckIn")
                        && B2CApp.b2cPreference.getCheckedIn() &&
                        B2CApp.b2cPreference.getCHECKEDINCMKEY() != (currentCustomer.getCmkey())) {
                    alertForCheckout();
                    //   alertUserForCheckout(this, "Alert !", "Please CheckOut " + getCompanuName(), "OK");
                    //	Toast.makeText(this, "Please CheckOut Previous Customer", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mLastLocation != null) {
                    Logger.LogError("Latitude:LongitudeCLICK", +mLastLocation.getLatitude() + "" + mLastLocation.getLongitude());
                }


                if (!B2CApp.b2cPreference.getCheckedIn() && txt_visits.getText().toString().equalsIgnoreCase("CheckIn")) {

                    if ((mLastLocation == null || mLastLocation.getLatitude() + mLastLocation.getLongitude() == 0)) {
//                    Logger.LogError("Latitude:LongitudeCLICK", +mLastLocation.getLatitude() + "" + mLastLocation.getLongitude());
                        alertGPSSwitch(1);
                        //   alertUserP(this, "Alert ! ", "Your location is not available\nPlease turn on GPS", "Ok");
                        return;
                    } else if ((isGPSEnabled()) && (mLastLocation == null || mLastLocation.getLatitude() + mLastLocation.getLongitude() == 0)) {
                        alertGPSSwitch(2);
                        startLocationUpdates();
                        if (mLastLocation == null) {
                            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                    tryEnableCounterOrderButton();
                    if (currentCustomerLoc.getLatitude() + currentCustomerLoc.getLongitude() == 0) {
                        Toast.makeText(this, "Customer Location not avaliable", Toast.LENGTH_SHORT).show();
                        gotoB2CLocateScreen();
                        return;
                    }


                    if (!isCounterEnabled) {
                        alertUserP(this, "Alert !", "Your location is too far to visit customer right now \n( " + (currentDistance.intValue()) + " meters )", "Ok");
                        return;
                    }
                    purchase_order_type = "Counter Order";
                    txt_visits.setText("CheckOut");
                    txt_visits.setTextColor(getResources().getColor(R.color.red));
                    txt_order.setTextColor(getResources().getColor(R.color.red));

                    iv_check.setBackgroundResource(R.drawable.round_red);
                    iv_order.setBackgroundResource(R.drawable.round_red);

                    B2CApp.b2cPreference.setCheckedIn(true);
                    B2CApp.b2cPreference.setCHECKEDINCMKEY((currentCustomer.getCmkey()));
                    B2CApp.b2cPreference.setCHECKEDINCUSKEY(Integer.parseInt((currentCustomer.getCus_key())));
                    checkEvent();
                } else if (txt_visits.getText().toString().equalsIgnoreCase("CheckOut") && B2CApp.b2cPreference.getCheckedIn()
                        && B2CApp.b2cPreference.getCHECKEDINCMKEY() == (currentCustomer.getCmkey())) {
                    DSREditProgScreen.toRefresh = true;
                    Logger.LogError("getCheckineventkey()", "" + getCheckineventkey(currentCustomer.getCmkey()));
                    DSREditProgScreen.eventKey = "" + getCheckineventkey(currentCustomer.getCmkey());
                    gotoBDSREditProgScreen();
				/*B2CApp.b2cPreference.setCheckedIn(false);
				B2CApp.b2cPreference.setCHECKEDINCMKEY(0);*/
                } else if (txt_visits.getText().toString().equalsIgnoreCase("CheckIn")
                        && B2CApp.b2cPreference.getCheckedIn() && B2CApp.b2cPreference.getCHECKEDINCMKEY() == (currentCustomer.getCmkey())) {

                    alertUserForCheckout(this, "Alert !", " " + getCompanuName() + " already CheckIn. Are you sure to checkout  ", "OK");
                    //	Toast.makeText(this, "Please CheckOut Previous Customer", Toast.LENGTH_SHORT).show();
                }

			/*if(isCounterEnabled)
			{
				*//*B2CPlaceOrderScreen.isCounterOrder = true;
				gotoDSRPlaceOrderScreen();*//*
				checkEvent();

			}
			else
			{
				if(!tryEnableCounterOrderButton())
					Toast.makeText(this, "Your location is too far to place counter order right now", Toast.LENGTH_SHORT).show();
			}*/

                break;

            case R.id.btn_call_conter:

                String tPhoneNumber = "" + tv_contact_phone.getText();
                if (tPhoneNumber != null && !tPhoneNumber.equalsIgnoreCase("null") &&
                        !tPhoneNumber.equalsIgnoreCase("") && tPhoneNumber.length() >= 10) {
                    String tel = "tel:" + tPhoneNumber;
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));

                    //Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(url));
                    startActivity(intent);
                } else
                    alertUserP(this, "Error", "Invalid phone number", "OK");

                break;

            case R.id.btn_sms_counter:

              /*
                Bitmap bitmap = Bitmap.createBitmap(rcv_pendingOrderList.getWidth(), rcv_pendingOrderList.getHeight(), Bitmap.Config.RGB_565);
                int childHight = 0;
                for (int i = 0; i < rcv_pendingOrderList.getAdapter().getItemCount(); i++) {
                    bitmap = imageConverter.getBitmapFromView(rcv_pendingOrderList);
                    childHight += rcv_pendingOrderList.getChildAt(i).getHeight();
                }*/


//
//
// String tPhoneNumber2 = "" + tv_contact_phone.getText();
//                if (tPhoneNumber2 != null && !tPhoneNumber2.equalsIgnoreCase("null") &&
//                        !tPhoneNumber2.equalsIgnoreCase("") && tPhoneNumber2.length() >= 10) {
///*				String sms = "sms:" + tStr2;
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sms));
//				intent.putExtra("sms_body", "test");
//				intent.setType("vnd.android-dir/mms-sms");
//				startActivity(intent);*/
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", tPhoneNumber2, null));
//                    startActivity(intent);
//                } else
//                    alertUserP(this, "Error", "Invalid phone number", "OK");

                break;

            case R.id.pending_order_share:
                onClickApp("com.whatsapp", imageConverter.getBitmapFromView(findViewById(R.id.listr_btn_wrapper5)));


                break;
            case R.id.pending_invoice_share:
                onClickApp("com.whatsapp", imageConverter.getBitmapFromView(findViewById(R.id.listr_btn_wrapper3)));

                break;

            case R.id.bottombar_one:
                gotoDSRMenuScreen();
                break;

            case R.id.bottombar_two:
                gotoDSRSyncScreen();
                break;

            case R.id.bottombar_three:
                gotoB2CCustListScreen();
                break;

            case R.id.bottombar_four:
                gotoB2CCountersScreen();
                break;

            case R.id.bottombar_five:
                gotoDSRPendingOrders();
                break;



          /*  case R.id.btnLocate:

                B2CLocateScreen.toRefresh = true;
                gotoB2CLocateScreen();
                break;*/
			
/*		case R.id.btnPlanAVisit:
			
			DSREditProgScreen.currentEvent = new EventData();
			
			DSREditProgScreen.currentEvent.setCustomer_name(currentMap.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name()));
			DSREditProgScreen.currentEvent.setCmkey(currentMap.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name()));

			Calendar mCal = Calendar.getInstance(Locale.getDefault());
			DSREditProgScreen.currentEvent.setFrom_date_time(""+DateFormat.format(DSRAppConstant.datetimeFormat, mCal.getTime()));
			mCal.set(Calendar.HOUR_OF_DAY, mCal.get(Calendar.HOUR_OF_DAY)+1);
			DSREditProgScreen.currentEvent.setTo_date_time(""+DateFormat.format(DSRAppConstant.datetimeFormat, mCal.getTime()));

			DSRAddProgScreen.toRefresh = true;
			DSRAddProgScreen.backToDayScreen = false;
			gotoDSRAddProgScreen();
			break;*/
        }
    }

    public void onClickApp(String pack, Bitmap bitmap) {
        Logger.LogError("currentCustomer.getPhone1()", "" + currentCustomer.getPhone1());
        PackageManager pm = getPackageManager();
        // String toNumber = "+91 95667 96870"; // contains spaces.
        String toNumber = "" + currentCustomer.getPhone1(); // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");
        toNumber = toNumber.startsWith("91") ? toNumber : "91".concat(toNumber);
        Logger.LogError("toNumber", "" + toNumber);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
            Uri imageUri = Uri.parse(path);

            @SuppressWarnings("unused")
            PackageInfo info = pm.getPackageInfo(pack, PackageManager.GET_META_DATA);

            Intent waIntent = new Intent(Intent.ACTION_MAIN);
            waIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
            waIntent.setType("image/*");
            waIntent.setPackage(pack);

            waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
            waIntent.putExtra(Intent.EXTRA_TEXT, "" + currentCustomer.getCompany_name());
            waIntent.setAction(Intent.ACTION_SEND);
            startActivity(Intent.createChooser(waIntent, "Share with"));
        } catch (Exception e) {
            Log.e("Error on sharing", e + " ");
            Toast.makeText(this, "App not Installed", Toast.LENGTH_SHORT).show();
        }
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

    public void setdirectCheckin() {
        if (txt_visits.getText().toString().equalsIgnoreCase("CheckIn")) {
            if (tryEnableCounterOrderButton()) {
                purchase_order_type = "Phone Order";
                txt_visits.setText("CheckIn");

                txt_visits.setTextColor(getResources().getColor(R.color.green));
                txt_order.setTextColor(getResources().getColor(R.color.green));

                iv_check.setBackgroundResource(R.drawable.round_green);
                iv_order.setBackgroundResource(R.drawable.round_green);

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null) {
            stopRemoveLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askPermission();
        }
        Logger.LogError("Latitude:Longitude", +mLastLocation.getLatitude() + "" + mLastLocation.getLongitude());
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {

            Logger.LogError("Latitude:Longitude", +mLastLocation.getLatitude() + "" + mLastLocation.getLongitude());
        }
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
        Logger.LogError("Update -> Latitude:Longitude", +mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
    }

    protected void createLocationRequest() {
        Logger.LogError("createLocationRequest", "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                if (mLastLocation != null) {
                    //   Log.e("Location.getLatitude start", "" + mLastLocation.getLatitude());
                    //   Log.e("Location.getLongitude start", "" + mLastLocation.getLongitude());
                    setdirectCheckin();
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
                        //  Log.e("Location.getLatitude changes start", "" + mLastLocation.getLatitude());
                        //  Log.e("Location.getLongitude changes start", "" + mLastLocation.getLongitude());
                        setdirectCheckin();
                    }
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


    private void checkEvent() {//
        Logger.LogError("geteventkey()", "" + geteventkey());
        if (geteventkey() != 0) {
            DSREditProgScreen.toRefresh = true;
            DSREditProgScreen.eventKey = "" + geteventkey();
            String from_date_Time = "" + DateFormat.format(B2CAppConstant.datetimeFormat1, new Date().getTime());
            //  String to_date_Time = "" + DateFormat.format(B2CAppConstant.datetimeFormat, (new Date().getTime() + 1 * 60 * 60 * 1000));//
            Logger.LogError("from_date_Time", "" + from_date_Time);

            B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateCurrentFromDate("" + geteventkey(), from_date_Time, "");

        } else {
            createNewEvent();

        }

    }

    private void createNewEvent() {
        //	Logger.LogError("geteventkey()",""+currentCustomer.getCmkey());
        CustomerData customerdata = new CustomerData();
        EventData eventData = new EventData();
        ArrayList<CustomerData> customersInArea = new ArrayList<CustomerData>();
        customerdata.setCmkey(currentCustomer.getCmkey());
        customerdata.setArea_name(currentCustomer.getArea_name());
        customerdata.setCompany_name(currentCustomer.getCompany_name());

        int newEntryCount = B2CApp.b2cPreference.getNewEntryCount() - 1;
        String eventDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());
        String startDate = "" + DateFormat.format(B2CAppConstant.dateFormat, new Date().getTime());
        String from_date = "" + DateFormat.format(B2CAppConstant.datetimeFormat1, new Date().getTime());
        String entered_on = "" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime());
        String to_date = "" + DateFormat.format(B2CAppConstant.datetimeFormat, (new Date().getTime() + 1 * 60 * 60 * 1000));
        Logger.LogError("to_date", "" + to_date);
        eventData.setEvent_key(newEntryCount);
        B2CApp.b2cPreference.setNewEntryCount(newEntryCount);
        eventData.setEvent_user_key(B2CApp.b2cPreference.getUserId());
        eventData.setEvent_type(DSRAppConstant.VISIT_TYPES[0]);
        eventData.setEvent_title("Counter Order");
        eventData.setFrom_date_time(from_date);
        //  eventData.setTo_date_time(to_date);
        eventData.setEvent_description("B2C test Desc");
        eventData.setStatus(DSRAppConstant.EVENT_STATUS.CheckIn.name());
        eventData.setCmkey("" + currentCustomer.getCmkey());
        eventData.setCus_key("" + currentCustomer.getCus_key());
        eventData.setArea("" + currentCustomer.getArea_name());
        eventData.setCustomer_name("" + currentCustomer.getCompany_name());
        eventData.setEvent_month(startDate.substring(0, 7));
        eventData.setEvent_date(eventDate);
        eventData.setEntered_on(entered_on);
        eventData.setEvent_date_absolute(startDate.substring(8));
        eventData.setIs_synced_to_server("0");

        B2CApp.getINSTANCE().getRoomDB().eventdata_dao().insertEventData(eventData);

        //   customersInArea.add(customerdata);
        //   B2CApp.b2cLdbs.insertBeat(this, customersInArea);

        DSREditProgScreen.toRefresh = true;
        DSREditProgScreen.eventKey = "" + geteventkey();
        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(newEntryCount);
        //gotoBDSREditProgScreen();

    }

    public int geteventkey() {
        String startDate = "" + DateFormat.format(B2CAppConstant.dateFormat, new Date().getTime());
        Integer eventKey = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchEventKey(currentCustomer.getCmkey(), startDate + "%");

        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(eventKey == null ? 0 : eventKey);
        return eventKey == null ? 0 : eventKey;
    }

    public int getCheckineventkey(int cmkey) {
        String startDate = "" + DateFormat.format(B2CAppConstant.dateFormat, new Date().getTime());
        Integer eventKey = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchCheckinEventKey(cmkey);

        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(eventKey == null ? 0 : eventKey);
        return eventKey == null ? 0 : eventKey;
    }

    public String getCompanuName() {//
        Logger.LogError("B2CApp.b2cPreference.getCHECKEDINCMKEY()", "" + B2CApp.b2cPreference.getCHECKEDINCMKEY());
        String CompanyName = B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchCompanyName(B2CApp.b2cPreference.getCHECKEDINCMKEY());
        //   Cursor mCursor = B2CApp.b2cLdbs.fetchCompanyName(this, B2CApp.b2cPreference.getCHECKEDINCMKEY());


        return CompanyName == null ? "" : CompanyName;
    }

    public void collectionEntryClicked(boolean isByCollectButton, String collectionAmount) {
        B2CCollectionEntryScreen.isFromCounterDetail = true;
        B2CCollectionEntryScreen.isByCollectButton = isByCollectButton;
        B2CCollectionEntryScreen.currentCusKey = currentCustomer.getCus_key();
        B2CCollectionEntryScreen.currentCustomerName = currentCustomer.getCompany_name();
        B2CCollectionEntryScreen.currentCollectionAmount = "" + collectionAmount;
        gotoB2cCollectionEntryScreen();
    }

    public void viewPODetailClicked(PendingOrderData po) {
        currentPendingOrder = po;
        popupWindowOrder();
        //initPopupOrder();
        if (fillPopupOrder(po))
            showPopupOrder();
        else
            alertUserP(this, "Error", "Product not exists in DB. Please sync products with server", "OK");
    }

    public void onCheckStockClicked(PendingOrderData tOrder, int position) {
        //tOrder.setCustomer_key(B2CCounterDetailScreen.currentMap.get(B2CTableCreate.COLOUMNS_CONTACT_MASTER.cmkey.name()));
        //tOrder.setContact_key(B2CCounterDetailScreen.currentContactKey);
        //tOrder.setDate(B2CApp.b2cUtils.getFormattedDate("yyyyMMdd", new Date().getTime()));

        currentPendingOrder = tOrder;

        PROGRESS_MSG = "Checking stock...";
        new OrderTask().execute(B2CApp.b2cPreference.getBaseUrl() + B2CAppConstant.METHOD_GET_STOCK);
    }

    private void initPopupOrder() {

        if (ppWindowOrder == null || popupHolderOrder == null) {
            ppWindowOrder = initOrderPopup();
        }

    }

    private void showPopupOrder() {

        if (ppWindowOrder != null && !ppWindowOrder.isShowing()) {
            bg_dim_layout.getForeground().setAlpha(200);
            ppWindowOrder.showAtLocation(findViewById(android.R.id.content),
                    Gravity.CENTER, 0, 0);
        }
    }

    private void dismissPopupOrder() {
        if (ppWindowOrder != null && ppWindowOrder.isShowing())
            ppWindowOrder.dismiss();
    }

    private PopupWindow initOrderPopup() {

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.popup_order, (ViewGroup) findViewById(R.id.PopUpView));

        popupHolderOrder = new PopupHolderOrder();
        popupHolderOrder.tv_pop_part_no = (TextView) layout.findViewById(R.id.tv_pop_part_no);
        popupHolderOrder.tv_pop_manu = (TextView) layout.findViewById(R.id.tv_pop_manu);
        popupHolderOrder.tv_pop_description = (TextView) layout.findViewById(R.id.tv_pop_description);
        //popupHolderOrder.tv_pop_vehicle_spec = (TextView) layout.findViewById(R.id.tv_pop_vehicle_spec);
        popupHolderOrder.tv_pop_lprice = (TextView) layout.findViewById(R.id.tv_pop_lprice);
        popupHolderOrder.tv_pop_mrp = (TextView) layout.findViewById(R.id.tv_pop_mrp);
        popupHolderOrder.tv_pop_order_qty = (TextView) layout.findViewById(R.id.tv_pop_order_qty);
        popupHolderOrder.tv_pop_pend_qty = (TextView) layout.findViewById(R.id.tv_pop_pend_qty);
        popupHolderOrder.tv_pop_date = (TextView) layout.findViewById(R.id.tv_pop_date);
        popupHolderOrder.tv_pop_avail = (TextView) layout.findViewById(R.id.tv_pop_avail);
        popupHolderOrder.btn_pop_cancel = (Button) layout.findViewById(R.id.btn_pop_cancel);
        popupHolderOrder.btn_pop_send = (Button) layout.findViewById(R.id.btn_pop_send);
        popupHolderOrder.btn_close_popup = (ImageView) layout.findViewById(R.id.btn_close_popup);

        ppWindowOrder = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);
        ppWindowOrder.setBackgroundDrawable(new BitmapDrawable());
        ppWindowOrder.setTouchable(true);
        ppWindowOrder.setOutsideTouchable(true);

        ppWindowOrder.setTouchInterceptor(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (ppWindowOrder.isShowing()) {
                        ppWindowOrder.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });

        ppWindowOrder.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                bg_dim_layout.getForeground().setAlpha(0);
            }
        });

        popupHolderOrder.btn_close_popup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ppWindowOrder.dismiss();
                popupdialog.dismiss();
            }
        });

        popupHolderOrder.btn_pop_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showCancellationAlert();
            }
        });

        popupHolderOrder.btn_pop_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showModificationAlert();
            }
        });

        return ppWindowOrder;
    }

    private void popupWindowOrder() {
        popupdialog = new Dialog(this);
        popupdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        popupdialog.setContentView(R.layout.popup_order);

        popupHolderOrder = new PopupHolderOrder();
        popupHolderOrder.tv_pop_part_no = (TextView) popupdialog.findViewById(R.id.tv_pop_part_no);
        popupHolderOrder.tv_pop_manu = (TextView) popupdialog.findViewById(R.id.tv_pop_manu);
        popupHolderOrder.tv_pop_description = (TextView) popupdialog.findViewById(R.id.tv_pop_description);
        //popupHolderOrder.tv_pop_vehicle_spec = (TextView) dialog.findViewById(R.id.tv_pop_vehicle_spec);
        popupHolderOrder.tv_pop_lprice = (TextView) popupdialog.findViewById(R.id.tv_pop_lprice);
        popupHolderOrder.tv_pop_mrp = (TextView) popupdialog.findViewById(R.id.tv_pop_mrp);
        popupHolderOrder.tv_pop_order_qty = (TextView) popupdialog.findViewById(R.id.tv_pop_order_qty);
        popupHolderOrder.tv_pop_pend_qty = popupdialog.findViewById(R.id.tv_pop_pend_qty);
        popupHolderOrder.tv_pop_date = (TextView) popupdialog.findViewById(R.id.tv_pop_date);
        popupHolderOrder.tv_pop_avail = (TextView) popupdialog.findViewById(R.id.tv_pop_avail);
        popupHolderOrder.btn_pop_cancel = (Button) popupdialog.findViewById(R.id.btn_pop_cancel);
        popupHolderOrder.btn_pop_send = (Button) popupdialog.findViewById(R.id.btn_pop_send);
        popupHolderOrder.btn_close_popup = (ImageView) popupdialog.findViewById(R.id.btn_close_popup);


        popupdialog.setCanceledOnTouchOutside(false);


        popupHolderOrder.btn_close_popup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                popupdialog.dismiss();
            }
        });

        popupHolderOrder.btn_pop_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showCancellationAlert();
            }
        });

        popupHolderOrder.btn_pop_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ValidationUtils.isEmpty("" + popupHolderOrder.tv_pop_order_qty.getText())) {
                    popupHolderOrder.tv_pop_order_qty.setError("");
                } else {
                    showModificationAlert();
                }

            }
        });
        popupdialog.show();
    }

    private boolean fillPopupOrder(PendingOrderData po) {
        ProductData product = B2CApp.getINSTANCE().getRoomDB().productData_dao().fetchproductSelected(po.getMi_key());

		/*Cursor mCursor = B2CApp.b2cLdbs.fetchSelected(this, B2CTableCreate.TABLE_B2C_PRODUCT_MASTER, null,
				B2CTableCreate.COLOUMNS_PRODUCT_MASTER.mikey.name(), B2CLocalDBStorage.SELECTION_OPERATION_LIKE, 
				new String[]{po.getMi_key()}, null);*/

        if (product != null) {
			/*ArrayList<ProductData> prods = new B2CCursorFactory().fetchAsProducts(mCursor, B2CTableCreate.getColoumnArrayTableProductMaster());
			ProductData product = prods.get(0);*/

            popupHolderOrder.tv_pop_part_no.setText(": " + product.getPart_no());
            popupHolderOrder.tv_pop_manu.setText(": " + product.getManu_name());
            popupHolderOrder.tv_pop_description.setText(": " + po.getMi_name());
            final double priceAT = (double) (product.getList_price() * (100 + product.getTaxPercent()) / 100);
            String formattedPAT = String.format("%.2f", priceAT);
            popupHolderOrder.tv_pop_lprice.setText(": " + B2CApp.b2cUtils.setGroupSeparaterWithZero("" + formattedPAT));
            popupHolderOrder.tv_pop_mrp.setText(": " + "" + B2CApp.b2cUtils.setGroupSeparaterWithZero("" + product.getMrp_rate()));
            popupHolderOrder.tv_pop_order_qty.setText("" + Double.valueOf(po.getOrdered_qty()).intValue());
            popupHolderOrder.tv_pop_pend_qty.setText(": " + "" + Double.valueOf(po.getPending_qty()).intValue());
            popupHolderOrder.tv_pop_date.setText(": " + B2CApp.b2cUtils.getFormattedDate("dd-MM-yyyy", B2CApp.b2cUtils.getTimestamp("yyyyMMdd", po.getDate())));
            popupHolderOrder.tv_pop_avail.setText(": " + "--NA--");
            return true;
        } else
            return false;

    }

    private void updateCounterDetails() {
        updateCustomerDetails();
        updatePendingBills();
        updatePendingOrders();
    }

    private void updateCustomerDetails() {


        //  Logger.LogError("customerDatagetCmkey", "" + customerData.getCmkey());

        Logger.LogError("currentMapCmkey", "" + currentMap.getCmkey());
        Logger.LogError("getCus_key", "" + currentMap.getCus_key());
        currentCustomer = B2CApp.getINSTANCE().getRoomDB().
                customerData_dao().fetchCustomerDetails(currentMap.getCmkey());
        if (currentCustomer != null) {
            tv_counter_name.setText("" + currentCustomer.getCompany_name());
            tv_counter_address.setText(currentCustomer.getAddress1() + " " + currentCustomer.getAddress2() + " " + currentCustomer.getAddress3() + " " + currentCustomer.getCity());
            tv_contact_name.setText(": " + currentCustomer.getFirst_name());
            tv_contact_phone.setText(": " + currentCustomer.getPhone1());

            if (ValidationUtils.isEmpty(currentCustomer.getGST_NO())) {
                tv_tin_no.setText(": -");
            } else {
                tv_tin_no.setText(": " + ValidationUtils.stringFormater(currentCustomer.getGST_NO()));
            }

            String con_key = "" + currentCustomer.getCus_key();
            String strLat = currentCustomer.getLatitude();
            String strLong = currentCustomer.getLongitude();
            Logger.LogError("strLat", "" + strLat);
            Logger.LogError("strLong", "" + strLong);

            double latitude = Double.parseDouble((strLat == null || strLat.equalsIgnoreCase("null")) ? "0.0" : strLat);
            double longitude = Double.parseDouble((strLong == null || strLat.equalsIgnoreCase("null")) ? "0.0" : strLong);

            currentContactIndKey = con_key;
            currentCustomerLoc.setLatitude(latitude);
            currentCustomerLoc.setLongitude(longitude);
           /* if (currentCustomer.getCredit_days() > 0)
                tv_credit_days.setText("" + currentCustomer.getCredit_days());
            else {
                tv_credit_days.setText("" + currentCustomer.getCredit_days());
                tv_credit_days.setTextColor(getResources().getColor(R.color.red));
            }*/
            if ((currentCustomer.getAmount_exceed() != null) && Double.valueOf(currentCustomer.getAmount_exceed()).intValue() > 0) {
                tv_over_due.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + currentCustomer.getAmount_exceed()));
                tv_over_due.setTextColor(getResources().getColor(R.color.red));
            } else {
                tv_over_due.setText("0");
                tv_over_due.setTextColor(getResources().getColor(R.color.red));
            }

            if ((currentCustomer.getMax_credit_limit() != null) && ((Double.valueOf(currentCustomer.getMax_credit_limit())).intValue() > 0)) {
                //double maxCredit = Double.parseDouble(currentCustomer.getMax_credit_limit());
                tv_total_credit.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + currentCustomer.getMax_credit_limit()));
                tv_avail_credit.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + currentCustomer.getMax_credit_limit()));
                B2CCounterDetailScreen.currentCustomer.setAvailable_credit("" + currentCustomer.getMax_credit_limit());


            } else {
                tv_total_credit.setText("0");
                tv_avail_credit.setText("0");
                tv_total_credit.setTextColor(getResources().getColor(R.color.red));
                tv_avail_credit.setTextColor(getResources().getColor(R.color.red));
            }

        } else {
            tv_total_credit.setText("0");
            tv_avail_credit.setText("0");
            //  tv_credit_days.setText("0");
            tv_total_credit.setTextColor(getResources().getColor(R.color.red));
            tv_avail_credit.setTextColor(getResources().getColor(R.color.red));
            //  tv_credit_days.setTextColor(getResources().getColor(R.color.red));
        }


    }


    private void updatePendingBills() {

        counterPenBillVM.init(billData_dao, "" + currentMap.getCus_key());

        pendingBillAdaptor = new RCVPendingBillList(this);
        counterPenBillVM.billDataList.observe(this, pagedList -> {
            pendingBillAdaptor.submitList(pagedList);
            rcv_pendingBillList.setAdapter(pendingBillAdaptor);
        });






       /* ArrayList<BillData> billDataArrayList = (ArrayList<BillData>) B2CApp.getINSTANCE().getRoomDB().billData_dao().fetchAllBills(currentMap.getCus_key());

        pendingBillAdaptor = new RCVPendingBillList(this, billDataArrayList);
        rcv_pendingBillList.setAdapter(pendingBillAdaptor);*/
		/*Cursor mCursor = B2CApp.b2cLdbs.fetchSelected(this, B2CTableCreate.TABLE_B2C_PENDING_BILLS, null,
				B2CTableCreate.COLOUMNS_PENDING_BILLS.customer_key.name(), B2CLocalDBStorage.SELECTION_OPERATION_LIKE, 
				new String[]{currentMap.getCus_key()}, B2CTableCreate.COLOUMNS_PENDING_BILLS.invoice_no.name());
		
		billEntries.clear();
		billEntries.addAll(new B2CCursorFactory().fetchAsBills(mCursor, B2CTableCreate.getColoumnArrayTablePendingBills()));*/


        //pendingBillAdaptor = new B2CPendingBillAdapter(this, billEntries);

        //pendingBillList.setAdapter(pendingBillAdaptor);



		/*LayoutParams lp = pendingBillList.getLayoutParams();
		float density = getResources().getDisplayMetrics().density;
		
		int rowCountToDisplay= billEntries.size()>5 ? 5 : billEntries.size() ;

		if(getResources().getBoolean(R.bool.isTablet))
			lp.height = (int) (rowCountToDisplay * 71 * density);
		else 
			lp.height = (int) (rowCountToDisplay * 41 * density);
		
*//*		if(getResources().getBoolean(R.bool.isTablet))
			lp.height = (int) (billEntries.size() * 50.5 * density);
		else 
			lp.height = (int) (billEntries.size() * 40.5 * density);*//*
		
		pendingBillList.setLayoutParams(lp);*/
    }

    private void updatePendingOrders() {
        Logger.LogError("getCmkey", "" + currentMap.getCmkey());
        Logger.LogError("currentMap.getCustomer_key()", "" + currentMap.getCustomer_key());
        Logger.LogError("currentMap.getCus_key()", "" + currentMap.getCus_key());

        counterPenOrderVM.init(pendingOrderData_dao, "" + currentMap.getCus_key());

        pendingOrderAdaptor = new RCV_PendingOrderList(this);
        counterPenOrderVM.pendingOrderDataList.observe(this, new Observer<PagedList<PendingOrderData>>() {
            @Override
            public void onChanged(@Nullable PagedList<PendingOrderData> pagedList) {
                pendingOrderAdaptor.submitList(pagedList);
                rcv_pendingOrderList.setAdapter(pendingOrderAdaptor);
            }
        });




       /* ArrayList<PendingOrderData> pendingOrderDataArrayList = (ArrayList<PendingOrderData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().fetchAllOrders("" + currentMap.getCus_key());
        pendingOrderAdaptor = new RCV_PendingOrderList(this, pendingOrderDataArrayList);
        rcv_pendingOrderList.setAdapter(pendingOrderAdaptor);*/

        /* Cursor mCursor = B2CApp.b2cLdbs.fetchSelected(this, B2CTableCreate.TABLE_B2C_PENDING_ORDERS, null,
                B2CTableCreate.COLOUMNS_PENDING_ORDERS.customer_key.name(), B2CLocalDBStorage.SELECTION_OPERATION_LIKE,
                new String[]{currentMap.getCus_key()}, null);
                 orderEntries.clear();
        orderEntries.addAll(new B2CCursorFactory().fetchAsOrders(mCursor, B2CTableCreate.getColoumnArrayTablePendingOrders()));*/

        /*pendingOrderAdaptor = new B2CPendingOrderTableAdapter(this, orderEntries);
		pendingOrderList.setAdapter(pendingOrderAdaptor);*/
        // Logger.LogError("currentMap.getCus_key()", "" + currentMap.getCmkey());
        // Logger.LogError("pendingOrderDataArrayList", "" + pendingOrderDataArrayList.size());

		
	/*	LayoutParams lp = pendingOrderList.getLayoutParams();
		float density = getResources().getDisplayMetrics().density;
		
		int rowCountToDisplay= orderEntries.size()>5 ? 5 : orderEntries.size() ;

		if(getResources().getBoolean(R.bool.isTablet))
			lp.height = (int) (rowCountToDisplay * 120 * density);
		else 
			lp.height = (int) (rowCountToDisplay * 90 * density);
		
*//*		if(getResources().getBoolean(R.bool.isTablet))
			lp.height = (int) (orderEntries.size() * 150 * density);
		else 
			lp.height = (int) (orderEntries.size() * 92 * density);*//*
		
		pendingOrderList.setLayoutParams(lp);*/

    }

    class OrderTask extends AsyncTask<String, String, String> {

        private int operationType;
        private String uriInProgress;
        private int position;

       /* public OrderTask(int pos) {

            position = pos;
        }*/

        protected String doInBackground(String... params) {

            uriInProgress = params[0];
            Logger.LogError("uriInProgress : ", uriInProgress);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(uriInProgress);
            try {

                operationType = getOperationType(uriInProgress);

                String jsonString = "";

                try {

                    jsonString = getJsonParams(operationType).toString();
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
                //	Logger.LogError("ClientProtocolException : ", e.toString());
            } catch (IOException e) {
                //	Logger.LogError("IOException : ", e.toString());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

            B2CApp.b2cUtils.updateMaterialProgress(B2CCounterDetailScreen.this, PROGRESS_MSG);

        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);

            B2CApp.b2cUtils.dismissMaterialProgress();

            String message = "Operation failed in a timely manner. Please try again";

            if (responseString == null) {

                alertUserP(B2CCounterDetailScreen.this, "Failed", message, "OK");
                return;
            }

            if (operationType == B2CAppConstant.OPTYPE_PLACE_AN_ORDER) {
                onPostPlaceAnOrder(responseString, position);
            } else if (operationType == B2CAppConstant.OPTYPE_GET_STOCK) {
                onPostGetStock(responseString, position);
            } else if (operationType == B2CAppConstant.OPTYPE_UPDATE_ORDERS) {
                onPostUpdateAllOrders(responseString);
            }


        }
    }

    protected void onPostPlaceAnOrder(String responseString, int position) {

        JSONObject responseJson;
        String message = "Operation failed in a timely manner. Please try again";

        try {
            responseJson = new JSONObject(responseString);

            if (responseJson.has(B2CAppConstant.KEY_STATUS) && responseJson.getInt(B2CAppConstant.KEY_STATUS) == 1
                    && responseJson.has(B2CAppConstant.KEY_SO_KEY) && responseJson.has(B2CAppConstant.KEY_SO_ITEM_KEY)
                    && responseJson.has(DSRAppConstant.KEY_DUMMY_KEY) && responseJson.has(B2CAppConstant.KEY_CUST_KEY)) {

                dismissPopupOrder();

                if (responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY).equalsIgnoreCase("")) {
                    if (Integer.parseInt(currentPendingOrder.getOrdered_qty()) == 0) {
                        /*B2CApp.b2cLdbs.deleteRows(B2CCounterDetailScreen.this, B2CTableCreate.TABLE_B2C_PENDING_ORDERS,
                                B2CTableCreate.COLOUMNS_PENDING_ORDERS.customer_key.name() + "= '" + responseJson.getString(B2CAppConstant.KEY_CUST_KEY)
                                        + "' AND " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_key.name() + "= '" + responseJson.getString(B2CAppConstant.KEY_SO_KEY)
                                        + "' AND " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_item_key.name() + "= '" + responseJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY) + "'");*/

                        B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().deletePendingOrderSoKeyData(responseJson.getString(B2CAppConstant.KEY_CUST_KEY),
                                responseJson.getString(B2CAppConstant.KEY_SO_KEY), responseJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));
                        alertUserP(B2CCounterDetailScreen.this, "Success", "Order cancelled successfully", "OK");
                    } else {
                        B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().updatePendingOrderSoItemKeyData(responseJson.getString(B2CAppConstant.KEY_CUST_KEY),
                                responseJson.getString(B2CAppConstant.KEY_SO_KEY), responseJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY),
                                responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY));


                       /* B2CApp.b2cLdbs.updatePendingOrder(B2CCounterDetailScreen.this, responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                                responseJson.getString(B2CAppConstant.KEY_CUST_KEY), responseJson.getString(B2CAppConstant.KEY_SO_KEY),
                                responseJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));*/

                        alertUserP(B2CCounterDetailScreen.this, "Success", "Modify Order updated successfully", "OK");
                    }
                } else {
                    B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().updatePendingOrderSoItemKeyData(responseJson.getString(B2CAppConstant.KEY_CUST_KEY),
                            responseJson.getString(B2CAppConstant.KEY_SO_KEY), responseJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY),
                            responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY));

                /*    B2CApp.b2cLdbs.updatePendingOrder(B2CCounterDetailScreen.this, responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                            responseJson.getString(B2CAppConstant.KEY_CUST_KEY), responseJson.getString(B2CAppConstant.KEY_SO_KEY),
                            responseJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));*/

                    alertUserP(B2CCounterDetailScreen.this, "Success", "New Order updated successfully", "OK");
                }
                B2CPendingOrderScreen.toUpdateCustomerDetail = true;
                updatePendingOrders();
                return;
            } else
                Logger.LogError("Incomplete response", " : params missing");

        } catch (JSONException e) {
            //	Logger.LogError("JSONException", " : " + e.toString());
            e.printStackTrace();
        }

        alertUserP(B2CCounterDetailScreen.this, "Failed", message, "OK");

    }

    protected void onPostGetStock(String responseString, int position) {

        JSONObject responseJson;
        String message = "Unable to check stock in a timely manner. Please try again";
        stocksInBranch.clear();
        try {
            responseJson = new JSONObject(responseString);

            if (responseJson.has(B2CAppConstant.KEY_STATUS) && responseJson.getInt(B2CAppConstant.KEY_STATUS) == 1
                    && responseJson.has(B2CAppConstant.KEY_DATA)) {

                JSONArray dataArray = responseJson.getJSONArray(B2CAppConstant.KEY_DATA);
                JSONObject dataElement;
                StockData tStock;
                stocksInBranch.clear();

                for (int i = 0; i < dataArray.length(); i++) {
                    tStock = new StockData();
                    dataElement = dataArray.getJSONObject(i);

                    if (dataElement.has(B2CAppConstant.KEY_UNIT_NAME))
                        tStock.setUnit_name(dataElement.getString(B2CAppConstant.KEY_UNIT_NAME));

                    if (dataElement.has(B2CAppConstant.KEY_UNIT_LOCATION))
                        tStock.setUnit_location(dataElement.getString(B2CAppConstant.KEY_UNIT_LOCATION));

                    if (dataElement.has(B2CAppConstant.KEY_AVAILABILITY))
                        tStock.setStock(Double.parseDouble(dataElement.getString(B2CAppConstant.KEY_AVAILABILITY)));

                    stocksInBranch.add(tStock);

                }

                showStockPopup();
                return;
            } else
                showStockPopup();
            //Logger.LogError("Incomplete response", " : params missing");

        } catch (JSONException e) {
            Logger.LogError("JSONException", " : " + e.toString());
            e.printStackTrace();
        }

        //	alertUserP(B2CCounterDetailScreen.this, "Failed", message, "OK");
    }

    protected void onPostUpdateAllOrders(String responseString) {

        JSONObject outerJson;
        JSONArray responseArray = null;
        try {
            outerJson = new JSONObject(responseString);
            if (outerJson.has(B2CAppConstant.KEY_STATUS) && outerJson.getInt(B2CAppConstant.KEY_STATUS) == 1) {

                responseArray = outerJson.getJSONArray(B2CAppConstant.KEY_DATA);


                JSONObject innerJson;
                for (int i = 0; i < responseArray.length(); i++) {

                    innerJson = responseArray.getJSONObject(i);

                    if ((innerJson.has(B2CAppConstant.IS_SUCCESS) && innerJson.getInt(B2CAppConstant.IS_SUCCESS) == 1) ||
                            (innerJson.has(B2CAppConstant.KEY_STATUS) && innerJson.getInt(B2CAppConstant.KEY_STATUS) == 1)) {

                        Logger.LogError("resultJson.getString(B2CAppConstant.KEY_SO_ITEM_QTY)", "" + innerJson.getString(B2CAppConstant.KEY_SO_ITEM_QTY));
                        Logger.LogError("resultJson.getString(B2CAppConstant.KEY_CUST_KEY)", "" + innerJson.getString(B2CAppConstant.KEY_CUST_KEY));
                        Logger.LogError("resultJson.getString(B2CAppConstant.KEY_SO_KEY)", "" + innerJson.getString(B2CAppConstant.KEY_SO_KEY));
                        Logger.LogError("resultJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY)", "" + innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));
                        if (Integer.parseInt(innerJson.getString(B2CAppConstant.KEY_SO_ITEM_QTY)) == 0) {


                            B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().deletePendingOrderSoKeyData(innerJson.getString(B2CAppConstant.KEY_CUST_KEY),
                                    innerJson.getString(B2CAppConstant.KEY_SO_KEY), innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));
                        } else {


                            B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().updatePendingOrderSoItemKeyData(innerJson.getString(B2CAppConstant.KEY_CUST_KEY),
                                    innerJson.getString(B2CAppConstant.KEY_SO_KEY), innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY),
                                    innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY));
                        }


				/*	B2CApp.b2cLdbs.updatePendingOrder(mContext, resultJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
							resultJson.getString(B2CAppConstant.KEY_CUST_KEY), resultJson.getString(B2CAppConstant.KEY_SO_KEY),
									resultJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));*/

                        Logger.LogError("is success : ", "true");
                    } else {

                        Logger.LogError("is success : ", "false");
                    }


                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
            Logger.LogError("JSONException UpdateAllOrders : ", e.toString());

            //	Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders failed", Toast.LENGTH_SHORT).show();
            return;
        }

        B2CApp.b2cPreference.setIsUpdatedPendingOrders(true);
        //Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders success", Toast.LENGTH_SHORT).show();

    }

    public void showStockPopup() {
        //initPopupStock();
        PopupViewStock();
        if (fillPopupStock())
            showPopupStock();
		/*else
			alertUserP(this, "Error", "Unable to check stock in a timely manner. Please try again", "OK");*/
    }


    private void initPopupStock() {

        if (ppWindowStock == null || popupHolderStock == null) {
            ppWindowStock = initPopupView();
        }

    }

    private void showPopupStock() {

        if (ppWindowStock != null && !ppWindowStock.isShowing()) {
            bg_dim_layout.getForeground().setAlpha(200);
            ppWindowStock.showAtLocation(findViewById(android.R.id.content),
                    Gravity.CENTER, 0, 0);
        }
    }

    private void dismissPopupStock() {
        if (ppWindowStock != null && ppWindowStock.isShowing())
            ppWindowStock.dismiss();
    }

    private void PopupViewStock() {

        popupdialog = new Dialog(this);
        popupdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupdialog.setContentView(R.layout.popup_stock);

        popupHolderStock = new PopupHolderStock();
        popupHolderStock.listview_stock = (ListView) popupdialog.findViewById(R.id.listview_stock);

        popupHolderStock.btn_pop_done = (Button) popupdialog.findViewById(R.id.btn_pop_done);
        popupHolderStock.btn_pop_close = (ImageView) popupdialog.findViewById(R.id.btn_pop_close);


        popupdialog.setCanceledOnTouchOutside(false);


        popupHolderStock.btn_pop_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                popupdialog.dismiss();
            }
        });

        popupHolderStock.btn_pop_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                popupdialog.dismiss();
            }
        });

        popupdialog.show();
    }

    private PopupWindow initPopupView() {

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.popup_stock, (ViewGroup) findViewById(R.id.PopUpView));

        popupHolderStock = new PopupHolderStock();
        popupHolderStock.listview_stock = (ListView) layout.findViewById(R.id.listview_stock);

        popupHolderStock.btn_pop_done = (Button) layout.findViewById(R.id.btn_pop_done);
        popupHolderStock.btn_pop_close = (ImageView) layout.findViewById(R.id.btn_pop_close);

        ppWindowStock = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);
        ppWindowStock.setBackgroundDrawable(new BitmapDrawable());
        ppWindowStock.setTouchable(true);
        ppWindowStock.setOutsideTouchable(true);

        ppWindowStock.setTouchInterceptor(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (ppWindowStock.isShowing()) {
                        ppWindowStock.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });

        ppWindowStock.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                bg_dim_layout.getForeground().setAlpha(0);
            }
        });

        popupHolderStock.btn_pop_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ppWindowStock.dismiss();
            }
        });

        popupHolderStock.btn_pop_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ppWindowStock.dismiss();
            }
        });

        return ppWindowStock;
    }


    private boolean fillPopupStock() {
        if (stocksInBranch.size() > 0) {
            stockAdapter = new B2CStockAdapter(this, stocksInBranch);
            popupHolderStock.listview_stock.setAdapter(stockAdapter);
            popupdialog.findViewById(R.id.listview_stock).setVisibility(View.VISIBLE);
            popupdialog.findViewById(R.id.lt_no_stock).setVisibility(View.GONE);
            return true;
        } else
            popupdialog.findViewById(R.id.listview_stock).setVisibility(View.GONE);
        popupdialog.findViewById(R.id.lt_no_stock).setVisibility(View.VISIBLE);
        return false;
    }

    private class B2CStockAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<StockData> data;
        private LayoutInflater inflater = null;

        public B2CStockAdapter(Activity a, ArrayList<StockData> d) {
            activity = a;
            data = d;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private class ViewHolder {

            public TextView tv_stock_branch;
            public EditText et_stock_branch;

        }

        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listrow_popup_stock, null);
                TextView tv_stock_branch = (TextView) convertView.findViewById(R.id.tv_stock_branch);
                EditText et_stock_branch = (EditText) convertView.findViewById(R.id.et_stock_branch);
                holder = new ViewHolder();
                holder.tv_stock_branch = tv_stock_branch;
                holder.et_stock_branch = et_stock_branch;

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            StockData stock = data.get(position);

            holder.tv_stock_branch.setText("" + stock.getUnit_name());
            holder.et_stock_branch.setText("" + (int) stock.getStock());

            return convertView;

        }

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

    private void showCancellationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Are you sure to cancel the order ?")
                .setTitle("Alert!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                dialog.cancel();
                                cancelOrder();
                                popupdialog.dismiss();
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

    private void showModificationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(
                "Are you sure to modify the order ?")
                .setTitle("Alert!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                dialog.cancel();
                                modifyOrder();
                                popupdialog.dismiss();
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

    private void cancelOrder() {

        String entered_on = "" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime());
        if (currentPendingOrder.getSo_key() < 0) {
            B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().deletePendingOrderSoKeyData(currentPendingOrder.getCustomer_key(),
                    "" + currentPendingOrder.getSo_key(), "" + currentPendingOrder.getSo_item_key());
            updatePendingOrders();
        } else {


            int intUpdatepenOrder = B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().modifyPendingOrder(currentPendingOrder.getCustomer_key(), currentPendingOrder
                    .getSo_key(), "" + currentPendingOrder.getSo_item_key(), "0", "0", purchase_order_type, entered_on);
            Logger.LogError("intUpdatepenOrder", "" + intUpdatepenOrder);

        /*if (B2CApp.b2cLdbs.modifyPendingOrder(B2CCounterDetailScreen.this, currentPendingOrder.getCustomer_key(), currentPendingOrder.getSo_key(),
                currentPendingOrder.getSo_item_key(), "0", "0")) {*/
            if (intUpdatepenOrder > 0) {
                currentPendingOrder.setOrdered_qty("0");
                currentPendingOrder.setPending_qty("0");

                if (B2CApp.b2cUtils.isNetAvailable()) {
                    PROGRESS_MSG = "Cancelling order...";
                    new OrderTask().execute(B2CApp.b2cPreference.getBaseUrl() + B2CAppConstant.METHOD_UPDATE_ALL_ORDERS);
                } else {
                    updatePendingOrders();
                    B2CApp.b2cPreference.setIsUpdatedPendingOrders(false);
                    Toast.makeText(B2CCounterDetailScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
                }

            } else
                Toast.makeText(B2CCounterDetailScreen.this, "Cancellation failed in a timely manner, please try again", Toast.LENGTH_LONG).show();
        }
    }

    private void modifyOrder() {
        String entered_on = "" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime());

        int currentOrderQty = ((Double) Double.parseDouble(currentPendingOrder.getOrdered_qty())).intValue();
        int currentPendingQty = ((Double) Double.parseDouble(currentPendingOrder.getPending_qty())).intValue();

        int newOrderQty = Integer.parseInt("" + popupHolderOrder.tv_pop_order_qty.getText());

        int difference = newOrderQty - currentOrderQty;
        int newPendingQty = currentPendingQty + difference;


        Logger.LogError("currentPendingOrder.getCustomer_key()", "" + currentPendingOrder.getCustomer_key());
        Logger.LogError("currentPendingOrder.getCustomer_key()", "" + currentPendingOrder.getSo_key());
        Logger.LogError("currentPendingOrder.getCustomer_key()", "" + currentPendingOrder.getSo_item_key());
        Logger.LogError("newOrderQty", "" + newOrderQty);
        Logger.LogError("newPendingQty", "" + newPendingQty);
        Logger.LogError("newPendingQty", "" + newPendingQty);
        Logger.LogError("purchase_order_type", "" + purchase_order_type);

        int intUpdatepenOrder = B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().modifyPendingOrder(currentPendingOrder.getCustomer_key(), currentPendingOrder
                .getSo_key(), "" + currentPendingOrder.getSo_item_key(), "" + newOrderQty, "" + newPendingQty, purchase_order_type, entered_on);
        Logger.LogError("intUpdatepenOrder", "" + intUpdatepenOrder);
        if (intUpdatepenOrder > 0) {

	/*	if(B2CApp.b2cLdbs.modifyPendingOrder(B2CCounterDetailScreen.this, currentPendingOrder.getCustomer_key(), currentPendingOrder.getSo_key(),
				currentPendingOrder.getSo_item_key(), ""+newOrderQty, ""+newPendingQty))*/
            currentPendingOrder.setOrdered_qty("" + newOrderQty);
            currentPendingOrder.setPending_qty("" + newPendingQty);
            if (B2CApp.b2cUtils.isNetAvailable()) {
                PROGRESS_MSG = "Updating order...";
                new OrderTask().execute(B2CApp.b2cPreference.getBaseUrl() + B2CAppConstant.METHOD_UPDATE_ALL_ORDERS);
            } else {
                B2CApp.b2cPreference.setIsUpdatedPendingOrders(false);
                Toast.makeText(B2CCounterDetailScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
            }
        } else
            Toast.makeText(B2CCounterDetailScreen.this, "Modification failed in a timely manner, please try again", Toast.LENGTH_LONG).show();
    }

    private int getOperationType(String uri) {

        int optype = B2CAppConstant.OPTYPE_UNKNOWN;

        if (uri.contains(B2CAppConstant.METHOD_PLACE_AN_ORDER))
            optype = B2CAppConstant.OPTYPE_PLACE_AN_ORDER;
        else if (uri.contains(B2CAppConstant.METHOD_GET_STOCK))
            optype = B2CAppConstant.OPTYPE_GET_STOCK;
        else if (uri.contains(B2CAppConstant.METHOD_UPDATE_ALL_ORDERS))
            optype = B2CAppConstant.OPTYPE_UPDATE_ORDERS;

        return optype;

    }

    private JSONObject getJsonParams(int opType) throws JSONException {

        JSONObject json = new JSONObject();

        if (opType == B2CAppConstant.OPTYPE_PLACE_AN_ORDER) {
            json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "");
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());
            json.put(B2CAppConstant.KEY_USER_KEY, "" + B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, "" + B2CApp.b2cPreference.getUnitKey());
            json.put(DSRAppConstant.KEY_DUMMY_KEY, "");
            json.put(B2CAppConstant.KEY_CUST_KEY, currentPendingOrder.getCustomer_key());
            json.put(B2CAppConstant.KEY_CONTACT_KEY, currentPendingOrder.getContact_key());
            json.put(B2CAppConstant.KEY_SO_KEY, currentPendingOrder.getSo_key());
            json.put(B2CAppConstant.KEY_SO_ITEM_KEY, currentPendingOrder.getSo_item_key());
            json.put(B2CAppConstant.KEY_MI_KEY, currentPendingOrder.getMi_key());
            json.put(B2CAppConstant.KEY_SO_ITEM_QTY, currentPendingOrder.getOrdered_qty());
            json.put(B2CAppConstant.KEY_PUR_ODR_TYPE, purchase_order_type);
            json.put(B2CAppConstant.KEY_SUPPLIED_QTY, "" + 0);

            //json.put(B2CAppConstant.KEY_PENDING_ORDERS, ""+B2CApp.b2cPreference.getBranchCode());
        } else if (opType == B2CAppConstant.OPTYPE_GET_STOCK) {
            json.put(B2CAppConstant.KEY_USER_KEY, "" + B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, "" + B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "432e");
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());

            //JSONObject dataJson = new JSONObject();
            json.put(B2CAppConstant.KEY_MIKEY, currentPendingOrder.getMi_key());
            json.put(B2CAppConstant.KEY_PART_NO, currentPendingOrder.getMi_name());

            //json.put(B2CAppConstant.KEY_DATA, dataJson);
        } else if (opType == B2CAppConstant.OPTYPE_UPDATE_ORDERS) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());
            json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "");

            JSONArray tJsonArray = new JSONArray();

            ArrayList<PendingOrderData> pendingOrderDataArrayList = (ArrayList<PendingOrderData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().fetchCustomSelection();
		/*	Cursor mCursor = B2CApp.b2cLdbs.fetchCustomSelection(mContext, B2CTableCreate.TABLE_B2C_PENDING_ORDERS, null, B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_item_key.name() + " < "+ "0"
					+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0");*/

            if (pendingOrderDataArrayList.size() <= 0) {
                json.put(B2CAppConstant.KEY_DATA, tJsonArray);
                return json;
            }

            for (int i = 0; i < pendingOrderDataArrayList.size(); i++) {

                PendingOrderData pendingOrderData = pendingOrderDataArrayList.get(i);
                JSONObject tJson = new JSONObject();

                int getSo_key = pendingOrderData.getSo_key();
                if (getSo_key < 0) {
                    tJson.put(DSRAppConstant.KEY_DUMMY_KEY, "" + getSo_key);
                    tJson.put(B2CAppConstant.KEY_SO_ITEM_KEY, "");
                    tJson.put(B2CAppConstant.KEY_SO_KEY, "");
                } else {
                    tJson.put(DSRAppConstant.KEY_DUMMY_KEY, "");
                    tJson.put(B2CAppConstant.KEY_SO_ITEM_KEY, "" + pendingOrderData.getSo_item_key());
                    tJson.put(B2CAppConstant.KEY_SO_KEY, pendingOrderData.getSo_key());
                }

                tJson.put(B2CAppConstant.KEY_CUST_KEY, pendingOrderData.getCustomer_key());
                tJson.put(B2CAppConstant.KEY_CONTACT_KEY, pendingOrderData.getContact_key());
                tJson.put(B2CAppConstant.KEY_MI_KEY, pendingOrderData.getMi_key());
                tJson.put(B2CAppConstant.KEY_MI_NAME, pendingOrderData.getMi_name());
                tJson.put(B2CAppConstant.KEY_PUR_ODR_DATE, pendingOrderData.getDate());
                tJson.put(B2CAppConstant.KEY_SO_ITEM_QTY, pendingOrderData.getOrdered_qty());
                tJson.put(B2CAppConstant.KEY_SUPPLIED_QTY, "" + 0);
                tJson.put(B2CAppConstant.KEY_PUR_ODR_TYPE, pendingOrderData.getPurchase_order_type());
                tJson.put(B2CAppConstant.KEY_TAX_PERCENT, pendingOrderData.getTax_percent());
                tJson.put(B2CAppConstant.KEY_ENTERED_ON, pendingOrderData.getEntered_on());

                tJsonArray.put(tJson);

            }


            json.put(B2CAppConstant.KEY_ARRAY_DATA, tJsonArray);
        }

        return json;
    }

    private void gotoDSRAddProgScreen() {

        startActivity(new Intent(this, DSRAddProgScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRCountersScreen() {

        startActivity(new Intent(this, B2CCountersScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }


    private void gotoB2CLocateScreen() {

        startActivity(new Intent(this, B2CLocateScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCountersScreen() {

        startActivity(new Intent(this, B2CCountersScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoBDSREditProgScreen() {

        startActivity(new Intent(this, DSREditProgScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CPendingOrderScreen() {

        startActivity(new Intent(this, B2CPendingOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CAllOrderScreen() {

        startActivity(new Intent(this, B2CAllOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRSyncScreen() {

        startActivity(new Intent(this, B2CSyncScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRPlaceOrderScreen() {

        B2CPlaceOrderScreen.toRefreshScreen = true;
        startActivity(new Intent(this, B2CPlaceOrderScreen.class)
                .putExtra("currentcus_key", currentCustomer.getCus_key())
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void gotoDSRPendingOrders() {

        startActivity(new Intent(this, B2CPendingOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCustListScreen() {

        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2cCollectionEntryScreen() {
        B2CCollectionEntryScreen.isFromCounter = true;
        startActivity(new Intent(this, B2CCollectionEntryScreen.class)

                .putExtra("backPress", B2CAppConstant.COUNTER_DETAILS)
                .putExtra(B2CAppConstant.BACKTOCOLLECTIONSUM, B2CAppConstant.SCREEN_COLL_ENTRY)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }
    }

    private boolean tryEnableCounterOrderButton() {
        appLocationService = new AppLocationService(this);
        if (mLastLocation != null) {
            Logger.LogError("Latitude:Longitude", +mLastLocation.getLatitude() + "" + mLastLocation.getLongitude());
            currentDistance = calcDistance(UNIT_METER, mLastLocation.getLatitude(), mLastLocation.getLongitude(),
                    currentCustomerLoc.getLatitude(), currentCustomerLoc.getLongitude());
        } else if (isGPSEnabled() && mLastLocation == null) {
            startLocationUpdates();
        } else if (!isGPSEnabled()) {
            alertGPSSwitch(1);
        }


        Logger.LogError("currentDistance", " : " + currentDistance);
        Logger.LogError("userLocData", mLastLocation.getLatitude() + " , " + mLastLocation.getLongitude());
        Logger.LogError("currentCustomerLoc", currentCustomerLoc.getLatitude() + " , " + currentCustomerLoc.getLongitude());

        //   Toast.makeText(this, "currentDistance : " + currentDistance, Toast.LENGTH_SHORT).show();

        if (currentDistance < 0.0 || currentDistance > 100) {
            tv_tabmenu4.setBackgroundResource(R.drawable.round_grey_grey);
            isCounterEnabled = false;
        } else {
            tv_tabmenu4.setBackgroundResource(R.drawable.round_isteercolor);
            isCounterEnabled = true;
        }

        return isCounterEnabled;
    }

    private static Double _KilometersToMile = 0.621371;
    private static Double _KilometerToNautical = 0.5399566364038877;
    private final byte UNIT_MILE = 0;
    private final byte UNIT_KM = 1;
    private final byte UNIT_NMILE = 2;
    private final byte UNIT_METER = 3;

    private Double calcDistance(byte unit, double lat1, double lon1, double lat2, double lon2) {
        Double d, c, a, dLat, dLon;

        int R = 6371; // km

        dLat = deg2rad(lat2) - deg2rad(lat1);
        dLon = deg2rad(lon2) - deg2rad(lon1);
        a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        d = R * c;

        if (unit == UNIT_NMILE)
            d = d * _KilometerToNautical;
        else if (unit == UNIT_METER)
            d = d * 1000;

        return d;


      /*  double theta = lon1 - lon2;
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

        return (distance);*/

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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

    public void alertUserForCheckout(Context context, String title, String msg, String btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setTitle(title)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DSREditProgScreen.toRefresh = true;
                        DSREditProgScreen.eventKey = "" + B2CApp.b2cPreference.getCHECKEDINEVENTKEY();
                        gotoBDSREditProgScreen();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void alertForCheckout() {


        String alttitle = "Alert !";
        String altmessage = "Please CheckOut " + getCompanuName();
        String right_btn = getResources().getString(R.string.ok);
        String left_btn = getResources().getString(R.string.cancel);


        AlertPopupDialog alertPopupDialog = new AlertPopupDialog(this, alttitle, altmessage, left_btn, right_btn,
                new AlertPopupDialog.myOnClickListenerLeft() {
                    @Override
                    public void onButtonClickLeft() {

                        AlertPopupDialog.dialogDismiss();


                    }
                }, new AlertPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight() {
                DSREditProgScreen.toRefresh = true;
                DSREditProgScreen.eventKey = "" + getCheckineventkey(B2CApp.b2cPreference.getCHECKEDINCMKEY());
                gotoBDSREditProgScreen();
                AlertPopupDialog.dialogDismiss();


            }
        });
    }

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_COUNTER_DET;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void currentLocGPS() {
        Location gpsLocation = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        Logger.LogError("\"gpsLocation:", "" + gpsLocation);
        if (gpsLocation != null) {
            double latitude = gpsLocation.getLatitude();
            double longitude = gpsLocation.getLongitude();
            Toast.makeText(
                    getApplicationContext(),
                    "Mobile Location (GPS): \nLatitude: " + latitude
                            + "\nLongitude: " + longitude,
                    Toast.LENGTH_LONG).show();
            Logger.LogError("\"Mobile Location (GPS):", "" + latitude + "  " + longitude);
        } else {
            //  showSettingsAlert("GPS");
        }


    }

    public void currentLocNetwork() {
        Location nwLocation = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);

        if (nwLocation != null) {
            latitudeNW = nwLocation.getLatitude();
            longitudeNW = nwLocation.getLongitude();

            Logger.LogError("\"Mobile Location (NW):", "" + latitudeNW + "  " + longitudeNW);

            currentDistance = calcDistance(UNIT_METER, latitudeNW, longitudeNW,
                    currentCustomerLoc.getLatitude(), currentCustomerLoc.getLongitude());
            //   showSettingsAlert("NETWORK");
        }


    }

    private void alertGPSSwitch(int type) {
        if (alertDialog == null) {
            if (type == 1) {
                title = "GPS is disabled";
                message = "Show location settings?";
            } else if (type == 2) {
                title = "Alert !";
                message = "Your Location not available.Are you sure to refresh";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message).setTitle(title).setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //isFromGPS=true;
//                            finish();
                            if (type == 1) {
                                gotoGPSSwitch();
                            } else if (type == 2) {
                                gotoResetApp();
                            }
                        }
                    });
            alertDialog = builder.create();
        }

        if (alertDialog != null && !alertDialog.isShowing())
            alertDialog.show();
    }

    private void gotoGPSSwitch() {

        Intent intent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        isGPSAlertShown = true;
    }

    private void gotoResetApp() {

        Intent mStartActivity = new Intent(this, B2CNewMainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

}








