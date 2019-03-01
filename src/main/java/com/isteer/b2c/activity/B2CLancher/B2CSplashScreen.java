package com.isteer.b2c.activity.B2CLancher;

import java.io.IOException;
import java.util.HashSet;

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
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConfig;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.Logger;

public class B2CSplashScreen extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    private Handler handler = new Handler();
    private String companyCode;

    private View ll_register;
    private LinearLayout btnRegister;
    private TextView txt_Register;
    private String TAG = "B2CSplashScreen";

    private static ProgressDialog pdialog;
    private static String PROGRESS_MSG = "Loading...";
    private int ASK_MULTPLE_PERMISSION = 101;
    private ImageView splashimage;


    private EditText et_company_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scr_b2c_splash);
        initVar();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //	B2CApp.isDbCreated();

        hideStatusBar();
        ll_register.setVisibility(View.INVISIBLE);
		
		/*final Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clockwise);
		final AnimationSet s = new AnimationSet(true);
		s.addAnimation(animation1);
		splashimage.startAnimation(s);*/

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Logger.LogError("B2CApp.b2cPreference.isRegistered()", "" + B2CApp.b2cPreference.isRegistered());
                Logger.LogError("B2CApp.b2cPreference.isLoggedIn()", "" + B2CApp.b2cPreference.isLoggedIn());
                Logger.LogError("B2CApp.b2cPreference.getUserId()", "" + B2CApp.b2cPreference.getUserId());
                if (!B2CApp.b2cPreference.isRegistered()) {

                    if (!check_permission()) {
                        askPermission();
                    } else {
                        ll_register.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (B2CApp.b2cPreference.isLoggedIn() && B2CApp.b2cPreference.getUserId() != null) {
                        if (B2CApp.b2cPreference.isDbFilled())
                            gotoB2CNewMainFragment();
                        else
                            gotoB2CSyncScreen();

                    } else

                        gotoB2CLoginScreen();
                }
            }
        }, B2CAppConfig.SPLASH_TIMEDELAY);
    }

    private void initVar() {

        ll_register = (View) findViewById(R.id.ll_register);
        et_company_name = (EditText) findViewById(R.id.et_company_name);
        splashimage = (ImageView) findViewById(R.id.splah_img);

        btnRegister = (LinearLayout) findViewById(R.id.btnRegister);
        txt_Register = (TextView) findViewById(R.id.txt_Register);
        txt_Register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                siteLogin();
            }
        });
        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                siteLogin();


            }
        });
        et_company_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    siteLogin();
                    return true;
                }

                return false;
            }
        });
    }

    public void siteLogin() {
        AlertPopupDialog.dialogDismiss();
        companyCode = et_company_name.getText().toString();
        Log.e("companyCode", companyCode);
        String message = this.getResources().getString(R.string.nointernetdescription);
        if (!B2CApp.b2cUtils.isNetAvailable()) {
            AlertPopupDialog.noInternetAlert(this, message, new AlertPopupDialog.myOnClickListenerRight() {
                @Override
                public void onButtonClickRight() {
                    if (B2CApp.b2cUtils.isNetAvailable()) {
                        AlertPopupDialog.dialogDismiss();
                        if (companyCode.length() < 1)
                            et_company_name.setError("Enter a valid name");
                            //alertUserP(B2CSplashScreen.this, "Error", "Enter a valid name", "OK");

                            //alertUserP(B2CSplashScreen.this, "Connection Error", "No Internet connection available", "OK");
                        else {

                            // clearEntries();
                            new AuthenticateTask().execute();
                        }
                    }
                }
            });
        } else if (companyCode.length() < 1)
            et_company_name.setError("Enter a valid name");
            //alertUserP(B2CSplashScreen.this, "Error", "Enter a valid name", "OK");

            //alertUserP(B2CSplashScreen.this, "Connection Error", "No Internet connection available", "OK");
        else {

            // clearEntries();
            new AuthenticateTask().execute();

        }
    }

    /*	public void checkAllPermission() {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == 0 ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == 0 ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == 0 ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECORD_AUDIO) == 0 ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_PHONE_STATE) == 0 ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.VIBRATE) == 0 ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_CONTACTS) == 0 ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == 0) {
                askPermission();
            } else {

            }


        }


        public void askPermission() {


            ActivityCompat.requestPermissions(B2CSplashScreen.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.VIBRATE,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.INTERNET,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    ASK_MULTPLE_PERMISSION);

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == ASK_MULTPLE_PERMISSION) {

                boolean coarseLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean fineLocation = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean recordAudio = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                boolean readPhoneState = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                boolean vibrate = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                boolean writeContact = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalStroage = grantResults[7] == PackageManager.PERMISSION_GRANTED;

            }
        }
        */
    class AuthenticateTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {

            String postUrl = B2CAppConstant.URL_REGISTER;
            Logger.LogError("postUrl : ", postUrl);

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(postUrl);
            try {

                JSONObject json = new JSONObject();
                json.put("sitename", companyCode);
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            B2CApp.b2cUtils.updateMaterialProgress(B2CSplashScreen.this, PROGRESS_MSG);

        }

        protected void onPostExecute(String responseString) {

            Logger.LogError("responseString : ", "" + responseString);

            B2CApp.b2cUtils.dismissMaterialProgress();

            if (responseString == null) {

                alertUserP(B2CSplashScreen.this, "Failed", "Registration failed in a timely manner. Please try again", "OK");
                return;
            }

            String success = null;
            String site1 = null, site2 = null, site3 = null, site4 = null, site5 = null;
            long LoginCheckTime = 0, MaxLoginTime = 0, AttendanceTrackingTime = 0, AlarmTime = 0;
            int MaxLoginAttempt = 0;

            JSONObject responseJson;

            try {
                responseJson = new JSONObject(responseString);

                if (responseJson.has(B2CAppConstant.KEY_SUCCESS))
                    success = responseJson
                            .getString(B2CAppConstant.KEY_SUCCESS);

                if (success != null && success.equalsIgnoreCase(B2CAppConstant.KEY_SUCCESS)) {

                    if (responseJson.has(B2CAppConstant.KEY_SITE1))
                        site1 = responseJson
                                .getString(B2CAppConstant.KEY_SITE1);

                    if (responseJson.has(B2CAppConstant.KEY_SITE2))
                        site2 = responseJson
                                .getString(B2CAppConstant.KEY_SITE2);

                    if (responseJson.has(B2CAppConstant.KEY_SITE3))
                        site3 = responseJson
                                .getString(B2CAppConstant.KEY_SITE3);

                    if (responseJson.has(B2CAppConstant.KEY_SITE4))
                        site4 = responseJson
                                .getString(B2CAppConstant.KEY_SITE4);

                    if (responseJson.has(B2CAppConstant.KEY_SITE5))
                        site5 = responseJson
                                .getString(B2CAppConstant.KEY_SITE5);

                    if (responseJson.has(B2CAppConstant.KEY_LOGIN_CHECK_TIME))
                        LoginCheckTime = responseJson
                                .getLong(B2CAppConstant.KEY_LOGIN_CHECK_TIME);

                    if (responseJson.has(B2CAppConstant.KEY_MAX_LOGIN_TIME))
                        MaxLoginTime = responseJson
                                .getLong(B2CAppConstant.KEY_MAX_LOGIN_TIME);

                    if (responseJson.has(B2CAppConstant.KEY_MAX_LOGIN_ATTEMPT))
                        MaxLoginAttempt = responseJson
                                .getInt(B2CAppConstant.KEY_MAX_LOGIN_ATTEMPT);

                    if (responseJson.has(B2CAppConstant.KEY_ATT_TIME))
                        AttendanceTrackingTime = responseJson
                                .getLong(B2CAppConstant.KEY_ATT_TIME);

                    if (responseJson.has(B2CAppConstant.KEY_ALARM_TIME))
                        AlarmTime = responseJson
                                .getLong(B2CAppConstant.KEY_ALARM_TIME);

                    HashSet<String> brCodes = new HashSet<String>();
                    HashSet<String> brNames = new HashSet<String>();

                    if (responseJson.has(B2CAppConstant.KEY_BRANCHES)) {
                        JSONArray branches = responseJson
                                .getJSONArray(B2CAppConstant.KEY_BRANCHES);

                        for (int i = 0; i < branches.length(); i++) {
                            JSONObject branch = branches.getJSONObject(i);
                            if (branch.has(B2CAppConstant.KEY_BRANCH_CODE) && branch.has(B2CAppConstant.KEY_BRANCH_NAME)) {
                                brCodes.add(branch.getString(B2CAppConstant.KEY_BRANCH_CODE));
                                brNames.add(branch.getString(B2CAppConstant.KEY_BRANCH_NAME));
                            }
                        }
                    }

                    clearEntries();
                    B2CApp.b2cPreference.setIsRegistered(true);
                    B2CApp.b2cPreference.setCompanyCode(companyCode);
                    B2CApp.b2cPreference.setBaseUrl(site1);
                    B2CApp.b2cPreference.setBaseUrl2(site2);
                    B2CApp.b2cPreference.setBaseUrl3(site3);
                    B2CApp.b2cPreference.setBaseUrl4(site4);
                    B2CApp.b2cPreference.setBaseUrl5(site5);

                    B2CApp.b2cPreference.setLoginCheckTime(LoginCheckTime);
                    B2CApp.b2cPreference.setMaxLoginTime(MaxLoginTime);
                    B2CApp.b2cPreference.setMaxLoginAtt(MaxLoginAttempt);
                    B2CApp.b2cPreference.setAttTrackTime(AttendanceTrackingTime);
                    B2CApp.b2cPreference.setAlarmTime(AlarmTime);

                    B2CApp.b2cPreference.setBRCodes(brCodes);
                    B2CApp.b2cPreference.setBRNames(brNames);

                    gotoB2CLoginScreen();
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Logger.LogError("JSONException : ", e.toString());
            }
            alertUserP(B2CSplashScreen.this, "Failed", "Registration failed in a timely manner. Please try again", "OK");
        }
    }

    private void clearEntries() {
        et_company_name.setText("");
    }

    @Override
    public void onBackPressed() {

        handler.removeCallbacksAndMessages(null);

        goBack();
    }

    private void goBack() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();

            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            ActionBar actionBar = getActionBar();
            if (actionBar != null)
                actionBar.hide();
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

    private void gotoB2CLoginScreen() {

        startActivity(new Intent(this, B2CLoginScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CSyncScreen() {

        startActivity(new Intent(this, B2CSyncScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CNewMainFragment() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private Boolean check_permission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            return false;
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            return false;
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            return false;
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else
            return true;
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }
}
