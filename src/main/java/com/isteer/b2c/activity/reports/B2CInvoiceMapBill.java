package com.isteer.b2c.activity.reports;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.adapter.PendingMapBill;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.B2CTableCreate;
import com.isteer.b2c.gson.Gson_PendingMapBills;
import com.isteer.b2c.model.BillData;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.utility.CustomPopupDialog;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.volley.VolleyHttpRequest;
import com.isteer.b2c.volley.VolleyTaskListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class B2CInvoiceMapBill extends AppCompatActivity implements OnClickListener, VolleyTaskListener {


    private TextView header_title;
    private ImageView btn_header_right;

    public static boolean isFromCounter = false;
    public static boolean isFromMenu = false;
    public static boolean backToCounter = true;

    private TextView tv_from_date, tv_to_date;

    public static String currentCounterName = "";

    private ListView collectionList;
    public static ArrayList<BillData> billMapList = new ArrayList<BillData>();
    private String fromDate, toDate;
    private PendingMapBill pendingmapbilladapter;
    private RecyclerView rcv_pendingBillList;
    private SwipeRefreshLayout srl_refresh;
    private Disposable disposableCustomerCollection;
    private LinearLayout lt_mapbill;
    public static CollectionData collectionData;
    private TextView tv_counter_name, tv_contact_name, txt_coll_bal_amount, txt_empty;
    private BillData selectedBillData;
    private LinearLayout lt_empty;
    private CustomPopupDialog customPopupDialog;
    private double unmap_col_bal_amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.b2cinvoice_mapbill);

        initVar();
        setCustomerDetails();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (customPopupDialog != null)
            customPopupDialog.dismissDialog();
        if (B2CApp.b2cUtils.isNetAvailable()) {

            syncPendingBills();

        } else {
            Toast.makeText(B2CInvoiceMapBill.this, "" + getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

        }
        Logger.LogError("collectionData", "" + collectionData.getPay_coll_key());
        Logger.LogError("collectionDatagetCus_key", "" + collectionData.getCus_key());

    }

    private void setCustomerDetails() {
        if (collectionData != null) {
            tv_counter_name.setText("" + collectionData.getCustomer_name());
            txt_coll_bal_amount.setText("₹ : " + B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + collectionData.getBalance_amount()));
            unmap_col_bal_amt = Double.parseDouble("" + collectionData.getBalance_amount());
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
        header_title.setText("Bill Map");

        lt_empty = findViewById(R.id.lt_empty);
        tv_counter_name = findViewById(R.id.tv_counter_name);
        tv_contact_name = findViewById(R.id.tv_contact_name);
        txt_coll_bal_amount = findViewById(R.id.txt_coll_bal_amount);

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);


        rcv_pendingBillList = findViewById(R.id.rcv_pendingBillList);
        rcv_pendingBillList.setLayoutManager(new LinearLayoutManager(this));


        srl_refresh = findViewById(R.id.srl_refresh);
        srl_refresh.setRefreshing(false);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (B2CApp.b2cUtils.isNetAvailable()) {

                    syncPendingBills();

                } else {
                    Toast.makeText(B2CInvoiceMapBill.this, "" + getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                    srl_refresh.setRefreshing(false);
                }
            }
        });
        findViewById(R.id.lt_save).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

        goBack();
    }


    private void goBack() {
        finish();
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
            case R.id.lt_save:
                gotoB2CProductsCatalogue();
                break;


        }
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


    private void syncPendingBills() {
        try {
            B2CApp.b2cUtils.updateMaterialProgress(this, "Loading Pending Invoices");
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                            + B2CAppConstant.METHOD_GET_PENDINGBILLS, getJsonParams(B2CAppConstant.METHOD_GET_PENDINGBILLS)
                    , B2CAppConstant.METHOD_GET_PENDINGBILLS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonParams(String opType) throws JSONException {

        JSONObject json = new JSONObject();


        if (opType == B2CAppConstant.METHOD_GET_PENDINGBILLS) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_CUST_KEY, collectionData.getCus_key());
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());


        } else if (opType == B2CAppConstant.METHOD_SAVE_ACCOUNT_DETAILS) {
            JSONArray jsonArray = new JSONArray();
            JSONObject innerJson = new JSONObject();
            // Logger.LogError("getMap_bill_amount", "" + selectedBillData.getMap_bill_amount());
            // Logger.LogError("getPending_amount", "" + selectedBillData.getPending_amount());
            // Logger.LogError("map_bill_amount.name()", "" + B2CTableCreate.COLOUMNS_PENDING_BILLS.map_bill_amount.name());
            innerJson.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            innerJson.put(B2CAppConstant.KEY_CUST_KEY, collectionData.getCus_key());
            innerJson.put(B2CTableCreate.COLOUMNS_PENDING_BILLS.invoice_key.name(), selectedBillData.getInvoice_key());
            innerJson.put(B2CTableCreate.COLOUMNS_PENDING_BILLS.invoice_amount.name(), selectedBillData.getInvoice_amount());
            innerJson.put(B2CTableCreate.COLOUMNS_PENDING_BILLS.map_bill_amount.name(), selectedBillData.getMap_bill_amount());
            innerJson.put(B2CTableCreate.COLOUMNS_PENDING_BILLS.pending_amount.name(), selectedBillData.getPending_amount());
            innerJson.put(B2CTableCreate.COLOUMNS_PENDING_BILLS.pay_coll_key.name(), collectionData.getPay_coll_key());
            jsonArray.put(innerJson);
            json.put(B2CAppConstant.KEY_DATA, jsonArray);

        }
        return json;

    }

    @Override
    public void handleResult(String method_name, JSONObject response) throws JSONException {
        Logger.LogError("method_name", "" + method_name);
        B2CApp.b2cUtils.dismissMaterialProgress();
        if (response == null) {

            if (method_name == B2CAppConstant.METHOD_GET_PENDINGBILLS) {
                srl_refresh.setRefreshing(false);
            } else if (method_name == B2CAppConstant.METHOD_SAVE_ACCOUNT_DETAILS) {
                alertUserP(this, "Failed !", "Bill Mapping failed", "Ok");
            }
        } else {

            if (method_name == B2CAppConstant.METHOD_GET_PENDINGBILLS) {
                onPostGetAllCollectionNew(response);

            } else if (method_name == B2CAppConstant.METHOD_SAVE_ACCOUNT_DETAILS) {
                onPostBillMapped(response);
            }
        }
    }


    @Override
    public void handleError(VolleyError e) {
        srl_refresh.setRefreshing(false);
    }


    private void onPostGetAllCollectionNew(JSONObject responseString) {
        Logger.LogError("unmap_col_bal_amt",""+unmap_col_bal_amt);
        txt_coll_bal_amount.setText("₹ : " + B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal(""+Double.valueOf(unmap_col_bal_amt).intValue()));
        Gson_PendingMapBills gson_pendingBills = new Gson().fromJson(responseString.toString(), Gson_PendingMapBills.class);

        if (Integer.parseInt(gson_pendingBills.getStatus()) == 0) {
            findViewById(R.id.srl_refresh).setVisibility(View.GONE);
            findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
            return;
        }
        if (gson_pendingBills.getBillDataList().size() == 0) {

        } else {
            updateCollections((gson_pendingBills.getBillDataList()));

        /*    disposableCustomerCollection = Observable.fromArray(gson_pendingBills)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(pendingBills -> {
                        updateCollections((pendingBills.getBillDataList()));
                    }, throwable ->
                            Logger.LogError("exception", "" + throwable.toString()));*/

        }


        srl_refresh.setRefreshing(false);


    }

    private void onPostBillMapped(JSONObject response) {

        billMapList.clear();
        try {
            if (response.has(B2CAppConstant.KEY_STATUS) && response.getInt(B2CAppConstant.KEY_STATUS) == 1) {
                Logger.LogError("unmap_col_bal_amt",""+unmap_col_bal_amt);
                Logger.LogError("selectedBillData.get",""+selectedBillData.getMap_bill_amount());
                unmap_col_bal_amt = (unmap_col_bal_amt - Double.parseDouble(selectedBillData.getMap_bill_amount()));
                selectedBillData = new BillData();

                alertUserP(this, "Success !", "Bill Mapping Successfully", "Ok");

                syncPendingBills();
            } else {
                alertUserP(this, "Failed !", "Bill Mapping Failed", "Ok");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateCollections(ArrayList<BillData> billDataList) {

        Logger.LogError("billMapList", "" + billDataList.size() + billDataList);
        pendingmapbilladapter = new PendingMapBill(this, billDataList, (unmap_col_bal_amt), new PendingMapBill.onclickInterface() {
            @Override
            public void onclick(BillData billData, PendingMapBill.ViewHolder holder) {
                onItemClick(billData, holder);
            }

            @Override
            public void onEdittextclick(double map_amount) {
                Logger.LogError("map_amount", "" + map_amount);
                collectionData.setBalance_amount("" + map_amount);
                txt_coll_bal_amount.setText("₹ : " + B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + map_amount));
            }
        });
        rcv_pendingBillList.setAdapter(pendingmapbilladapter);
        if (billDataList.size() != 0) {
            findViewById(R.id.lt_empty).setVisibility(View.GONE);
            findViewById(R.id.srl_refresh).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.srl_refresh).setVisibility(View.GONE);
        }


    }

    private void onItemClick(BillData billData, PendingMapBill.ViewHolder holder) {
        if (B2CApp.b2cUtils.isNetAvailable()) {
            selectedBillData = billData;
            //  alertForMapBill(holder);
            mapbillButtonPopup(holder);
        } else {
            Toast.makeText(this, "No internet please try again", Toast.LENGTH_SHORT).show();
        }


    }

    private void syncSaveAccountDetails() {
        try {
            B2CApp.b2cUtils.updateMaterialProgress(this, "Bill Mapping...");
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                            + B2CAppConstant.METHOD_SAVE_ACCOUNT_DETAILS, getJsonParams(B2CAppConstant.METHOD_SAVE_ACCOUNT_DETAILS)
                    , B2CAppConstant.METHOD_SAVE_ACCOUNT_DETAILS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void alertForMapBill(PendingMapBill.ViewHolder holder) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure map  bill").setTitle("Alert !").setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        holder.iv_mapbill.setFocusable(false);
                        holder.iv_mapbill.setClickable(false);

                        syncSaveAccountDetails();
                        dialog.cancel();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void mapbillButtonPopup(PendingMapBill.ViewHolder holder) {
        String title = getResources().getString(R.string.map_bill);
        String message = getResources().getString(R.string.are_map_bill);
        customPopupDialog = new CustomPopupDialog(this, title, message, "", "No", "Yes", new CustomPopupDialog.myOnClickListenerLeft() {
            @Override
            public void onButtonClickLeft(String s) {

            }
        }, new CustomPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight(String s) {
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    holder.iv_mapbill.setFocusable(false);
                    holder.iv_mapbill.setClickable(false);

                    syncSaveAccountDetails();
                }else {
                    Toast.makeText(B2CInvoiceMapBill.this,R.string.nointernetdescription, Toast.LENGTH_SHORT).show();
                }

            }
        }, new CustomPopupDialog.myOnClickListenerThird() {
            @Override
            public void onButtonClickThird(String s) {

            }
        });
    }
}
