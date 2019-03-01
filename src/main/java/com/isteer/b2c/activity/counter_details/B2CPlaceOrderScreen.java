package com.isteer.b2c.activity.counter_details;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.R;
import com.isteer.b2c.adapter.RCV_B2CPlaceOrderAdaptor;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.dao.ProductData_DAO;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;
import com.isteer.b2c.model.StockData;
import com.isteer.b2c.repository.ProductViewModel;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.utility.ValidationUtils;

public class B2CPlaceOrderScreen extends AppCompatActivity implements OnClickListener {

    private TextView header_title;
    private ImageView btn_header_right;
    public static boolean toRefreshScreen = false;
    public static boolean isLimitExceeded = false;
    public static boolean isCounterOrder = true;
    public static double currentOrderValue = 0.00;
    public static ArrayList<ProductData> listEntries = new ArrayList<ProductData>();
    public PendingOrderData currentPendingOrder = new PendingOrderData();
    public static String currentCounterName = "";

    private ListView nameEntryList;

    private PopupWindow ppWindow;
    private FrameLayout bg_dim_layout;
    private PopupHolder popupHolder;

    private Spinner spinnerBrand;
    private String strHint = "";
    private EditText et_search_hint;
    private TextView po_tv_available_credit, po_tv_total_value;

    private ArrayList<String> manufacturersList = new ArrayList<String>();
    private ArrayList<StockData> stocksInBranch = new ArrayList<StockData>();
    //public static String manufacturerSelected = "";

    private static ProgressDialog pdialog;
    private static String PROGRESS_MSG = "Placing order...";
    public B2CStockAdapter stockAdapter;
    private RecyclerView rcv_nameEntryList;
    private RCV_B2CPlaceOrderAdaptor rcv_orderEntryAdaptor;
    private Dialog popupdialog;
    private String strbrands = "";
    private ProductViewModel productViewModel;
    private ProductData_DAO productData_dao;
    private String purchase_order_type;

    private static class PopupHolder {

        public ListView listview_stock;
        public View btn_pop_done;
        public View btn_pop_close;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.scr_b2c_place_order);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initVar();
    }

    @Override
    protected void onResume() {

        super.onResume();
        B2CApp.b2cUtils.dismissMaterialProgress();
        Logger.LogError("B2CApp.b2cPreference.getCHECKEDINCMKEY()", "" + B2CApp.b2cPreference.getCHECKEDINCMKEY());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int cus_key = (ValidationUtils.integerFormat(bundle.getString("currentcus_key")));
        Logger.LogError("bundle.getInt", "" + cus_key);
        purchase_order_type = "Phone Order";
        if (B2CApp.b2cPreference.getCHECKEDINCUSKEY() == cus_key) {
            header_title.setText("Counter Order");
            purchase_order_type = "Counter Order";
        } else {
            header_title.setText("Phone Order");
            purchase_order_type = "Phone Order";
        }
        if (toRefreshScreen) {
            toRefreshScreen = false;
            refreshScreen();

           /* new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    B2CApp.b2cUtils.hideSoftKeyboard(et_search_hint);
                }
            }, 500);*/
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

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);


        po_tv_available_credit = (TextView) findViewById(R.id.po_tv_available_credit);
        po_tv_total_value = (TextView) findViewById(R.id.po_tv_total_value);

        //	nameEntryList = ((ListView) findViewById(R.id.nameEntryList));
        rcv_nameEntryList = ((RecyclerView) findViewById(R.id.rcv_nameEntryList));
        rcv_nameEntryList.setLayoutManager(new LinearLayoutManager(this));
        productData_dao = B2CApp.getINSTANCE().getRoomDB().productData_dao();
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        suggestList();
	/*	nameEntryList.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				B2CApp.b2cUtils.hideSoftKeyboard(et_search_hint);				
				return false;
			}
		});*/

        et_search_hint = ((EditText) findViewById(R.id.et_search_hint));
        et_search_hint.setOnClickListener(this);
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

                strHint = et_search_hint.getText().toString();

                productViewModel.init(productData_dao, "" + strbrands + "%", "%" + strHint + "%");
                productViewModel.productDataList.observe(B2CPlaceOrderScreen.this, pagedList -> {
                    rcv_orderEntryAdaptor.submitList(pagedList);

                });
                //suggestList();

            }

        });
        manufacturersList = (ArrayList<String>) B2CApp.getINSTANCE().getRoomDB().productData_dao().fetchDistinctMenuName();

        manufacturersList.add(0, B2CAppConstant.KEY_ALL_BRANDS);
        manufacturersList.remove(manufacturersList.contains(""));
        manufacturersList.removeAll(Collections.singleton(null));
        manufacturersList.remove(manufacturersList.contains(" "));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, manufacturersList);
        spinnerBrand = ((Spinner) findViewById(R.id.spinnerBrand));
        spinnerBrand.setAdapter(adapter);
        spinnerBrand.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                B2CApp.b2cUtils.hideSoftKeyboard(et_search_hint);
                return false;
            }
        });

        spinnerBrand.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {


                if (pos == 0) {
                    strbrands = "";
                } else {
                    strbrands = manufacturersList.get(pos);
                }
               // Logger.LogError("strbrands", "" + strbrands);

                productViewModel.init(productData_dao, "" + strbrands + "%", "%");
                productViewModel.productDataList.observe(B2CPlaceOrderScreen.this, pagedList -> {
                    rcv_orderEntryAdaptor.submitList(pagedList);
                });
                et_search_hint.getText().clear();
                //   suggestList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                // suggestList();
            }
        });
		
		/*bg_dim_layout = (FrameLayout) findViewById(R.id.bg_dim_layout);
		bg_dim_layout.getForeground().setAlpha(0);*/
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
        gotoDSRCounterDetailScreen();
    }

    private void refreshScreen() {
        et_search_hint.getText().clear();
        strHint = "";
        spinnerBrand.setSelection(0);
        if ((Double.valueOf(""+B2CCounterDetailScreen.currentCustomer.getAvailable_credit())).intValue() > 1)
        po_tv_available_credit.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal(B2CCounterDetailScreen.currentCustomer.getAvailable_credit()));
        else {
            po_tv_available_credit.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal(B2CCounterDetailScreen.currentCustomer.getAvailable_credit()));
            po_tv_available_credit.setTextColor(getResources().getColor(R.color.red));
        }
        currentOrderValue = 0.00;
        po_tv_total_value.setText("0.00");
        suggestList();

        //updateCurrentCustomerData(DSRApp.dsrLdbs.fetchSelected(this, DSRTableCreate.TABLE_AERP_CONTACT_MASTER, null, DSRTableCreate.COLOUMNS_CONTACT_MASTER.company_name.name(), DSRLocalDBStorage.SELECTION_OPERATION_LIKE, new String[]{currentCustomer.getCompany_name()}));
    }

    private void suggestList() {
        productViewModel.init(productData_dao, "" + strbrands + "%", "%" + strHint + "%");

        rcv_orderEntryAdaptor = new RCV_B2CPlaceOrderAdaptor(this);
        productViewModel.productDataList.observe(B2CPlaceOrderScreen.this, pagedList -> {
            rcv_orderEntryAdaptor.submitList(pagedList);
            rcv_nameEntryList.setAdapter(rcv_orderEntryAdaptor);
        });
		/*listEntries.clear();
		Cursor cCursor = B2CApp.b2cLdbs.fetchLike(this, B2CTableCreate.TABLE_B2C_PRODUCT_MASTER, 
				B2CTableCreate.COLOUMNS_PRODUCT_MASTER.display_code.name(), strHint, 
				B2CTableCreate.COLOUMNS_PRODUCT_MASTER.manu_name.name() + " , " + B2CTableCreate.COLOUMNS_PRODUCT_MASTER.display_code.name());
        listEntries.addAll(new B2CCursorFactory().fetchAsProducts(cCursor, B2CTableCreate.getColoumnArrayTableProductMaster()));*/

     /*   ArrayList<ProductData> productDataArrayList = (ArrayList<ProductData>) B2CApp.getINSTANCE().getRoomDB().
                productData_dao().getAllProductData("%" + strHint + "%", "%" + strbrands + "%");
        //  Logger.LogError("productDataArrayList", "" + productDataArrayList.size());
        rcv_orderEntryAdaptor = new RCV_B2CPlaceOrderAdaptor(this, productDataArrayList);


        //	rcv_orderEntryAdaptor = new RCV_B2CPlaceOrderAdaptor(this, listEntries);
        rcv_nameEntryList.setAdapter(rcv_orderEntryAdaptor);*/

		/*orderEntryAdaptor = new B2CPlaceOrderAdaptor(this, listEntries);
		nameEntryList.setAdapter(orderEntryAdaptor);*/

    }

    public void showStockPopup() {
        //initPopup();
        initPopupView();
        if (fillPopup())
            showPopup();
		/*else
			alertUserP(this, "Error", "Unable to check stock in a timely manner. Please try again", "OK");*/
    }

    public void onOrderClicked(PendingOrderData tOrder, ImageView btnOrder, int position, double orderValue) {
        tOrder.setCustomer_key(B2CCounterDetailScreen.currentMap.getCus_key());
        //	tOrder.setContact_key(""+B2CCounterDetailScreen.currentCustomer.getCmkey());
        tOrder.setDate(B2CApp.b2cUtils.getFormattedDate("yyyyMMdd", new Date().getTime()));
        tOrder.setPurchase_order_type(isCounterOrder ? "MAC" : "MAT");

        int newEntryCount = B2CApp.b2cPreference.getNewEntryCount() - 1;
        tOrder.setSo_item_key(newEntryCount);
        tOrder.setSo_key(newEntryCount);
        B2CApp.b2cPreference.setNewEntryCount(newEntryCount);
        currentPendingOrder = tOrder;

        double newCurrentOrderValue = currentOrderValue + orderValue;
        Logger.LogError("B2CCounterDetailScreen", "" + B2CCounterDetailScreen.currentCustomer.getAvailable_credit());
        double availableCreditLimit = Double.parseDouble(B2CCounterDetailScreen.currentCustomer.getAvailable_credit());
        isLimitExceeded = newCurrentOrderValue > availableCreditLimit;

        placeOrder(btnOrder, position, orderValue);
    }

    public void onCheckStockClicked(PendingOrderData tOrder, int position) {
        //tOrder.setCustomer_key(B2CCounterDetailScreen.currentMap.get(B2CTableCreate.COLOUMNS_CONTACT_MASTER.cmkey.name()));
        //tOrder.setContact_key(B2CCounterDetailScreen.currentContactKey);
        //tOrder.setDate(B2CApp.b2cUtils.getFormattedDate("yyyyMMdd", new Date().getTime()));

        currentPendingOrder = tOrder;

        PROGRESS_MSG = "Checking stock...";
        new OrderTask(null, position).execute(B2CApp.b2cPreference.getBaseUrl() + B2CAppConstant.METHOD_GET_STOCK);
    }

    private void placeOrder(ImageView btnOrder, int position, double orderValue) {

        PendingOrderData order = currentPendingOrder;
        order.setPurchase_order_type(purchase_order_type);
        order.setIs_synced_to_server(0);

        //B2CCounterDetailScreen.currentMap.get(B2CTableCreate.COLOUMNS_CONTACT_MASTER.cus_key.name())
        long longpeninsert = B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().insertPendingOrderData(order);
        Logger.LogError("getCustomer_key", "" + order.getCustomer_key());
        Logger.LogError("getContact_key", "" + order.getContact_key());
        Logger.LogError("222", "" + B2CCounterDetailScreen.currentMap.getCus_key());
        Logger.LogError("longpeninsert", "" + longpeninsert);
        //if(B2CApp.b2cLdbs.insertPendingOrder(B2CPlaceOrderScreen.this, order, B2CCounterDetailScreen.currentCustomer.getCus_key(), ""+B2CCounterDetailScreen.currentCustomer.getCmkey(), 0))
        if (longpeninsert < 0) {
            btnOrder.setImageResource(R.drawable.ic_check_circle_black_24dp);
            RCV_B2CPlaceOrderAdaptor.isOrderPlaced[position] = true;

            currentOrderValue = currentOrderValue + orderValue;
            po_tv_total_value.setText("" + String.format("%.2f", currentOrderValue));

            if (B2CApp.b2cUtils.isNetAvailable()) {
                PROGRESS_MSG = "Placing order...";
                new OrderTask(btnOrder, position).execute(B2CApp.b2cPreference.getBaseUrl() + B2CAppConstant.METHOD_PLACE_AN_ORDER);
            } else {
                B2CPendingOrderScreen.toUpdateCustomerDetail = true;
                B2CCounterDetailScreen.toUpdateCounterDetail = true;
                B2CApp.b2cPreference.setIsUpdatedPendingOrders(false);

                et_search_hint.setSelectAllOnFocus(true);

                Toast.makeText(B2CPlaceOrderScreen.this, R.string.saved_locally, Toast.LENGTH_LONG).show();

                if (isLimitExceeded) {
                    isLimitExceeded = false;
                    alertUserP(B2CPlaceOrderScreen.this, "Warning!", "Order value exceeds available credit limit", "OK");
                }
            }
        } else
            Toast.makeText(B2CPlaceOrderScreen.this, "Order placement failed in a timely manner, please try again", Toast.LENGTH_LONG).show();
    }

    class OrderTask extends AsyncTask<String, String, String> {

        private int operationType;
        private String uriInProgress;
        private ImageView btnOrder;
        private int position;

        public OrderTask(ImageView btn, int pos) {
            btnOrder = btn;
            position = pos;
        }

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
                Logger.LogError("ClientProtocolException : ", e.toString());
            } catch (IOException e) {
                Logger.LogError("IOException : ", e.toString());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

            B2CApp.b2cUtils.updateMaterialProgress(B2CPlaceOrderScreen.this, PROGRESS_MSG);

        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);

            B2CApp.b2cUtils.dismissMaterialProgress();

            String message = "Operation failed in a timely manner. Please try again";

            if (responseString == null) {

                alertUserP(B2CPlaceOrderScreen.this, "Failed", message, "OK");
                return;
            }

            if (operationType == B2CAppConstant.OPTYPE_PLACE_AN_ORDER) {
                onPostPlaceAnOrder(responseString, btnOrder, position);
            } else if (operationType == B2CAppConstant.OPTYPE_GET_STOCK) {
                onPostGetStock(responseString, position);
            }

        }
    }

    protected void onPostPlaceAnOrder(String responseString, ImageView btnOrder, int position) {

        JSONObject responseJson;
        // String message = "Operation failed in a timely manner. Please try again";
        String message = "" + getResources().getString(R.string.saved_locally);

        try {
            responseJson = new JSONObject(responseString);

            if (responseJson.has(B2CAppConstant.KEY_STATUS) && responseJson.getInt(B2CAppConstant.KEY_STATUS) == 1
                    && responseJson.has(B2CAppConstant.KEY_SO_KEY) && responseJson.has(B2CAppConstant.KEY_SO_ITEM_KEY)
                    && responseJson.has(DSRAppConstant.KEY_DUMMY_KEY) && responseJson.has(B2CAppConstant.KEY_CUST_KEY)) {


                B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().updatePendingOrderSoItemKeyData(responseJson.getString(B2CAppConstant.KEY_CUST_KEY),
                        responseJson.getString(B2CAppConstant.KEY_SO_KEY), responseJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY),
                        responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY));

					/*B2CApp.b2cLdbs.updatePendingOrder(B2CPlaceOrderScreen.this, responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
							responseJson.getString(B2CAppConstant.KEY_CUST_KEY), responseJson.getString(B2CAppConstant.KEY_SO_KEY), 
									responseJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));*/
                et_search_hint.setSelectAllOnFocus(true);

                alertUserP(B2CPlaceOrderScreen.this, "Success", "Order detail updated successfully", "OK");

                if (isLimitExceeded) {
                    isLimitExceeded = false;
                    alertUserP(B2CPlaceOrderScreen.this, "Warning!", "Order value exeeds available credit limit", "OK");
                }

                B2CPendingOrderScreen.toUpdateCustomerDetail = true;
                B2CCounterDetailScreen.toUpdateCounterDetail = true;
                return;
            } else
                Logger.LogError("Incomplete response", " : params missing");

        } catch (JSONException e) {
            Logger.LogError("JSONException", " : " + e.toString());
            e.printStackTrace();
        }
        et_search_hint.setSelectAllOnFocus(true);

        B2CApp.b2cPreference.setIsUpdatedPendingOrders(false);
        alertUserP(B2CPlaceOrderScreen.this, "Alert !", message, "OK");
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
            //	Logger.LogError("Incomplete response", " : params missing");

        } catch (JSONException e) {
            Logger.LogError("JSONException", " : " + e.toString());
            e.printStackTrace();
        }

        //alertUserP(B2CPlaceOrderScreen.this, "Failed", message, "OK");
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
            json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "432e");
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());
            json.put(B2CAppConstant.KEY_USER_KEY, "" + B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, "" + B2CApp.b2cPreference.getUnitKey());

            json.put(DSRAppConstant.KEY_DUMMY_KEY, "" + B2CApp.b2cPreference.getNewEntryCount());
            json.put(B2CAppConstant.KEY_CUST_KEY, currentPendingOrder.getCustomer_key());
            json.put(B2CAppConstant.KEY_CONTACT_KEY, currentPendingOrder.getContact_key());
            json.put(B2CAppConstant.KEY_SO_KEY, "");
            json.put(B2CAppConstant.KEY_SO_ITEM_KEY, "");
            json.put(B2CAppConstant.KEY_MI_KEY, currentPendingOrder.getMi_key());
            json.put(B2CAppConstant.KEY_TAX_PERCENT, currentPendingOrder.getTax_percent());
            json.put(B2CAppConstant.KEY_SO_ITEM_QTY, currentPendingOrder.getOrdered_qty());
            json.put(B2CAppConstant.KEY_SUPPLIED_QTY, "" + 0);
            json.put(B2CAppConstant.KEY_PUR_ODR_DATE, "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime()));

            //String pur_odr_type = isCounterOrder ? "MAC" : "MAT";
            json.put(B2CAppConstant.KEY_PUR_ODR_TYPE, "" + purchase_order_type);
            json.put(B2CAppConstant.KEY_ENTERED_ON, "" + currentPendingOrder.getEntered_on());

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
        }

        return json;
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

            case R.id.btn_header_right:
                gotoB2CProductsCatalogue();
                break;

            case R.id.et_search_hint:
                if (et_search_hint.isFocused()) {
                    et_search_hint.clearFocus();
                    et_search_hint.requestFocus();
                } else {
                    et_search_hint.requestFocus();
                    et_search_hint.clearFocus();
                }
                break;


        }
    }

    private void initPopup() {
		
		/*if (ppWindow==null || popupHolder==null) {
			ppWindow = initPopupView();
		}*/

    }

    private void showPopup() {

        if (ppWindow != null && !ppWindow.isShowing()) {
            //	bg_dim_layout.getForeground().setAlpha(200);
            ppWindow.showAtLocation(findViewById(android.R.id.content),
                    Gravity.CENTER, 0, 0);
        }
    }

    private void dismissPopup() {
        if (ppWindow != null && ppWindow.isShowing())
            ppWindow.dismiss();
    }

    private Dialog initPopupView() {
        popupdialog = new Dialog(this);
        popupdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupdialog.setContentView(R.layout.popup_stock);
		/*LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.popup_stock, (ViewGroup) findViewById(R.id.PopUpView));*/

        popupHolder = new PopupHolder();

        popupHolder.listview_stock = (ListView) popupdialog.findViewById(R.id.listview_stock);

        popupHolder.btn_pop_done = (Button) popupdialog.findViewById(R.id.btn_pop_done);
        popupHolder.btn_pop_close = (ImageView) popupdialog.findViewById(R.id.btn_pop_close);

        popupHolder.btn_pop_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                popupdialog.dismiss();
                //	ppWindow.dismiss();
            }
        });

        popupHolder.btn_pop_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                popupdialog.dismiss();
//				ppWindow.dismiss();
            }
        });
		/*ppWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		ppWindow.setBackgroundDrawable(new BitmapDrawable());
		ppWindow.setTouchable(true);
		ppWindow.setOutsideTouchable(true);

		ppWindow.setTouchInterceptor(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (ppWindow.isShowing()) {
						ppWindow.dismiss();
						return true;
					}
				}
				return false;
			}
		});*/


        popupdialog.show();
        return popupdialog;
    }

    private boolean fillPopup() {
        if (stocksInBranch.size() > 0) {
            stockAdapter = new B2CStockAdapter(this, stocksInBranch);
            popupHolder.listview_stock.setAdapter(stockAdapter);
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

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_PLACE_ORDER;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
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

}
