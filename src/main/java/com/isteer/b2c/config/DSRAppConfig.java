package com.isteer.b2c.config;

import android.app.AlarmManager;

public class DSRAppConfig {

	public static final int SPLASH_TIMEDELAY = 3*1000;
	public static final int MAX_LOGIN_ATTEMPT = 5;
	public static final long AUTO_LOGOUT_THRESHOLD = AlarmManager.INTERVAL_DAY;
	public static final long LOGIN_CHECKUP_TIME = 2*AlarmManager.INTERVAL_HOUR;
	
	public static final String PROJECT_NUMBER = "724244711644";
	public static final String API_KEY = "AIzaSyDDES9Jlg2_Mh6kjWs3eF8o3iaKX0iXZ-U";
	public static final long DEFAULT_ATTENDENCE_INTERVAL = 10*60*1000;
	
}
