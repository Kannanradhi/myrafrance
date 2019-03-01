package com.isteer.b2c.receiver;

import java.io.IOException;
import java.util.ArrayList;

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.B2CTableCreate;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.AttendenceData;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.utility.Logger;

public class B2CConnectivityReceiver extends BroadcastReceiver {

    public Context mContext;

    public static boolean isSyncSuccess, isSyncInProgress;
    public static boolean addIsUptodate = false;
    public static boolean updateIsUptodate = false;
    private static final int OPTYPE_UPDATEALLDATA = 54;
    public int currentSync = DSRAppConstant.SYNC_TYPE_NOTHING;

    @Override
    public void onReceive(Context context, Intent intent) {

         Logger.LogError("B2CConnectivityReceiver", "onReceive");

        mContext = context;
        if (B2CApp.b2cUtils.isNetAvailable()) {
            syncDataToServer();

        }

    }


    private void syncDataToServer() {

        //	Toast.makeText(mContext, "B2C Auto-Sync - starts", Toast.LENGTH_SHORT).show();
        Logger.LogError("isSyncInProgress", "" + isSyncInProgress);
        Logger.LogError("B2CApp.b2cPreference.isUpdatedCollections()", "" + B2CApp.b2cPreference.isUpdatedCollections());
        Logger.LogError("B2CApp.b2cPreference.isFilledCustomers()", "" + B2CApp.b2cPreference.isFilledCustomers());
        if (!isSyncInProgress && B2CApp.b2cPreference.isFilledCustomers()) {

            if (!B2CApp.b2cPreference.isUpdatedCollections())
                startSyncNewCollections();
            else {
                //	Toast.makeText(mContext, "B2C Auto-Sync - Collections are uptodate", Toast.LENGTH_SHORT).show();


                ArrayList<PendingOrderData> pendingOrderDataArrayList = (ArrayList<PendingOrderData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().fetchCustomSelection();
					/*boolean isOrdersUptodate = (B2CApp.b2cLdbs.fetchCustomSelection(mContext,B2CTableCreate.TABLE_B2C_PENDING_ORDERS,
									null,B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_item_key.name()+ " < "+ "0"+ " OR "
									+ B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0") == null);*/

                if (pendingOrderDataArrayList.size() > 0) {
                    updatePendingOrdersToServer();
                }else {
                    startSyncAddAllNewEvent();
                }
                //	Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders are uptodate", Toast.LENGTH_SHORT).show();

                //	Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders are uptodate", Toast.LENGTH_SHORT).show();
            }
        }else
            B2CApp.b2cPreference.setFULLYSYNCEDTOSERVER(true);
    }

    public void syncAttnToServer() {
        ArrayList<AttendenceData> attendenceDataList = (ArrayList<AttendenceData>) B2CApp.getINSTANCE().getRoomDB().attendence_dao().getAsyncronizedAttendence();
        Logger.LogError("attendenceDataList", "" + attendenceDataList.size());

        if (attendenceDataList.size() > 0) {


            if (B2CApp.b2cUtils.isNetAvailable()) {

                new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl() + DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG);
            }
        }else {
            syncLocationToServer();
        }
    }

    public void syncLocationToServer() {
        Long locationCount = B2CApp.getINSTANCE().getRoomDB().locationData_dao().getLocationCount();

        Logger.LogError("locationCount", "" + locationCount);
        if (locationCount > 0) {
            new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl() + DSRAppConstant.METHOD_UPDATE_LOCATION_LOG);
        }else {
            B2CApp.b2cPreference.setFULLYSYNCEDTOSERVER(true);
        }
    }

    private void startSyncNewCollections() {
        ArrayList<CollectionData> collectionDataArrayList = (ArrayList<CollectionData>) B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().fetchCustomSelection();

		/*boolean isCollectionsUptodate = (B2CApp.b2cLdbs.fetchCustomSelection(mContext, B2CTableCreate.TABLE_B2C_COLLECTIONS, null, B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name() + " < "+ "0"
									+ " OR " + B2CTableCreate.COLOUMNS_COLLECTIONS.is_synced_to_server.name() + " = " + "0")==null);*/

        if (collectionDataArrayList.size() > 0) {
            syncAllCollectionsToServer();
        } else
            startSyncPendingOrders();
    }

    private void syncAllCollectionsToServer() {
        isSyncInProgress = true;
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_INSERT_ALL_COLLECTION);
    }

    private void syncAllCuctomerLocationToServer() {
        ArrayList<CustomerData> customerDataArrayList = (ArrayList<CustomerData>) B2CApp.getINSTANCE().getRoomDB().customerData_dao().fetchAllCustomerLocation();

		/*	Cursor mCursor = B2CApp.b2cLdbs.fetchCustomSelection(mContext, B2CTableCreate.TABLE_B2C_COLLECTIONS, null, B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name() + " < "+ "0"
					+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0");*/

        if (customerDataArrayList.size() > 0) {

            new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl2() + DSRAppConstant.METHOD_UPDATE_CUST_LOC);
        }
    }

    private void updatePendingOrdersToServer() {
        isSyncInProgress = true;
        new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl()
                + B2CAppConstant.METHOD_UPDATE_ALL_ORDERS);
    }

    private void startSyncPendingOrders() {

        if (!B2CApp.b2cPreference.isUpdatedPendingOrders()) {
            ArrayList<PendingOrderData> pendingOrderDataArrayList = (ArrayList<PendingOrderData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().fetchCustomSelection();

			/*boolean isOrdersUptodate = (B2CApp.b2cLdbs.fetchCustomSelection(mContext, B2CTableCreate.TABLE_B2C_PENDING_ORDERS, null, B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_item_key.name() + " < "+ "0"
										+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0")==null);*/

            if (pendingOrderDataArrayList.size() > 0) {
                updatePendingOrdersToServer();
            } else {
                startSyncAddAllNewEvent();
                //	Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders are uptodate", Toast.LENGTH_SHORT).show();
                isSyncInProgress = false;
            }
        } else {
            startSyncAddAllNewEvent();
            //	Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders are uptodate", Toast.LENGTH_SHORT).show();
            isSyncInProgress = false;
        }
    }

    private void startSyncAddAllNewEvent() {


        ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchnonSynedData();

			/*boolean isOrdersUptodate = (B2CApp.b2cLdbs.fetchCustomSelection(mContext, B2CTableCreate.TABLE_B2C_PENDING_ORDERS, null, B2CTableCreate.COLOUMNS_PENDING_ORDERS.so_item_key.name() + " < "+ "0"
										+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0")==null);*/
        Logger.LogError("eventDataArrayList", "" + eventDataArrayList.size());
        if (eventDataArrayList.size() > 0) {
            if (B2CApp.b2cUtils.isNetAvailable())
                new SyncManager().execute(B2CApp.b2cPreference.getBaseUrl2()
                        + DSRAppConstant.METHOD_ADDALL_NEW_EVENTS);
        }else {
            syncAttnToServer();
        }


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

        }

        protected void onPostExecute(String responseString) {

             Logger.LogError("responseString", " : " + responseString);

            if (responseString == null) {

                if (operationType == B2CAppConstant.OPTYPE_INSERT_ALL_COLLECTION) {
                    //	Toast.makeText(mContext, "B2C Auto-Sync - Collections Failed", Toast.LENGTH_SHORT).show();
                    startSyncPendingOrders();
                } else if (operationType == B2CAppConstant.OPTYPE_UPDATE_ORDERS) {
                    //	Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders Failed", Toast.LENGTH_SHORT).show();
                    startSyncAddAllNewEvent();
                } else if (operationType == OPTYPE_UPDATEALLDATA) {
                    syncAttnToServer();
                    //	Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders Failed", Toast.LENGTH_SHORT).show();
                } else if (operationType == B2CAppConstant.OPTYPE_ATTENDENCE_LOG) {
                    syncLocationToServer();
                } else if (operationType == B2CAppConstant.OPTYPE_UPDATE_LOCATION_LOG) {

                }

            } else {
                if (operationType == B2CAppConstant.OPTYPE_UPDATE_ORDERS)
                    onPostUpdateAllOrders(responseString);
                else if (operationType == B2CAppConstant.OPTYPE_INSERT_ALL_COLLECTION)
                    onPostInsertAllCollections(responseString);
                else if (operationType == OPTYPE_UPDATEALLDATA) {
                    onPostAddAllNewEvent(responseString);
                } else  if (operationType == B2CAppConstant.OPTYPE_UPDATE_LOCATION_LOG) {
                    onPostUPDATE_LOCATION_LOG(responseString);

                } else if (operationType == B2CAppConstant.OPTYPE_ATTENDENCE_LOG) {

                    onPostATTENDENCE_LOG(responseString);
                } else
                    isSyncInProgress = false;
            }
        }
    }

    private int getOperationType(String uri) {

        int optype = B2CAppConstant.OPTYPE_UNKNOWN;

        if (uri.contains(B2CAppConstant.METHOD_UPDATE_ALL_ORDERS))
            optype = B2CAppConstant.OPTYPE_UPDATE_ORDERS;
        else if (uri.contains(B2CAppConstant.METHOD_INSERT_ALL_COLLECTION))
            optype = B2CAppConstant.OPTYPE_INSERT_ALL_COLLECTION;
        else if (uri.contains(DSRAppConstant.METHOD_ADDALL_NEW_EVENTS))
            optype = OPTYPE_UPDATEALLDATA;
        else if (uri.contains(DSRAppConstant.METHOD_UPDATE_ATTENDENCE_LOG))
            optype = B2CAppConstant.OPTYPE_ATTENDENCE_LOG;
        else if (uri.contains(DSRAppConstant.METHOD_UPDATE_LOCATION_LOG))
            optype = B2CAppConstant.OPTYPE_UPDATE_LOCATION_LOG;

        return optype;

    }

    private JSONObject getJsonParams(int opType) throws JSONException {

        JSONObject json = new JSONObject();

        if (opType == B2CAppConstant.OPTYPE_UPDATE_ORDERS) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());
            json.put(B2CAppConstant.KEY_SE_KEY, B2CApp.b2cPreference.getSekey());
            json.put(B2CAppConstant.KEY_AUTH_TOKEN, B2CApp.b2cPreference.getToken());
            json.put(B2CAppConstant.KEY_SECURITY_TOKEN, "");

            JSONArray tJsonArray = new JSONArray();

            ArrayList<PendingOrderData> pendingOrderDataArrayList = (ArrayList<PendingOrderData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().fetchCustomSelection();

            if (pendingOrderDataArrayList.size() <= 0) {
                json.put(B2CAppConstant.KEY_DATA, tJsonArray);
                return json;
            }

            for (int i = 0; i < pendingOrderDataArrayList.size(); i++) {

                PendingOrderData pendingOrderData = pendingOrderDataArrayList.get(i);
                JSONObject tJson = new JSONObject();

                int getSo_key = pendingOrderData.getSo_key();
                if (getSo_key < 0) {
                    tJson.put(DSRAppConstant.KEY_DUMMY_KEY, "" + getSo_key);
                    tJson.put(B2CAppConstant.KEY_SO_ITEM_KEY, "");
                    tJson.put(B2CAppConstant.KEY_SO_KEY, "");
                } else {
                    tJson.put(DSRAppConstant.KEY_DUMMY_KEY, "");
                    tJson.put(B2CAppConstant.KEY_SO_ITEM_KEY, "" + pendingOrderData.getSo_item_key());
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

		/*	Cursor mCursor = B2CApp.b2cLdbs.fetchCustomSelection(mContext, B2CTableCreate.TABLE_B2C_COLLECTIONS, null, B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name() + " < "+ "0"
					+ " OR " + B2CTableCreate.COLOUMNS_PENDING_ORDERS.is_synced_to_server.name() + " = " + "0");*/

            if (collectionDataArrayList.size() <= 0) {
                json.put(B2CAppConstant.KEY_DATA, tJsonArray);
                return json;
            }

            for (int i = 0; i < collectionDataArrayList.size(); i++) {
                CollectionData collectionData = collectionDataArrayList.get(i);
                JSONObject tJson = new JSONObject();

                Integer pay_coll_key = collectionData.getPay_coll_key();
                Logger.LogError("pay_coll_key", "" + pay_coll_key);

                if (pay_coll_key < 0) {
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
        } else if (opType == OPTYPE_UPDATEALLDATA) {


            JSONArray tJsonArray = new JSONArray();

            ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchnonSynedDataToServer();

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
        } else if (opType == B2CAppConstant.OPTYPE_UPDATEALLCUSTOMERLOCATION) {
            json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());
            json.put(B2CAppConstant.KEY_UNIT_KEY, B2CApp.b2cPreference.getUnitKey());


            JSONArray tJsonArray = new JSONArray();
            ArrayList<CustomerData> customerDataArrayList = (ArrayList<CustomerData>) B2CApp.getINSTANCE().getRoomDB().customerData_dao().fetchAllCustomerLocation();


            if (customerDataArrayList.size() <= 0) {
                json.put(B2CAppConstant.KEY_DATA, tJsonArray);
                return json;
            }

            for (int i = 0; i < customerDataArrayList.size(); i++) {
                CustomerData customerData = customerDataArrayList.get(i);
                JSONObject tJson = new JSONObject();

                if (Double.parseDouble(customerData.getLatitude()) != 0 && Double.parseDouble(customerData.getLongitude()) != 0) {
                    tJson.put(B2CAppConstant.KEY_CMKEY, "" + customerData.getCmkey());
                    tJson.put(B2CAppConstant.KEY_LATITUDE, "" + customerData.getLatitude());
                    tJson.put(B2CAppConstant.KEY_LONGITUDE, "" + customerData.getLongitude());
                    tJson.put(B2CAppConstant.KEY_ALTITUDE, "" + 0.0);
                }

                tJsonArray.put(tJson);

            }


            json.put(B2CAppConstant.KEY_DATA, tJsonArray);

        } else if (opType == B2CAppConstant.OPTYPE_UPDATE_LOCATION_LOG) {
            JSONArray tJsonArray = new JSONArray();
            ArrayList<LocationData> locationDataArrayList = (ArrayList<LocationData>) B2CApp.getINSTANCE().getRoomDB().locationData_dao().getAsyncronizedLocationList();
            //Cursor mCursor = DSRApp.dsrLdbs.getAsyncronizedLocationList(mContext);
            Logger.LogError("locationDataArrayList", "" + locationDataArrayList.size());
            if (locationDataArrayList.size() <= 0) {
                return json;
            }


            for (int i = 0; i < locationDataArrayList.size(); i++) {
                JSONObject innerJson = new JSONObject();
                LocationData locationData = locationDataArrayList.get(i);
                innerJson.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.loc_key.name(), locationData.getLoc_key());
                innerJson.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.user_key.name(), locationData.getUser_key());
                innerJson.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.date_time.name(), locationData.getDate_time());
                innerJson.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.longitude.name(), locationData.getLongitude());
                innerJson.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.latitude.name(), locationData.getLatitude());
                innerJson.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.altitude.name(), locationData.getAltitude());
                innerJson.put(DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.battery_level.name(), locationData.getBattery_level());
                tJsonArray.put(innerJson);
                //  System.out.println("json string is" + json.toString());

            }
            json.put(B2CAppConstant.KEY_DATA, tJsonArray);

        } else if (opType == B2CAppConstant.OPTYPE_ATTENDENCE_LOG) {
            JSONArray tJsonArray = new JSONArray();
            ArrayList<AttendenceData> attendenceDataList = (ArrayList<AttendenceData>) B2CApp.getINSTANCE().getRoomDB().attendence_dao().getAsyncronizedAttendence();
            if (attendenceDataList.size() > 0) {
                for (int i = 0; i < attendenceDataList.size(); i++) {
                    AttendenceData attendenceData = attendenceDataList.get(i);
                    JSONObject innerJson = new JSONObject();
                    if (attendenceData.getAtt_key() < 0) {

                        innerJson.put(DSRAppConstant.KEY_DUMMY_KEY, attendenceData.getAtt_key());
                        innerJson.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.att_key.name(), "");
                    } else {
                        innerJson.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.att_key.name(), attendenceData.getAtt_key());
                        innerJson.put(DSRAppConstant.KEY_DUMMY_KEY, "");
                    }

                    innerJson.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.user_key.name(), attendenceData.getUser_key());
                    innerJson.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.date.name(), attendenceData.getDate());
                    innerJson.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.start_time.name(), attendenceData.getStart_time());
                    innerJson.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.stop_time.name(), attendenceData.getStop_time());
                    innerJson.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.status.name(), attendenceData.getStatus());
                    innerJson.put(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.update_status.name(), attendenceData.getUpdate_status());
                    innerJson.put(B2CAppConstant.start_latitude, attendenceData.getStart_latitude());
                    innerJson.put(B2CAppConstant.start_longitude, attendenceData.getStart_longitude());
                    innerJson.put(B2CAppConstant.stop_latitude, attendenceData.getStop_latitude());
                    innerJson.put(B2CAppConstant.stop_longitude, attendenceData.getStop_longitude());
                    System.out.println("json string is" + innerJson.toString());
                    tJsonArray.put(innerJson);
                }
                json.put(B2CAppConstant.KEY_DATA, tJsonArray);
            }
        }
        return json;
    }

    protected void onPostInsertAllCollections(String responseString) {

        JSONObject outerJson;
        JSONArray responseArray;
        JSONObject innerJson;
        try {
            outerJson = new JSONObject(responseString);
            if (outerJson.has(B2CAppConstant.KEY_STATUS) && outerJson.getInt(B2CAppConstant.KEY_STATUS) == 1
                    && outerJson.has(B2CAppConstant.KEY_DATA)) {


                responseArray = outerJson.getJSONArray(B2CAppConstant.KEY_DATA);
                for (int i = 0; i < responseArray.length(); i++) {


                    innerJson = responseArray.getJSONObject(i);
                    //Logger.LogError("innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY)", "" + innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY));
                    //Logger.LogError("innerJson.getString(DSRAppConstant.cus_key)", "" + innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.cus_key.name()));
                    //Logger.LogError("innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.KEY_DUMMY_KEY)", "" + innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name()));
                    if (innerJson.has(B2CAppConstant.KEY_STATUS) && innerJson.getInt(B2CAppConstant.KEY_STATUS) == 1) {
                        B2CApp.getINSTANCE().getRoomDB().collectiondata_dao().updateCollectionKey(
                                innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                                innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.cus_key.name()),
                                innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.sc_ledger_key.name()),
                                Integer.parseInt(innerJson.getString(B2CTableCreate.COLOUMNS_COLLECTIONS.pay_coll_key.name())),
                                1);

					/*B2CApp.b2cLdbs.updateCollectionKey(mContext, innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
							innerJson.getString(B2CAppConstant.KEY_CUST_KEY), innerJson.getString(B2CAppConstant.KEY_PAY_COLL_KEY),
							innerJson.getString(B2CAppConstant.KEY_SC_LEDGER_KEY));*/

                         Logger.LogError("is success : ", "true");
                    } else {
                        isSyncSuccess = false;
                         Logger.LogError("is success : ", "false");
                    }


                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
             Logger.LogError("JSONException insert collections : ", e.toString());
            isSyncSuccess = false;

            //	Toast.makeText(mContext, "B2C Auto-Sync - Collections Failed", Toast.LENGTH_SHORT).show();
            startSyncPendingOrders();
            return;
        }


        B2CApp.b2cPreference.setIsUpdatedCollections(true);
        //Toast.makeText(mContext, "B2C Auto-Sync - Collections success", Toast.LENGTH_SHORT).show();
        startSyncPendingOrders();
    }

    protected void onPostUpdateAllOrders(String responseString) {

        JSONObject outerJson;
        JSONArray responseArray = null;
        try {
            outerJson = new JSONObject(responseString);
            if (outerJson.has(B2CAppConstant.KEY_STATUS) && outerJson.getInt(B2CAppConstant.KEY_STATUS) == 1) {

                responseArray = outerJson.getJSONArray(B2CAppConstant.KEY_DATA);


                JSONObject innerJson;
                for (int i = 0; i < responseArray.length(); i++) {

                    innerJson = responseArray.getJSONObject(i);

                    if ((innerJson.has(B2CAppConstant.IS_SUCCESS) && innerJson.getInt(B2CAppConstant.IS_SUCCESS) == 1) ||
                            (innerJson.has(B2CAppConstant.KEY_STATUS) && innerJson.getInt(B2CAppConstant.KEY_STATUS) == 1)) {

                        Logger.LogError("resultJson.getString(B2CAppConstant.KEY_SO_ITEM_QTY)", "" + innerJson.getString(B2CAppConstant.KEY_SO_ITEM_QTY));
                        Logger.LogError("resultJson.getString(B2CAppConstant.KEY_CUST_KEY)", "" + innerJson.getString(B2CAppConstant.KEY_CUST_KEY));
                        Logger.LogError("resultJson.getString(B2CAppConstant.KEY_SO_KEY)", "" + innerJson.getString(B2CAppConstant.KEY_SO_KEY));
                        Logger.LogError("resultJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY)", "" + innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));
                        if (Integer.parseInt(innerJson.getString(B2CAppConstant.KEY_SO_ITEM_QTY)) == 0) {


                            B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().deletePendingOrderSoKeyData(innerJson.getString(B2CAppConstant.KEY_CUST_KEY),
                                    innerJson.getString(B2CAppConstant.KEY_SO_KEY), innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));
                        } else {


                            B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().updatePendingOrderSoItemKeyData(innerJson.getString(B2CAppConstant.KEY_CUST_KEY),
                                    innerJson.getString(B2CAppConstant.KEY_SO_KEY), innerJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY),
                                    innerJson.getString(DSRAppConstant.KEY_DUMMY_KEY));
                        }


				/*	B2CApp.b2cLdbs.updatePendingOrder(mContext, resultJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
							resultJson.getString(B2CAppConstant.KEY_CUST_KEY), resultJson.getString(B2CAppConstant.KEY_SO_KEY),
									resultJson.getString(B2CAppConstant.KEY_SO_ITEM_KEY));*/

                         Logger.LogError("is success : ", "true");
                    } else {
                        isSyncSuccess = false;
                         Logger.LogError("is success : ", "false");
                    }


                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
             Logger.LogError("JSONException UpdateAllOrders : ", e.toString());
            isSyncSuccess = false;

            //	Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders failed", Toast.LENGTH_SHORT).show();
            isSyncInProgress = false;
            return;
        }

        B2CApp.b2cPreference.setIsUpdatedPendingOrders(true);
        startSyncAddAllNewEvent();
        //Toast.makeText(mContext, "B2C Auto-Sync - Pending Orders success", Toast.LENGTH_SHORT).show();
        isSyncInProgress = false;

    }

    private void onPostAddAllNewEvent(String responseString) {
        try {
            JSONObject responseJson;
            JSONObject outerresponseJson;
            outerresponseJson = new JSONObject(responseString);
            if (outerresponseJson.has(DSRAppConstant.KEY_STATUS) &&
                    (outerresponseJson.getInt(DSRAppConstant.KEY_STATUS) == 1)) {
                JSONArray responseArray = outerresponseJson.getJSONArray(B2CAppConstant.KEY_DATA);
                for (int i = 0; i < responseArray.length(); i++) {
                    responseJson = responseArray.getJSONObject(i);


                    B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateEventKey(
                            responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
                            responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()),
                            responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));

                    int dummy_key = Integer.valueOf(responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY));
                    int event_key = Integer.valueOf(responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));
                    Logger.LogError("dummy_key",""+dummy_key);
                    Logger.LogError("event_key",""+event_key);
                    if (B2CApp.b2cPreference.getCHECKEDINEVENTKEY() == dummy_key) {
                        B2CApp.b2cPreference.setCHECKEDINEVENTKEY(event_key);
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.LogError("JSONException : ", e.toString());

        }
        syncAttnToServer();
    }
    private void onPostATTENDENCE_LOG(String outerResponseJson) {
        JSONObject responseJson;
        try {
            responseJson = new JSONObject(outerResponseJson);

            if (responseJson.has(DSRAppConstant.KEY_STATUS) && responseJson.getInt(DSRAppConstant.KEY_STATUS) == 1
                    && responseJson.has(B2CAppConstant.KEY_DATA)) {

                JSONArray keys = responseJson.getJSONArray(B2CAppConstant.KEY_DATA);
                for (int i = 0; i < keys.length(); i++) {

                    JSONObject singleEntry = keys.getJSONObject(i);
                    if (singleEntry.has(DSRAppConstant.KEY_STATUS) && (singleEntry.getInt(DSRAppConstant.KEY_STATUS) == 1)) {
                        if (!(singleEntry.getString(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.stop_time.name()).isEmpty())) {
                            if ((singleEntry.getString(DSRAppConstant.KEY_DUMMY_KEY).isEmpty())) {
                                B2CApp.getINSTANCE().getRoomDB().attendence_dao().delete_attendence_log(singleEntry.getString((DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.att_key.name())));
                            } else {
                                B2CApp.getINSTANCE().getRoomDB().attendence_dao().delete_attendence_log(singleEntry.getString(DSRAppConstant.KEY_DUMMY_KEY));
                            }

                        } else {
                            B2CApp.getINSTANCE().getRoomDB().attendence_dao().updateattendence_log(
                                    singleEntry.getString(DSRAppConstant.KEY_DUMMY_KEY),
                                    singleEntry.getString(DSRTableCreate.COLOUMNS_AERP_ATTENDENCE_LOG.att_key.name()));

                        }
                    }
                }

            } else {
                Toast.makeText(mContext, "" + responseJson.getString(DSRAppConstant.KEY_MSG), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        syncLocationToServer();
    }

    private void onPostUPDATE_LOCATION_LOG(String responseString) {
        JSONObject responseJson;
        try {
            responseJson = new JSONObject(responseString);

            if (responseJson.has(DSRAppConstant.KEY_STATUS) && responseJson.getInt(DSRAppConstant.KEY_STATUS) == 1) {

                JSONArray keys = responseJson.getJSONArray("data");
                for (int i = 0; i < keys.length(); i++) {
                    JSONObject singleEntry = keys.getJSONObject(i);
                    if (singleEntry.getInt(B2CAppConstant.KEY_STATUS) == 1) {
                        B2CApp.getINSTANCE().getRoomDB().locationData_dao().deleteLocationLogUpdateStatus1(singleEntry.getString((DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.loc_key.name())));
                    }
                    //	DSRApp.dsrLdbs.updateattendence_log(mContext, singleEntry.getString((DSRTableCreate.COLOUMNS_AERP_LOCATION_LOG.loc_key.name())));

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        B2CApp.b2cPreference.setFULLYSYNCEDTOSERVER(true);
    }

    private void onPostUpdateAllCustomerLoc(String responseString) {
        try {
            JSONObject innerResponseJson;
            JSONObject outerresponseJson;
            outerresponseJson = new JSONObject(responseString);
            if (outerresponseJson.has(DSRAppConstant.KEY_STATUS) &&
                    (outerresponseJson.getInt(DSRAppConstant.KEY_STATUS) == 1)) {
                JSONArray responseArray = outerresponseJson.getJSONArray(B2CAppConstant.KEY_DATA);
                for (int i = 0; i < responseArray.length(); i++) {
                    innerResponseJson = responseArray.getJSONObject(i);


                    B2CApp.getINSTANCE().getRoomDB().customerData_dao().updateCustomerIndSynctoserver(
                            innerResponseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name()));


                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.LogError("JSONException : ", e.toString());

        }
    }

    private JSONArray getAllEventsArray() throws JSONException {
        JSONArray tJsonArray = new JSONArray();

        ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchnonSynedData();

        for (int i = 0; i < eventDataArrayList.size(); i++) {
            EventData mCursor = eventDataArrayList.get(i);


            JSONObject json = new JSONObject();
            if (mCursor.getEvent_key() < 0) {
                json.put(DSRAppConstant.KEY_DUMMY_KEY, mCursor.getEvent_key());
                json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), "");
            } else {
                json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), mCursor.getEvent_key());
                json.put(DSRAppConstant.KEY_DUMMY_KEY, "");
            }
            // json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), mCursor.getString(columnIndexKey));
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name(), mCursor.getEvent_user_key());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type.name(), mCursor.getEvent_type());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title.name(), mCursor.getEvent_title());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name(), mCursor.getEvent_date());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.entered_on.name(), mCursor.getEntered_on());
            json.put(B2CAppConstant.KEY_CUST_KEY, mCursor.getCus_key());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name(), mCursor.getFrom_date_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name(), mCursor.getTo_date_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description.name(), mCursor.getEvent_description());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), mCursor.getStatus());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name(), mCursor.getCmkey());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name(), mCursor.getArea());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name(), mCursor.getLatitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name(), mCursor.getLongitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude.name(), mCursor.getAltitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time.name(), mCursor.getVisit_update_time());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response.name(), mCursor.getAction_response());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name(), mCursor.getPlan());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name(), mCursor.getObjective());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name(), mCursor.getStrategy());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), mCursor.getCustomer_name());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation.name(), mCursor.getPreparation());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude.name(), mCursor.getEvent_visited_latitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude.name(), mCursor.getEvent_visited_longitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude.name(), mCursor.getEvent_visited_altitude());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on.name(), mCursor.getCompleted_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on.name(), mCursor.getCancelled_on());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.anticipate.name(), mCursor.getAnticipate());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.mins_of_meet.name(), mCursor.getMinutes_of_meet());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.competition_pricing.name(), mCursor.getCompetition_pricing());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.feedback.name(), mCursor.getFeedback());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.order_taken.name(), mCursor.getOrder_taken());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.product_display.name(), mCursor.getProduct_display());
            json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.promotion_activated.name(), mCursor.getPromotion_activated());

            Logger.LogError("mCursor.getString(columnIndexCMKey)", "" + mCursor.getCmkey());

            tJsonArray.put(json);

        }
        return tJsonArray;
    }



}