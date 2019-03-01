package com.isteer.b2c.activity.B2CLancher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.isteer.b2c.activity.action.B2CCollectionEntryScreen;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.counter_details.B2CLocateScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.R;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.Logger;

public class B2CLoginScreen extends AppCompatActivity {
    //  Hs^J2f1x

	private Handler handler = new Handler();

	private LinearLayout btnLogin;
	private String TAG = "ISDLoginScreen";
	private Spinner spinnerBranch;

	EditText login_name,login_pass;
	String user_name, password;

	private TextView header_title;

	private static ProgressDialog pdialog;
	private static String PROGRESS_MSG = "Signing in...";
	
	private TextView txt_Login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.scr_sem_login);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		initVar();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(!B2CApp.b2cPreference.isRegistered())
			gotoB2CSplashScreen();
	}

	@Override
	protected void onPause() {
		
		super.onPause();

	}

	@Override
	public void onBackPressed() {

		goBack();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void goBack() {

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);		
	}
	
	private void initVar() {
		findViewById(R.id.img_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logoutApp();
				gotoB2CSplashScreen();
			}
		});
		findViewById(R.id.img_home).setVisibility(View.INVISIBLE);
		header_title = (TextView) findViewById(R.id.header_title);
		header_title.setText("Login");
		
		login_name = (EditText) findViewById(R.id.login_name);
		login_pass = (EditText) findViewById(R.id.login_pass);
		login_pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				if (i == EditorInfo.IME_ACTION_DONE){
					loginToMain();
					return true;
				}
				return false;
			}
		});
		((View) findViewById(R.id.btn_header_right)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				gotoURL("http://www.getisteer.com/");
			}
		});
		
		addListenerOnClick();

		((View) findViewById(R.id.btn_header_left))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						onBackPressed();
					}
				});
		
		final ArrayList<String> brCodes = new ArrayList<String>(B2CApp.b2cPreference.getBRCodes());
		final ArrayList<String> brNames = new ArrayList<String>(B2CApp.b2cPreference.getBRNames());
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, brNames);
		/*spinnerBranch = ((Spinner) findViewById(R.id.spinnerBranch));
		spinnerBranch.setAdapter(adapter);
		spinnerBranch.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {

				B2CApp.b2cPreference.setBranchCode(brCodes.get(pos));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				B2CApp.b2cPreference.setBranchCode("");
			}
		});*/
		
	/*	if(brNames.size()>0)
			spinnerBranch.setVisibility(View.INVISIBLE);
		else
		{
		//	spinnerBranch.setVisibility(View.GONE);
			B2CApp.b2cPreference.setBranchCode("");
		}*/

	}
	
	private void addListenerOnClick() {

		btnLogin = (LinearLayout) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			loginToMain();
			}
		});

		txt_Login = (TextView) findViewById(R.id.txt_Login);
		txt_Login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			loginToMain();
			}
		});
	}

	private void loginToMain() {
		AlertPopupDialog.dialogDismiss();
		user_name = login_name.getText().toString();

		password = login_pass.getText().toString();
		String message = this.getResources().getString(R.string.nointernetdescription);
		 if(B2CApp.b2cUtils.isNetAvailable()) {

		 	loginClick();
		 	//alertUserP(B2CLoginScreen.this, "Connection Error", "No Internet connection available", "OK");
		 }else {
			 AlertPopupDialog.noInternetAlert(this,message, new AlertPopupDialog.myOnClickListenerRight() {
				 @Override
				 public void onButtonClickRight() {
					 if(B2CApp.b2cUtils.isNetAvailable()) {
					 	AlertPopupDialog.dialogDismiss();
					 	loginClick();
					 }
				 }
			 });
		 }
	}

	private void loginClick() {
		if (user_name.length() < 3)
			login_name.setError("Enter a valid name");
			//alertUserP(B2CLoginScreen.this, "Error", "Enter a valid name", "OK");

		else if (password.length() < 3)

			login_pass.setError("Enter a valid password");
			//alertUserP(B2CLoginScreen.this, "Error", "Enter a valid password", "OK");
		else if(B2CApp.b2cPreference.getBaseUrl()!=null)
			new AuthenticateTask().execute();
		else
		{
			clearEntries();
			AlertDialog.Builder builder = new AlertDialog.Builder(
					B2CLoginScreen.this);
			builder.setMessage(
					"Application is not registered yet. Please register the app first")
					.setTitle("Error")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									dialog.cancel();
									gotoB2CSplashScreen();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	class AuthenticateTask extends AsyncTask<String, String, String> {

		protected String doInBackground(String... urls) {

			String postUrl = B2CApp.b2cPreference.getBaseUrl2() + B2CAppConstant.METHOD_LOGIN;
			Logger.LogError("postUrl : ", postUrl);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(postUrl);
			try {
				
				JSONObject json = new JSONObject();

				try {
					json.put("user_name", user_name);
					json.put("password", password);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				String jsonString = json.toString();
				
				Logger.LogError("jsonString : ", jsonString);

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

			B2CApp.b2cUtils.updateMaterialProgress(B2CLoginScreen.this,PROGRESS_MSG);

		}

		protected void onPostExecute(String responseString) {

			Logger.LogError("responseString : ", "" + responseString);

			B2CApp.b2cUtils.dismissMaterialProgress();
			
			String message = "Authentication failed in a timely manner. Please try again";
			
			if (responseString == null) {

				runOnFail(message);
				return;
			}

			String sekey = null;
			String unit_key = null;
			String user_id = null;
			String Token = null;

			JSONObject responseOuterJson, responseJson;

			try {
				responseOuterJson = new JSONObject(responseString);

				if (responseOuterJson.has(DSRAppConstant.KEY_STATUS) && responseOuterJson.getString(DSRAppConstant.KEY_STATUS).equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS))
				{
					if(responseOuterJson.has(DSRAppConstant.KEY_DATA))
					{
						responseJson = responseOuterJson.getJSONObject(DSRAppConstant.KEY_DATA);
						
						if(responseJson.has(DSRAppConstant.KEY_USER_ID))
							user_id = responseJson.getString(DSRAppConstant.KEY_USER_ID);
	
						if (responseJson.has(DSRAppConstant.KEY_SE_KEY))
							sekey = responseJson.getString(DSRAppConstant.KEY_SE_KEY);
		
						if (responseJson.has(DSRAppConstant.KEY_UNIT_KEY))
							unit_key = responseJson.getString(DSRAppConstant.KEY_UNIT_KEY);
			
						if (responseJson.has(DSRAppConstant.KEY_TOKEN))
							Token = responseJson.getString(DSRAppConstant.KEY_TOKEN);
						
/*						if (responseJson.has(B2CAppConstant.KEY_CUST_ID))
							cus_key = responseJson.getString(B2CAppConstant.KEY_CUST_ID);*/
						
						B2CApp.b2cPreference.setIsLoggedIn(true);
						B2CApp.b2cPreference.setIsDaystoppedAuto(false);
						B2CApp.b2cPreference.setUserId(user_id);
						B2CApp.b2cPreference.setUnitKey(unit_key);
						B2CApp.b2cPreference.setSekey(sekey);
						B2CApp.b2cPreference.setToken(Token);
						B2CApp.b2cPreference.setIsTokenValid(true);
						B2CApp.b2cPreference.setLastValidatedTime(new Date().getTime());

						B2CApp.b2cPreference.setUserName(user_name);
						B2CApp.b2cPreference.setUserPass(password);
						B2CApp.b2cPreference.setIsLoginFailed(false);
						B2CApp.b2cPreference.setLoginFailCount(0);
						
						clearEntries();
						B2CCountersScreen.toRefresh = true;
						/*if(B2CApp.b2cPreference.isDbFilled())
							gotoB2CCountersScreen();
						else*/
							gotoB2CSyncScreen();
						return;
					}
				}
				
				if (responseOuterJson.has(B2CAppConstant.KEY_MSG))
					message = responseOuterJson.getString(B2CAppConstant.KEY_MSG);
				
			} catch (JSONException e) {
				e.printStackTrace();
				Logger.LogError("JSONException : ", e.toString());
				
			}
			
			runOnFail(message);
		}
		
		private void runOnFail(String message)
		{
			B2CApp.b2cPreference.setIsLoginFailed(true);
			int loginFailCount = B2CApp.b2cPreference.getLoginFailCount()+1;
			if(loginFailCount>=B2CApp.b2cPreference.getMaxLoginAtt())
			{
				//logoutApp();
				gotoB2CSplashScreen();
			}
			else
			{
				B2CApp.b2cPreference.setLoginFailCount(loginFailCount);
				alertUserP(B2CLoginScreen.this, "Failed", message, "OK");
			}
		}
		
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

	private boolean validateEmail(String email1) {
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return email1.matches(EMAIL_REGEX);
	}

	private void clearEntries() {
		login_name.setText("");
		login_pass.setText("");
		B2CApp.b2cPreference.setLastIndexFilled(0);
	}

	public static void logoutApp() {
		
		B2CApp.b2cPreference.setBaseUrl(null);
		B2CApp.b2cPreference.setIsLoggedIn(false);
		B2CApp.b2cPreference.setIsRegistered(false);
		B2CApp.b2cPreference.setSekey(null);
		B2CApp.b2cPreference.setToken(null);
		B2CApp.b2cPreference.setUnitKey(null);
		B2CApp.b2cPreference.setUserId(null);
		B2CApp.b2cPreference.setUserName(null);
		B2CApp.b2cPreference.setUserPass(null);
		B2CApp.b2cPreference.setLastValidatedTime(01);
		B2CApp.b2cPreference.setBranchCode("");
		B2CApp.b2cPreference.setBRNames(new HashSet<String>());
		B2CApp.b2cPreference.setBRCodes(new HashSet<String>());
	}
	
	public static boolean isSessionValid() {
		
		if(B2CApp.b2cPreference.isRegistered() && B2CApp.b2cPreference.isLoggedIn() && B2CApp.b2cPreference.isTokenValid())
			return true;
		else
			return false;
	}

	private void gotoB2CSyncScreen() {

		startActivity(new Intent(this, B2CSyncScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}
	
	private void gotoB2CSplashScreen() {

		startActivity(new Intent(this, B2CSplashScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	private void gotoURL(String weblink)
	{
		Intent lLink = new Intent(Intent.ACTION_VIEW, Uri.parse(weblink));
		startActivity(lLink);
	}
	
	private void gotoB2CCountersScreen() {

		startActivity(new Intent(this, B2CCountersScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}


	
}
