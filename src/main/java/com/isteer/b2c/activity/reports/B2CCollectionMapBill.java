package com.isteer.b2c.activity.reports;

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
import com.isteer.b2c.adapter.RCVUnmappedCollectionAdapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.gson.Gson_CustomerCollection;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.volley.VolleyHttpRequest;
import com.isteer.b2c.volley.VolleyTaskListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;


public class B2CCollectionMapBill extends AppCompatActivity implements OnClickListener, VolleyTaskListener {

    private TextView header_title;
    private ImageView btn_header_right;

    public static boolean isFromCounter = false;
    public static boolean isFromMenu = false;
    public static boolean backToCounter = true;

    private TextView tv_from_date, tv_to_date;

    public static String currentCounterName = "";

    private ListView collectionList;
    private String fromDate, toDate;
    private RCVUnmappedCollectionAdapter rcvcollectionadapter;
    private RecyclerView rcv_collectionList;
    private SwipeRefreshLayout srl_refresh;
    private Disposable disposableCustomerCollection;
    private LinearLayout lt_mapbill;
    public static ArrayList<CollectionData> collectionEntries = new ArrayList<CollectionData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.b2c_collection_mapbill);

        initVar();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (B2CApp.b2cUtils.isNetAvailable()) {

            startSyncNewCollections();

        } else {
            Toast.makeText(B2CCollectionMapBill.this, "" + getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();

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
        header_title.setText("Unmapped Collection");

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);


        collectionList = (ListView) findViewById(R.id.collectionList);
        rcv_collectionList = findViewById(R.id.rcv_collectionList);
        rcv_collectionList.setLayoutManager(new LinearLayoutManager(this));


        srl_refresh = findViewById(R.id.srl_refresh);
        srl_refresh.setRefreshing(false);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (B2CApp.b2cUtils.isNetAvailable()) {

                    startSyncNewCollections();

                } else {
                    Toast.makeText(B2CCollectionMapBill.this, "" + getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
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

    private void startSyncNewCollections() {
        B2CApp.b2cUtils.updateMaterialProgress(this, "Unmapped Collection Loading...");
        syncAllCollectionFromServer();


    }


    private void syncAllCollectionFromServer() {
        try {
            VolleyHttpRequest.makeVolleyPostHeaderActivity(this, B2CApp.b2cPreference.getBaseUrl()
                            + B2CAppConstant.METHOD_UNMAPPED_COLLECTION, getJsonParams(B2CAppConstant.METHOD_UNMAPPED_COLLECTION)
                    , B2CAppConstant.METHOD_UNMAPPED_COLLECTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJsonParams(String opType) throws JSONException {

        JSONObject json = new JSONObject();

        if (opType == B2CAppConstant.METHOD_UNMAPPED_COLLECTION) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());

        }

        return json;
    }

    @Override
    public void handleResult(String method_name, JSONObject response) throws JSONException {
        Logger.LogError("method_name", "" + method_name);
        B2CApp.b2cUtils.dismissMaterialProgress();
        if (response == null) {

            if (method_name == B2CAppConstant.METHOD_UNMAPPED_COLLECTION) {
                srl_refresh.setRefreshing(false);
            }
        } else {

            if (method_name == B2CAppConstant.METHOD_UNMAPPED_COLLECTION)
                onPostGetAllCollectionNew(response);

        }
    }

    @Override
    public void handleError(VolleyError e) {
        srl_refresh.setRefreshing(false);
    }


    private void onPostGetAllCollectionNew(JSONObject responseString) {

        Gson_CustomerCollection gsonCustomerCollection = new Gson().fromJson(responseString.toString(), Gson_CustomerCollection.class);

        if (gsonCustomerCollection.getCollectionList().size() == 0) {


        } else {
       /*     disposableCustomerCollection = Observable.fromArray(gsonCustomerCollection)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.io())
                    .subscribe(customerCollection -> {
                          collectionEntries = (customerCollection.getCollectionList());

                    }, throwable ->
                            Logger.LogError("exception", "" + throwable.toString()));*/
            collectionEntries = (gsonCustomerCollection.getCollectionList());
            updateCollections();
        }


        srl_refresh.setRefreshing(false);


    }

    private void updateCollections() {

        rcvcollectionadapter = new RCVUnmappedCollectionAdapter(this, collectionEntries);
        if (collectionEntries.size() != 0) {
            findViewById(R.id.lt_empty).setVisibility(View.GONE);
            findViewById(R.id.srl_refresh).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.srl_refresh).setVisibility(View.GONE);
        }
        rcv_collectionList.setAdapter(rcvcollectionadapter);


    }
}
