package com.isteer.b2c.activity.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

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

import com.google.gson.Gson;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CLoginScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.activity.B2CLancher.B2CSplashScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.db.B2CTableCreate;
import com.isteer.b2c.gson.Gson_CustomerCredit;
import com.isteer.b2c.gson.Gson_CustomerDetails;
import com.isteer.b2c.gson.Gson_CustomerCollection;
import com.isteer.b2c.gson.Gson_PendingBills;
import com.isteer.b2c.gson.Gson_PendingOrders;
import com.isteer.b2c.gson.Gson_ProductMaster;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.model.CreditData;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.R;
import com.isteer.b2c.utility.Logger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class B2CSyncScreen extends AppCompatActivity implements OnClickListener {
    private Disposable disposableProduct, disposableCustomer, disposableCustomerCollection,
            disposableBilldata, disposableOrderData, disposableCreditData;
    private TextView header_title;
    private ImageView btn_header_left, btn_header_right;
    private TextView tv_sync_message_one, alertmessage;
    private ImageView spinnerImage;
    private Animation mRotateAnim;
    private Button btnSyncOK;
    private Handler myHandler = new Handler();

    private ArrayList<String> cusKeyList = new ArrayList<String>();

    public static boolean isSyncSuccess = false;
    public static boolean isSyncInProgress = false;
    public static boolean isCurrentSyncFailed = false;
    public static String currentCustomerKey = "-1";

    public static boolean isAllRecordsFetched = false;

    //public static boolean isByReset = true;
    public static int manufacturerCode = 0;

    //private int currentSync = SEMAppConstant.SYNC_TYPE_NOTHING;
    private LinearLayout bottombar_one, bottombar_two, bottombar_three, bottombar_four, bottombar_five;
    private String message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.scr_b2c_sync);

        initVar();
    }

    @Override
    protected void onDestroy() {

        if (disposableProduct != null && !disposableProduct.isDisposed()) {
            disposableProduct.dispose();
        } else if (disposableCustomer != null && !(disposableCustomer.isDisposed())) {
            disposableCustomer.dispose();
        } else if (disposableCustomerCollection != null && !(disposableCustomerCollection.isDisposed())) {
            disposableCustomerCollection.dispose();
        } else if (disposableBilldata != null && !(disposableBilldata.isDisposed())) {
            disposableBilldata.dispose();
        } else if (disposableOrderData != null && !(disposableOrderData.isDisposed())) {
            disposableOrderData.dispose();
        } else if (disposableCreditData != null && !(disposableCreditData.isDisposed())) {
            disposableCreditData.dispose();
        }
        super.onDestroy();

    }

    @Override
    protected void onResume() {

        super.onResume();
        clearColor();
        bottombar_two.setBackgroundColor(getResources().getColor(R.color.iSteerColor1));
        if (!B2CLoginScreen.isSessionValid())
            gotoSEMSplashScreen();
        else if (isSyncInProgress)
            return;
        else {
            if (!isSyncInProgress)
                resetProgression();

            if (B2CApp.b2cUtils.isNetAvailable()) {
                btnSyncOK.setAlpha(1.0f);
                alertmessage.setVisibility(View.GONE);
                Logger.LogError("B2CApp.b2cPreference.isDbFilled()", "" + B2CApp.b2cPreference.isDbFilled());
                Logger.LogError("B2CApp.b2cPreference.getDBFilledTime()", "" + B2CApp.b2cPreference.getDBFilledTime());
                if (!B2CApp.b2cPreference.isDbFilled() || B2CApp.b2cPreference.getDBFilledTime().equalsIgnoreCase("")) {
                    syncByDBFilled();
                } else {
                    btnSyncOK.setText("Sync");
                    updateMessageColor(B2CAppConstant.SYNC_STATUS_GREEN, "Sync Completed Successfully");
                }
            } else {
                toggleLoader(false);
                btnSyncOK.setAlpha(0.5f);
                btnSyncOK.setText("Sync");
                alertmessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void syncByDBFilled() {
        if (!B2CApp.b2cPreference.isFilledCustomers())
            syncCustomersFromServer();
        else if (!B2CApp.b2cPreference.isFilledPendingBills())
            syncAllPendingBillsFromServer();

        else if (!B2CApp.b2cPreference.isUpdatedCollections())
            startSyncNewCollections();
        else
            startSyncPendingOrders();
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

    private void startSyncPendingOrders() {

        if (!B2CApp.b2cPreference.isUpdatedPendingOrders()) {
            ArrayList<PendingOrderData> pendingOrderDataArrayList = (ArrayList<PendingOrderData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().fetchCustomSelection();
			/*boolean isOrdersUptodate = (B2CApp.b2cLdbs.fetchCustomSelection(this, B2CTableCreate.TABLE_B2C_PENDING_ORDERS, null, B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_item_key.name() + " < "+ "0"
										+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0")==null);*/

            if (pendingOrderDataArrayList.size() > 0) {
                updatePendingOrdersToServer();
            } else
                callsAfterOrderUpdates();
        } else
            callsAfterOrderUpdates();
    }

    private void callsAfterOrderUpdates() {


        if (!B2CApp.b2cPreference.isFilledPendingOrders())
            //   syncPendingOrdersFromServer();
            syncAllPendingOrdersFromServer();
        else if (!B2CApp.b2cPreference.isFilledCollection())
            syncAllCollectionFromServer();
        else if (!B2CApp.b2cPreference.isFilledProducts())
            syncProductsFromServer();
        else {
            btnSyncOK.setText("Sync");
            toggleLoader(false);
            updateMessageColor(B2CAppConstant.SYNC_STATUS_GREEN, "Sync Completed Successfully");
            isSyncInProgress = false;
        }
    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);

        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Sync Data");

        btn_header_left = (ImageView) findViewById(R.id.btn_header_left);
        btn_header_left.setVisibility(View.VISIBLE);
        btn_header_left.setOnClickListener(this);

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

        btnSyncOK = (Button) findViewById(R.id.btnSyncOK);
        btnSyncOK.setOnClickListener(this);

        spinnerImage = findViewById(R.id.loading);
        mRotateAnim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        //  mRotateAnim = AnimationUtils.loadAnimation(this, R.anim.anim_moveltr);

        alertmessage = (TextView) findViewById(R.id.alertmessage);
        tv_sync_message_one = (TextView) findViewById(R.id.tv_sync_message_one);

    }

    @Override
    public void onClick(View pView) {

        switch (pView.getId()) {
            case R.id.img_back:
                gotoB2CMenuScreen();
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
                gotoB2CPendingOrderScreen();
                break;

            case R.id.btnSyncOK:
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    if (btnSyncOK.getText().toString().equalsIgnoreCase("Sync")) {
                        startSyncDataFromServer();
                    } else if (btnSyncOK.getText().toString().equalsIgnoreCase("Retry")) {
                        syncByDBFilled();
                    }
                }
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

    private void resetProgression() {
        toggleLoader(false);
        updateMessageColor(B2CAppConstant.SYNC_STATUS_RED, "Sync Data");
    }

    private void toggleLoader(boolean showLoader) {
        if (showLoader) {
            btnSyncOK.setVisibility(View.GONE);
            spinnerImage.setVisibility(View.VISIBLE);

            spinnerImage.startAnimation(mRotateAnim);

        } else {
            spinnerImage.clearAnimation();
            spinnerImage.setVisibility(View.GONE);
            btnSyncOK.setVisibility(View.VISIBLE);
        }
    }

    private void updateMessageColor(int syncStatus, String message) {

        if (syncStatus == B2CAppConstant.SYNC_STATUS_RED)
            btnSyncOK.setText("Retry");
        else
            btnSyncOK.setText("Sync");

        tv_sync_message_one.setText(message);

        if (syncStatus == B2CAppConstant.SYNC_STATUS_RED) {
            tv_sync_message_one.setTextColor(getResources().getColor(R.color.red));
        } else if (syncStatus == B2CAppConstant.SYNC_STATUS_YELLOW) {
            tv_sync_message_one.setTextColor(getResources().getColor(R.color.very_dark_yellow));
        } else if (syncStatus == B2CAppConstant.SYNC_STATUS_GREEN) {
            tv_sync_message_one.setTextColor(getResources().getColor(R.color.green));
        }

    }

    private void startSyncDataFromServer() {
        if (btnSyncOK.getText().toString().equalsIgnoreCase("Sync")) {
            B2CApp.b2cPreference.setIsDbFilled(false);
            B2CApp.b2cPreference.setDBFilledTime("");

            B2CApp.b2cPreference.setIsFilledCustomers(false);
            B2CApp.b2cPreference.setIsFilledCustomerIndividual(false);
            B2CApp.b2cPreference.setIsFilledPendingBills(false);
            B2CApp.b2cPreference.setIsFilledCustomerCredits(false);
            B2CApp.b2cPreference.setIsUpdatedCollections(false);
            B2CApp.b2cPreference.setIsUpdatedPendingOrders(false);
            B2CApp.b2cPreference.setIsFilledPendingOrders(false);
            B2CApp.b2cPreference.setIsFilledProducts(false);


            B2CApp.b2cPreference.setLastIndexFilled(0);
         }

        syncByDBFilled();
    }

    private int getOperationType(String uri) {

        int optype = B2CAppConstant.OPTYPE_UNKNOWN;

        if (uri.contains(B2CAppConstant.METHOD_GET_CUST_DET))
            optype = B2CAppConstant.OPTYPE_GET_CUST_DET;
        else if (uri.contains(B2CAppConstant.METHOD_GET_CUST_LOC))
            optype = B2CAppConstant.OPTYPE_GET_CUST_LOC;
        else if (uri.contains(B2CAppConstant.METHOD_GET_PENDINGBILLS))
            optype = B2CAppConstant.OPTYPE_GET_PENDINGBILLS;
        else if (uri.contains(B2CAppConstant.METHOD_GET_ALLPENDINGBILLS))
            optype = B2CAppConstant.OPTYPE_GET_ALLPENDINGBILLS;
        else if (uri.contains(B2CAppConstant.METHOD_GET_PENDINGORDERS))
            optype = B2CAppConstant.OPTYPE_GET_PENDINGORDERS;
        else if (uri.contains(B2CAppConstant.METHOD_GET_ALLPENDINGORDERS))
            optype = B2CAppConstant.OPTYPE_GET_ALLPENDINGORDERS;
        else if (uri.contains(B2CAppConstant.METHOD_GET_CREDITDETAILS))
            optype = B2CAppConstant.OPTYPE_GET_CUSTCREDITS;
        else if (uri.contains(B2CAppConstant.METHOD_GET_ALLCREDITDETAILS))
            optype = B2CAppConstant.OPTYPE_GET_CUSTALLCREDITS;
        else if (uri.contains(B2CAppConstant.METHOD_GET_PRODUCTS))
            optype = B2CAppConstant.OPTYPE_GET_PRODUCTS;
        else if (uri.contains(B2CAppConstant.METHOD_GET_ALL_COLLECTION))
            optype = B2CAppConstant.OPTYPE_GET_ALL_COLLECTION;
        else if (uri.contains(B2CAppConstant.METHOD_UPDATE_ALL_ORDERS))
            optype = B2CAppConstant.OPTYPE_UPDATE_ORDERS;
        else if (uri.contains(B2CAppConstant.METHOD_INSERT_ALL_COLLECTION))
            optype = B2CAppConstant.OPTYPE_INSERT_ALL_COLLECTION;

        return optype;

    }

    private JSONObject getJsonParams(int opType) throws JSONException {

        JSONObject json = new JSONObject();

        if (opType == B2CAppConstant.OPTYPE_GET_PRODUCTS) {
            JSONObject ijson = new JSONObject();

            json.put(B2CAppConstant.KEY_MANU_CODE, "");
            json.put(B2CAppConstant.KEY_MOD_TIME, ""); //+B2CApp.b2cPreference.getDBFilledTime());
            json.put(B2CAppConstant.KEY_LAST_INDEX, "" + B2CApp.b2cPreference.getLastIndexFilled());
            json.put(B2CAppConstant.KEY_UNIT_KEY, "" + B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_BRANCH_CODE, "" + B2CApp.b2cPreference.getBranchCode());

            //json.put(B2CAppConstant.KEY_OBJ_NAME, ijson);
        } else if (opType == B2CAppConstant.OPTYPE_GET_CUST_DET) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(DSRAppConstant.KEY_PUNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(DSRAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());
        } else if (opType == B2CAppConstant.OPTYPE_GET_CUST_LOC) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(DSRAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());
        } else if (opType == B2CAppConstant.OPTYPE_GET_ALLPENDINGBILLS || opType == B2CAppConstant.OPTYPE_GET_CUSTALLCREDITS
                || opType == B2CAppConstant.OPTYPE_GET_ALLPENDINGORDERS) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(DSRAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());
        } else if (opType == B2CAppConstant.OPTYPE_GET_PENDINGBILLS || opType == B2CAppConstant.OPTYPE_GET_CUSTCREDITS ||
                opType == B2CAppConstant.OPTYPE_GET_PENDINGORDERS) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            if (isCurrentSyncFailed) {
                isCurrentSyncFailed = false;
                json.put(B2CAppConstant.KEY_CUST_KEY, currentCustomerKey);
            } else if (!cusKeyList.isEmpty()) {
                currentCustomerKey = cusKeyList.remove(0);
                json.put(B2CAppConstant.KEY_CUST_KEY, currentCustomerKey);
            }
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());
            json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "");
        } else if (opType == B2CAppConstant.OPTYPE_GET_ALL_COLLECTION) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());

        } else if (opType == B2CAppConstant.OPTYPE_UPDATE_ORDERS) {
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
        } else if (opType == B2CAppConstant.OPTYPE_INSERT_ALL_COLLECTION) {
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
        }

        return json;
    }

    class SyncManager extends AsyncTask<String, String, String> {

        private int operationType;
        private String uriInProgress;

        protected String doInBackground(String... urls) {

            uriInProgress = urls[0];
            Logger.LogError("postUrl : ", uriInProgress);

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
                /*if (B2CApp.b2cPreference.isLoggedIn()) {
                    String respon = httpclient.execute(httppost, responseHandler);

                    Logger.LogError("respon", "" + respon);
                }*/
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

            //updateProgress(PROGRESS_MSG_SYNC);
            isSyncInProgress = true;
            toggleLoader(true);

        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString", " : " + responseString);
            if (B2CApp.b2cUtils.isNetAvailable()) {
                if (responseString == null || responseString.equalsIgnoreCase("")) {

                    if (operationType == B2CAppConstant.OPTYPE_GET_CUST_DET) {
                        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_ALLPENDINGBILLS);
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_CUST_LOC) {

                        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_ALLPENDINGBILLS);
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_PENDINGBILLS) {
                        isCurrentSyncFailed = true;
                       /* new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_PENDINGBILLS);*/
                        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_ALLCREDITDETAILS);
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_ALLPENDINGBILLS) {
                        isCurrentSyncFailed = true;
                       /* new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_PENDINGBILLS);*/
                        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_ALLCREDITDETAILS);
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_CUSTCREDITS) {
                        isCurrentSyncFailed = true;
                       /* new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_CREDITDETAILS);*/
                        startSyncNewCollections();
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_CUSTALLCREDITS) {
                        isCurrentSyncFailed = true;
                       /* new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_CREDITDETAILS);*/
                        startSyncNewCollections();
                    } else if (operationType == B2CAppConstant.OPTYPE_INSERT_ALL_COLLECTION) {
                        //startSyncNewCollections();
                        updatePendingOrdersToServer();
                    } else if (operationType == B2CAppConstant.OPTYPE_UPDATE_ORDERS) {
                        // updatePendingOrdersToServer();
                        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_PENDINGORDERS);
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_PENDINGORDERS) {
                        isCurrentSyncFailed = true;
                      /*  new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                                + B2CAppConstant.METHOD_GET_PENDINGORDERS);*/
                        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl3() +
                                B2CAppConstant.METHOD_GET_PRODUCTS);
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_ALLPENDINGORDERS) {
                        isCurrentSyncFailed = true;

                        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl3() +
                                B2CAppConstant.METHOD_GET_ALL_COLLECTION);
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_ALL_COLLECTION) {
                        isCurrentSyncFailed = true;

                        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl3() +
                                B2CAppConstant.METHOD_GET_PRODUCTS);
                    } else if (operationType == B2CAppConstant.OPTYPE_GET_PRODUCTS) {
                      /*  new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl3() +
                                B2CAppConstant.METHOD_GET_PRODUCTS);*/
                        updateMessageColor(B2CAppConstant.SYNC_STATUS_RED, "No product found please try again");
                        isSyncInProgress = false;
                        toggleLoader(false);
                    }
                } else {

                    if (operationType == B2CAppConstant.OPTYPE_GET_CUST_DET)
                        // onPostCustomerDetails(responseString);
                        onPostCustomerDetailsNew(responseString);

                    else if (operationType == B2CAppConstant.OPTYPE_GET_PENDINGBILLS)
                        //  onPostPendingBills(responseString);
                        onPostPendingBillsNew(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_GET_ALLPENDINGBILLS)
                        onPostPendingBillsNew(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_GET_CUSTCREDITS)
                        // onPostCustomerCredits(responseString);
                        onPostCustomerCreditsNew(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_GET_CUSTALLCREDITS)
                        // onPostCustomerCredits(responseString);
                        onPostCustomerAllCreditsNew(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_INSERT_ALL_COLLECTION)
                        onPostInsertAllCollections(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_UPDATE_ORDERS)
                        onPostUpdateAllOrders(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_GET_PENDINGORDERS)
                        //   onPostPendingOrders(responseString);
                        onPostPendingOrdersNew(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_GET_ALLPENDINGORDERS)
                        onPostPendingOrdersNew(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_GET_ALL_COLLECTION)
                        onPostAllCollectionNew(responseString);
                    else if (operationType == B2CAppConstant.OPTYPE_GET_PRODUCTS)
                        //onPostGetProducts(responseString);
                        onPosstGetProductsNew(responseString);
                }
            } else {
                updateMessageColor(B2CAppConstant.SYNC_STATUS_RED, "* No internet available");
                isSyncInProgress = false;
                toggleLoader(false);
            }
        }
    }

    private void onPostCustomerDetailsNew(String responseString) {

        B2CApp.getINSTANCE().getRoomDB().customerData_dao().clearTable();
        Gson_CustomerDetails gson_customerDetails = new Gson().fromJson(responseString, Gson_CustomerDetails.class);
        if (gson_customerDetails.getCustomerDataList().size() == 0) {
            updateMessageColor(B2CAppConstant.SYNC_STATUS_RED, "" + getString(R.string.customer_not_found));

            Toast.makeText(this, "" + gson_customerDetails.getMsg(), Toast.LENGTH_SHORT).show();
        } else {
            disposableCustomer = Observable.fromArray(gson_customerDetails)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(gsondata -> {
                        B2CApp.getINSTANCE().getRoomDB().customerData_dao().insertAllCustomerData(gsondata.getCustomerDataList());
                    }, throwable ->
                            Logger.LogError("exception", "" + throwable.toString()));
            B2CApp.b2cPreference.setIsDbFilled(true);
            B2CApp.b2cPreference.setIsFilledCustomers(true);
        }


        syncAllPendingBillsFromServer();
        // syncCustomersIndFromServer();
    }


    private void onPostPendingBillsNew(String responseString) {
        B2CApp.getINSTANCE().getRoomDB().billData_dao().clearTable();

        Gson_PendingBills gson_pendingBills = new Gson().fromJson(responseString, Gson_PendingBills.class);

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
        startSyncNewCollections();

    }

    private void onPostCustomerCreditsNew(String responseString) {
        JSONObject response;
        try {
            response = new JSONObject(responseString);
            if (response.has(B2CAppConstant.KEY_STATUS) &&
                    (response.getInt(B2CAppConstant.KEY_STATUS) == 0)) {

            } else {
                disposableCreditData = Observable.just(new Gson().fromJson(responseString, CreditData.class))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(Schedulers.io())
                        .subscribe(creditdate -> {


                            B2CApp.getINSTANCE().getRoomDB().creditData_dao().insertCreditData(creditdate);

                        }, throwable ->
                                Logger.LogError("exception", "" + throwable.toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (cusKeyList.isEmpty()) {
            B2CApp.b2cPreference.setIsFilledCustomerCredits(true);
            startSyncNewCollections();
        } else {
            new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                    + B2CAppConstant.METHOD_GET_CREDITDETAILS);
        }
    }

    private void onPostCustomerAllCreditsNew(String responseString) {
        Gson_CustomerCredit gson_customerCredit = new Gson().fromJson(responseString, Gson_CustomerCredit.class);
        if (gson_customerCredit.getCreditDataList().size() == 0) {
            Toast.makeText(this, "" + gson_customerCredit.getMsg(), Toast.LENGTH_SHORT).show();

        } else {
            disposableCreditData = Observable.fromArray(gson_customerCredit)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(creditdata -> {


                        B2CApp.getINSTANCE().getRoomDB().creditData_dao().insertAllCreditData(creditdata.getCreditDataList());

                    }, throwable ->
                            Logger.LogError("exception", "" + throwable.toString()));
        }

        B2CApp.b2cPreference.setIsFilledCustomerCredits(true);
        startSyncNewCollections();


    }

    private void onPostPendingOrdersNew(String responseString) {
        B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().clearTable();
        Gson_PendingOrders gson_pendingOrders = new Gson().fromJson(responseString, Gson_PendingOrders.class);
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
        syncAllCollectionFromServer();


    }

    private void onPostAllCollectionNew(String responseString) {
        B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().clearTable();
        Gson_CustomerCollection gsonCustomerCollection = new Gson().fromJson(responseString, Gson_CustomerCollection.class);

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


        B2CApp.b2cPreference.setIsFilledPendingOrders(true);
        syncProductsFromServer();


    }

    private void onPosstGetProductsNew(String responseString) {


        JSONObject response;
        JSONArray dataArry = null;
        try {
            response = new JSONObject(responseString);
            if (response.has(B2CAppConstant.KEY_STATUS) &&
                    (response.getInt(B2CAppConstant.KEY_STATUS) == 1)) {


                if (response.has(B2CAppConstant.KEY_DATA)) {
                    dataArry = response.getJSONArray("data");
                    if (dataArry.length() == 0) {


                    } else {
                        disposableProduct = Observable.fromArray(new Gson().fromJson(responseString, Gson_ProductMaster.class).getProductDataList())
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(Schedulers.io())
                                .subscribe(productData -> {
                                    B2CApp.getINSTANCE().getRoomDB().productData_dao().insertAllProductData(productData);
                                }, throwable -> Logger.LogError("exception", "" + throwable.toString()));

                        //  Logger.LogError("productDatasize()111", "" + response.getString("totalRecords"));

                        int arraylength = dataArry.length() - 1;
                        // Logger.LogError("arraylength", "" + arraylength);
                        //  Logger.LogError("productDatasize()222", "" + dataArry.getJSONObject(arraylength).getString("lastIndex"));
                        int lastIndex = Integer.parseInt("" + dataArry.getJSONObject(arraylength).getString("lastIndex"));
                        int totalRecords = Integer.parseInt("" + response.getString("totalRecords"));
                        int percentage = ((int) ((lastIndex * 100) / totalRecords));
                        tv_sync_message_one.setTextColor(this.getResources().getColor(R.color.very_dark_yellow));
                        tv_sync_message_one.setText("Syncing Products - " + percentage + "%");
                        //   Logger.LogError("totalRecords", "" + totalRecords);
                        //   Logger.LogError("lastIndex", "" + lastIndex);
                        B2CApp.b2cPreference.setLastIndexFilled(lastIndex);
                        if (totalRecords == lastIndex) {
                            isAllRecordsFetched = true;
                        } else {
                            isAllRecordsFetched = false;
                            if (B2CApp.b2cPreference.isLoggedIn())
                                new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl3() +
                                        B2CAppConstant.METHOD_GET_PRODUCTS);


                        }

                    }
                }
            }else {
                updateMessageColor(B2CAppConstant.SYNC_STATUS_RED, "No product found please try again");
                isSyncInProgress = false;
                toggleLoader(false);
            }


            if (isAllRecordsFetched) {
                isAllRecordsFetched = false;
                B2CApp.b2cPreference.setIsDbFilled(true);
                B2CApp.b2cPreference.setDBFilledTime("" + new Date().getTime());
                toggleLoader(false);
                B2CApp.b2cPreference.setIsFilledProducts(true);
                updateMessageColor(B2CAppConstant.SYNC_STATUS_GREEN, "Sync Completed Successfully");
                isSyncInProgress = false;
                //  B2CApp.getINSTANCE().initLocationListner();
                //  B2CApp.getINSTANCE().initAlarmForStartDay();

                gotoB2CMenuScreen();

//                System.out.println("*******************kannan move to next screen**********************");

                return;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    protected void onPostInsertAllCollections(String responseString) {

        JSONObject outerJson;
        JSONArray responseArray = null;
        try {
            outerJson = new JSONObject(responseString);
            if (outerJson.has(B2CAppConstant.KEY_STATUS) && outerJson.getInt(B2CAppConstant.KEY_STATUS) == 1
                    && outerJson.has(B2CAppConstant.KEY_DATA)) {
                responseArray = outerJson.getJSONArray(B2CAppConstant.KEY_DATA);


                JSONObject innerJson;
                for (int i = 0; i < responseArray.length(); i++) {

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
                        isSyncSuccess = false;
                        Logger.LogError("is success : ", "false");
                    }


                }
            } else {
                isSyncSuccess = false;
                Logger.LogError("is success : ", "false");
            }
        } catch (JSONException e) {

            e.printStackTrace();
            Logger.LogError("onPostInsertAllCollections JSONException 1: ", e.toString());
            isSyncSuccess = false;

            startSyncPendingOrders();

            return;
        }

        B2CApp.b2cPreference.setIsUpdatedCollections(true);

        startSyncPendingOrders();
    }

    protected void onPostUpdateAllOrders(String responseString) {

        JSONObject outerJson;
        JSONArray responseArray = null;
        try {
            outerJson = new JSONObject(responseString);
            responseArray = outerJson.getJSONArray(B2CAppConstant.KEY_DATA);
        } catch (JSONException e) {

            e.printStackTrace();
            Logger.LogError("onPostUpdateAllOrders JSONException : ", e.toString());
            isSyncSuccess = false;

            callsAfterOrderUpdates();
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
                    isSyncSuccess = false;
                    Logger.LogError("is success : ", "false");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Logger.LogError("onPostUpdateAllOrders JSONException for : ", e.toString());
                isSyncSuccess = false;
            }
        }

        B2CApp.b2cPreference.setIsUpdatedPendingOrders(true);

        callsAfterOrderUpdates();
    }


    private void syncCustomersFromServer() {

        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Customers");

        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl2()
                + B2CAppConstant.METHOD_GET_CUST_DET);
    }

    private void syncAllPendingBillsFromServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Pending Bills");

        // refillCustomers();
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl() +
                B2CAppConstant.METHOD_GET_ALLPENDINGBILLS);
    }

    private void syncAllCollectionsToServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Collections");
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_INSERT_ALL_COLLECTION);
    }

    private void updatePendingOrdersToServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Pending Orders");
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_UPDATE_ALL_ORDERS);
    }

    private void syncAllPendingOrdersFromServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Pending Orders");

        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_GET_ALLPENDINGORDERS);
    }

    private void syncAllCollectionFromServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing  Collections");

        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_GET_ALL_COLLECTION);
    }

    private void syncProductsFromServer() {
        B2CApp.getINSTANCE().getRoomDB().productData_dao().clearTable();
        Logger.LogError("customer count not sync", ":" + cusKeyList.size());

        isSyncInProgress = true;
        isAllRecordsFetched = false;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Products");
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl3() +
                B2CAppConstant.METHOD_GET_PRODUCTS);
    }

    private void refillCustomers() {
	/*	Cursor tCursor = B2CApp.b2cLdbs.fetchDistinct(this, B2CTableCreate.TABLE_B2C_CONTACT_MASTER, B2CTableCreate.COLOUMNS_CONTACT_MASTER.cus_key.name(),
				B2CTableCreate.COLOUMNS_CONTACT_MASTER.cus_key.name());
		cusKeyList = new B2CCursorFactory().fetchAColoumn(tCursor, B2CTableCreate.COLOUMNS_CONTACT_MASTER.cus_key.name());*/
        cusKeyList = (ArrayList<String>) B2CApp.getINSTANCE().getRoomDB().customerData_dao().fetchDistinct();
        Logger.LogError("cusKeyList", "" + cusKeyList);
    }

    private void syncCustomersIndFromServer() {

        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Customers");

        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl2()
                + B2CAppConstant.METHOD_GET_CUST_LOC);
    }

    private void syncPendingBillsFromServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Pending Bills");

        refillCustomers();
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl() +
                B2CAppConstant.METHOD_GET_PENDINGBILLS);
    }


    private void syncCustomerCreditsFromServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Customer Credits");

        refillCustomers();
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_GET_CREDITDETAILS);
    }

    private void syncCustomerAllCreditsFromServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Customer Credits");

        //  refillCustomers();
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_GET_ALLCREDITDETAILS);
    }


    private void syncPendingOrdersFromServer() {
        isSyncInProgress = true;
        updateMessageColor(B2CAppConstant.SYNC_STATUS_YELLOW, "Syncing Pending Orders");

        refillCustomers();
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_GET_PENDINGORDERS);
    }


    @Override
    public void onBackPressed() {

        goBack();
    }


    private void goBack() {
        gotoB2CMenuScreen();
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

    private void gotoSEMSplashScreen() {

        startActivity(new Intent(this, B2CSplashScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoURL(String weblink) {
        Intent lLink = new Intent(Intent.ACTION_VIEW, Uri.parse(weblink));
        startActivity(lLink);
    }
    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_SYNC;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CSyncScreen() {

        startActivity(new Intent(this, B2CSyncScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCustListScreen() {

        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCountersScreen() {

        startActivity(new Intent(this, B2CCountersScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CPendingOrderScreen() {

        startActivity(new Intent(this, B2CPendingOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

}
