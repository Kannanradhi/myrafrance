package com.isteer.b2c.activity.reports;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.adapter.RCVAllPendingOrderScreenAdapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.dao.PendingOrderData_DAO;
import com.isteer.b2c.db.B2CTableCreate;
import com.isteer.b2c.gson.Gson_PendingBills;
import com.isteer.b2c.gson.Gson_PendingOrders;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.repository.ReportsOrderVM;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.volley.VolleyHttpRequest;
import com.isteer.b2c.volley.VolleyTaskListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class B2CAllOrderScreen extends AppCompatActivity implements OnClickListener, VolleyTaskListener, RCVAllPendingOrderScreenAdapter.ClickListener {

    private TextView header_title;
    private ImageView btn_header_right;

    public static boolean toUpdateCustomerDetail = false;

    private ListView pendingOrderList;

    public static String currentCounterName = "";

    private TextView tv_counter_name, tv_customer_area_name, tv_customer_phone, tv_customer_email, tv_customer_address;
    private LinearLayout bottombar_one, bottombar_two, bottombar_three, bottombar_four, bottombar_five;
    private TextView tv_from_date, tv_to_date;
    private RCVAllPendingOrderScreenAdapter rcvallpendingorderscreenadapter;
    private RecyclerView rcv_pendingOrderList;
    private ReportsOrderVM reportsOrderVM;
    private PendingOrderData_DAO pendingOrder_dao;
    private String fromDate, toDate;
    private SwipeRefreshLayout srl_refresh;
    private Disposable disposableBilldata, disposableOrderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.all_pending_orders);
        pendingOrder_dao = B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao();
        reportsOrderVM = ViewModelProviders.of(this).get(ReportsOrderVM.class);

        rcvallpendingorderscreenadapter = new RCVAllPendingOrderScreenAdapter(this);


        initVar();
        refreshList();
        rcv_pendingOrderList.setAdapter(rcvallpendingorderscreenadapter);
    }

    @Override
    protected void onResume() {

        super.onResume();
        tv_from_date.setText("");
        tv_to_date.setText("");
        clearColor();
        bottombar_five.setBackgroundColor(getResources().getColor(R.color.iSteerColor1));
	/*	if(toUpdateCustomerDetail)
		{
			toUpdateCustomerDetail = false;

		}*/
        //refreshList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (disposableBilldata != null && !(disposableBilldata.isDisposed())) {
            disposableBilldata.dispose();
        } else if (disposableOrderData != null && !(disposableOrderData.isDisposed())) {
            disposableOrderData.dispose();
        }
        super.onDestroy();

    }

    private void initVar() {

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);
        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Orders");

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);

        bottombar_one = ((LinearLayout) findViewById(R.id.bottombar_one));
        bottombar_one.setOnClickListener(this);
        bottombar_two = ((LinearLayout) findViewById(R.id.bottombar_two));
        bottombar_two.setOnClickListener(this);
        bottombar_three = ((LinearLayout) findViewById(R.id.bottombar_three));
        bottombar_three.setOnClickListener(this);
        bottombar_four = ((LinearLayout) findViewById(R.id.bottombar_four));
        bottombar_four.setOnClickListener(this);
        bottombar_five = ((LinearLayout) findViewById(R.id.bottombar_five));
        bottombar_five.setOnClickListener(this);

        pendingOrderList = ((ListView) findViewById(R.id.pendingOrderList));
        rcv_pendingOrderList = (findViewById(R.id.rcv_pendingOrderList));
        rcv_pendingOrderList.setLayoutManager(new LinearLayoutManager(this));


        tv_from_date = findViewById(R.id.tv_from_date);
        tv_from_date.setOnClickListener(this);
        tv_to_date = findViewById(R.id.tv_to_date);
        tv_to_date.setOnClickListener(this);
        findViewById(R.id.til_to_date).setOnClickListener(this);
        findViewById(R.id.til_to_date).setOnClickListener(this);


        tv_from_date.setText("");
        tv_to_date.setText("");
        srl_refresh = findViewById(R.id.srl_refresh);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    tv_from_date.setText("");
                    tv_to_date.setText("");
                    startSyncPendingOrders();
                    //startSyncNewCollections();
                    refreshList();
                } else {
                    Toast.makeText(B2CAllOrderScreen.this, ""+getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                    srl_refresh.setRefreshing(false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        gotoB2CMenuScreen();
        //goBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            gotoB2CMenuScreen();

        }
        return true;

    }

    private void goBack() {
        gotoB2CCounterDetailScreen();
    }

    private void refreshList() {
        reportsOrderVM.init(pendingOrder_dao);
        reportsOrderVM.pendingAllOrderDataList.observe(this, pagedList -> {
            rcvallpendingorderscreenadapter.submitList(pagedList);
            if (pagedList.size() != 0) {
                findViewById(R.id.lt_empty).setVisibility(View.GONE);
                findViewById(R.id.srl_refresh).setVisibility(View.VISIBLE);
            }else {
                findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
                findViewById(R.id.srl_refresh).setVisibility(View.GONE);
            }
        });

    }

    private void refreshSearchDateList() {

        if (tv_from_date.getText().length() != 0) {
            fromDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                    B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat3, "" + tv_from_date.getText()));

        } else {
            fromDate = "2";
            ;
        }
        if ((tv_to_date.getText().length() != 0)) {
            toDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                    B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat3, "" + tv_to_date.getText()));

        } else {
            toDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date());

        }


        reportsOrderVM.init(pendingOrder_dao, fromDate, toDate);


        reportsOrderVM.pendingOrderDataList.observe(this, pagedList -> {
            rcvallpendingorderscreenadapter.submitList(pagedList);
            if (pagedList.size() != 0) {
                findViewById(R.id.lt_empty).setVisibility(View.GONE);
            }

        });
        //ArrayList<OrderNewData> orderNewDataArray = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().getReportOrderByDate(fromDate,toDate);
        //	Logger.LogError("orderNewDataArray",""+orderNewDataArray.size());
	/*	rcvallpendingorderscreenadapter = new RCVAllPendingOrderScreenAdapter(this, new B2CBasicUtils.ClickListener() {
			@Override
			public void onclickCheckListener(int position) {
				B2CApp.b2cUtils.updateMaterialProgress(B2CAllOrderScreen.this,""+B2CAppConstant.LOADING);
				Logger.LogError("onclick","pendingOrder");
				B2CCounterDetailScreen.currentMap = orderNewDataArray.get(position);
				B2CCounterDetailScreen.toUpdateCounterDetail = true;
				gotoB2CCounterDetailScreen();
			}
		});
		rcv_pendingOrderList.setAdapter(rcvallpendingorderscreenadapter);*/

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
            case R.id.btn_header_right:
                gotoB2CProductsCatalogue();
                break;


            case R.id.bottombar_one:
                clearColor();
                bottombar_one.setBackgroundColor(getResources().getColor(R.color.iSteerColor1));

                gotoB2CMenuScreen();
                break;

            case R.id.bottombar_two:
                clearColor();
                bottombar_two.setBackgroundColor(getResources().getColor(R.color.iSteerColor1));
                gotoB2CSyncScreen();
                break;

            case R.id.bottombar_three:
                clearColor();
                bottombar_three.setBackgroundColor(getResources().getColor(R.color.iSteerColor1));
                gotoB2CCustListScreen();
                break;

            case R.id.bottombar_four:
                clearColor();
                bottombar_four.setBackgroundColor(getResources().getColor(R.color.iSteerColor1));
                gotoB2CCountersScreen();
                break;

            case R.id.bottombar_five:
                clearColor();
                bottombar_five.setBackgroundColor(getResources().getColor(R.color.iSteerColor1));
                gotoB2CPendingOrders();
                break;
            case R.id.til_from_date:
            case R.id.tv_from_date:
                showDatePicker(true);
                break;

            case R.id.til_to_date:
            case R.id.tv_to_date:
                showDatePicker(false);
                break;
        }

    }

    private void showDatePicker(final boolean isFrom) {

        Calendar c = Calendar.getInstance();
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mMonth = c.get(Calendar.MONTH);
        int mYear = c.get(Calendar.YEAR);


        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar tCalendar = Calendar.getInstance();
                tCalendar.set(year, monthOfYear, dayOfMonth);

                String tDate = "" + DateFormat.format(B2CAppConstant.dateFormat3, tCalendar.getTime());

                if (isFrom) {


                    tv_from_date.setText(tDate);
                    Logger.LogError("fromDate1", "" + tv_from_date.getText().toString());
                    //tv_to_date.setText(tDate);
                } else {
                    tv_to_date.setText(tDate);
                    Logger.LogError("tv_to_date1", "" + tv_to_date.getText().toString());
                }

                refreshSearchDateList();

            }

        }, mYear, mMonth, mDay);

        dpd.show();


    }

    public void clearColor() {
        bottombar_one.setBackgroundColor(getResources().getColor(R.color.transparent_background));
        bottombar_two.setBackgroundColor(getResources().getColor(R.color.transparent_background));
        bottombar_three.setBackgroundColor(getResources().getColor(R.color.transparent_background));
        bottombar_four.setBackgroundColor(getResources().getColor(R.color.transparent_background));
        bottombar_five.setBackgroundColor(getResources().getColor(R.color.transparent_background));
    }

    private void updateCurrentCustomerData(Cursor syncCursor) {/*
		currentCustomer = new CustomerData();
		
		int columnIndexCmkey = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmkey.name());
		int columnIndexUserkey = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.userkey.name());
		int columnIndexCompany_name = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.company_name.name());
		int columnIndexCmp_phone1 = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmp_phone1.name());
		int columnIndexCmp_email = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmp_email.name());
		int columnIndexAddress1 = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address1.name());
		int columnIndexAddress2 = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address2.name());
		int columnIndexAddress3 = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address3.name());
		int columnIndexCity = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.city.name());
		int columnIndexAreaName = syncCursor.getColumnIndex(DSRTableCreate.COLOUMNS_CONTACT_MASTER.area_name.name());

		currentCustomer.setCmkey(syncCursor.getString(columnIndexCmkey));
		currentCustomer.setUserkey(syncCursor.getString(columnIndexUserkey));
		currentCustomer.setCompany_name(syncCursor.getString(columnIndexCompany_name));
		currentCustomer.setCmp_phone1(syncCursor.getString(columnIndexCmp_phone1));
		currentCustomer.setCmp_email(syncCursor.getString(columnIndexCmp_email));
		currentCustomer.setAddress1(syncCursor.getString(columnIndexAddress1));
		currentCustomer.setAddress2(syncCursor.getString(columnIndexAddress2));
		currentCustomer.setAddress3(syncCursor.getString(columnIndexAddress3));
		currentCustomer.setCity(syncCursor.getString(columnIndexCity));
		currentCustomer.setArea_name(syncCursor.getString(columnIndexAreaName));

		syncCursor.close();
		
		tv_customer_name.setText(""+currentCustomer.getCompany_name());
		tv_customer_phone.setText(""+currentCustomer.getCmp_phone1());
		tv_customer_email.setText(""+currentCustomer.getCmp_email());
		tv_customer_area_name.setText(""+currentCustomer.getArea_name());
		tv_customer_address.setText(""+currentCustomer.getAddress1() + " " + currentCustomer.getAddress2() + " " + currentCustomer.getAddress3() + " " + currentCustomer.getCity());

	*/
    }

    public void viewPOActionClicked(OrderNewData tMap) {
        B2CCounterDetailScreen.currentMap = tMap;
        B2CCounterDetailScreen.toUpdateCounterDetail = true;
        gotoB2CCounterDetailScreen();
    }


    private void gotoB2CCustListScreen() {

        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CPendingOrders() {

        startActivity(new Intent(this, B2CAllOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CSyncScreen() {

        startActivity(new Intent(this, B2CSyncScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCountersScreen() {

        startActivity(new Intent(this, B2CCountersScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_PENDING_ORDER;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCounterDetailScreen() {

        startActivity(new Intent(this, B2CCounterDetailScreen.class)
                .putExtra(B2CAppConstant.BACKTOCOUNTERDETAILS, B2CAppConstant.REPORTORDER)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void startSyncNewCollections() {
        ArrayList<CollectionData> collectionDataArrayList = (ArrayList<CollectionData>) B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().fetchCustomSelection();
		/*boolean isCollectionsUptodate = (B2CApp.b2cLdbs.fetchCustomSelection(this, B2CTableCreate.TABLE_B2C_COLLECTIONS, null, B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name() + " < "+ "0"
									+ " OR " + B2CTableCreate.COLOUMNS_COLLECTIONS.is_synced_to_server.name() + " = " + "0")==null);*/

        if (collectionDataArrayList.size() > 0) {
            syncAllCollectionsToServer();
        } else
            startSyncPendingOrders();
    }

    private void syncAllCollectionsToServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                    + B2CAppConstant.METHOD_INSERT_ALL_COLLECTION, getJsonParams(B2CAppConstant.METHOD_INSERT_ALL_COLLECTION), B2CAppConstant.METHOD_INSERT_ALL_COLLECTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncAllpendingBillToServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                    + B2CAppConstant.METHOD_GET_ALLPENDINGBILLS, getJsonParams(B2CAppConstant.METHOD_GET_ALLPENDINGBILLS), B2CAppConstant.METHOD_GET_ALLPENDINGBILLS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updatePendingOrdersToServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                    + B2CAppConstant.METHOD_UPDATE_ALL_ORDERS, getJsonParams(B2CAppConstant.METHOD_UPDATE_ALL_ORDERS), B2CAppConstant.METHOD_UPDATE_ALL_ORDERS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncAllPendingOrdersFromServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                    + B2CAppConstant.METHOD_GET_ALLPENDINGORDERS, getJsonParams(B2CAppConstant.METHOD_GET_ALLPENDINGORDERS), B2CAppConstant.METHOD_GET_ALLPENDINGORDERS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startSyncPendingOrders() {


        ArrayList<PendingOrderData> pendingOrderDataArrayList = (ArrayList<PendingOrderData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().fetchCustomSelection();


        if (pendingOrderDataArrayList.size() > 0) {
          //  updatePendingOrdersToServer();
            srl_refresh.setRefreshing(false);
            Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
        } else
            syncAllPendingOrdersFromServer();

    }


    @Override
    public void handleResult(String method_name, JSONObject response) throws JSONException {
        onPostExecute(method_name, response);
    }

    @Override
    public void handleError(VolleyError e) {
        srl_refresh.setRefreshing(false);
    }

    private JSONObject getJsonParams(String opType) throws JSONException {

        JSONObject json = new JSONObject();

        if (opType == B2CAppConstant.METHOD_GET_ALLPENDINGBILLS
                || opType == B2CAppConstant.METHOD_GET_ALLPENDINGORDERS) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(DSRAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());
        } else if (opType == B2CAppConstant.METHOD_UPDATE_ALL_ORDERS) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());
            json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "");

            JSONArray tJsonArray = new JSONArray();
            ArrayList<PendingOrderData> pendingOrderDataArrayList = (ArrayList<PendingOrderData>) B2CApp.getINSTANCE().getRoomDB().
                    pendingorderdata_dao().fetchCustomSelection();

			/*Cursor mCursor = B2CApp.b2cLdbs.fetchCustomSelection(this, B2CTableCreate.TABLE_B2C_PENDING_ORDERS, null, B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_item_key.name() + " < "+ "0"
					+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0");*/

            if (pendingOrderDataArrayList.size() <= 0) {
                json.put(B2CAppConstant.KEY_DATA, tJsonArray);
                Logger.LogError("getJsonParams", "mCursor is null");
                return json;
            } else {
                Logger.LogError("getJsonParams", "mCursor is not null");
            }


            //is_synced_to_server

            for (int i = 0; i < pendingOrderDataArrayList.size(); i++) {
                PendingOrderData pendingOrderData = pendingOrderDataArrayList.get(i);
                JSONObject tJson = new JSONObject();

                Integer so_item_key = pendingOrderData.getSo_item_key();
                boolean isDummyKey = so_item_key != null && so_item_key < 0;
                if (isDummyKey) {
                    tJson.put(DSRAppConstant.KEY_DUMMY_KEY, "" + so_item_key);
                    tJson.put(B2CAppConstant.KEY_SO_ITEM_KEY, "");
                    tJson.put(B2CAppConstant.KEY_SO_KEY, "");
                } else {
                    tJson.put(DSRAppConstant.KEY_DUMMY_KEY, "");
                    tJson.put(B2CAppConstant.KEY_SO_ITEM_KEY, "" + so_item_key);
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
        } else if (opType == B2CAppConstant.METHOD_INSERT_ALL_COLLECTION) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());
            json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "");

            JSONArray tJsonArray = new JSONArray();
            ArrayList<CollectionData> collectionDataArrayList = (ArrayList<CollectionData>) B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().fetchCustomSelection();

			/*Cursor mCursor = B2CApp.b2cLdbs.fetchCustomSelection(this, B2CTableCreate.TABLE_B2C_COLLECTIONS, null, B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name() + " < "+ "0"
					+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0");*/

            if (collectionDataArrayList.size() <= 0) {
                json.put(B2CAppConstant.KEY_DATA, tJsonArray);
                return json;
            } else {
                Logger.LogError("getJsonParams", "mCursor is not null");
            }


            //is_synced_to_server

            for (int i = 0; i < collectionDataArrayList.size(); i++) {
                CollectionData collectionData = collectionDataArrayList.get(i);
                JSONObject tJson = new JSONObject();

                Integer pay_coll_key = collectionData.getPay_coll_key();
                boolean isDummyKey = pay_coll_key != null && pay_coll_key < 0;
                if (isDummyKey) {
                    tJson.put(DSRAppConstant.KEY_DUMMY_KEY, "" + pay_coll_key);
                    tJson.put(B2CAppConstant.KEY_PAY_COLL_KEY, "");
                    tJson.put(B2CAppConstant.KEY_SC_LEDGER_KEY, "");
                } else {
                    tJson.put(DSRAppConstant.KEY_DUMMY_KEY, "");
                    tJson.put(B2CAppConstant.KEY_PAY_COLL_KEY, "" + collectionData.getPay_coll_key());
                    tJson.put(B2CAppConstant.KEY_SC_LEDGER_KEY, collectionData.getSc_ledger_key());
                }

                tJson.put(B2CAppConstant.KEY_CUST_KEY, collectionData.getCus_key());
                tJson.put(B2CAppConstant.KEY_CONTACT_KEY, collectionData.getContact_key());
                tJson.put(B2CAppConstant.KEY_AMOUNT, collectionData.getAmount());
                tJson.put(B2CAppConstant.KEY_TRANS_DATE, collectionData.getTrans_date());
                tJson.put(B2CAppConstant.KEY_ENTERED_DATE_TIME, collectionData.getEntered_date_time());
                tJson.put(B2CAppConstant.KEY_PAYMENT_MODE, collectionData.getPayment_mode());
                tJson.put(B2CAppConstant.KEY_RECEIPT_NO, collectionData.getReceipt_no());
                tJson.put(B2CAppConstant.KEY_CHEQUE_NO, collectionData.getCheque_no());
                tJson.put(B2CAppConstant.KEY_CHEQUE_DATE, collectionData.getCheque_date());
                tJson.put(B2CAppConstant.KEY_BANK, collectionData.getBank());
                tJson.put(B2CAppConstant.KEY_REMARKS, collectionData.getRemarks());

                tJsonArray.put(tJson);

            }

            json.put(B2CAppConstant.KEY_ARRAY_DATA, tJsonArray);
        }

        return json;
    }

    protected void onPostExecute(String method_name, JSONObject responseString) {

        Logger.LogError("responseString", " : " + responseString);
        if (B2CApp.b2cUtils.isNetAvailable()) {
            if (responseString == null) {

                if (method_name == B2CAppConstant.METHOD_GET_ALLPENDINGBILLS) {

                    srl_refresh.setRefreshing(false);
                } else if (method_name == B2CAppConstant.METHOD_INSERT_ALL_COLLECTION) {
                    //startSyncNewCollections();
                    updatePendingOrdersToServer();
                } else if (method_name == B2CAppConstant.METHOD_UPDATE_ALL_ORDERS) {
                    // updatePendingOrdersToServer();
                    syncAllPendingOrdersFromServer();
                } else if (method_name == B2CAppConstant.METHOD_GET_ALLPENDINGORDERS) {
                    // updatePendingOrdersToServer();
                    syncAllpendingBillToServer();
                }
            } else {

                if (method_name == B2CAppConstant.METHOD_GET_ALLPENDINGBILLS)
                    onPostPendingBillsNew(responseString);
                else if (method_name == B2CAppConstant.METHOD_INSERT_ALL_COLLECTION)
                    onPostInsertAllCollections(responseString);
                else if (method_name == B2CAppConstant.METHOD_UPDATE_ALL_ORDERS)
                    onPostUpdateAllOrders(responseString);
                else if (method_name == B2CAppConstant.METHOD_GET_ALLPENDINGORDERS)
                    //   onPostPendingOrders(responseString);
                    onPostPendingOrdersNew(responseString);

            }
        }
    }


    protected void onPostInsertAllCollections(JSONObject outerJson) {

        JSONArray responseArray = null;
        try {
            responseArray = outerJson.getJSONArray(B2CAppConstant.KEY_DATA);
        } catch (JSONException e) {

            e.printStackTrace();
            Logger.LogError("onPostInsertAllCollections JSONException 1: ", e.toString());

            startSyncPendingOrders();

            return;
        }

        JSONObject innerJson;
        for (int i = 0; i < responseArray.length(); i++) {
            try {
                innerJson = responseArray.getJSONObject(i);

                if ((innerJson.has(B2CAppConstant.IS_SUCCESS) && innerJson.getInt(B2CAppConstant.IS_SUCCESS) == 1)
                        || (innerJson.has(B2CAppConstant.KEY_SUCCESS) && innerJson.getInt(B2CAppConstant.KEY_SUCCESS) == 1)) {
                    int updatecollection = B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().updateCollectionKey(
                            innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                            innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.cus_key.name()),
                            innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.sc_ledger_key.name()),
                            Integer.parseInt(innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name())),
                            1);
					/*B2CApp.b2cLdbs.updateCollectionKey(B2CSyncScreen.this, resultJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
							resultJson.getString(B2CAppConstant.KEY_CUST_KEY), resultJson.getString(B2CAppConstant.KEY_PAY_COLL_KEY),
							resultJson.getString(B2CAppConstant.KEY_SC_LEDGER_KEY));*/

                    Logger.LogError("is success : ", "true");
                } else {
                    Logger.LogError("is success : ", "false");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Logger.LogError("onPostInsertAllCollections JSONException for : ", e.toString());
            }
        }

        B2CApp.b2cPreference.setIsUpdatedCollections(true);

        startSyncPendingOrders();
    }

    protected void onPostUpdateAllOrders(JSONObject outerJson) {

        JSONArray responseArray = null;
        try {
            responseArray = outerJson.getJSONArray(B2CAppConstant.KEY_DATA);
        } catch (JSONException e) {

            e.printStackTrace();
            Logger.LogError("onPostUpdateAllOrders JSONException : ", e.toString());

            syncAllPendingOrdersFromServer();
            return;
        }

        JSONObject innerJson;
        for (int i = 0; i < responseArray.length(); i++) {
            try {
                innerJson = responseArray.getJSONObject(i);

                if ((innerJson.has(B2CAppConstant.IS_SUCCESS) && innerJson.getInt(B2CAppConstant.IS_SUCCESS) == 1) ||
                        (innerJson.has(B2CAppConstant.KEY_STATUS) && innerJson.getInt(B2CAppConstant.KEY_STATUS) == 1)) {

                  /*  B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().updatePendingOrderSoItemKeyData(innerJson.getString(B2CAppConstant.KEY_CUST_KEY),
                            innerJson.getString(B2CAppConstant.KEY_SO_KEY), innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY),
                            innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY));*/
                    if (Integer.parseInt(innerJson.getString(B2CAppConstant.KEY_SO_ITEM_QTY)) == 0) {


                        B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().deletePendingOrderSoKeyData(innerJson.getString(B2CAppConstant.KEY_CUST_KEY),
                                innerJson.getString(B2CAppConstant.KEY_SO_KEY), innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));
                    } else {


                        B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().updatePendingOrderSoItemKeyData(innerJson.getString(B2CAppConstant.KEY_CUST_KEY),
                                innerJson.getString(B2CAppConstant.KEY_SO_KEY), innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY),
                                innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY));
                    }
					/*B2CApp.b2cLdbs.updatePendingOrder(B2CSyncScreen.this, innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
							innerJson.getString(B2CAppConstant.KEY_CUST_KEY), innerJson.getString(B2CAppConstant.KEY_SO_KEY),
									innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));*/

                    Logger.LogError("is success : ", "true");
                } else {
                    Logger.LogError("is success : ", "false");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Logger.LogError("onPostUpdateAllOrders JSONException for : ", e.toString());
            }
        }

        B2CApp.b2cPreference.setIsUpdatedPendingOrders(true);

        syncAllPendingOrdersFromServer();
    }

    private void onPostPendingOrdersNew(JSONObject responseString) {
        B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().clearTable();
        Gson_PendingOrders gson_pendingOrders = new Gson().fromJson(responseString.toString(), Gson_PendingOrders.class);
        Logger.LogError("gson_pendingOrders.getOrderDataList().size()", "" + gson_pendingOrders.getOrderDataList().size());

        if (gson_pendingOrders.getOrderDataList().size() == 0) {


        } else {

            disposableOrderData = Observable.fromArray(gson_pendingOrders)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(orderDataList -> {
                        B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().insertAllPendingOrderData(orderDataList.getOrderDataList());
                    }, throwable ->
                            Logger.LogError("exception", "" + throwable.toString()));
        }


        B2CApp.b2cPreference.setIsFilledPendingOrders(true);

        syncAllpendingBillToServer();
    }

    private void onPostPendingBillsNew(JSONObject responseString) {
        B2CApp.getINSTANCE().getRoomDB().billData_dao().clearTable();

        Gson_PendingBills gson_pendingBills = new Gson().fromJson(responseString.toString(), Gson_PendingBills.class);

        if (gson_pendingBills.getBillDataList().size() == 0) {
            Toast.makeText(this, "" + gson_pendingBills.getMsg(), Toast.LENGTH_SHORT).show();

        } else {
            disposableBilldata = Observable.fromArray(gson_pendingBills)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(billDataList -> {
                        B2CApp.getINSTANCE().getRoomDB().billData_dao().insertAllBillData(billDataList.getBillDataList());
                    }, throwable ->
                            Logger.LogError("exception", "" + throwable.toString()));


        }

        B2CApp.b2cPreference.setIsFilledPendingBills(true);
        //  syncCustomerCreditsFromServer();
        //syncCustomerAllCreditsFromServer();
        srl_refresh.setRefreshing(false);

    }

    @Override
    public void onclickListener(int position, OrderNewData item) {
        // B2CApp.b2cUtils.updateMaterialProgress(B2CAllOrderScreen.this,""+B2CAppConstant.LOADING);
        B2CCounterDetailScreen.currentMap = item;
        B2CCounterDetailScreen.toUpdateCounterDetail = true;
        gotoB2CCounterDetailScreen();
    }
}
