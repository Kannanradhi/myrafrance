package com.isteer.b2c.activity.B2CLancher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.isteer.b2c.activity.action.B2CCollectionEntryScreen;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.activity.calender.DSRAddProgScreen;
import com.isteer.b2c.activity.calender.DSREditProgScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.adapter.RCV_DSRCustAdapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.B2CTableCreate;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.R;
import com.isteer.b2c.gson.Gson_CustomerDetails;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.volley.VolleyHttpRequest;
import com.isteer.b2c.volley.VolleyTaskListener;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class B2CCustSearchScreen extends AppCompatActivity implements OnClickListener, VolleyTaskListener {

    private static final String TAG = "B2CCustListScreen";
    public static ArrayList<String> listEntries = new ArrayList<String>();
    public static ArrayList<String> cmkeyEntries = new ArrayList<String>();
    public static ArrayList<String> cuskeyEntries = new ArrayList<String>();
    public static ArrayList<String> areaNameEntries = new ArrayList<String>();
    public static boolean isFromAddProg = false;
    RCV_DSRCustAdapter rcv_dsrcustadapter;
    private Disposable disposableCustomer;
    private RecyclerView rcv_nameEntryList;
    private ImageView btn_header_right;
    private TextView header_title;
    private EditText et_search_hint;
    private String strHint = "";
    private LinearLayout bottombar_one, bottombar_two, bottombar_three, bottombar_four, bottombar_five;
    private Typeface raleway_bold, raleway;
    private ArrayList<OrderNewData> customerDataArrayList;
    private SwipeRefreshLayout srl_refresh;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scr_isr_customer_search);

        initVar();

        //  Logger.LogError("currentDate", "" + currentDate);
        //  Logger.LogError("B2CApp.b2cPreference.getTodayobjCurrentdate()", "" + B2CApp.b2cPreference.getTodayobjCurrentdate());


    }

    @Override
    protected void onResume() {

        super.onResume();
        currentDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());
        if (B2CApp.b2cUtils.isNetAvailable() && (B2CApp.b2cPreference.getTodayobjCurrentdate() != Integer.parseInt(currentDate))) {

            B2CApp.b2cUtils.updateMaterialProgress(this, "Lodding Customer...");
            syncCustomersFromServer();

        } else {
            et_search_hint.getText().clear();
            suggestList();
        }

    }

    @Override
    protected void onDestroy() {
        if (disposableCustomer != null && !(disposableCustomer.isDisposed())) {
            disposableCustomer.dispose();
        }
        super.onDestroy();
    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);
        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Customer search");
        //header_title.setTypeface(raleway_bold);

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);

        // nameEntryList = ((ListView) findViewById(R.id.nameEntryList));
        rcv_nameEntryList = ((RecyclerView) findViewById(R.id.rcv_nameEntryList));
        rcv_nameEntryList.setLayoutManager(new LinearLayoutManager(this));
        et_search_hint = ((EditText) findViewById(R.id.et_search_hint));
        //et_search_hint.setTypeface(raleway);
        et_search_hint.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (disposableCustomer != null && !(disposableCustomer.isDisposed())) {
                    disposableCustomer.dispose();
                }
                strHint = et_search_hint.getText().toString();
                suggestList();

            }

        });


       /* nameEntryList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v,
                                    int position, long id) {

                if (isFromAddProg) {
                    isFromAddProg = false;
                    DSRAddProgScreen.toUpdateCustomerName = true;
                    DSREditProgScreen.currentEvent.setCustomer_name(listEntries.get(position));
                    DSREditProgScreen.currentEvent.setArea(areaNameEntries.get(position));
                    DSREditProgScreen.currentEvent.setCmkey(cmkeyEntries.get(position));
                    DSREditProgScreen.currentEvent.setCus_key(cuskeyEntries.get(position));
                    gotoDSRAddProgScreen();
                } else if (B2CCollectionEntryScreen.isFromCollection) {
                    B2CCollectionEntryScreen.isFromCollection = false;
                    B2CCollectionEntryScreen.isFromCustList = true;
                    B2CCollectionEntryScreen.currentCusKey = cuskeyEntries.get(position);
                    B2CCollectionEntryScreen.currentCustomerName = listEntries.get(position);
                    gotoB2CCollectionEntryScreen();
                } else {
                    OrderNewData ordernewdata = customerDataArrayList.get(position);
                    HashMap<String, String> tMap = new HashMap<String, String>();
                    tMap.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), listEntries.get(position));
                    tMap.put(B2CTableCreate.COLOUMNS_CONTACT_MASTER.cmkey.name(), cmkeyEntries.get(position));
                    tMap.put(B2CTableCreate.COLOUMNS_CONTACT_MASTER.cus_key.name(), cuskeyEntries.get(position));
                    B2CCounterDetailScreen.currentMap = ordernewdata;
                    B2CCounterDetailScreen.toUpdateCounterDetail = true;
                    gotoB2CCounterDetailScreen();
                }
            }
        });*/
        srl_refresh = findViewById(R.id.srl_refresh);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    syncCustomersFromServer();
                } else {
                    Toast.makeText(B2CCustSearchScreen.this, R.string.please_try_again, Toast.LENGTH_SHORT).show();
                    suggestList();
                    srl_refresh.setRefreshing(false);
                }
            }
        });

    }

    public class syncCustomersFromSer extends TimerTask {


        @Override
        public void run() {
            Logger.LogError("synserver", "syncserver");
            if (B2CApp.b2cUtils.isNetAvailable())
                syncCustomersFromServer();
        }
    }

    private void suggestList() {


        customerDataArrayList = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().customerData_dao().fetchCustomerSearch("%" + strHint + "%");
        if (customerDataArrayList.size() < 1) {
            findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.srl_refresh).setVisibility(View.GONE);
        } else {
            findViewById(R.id.lt_empty).setVisibility(View.GONE);
            findViewById(R.id.srl_refresh).setVisibility(View.VISIBLE);


        }
        rcv_dsrcustadapter = new RCV_DSRCustAdapter(this, customerDataArrayList, new RCV_DSRCustAdapter.myClickListner() {
            @Override
            public void onClickListener(int position) {
                onClickDirection(position);
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

    private void searchSuggestList() {


        ArrayList<OrderNewData> filtercustomerDataArrayList = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().customerData_dao().fetchCustomerSearch("%" + strHint + "%");
        if (filtercustomerDataArrayList.size() < 1) {
            findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.srl_refresh).setVisibility(View.GONE);
        } else {
            findViewById(R.id.lt_empty).setVisibility(View.GONE);
            findViewById(R.id.srl_refresh).setVisibility(View.VISIBLE);


        }

        if (rcv_dsrcustadapter != null) {
            rcv_dsrcustadapter.refreshList(filtercustomerDataArrayList);
        }

    }


    public void onClickDirection(int position) {
        OrderNewData orderNewData = customerDataArrayList.get(position);
        if (isFromAddProg) {
            isFromAddProg = false;
            DSRAddProgScreen.toUpdateCustomerName = true;
            DSREditProgScreen.currentEvent.setCustomer_name(orderNewData.getCompany_name());
            DSREditProgScreen.currentEvent.setArea(orderNewData.getArea());
            DSREditProgScreen.currentEvent.setCmkey(orderNewData.getCmkey());
            DSREditProgScreen.currentEvent.setCus_key(orderNewData.getCus_key());
            gotoDSRAddProgScreen();
        } else if (B2CCollectionEntryScreen.isFromCollection) {
            B2CCollectionEntryScreen.isFromCollection = false;
            B2CCollectionEntryScreen.isFromCustList = true;
            B2CCollectionEntryScreen.currentCusKey = orderNewData.getCus_key();
            B2CCollectionEntryScreen.currentCustomerName = orderNewData.getCompany_name();
            gotoB2CCollectionEntryScreen();
        } else {
            B2CApp.b2cUtils.updateMaterialProgress(this, B2CAppConstant.LOADING);
            HashMap<String, String> tMap = new HashMap<String, String>();
            tMap.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), orderNewData.getCustomer_name());
            tMap.put(B2CTableCreate.COLOUMNS_CONTACT_MASTER.cmkey.name(), orderNewData.getCmkey());
            tMap.put(B2CTableCreate.COLOUMNS_CONTACT_MASTER.cus_key.name(), orderNewData.getCus_key());
            B2CCounterDetailScreen.currentMap = orderNewData;
            B2CCounterDetailScreen.toUpdateCounterDetail = true;
            gotoB2CCounterDetailScreen();
        }
    }

    @Override
    public void onBackPressed() {

        goBack();
        //gotoB2CMenuScreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            goBack();

        }
        return true;

    }

    private void goBack()

    {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int backPress = bundle.getInt(B2CAppConstant.BACKTOCUSTOMERSEARCH);
            //  Logger.LogError(B2CAppConstant.BACKTOCUSTOMERSEARCH, "" + backPress);

            if (backPress == B2CAppConstant.MENU_SCREEN) {
                intent.putExtra(B2CAppConstant.BACKTOCUSTOMERSEARCH, 0);
                gotoB2CMenuScreen();
            } else if (backPress == B2CAppConstant.COLLECTION_SCREEN) {
                intent.putExtra(B2CAppConstant.BACKTOCUSTOMERSEARCH, 0);
                gotoB2CCollectionEntryScreen();
            } else if (backPress == B2CAppConstant.ADDPROGSCREEN) {
                intent.putExtra(B2CAppConstant.BACKTOCUSTOMERSEARCH, 0);
                gotoDSRAddProgScreen();
            } else if (backPress == 0) {
                gotoB2CMenuScreen();
            }
        } else {
            gotoB2CMenuScreen();
        }


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


            default:
                break;
        }
    }

    private void gotoDSRAddProgScreen() {

        startActivity(new Intent(this, DSRAddProgScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
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

    private void gotoB2CCounterDetailScreen() {

        startActivity(new Intent(this, B2CCounterDetailScreen.class)
                .putExtra(B2CAppConstant.BACKTOCOUNTERDETAILS, B2CAppConstant.CUSTOMERSEARCH)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_CUST_SEARCH;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCollectionEntryScreen() {

        startActivity(new Intent(this, B2CCollectionEntryScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void syncCustomersFromServer() {
        //

        VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl2()
                + B2CAppConstant.METHOD_GET_CUST_DET, getJsonInput(), B2CAppConstant.METHOD_GET_CUST_DET);
    }

    private JSONObject getJsonInput() {
        JSONObject json = new JSONObject();
        try {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());

            json.put(DSRAppConstant.KEY_PUNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(DSRAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    @Override
    public void handleResult(String method_name, JSONObject response) throws JSONException {
        onPostCustomerDetailsNew(response);
    }

    @Override
    public void handleError(VolleyError e) {

    }

    public void onPostCustomerDetailsNew(JSONObject responseString) {


        Gson_CustomerDetails gson_customerDetails = new Gson().fromJson(responseString.toString(), Gson_CustomerDetails.class);
        if (gson_customerDetails.getCustomerDataList().size() == 0) {

            Toast.makeText(this, "" + gson_customerDetails.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            B2CApp.getINSTANCE().getRoomDB().customerData_dao().clearTable();

            Observable observable = Observable.fromArray(gson_customerDetails)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());


            disposableCustomer = observable.subscribe(new Consumer() {
                @Override
                public void accept(Object o) throws Exception {
                    B2CApp.getINSTANCE().getRoomDB().customerData_dao().insertAllCustomerData(((Gson_CustomerDetails) o).getCustomerDataList());
                    suggestList();
                }

            });
            B2CApp.b2cPreference.setTodayobjCurrentdate(Integer.parseInt(currentDate));
            B2CApp.b2cPreference.setIsDbFilled(true);
            B2CApp.b2cPreference.setIsFilledCustomers(true);

        }


        srl_refresh.setRefreshing(false);

    }
}
