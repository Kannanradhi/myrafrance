package com.isteer.b2c.activity.B2CLancher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.calender.DSRAddProgScreen;
import com.isteer.b2c.activity.calender.DSREditProgScreen;
import com.isteer.b2c.activity.calender.DSRMonthScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConfig;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.utility.ColorCodes;
import com.isteer.b2c.utility.B2CBasicUtils;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.utility.SimpleGestureFilter;
import com.isteer.b2c.utility.SimpleGestureFilter.SimpleGestureListener;
import com.isteer.b2c.R;

public class DSRDayScreen extends AppCompatActivity implements SimpleGestureListener,
		OnClickListener, OnLongClickListener,
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
	public static boolean isGPSAlertShown = false;
	public static final String TAG = "DSRDayScreen";

	private SimpleGestureFilter detector;

	private GestureDetectorCompat gDetector;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	String SENDER_ID = DSRAppConfig.PROJECT_NUMBER;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	String regid;

	private static String PROGRESS_MSG_SYNC = "Syncing data to server...";

	public ArrayList<HashMap<String, String>> eventPresentsInDay = new ArrayList<HashMap<String, String>>();

	private RelativeLayout am_line_events, am1_line_events, am2_line_events,
			am3_line_events, am4_line_events, am5_line_events, am6_line_events,
			am7_line_events, am8_line_events, am9_line_events,
			am10_line_events, am11_line_events, am12_line_events,
			pm1_line_events, pm2_line_events, pm3_line_events, pm4_line_events,
			pm5_line_events, pm6_line_events, pm7_line_events, pm8_line_events,
			pm9_line_events, pm10_line_events, pm11_line_events;

	private int[] currentmargins = new int[24];
	private boolean[] is_margin_not_for_first = new boolean[24];

	private ImageView prevMonth;
	private ImageView nextMonth;
	private TextView currentDate;

	private static ProgressDialog pdialog;
	private static String PROGRESS_MSG = "Refreshing...";
	public static boolean toUpdateFromMonth = false;

	private TextView header_title;
	private ImageView btn_header_left, btn_header_right, btn_header_counter;

	private B2CBasicUtils isdUtils;

	private Calendar calendarWithCurrentDate;
	private String startDate;
	private Context mContext;

	// int margin = 0;
	Animation animFlipInForeward;
	Animation animFlipOutForeward;
	Animation animFlipInBackward;
	Animation animFlipOutBackward;

	private static final String dateFormat = "yyyy-MM-dd";

	private String[] timevalues = { "00", "01", "02", "03", "04", "05", "06",
			"07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
			"18", "19", "20", "21", "22", "23" };

	/*
	 * private String[] timevalues = { "00:00 AM", "00:30 AM", "01:00 AM",
	 * "01:30 AM", "02:00 AM", "02:30 AM", "03:00 AM", "03:30 AM", "04:00 AM",
	 * "04:30 AM", "05:00 AM", "05:30 AM", "06:00 AM", "06:30 AM", "07:00 AM",
	 * "07:30 AM", "08:00 AM", "08:30 AM", "09:00 AM", "09:30 AM", "10:00 AM",
	 * "10:30 AM", "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "13:00 PM",
	 * "13:30 PM", "14:00 PM", "14:30 PM", "15:00 PM", "15:30 PM", "16:00 PM",
	 * "16:30 PM", "17:00 PM", "17:30 PM", "18:00 PM", "18:30 PM", "19:00 PM",
	 * "19:30 PM", "20:00 PM", "20:30 PM", "21:00 PM", "21:30 PM", "22:00 PM",
	 * "22:30 PM", "23:00 PM", "23:30 PM" };
	 */

	private String months[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

	private String monthsNumbers[] = { "01", "02", "03", "04", "05", "06",
			"07", "08", "09", "10", "11", "12" };
	// String eventdate;
	String title;

	public static boolean isSyncSuccess = false;
	public static boolean addIsUptodate = false;
	public static boolean updateIsUptodate = false;
	private int toTimeAbsolute;
	private int timeDifference;
	private Typeface raleway;

	public DSRDayScreen()
	{

	}

	public DSRDayScreen(String startDate) {
		this.startDate = startDate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.scr_isr_dayview);
		raleway = Typeface.createFromAsset(this.getAssets(),"font/raleway.ttf");
		initVar();

/*		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(this);

			Logger.LogError("Registration id : ", "" + regid);

			if (regid.isEmpty()) {
				registerInBackground();
			}

		} else {
			Logger.LogError(TAG, "No valid Google Play Services APK found.");
		}*/

	}

	@Override
	protected void onResume() {

		super.onResume();

		if (toUpdateFromMonth) {
			toUpdateFromMonth = false;
			calendarWithCurrentDate.set(DSRMonthScreen.current_year,
					DSRMonthScreen.current_month - 1,
					DSRMonthScreen.current_day);
		}

		currentDate.setText(getDateForTitle());
		queryEventsOfDay();

		//sendRegistrationIdToBackend();

	}

	private void initVar() {


		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.img_home).setOnClickListener(this);

		header_title = (TextView) findViewById(R.id.header_title);
		header_title.setText("Visit Plan");

		btn_header_left = (ImageView) findViewById(R.id.btn_header_left);
		btn_header_left.setVisibility(View.VISIBLE);
		btn_header_left.setOnClickListener(this);

		btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
		btn_header_right.setVisibility(View.VISIBLE);
		btn_header_right.setImageResource(R.drawable.calendar_icon);
		btn_header_right.setOnClickListener(this);

		((View) findViewById(R.id.bottombar_one)).setOnClickListener(this);
		((View) findViewById(R.id.bottombar_two)).setOnClickListener(this);
		((View) findViewById(R.id.bottombar_three)).setOnClickListener(this);
		((View) findViewById(R.id.bottombar_four)).setOnClickListener(this);
		((View) findViewById(R.id.bottombar_five)).setOnClickListener(this);

		// ((View)
		// findViewById(R.id.dayview_wrapper)).setOnGestureListener(this);

		prevMonth = (ImageView) findViewById(R.id.prevDay);
		nextMonth = (ImageView) findViewById(R.id.nextDay);
		currentDate = (TextView) findViewById(R.id.currentDate);
		mContext = this.getApplicationContext();

		isdUtils = new B2CBasicUtils(this);

		calendarWithCurrentDate = Calendar.getInstance(Locale.getDefault());
		// calendarWithCurrentDate.set(2014, 10, 25);

		if (startDate == null) {
			// startDate = yearSelected + "/" + (month + 1) + "/" + day;
			startDate = (DSRMonthScreen.current_month + 1) + "/"
					+ DSRMonthScreen.current_day + "/"
					+ DSRMonthScreen.current_year;
		}
		/*
		 * Date date = new Date(startDate); startDate = (String)
		 * dateformatter.format(dateformat, date); eventdate = (String)
		 * dateformatter.format("MM/dd/yyyy", date); date = new Date(startDate);
		 * cal.setTime(date);
		 */

		currentDate.setText(getDateForTitle());

		// loadDataForDay();

		// scrollView.setOnTouchListener(gestureListener);
		prevMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				onLTRFling();
			}
		});

		nextMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onRTLFling();
			}

		});

/*		if (!B2CApp.b2cPreference.isDbFilled())
			refreshDB();*/

		initEventWrappper();

		gDetector = new GestureDetectorCompat(this, this);
		gDetector.setOnDoubleTapListener(this);

		detector = new SimpleGestureFilter(this, this);

	}

	private void initEventWrappper() {

		am_line_events = (RelativeLayout) findViewById(R.id.am_line_events);
		am1_line_events = (RelativeLayout) findViewById(R.id.am1_line_events);
		am2_line_events = (RelativeLayout) findViewById(R.id.am2_line_events);
		am3_line_events = (RelativeLayout) findViewById(R.id.am3_line_events);
		am4_line_events = (RelativeLayout) findViewById(R.id.am4_line_events);
		am5_line_events = (RelativeLayout) findViewById(R.id.am5_line_events);
		am6_line_events = (RelativeLayout) findViewById(R.id.am6_line_events);
		am7_line_events = (RelativeLayout) findViewById(R.id.am7_line_events);
		am8_line_events = (RelativeLayout) findViewById(R.id.am8_line_events);
		am9_line_events = (RelativeLayout) findViewById(R.id.am9_line_events);
		am10_line_events = (RelativeLayout) findViewById(R.id.am10_line_events);
		am11_line_events = (RelativeLayout) findViewById(R.id.am11_line_events);
		am12_line_events = (RelativeLayout) findViewById(R.id.am12_line_events);

		pm1_line_events = (RelativeLayout) findViewById(R.id.pm1_line_events);
		pm2_line_events = (RelativeLayout) findViewById(R.id.pm2_line_events);
		pm3_line_events = (RelativeLayout) findViewById(R.id.pm3_line_events);
		pm4_line_events = (RelativeLayout) findViewById(R.id.pm4_line_events);
		pm5_line_events = (RelativeLayout) findViewById(R.id.pm5_line_events);
		pm6_line_events = (RelativeLayout) findViewById(R.id.pm6_line_events);
		pm7_line_events = (RelativeLayout) findViewById(R.id.pm7_line_events);
		pm8_line_events = (RelativeLayout) findViewById(R.id.pm8_line_events);
		pm9_line_events = (RelativeLayout) findViewById(R.id.pm9_line_events);
		pm10_line_events = (RelativeLayout) findViewById(R.id.pm10_line_events);
		pm11_line_events = (RelativeLayout) findViewById(R.id.pm11_line_events);

		((View) findViewById(R.id.am_line_events)).setOnLongClickListener(this);
		((View) findViewById(R.id.am1_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am2_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am3_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am4_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am5_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am6_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am7_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am8_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am9_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am10_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am11_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.am12_line_events))
				.setOnLongClickListener(this);

		((View) findViewById(R.id.pm1_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm2_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm3_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm4_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm5_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm6_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm7_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm8_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm9_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm10_line_events))
				.setOnLongClickListener(this);
		((View) findViewById(R.id.pm11_line_events))
				.setOnLongClickListener(this);

	}

	private void clearEventWrappers() {
		setStartColor();
		am_line_events.removeAllViews();
		am1_line_events.removeAllViews();
		am2_line_events.removeAllViews();
		am3_line_events.removeAllViews();
		am4_line_events.removeAllViews();
		am5_line_events.removeAllViews();
		am6_line_events.removeAllViews();
		am7_line_events.removeAllViews();
		am8_line_events.removeAllViews();
		am9_line_events.removeAllViews();
		am10_line_events.removeAllViews();
		am11_line_events.removeAllViews();
		am12_line_events.removeAllViews();

		pm1_line_events.removeAllViews();
		pm2_line_events.removeAllViews();
		pm3_line_events.removeAllViews();
		pm4_line_events.removeAllViews();
		pm5_line_events.removeAllViews();
		pm6_line_events.removeAllViews();
		pm7_line_events.removeAllViews();
		pm8_line_events.removeAllViews();
		pm9_line_events.removeAllViews();
		pm10_line_events.removeAllViews();
		pm11_line_events.removeAllViews();

	}

	private void queryEventsOfDay() {

		currentmargins = new int[24];
		is_margin_not_for_first = new boolean[24];
		eventPresentsInDay.clear();
		clearEventWrappers();

		/*Cursor mCursor = DSRApp.dsrLdbs.fetchSelected(this,
				DSRTableCreate.TABLE_AERP_EVENT_MASTER, null,
				DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name(),
				DSRLocalDBStorage.SELECTION_OPERATION_LIKE,
				new String[] { getDateForQuery() });*/

		ArrayList<EventData> eventDataArrayList =(ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().getEventsOfDay(getDateForQuery());
Logger.LogError("eventDataArrayList",""+eventDataArrayList.size());
Logger.LogError("getDateForQuery()",""+getDateForQuery());
		if (eventDataArrayList.size() < 0 ) {
			Toast.makeText(this, "Events updation failed in a timely manner",
					Toast.LENGTH_LONG);
			return;
		}

		/*int columnIndexEvenKey = mCursor
				.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key
						.name());
		int columnIndexStatus = mCursor
				.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.status
						.name());
		int columnIndexFromTime = mCursor
				.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time
						.name());
		 int columnIndexToTime =
		 mCursor.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name());
		int columnIndexCustomerName = mCursor
				.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name
						.name());*/

		HashMap<String, String> tMap;


for (int i=0;i<eventDataArrayList.size();i++) {
	EventData eventData = eventDataArrayList.get(i);
	String tEKey = ""+eventData.getEvent_key();
	String tStatus =eventData.getStatus();
	String tTime1 = eventData.getFrom_date_time();
	String tTime2 = eventData.getTo_date_time();
	String tCustomerName = eventData.getCustomer_name();
Logger.LogError("eventData.getArea()()",""+eventData.getArea());
	tMap = new HashMap<String, String>();

	tMap.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(),
			tEKey);
	tMap.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(),
			tStatus);
	tMap.put(
			DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name(),
			tTime1);
	tMap.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name(),
			tTime2);
	tMap.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(),
			tCustomerName);

	eventPresentsInDay.add(tMap);

}

	//	mCursor.close();

		loadDataForDay();

	}

	private void loadDataForDay() {

		for (HashMap<String, String> appointment : eventPresentsInDay) {

			String event_key = appointment
					.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name());
			String customer_name = appointment
					.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name
							.name());
			String status = appointment
					.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name());
			String startTime = appointment
					.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time
							.name());
			String endTime = appointment
					.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time
							.name());
			// String endTime =
			// appointment.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name());

			createViewForAppointment(event_key, customer_name, status,
					startTime,endTime);
Logger.LogError("event_key",""+event_key);
		}
	}

	private void createViewForAppointment(final String ekey, String cname,
										  String tStatus, String startTime, String endTime) {
		Logger.LogError("endTime1111",""+endTime);

		int timeAbsolute = Integer.parseInt(startTime.substring(
				startTime.indexOf(" ") + 1, startTime.indexOf(" ") + 3));
		if (endTime != null && (!endTime.equalsIgnoreCase("") && (!endTime.isEmpty()))) {
			 toTimeAbsolute = Integer.parseInt(endTime.substring(
					endTime.indexOf(" ") + 1, endTime.indexOf(" ") + 3));
		 timeDifference = toTimeAbsolute - timeAbsolute;
		}else {
			timeDifference = 1;
		}
		int marginTop = calculateMargin(timeAbsolute);
Logger.LogError("timeAbsolute",""+timeAbsolute);
Logger.LogError("marginTop",""+marginTop);
Logger.LogError("timeDifference",""+timeDifference);


		LayoutParams lprams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		lprams.setMargins(30, marginTop, 0, 0);

		Button button = new Button(mContext);
		button.setBackgroundResource(R.color.transparent_background);
		button.setLayoutParams(lprams);
		button.setText("" + cname);
		button.setTextSize(16);
		button.setTypeface(raleway);
		button.setSingleLine();



		if (tStatus
				.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Pending.name()))
			button.setTextColor(getResources().getColor(R.color.darkblue));
		else if (tStatus
				.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.CheckIn.name()))
			button.setTextColor(getResources().getColor(R.color.checkin_color));
		else if (tStatus.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Completed
				.name()))
			button.setTextColor(Color.parseColor(ColorCodes.COLOR_DARK_GREEN));
		else if (tStatus.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Cancelled
				.name()))
			button.setTextColor(Color.GRAY);
		else if (tStatus.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Visited
				.name()))
			button.setTextColor(getResources().getColor(R.color.very_dark_yellow));

		/*
		 * if (height <= 18) { button.setSingleLine(); }
		 */

		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				DSREditProgScreen.toRefresh = true;
				DSREditProgScreen.backToDayScreen = true;
				DSREditProgScreen.eventKey = ekey;
				gotoDSREditProgScreen();
			}
		});

		addViewTo(timeAbsolute,timeDifference, button);

	}

	private long calculateDiffInTime(String startTime, String endTime) {

		String startTimeAppointment = startTime;
		String endTimeAppointment = endTime;
		long diffMinutes = 0;
		SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(startTimeAppointment);
			d2 = format.parse(endTimeAppointment);

			long diff = d2.getTime() - d1.getTime();
			diffMinutes = diff / (60 * 1000);
			float diffHours = diffMinutes / 60;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diffMinutes;
	}

	private int calculateMargin(int timeAbsolute) {

		// String timeAbsolute = startTime.substring(startTime.indexOf(" ")+1,
		// startTime.indexOf(" ")+3);
		// margin = Arrays.asList(timevalues).indexOf(timeAbsolute)*60;

		if (is_margin_not_for_first[timeAbsolute])
			currentmargins[timeAbsolute] += 70;
		else {
			is_margin_not_for_first[timeAbsolute] = true;
		}

		return currentmargins[timeAbsolute];
	}

	private void addViewTo(int timeAbsolute, int timeDifference, View tView) {

		if (timeDifference <= 0) {
			timeDifference = 1;

		}else if (timeAbsolute > 5){
			timeDifference = 1;
		}
for (int i =0; i<timeDifference;i++){
		switch (timeAbsolute +i) {

			case 0:
				if (i == 0) {
					am_line_events.addView(tView);
				}
				am_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 1:
				if (i == 0) {
					am1_line_events.addView(tView);
				}
				am1_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 2:
				if (i == 0) {
					am2_line_events.addView(tView);
				}
				am2_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 3:
				if (i == 0) {
					am3_line_events.addView(tView);
				}
				am3_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 4:
				if (i == 0) {
					am4_line_events.addView(tView);
				}
				am4_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;
			case 5:
				if (i == 0) {
					am5_line_events.addView(tView);
				}
				am5_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 6:
				if (i == 0) {
					am6_line_events.addView(tView);
				}
				am6_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 7:
				if (i == 0) {
					am7_line_events.addView(tView);
				}
				am7_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 8:

				if (i == 0) {
					am8_line_events.addView(tView);
				}
				am8_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 9:
				if (i == 0) {
					am9_line_events.addView(tView);
				}
				am9_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 10:
				if (i == 0) {

				}
				am10_line_events.addView(tView);
				am10_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));


				break;

			case 11:
				if (i == 0) {
					am11_line_events.addView(tView);
				}
				am11_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 12:
				if (i == 0) {
					am12_line_events.addView(tView);
				}
				am12_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 13:
				if (i == 0) {
					pm1_line_events.addView(tView);
				}
				pm1_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 14:
				if (i == 0) {
					pm2_line_events.addView(tView);
				}
				pm2_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 15:
				if (i == 0) {
					pm3_line_events.addView(tView);
				}
				pm3_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 16:
				if (i == 0) {
					pm4_line_events.addView(tView);
				}
				pm4_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 17:
				Logger.LogError("timeDifference8888",""+timeDifference);
				if (i == 0) {
					pm5_line_events.addView(tView);
				}
				pm5_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 18:
				if (i == 0) {
					pm6_line_events.addView(tView);
				}
				pm6_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 19:
				if (i == 0) {
					pm7_line_events.addView(tView);
				}
				pm7_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 20:
				if (i == 0) {
					pm8_line_events.addView(tView);
				}
				pm8_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 21:
				if (i == 0) {
					pm9_line_events.addView(tView);
				}
				pm9_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 22:
				if (i == 0) {
					pm10_line_events.addView(tView);
				}
				pm10_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			case 23:
				if (i == 0) {
					pm11_line_events.addView(tView);
				}
				pm11_line_events.setBackgroundColor(getResources().getColor(R.color.very_light_gray));
				break;

			default:
				break;
		}
		}

	}
	public void setStartColor() {
		am_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am1_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am2_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am3_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am4_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am5_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am6_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am7_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am8_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am9_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am10_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am11_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		am12_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm10_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm1_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm2_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm3_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm4_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm5_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm6_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm7_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm8_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm9_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm10_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
		pm11_line_events.setBackgroundColor(getResources().getColor(R.color.transparent_background));
	}
	private void setGridCellAdapterToDate(int month, int year)
			throws ParseException {

		loadDataForDay();

		currentDate.setText(getDateForTitle());

	}

	private void onLTRFling() {

		calendarWithCurrentDate.add(Calendar.DAY_OF_MONTH, -1);

		try {
			setGridCellAdapterToDate(calendarWithCurrentDate.MONTH,
					calendarWithCurrentDate.YEAR);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		queryEventsOfDay();
	}

	private void onRTLFling() {

		calendarWithCurrentDate.add(Calendar.DAY_OF_MONTH, 1);

		try {
			setGridCellAdapterToDate(calendarWithCurrentDate.MONTH,
					calendarWithCurrentDate.YEAR);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		queryEventsOfDay();
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

	@Override
	public void onBackPressed() {
		gotoDSRMenuScreen();
		//goBack();
	}

	@Override
	public void onClick(View pView) {

		switch (pView.getId()) {
			case R.id.img_back:
				finish();
				break;
			case R.id.img_home:
				gotoB2CMenuScreen();
				break;
		case R.id.btn_header_right:
			gotoDSRMonthScreen();
			break;

		case R.id.bottombar_one:
			gotoDSRMenuScreen();
			break;

		case R.id.bottombar_two:
			gotoDSRSyncScreen();
			break;

		case R.id.bottombar_three:
			gotoDSRCustListScreen();
			break;

		case R.id.bottombar_four:
			gotoDSRCountersScreen();
			break;

		case R.id.bottombar_five:
			gotoDSRPendingOrders();
			break;

		default:
			break;
		}
	}

	/*
	 * private void syncDataToServer() { addIsUptodate =
	 * (DSRApp.dsrLdbs.fetchCustomSelection(mContext,
	 * DSRTableCreate.TABLE_AERP_EVENT_MASTER, null,
	 * DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name() + " < "+
	 * "0")==null); updateIsUptodate =
	 * (DSRApp.dsrLdbs.fetchCustomSelection(mContext,
	 * DSRTableCreate.TABLE_AERP_EVENT_MASTER, null,
	 * DSRTableCreate.COLOUMNS_EVENT_MASTER.is_synced_to_server.name() + " = " +
	 * "0")==null);
	 *
	 * if(addIsUptodate && updateIsUptodate) alertUserP(this, "Alert",
	 * "Application Data are already upto date", "OK"); else if(!addIsUptodate)
	 * { isSyncSuccess = true; new
	 * DSRSyncToServer(DSRDayScreen.this).execute(isdPreference
	 * .getBaseUrl()+DSRAppConstant.METHOD_ADDALL_NEW_EVENTS); } else
	 * if(!updateIsUptodate) { isSyncSuccess = true; new
	 * DSRSyncToServer(DSRDayScreen
	 * .this).execute(isdPreference.getBaseUrl()+DSRAppConstant
	 * .METHOD_UPDATE_ALL_DATA); }
	 *
	 * }
	 */

	@Override
	public boolean onLongClick(View pView) {

		switch (pView.getId()) {

		case R.id.am_line_events:
			setTimeAndAdd("00:00", "01:00");
			break;

		case R.id.am1_line_events:
			setTimeAndAdd("01:00", "02:00");
			break;

		case R.id.am2_line_events:
			setTimeAndAdd("02:00", "03:00");
			break;

		case R.id.am3_line_events:
			setTimeAndAdd("03:00", "04:00");
			break;

		case R.id.am4_line_events:
			setTimeAndAdd("04:00", "05:00");
			break;

		case R.id.am5_line_events:
			setTimeAndAdd("05:00", "06:00");
			break;

		case R.id.am6_line_events:
			setTimeAndAdd("06:00", "07:00");
			break;

		case R.id.am7_line_events:
			setTimeAndAdd("07:00", "08:00");
			break;

		case R.id.am8_line_events:
			setTimeAndAdd("08:00", "09:00");
			break;

		case R.id.am9_line_events:
			setTimeAndAdd("09:00", "10:00");
			break;

		case R.id.am10_line_events:
			setTimeAndAdd("10:00", "11:00");
			break;

		case R.id.am11_line_events:
			setTimeAndAdd("11:00", "12:00");
			break;

		case R.id.am12_line_events:
			setTimeAndAdd("12:00", "13:00");
			break;

		case R.id.pm1_line_events:
			setTimeAndAdd("13:00", "14:00");
			break;

		case R.id.pm2_line_events:
			setTimeAndAdd("14:00", "15:00");
			break;

		case R.id.pm3_line_events:
			setTimeAndAdd("15:00", "16:00");
			break;

		case R.id.pm4_line_events:
			setTimeAndAdd("16:00", "17:00");
			break;

		case R.id.pm5_line_events:
			setTimeAndAdd("17:00", "18:00");
			break;

		case R.id.pm6_line_events:
			setTimeAndAdd("18:00", "19:00");
			break;

		case R.id.pm7_line_events:
			setTimeAndAdd("19:00", "20:00");
			break;

		case R.id.pm8_line_events:
			setTimeAndAdd("20:00", "21:00");
			break;

		case R.id.pm9_line_events:
			setTimeAndAdd("21:00", "22:00");
			break;

		case R.id.pm10_line_events:
			setTimeAndAdd("22:00", "23:00");
			break;

		case R.id.pm11_line_events:
			setTimeAndAdd("23:00", "00:00");
			break;

		}

		return false;
	}

	private void setTimeAndAdd(final String stime, final String etime) {

		AlertDialog.Builder builder = new AlertDialog.Builder(DSRDayScreen.this);
		builder.setMessage("Do you want to make a new call plan?")
				.setTitle("Alert!")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();

								DSREditProgScreen.currentEvent = new EventData();
								DSREditProgScreen.currentEvent
										.setFrom_date_time(DateFormat.format(
												dateFormat,
												calendarWithCurrentDate
														.getTime().getTime())
												+ " " + stime);
								DSREditProgScreen.currentEvent
										.setTo_date_time(DateFormat.format(
												dateFormat,
												calendarWithCurrentDate
														.getTime().getTime())
												+ " " + etime);

								DSRAddProgScreen.toRefresh = true;
								DSRAddProgScreen.backToDayScreen = true;
								gotoDSRAddProgScreen();

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private String getDateForTitle() {
		String dateFormatted = "";

		String month = months[calendarWithCurrentDate.get(Calendar.MONTH)];
		String date = " " + calendarWithCurrentDate.get(Calendar.DATE) + " ";
		String year = " " + calendarWithCurrentDate.get(Calendar.YEAR);

		dateFormatted = date + month + "," + year;

		return dateFormatted;
	}

	private String getDateForQuery() {

		String dateFormatted = ""
				+ DateFormat.format(B2CAppConstant.dateFormat2,
						calendarWithCurrentDate.getTime());

		Logger.LogError("dateFormatted", dateFormatted);

		return dateFormatted;
	}

/*	private void refreshDB() {
		clearDB();
		fillDB();
	}*/



/*	private void fillDB() {
		new PostRequestManager().execute(B2CApp.b2cPreference.getBaseUrl2()
				+ DSRAppConstant.METHOD_GETALLEVENTS);
	}*/

	private void goBack() {

		gotoDSRCountersScreen();
	}


	private void gotoDSRMonthScreen() {

		startActivity(new Intent(this, DSRMonthScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	private void gotoDSREditProgScreen() {

		startActivity(new Intent(this, DSREditProgScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	private void gotoDSRSyncScreen() {

		startActivity(new Intent(this, B2CSyncScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	private void gotoDSRAddProgScreen() {

		startActivity(new Intent(this, DSRAddProgScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	private void gotoDSRCustListScreen() {

		startActivity(new Intent(this, B2CCustSearchScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	private void gotoDSRPendingOrders() {

		startActivity(new Intent(this, B2CPendingOrderScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	private void gotoDSRCountersScreen() {

		startActivity(new Intent(this, B2CCountersScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	private void gotoDSRMenuScreen() {

		startActivity(new Intent(this, B2CNewMainActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}
	private void gotoB2CMenuScreen() {

		startActivity(new Intent(this, B2CNewMainActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
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

	@Override
	public boolean onFling(MotionEvent start, MotionEvent finish,
			float velocityX, float velocityY) {

		Logger.LogError("onFling", "onFling");

		if (start.getRawX() < finish.getRawX()) {

			Logger.LogError("onFling", "onRTLFling");
			onRTLFling();

		} else if (start.getRawX() > finish.getRawX()) {

			Logger.LogError("onFling", "onLTRFling");
			onLTRFling();

		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.gDetector.onTouchEvent(event);
		// Be sure to call the superclass implementation
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent event) {
		Logger.LogError("onDown: ", "" + event.toString());
		return true;
	}

	@Override
	public void onLongPress(MotionEvent event) {
		Logger.LogError("onLongPress: ", "" + event.toString());
	}

	@Override
	public boolean onScroll(MotionEvent event, MotionEvent e2, float distanceX,
			float distanceY) {
//		Logger.LogError("onScroll: ", "" + event.toString());
		return true;
	}

	@Override
	public void onShowPress(MotionEvent event) {
		Logger.LogError("onShowPress: ", "" + event.toString());
	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		Logger.LogError("onSingleTapUp: ", "" + event.toString());
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent event) {
		Logger.LogError("onDoubleTap: ", "" + event.toString());
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		Logger.LogError("onDoubleTapEvent: ", "" + event.toString());
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		Logger.LogError("onSingleTapConfirmed: ", "" + event.toString());
		return true;
	}

	@Override
	public void onDoubleTap() {
		Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		// Call onTouchEvent of SimpleGestureFilter class
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	 @Override
     public void onSwipe(int direction) {
      String str = "";

      switch (direction) {

      case SimpleGestureFilter.SWIPE_RIGHT :
    	  str = "Swipe Right";
    	  onLTRFling();
    	  break;
      case SimpleGestureFilter.SWIPE_LEFT :
    	  str = "Swipe Left";
    	  onRTLFling();
          break;
      case SimpleGestureFilter.SWIPE_DOWN :
    	  str = "Swipe Down";
          break;
      case SimpleGestureFilter.SWIPE_UP :
    	  str = "Swipe Up";
          break;

      }
      //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
     }
	 

/*	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Logger.LogError(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId(Context context) {

		String registrationId = B2CApp.b2cPreference.getGCMRegID();

		if (registrationId.isEmpty()) {
			Logger.LogError(TAG, "Registration id not found.");
			return "";
		}

		int registeredVersion = B2CApp.b2cPreference.getAppVersion();
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Logger.LogError(TAG, "App version changed.");
			return "";
		}

		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private void registerInBackground() {

		new GCMRegisterTask().execute();
	}

	class GCMRegisterTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(DSRDayScreen.this);
				}
				regid = gcm.register(SENDER_ID);
				msg = "Device registered, registration ID=" + regid;
				//sendRegistrationIdToBackend();
				storeRegistrationId(regid);

			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
			}
			return msg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Logger.LogError("onPreExecute : ", "onPreExecute");
		}

		@Override
		protected void onPostExecute(String msg) {
			super.onPostExecute(msg);

			Logger.LogError("onPostExecute", "responseString : " + msg);

			Toast.makeText(DSRDayScreen.this, msg, Toast.LENGTH_LONG).show();

		}

	}

	class PostRequestManager2 extends AsyncTask<String, String, String> {

		protected String doInBackground(String... params) {

			String postUrl = B2CApp.b2cPreference.getBaseUrl()
					+ DSRAppConstant.METHOD_PUT_GCMID;
			Logger.LogError("postUrl : ", postUrl);

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(postUrl);
			try {

				JSONObject json = new JSONObject();
				json.put(DSRAppConstant.KEY_COMPANY_CODE,
						B2CApp.b2cPreference.getCompanyCode());
				json.put(DSRAppConstant.KEY_UNIT_KEY,
						B2CApp.b2cPreference.getUnitKey());
				json.put(DSRAppConstant.KEY_USER_ID,
						B2CApp.b2cPreference.getUserId());
				json.put(DSRAppConstant.KEY_REG_ID, params[0]);
				json.put(
						DSRAppConstant.KEY_IMEI_NO,
						""
								+ ((TelephonyManager) DSRDayScreen.this
										.getSystemService(Context.TELEPHONY_SERVICE))
										.getDeviceId());
				json.put(
						DSRAppConstant.KEY_ANDROID_ID,
						""
								+ Secure.getString(
										DSRDayScreen.this.getContentResolver(),
										Secure.ANDROID_ID));
				json.put(DSRAppConstant.KEY_MODEL_NAME, "" + getDeviceName());

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
				e.printStackTrace();
			} catch (IOException e) {
				Logger.LogError("IOException : ", e.toString());
				e.printStackTrace();
			} catch (JSONException e) {
				Logger.LogError("JSONException : ", e.toString());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {

		}

		protected void onPostExecute(String responseString) {

			Logger.LogError("responseString : ", "" + responseString);

			if (responseString == null) {

				return;
			}

			JSONObject responseJson;

			
			 * try { responseJson = new JSONObject(responseString);
			 * 
			 * }
			 
		}
	}

	private void storeRegistrationId(String regId) {

		B2CApp.b2cPreference.setAppVersion(getAppVersion(this));
		B2CApp.b2cPreference.setGCMRegID(regId);

	}

	private void sendRegistrationIdToBackend() {

		new PostRequestManager2().execute(regid);
	}

	public String getDeviceName() {

		String manufacturer = Build.MANUFACTURER;
		String product = Build.PRODUCT;
		String model = Build.MODEL;

		if (model.startsWith(manufacturer))
			return model;
		else
			return manufacturer + " " + model;
	}
*/
	 

	 /*	class PostRequestManager extends AsyncTask<String, String, String> {

		private int opType = DSRAppConstant.OPTYPE_UNKNOWN;
		private String uriInProgress;

		protected String doInBackground(String... urls) {

			uriInProgress = urls[0];

			Logger.LogError("uriInProgress : ", uriInProgress);

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(uriInProgress);
			try {

				opType = getOperationType(uriInProgress);

				JSONObject json = getJsonParams(opType);
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
				e.printStackTrace();
			} catch (IOException e) {
				Logger.LogError("IOException : ", e.toString());
				e.printStackTrace();
			} catch (JSONException e) {
				Logger.LogError("JSONException : ", e.toString());
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {

			updateProgress(PROGRESS_MSG);

		}

		protected void onPostExecute(String responseString) {
			Logger.LogError("responseString : ", "" + responseString);

			if (responseString == null) {
				dismissProgress();
				alertUserP(
						DSRDayScreen.this,
						"Failed",
						"Operation failed in a timely manner. Please try again",
						"OK");
				return;
			} else if (opType == DSRAppConstant.OPTYPE_GETALLEVENTS)
				onPostGetAllEvents(responseString);
			else if (opType == DSRAppConstant.OPTYPE_GETCUSTOMERS)
				onPostCustomerDetails(responseString);
			else if (opType == DSRAppConstant.OPTYPE_GETSPANCOPLIST)
				onPostSpancoplist(responseString);
			else if (opType == DSRAppConstant.OPTYPE_GETMASTERITEM)
				onPostMasterItem(responseString);
			else if (opType == DSRAppConstant.OPTYPE_GETITEMMSTR)
				onPostItemMstr(responseString);
			else if (opType == DSRAppConstant.OPTYPE_GETPROSPECTMSTR)
				onPostProspectMstr(responseString);
			else if (opType == DSRAppConstant.OPTYPE_GETCUSTCATLIST)
				onPostCustCatList(responseString);
		}
	}

	private int getOperationType(String uri) {

		int optype = DSRAppConstant.OPTYPE_UNKNOWN;

		if (uri.contains(DSRAppConstant.METHOD_GETALLEVENTS))
			optype = DSRAppConstant.OPTYPE_GETALLEVENTS;
		else if (uri.contains(DSRAppConstant.METHOD_GET_CUSTOMER_DETAILS))
			optype = DSRAppConstant.OPTYPE_GETCUSTOMERS;
		else if (uri.contains(DSRAppConstant.METHOD_GET_SPANCOPLIST))
			optype = DSRAppConstant.OPTYPE_GETSPANCOPLIST;
		else if (uri.contains(DSRAppConstant.METHOD_GET_CUST_CAT_LIST))
			optype = DSRAppConstant.OPTYPE_GETCUSTCATLIST;
		else if (uri.contains(DSRAppConstant.METHOD_GET_AERP_MASTER_ITEM))
			optype = DSRAppConstant.OPTYPE_GETMASTERITEM;
		else if (uri.contains(DSRAppConstant.METHOD_GET_AERP_ITEM_MSTR))
			optype = DSRAppConstant.OPTYPE_GETITEMMSTR;
		else if (uri.contains(DSRAppConstant.METHOD_GET_AERP_PROSPECT_MSTR))
			optype = DSRAppConstant.OPTYPE_GETPROSPECTMSTR;

		return optype;

	}

	private JSONObject getJsonParams(int opType) throws JSONException {

		JSONObject json = new JSONObject();

		json.put(DSRAppConstant.KEY_USER_KEY, B2CApp.b2cPreference.getUserId());

		if (opType == DSRAppConstant.OPTYPE_GETALLEVENTS|| opType == DSRAppConstant.OPTYPE_GETCUSTCATLIST) {
		} else if (opType == DSRAppConstant.OPTYPE_GETCUSTOMERS) {
			json.put(DSRAppConstant.KEY_PUNIT_KEY, B2CApp.b2cPreference.getUnitKey());
		} else if (opType == DSRAppConstant.OPTYPE_GETSPANCOPLIST) {
			json.put(DSRAppConstant.KEY_CM_KEY, "");
		}

		return json;
	}

	protected void onPostGetAllEvents(String responseString) {

		String result = "false";
		String uid = null;
		String pid = null;
		String name = null;
		String message = "Operation failed in a timely manner. Please try again";

		JSONObject outerJson;

		try {
			outerJson = new JSONObject(responseString);

			if (outerJson.has(DSRAppConstant.KEY_STATUS)
					&& outerJson.getString(DSRAppConstant.KEY_STATUS)
							.equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS)) {
				JSONArray dataArray = outerJson
						.getJSONArray(DSRAppConstant.KEY_DATA);

				ArrayList<EventData> eventList = new ArrayList<EventData>();

				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject singleEvent = dataArray.getJSONObject(i);
					EventData eventData = new EventData();

					if (singleEvent
							.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key
									.name())) {
						eventData
								.setEvent_key(singleEvent
										.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key
												.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key
										.name()))
							eventData
									.setEvent_user_key(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type
										.name()))
							eventData
									.setEvent_type(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title
										.name()))
							eventData
									.setEvent_title(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.area
										.name()))
							eventData
									.setArea(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.area
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time
										.name())) {
							String timeString = singleEvent
									.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time
											.name());
							eventData.setFrom_date_time(timeString);
							eventData.setEvent_month(timeString.substring(0,
									timeString.indexOf('-', 5)));
							eventData.setEvent_date(timeString.substring(0,
									timeString.indexOf(" ")));
							eventData.setEvent_date_absolute(timeString
									.substring(8, timeString.indexOf(" ")));
						}

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time
										.name()))
							eventData
									.setTo_date_time(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description
										.name()))
							eventData
									.setEvent_description(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.status
										.name()))
							eventData
									.setStatus(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.status
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey
										.name()))
							eventData
									.setCmkey(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude
										.name()))
							eventData
									.setLatitude(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude
										.name()))
							eventData
									.setLongitude(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude
										.name()))
							eventData
									.setAltitude(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time
										.name()))
							eventData
									.setVisit_update_time(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response
										.name()))
							eventData
									.setAction_response(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.action
										.name()))
							eventData
									.setAnticipate(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.action
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan
										.name()))
							eventData
									.setPlan(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective
										.name()))
							eventData
									.setObjective(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy
										.name()))
							eventData
									.setStrategy(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name
										.name()))
							eventData
									.setCustomer_name(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation
										.name()))
							eventData
									.setPreparation(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude
										.name()))
							eventData
									.setEvent_visited_latitude(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude
										.name()))
							eventData
									.setEvent_visited_longitude(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude
										.name()))
							eventData
									.setEvent_visited_altitude(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on
										.name()))
							eventData
									.setCompleted_on(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on
										.name()))
							eventData
									.setCancelled_on(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on
													.name()));

						if (singleEvent
								.has(DSRTableCreate.COLOUMNS_EVENT_MASTER.entered_on
										.name()))
							eventData
									.setEntered_on(singleEvent
											.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.entered_on
													.name()));

						eventData.setIs_synced_to_server("1");

						eventList.add(eventData);

					}
				}

				B2CApp.b2cPreference.setIsDbFilled(DSRApp.dsrLdbs.insertEvents(this,
						eventList));
				new PostRequestManager().execute(B2CApp.b2cPreference.getBaseUrl2()
						+ DSRAppConstant.METHOD_GET_CUSTOMER_DETAILS);
				return;
			} else {
				if (outerJson.has(DSRAppConstant.KEY_MSG))
					message = outerJson.getString(DSRAppConstant.KEY_MSG);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Logger.LogError("Exception : ", e.toString());
		}

		dismissProgress();
		alertUserP(DSRDayScreen.this, "Failed", message, "OK");
	}

	protected void onPostCustomerDetails(String responseString) {

		JSONObject responseOuterJson;
		JSONArray dataArray;

		String message = "Operation failed in a timely manner. Please try again";

		try {
			responseOuterJson = new JSONObject(responseString);
			if (responseOuterJson.getString(DSRAppConstant.KEY_STATUS)
					.equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS)
					&& responseOuterJson.has(DSRAppConstant.KEY_DATA)) {
				dataArray = responseOuterJson
						.getJSONArray(DSRAppConstant.KEY_DATA);
				ArrayList<CustomerData> custList = new ArrayList<CustomerData>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject singleEntry = dataArray.getJSONObject(i);
					CustomerData custData = new CustomerData();
					if (singleEntry
							.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmkey
									.name())) {
						custData.setCmkey(singleEntry
								.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmkey
										.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.userkey
										.name()))
							custData.setUserkey(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.userkey
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmp_phone1
										.name()))
							custData.setCmp_phone1(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmp_phone1
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmp_phone2
										.name()))
							custData.setCmp_phone2(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmp_phone2
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmp_email
										.name()))
							custData.setCmp_email(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.cmp_email
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.company_name
										.name()))
							custData.setCompany_name(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.company_name
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address1
										.name()))
							custData.setAddress1(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address1
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address2
										.name()))
							custData.setAddress2(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address2
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address3
										.name()))
							custData.setAddress3(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.address3
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.area
										.name()))
							custData.setArea(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.area
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.city
										.name()))
							custData.setCity(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.city
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.state
										.name()))
							custData.setState(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.state
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.country
										.name()))
							custData.setCountry(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.country
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.pincode
										.name()))
							custData.setPincode(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.pincode
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.email
										.name()))
							custData.setEmail(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.email
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.phone1
										.name()))
							custData.setPhone1(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.phone1
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.phone2
										.name()))
							custData.setPhone2(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.phone2
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.website
										.name()))
							custData.setWebsite(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.website
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.industry
										.name()))
							custData.setIndustry(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.industry
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.category1
										.name()))
							custData.setCategory1(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.category1
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.category2
										.name()))
							custData.setCategory2(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.category2
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.category3
										.name()))
							custData.setCategory3(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.category3
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.display_code
										.name()))
							custData.setDisplay_code(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.display_code
											.name()));

						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CONTACT_MASTER.area_name
										.name()))
							custData.setArea_name(singleEntry
									.getString(DSRTableCreate.COLOUMNS_CONTACT_MASTER.area_name
											.name()));

						if (singleEntry
								.has(B2CTableCreate.COLOUMNS_CONTACT_MASTER.first_name
										.name()))
							custData.setFirst_name(singleEntry
									.getString(B2CTableCreate.COLOUMNS_CONTACT_MASTER.first_name
											.name()));
						
					}
					custList.add(custData);
				}

				B2CApp.b2cPreference.setIsDbFilled(B2CApp.b2cPreference.isDbFilled()
						&& B2CApp.b2cLdbs.insertCustomer(this, custList));
				new PostRequestManager().execute(B2CApp.b2cPreference.getBaseUrl2()
						+ DSRAppConstant.METHOD_GET_SPANCOPLIST);
				return;
			} else {
				if (responseOuterJson.has(DSRAppConstant.KEY_MSG))
					message = responseOuterJson
							.getString(DSRAppConstant.KEY_MSG);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			Logger.LogError("JSONException : ", e.toString());
		}

		dismissProgress();
		alertUserP(DSRDayScreen.this, "Failed", message, "OK");
	}

	protected void onPostSpancoplist(String responseString) {
		JSONObject responseOuterJson;
		JSONArray dataArray;
		String message = "Operation failed in a timely manner. Please try again";
		try {
			responseOuterJson = new JSONObject(responseString);
			if (responseOuterJson.getString(DSRAppConstant.KEY_STATUS)
					.equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS)
					&& responseOuterJson.has(DSRAppConstant.KEY_DATA)) {
				dataArray = responseOuterJson
						.getJSONArray(DSRAppConstant.KEY_DATA);
				ArrayList<SpancopData> spancopList = new ArrayList<SpancopData>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject singleEntry = dataArray.getJSONObject(i);
					SpancopData spancopData = new SpancopData();
					if (singleEntry
							.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.cp_key
									.name())) {
						spancopData
								.setCp_key(singleEntry
										.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.cp_key
												.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.cmkey
										.name()))
							spancopData
									.setCmkey(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.cmkey
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.sekey
										.name()))
							spancopData
									.setSekey(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.sekey
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.manu_code
										.name()))
							spancopData
									.setManu_code(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.manu_code
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.item_code
										.name()))
							spancopData
									.setItem_code(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.item_code
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.prod_key
										.name()))
							spancopData
									.setProd_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.prod_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.mapping_status
										.name()))
							spancopData
									.setStatus(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.mapping_status
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.actual_qty
										.name()))
							spancopData
									.setActual_qty(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.actual_qty
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.annual_qty
										.name()))
							spancopData
									.setAnnual_qty(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.annual_qty
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.type
										.name()))
							spancopData
									.setType(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.type
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.uom
										.name()))
							spancopData
									.setUom(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.uom
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.buying_frequency
										.name()))
							spancopData
									.setBuying_frequency(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.buying_frequency
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.added_date
										.name()))
							spancopData
									.setAdded_date(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.added_date
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.added_by
										.name()))
							spancopData
									.setAdded_by(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.added_by
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.target_date
										.name()))
							spancopData
									.setTarget_date(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.target_date
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.replaced_brand
										.name()))
							spancopData
									.setReplaced_brand(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.replaced_brand
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.replaced_product
										.name()))
							spancopData
									.setReplaced_product(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.replaced_product
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.lost_date
										.name()))
							spancopData
									.setLost_date(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.lost_date
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.lost_reason
										.name()))
							spancopData
									.setLost_reason(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.lost_reason
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.insert_stat
										.name()))
							spancopData
									.setInsert_stat(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.insert_stat
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.modified_by
										.name()))
							spancopData
									.setModified_by(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.modified_by
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.modified_on
										.name()))
							spancopData
									.setModified_on(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.modified_on
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.status
										.name()))
							spancopData
									.setStatus(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.status
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.brand
										.name()))
							spancopData
									.setBrand(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.brand
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.prod_name
										.name()))
							spancopData
									.setProd_name(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.prod_name
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.category_name
										.name()))
							spancopData
									.setCategory_name(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.category_name
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.map_status
										.name()))
							spancopData
									.setMap_status(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.map_status
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.remarks
										.name()))
							spancopData
									.setRemarks(singleEntry
											.getString(DSRTableCreate.COLOUMNS_CUSTOMERWISE_TARGET.remarks
													.name()));
						spancopData.setIs_synced_to_server("1");
					}
					spancopList.add(spancopData);
				}
				B2CApp.b2cPreference.setIsDbFilled(B2CApp.b2cPreference.isDbFilled()
						&& DSRApp.dsrLdbs.insertSpancops(this, spancopList));
				new PostRequestManager().execute(B2CApp.b2cPreference.getBaseUrl2()
						+ DSRAppConstant.METHOD_GET_AERP_MASTER_ITEM);
				return;
			} else {
				if (responseOuterJson.has(DSRAppConstant.KEY_MSG))
					message = responseOuterJson
							.getString(DSRAppConstant.KEY_MSG);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.LogError("JSONException : ", e.toString());
		}
		dismissProgress();
		alertUserP(DSRDayScreen.this, "Failed", message, "OK");
	}

	protected void onPostMasterItem(String responseString) {
		JSONObject responseOuterJson;
		JSONArray dataArray;
		String message = "Operation failed in a timely manner. Please try again";
		System.out
				.println("*******************************************************************************************************");
		System.out.println("Inside onPostMasterItem ");
		System.out
				.println("*******************************************************************************************************");
		try {
			responseOuterJson = new JSONObject(responseString);
			if (responseOuterJson.getString(DSRAppConstant.KEY_STATUS)
					.equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS)
					&& responseOuterJson.has(DSRAppConstant.KEY_DATA)) {
				dataArray = responseOuterJson
						.getJSONArray(DSRAppConstant.KEY_DATA);
				ArrayList<MasterItemData> MasterItemDataList = new ArrayList<MasterItemData>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject singleEntry = dataArray.getJSONObject(i);
					MasterItemData MasterItemData = new MasterItemData();
					if (singleEntry
							.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MI_KEY
									.name())) {
						MasterItemData
								.setMI_KEY(singleEntry
										.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MI_KEY
												.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.U_ITEM_CODE
										.name()))
							MasterItemData
									.setU_ITEM_CODE(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.U_ITEM_CODE
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.ITEM_CODE
										.name()))
							MasterItemData
									.setITEM_CODE(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.ITEM_CODE
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.THICKNESS_DIA
										.name()))
							MasterItemData
									.setTHICKNESS_DIA(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.THICKNESS_DIA
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.COLOR_CODE
										.name()))
							MasterItemData
									.setCOLOR_CODE(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.COLOR_CODE
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MANU_CODE
										.name()))
							MasterItemData
									.setMANU_CODE(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MANU_CODE
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MATERIAL_TYPE_CODE
										.name()))
							MasterItemData
									.setMATERIAL_TYPE_CODE(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MATERIAL_TYPE_CODE
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_key
										.name()))
							MasterItemData
									.setItem_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.thickness_dia_key
										.name()))
							MasterItemData
									.setThickness_dia_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.thickness_dia_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.color_key
										.name()))
							MasterItemData
									.setColor_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.color_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.manu_key
										.name()))
							MasterItemData
									.setManu_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.manu_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.material_type_key
										.name()))
							MasterItemData
									.setMaterial_type_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.material_type_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.REMARKS
										.name()))
							MasterItemData
									.setREMARKS(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.REMARKS
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.SHORT_DESC
										.name()))
							MasterItemData
									.setSHORT_DESC(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.SHORT_DESC
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.LONG_DESC
										.name()))
							MasterItemData
									.setLONG_DESC(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.LONG_DESC
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.tally_item_desc
										.name()))
							MasterItemData
									.setTally_item_desc(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.tally_item_desc
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.TYPE
										.name()))
							MasterItemData
									.setTYPE(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.TYPE
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MIN_INVENTARY
										.name()))
							MasterItemData
									.setMIN_INVENTARY(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MIN_INVENTARY
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MIN_REODR_QTY
										.name()))
							MasterItemData
									.setMIN_REODR_QTY(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MIN_REODR_QTY
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MAX_REODR_QTY
										.name()))
							MasterItemData
									.setMAX_REODR_QTY(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MAX_REODR_QTY
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MAX_INVENTAR
										.name()))
							MasterItemData
									.setMAX_INVENTAR(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MAX_INVENTAR
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.LIST_PRICE
										.name()))
							MasterItemData
									.setLIST_PRICE(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.LIST_PRICE
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.standard_price
										.name()))
							MasterItemData
									.setStandard_price(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.standard_price
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.mrp_rate
										.name()))
							MasterItemData
									.setMrp_rate(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.mrp_rate
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.basic_pur_cost
										.name()))
							MasterItemData
									.setBasic_pur_cost(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.basic_pur_cost
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.stock_trnf_price
										.name()))
							MasterItemData
									.setStock_trnf_price(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.stock_trnf_price
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_moving_status
										.name()))
							MasterItemData
									.setItem_moving_status(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_moving_status
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.BOM
										.name()))
							MasterItemData
									.setBOM(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.BOM
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.CATALOG_NO
										.name()))
							MasterItemData
									.setCATALOG_NO(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.CATALOG_NO
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MIN_REODR_LEVEL
										.name()))
							MasterItemData
									.setMIN_REODR_LEVEL(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MIN_REODR_LEVEL
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MAX_STOCK_LEVEL
										.name()))
							MasterItemData
									.setMAX_STOCK_LEVEL(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.MAX_STOCK_LEVEL
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_type
										.name()))
							MasterItemData
									.setItem_type(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_type
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_type_key
										.name()))
							MasterItemData
									.setItem_type_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_type_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.category7_key
										.name()))
							MasterItemData
									.setCategory7_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.category7_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.grade_desc
										.name()))
							MasterItemData
									.setGrade_desc(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.grade_desc
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.SHORT_NAME
										.name()))
							MasterItemData
									.setSHORT_NAME(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.SHORT_NAME
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.DISORDER
										.name()))
							MasterItemData
									.setDISORDER(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.DISORDER
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.display_code
										.name()))
							MasterItemData
									.setDisplay_code(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.display_code
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.ACTIVE_STATUS
										.name()))
							MasterItemData
									.setACTIVE_STATUS(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.ACTIVE_STATUS
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.tariff_sh_no
										.name()))
							MasterItemData
									.setTariff_sh_no(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.tariff_sh_no
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.lp_tax_key
										.name()))
							MasterItemData
									.setLp_tax_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.lp_tax_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.op_tax_key
										.name()))
							MasterItemData
									.setOp_tax_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.op_tax_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.discount_per
										.name()))
							MasterItemData
									.setDiscount_per(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.discount_per
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_base_unit
										.name()))
							MasterItemData
									.setItem_base_unit(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_base_unit
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_package_unit
										.name()))
							MasterItemData
									.setItem_package_unit(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_package_unit
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_pack_to_base_conversion
										.name()))
							MasterItemData
									.setItem_pack_to_base_conversion(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.item_pack_to_base_conversion
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.created_by
										.name()))
							MasterItemData
									.setCreated_by(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.created_by
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.created_on
										.name()))
							MasterItemData
									.setCreated_on(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.created_on
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.modified_by
										.name()))
							MasterItemData
									.setModified_by(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.modified_by
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.modified_on
										.name()))
							MasterItemData
									.setModified_on(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.modified_on
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.rate
										.name()))
							MasterItemData
									.setRate(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.rate
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.is_non_standard
										.name()))
							MasterItemData
									.setIs_non_standard(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.is_non_standard
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.mi_post_status
										.name()))
							MasterItemData
									.setMi_post_status(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_MASTER_ITEM.mi_post_status
													.name()));

						 MasterItemData.setIs_synced_to_server("1"); 
					}
					MasterItemDataList.add(MasterItemData);
				}
				B2CApp.b2cPreference.setIsDbFilled(B2CApp.b2cPreference.isDbFilled()
						&& DSRApp.dsrLdbs.insertMasterItem(this,
								MasterItemDataList));
				new PostRequestManager().execute(B2CApp.b2cPreference.getBaseUrl2()
						+ DSRAppConstant.METHOD_GET_AERP_ITEM_MSTR);
				return;
			} else {
				if (responseOuterJson.has(DSRAppConstant.KEY_MSG))
					message = responseOuterJson
							.getString(DSRAppConstant.KEY_MSG);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.LogError("JSONException : ", e.toString());
		}
		dismissProgress();
		alertUserP(DSRDayScreen.this, "Failed", message, "OK");
	}

	protected void onPostItemMstr(String responseString) {
		JSONObject responseOuterJson;
		JSONArray dataArray;
		String message = "Operation failed in a timely manner. Please try again";
		System.out
				.println("*******************************************************************************************************");
		System.out.println("Inside onPostItemMstr ");
		System.out
				.println("*******************************************************************************************************");
		try {
			responseOuterJson = new JSONObject(responseString);
			if (responseOuterJson.getString(DSRAppConstant.KEY_STATUS)
					.equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS)
					&& responseOuterJson.has(DSRAppConstant.KEY_DATA)) {
				dataArray = responseOuterJson
						.getJSONArray(DSRAppConstant.KEY_DATA);
				ArrayList<ItemMstrData> ItemMstrDataList = new ArrayList<ItemMstrData>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject singleEntry = dataArray.getJSONObject(i);
					ItemMstrData ItemMstrData = new ItemMstrData();
					if (singleEntry
							.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.s_no
									.name())) {
						ItemMstrData
								.setS_no(singleEntry
										.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.s_no
												.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.ref_no
										.name()))
							ItemMstrData
									.setRef_no(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.ref_no
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_code
										.name()))
							ItemMstrData
									.setItem_code(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_code
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_type
										.name()))
							ItemMstrData
									.setItem_type(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_type
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_description
										.name()))
							ItemMstrData
									.setItem_description(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_description
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_shortname
										.name()))
							ItemMstrData
									.setItem_shortname(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_shortname
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.mfgr_ref_key
										.name()))
							ItemMstrData
									.setMfgr_ref_key(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.mfgr_ref_key
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_unit
										.name()))
							ItemMstrData
									.setItem_unit(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.item_unit
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.entered_by
										.name()))
							ItemMstrData
									.setEntered_by(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.entered_by
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.entered_on
										.name()))
							ItemMstrData
									.setEntered_on(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.entered_on
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.last_modified_by
										.name()))
							ItemMstrData
									.setLast_modified_by(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.last_modified_by
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.last_modified_on
										.name()))
							ItemMstrData
									.setLast_modified_on(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_ITEM_MSTR.last_modified_on
													.name()));
					}
					ItemMstrDataList.add(ItemMstrData);
				}
				B2CApp.b2cPreference.setIsDbFilled(B2CApp.b2cPreference.isDbFilled()
						&& DSRApp.dsrLdbs
								.insertItemMstr(this, ItemMstrDataList));
				new PostRequestManager().execute(B2CApp.b2cPreference.getBaseUrl2()
						+ DSRAppConstant.METHOD_GET_AERP_PROSPECT_MSTR);
				return;
			} else {
				if (responseOuterJson.has(DSRAppConstant.KEY_MSG))
					message = responseOuterJson
							.getString(DSRAppConstant.KEY_MSG);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.LogError("JSONException : ", e.toString());
		}
		dismissProgress();
		alertUserP(DSRDayScreen.this, "Failed", message, "OK");
	}

	protected void onPostProspectMstr(String responseString) {
		JSONObject responseOuterJson;
		JSONArray dataArray;
		String message = "Operation failed in a timely manner. Please try again";
		System.out
				.println("*******************************************************************************************************");
		System.out.println("Inside onPostProspectMstr ");
		System.out
				.println("*******************************************************************************************************");
		try {
			responseOuterJson = new JSONObject(responseString);
			if (responseOuterJson.getString(DSRAppConstant.KEY_STATUS)
					.equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS)
					&& responseOuterJson.has(DSRAppConstant.KEY_DATA)) {
				dataArray = responseOuterJson
						.getJSONArray(DSRAppConstant.KEY_DATA);
				ArrayList<ProspectMstrData> ProspectMstrDataList = new ArrayList<ProspectMstrData>();
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject singleEntry = dataArray.getJSONObject(i);
					ProspectMstrData ProspectMstrData = new ProspectMstrData();
					if (singleEntry
							.has(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.pros_key
									.name())) {
						ProspectMstrData
								.setPros_key(singleEntry
										.getString(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.pros_key
												.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.pros_name
										.name()))
							ProspectMstrData
									.setPros_name(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.pros_name
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.status
										.name()))
							ProspectMstrData
									.setStatus(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.status
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.enteredBy
										.name()))
							ProspectMstrData
									.setEnteredBy(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.enteredBy
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.enteredOn
										.name()))
							ProspectMstrData
									.setEnteredOn(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.enteredOn
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.last_modified_by
										.name()))
							ProspectMstrData
									.setLast_modified_by(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.last_modified_by
													.name()));
						if (singleEntry
								.has(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.last_modified_on
										.name()))
							ProspectMstrData
									.setLast_modified_on(singleEntry
											.getString(DSRTableCreate.COLOUMNS_AERP_PROSPECT_MSTR.last_modified_on
													.name()));
					}
					ProspectMstrDataList.add(ProspectMstrData);
				}
				B2CApp.b2cPreference.setIsDbFilled(B2CApp.b2cPreference.isDbFilled()
						&& DSRApp.dsrLdbs.insertProspectMstr(this,
								ProspectMstrDataList));
				new PostRequestManager().execute(B2CApp.b2cPreference.getBaseUrl2()
						+ DSRAppConstant.METHOD_GET_CUST_CAT_LIST);
				return;
			} else {
				if (responseOuterJson.has(DSRAppConstant.KEY_MSG))
					message = responseOuterJson
							.getString(DSRAppConstant.KEY_MSG);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.LogError("JSONException : ", e.toString());
		}
		dismissProgress();
		alertUserP(DSRDayScreen.this, "Failed", message, "OK");
	}

	protected void onPostCustCatList(String responseString) {
		JSONObject responseOuterJson;
		JSONArray dataArray;
		HashSet<String> eventList = new HashSet<String>();
		String message = "Operation failed in a timely manner. Please try again";
		try {
			responseOuterJson = new JSONObject(responseString);
			if (responseOuterJson.getString(DSRAppConstant.KEY_STATUS)
					.equalsIgnoreCase(DSRAppConstant.KEY_SUCCESS)
					&& responseOuterJson.has(DSRAppConstant.KEY_DATA)) {
				dataArray = responseOuterJson.getJSONArray("data");
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject singleEntry = dataArray.getJSONObject(i);
					if (singleEntry.has(DSRAppConstant.KEY_COMPANY_NAME)) {
						eventList.add(singleEntry
								.getString(DSRAppConstant.KEY_COMPANY_NAME));
					}
				}
				B2CApp.b2cPreference.setTCustomerList(eventList);
				dismissProgress();
				if (isSyncSuccess)
					alertUserP(mContext, "Success",
							"Synced to server successfully.", "OK");
				isSyncSuccess = false;
				currentDate.setText(getDateForTitle());
				queryEventsOfDay();
				return;
			} else {
				if (responseOuterJson.has(DSRAppConstant.KEY_MSG))
					message = responseOuterJson
							.getString(DSRAppConstant.KEY_MSG);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Logger.LogError("JSONException : ", e.toString());
		}
		dismissProgress();
		alertUserP(DSRDayScreen.this, "Failed", message, "OK");
	}*/


/*	class DSRSyncToServer extends AsyncTask<String, String, String> {

		private static final int OPTYPE_UNKNOWN = -1;
		private static final int OPTYPE_ADDALLNEWDATA = 61;
		private static final int OPTYPE_UPDATEALLDATA = 62;

		private int operationType;
		private String uriInProgress;

		private Context mContext;

		public DSRSyncToServer(Context context) {
			mContext = context;
		}

		protected String doInBackground(String... urls) {

			uriInProgress = urls[0];
			Logger.LogError("postUrl : ", uriInProgress);

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(uriInProgress);
			try {

				operationType = getOperationType(uriInProgress);

				String jsonString = "";

				try {

					jsonString = (new JSONObject().put(
							DSRAppConstant.KEY_VALUES,
							getAllEventsArray(operationType))).toString();

					Logger.LogError("jsonString : ", jsonString);

				} catch (JSONException e) {
					e.printStackTrace();
					Logger.LogError("JSONException 1 : ", e.toString());
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

			updateProgress(PROGRESS_MSG_SYNC);

		}

		protected void onPostExecute(String responseString) {

			Logger.LogError("responseString : ", "" + responseString);

			if (responseString == null) {

				isSyncSuccess = false;

				if (operationType == OPTYPE_ADDALLNEWDATA) {

					if (!updateIsUptodate)
						new DSRSyncToServer(DSRDayScreen.this)
								.execute(B2CApp.b2cPreference.getBaseUrl2()
										+ DSRAppConstant.METHOD_UPDATE_ALL_DATA);
					else {
						dismissProgress();
						alertUserP(
								mContext,
								"Failed",
								"Operation failed in a timely manner. Please try again.",
								"OK");
					}
				} else {
					dismissProgress();
					alertUserP(
							mContext,
							"Failed",
							"Operation failed in a timely manner. Please try again.",
							"OK");
				}
			} else {
				if (operationType == OPTYPE_ADDALLNEWDATA)
					onPostExecuteAddAll(responseString);
				else if (operationType == OPTYPE_UPDATEALLDATA)
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
				Logger.LogError("JSONException add : ", e.toString());
				isSyncSuccess = false;
				if (!updateIsUptodate)
					new DSRSyncToServer(DSRDayScreen.this)
							.execute(B2CApp.b2cPreference.getBaseUrl2()
									+ DSRAppConstant.METHOD_UPDATE_ALL_DATA);
				else {
					dismissProgress();
					alertUserP(
							mContext,
							"Failed",
							"Operation failed in a timely manner. Please try again.",
							"OK");
				}
				return;
			}

			for (int i = 0; i < responseArray.length(); i++) {
				try {
					responseJson = responseArray.getJSONObject(i);

					if (responseJson.has(DSRAppConstant.KEY_STATUS)
							&& responseJson
									.getString(DSRAppConstant.KEY_STATUS)
									.equalsIgnoreCase(
											DSRAppConstant.KEY_SUCCESS)) {

						DSRApp.dsrLdbs
								.updateEventKey(
										mContext,
										responseJson
												.getString(DSRAppConstant.KEY_DUMMY_KEY),
										responseJson
												.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key
														.name()),
										responseJson
												.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key
														.name()));

					} else
						isSyncSuccess = false;

				} catch (JSONException e) {
					e.printStackTrace();
					Logger.LogError("JSONException add 2 : ", e.toString());
					isSyncSuccess = false;
				}
			}

			if (!updateIsUptodate)
				new DSRSyncToServer(DSRDayScreen.this).execute(B2CApp.b2cPreference
						.getBaseUrl2() + DSRAppConstant.METHOD_UPDATE_ALL_DATA);
			else {
				if (isSyncSuccess) {
					refreshDB();
				} else {
					dismissProgress();
					alertUserP(
							mContext,
							"Failed",
							"Operation failed in a timely manner. Please try again.",
							"OK");
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
				Logger.LogError("JSONException upd : ", e.toString());
				isSyncSuccess = false;
				dismissProgress();
				alertUserP(
						mContext,
						"Failed",
						"Operation failed in a timely manner. Please try again.",
						"OK");
				return;
			}

			for (int i = 0; i < responseArray.length(); i++) {
				try {
					responseJson = responseArray.getJSONObject(i);

					if (responseJson.has(DSRAppConstant.KEY_STATUS)
							&& responseJson
									.getString(DSRAppConstant.KEY_STATUS)
									.equalsIgnoreCase(
											DSRAppConstant.KEY_SUCCESS)) {
						DSRApp.dsrLdbs
								.updateAsSynced(
										mContext,
										responseJson
												.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key
														.name()),
										responseJson
												.getString(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key
														.name()));
					} else
						isSyncSuccess = false;

				} catch (JSONException e) {
					e.printStackTrace();
					Logger.LogError("JSONException upd 2 : ", e.toString());
					isSyncSuccess = false;
				}
			}

			dismissProgress();

			if (isSyncSuccess == false)
				alertUserP(
						mContext,
						"Failed",
						"Operation failed in a timely manner. Please try again.",
						"OK");
			else
				refreshDB();
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

		private void alertUserP(Context context, String title, String msg,
				String btn) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage(msg)
					.setTitle(title)
					.setCancelable(false)
					.setPositiveButton(btn,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}

		private JSONArray getAllEventsArray(int optype) throws JSONException {

			JSONArray tJsonArray = new JSONArray();
			String keyName;

			Cursor mCursor;

			if (optype == OPTYPE_UPDATEALLDATA) {
				mCursor = DSRApp.dsrLdbs
						.fetchCustomSelection(
								mContext,
								DSRTableCreate.TABLE_AERP_EVENT_MASTER,
								null,
								DSRTableCreate.COLOUMNS_EVENT_MASTER.is_synced_to_server
										.name() + " = " + "0");
				keyName = DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name();
			} else {
				mCursor = DSRApp.dsrLdbs.fetchCustomSelection(mContext,
						DSRTableCreate.TABLE_AERP_EVENT_MASTER, null,
						DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name()
								+ " < " + "0");
				keyName = DSRAppConstant.KEY_DUMMY_KEY;
			}

			if (mCursor == null)
				return tJsonArray;

			int columnIndexKey = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key
							.name());
			int columnIndexUserKey = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key
							.name());
			int columnIndexType = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type
							.name());
			int columnIndexTitle = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title
							.name());
			int columnIndexFromDatetime = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time
							.name());
			int columnIndexToDateTime = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time
							.name());
			int columnIndexEventDesc = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description
							.name());
			int columnIndexEventStatus = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.status
							.name());
			int columnIndexCMKey = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey
							.name());
			int columnIndexLocation = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.area
							.name());
			int columnIndexLatitude = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude
							.name());
			int columnIndexLongitude = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude
							.name());
			int columnIndexAltitude = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude
							.name());
			int columnIndexVisitUpdateTime = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time
							.name());
			int columnIndexActionDesc = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response
							.name());
			int columnIndexPlan = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan
							.name());
			int columnIndexObjective = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective
							.name());
			int columnIndexStrategy = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy
							.name());
			int columnIndexCustomerName = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name
							.name());
			int columnIndexPurpose = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation
							.name());
			int columnIndexvisitedLatitude = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude
							.name());
			int columnIndexvisitedLongitude = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude
							.name());
			int columnIndexvisitedAltitude = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude
							.name());
			int columnIndexCompletedOn = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on
							.name());
			int columnIndexCancelledOn = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on
							.name());
			int columnIndexAction = mCursor
					.getColumnIndex(DSRTableCreate.COLOUMNS_EVENT_MASTER.action
							.name());

			do {

				JSONObject json = new JSONObject();

				json.put(keyName, mCursor.getString(columnIndexKey));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_user_key
						.name(), mCursor.getString(columnIndexUserKey));
				json.put(
						DSRTableCreate.COLOUMNS_EVENT_MASTER.event_type.name(),
						mCursor.getString(columnIndexType));
				json.put(
						DSRTableCreate.COLOUMNS_EVENT_MASTER.event_title.name(),
						mCursor.getString(columnIndexTitle));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time
						.name(), mCursor.getString(columnIndexFromDatetime));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time
						.name(), mCursor.getString(columnIndexToDateTime));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.event_description
						.name(), mCursor.getString(columnIndexEventDesc));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(),
						mCursor.getString(columnIndexEventStatus));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name(),
						mCursor.getString(columnIndexCMKey));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name(),
						mCursor.getString(columnIndexLocation));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.latitude.name(),
						mCursor.getString(columnIndexLatitude));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.longitude.name(),
						mCursor.getString(columnIndexLongitude));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.altitude.name(),
						mCursor.getString(columnIndexAltitude));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.visit_update_time
						.name(), mCursor.getString(columnIndexVisitUpdateTime));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action_response
						.name(), mCursor.getString(columnIndexActionDesc));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.plan.name(),
						mCursor.getString(columnIndexPlan));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.objective.name(),
						mCursor.getString(columnIndexObjective));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.strategy.name(),
						mCursor.getString(columnIndexStrategy));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name
						.name(), mCursor.getString(columnIndexCustomerName));
				json.put(
						DSRTableCreate.COLOUMNS_EVENT_MASTER.preparation.name(),
						mCursor.getString(columnIndexPurpose));
				json.put(
						DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_latitude
								.name(), mCursor
								.getString(columnIndexvisitedLatitude));
				json.put(
						DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_longitude
								.name(), mCursor
								.getString(columnIndexvisitedLongitude));
				json.put(
						DSRTableCreate.COLOUMNS_EVENT_MASTER.event_visited_altitude
								.name(), mCursor
								.getString(columnIndexvisitedAltitude));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.completed_on
						.name(), mCursor.getString(columnIndexCompletedOn));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.cancelled_on
						.name(), mCursor.getString(columnIndexCancelledOn));
				json.put(DSRTableCreate.COLOUMNS_EVENT_MASTER.action.name(),
						mCursor.getString(columnIndexAction));

				json.put("event_repeat_config", "ONE_TIME_EVENT");

				tJsonArray.put(json);

			} while (mCursor.moveToNext());

			mCursor.close();
			mCursor = null;

			return tJsonArray;
		}

	}*/

	// send an upstream message
	/*
	 * public void onClick(final View view) { if (view ==
	 * findViewById(R.id.send)) { new AsyncTask() {
	 *
	 * @Override protected String doInBackground(Void... params) { String msg =
	 * ""; try { Bundle data = new Bundle(); data.putString("my_message",
	 * "Hello World"); data.putString("my_action",
	 * "com.google.android.gcm.demo.app.ECHO_NOW"); String id =
	 * Integer.toString(msgId.incrementAndGet()); gcm.send(SENDER_ID +
	 * "@gcm.googleapis.com", id, data); msg = "Sent message"; } catch
	 * (IOException ex) { msg = "Error :" + ex.getMessage(); } return msg; }
	 *
	 * @Override protected void onPostExecute(String msg) { mDisplay.append(msg
	 * + "\n"); } }.execute(null, null, null); } else if (view ==
	 * findViewById(R.id.clear)) { mDisplay.setText(""); } }
	 */

}

/* http://worksheet.sementerprises.in/worksheet/isteerb2crestphp/getPendingSalesOrder/

		 {"user_key":"175","unit_key":"101","cus_key":"3603",
		 "auth_token":"0d00db014addc07cf0a37df654a5b4e9","security_token":""}

		 http://worksheet.sementerprises.in/worksheet/isteerb2crestphp/getPendingSalesOrder/
		 {"auth_token":"ea61cb08c0ac7210f8e76089f6bd69aa","cus_key":"1205","unit_key":"101",
		 "security_token":"","user_key":"175"}*/

























