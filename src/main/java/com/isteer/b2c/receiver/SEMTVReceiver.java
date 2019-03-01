package com.isteer.b2c.receiver;

import java.io.IOException;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.isteer.b2c.activity.B2CLancher.B2CLoginScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConfig;
import com.isteer.b2c.config.B2CAppConstant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class SEMTVReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.e("SEMTVReceiver", "onReceive");

		if(B2CApp.b2cPreference.isLoggedIn())
		{
			if(B2CApp.b2cUtils.isNetAvailable())
				new TokenValidatorTask().execute();
			else
				runOnFail();
		}
	}

	class TokenValidatorTask extends AsyncTask<String, String, String> {

		protected String doInBackground(String... urls) {

			String postUrl = B2CApp.b2cPreference.getBaseUrl()
					+ B2CAppConstant.METHOD_VALIDATE_TOKEN + B2CApp.b2cPreference.getUserId() + "/"
					+ B2CApp.b2cPreference.getToken() ;
			Log.e("postUrl : ", postUrl);

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(postUrl);
			try {

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

			super.onPreExecute();
		}

		protected void onPostExecute(String responseString) {

			Log.e("responseString : ", "" + responseString);

			if (responseString == null) {

				runOnFail();
				return;
			}

			JSONObject responseJson;

			try {
				responseJson = new JSONObject(responseString);

				if (responseJson.has(B2CAppConstant.KEY_RESULT) && responseJson.getBoolean(B2CAppConstant.KEY_RESULT)) {
					B2CApp.b2cPreference.setLastValidatedTime(new Date().getTime());
					return;
				}

			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("JSONException : ", e.toString());

			}

			runOnFail();
		}

	}

	private void runOnFail() {
		
	/*	if((new Date().getTime()-B2CApp.b2cPreference.getLastValidatedTime()) > B2CAppConfig.AUTO_LOGOUT_THRESHOLD)
			B2CLoginScreen.logoutApp();*/
	}
	
}