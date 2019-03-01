package com.isteer.b2c.asynctask;

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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.app.DSRApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.utility.Logger;

public class DSRSyncToServer extends AsyncTask<String, String, String> {

	private static final int OPTYPE_UNKNOWN = -1;
	private static final int OPTYPE_ADDALLNEWDATA = 61;
	private static final int OPTYPE_UPDATEALLDATA = 62;

	private int operationType;
	private String uriInProgress;

	private Context mContext;
	private static ProgressDialog pdialog;
	private static String PROGRESS_MSG = "Syncing data to server...";

	public static boolean isSuccess = true;
	
	public DSRSyncToServer(Context context) 
	{
		mContext = context;
	}
	
	protected String doInBackground(String... urls) {

		uriInProgress = urls[0];
		Log.e("postUrl : ", uriInProgress);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(uriInProgress);
		try {

			operationType = getOperationType(uriInProgress);

			String jsonString = "";

			try {

				jsonString = (new JSONObject().put(DSRAppConstant.KEY_VALUES,
						getAllEventsArray(operationType))).toString();

				Log.e("jsonString : ", jsonString);

			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSONException : ", e.toString());
			}

			StringEntity se = new StringEntity(jsonString);
			httppost.setEntity(se);
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");

			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			return httpclient.execute(httppost, responseHandler);

		} catch (ClientProtocolException e) {
			Log.e("ClientProtocolException : ", e.toString());
		} catch (IOException e) {
			Log.e("IOException : ", e.toString());
		}

		return null;
	}

	@Override
	protected void onPreExecute() {

		updateProgress(PROGRESS_MSG);

	}

	protected void onPostExecute(String responseString) {

		Log.e("responseString : ", "" + responseString);

		if (responseString == null) {

			if(operationType==OPTYPE_ADDALLNEWDATA)
			{
				isSuccess = false;
				return;
			}
			else
			{
				dismissProgress();
				alertUserP(mContext, "Failed", "Operation failed in a timely manner. Please try again.", "OK");
			}
		}
		else
		{
			if(operationType==OPTYPE_ADDALLNEWDATA)
				onPostExecuteAddAll(responseString);
			else if(operationType==OPTYPE_UPDATEALLDATA)
				onPostExecuteUpdateAll(responseString);
		}

	}

	protected void onPostExecuteAddAll(String responseString) {

		JSONObject responseJson;
		JSONArray responseArray = null;
		try {
			responseArray = new JSONArray(responseString);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSONException : ", e.toString());
			isSuccess = false;
			return;
		}

		for(int i=0; i<responseArray.length(); i++)
		{
			try {
				responseJson = responseArray.getJSONObject(i);
	
				if (responseJson.has(DSRAppConstant.KEY_STATUS)&& responseJson.getString(DSRAppConstant.KEY_STATUS).equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS)) {

					B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateEventKey(responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),
							responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()),
							responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));
				//	DSRApp.dsrLdbs.updateEventKey(mContext,responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY),responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()),responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));

					int dummy_key = Integer.valueOf(responseJson.getString(DSRAppConstant.KEY_DUMMY_KEY));
					int event_key = Integer.valueOf(responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()));
					Logger.LogError("dummy_key",""+dummy_key);
					Logger.LogError("event_key",""+event_key);
					if (B2CApp.b2cPreference.getCHECKEDINEVENTKEY() == dummy_key) {
						B2CApp.b2cPreference.setCHECKEDINEVENTKEY(event_key);
					}


				}

			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSONException : ", e.toString());
			}
		}
	}
	
	protected void onPostExecuteUpdateAll(String responseString) {
		
		JSONObject responseJson;
		JSONArray responseArray = null;
		try {
			responseArray = new JSONArray(responseString);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSONException : ", e.toString());
			dismissProgress();
			alertUserP(mContext, "Failed", "Operation failed in a timely manner. Please try again.", "OK");
			return;
		}

		for(int i=0; i<responseArray.length(); i++)
		{
			try {
				responseJson = responseArray.getJSONObject(i);
			
				if (responseJson.has(DSRAppConstant.KEY_STATUS) && responseJson.getString(DSRAppConstant.KEY_STATUS).equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS))
				{
					B2CApp.getINSTANCE().getRoomDB().eventdata_dao().updateisSyncedToserver(responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()), responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()));
					// DSRApp.dsrLdbs.updateAsSynced(mContext, responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()), responseJson.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name()));
				}
			
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSONException : ", e.toString());
			}
		}
		
		dismissProgress();
		
		if(isSuccess == false)
			alertUserP(mContext, "Failed", "Operation failed in a timely manner. Please try again.", "OK");
		else
			alertUserP(mContext, "Success", "Synced to server successfully.", "OK");
		
	//	DSRApp.dsrLdbs.fetchSelected(mContext, DSRTableCreate.TABLE_AERP_EVENT_MASTER, null, DSRTableCreate.COLOUMNS_EVENT_MASTER.is_synced_to_server.name() + " = " + "0", null, null);

	//	DSRApp.dsrLdbs.fetchSelected(mContext, DSRTableCreate.TABLE_AERP_EVENT_MASTER, null, DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name() + " < "+ "0", null, null);

	}
	
	private int getOperationType(String tUri) {

		int operation = OPTYPE_UNKNOWN;

		if (tUri.contains(DSRAppConstant.METHOD_ADDALL_NEW_EVENTS))
			operation = OPTYPE_ADDALLNEWDATA;
		else if (tUri.contains(DSRAppConstant.METHOD_UPDATE_ALL_DATA))
			operation = OPTYPE_UPDATEALLDATA;
		else
			operation = OPTYPE_UNKNOWN;

		return operation;
	}

	private void updateProgress(String message) {
		if (pdialog != null && pdialog.isShowing())
			pdialog.setMessage(message);
		else
			pdialog = ProgressDialog.show(mContext, "", message, true);
	}

	private void dismissProgress() {
		if (pdialog != null && pdialog.isShowing())
			pdialog.dismiss();
	}
	
	private void alertUserP(Context context, String title, String msg, String btn) {
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
	
	private JSONArray getAllEventsArray(int optype) throws JSONException
	{
		
		JSONArray tJsonArray = new JSONArray();
		
		Cursor mCursor;
		
		/*if(optype==OPTYPE_UPDATEALLDATA)
			mCursor = DSRApp.dsrLdbs.fetchSelected(mContext, DSRTableCreate.TABLE_AERP_EVENT_MASTER, null, DSRTableCreate.COLOUMNS_EVENT_MASTER.is_synced_to_server.name() + " = " + "0", null, null);
		else
			mCursor = DSRApp.dsrLdbs.fetchSelected(mContext, DSRTableCreate.TABLE_AERP_EVENT_MASTER, null, DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name() + " < "+ "0", null, null);*/
		ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB()
				.eventdata_dao().fetchnonSynedData();

		for(int i =0;i<eventDataArrayList.size();i++){
			EventData eventData = eventDataArrayList.get(i);
			
			JSONObject json = new JSONObject();
if (eventData.getEvent_key() < 0){
	json.put(DSRAppConstant.KEY_DUMMY_KEY, eventData.getEvent_key());
	json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), "");
}else {
	json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(), eventData.getEvent_key());
	json.put(DSRAppConstant.KEY_DUMMY_KEY, "");
}

			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key.name(), eventData.getEvent_user_key());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type.name(), eventData.getEvent_type());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title.name(), eventData.getEvent_title());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name(), eventData.getEvent_date());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.entered_on.name(), eventData.getEntered_on());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name(), eventData.getFrom_date_time());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name(), eventData.getTo_date_time());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description.name(), eventData.getEvent_description());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), eventData.getStatus());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name(), eventData.getCmkey());
			json.put(B2CAppConstant.KEY_CUST_KEY, eventData.getCus_key());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name(), eventData.getArea());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name(), eventData.getLatitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name(), eventData.getLongitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude.name(), eventData.getAltitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time.name(), eventData.getVisit_update_time());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response.name(), eventData.getAction_response());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name(), eventData.getPlan());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name(), eventData.getObjective());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name(), eventData.getStrategy());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), eventData.getCustomer_name());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation.name(), eventData.getPreparation());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude.name(), eventData.getEvent_visited_latitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude.name(), eventData.getEvent_visited_longitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude.name(), eventData.getEvent_visited_altitude());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on.name(), eventData.getCompleted_on());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on.name(), eventData.getCancelled_on());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.anticipate.name(), eventData.getAnticipate());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.mins_of_meet.name(), eventData.getMinutes_of_meet());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.competition_pricing.name(), eventData.getCompetition_pricing());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.feedback.name(), eventData.getFeedback());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.order_taken.name(), eventData.getOrder_taken());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.product_display.name(), eventData.getProduct_display());
			json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.promotion_activated.name(), eventData.getPromotion_activated());
			
			tJsonArray.put(json);
			
		}
		

		
		return tJsonArray;
	}
	
}