package com.isteer.b2c.activity.B2CLancher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.isteer.b2c.activity.action.B2CAddTodaysBeat;
import com.isteer.b2c.activity.action.B2CCollectionEntryScreen;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.counter_details.B2CLocateScreen;
import com.isteer.b2c.activity.counter_details.B2CPlaceOrderScreen;
import com.isteer.b2c.activity.reports.B2CCollectionSumScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.adapter.RCV_B2CProductCatalogueAdaptor;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.dao.ProductData_DAO;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;
import com.isteer.b2c.model.StockData;
import com.isteer.b2c.repository.ProductViewModel;
import com.isteer.b2c.utility.Logger;

public class B2CProductsCatalogue extends AppCompatActivity implements OnClickListener {

    private TextView header_title;
    private ImageView btn_header_right;
    public static boolean toRefreshScreen = false;
    public static int previousScreen = B2CAppConstant.SCREEN_SYNC;
    public static ArrayList<ProductData> listEntries = new ArrayList<ProductData>();
    public PendingOrderData currentPendingOrder = new PendingOrderData();

    private ListView nameEntryList;

    private PopupWindow ppWindow;
    private CoordinatorLayout bg_dim_layout;
    private PopupHolder popupHolder;

    private Spinner spinnerBrand;
    private LinearLayout lt_spinnerBrand;
    private AppBarLayout lt_screen_header;
    private String strHint = "";
    private String strbrands = "";
    private EditText et_search_hint;

    private ArrayList<String> manufacturersList = new ArrayList<String>();
    private ArrayList<StockData> stocksInBranch = new ArrayList<StockData>();

    private static ProgressDialog pdialog;
    private static String PROGRESS_MSG = "Placing order";
    public B2CStockAdapter stockAdapter;
    private LinearLayout llbtn_ringtones;
    private Dialog popupdialog;
    private Toolbar mToolbar;
    private LinearLayout SCREEN_HEADER;
    private RecyclerView rcv_nameEntryList;
    private RCV_B2CProductCatalogueAdaptor rcv_catalogueAdaptor;
    private ProductViewModel productViewModel;
    public ProductData_DAO productData_dao;

    private static class PopupHolder {

        public ListView listview_stock;
        public View btn_pop_done;
        public View btn_pop_close;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.scr_isr_product_catalogue);

        initVar();


        productData_dao = B2CApp.getINSTANCE().getRoomDB().productData_dao();
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);


        /*  MyView.concertList1.observeOn(this, {productlist ->
                rcv_catalogueAdaptor.submitList(productlist) });
        rcv_nameEntryList.setAdapter(rcv_catalogueAdaptor);
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        productViewModel.getProductDataList().observe(this, new Observer<List<ProductData>>() {
            @Override
            public void onChanged(@Nullable List<ProductData> productData) {
                rcv_catalogueAdaptor.setProductData(productData);
            }


        });*/

    }

    @Override
    protected void onResume() {

        super.onResume();

        if (toRefreshScreen) {
            //toRefreshScreen = false;
          //  refreshScreen();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    B2CApp.b2cUtils.hideSoftKeyboard(et_search_hint);
                }
            }, 500);
        }

        suggestList();
        setSpinnerBrand();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initVar() {
        //  mToolbar = findViewById(R.id.tb_toolbar);

        //setSupportActionBar(mToolbar);

        //	SCREEN_HEADER = (LinearLayout)findViewById(R.id.SCREEN_HEADER);
        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Catalogue");
        toRefreshScreen = true;

        llbtn_ringtones = (LinearLayout) findViewById(R.id.llbtn_ringtones);
        lt_spinnerBrand = (LinearLayout) findViewById(R.id.lt_spinnerBrand);
        lt_screen_header = (AppBarLayout) findViewById(R.id.lt_screen_header);
        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);


        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);

        nameEntryList = ((ListView) findViewById(R.id.nameEntryList));
        rcv_nameEntryList = ((RecyclerView) findViewById(R.id.rcv_nameEntryList));
        spinnerBrand = ((Spinner) findViewById(R.id.spinnerBrand));

        rcv_nameEntryList.setLayoutManager(new LinearLayoutManager(this));
        //nameEntryList.setPadding(nameEntryList.getPaddingLeft(), 112+108, nameEntryList.getPaddingRight(), nameEntryList.getPaddingBottom());

        nameEntryList.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                B2CApp.b2cUtils.hideSoftKeyboard(et_search_hint);
                return false;
            }
        });
        nameEntryList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
/*if(mLastFirstVisibleItem<i) {

					onHide();
				}
				if(mLastFirstVisibleItem>i)
				{
					onShow();
					;
							Log.i("SCROLLING UP", "TRUE");



				}
				mLastFirstVisibleItem=i;*/

            }
        });
        et_search_hint = ((EditText) findViewById(R.id.et_search_hint));
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
                productViewModel.init(productData_dao,""+strbrands+"%","%"+strHint+"%");

                productViewModel.productDataList.observe(B2CProductsCatalogue.this, pagedList -> {
                    rcv_catalogueAdaptor.submitList(pagedList);

                });
              //  suggestList();

            }

        });


        bg_dim_layout = (CoordinatorLayout) findViewById(R.id.bg_dim_layout);
//		bg_dim_layout.getForeground().setAlpha(0);
    }

    private void setSpinnerBrand() {
        manufacturersList = (ArrayList<String>) B2CApp.getINSTANCE().getRoomDB().productData_dao().fetchDistinctMenuName();


		Logger.LogError("manufacturersList",""+manufacturersList);
        manufacturersList.add(0, B2CAppConstant.KEY_ALL_BRANDS);
        manufacturersList.remove(manufacturersList.contains(""));
        manufacturersList.removeAll(Collections.singleton(null));
        manufacturersList.remove(manufacturersList.contains(" "));
        Logger.LogError("manufacturersList",""+manufacturersList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, manufacturersList);

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


             //   strHint = "" + et_search_hint.getText().toString();
                if (pos == 0) {
                    strbrands = "";
                } else {
                    strbrands = manufacturersList.get(pos);
                }
                productViewModel.init(productData_dao,""+strbrands+"%","%");
                productViewModel.productDataList.observe(B2CProductsCatalogue.this, pagedList -> {
                    rcv_catalogueAdaptor.submitList(pagedList);

                });
                et_search_hint.getText().clear();
               // suggestList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

              //  suggestList();
            }
        });
    }

    @Override
    public void onBackPressed() {
        gotoB2CMenuScreen();
        //goBack();
    }

    public static void slideUp(final View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.f);

        if (view.getHeight() > 0) {
            slideUpNow(view);
        } else {
            // wait till height is measured
            view.post(new Runnable() {
                @Override
                public void run() {
                    slideUpNow(view);
                }
            });
        }
    }

    private static void slideUpNow(final View view) {
        view.setTranslationY(view.getHeight());
        view.animate()
                .translationY(0)
                .alpha(1.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (view.getVisibility() != View.VISIBLE) {
                            view.setVisibility(View.VISIBLE);
                            view.setAlpha(1.f);
                        }

                    }
                });
    }

    // slide the view from its current position to below itself
    public static void slideDown(final View view) {
        view.animate()
                .translationY(view.getHeight())
                .alpha(0.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // superfluous restoration
                        view.setVisibility(View.GONE);
                        view.setAlpha(1.f);
                        view.setTranslationY(0.f);
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            gotoB2CMenuScreen();

        }
        return true;

    }

    private void goBack() {
        switch (previousScreen) {

            case B2CAppConstant.SCREEN_ADDTO_BEAT:
                gotoB2CAddTodaysBeat();
                break;

            case B2CAppConstant.SCREEN_COLL_ENTRY:
                gotoB2CCollectionEntryScreen();
                break;

            case B2CAppConstant.SCREEN_COLL_SUMM:
                gotoB2CCollectionSumScreen();
                break;

            case B2CAppConstant.SCREEN_COUNTER_DET:
                gotoB2CCounterDetailScreen();
                break;

            case B2CAppConstant.SCREEN_COUNTERS:
                gotoB2CCountersScreen();
                break;

            case B2CAppConstant.SCREEN_CUST_SEARCH:
                gotoB2CCustListScreen();
                break;

            case B2CAppConstant.SCREEN_LOCATE:
                gotoB2CLocateScreen();
                break;

            case B2CAppConstant.SCREEN_MENU:
                gotoB2CMenuScreen();
                break;

            case B2CAppConstant.SCREEN_SYNC:
                gotoB2CSyncScreen();
                break;

            case B2CAppConstant.SCREEN_PLACE_ORDER:
                gotoB2CPlaceOrderScreen();
                break;

            case B2CAppConstant.SCREEN_PENDING_ORDER:
                gotoB2CPendingOrders();
                break;

        }
    }

    private void refreshScreen() {
        et_search_hint.setText("");
        strHint = "";
        spinnerBrand.setSelection(0);
        suggestList();

        //updateCurrentCustomerData(DSRApp.dsrLdbs.fetchSelected(this, DSRTableCreate.TABLE_AERP_CONTACT_MASTER, null, DSRTableCreate.COLOUMNS_CONTACT_MASTER.company_name.name(), DSRLocalDBStorage.SELECTION_OPERATION_LIKE, new String[]{currentCustomer.getCompany_name()}));
    }

    private void suggestList() {


        productViewModel.init(productData_dao,""+strbrands+"%","%"+strHint+"%");

        rcv_catalogueAdaptor = new RCV_B2CProductCatalogueAdaptor(this);
        productViewModel.productDataList.observe(this, pagedList -> { rcv_catalogueAdaptor.submitList(pagedList);
            rcv_nameEntryList.setAdapter(rcv_catalogueAdaptor);
        });


       /* listEntries.clear();
		*//*Cursor cCursor = B2CApp.b2cLdbs.fetchLike(this, B2CTableCreate.TABLE_B2C_PRODUCT_MASTER,
				B2CTableCreate.COLOUMNS_PRODUCT_MASTER.display_code.name(), strHint,
				B2CTableCreate.COLOUMNS_PRODUCT_MASTER.manu_name.name() + " , " + B2CTableCreate.COLOUMNS_PRODUCT_MASTER.display_code.name());
		listEntries.addAll(new B2CCursorFactory().fetchAsProducts(cCursor, B2CTableCreate.getColoumnArrayTableProductMaster()));*//*

        ArrayList<ProductData> productDataArrayList = (ArrayList<ProductData>) B2CApp.getINSTANCE().getRoomDB().
                productData_dao().getAllProductData("%" + strHint + "%", "" + strbrands + "%");
       *//* rcv_catalogueAdaptor = new RCV_B2CProductCatalogueAdaptor(this);
        rcv_catalogueAdaptor.setProductData(productDataArrayList);*//*

        //rcv_catalogueAdaptor = new RCV_B2CProductCatalogueAdaptor(this, listEntries);
        rcv_nameEntryList.setAdapter(rcv_catalogueAdaptor);
*/
    }

    public void showStockPopup() {
        //initPopup();
        initPopupView();
        if (fillPopup())
            showPopup();

		/*else
			alertUserP(this, "Error", "Unable to check stock in a timely manner. Please try again", "OK");*/
    }

    public void onCheckStockClicked(PendingOrderData tOrder, int position) {
        //tOrder.setCustomer_key(B2CCounterDetailScreen.currentMap.get(B2CTableCreate.COLOUMNS_CONTACT_MASTER.cmkey.name()));
        //tOrder.setContact_key(B2CCounterDetailScreen.currentContactKey);
        //tOrder.setDate(B2CApp.b2cUtils.getFormattedDate("yyyyMMdd", new Date().getTime()));

        currentPendingOrder = tOrder;

        PROGRESS_MSG = "Checking stock...";
        new OrderTask(null, position).execute(B2CApp.b2cPreference.getBaseUrl() + B2CAppConstant.METHOD_GET_STOCK);
    }

    class OrderTask extends AsyncTask<String, String, String> {

        private int operationType;
        private String uriInProgress;
        private TextView btnOrder;
        private int position;

        public OrderTask(TextView btn, int pos) {
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

            B2CApp.b2cUtils.updateMaterialProgress(B2CProductsCatalogue.this, PROGRESS_MSG);

        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);

            B2CApp.b2cUtils.dismissMaterialProgress();

            String message = "Operation failed in a timely manner. Please try again";

            if (responseString == null) {

                alertUserP(B2CProductsCatalogue.this, "Failed", message, "OK");
                return;
            }

            if (operationType == B2CAppConstant.OPTYPE_GET_STOCK) {
                onPostGetStock(responseString, position);
            }

        }
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

        //	alertUserP(B2CProductsCatalogue.this, "Failed", message, "OK");
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

        return optype;

    }

    private JSONObject getJsonParams(int opType) throws JSONException {

        JSONObject json = new JSONObject();

        if (opType == B2CAppConstant.OPTYPE_GET_STOCK) {
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
                finish();
                break;
            case R.id.img_home:
                gotoB2CMenuScreen();
                break;


        }
    }

    private void initPopup() {
		/*
		if (ppWindow==null || popupHolder==null) {
			ppWindow = initPopupView();
		}*/

    }

    private void showPopup() {

        if (ppWindow != null && !ppWindow.isShowing()) {
            bg_dim_layout.getForeground().setAlpha(200);
            ppWindow.showAtLocation(findViewById(android.R.id.content),
                    Gravity.CENTER, 0, 0);
            ppWindow.setOutsideTouchable(false);
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
		
	/*	ppWindow = new PopupWindow(popupdialog, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);*/
//		ppWindow.setBackgroundDrawable(new BitmapDrawable());
		/*ppWindow.setTouchable(true);
		ppWindow.setOutsideTouchable(false);

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
		});

		ppWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				bg_dim_layout.getForeground().setAlpha(0);
			}
		});
		*/
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
        popupdialog.show();
        return popupdialog;
    }

    private boolean fillPopup() {
        Logger.LogError("stocksInBranch", "" + stocksInBranch.size());
        if (stocksInBranch.size() > 0) {
            stockAdapter = new B2CStockAdapter(this, stocksInBranch);
            popupHolder.listview_stock.setAdapter(stockAdapter);
            popupdialog.findViewById(R.id.listview_stock).setVisibility(View.VISIBLE);
            popupdialog.findViewById(R.id.lt_no_stock).setVisibility(View.GONE);
            return true;
        } else
            popupdialog.findViewById(R.id.listview_stock).setVisibility(View.GONE);
        popupdialog.findViewById(R.id.lt_no_stock).setVisibility(View.VISIBLE);
        //  alertUserP(this, "Alert !", "No stock available", "OK");
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

    private void gotoB2CCustListScreen() {

        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CPendingOrders() {

        startActivity(new Intent(this, B2CPendingOrderScreen.class)
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
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CAddTodaysBeat() {

        startActivity(new Intent(this, B2CAddTodaysBeat.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRDayScreen() {

        startActivity(new Intent(this, DSRDayScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCollectionEntryScreen() {

        startActivity(new Intent(this, B2CCollectionEntryScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCollectionSumScreen() {

        startActivity(new Intent(this, B2CCollectionSumScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CLocateScreen() {

        startActivity(new Intent(this, B2CLocateScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CPlaceOrderScreen() {

        startActivity(new Intent(this, B2CPlaceOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void onShow() {
        lt_screen_header.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    public void onHide() {
        lt_screen_header.animate().translationY(-112).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.cardview_compat_inset_shadow);
    }
}
