package com.isteer.b2c.activity.action;

import java.util.ArrayList;
import java.util.HashMap;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.adapter.RCVAllPendingOrderScreenAdapter;
import com.isteer.b2c.adapter.RCV_PendingOrderScreenAdapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.dao.PendingOrderData_DAO;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.repository.ReportsOrderVM;
import com.isteer.b2c.utility.Logger;

public class B2CPendingOrderScreen extends AppCompatActivity implements OnClickListener {

    private TextView header_title;
    private ImageView btn_header_right;

    public static boolean toUpdateCustomerDetail = false;
    public static ArrayList<HashMap<String, String>> listEntries = new ArrayList<HashMap<String, String>>();

    private ListView pendingOrderList;
    private RecyclerView rcv_pendingOrderList;
    private RCV_PendingOrderScreenAdapter rcv_pendingorderscreenadapter;

    public static String currentCounterName = "";

    private TextView tv_counter_name, tv_customer_area_name, tv_customer_phone, tv_customer_email, tv_customer_address;
    private LinearLayout bottombar_one, bottombar_two, bottombar_three, bottombar_four, bottombar_five;
    private PendingOrderData_DAO pendingOrder_dao;
    private ReportsOrderVM reportsOrderVM;
    private EditText et_search_hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.scr_isr_pending_orders);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        pendingOrder_dao = B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao();
        reportsOrderVM = ViewModelProviders.of(this).get(ReportsOrderVM.class);
        initVar();
        refreshList();
    }

    @Override
    protected void onResume() {

        super.onResume();
        clearColor();
        bottombar_five.setBackgroundColor(getResources().getColor(R.color.iSteerColor1));
		/*if(toUpdateCustomerDetail)
		{
			toUpdateCustomerDetail = false;

		}*/

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);
        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Pending Orders");

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
        rcv_pendingOrderList = findViewById(R.id.rcv_pendingOrderList);
        rcv_pendingOrderList.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.lt_search_hint).setOnClickListener(this);
        et_search_hint =    findViewById(R.id.et_search_hint);
        et_search_hint.setOnClickListener(this);
        et_search_hint.setFocusable(false);
        et_search_hint.setClickable(false);


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
        rcv_pendingorderscreenadapter = new RCV_PendingOrderScreenAdapter(this, new RCVAllPendingOrderScreenAdapter.ClickListener() {

            @Override
            public void onclickListener(int position, OrderNewData item) {
                //	B2CApp.b2cUtils.updateMaterialProgress(B2CPendingOrderScreen.this,""+B2CAppConstant.LOADING);
                B2CCounterDetailScreen.currentMap = item;
                B2CCounterDetailScreen.toUpdateCounterDetail = true;
                gotoB2CCounterDetailScreen();
            }
        });
        reportsOrderVM.ReportPendingAllOrderDataList.observe(this, pagedList -> {
            rcv_pendingorderscreenadapter.submitList(pagedList);
            if (pagedList.size() != 0) {
                findViewById(R.id.lt_empty).setVisibility(View.GONE);
            }

            rcv_pendingOrderList.setAdapter(rcv_pendingorderscreenadapter);
        });

	/*	ArrayList<OrderNewData> orderNewDataArrayList = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().getActionOrder();
		Logger.LogError("orderNewDataArrayList",""+orderNewDataArrayList.size());
		rcv_pendingorderscreenadapter = new RCV_PendingOrderScreenAdapter(this, orderNewDataArrayList, new B2CBasicUtils.ClickListener() {
			@Override
			public void onclickCheckListener(int position) {
				B2CApp.b2cUtils.updateMaterialProgress(B2CPendingOrderScreen.this,""+B2CAppConstant.LOADING);
				B2CCounterDetailScreen.currentMap = orderNewDataArrayList.get(position);
				B2CCounterDetailScreen.toUpdateCounterDetail = true;
				gotoB2CCounterDetailScreen();
			}
		});
		rcv_pendingOrderList.setAdapter(rcv_pendingorderscreenadapter);*/

    }

    @Override
    public void onClick(View pView) {

        switch (pView.getId()) {
            case R.id.img_back:
//                finish();
                gotoB2CMenuScreen();
                break;
            case R.id.img_home:
                gotoB2CMenuScreen();
                break;
            case R.id.btn_header_right:
                gotoB2CProductsCatalogue();
                break;

            case R.id.lt_search_hint:
            case R.id.et_search_hint:
                gotoB2CCustListScreen();
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

        }
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
        Logger.LogError("Map.getCmkey(", "" + tMap.getCmkey());
        B2CCounterDetailScreen.currentMap = tMap;
        B2CCounterDetailScreen.toUpdateCounterDetail = true;
        gotoB2CCounterDetailScreen();
    }


    private void gotoB2CCustListScreen() {

        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CPendingOrders() {

        startActivity(new Intent(this, B2CPendingOrderScreen.class)
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
                .putExtra(B2CAppConstant.BACKTOCOUNTERDETAILS, B2CAppConstant.ACTIONORDER)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

}
