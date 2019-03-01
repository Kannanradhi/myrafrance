package com.isteer.b2c.app;

import java.util.Date;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.isteer.b2c.config.DSRAppConfig;
import com.isteer.b2c.receiver.DSRTVReceiver;

public class DSRApp extends Application {

	public static final String TAG = "DSRApp";
	

    @Override
	public void onCreate() {
		
		super.onCreate();
		

		initTokenValidator();
	}




	private void initTokenValidator()
	{
		Log.e("initTokenValidator", "called");

		AlarmManager am = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(this.getApplicationContext(), DSRTVReceiver.class);
	    PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), 0, i,  0);
	    am.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime()+10*1000, DSRAppConfig.LOGIN_CHECKUP_TIME, pi);

	}
	
}
