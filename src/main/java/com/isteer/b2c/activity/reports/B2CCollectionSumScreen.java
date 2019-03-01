package com.isteer.b2c.activity.reports;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
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
import com.isteer.b2c.activity.action.B2CCollectionEntryScreen;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.calender.DSRAddProgScreen;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.activity.counter_details.B2CLocateScreen;
import com.isteer.b2c.adapter.RCVCollectionAdapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.B2CTableCreate;
import com.isteer.b2c.gson.Gson_CustomerCollection;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.volley.VolleyHttpRequest;
import com.isteer.b2c.volley.VolleyTaskListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class B2CCollectionSumScreen extends AppCompatActivity implements OnClickListener, VolleyTaskListener {

    private TextView header_title;
    private ImageView btn_header_right;

    public static boolean isFromCounter = false;
    public static boolean isFromMenu = false;
    public static boolean backToCounter = true;

    private TextView tv_from_date, tv_to_date;

    public static String currentCounterName = "";

    private ListView collectionList;
    public static ArrayList<CollectionData> collectionEntries = new ArrayList<CollectionData>();
    private String fromDate, toDate;
    private RCVCollectionAdapter rcvcollectionadapter;
    private RecyclerView rcv_collectionList;
    private SwipeRefreshLayout srl_refresh;
    private Disposable disposableCustomerCollection;
    private LinearLayout lt_mapbill;
    private ImageView iv_mapbill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.scr_b2c_collection_summary);

        initVar();
    }

    @Override
    protected void onResume() {

        super.onResume();
        tv_from_date.setText("");
        tv_to_date.setText("");
        updateCollections();

        if (isFromCounter) {
            isFromCounter = false;
            backToCounter = true;
        } else if (isFromMenu) {
            isFromMenu = false;
            backToCounter = false;
        } else {

        }
        if (B2CApp.b2cUtils.isNetAvailable()) {
            iv_mapbill.setBackgroundResource(R.drawable.round_green);
        }else {
            iv_mapbill.setBackgroundResource(R.drawable.round_lightblue);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if (disposableCustomerCollection != null && !(disposableCustomerCollection.isDisposed())) {
            disposableCustomerCollection.dispose();
        }
        super.onDestroy();

    }

    private void initVar() {

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);
        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Collection");

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);

        findViewById(R.id.bottombar_one).setOnClickListener(this);
        findViewById(R.id.bottombar_two).setOnClickListener(this);
        findViewById(R.id.bottombar_three).setOnClickListener(this);
        findViewById(R.id.bottombar_four).setOnClickListener(this);
        findViewById(R.id.bottombar_five).setOnClickListener(this);
        findViewById(R.id.btnAddCollection).setOnClickListener(this);
        findViewById(R.id.lt_mapbill).setOnClickListener(this);
        iv_mapbill = findViewById(R.id.iv_mapbill);

        collectionList = (ListView) findViewById(R.id.collectionList);
        rcv_collectionList = findViewById(R.id.rcv_collectionList);
        rcv_collectionList.setLayoutManager(new LinearLayoutManager(this));

        ((View) findViewById(R.id.ll_from_date)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_to_date)).setOnClickListener(this);

        tv_from_date = (TextView) findViewById(R.id.tv_from_date);
        tv_to_date = (TextView) findViewById(R.id.tv_to_date);
        findViewById(R.id.til_from_date).setOnClickListener(this);
        findViewById(R.id.til_to_date).setOnClickListener(this);
        tv_from_date.setOnClickListener(this);
        tv_to_date.setOnClickListener(this);
		
		/*tv_from_date.setText(""+DateFormat.format(B2CAppConstant.dateFormat3, new Date()));
		tv_to_date.setText(""+DateFormat.format(B2CAppConstant.dateFormat3, new Date()));*/
        tv_from_date.setText("");
        tv_to_date.setText("");

        srl_refresh = findViewById(R.id.srl_refresh);
        srl_refresh.setRefreshing(false);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    tv_from_date.setText("");
                    tv_to_date.setText("");
                    startSyncNewCollections();
                    updateCollections();
                } else {
                    Toast.makeText(B2CCollectionSumScreen.this, "" + getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                    srl_refresh.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        goBack();
    }


    private void goBack() {
        if (backToCounter) {
            gotoDSRCounterDetailScreen();
        }
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int backPress = extras.getInt(B2CAppConstant.BACKTOCOLLECTIONSUM);
            Logger.LogError("backPress", "" + backPress);
            if (backPress == B2CAppConstant.MENU_SCREEN) {
                gotoB2CMenuScreen();

            } else if (backPress == B2CAppConstant.SCREEN_COUNTER_DET)
                gotoDSRCounterDetailScreen();
            else
                gotoB2CMenuScreen();

        } else {
            gotoB2CMenuScreen();
        }
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
            default:
                break;
            case R.id.btn_header_right:
                gotoB2CProductsCatalogue();
                break;


            case R.id.bottombar_one:
                gotoDSRMenuScreen();
                break;

            case R.id.bottombar_two:
                gotoDSRSyncScreen();
                break;

            case R.id.bottombar_three:
                gotoDSRCustListScreen();
                break;

            case R.id.bottombar_four:
                gotoDSRCountersScreen();
                break;

            case R.id.bottombar_five:
                gotoDSRPendingOrders();
                break;

            case R.id.ll_from_date:
            case R.id.tv_from_date:
                showDatePicker(true);
                break;

            case R.id.ll_to_date:
            case R.id.tv_to_date:
                showDatePicker(false);
                break;


            case R.id.btnAddCollection:
                B2CCollectionEntryScreen.isFromCollectionSum = true;
                gotoB2cCollectionEntryScreen();
                break;
            case R.id.lt_mapbill:
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    gotoB2CCollectionMapBill();
                }else {
                    String message = getResources().getString(R.string.nointernetdescription);
                    AlertPopupDialog.noInternetAlert(this, message, new AlertPopupDialog.myOnClickListenerRight() {
                        @Override
                        public void onButtonClickRight() {
                            if (B2CApp.b2cUtils.isNetAvailable()) {
                                gotoB2CCollectionMapBill();
                            }
                        }
                    });

                }

                break;

        }
    }

    private void updateCollections() {
        if (tv_from_date.length() != 0 || tv_to_date.length() != 0) {
            if (tv_from_date.length() != 0 && tv_to_date.length() == 0) {
                fromDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                        B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat3, "" + tv_from_date.getText()));
                toDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date());
            } else if (tv_from_date.length() == 0 && tv_to_date.length() != 0) {
                fromDate = "2";
                toDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                        B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat3, "" + tv_to_date.getText()));
                ;
            } else if (tv_from_date.length() != 0 && tv_to_date.length() != 0) {
                fromDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                        B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat3, "" + tv_from_date.getText()));
                toDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                        B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat3, "" + tv_to_date.getText()));
                ;
            }
            collectionEntries.clear();
            collectionEntries = (ArrayList<CollectionData>) B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().getCollectionByDate(fromDate, toDate);
        } else {
            collectionEntries.clear();
            collectionEntries = (ArrayList<CollectionData>) B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().getAllCollection();

        }

        //	Cursor mCursor = B2CApp.b2cLdbs.fetchCollectionsByDate(this, fromDate, toDate);


        //	collectionEntries.addAll(new B2CCursorFactory().fetchAsCollections(mCursor, B2CTableCreate.getColoumnArrayTableCollections()));

        rcvcollectionadapter = new RCVCollectionAdapter(this, collectionEntries);
        if (collectionEntries.size() != 0) {
            findViewById(R.id.lt_empty).setVisibility(View.GONE);
            findViewById(R.id.srl_refresh).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.srl_refresh).setVisibility(View.GONE);
        }


        rcv_collectionList.setAdapter(rcvcollectionadapter);
    }

    private void showDatePicker(final boolean isFrom) {
        String cDate;
		
		/*if(isFrom)
			cDate = ""+tv_from_date.getText();
		else
			cDate = ""+tv_to_date.getText();
		
		String[] partics = cDate.split("-");
		int mDay = Integer.parseInt(partics[0]);
		int mMonth = Integer.parseInt(partics[1])-1;
		int mYear = Integer.parseInt(partics[2]);*/
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
                    //tv_to_date.setText(tDate);
                } else
                    tv_to_date.setText(tDate);

                updateCollections();
            }

        }, mYear, mMonth, mDay);

        dpd.show();
    }

    private void gotoDSRAddProgScreen() {

        startActivity(new Intent(this, DSRAddProgScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRCounterDetailScreen() {

        startActivity(new Intent(this, B2CCounterDetailScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }


    private void gotoDSRMapScreen() {

        startActivity(new Intent(this, B2CLocateScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }


    private void gotoDSRMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRCustListScreen() {

        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRSyncScreen() {

        startActivity(new Intent(this, B2CSyncScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRPendingOrders() {

        startActivity(new Intent(this, B2CPendingOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRCountersScreen() {

        startActivity(new Intent(this, B2CCountersScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2cCollectionEntryScreen() {

        startActivity(new Intent(this, B2CCollectionEntryScreen.class)
                .putExtra("backPress", B2CAppConstant.COLLECTION_SCREEN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private void gotoB2CCollectionMapBill() {

        startActivity(new Intent(this, B2CCollectionMapBill.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_COLL_SUMM;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void startSyncNewCollections() {
        ArrayList<CollectionData> collectionDataArrayList = (ArrayList<CollectionData>) B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().fetchCustomSelection();

        if (collectionDataArrayList.size() > 0) {
            srl_refresh.setRefreshing(false);
            Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();
            //  syncInsertAllCollectionsToServer();
        } else {
            syncAllCollectionFromServer();
        }

    }

    private void syncInsertAllCollectionsToServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                            + B2CAppConstant.METHOD_INSERT_ALL_COLLECTION, getJsonParams(B2CAppConstant.METHOD_INSERT_ALL_COLLECTION)
                    , B2CAppConstant.METHOD_INSERT_ALL_COLLECTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncAllCollectionFromServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                            + B2CAppConstant.METHOD_GET_ALL_COLLECTION, getJsonParams(B2CAppConstant.METHOD_GET_ALL_COLLECTION)
                    , B2CAppConstant.METHOD_GET_ALL_COLLECTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonParams(String opType) throws JSONException {

        JSONObject json = new JSONObject();

        if (opType == B2CAppConstant.METHOD_INSERT_ALL_COLLECTION) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());
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
        } else if (opType == B2CAppConstant.METHOD_GET_ALL_COLLECTION) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());

        }

        return json;
    }

    @Override
    public void handleResult(String method_name, JSONObject response) throws JSONException {
        Logger.LogError("method_name", "" + method_name);
        if (response == null) {

            if (method_name == B2CAppConstant.METHOD_INSERT_ALL_COLLECTION) {
                syncAllCollectionFromServer();
            } else if (method_name == B2CAppConstant.METHOD_GET_ALL_COLLECTION) {
                srl_refresh.setRefreshing(false);
            }
        } else {

            if (method_name == B2CAppConstant.METHOD_INSERT_ALL_COLLECTION)
                onPostInsertAllCollections(response);
            else if (method_name == B2CAppConstant.METHOD_GET_ALL_COLLECTION)
                onPostGetAllCollectionNew(response);

        }
    }

    @Override
    public void handleError(VolleyError e) {
        srl_refresh.setRefreshing(false);
    }

    public void onPostExecute(JSONObject responseString, String method_name) {

        if (B2CApp.b2cUtils.isNetAvailable()) {
            if (responseString == null) {

                if (method_name == B2CAppConstant.METHOD_INSERT_ALL_COLLECTION) {
                    //startSyncNewCollections();
                    syncAllCollectionFromServer();
                } else if (method_name == B2CAppConstant.METHOD_GET_ALL_COLLECTION) {
                    //startSyncNewCollections();
                    srl_refresh.setRefreshing(false);
                }
            } else {

                if (method_name == B2CAppConstant.METHOD_INSERT_ALL_COLLECTION)
                    onPostInsertAllCollections(responseString);
                else if (method_name == B2CAppConstant.METHOD_GET_ALL_COLLECTION)
                    onPostGetAllCollectionNew(responseString);

            }

        } else {
            srl_refresh.setRefreshing(false);
            Toast.makeText(this, "* No internet available", Toast.LENGTH_SHORT).show();

        }
    }

    protected void onPostInsertAllCollections(JSONObject outerJson) {

        JSONArray responseArray = null;
        try {
            if (outerJson.has(B2CAppConstant.KEY_STATUS) && outerJson.getInt(B2CAppConstant.KEY_STATUS) == 1
                    && outerJson.has(B2CAppConstant.KEY_DATA)) {
                responseArray = outerJson.getJSONArray(B2CAppConstant.KEY_DATA);


                JSONObject innerJson;
                for (int i = 0; i < responseArray.length(); i++) {
                    try {
                        innerJson = responseArray.getJSONObject(i);

                        if ((innerJson.has(B2CAppConstant.IS_SUCCESS) && innerJson.getInt(B2CAppConstant.IS_SUCCESS) == 1)
                                || (innerJson.has(B2CAppConstant.KEY_STATUS) && innerJson.getInt(B2CAppConstant.KEY_STATUS) == 1)) {
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
            } else {
                Logger.LogError("is success : ", "false");
            }


        } catch (JSONException e) {

            e.printStackTrace();
            Logger.LogError("onPostInsertAllCollections JSONException 1: ", e.toString());

            syncAllCollectionFromServer();

            return;
        }

        B2CApp.b2cPreference.setIsUpdatedCollections(true);

        syncAllCollectionFromServer();
    }

    private void onPostGetAllCollectionNew(JSONObject responseString) {
        B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().clearTable();
        Gson_CustomerCollection gsonCustomerCollection = new Gson().fromJson(responseString.toString(), Gson_CustomerCollection.class);

        if (gsonCustomerCollection.getCollectionList().size() == 0) {


        } else {
            disposableCustomerCollection = Observable.fromArray(gsonCustomerCollection)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(customerCollection -> {
                        B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().insertAllCollectionData(customerCollection.getCollectionList());
                    }, throwable ->
                            Logger.LogError("exception", "" + throwable.toString()));
        }


        srl_refresh.setRefreshing(false);


    }
}
