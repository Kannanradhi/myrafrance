package com.isteer.b2c.activity.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.activity.calender.DSRAddProgScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.adapter.RCV_B2CAddTodaysBeatAdapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.volley.VolleyHttpRequest;
import com.isteer.b2c.volley.VolleyTaskListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class B2CAddTodaysBeat extends AppCompatActivity implements OnClickListener,VolleyTaskListener {

    private static final String TAG = "B2CAddTodaysBeat";


    public static ArrayList<HashMap<String, String>> listEntries = new ArrayList<HashMap<String, String>>();


    private ListView nameEntryList;

    public TextView txt_todays_count;

    public TextView header_title;
    private ImageView btn_header_left, btn_header_right, img_back, img_home;

    public static boolean isFromAddProg = false;
    public static boolean isFromLogin = false;
    public static boolean toRefresh = false;
    private static int longselectedCount;
    private static ArrayList<String> alreadySelectedCounters = new ArrayList<String>();
    private Typeface raleway_bold, raleway;
    private RecyclerView rcv_nameEntryList;
    private RCV_B2CAddTodaysBeatAdapter rcv_nameEntryAdaptor;
    private ArrayList<String> alreadyAddedCounters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scr_b2c_counterlist);
        initVar();


    }

    @Override
    protected void onResume() {

        super.onResume();

        updateAlreadyAddedCounters();

        if (toRefresh) {
            toRefresh = false;
            suggestList();

        }
        //  resetCount();


    }

    private void initVar() {


        // B2CApp.b2cPreference.setSelectedCount(0);
        header_title = (TextView) findViewById(R.id.header_title);
        //  header_title.setTypeface(raleway_bold);
        header_title.setText("Add Today's Plan");
        findViewById(R.id.lt_todays_count).setOnClickListener(this);

        txt_todays_count = (TextView) findViewById(R.id.txt_todays_count);
        //    txt_todays_count.setTypeface(raleway_bold);
        txt_todays_count.setOnClickListener(this);
        txt_todays_count.setVisibility(View.VISIBLE);
        header_title.setTextColor(getResources().getColor(R.color.White));

        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);

        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);


        nameEntryList = ((ListView) findViewById(R.id.nameEntryList));
        rcv_nameEntryList = ((RecyclerView) findViewById(R.id.rcv_nameEntryList));
        rcv_nameEntryList.setLayoutManager(new LinearLayoutManager(this));

    }

    private void suggestList() {
       /* listEntries = new B2CCursorFactory().fetchColoumns(B2CApp.b2cLdbs.fetchAreaWiseCount(B2CAddTodaysBeat.this),
                new String[]{B2CTableCreate.COLOUMNS_CONTACT_MASTER.area.name(), B2CTableCreate.COLOUMNS_CONTACT_MASTER.area_name.name(), "count"});*/
       /* nameEntryAdaptor = new B2CAddTodaysBeatAdapteropensans_light(this, listEntries);
        nameEntryList.setAdapter(nameEntryAdaptor);*/


        ArrayList<OrderNewData> customerDataArrayList = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().customerData_dao().getTodaysBeatPlan();

        if (customerDataArrayList.size() < 1) {
            findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.lt_empty).setVisibility(View.GONE);

        }
        rcv_nameEntryAdaptor = new RCV_B2CAddTodaysBeatAdapter(this, customerDataArrayList);
        // rcv_nameEntryAdaptor = new RCV_B2CAddTodaysBeatAdapter(this, listEntries);
        rcv_nameEntryList.setAdapter(rcv_nameEntryAdaptor);
    }

    private void resetCount() {

        long setTimeLong = Long.parseLong(new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date()));
        long currentTimeLong = 1;
        Logger.LogDebug("setTimeLong", "" + setTimeLong);
        //   Logger.LogDebug();("currentTimeLong",""+currentTimeLong);
        if (setTimeLong >= currentTimeLong) {
            B2CApp.b2cPreference.setSelectedCount(0);

        }
    }

    public void selectedCount(ArrayList<String> alreadyAddedCounters) {

        if (!(alreadyAddedCounters.size() < 0)) {
            Logger.LogDebug("alreadyAddedCounters.size()", "" + alreadyAddedCounters.size());
            // txt_todays_count.setText("Today's Planned Counters (" + B2CApp.b2cPreference.getSelectedCount() + ")");
            txt_todays_count.setText("Today's Planned Counters ( " + alreadyAddedCounters.size() + " )");
        } else {
            txt_todays_count.setText("Today's Planned Counters ( 0 )");
        }


    }

    @Override
    public void onBackPressed() {
        gotoB2CMenuScreen();
        //  goBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            // goBack();

        }
        return true;

    }

    private void goBack() {
        if (isFromAddProg) {
            isFromAddProg = false;
            gotoDSRAddProgScreen();
        } else
            gotoB2CCountersScreen();
    }

    @Override
    public void onClick(View pView) {

        switch (pView.getId()) {

            case R.id.lt_todays_count:
                gotoB2CCountersScreen();
                break;
            case R.id.txt_todays_count:
                gotoB2CCountersScreen();
                break;
            case R.id.btn_header_right:
                gotoB2CProductsCatalogue();
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.img_home:
                gotoB2CMenuScreen();
                break;


            default:
                break;

        }
    }

    public void addToBeatClicked(String areaName, String areaCode) {
        insertBeatToDB(areaName, areaCode);
    }

    public void removeFromBeatClicked(String areaName, String areaCode) {
        removeFromBeat(areaName, areaCode);
    }

    private void insertBeatToDB(String areaName, String areaCode) {


        ArrayList<CustomerData> customerDataArrayList = (ArrayList<CustomerData>) B2CApp.getINSTANCE().getRoomDB().customerData_dao().getareaForEvent("" + areaCode + "%");

        for (int i = 0; i < customerDataArrayList.size(); i++) {
            CustomerData customer = customerDataArrayList.get(i);
            EventData eventData = new EventData();
            String eventDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date().getTime());
            String startDate = "" + DateFormat.format(B2CAppConstant.dateFormat, new Date().getTime());
            String from_date = "" + DateFormat.format(B2CAppConstant.datetimeFormat1, new Date().getTime());
            String entered_onDate = "" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime());
            String stopDate = "" + DateFormat.format(B2CAppConstant.datetimeFormat, (new Date().getTime()+60*60*1000));

            int newEntryCount = B2CApp.b2cPreference.getNewEntryCount() - 1;
            B2CApp.b2cPreference.setNewEntryCount(newEntryCount);

            eventData.setEvent_key(newEntryCount);
            eventData.setEvent_user_key(B2CApp.b2cPreference.getUserId());
            eventData.setEvent_type(DSRAppConstant.VISIT_TYPES[0]);
            eventData.setEvent_title("Counter Order");
           // eventData.setFrom_date_time(startDate + " " + "09:00:00");
            //eventData.setTo_date_time(startDate + " " + "10:00:00");
            eventData.setFrom_date_time(from_date);
         //   eventData.setTo_date_time(stopDate);
            eventData.setEvent_description("B2C test Desc");
            eventData.setStatus(DSRAppConstant.EVENT_STATUS.Pending.name());
            eventData.setCmkey("" + customer.getCmkey());
            eventData.setCus_key("" + customer.getCus_key());
            eventData.setArea(customer.getArea_name());
            eventData.setArea(customer.getArea_name());
            eventData.setCustomer_name(customer.getCompany_name());
            eventData.setEvent_month(startDate.substring(0, 7));
            eventData.setEvent_date(eventDate);
            eventData.setEntered_on(entered_onDate);
            eventData.setEvent_date_absolute(startDate.substring(8));
            eventData.setIs_synced_to_server("0");
            B2CApp.getINSTANCE().getRoomDB().eventdata_dao().insertEventData(eventData);
        }

        B2CCountersScreen.toRefresh = true;
        startSyncAddAllNewEvent();
    }

    private void removeFromBeat(String areaName, String areaCode) {
        /*Cursor cCursor = B2CApp.b2cLdbs.fetchSelected(B2CAddTodaysBeat.this, B2CTableCreate.TABLE_B2C_CONTACT_MASTER, null,
                B2CTableCreate.COLOUMNS_CONTACT_MASTER.area.name(), B2CLocalDBStorage.SELECTION_OPERATION_LIKE, new String[]{"" + areaCode}, null);
        ArrayList<String> customersInArea = new DSRCursorFactory().fetchAColoumn(cCursor, B2CTableCreate.COLOUMNS_CONTACT_MASTER.cmkey.name());
B2CApp.b2cLdbs.removeFromBeat(B2CAddTodaysBeat.this, customersInArea);*/

        String todayDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date());
        Logger.LogDebug("deleteareaForEvent", "" + areaCode);
     //   B2CApp.getINSTANCE().getRoomDB().eventdata_dao().deleteEventsOfDay("" + areaCode + "%", todayDate);
        B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateRemoveEventsOfDay("" + areaCode + "%", todayDate);
        startSyncAddAllNewEvent();
        B2CCountersScreen.toRefresh = true;
    }

    private void gotoB2CCounterDetail() {

        startActivity(new Intent(this, B2CCounterDetailScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoDSRAddProgScreen() {

        startActivity(new Intent(this, DSRAddProgScreen.class)
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

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_ADDTO_BEAT;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void addOneCount(ArrayList<String> alreadyAddedCounters) {

        selectedCount(alreadyAddedCounters);
    }


    private void updateAlreadyAddedCounters() {


        String startDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date());
        String[] status = {"Visited","Cancelled","Pending"};
        alreadyAddedCounters = (ArrayList<String>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().todayPlannedArea(startDate,status);
        Logger.LogDebug("alreadyAddedCounters", "" + alreadyAddedCounters.size());
        selectedCount(alreadyAddedCounters);
    }
    private void startSyncAddAllNewEvent()
    {


        ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchnonSynedData();

			/*boolean isOrdersUptodate = (B2CApp.b2cLdbs.fetchCustomSelection(mContext, B2CTableCreate.TABLE_B2C_PENDING_ORDERS, null, B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_item_key.name() + " < "+ "0"
										+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0")==null);*/

        if(eventDataArrayList.size() > 0)
        {
            if (B2CApp.b2cUtils.isNetAvailable())
                VolleyHttpRequest.makeVolleyPostHeaderActivity(this,B2CApp.b2cPreference.getBaseUrl2()
                        + DSRAppConstant.METHOD_ADDALL_NEW_EVENTS,getJSONInput(),DSRAppConstant.METHOD_ADDALL_NEW_EVENTS);
        }



    }

    private JSONObject getJSONInput() {


        JSONObject json = new JSONObject();
            JSONArray tJsonArray = new JSONArray();
            ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchnonSynedData();
        try {
            for (int i = 0; i < eventDataArrayList.size(); i++) {
                EventData mCursor = eventDataArrayList.get(i);


                JSONObject innerjson = new JSONObject();
                if (mCursor.getEvent_key() < 0) {
                    innerjson.put(DSRAppConstant.KEY_DUMMY_KEY, mCursor.getEvent_key());
                    innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), "");
                } else {
                    innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), mCursor.getEvent_key());
                    innerjson.put(DSRAppConstant.KEY_DUMMY_KEY, "");
                }
                // json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), mCursor.getString(columnIndexKey));
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name(), mCursor.getEvent_user_key());

                    innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type.name(), mCursor.getEvent_type());

                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title.name(), mCursor.getEvent_title());
                json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name(), mCursor.getEvent_date());
                json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.entered_on.name(), mCursor.getEntered_on());
                json.put(B2CAppConstant.KEY_CUST_KEY, mCursor.getCus_key());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name(), mCursor.getFrom_date_time());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name(), mCursor.getTo_date_time());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description.name(), mCursor.getEvent_description());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), mCursor.getStatus());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name(), mCursor.getCmkey());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name(), mCursor.getArea());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name(), mCursor.getLatitude());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name(), mCursor.getLongitude());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude.name(), mCursor.getAltitude());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time.name(), mCursor.getVisit_update_time());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response.name(), mCursor.getAction_response());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name(), mCursor.getPlan());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name(), mCursor.getObjective());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name(), mCursor.getStrategy());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), mCursor.getCustomer_name());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation.name(), mCursor.getPreparation());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude.name(), mCursor.getEvent_visited_latitude());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude.name(), mCursor.getEvent_visited_longitude());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude.name(), mCursor.getEvent_visited_altitude());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on.name(), mCursor.getCompleted_on());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on.name(), mCursor.getCancelled_on());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.anticipate.name(), mCursor.getAnticipate());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.mins_of_meet.name(), mCursor.getMinutes_of_meet());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.competition_pricing.name(), mCursor.getCompetition_pricing());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.feedback.name(), mCursor.getFeedback());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.order_taken.name(), mCursor.getOrder_taken());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.product_display.name(), mCursor.getProduct_display());
                innerjson.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.promotion_activated.name(), mCursor.getPromotion_activated());

                Logger.LogError("mCursor.getString(columnIndexCMKey)", "" + mCursor.getCmkey());

                tJsonArray.put(innerjson);

            }

             json.put(B2CAppConstant.KEY_DATA, tJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    return json;
    }

    @Override
    public void handleResult(String method_name, JSONObject response) throws JSONException {
        onPostAddAllNewEvent(response);
    }

    @Override
    public void handleError(VolleyError e) {

    }
    private void onPostAddAllNewEvent(JSONObject outerresponseJson){
        try {
            JSONObject responseJson ;
            if (outerresponseJson.has(DSRAppConstant.KEY_STATUS) &&
                    (outerresponseJson.getInt(DSRAppConstant.KEY_STATUS) == 1)) {
                JSONArray responseArray = outerresponseJson.getJSONArray(B2CAppConstant.KEY_DATA);
                for (int i = 0; i < responseArray.length(); i++) {
                    responseJson = responseArray.getJSONObject(i);



                    B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateEventKey(
                            responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                            responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()),
                            responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));



                }

            }
        }catch (JSONException e) {
            e.printStackTrace();
            Logger.LogError("JSONException : ", e.toString());

        }
    }
}
