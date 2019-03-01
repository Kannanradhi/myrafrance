package com.isteer.b2c.activity.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.activity.counter_details.B2CLocateScreen;
import com.isteer.b2c.activity.calender.DSRAddProgScreen;
import com.isteer.b2c.activity.reports.B2CCollectionSumScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.B2CTableCreate;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.picker.StringPickerDialog;
import com.isteer.b2c.utility.CustomPopupDialog;
import com.isteer.b2c.utility.Logger;

public class B2CCollectionEntryScreen extends AppCompatActivity implements OnClickListener, StringPickerDialog.OnClickListener, StringPickerDialog.OnDismissListener {


    public static boolean isFromCounter = false;
    private String TAG = "B2CCollectionEntryScreen";

    private TextView header_title;
    private ImageView btn_header_right;

    public static boolean isFromCollectionSum = false;
    public static boolean isFromCounterDetail = false;
    public static boolean isByCollectButton = false;
    public static boolean isFromCustList = false;
    public static boolean backToCollectionSum = true;
    public static String currentCusKey = "";
    public static String currentCustomerName = "";
    public static String currentCollectionAmount = "";
    private CollectionData newCollection;

    public static boolean isPickerActive = false;
    private static final int OPTYPE_PAY_TYPES = 30;

    private TextView tv_customername, tv_paymentmode, tv_chequedate;
    private EditText et_amount, et_receiptno, et_chequeno, et_bankname, et_remarks;
    private View ll_tobehidden, ll_receipt_no, ll_wrapper;

    private static ProgressDialog pdialog;
    private static String PROGRESS_MSG = "Submitting...";
    private Uri fileUri;
    public ImageView img_cheque_image;
    private String mCurrentPhotoPath;
    private Uri selectedImage;
    private Bitmap photo;
    private String picturePath;
    private int type;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private int mDay, mMonth, mYear;
    public static boolean isFromCollection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.scr_isr_add_collection);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initVar();
    }

    @Override
    protected void onResume() {

        super.onResume();
        setErrorNull();
        if (isFromCollectionSum) {
            isFromCollectionSum = false;
            backToCollectionSum = true;

            clearEntries();
        } else if (isFromCounterDetail) {
            isFromCounterDetail = false;
            backToCollectionSum = false;
            clearEntries();
            tv_customername.setText(currentCustomerName);

            if (isByCollectButton) {
                isByCollectButton = false;
                et_amount.setText(currentCollectionAmount);
            }
        } else if (isFromCustList) {
            isFromCustList = false;

            tv_customername.setText(currentCustomerName);
        } else {

        }
    }

    private void clearEntries() {

        tv_customername.setText("");
        tv_paymentmode.setText("");
        tv_chequedate.setText("" + DateFormat.format(B2CAppConstant.dateFormat3, new Date()));

        et_amount.setText("");
        et_receiptno.setText("");
        et_chequeno.setText("");
        et_bankname.setText("");
        et_remarks.setText("");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);
        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Collection Entry");

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);

        tv_customername = (TextView) findViewById(R.id.tv_customername);
        tv_paymentmode = (TextView) findViewById(R.id.tv_paymentmode);
        tv_chequedate = (TextView) findViewById(R.id.tv_chequedate);
        findViewById(R.id.lt_cheque_image).setOnClickListener(this);
        img_cheque_image = (ImageView) findViewById(R.id.img_cheque_image);
        et_amount = (EditText) findViewById(R.id.et_amount);
        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                et_amount.removeTextChangedListener(this);


                et_amount.setText(B2CApp.b2cUtils.setGroupSeparaterEditText("" + editable));
                et_amount.setSelection(et_amount.getText().length());
                et_amount.addTextChangedListener(this);
            }
        });
        et_receiptno = (EditText) findViewById(R.id.et_receiptno);
        et_chequeno = (EditText) findViewById(R.id.et_chequeno);
        et_bankname = (EditText) findViewById(R.id.et_bankname);
        et_remarks = (EditText) findViewById(R.id.et_remarks);

        ((View) findViewById(R.id.btnAddCol)).setOnClickListener(this);
        ((View) findViewById(R.id.txt_AddCol)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_type)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_date)).setOnClickListener(this);
        ((View) findViewById(R.id.ll_cname)).setOnClickListener(this);

        tv_customername = (TextView) findViewById(R.id.tv_customername);
        ll_tobehidden = (View) findViewById(R.id.ll_tobehidden);
        ll_receipt_no = (View) findViewById(R.id.ll_receipt_no);
        ll_wrapper = (View) findViewById(R.id.ll_wrapper);

        ll_wrapper.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                B2CApp.b2cUtils.hideSoftKeyboard(et_amount);
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        goBack();
    }


    private void goBack() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int backPress = extras.getInt("backPress");
            Logger.LogError("backPress", "" + backPress);
            if (backPress == B2CAppConstant.MENU_SCREEN) {
                gotoB2CMenuScreen();
                intent.putExtra("backPress", 0);

            } else if (backPress == B2CAppConstant.COLLECTION_SCREEN) {
                gotoBackB2CCollectionSumScreen();
                intent.putExtra("backPress", 0);

            } else if (backPress == 0 || backPress == 3)
                gotoB2CCounterDetailScreen();

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

            case R.id.btnAddCol:
            case R.id.txt_AddCol:
                setErrorNull();
                validateCollection();
                break;


            case R.id.ll_type:
                //showStringPicker(OPTYPE_PAY_TYPES);
                //  showPaymentModeAlert();
                setErrorNull();
                popUpPaymentAlert();
                break;

            case R.id.ll_date:
                setErrorNull();
                showDatePicker();
                break;

            case R.id.ll_cname:
                setErrorNull();
                isFromCollection = true;
                gotoDSRCustListScreen();
                break;
            case R.id.lt_cheque_image:
                gotoCapturePhoto();
                break;
			
/*		case R.id.bottombar_one:
			gotoDSRMenuScreen();
			break;
			
		case R.id.bottombar_two:
			gotoDSRCountersScreen();
			break;
			
		case R.id.bottombar_three:
			gotoDSRSyncScreen();
			break; 
			
		case R.id.bottombar_four:
			gotoDSRCustListScreen();
			break;
			
		case R.id.bottombar_five:
			gotoDSRPendingOrders();
			break;*/

        }
    }

    private void validateCollection() {

        final String customer = "" + tv_customername.getText();
        final String pay_mode = "" + tv_paymentmode.getText();
        final String amount = "" + et_amount.getText().toString().replace(",", "");
        final String receipt_no = "" + et_receiptno.getText();
        final String cheque_no = "" + et_chequeno.getText();
        final String cheque_date = "" + tv_chequedate.getText();
        final String bank = "" + et_bankname.getText();
        final String remarks = "" + et_remarks.getText();

        if (customer.equalsIgnoreCase("")) {
            tv_customername.setError("Select a valid customer name");
            //alertUserP(this, "Alert!", "Select a valid customer name", "OK");
            return;
        } else if (pay_mode.equalsIgnoreCase("")) {
            tv_paymentmode.setError("Select a payment mode");
            //alertUserP(this, "Alert!", "Select a payment mode", "OK");
            return;
        } else if (amount.equalsIgnoreCase("")) {
            et_amount.requestFocus();
            et_amount.setError("Enter a valid amount");
            //alertUserP(this, "Alert!", "Enter a valid amount", "OK");
            return;
        } else if (pay_mode.equalsIgnoreCase(B2CAppConstant.PAY_CASH) && receipt_no.length() < 1) {
            et_receiptno.requestFocus();
            et_receiptno.setError("Enter a valid receipt no");
            //alertUserP(this, "Alert!", "Enter a valid receipt no", "OK");
            return;
        } else if (pay_mode.equalsIgnoreCase(B2CAppConstant.PAY_CHEQUE) && cheque_no.equalsIgnoreCase("")) {
            et_chequeno.requestFocus();
            et_chequeno.setError("Enter a valid cheque no");
            //alertUserP(this, "Alert!", "Enter a valid cheque no", "OK");
            return;
        } else if (pay_mode.equalsIgnoreCase(B2CAppConstant.PAY_CHEQUE) && cheque_date.equalsIgnoreCase("")) {
            tv_chequedate.setError("Enter a valid cheque date");
            //alertUserP(this, "Alert!", "Enter a valid cheque date", "OK");
            return;
        } else if (pay_mode.equalsIgnoreCase(B2CAppConstant.PAY_CHEQUE) && bank.equalsIgnoreCase("")) {
            et_bankname.requestFocus();
            et_bankname.setError("Enter a valid bank name");
            //alertUserP(this, "Alert!", "Enter a valid bank name", "OK");
            return;
        } else {
            newCollection = new CollectionData();
            newCollection.setCus_key(currentCusKey);
            newCollection.setCustomer_name(currentCustomerName);
            // newCollection.setContact_key(null);
            newCollection.setAmount(amount);
            newCollection.setPayment_mode(pay_mode);
            newCollection.setTrans_date("" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime()));
            newCollection.setEntered_date_time("" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime()));
            newCollection.setCheque_no(cheque_no);
            newCollection.setCheque_date(cheque_date);
            newCollection.setReceipt_no(receipt_no);
            newCollection.setBank(bank);
            newCollection.setRemarks(remarks);
            newCollection.setIs_synced_to_server(0);
            int newEntryCount = B2CApp.b2cPreference.getNewEntryCount() - 1;
            newCollection.setPay_coll_key(newEntryCount);
            B2CApp.b2cPreference.setNewEntryCount(newEntryCount);

            Long insertlong = B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().insertCollectionData(newCollection);
            Logger.LogError("insertlong", "" + insertlong);
            //	boolean isSuccess = B2CApp.b2cLdbs.insertCollection(B2CCollectionEntryScreen.this, newCollection);
            if (insertlong != 0) {
                clearEntries();

                if (B2CApp.b2cUtils.isNetAvailable()) {

                    new SyncToServerTask().execute(B2CApp.b2cPreference.getBaseUrl() + B2CAppConstant.METHOD_INSERT_ALL_COLLECTION);
                } else {
                    B2CApp.b2cPreference.setIsUpdatedCollections(false);
                    Toast.makeText(B2CCollectionEntryScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
                    /// gotoB2CCollectionSumScreen();
                    gotoSaveBtnClick();
                }

            } else
                alertUserP(this, "Failed", "Operation failed in a timely manner, please try again", "OK");

        }

    }


    public void setErrorNull() {
        tv_customername.setError(null);
        tv_paymentmode.setError(null);
        et_amount.setError(null);
        et_receiptno.setError(null);
        et_chequeno.setError(null);
        tv_chequedate.setError(null);
        et_bankname.setError(null);
        et_remarks.setError(null);
    }

    class SyncToServerTask extends AsyncTask<String, String, String> {

        private int operationType;

        protected String doInBackground(String... urls) {

            Logger.LogError("postUrl : ", urls[0]);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urls[0]);
            try {

                String jsonString = "";

                try {

                    jsonString = getJsonParams().toString();

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
                Logger.LogError("ClientProtocolException : ", e.toString());
            } catch (IOException e) {
                Logger.LogError("IOException : ", e.toString());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

            B2CApp.b2cUtils.updateMaterialProgress(B2CCollectionEntryScreen.this, PROGRESS_MSG);

        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);

            B2CApp.b2cUtils.dismissMaterialProgress();

            if (responseString == null) {

                //B2CApp.b2cPreference.setIsUpdatedcollections
                Toast.makeText(B2CCollectionEntryScreen.this, "Saved locally", Toast.LENGTH_LONG).show();
                //goBack();
                return;
            }
            onPostInsertAllCollections(responseString);

           /*

            try {

                JSONObject responseJson = new JSONObject(responseString);

                if (responseJson.has(DSRAppConstant.KEY_STATUS) && responseJson.getInt(DSRAppConstant.KEY_STATUS) == 1) {
                    int updatecollection = B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().updateCollectionKey(
                            responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                            responseJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.cus_key.name()),
                            responseJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.sc_ledger_key.name()),
                            Integer.parseInt(responseJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name())),
                            1);
					*//*if (B2CApp.b2cLdbs.updateCollectionKey(B2CCollectionEntryScreen.this, responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
							responseJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.cus_key.name()),
							responseJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name()),
							""
							//,responseJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.sc_ledger_key.name())
					)) {
						Toast.makeText(B2CCollectionEntryScreen.this, "Added successfully", Toast.LENGTH_LONG).show();
					}*//*
                    Toast.makeText(B2CCollectionEntryScreen.this, "Collection added successfully", Toast.LENGTH_SHORT).show();
                    //    alertUserP(B2CCollectionEntryScreen.this, "Success ", "Collection added successfully", "OK");
                    gotoSaveBtnClick();
                    if (!backToCollectionSum)
                        tv_customername.setText(currentCustomerName);
                    else
                        gotoSaveBtnClick();
                    //  gotoB2CCollectionSumScreen();
                    return;
                } else {
                    alertUserP(B2CCollectionEntryScreen.this, "Failed !", "Duplicte entry please try again", "Ok");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Logger.LogError("JSONException : ", e.toString());
            }*/


            B2CApp.b2cPreference.setIsUpdatedCollections(false);
        }
    }

    protected void onPostInsertAllCollections(String strOuterJson) {
        try {
            JSONObject outerJson = new JSONObject(strOuterJson);
            JSONArray responseArray = null;

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
                Toast.makeText(B2CCollectionEntryScreen.this, "Collection added successfully", Toast.LENGTH_SHORT).show();
                gotoSaveBtnClick();
            } else {
                alertUserP(B2CCollectionEntryScreen.this, "Failed !", "Duplicte entry please try again", "Ok");
                Logger.LogError("is success : ", "false");
            }


        } catch (JSONException e) {

            e.printStackTrace();
            Logger.LogError("onPostInsertAllCollections JSONException 1: ", e.toString());


            return;
        }

        B2CApp.b2cPreference.setIsUpdatedCollections(true);


    }

    private void updateProgress(String message) {
        if (pdialog != null && pdialog.isShowing())
            pdialog.setMessage(message);
        else
            pdialog = ProgressDialog.show(this, "", message, true);
    }

    private JSONObject getJsonParams() throws JSONException {

        JSONObject json = new JSONObject();

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

        return json;
    }
   /* public JSONObject getJsonParams() throws JSONException {

        JSONObject json = new JSONObject();

        json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
        json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
        json.put(B2CAppConstant.KEY_CUST_KEY, currentCusKey);
        Logger.LogError("B2CApp.b2cPreference.getToken()", "" + B2CApp.b2cPreference.getToken());
        json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());
        json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "432e");

        json.put(DSRAppConstant.KEY_DUMMY_KEY, B2CApp.b2cPreference.getNewEntryCount());
        json.put(B2CAppConstant.KEY_AMOUNT, "" + newCollection.getAmount());
        json.put(B2CAppConstant.KEY_PAYMENT_MODE, "" + newCollection.getPayment_mode());
        json.put(B2CAppConstant.KEY_TRANS_DATE, "" + newCollection.getTrans_date());
        if (newCollection.getPayment_mode().equalsIgnoreCase(B2CAppConstant.PAY_CHEQUE)) {
            json.put(B2CAppConstant.KEY_CHEQUE_NO, "" + newCollection.getCheque_no());
            json.put(B2CAppConstant.KEY_CHEQUE_DATE, "" + newCollection.getCheque_date());
            json.put(B2CAppConstant.KEY_BANK, "" + newCollection.getBank());
        } else {
            json.put(B2CAppConstant.KEY_RECEIPT_NO, "" + newCollection.getReceipt_no());
        }
        json.put(B2CAppConstant.KEY_REMARKS, "" + newCollection.getRemarks());
        json.put(B2CAppConstant.KEY_ENTERED_DATE_TIME, "" + newCollection.getEntered_date_time());

        return json;
    }*/

    private void dismissProgress() {
        if (pdialog != null && pdialog.isShowing())
            pdialog.dismiss();
    }

    private void showStringPicker(int opType) {
        if (isPickerActive) {
            return;
        }

        Bundle bundle = new Bundle();

        if (opType == OPTYPE_PAY_TYPES) {
            bundle.putStringArray(getString(R.string.string_picker_dialog_values), B2CAppConstant.PAY_MODES);
            bundle.putString(getString(R.string.string_picker_title), "Select payment mode");
        }

        bundle.putInt(getString(R.string.string_picker_type), opType);
        StringPickerDialog dialog = new StringPickerDialog();
        dialog.setArguments(bundle);
        dialog.show(this.getFragmentManager(), TAG);

        isPickerActive = true;
    }

    public void onClick(boolean isPositive, String value, int opt) {

        isPickerActive = false;
        if (!isPositive)
            return;

        if (opt == OPTYPE_PAY_TYPES) {
            tv_paymentmode.setText("" + value);

            if (value.equalsIgnoreCase(B2CAppConstant.PAY_CASH)) {
                ll_tobehidden.setVisibility(View.GONE);
                ll_receipt_no.setVisibility(View.VISIBLE);
            } else if (value.equalsIgnoreCase(B2CAppConstant.PAY_CHEQUE)) {
                ll_receipt_no.setVisibility(View.GONE);
                ll_tobehidden.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDismiss(final int type) {

        isPickerActive = false;

    }

    private void showDatePicker() {
        String cDate = "" + tv_chequedate.getText();

        if (cDate.isEmpty()) {
            Calendar c = Calendar.getInstance();
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mMonth = c.get(Calendar.MONTH);
            mYear = c.get(Calendar.YEAR);
        } else {
            String[] partics = cDate.split("-");
            mDay = Integer.parseInt(partics[0]);
            mMonth = Integer.parseInt(partics[1]) - 1;
            mYear = Integer.parseInt(partics[2]);
        }
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar tCalendar = Calendar.getInstance();
                tCalendar.set(year, monthOfYear, dayOfMonth);

                String tDate = "" + DateFormat.format(B2CAppConstant.dateFormat3, tCalendar.getTime());
                tv_chequedate.setText(tDate);

            }

        }, mYear, mMonth, mDay);

        dpd.show();
    }

    private void gotoDSRAddProgScreen() {

        startActivity(new Intent(this, DSRAddProgScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }


    private void gotoDSRMapScreen() {

        startActivity(new Intent(this, B2CLocateScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoSaveBtnClick() {
        if (isFromCounter) {
            isFromCounter = false;
            finish();
        } else {
            gotoB2CCollectionSumScreen();
        }
    }

    private void gotoB2CCollectionSumScreen() {

        startActivity(new Intent(this, B2CCollectionSumScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoBackB2CCollectionSumScreen() {

        startActivity(new Intent(this, B2CCollectionSumScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCounterDetailScreen() {

        startActivity(new Intent(this, B2CCounterDetailScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRCustListScreen() {
        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .putExtra(B2CAppConstant.BACKTOCUSTOMERSEARCH, B2CAppConstant.COLLECTION_SCREEN)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void showPaymentModeAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);

        builder
                //.setMessage("Select payment mode")
                .setTitle("Select payment mode")
                .setCancelable(false)
                .setNegativeButton(B2CAppConstant.PAY_CASH,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                dialog.cancel();
                                tv_paymentmode.setText("" + B2CAppConstant.PAY_CASH);
                                ll_tobehidden.setVisibility(View.GONE);
                                ll_receipt_no.setVisibility(View.VISIBLE);

                            }
                        })
                .setPositiveButton(B2CAppConstant.PAY_CHEQUE,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                dialog.cancel();
                                tv_paymentmode.setText("" + B2CAppConstant.PAY_CHEQUE);
                                ll_receipt_no.setVisibility(View.GONE);
                                ll_tobehidden.setVisibility(View.VISIBLE);

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void popUpPaymentAlert() {
        String title = getResources().getString(R.string.payment_title);
        String message = getResources().getString(R.string.payment_message);
        String cash = getResources().getString(R.string.cash);
        String cheque = getResources().getString(R.string.cheque);
        CustomPopupDialog customPopupDialog = new CustomPopupDialog(this, title, message, "", cash, cheque,
                new CustomPopupDialog.myOnClickListenerLeft() {
                    @Override
                    public void onButtonClickLeft(String s) {
                        tv_paymentmode.setText("" + B2CAppConstant.PAY_CASH);
                        ll_tobehidden.setVisibility(View.GONE);
                        ll_receipt_no.setVisibility(View.VISIBLE);
                        et_chequeno.setText("");
                        tv_chequedate.setText("");
                        et_bankname.setText("");
                    }
                }, new CustomPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight(String s) {
                tv_paymentmode.setText("" + B2CAppConstant.PAY_CHEQUE);
                ll_receipt_no.setVisibility(View.GONE);
                ll_tobehidden.setVisibility(View.VISIBLE);
                et_receiptno.setText("");
            }
        }, new CustomPopupDialog.myOnClickListenerThird() {

            @Override
            public void onButtonClickThird(String s) {
                tv_paymentmode.setText("" + B2CAppConstant.PAY_RTGS);
                ll_receipt_no.setVisibility(View.GONE);
                ll_tobehidden.setVisibility(View.VISIBLE);
                et_receiptno.setText("");
            }
        });
    }

    public void alertUserP(Context context, String title, String msg, String btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setTitle(title).setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().deleteCollectionData(newCollection);
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_COLL_ENTRY;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoCapturePhoto() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            Toast.makeText(this, " Check the permission for camera ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 2);
        }


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK) {

            //previewCapturedImage();
            Logger.LogError("resultCode", "" + resultCode + "" + requestCode + " path " + mCurrentPhotoPath);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String chequePath = saveToInternalStorage(photo);
            Logger.LogError("chequePath", "" + chequePath);
            img_cheque_image.setImageBitmap(photo);
            //	new SetPic(this,requestCode,resultCode,data,img_cheque_image,mCurrentPhotoPath);

        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String currentDate = df.format(c.getTime());

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        String extr = Environment.getExternalStorageDirectory().toString()
                + File.separator + "images";

        File mypath1 = new File("/sdcard/cheque/");
        mypath1.mkdirs();
        File mypath = new File(mypath1, currentDate + ".jpg");

        Logger.LogError("sdcard path in credit", "" + mypath);

       /* File directory = cw.getDir("images", Context.MODE_PRIVATE);
      //  File directory = cw.getDir("images", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, currentDate + ".jpg");
       */
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Logger.LogError("path for cheque", "" + mypath.getPath());
            return mypath.getPath();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath

                // Save a file: path for use with ACTION_VIEW intents
                = image.getAbsolutePath();
        Logger.LogError("Getpath", "" + mCurrentPhotoPath);
        return image;
    }
}