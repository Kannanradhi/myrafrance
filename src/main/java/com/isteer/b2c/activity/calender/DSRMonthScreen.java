package com.isteer.b2c.activity.calender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract.Instances;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.action.B2CPendingOrderScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.activity.B2CLancher.DSRDayScreen;
import com.isteer.b2c.activity.action.B2CCountersScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.utility.B2CBasicUtils;
import com.isteer.b2c.utility.Logger;
import com.isteer.b2c.utility.SimpleGestureFilter;
import com.isteer.b2c.utility.SimpleGestureFilter.SimpleGestureListener;
import com.isteer.b2c.R;

public class DSRMonthScreen extends AppCompatActivity implements OnClickListener, SimpleGestureListener {
	
	private PopupWindow ppWindow;
	private SimpleGestureFilter detector;

	public static final String[] INSTANCE_PROJECTION = new String[] {
	    Instances.CALENDAR_ID,
	    Instances.EVENT_ID,      
	    Instances.TITLE,
	    Instances.DESCRIPTION,
	    Instances.EVENT_LOCATION,
	    Instances.EVENT_TIMEZONE,
	    Instances.BEGIN,         
	    Instances.END,
	    Instances.ALL_DAY,
	    Instances.RRULE,
	    Instances.HAS_ALARM,
	    Instances.CUSTOM_APP_PACKAGE
	};
	
	public int[][] eventPresents = new int[31][3];
	
	public static ArrayList<HashMap<String, String>> entries = new ArrayList<HashMap<String, String>>();

	private static final int INDEX_CALENDAR_ID = 0;
	private static final int INDEX_EVENT_ID = 1;
	private static final int INDEX_TITLE = 2;
	private static final int INDEX_DESCRIPTION = 3;
	private static final int INDEX_EVENT_LOCATION = 4;
	private static final int INDEX_EVENT_TIMEZONE = 5;
	private static final int INDEX_BEGIN = 6;
	private static final int INDEX_END = 7;
	private static final int INDEX_ALL_DAY = 8;
	private static final int INDEX_RRULE = 9;
	private static final int INDEX_HAS_ALARM = 10;

	private static final int OPTYPE_UNKNOWN = -1;
	private static final int OPTYPE_SEARCH_DATE = 12;
	
	private int currentFirstVisibleItem = 0;
	private int currentVisibleItemCount = 0;
	private int currentScrollState = 0;
	private int totalItemCount = 0;
	private int totalItemCountPrev = 0;
	private int pageLoaded = 0;
	private boolean loadingMore = false;
	
	private static ProgressDialog pdialog;
	private static String PROGRESS_MSG = "Loading...";
	
	private B2CBasicUtils isdUtils;

	private String uriInProgress;

	private TextView bg_pending_eventbox_count, bg_completed_eventbox_count;
	
	//private Vibrator myVibrator;
	//private FFUtilities ffUtility;

	private static final String TAG = "FFCalendarScreen";
	public static boolean isShowingCalendar = true;

	private View highlightedView;
	//private Drawable prevBgResource = null;
	
	//private ListView entryList;
	//FFEventsAdaptor entryListAdaptor;
	
	private TextView currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar _calendar;
	@SuppressLint("NewApi")
	public static int current_day, current_month, current_year;
	@SuppressWarnings("unused")
	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
	private final DateFormat dateFormatter = new DateFormat();
	private static final String dateTemplate = "MMMM yyyy";

	private TextView header_title;
	private ImageView btn_header_left, btn_header_right;
	private LinearLayout wrapper_calendar, wrapper_list_c;

	public static boolean isToCloseAlert = false;
	
	private static final String dateFormat = "yyyyMM";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.scr_isr_monthview);

		initVar();
		
		_calendar = Calendar.getInstance(Locale.getDefault());
		current_month = _calendar.get(Calendar.MONTH) + 1;
		current_year = _calendar.get(Calendar.YEAR);
		Log.e(TAG, "Calendar Instance:= " + "Month: " + current_month + " " + "Year: "
				+ current_year);

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (TextView) this.findViewById(R.id.currentMonth);
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		adapter = new GridCellAdapter(getApplicationContext(),
				R.id.calendar_day_gridcell, current_month, current_year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	protected void onResume() {

		super.onResume();

		setGridCellAdapterToDate(current_month, current_year);
		queryEventsOfMonth();
		
		if(isToCloseAlert && ppWindow!=null && ppWindow.isShowing())
		{
			isToCloseAlert = false;
			ppWindow.dismiss();
		}
		
/*		if (highlightedView != null)
	        unhighlightDate(highlightedView);
	*/	
	}

	/**
	 * 
	 * @param month
	 * @param year
	 */
	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(getApplicationContext(),
				R.id.calendar_day_gridcell, month, year);
		_calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(DateFormat.format(dateTemplate,
				_calendar.getTime()));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	@Override
	public void onClick(View pView) {
		
		//myVibrator.vibrate(50);

		if (pView == prevMonth) {
			
			//entryList.setAdapter(null);
			
			if (current_month <= 1) {
				current_month = 12;
				current_year--;
			} else {
				current_month--;
			}

			setGridCellAdapterToDate(current_month, current_year);
			queryEventsOfMonth();
		}
		else if (pView == nextMonth) {
			
			//entryList.setAdapter(null);

			if (current_month > 11) {
				current_month = 1;
				current_year++;
			} else {
				current_month++;
			}

			setGridCellAdapterToDate(current_month, current_year);
			queryEventsOfMonth();
		}
		else
		{

			//if (isdUtils.isNetAvailable()) 
			{
				switch (pView.getId()) {
					case R.id.img_back:
						finish();
						break;
					case R.id.img_home:
						gotoB2CMenuScreen();
						break;
				case R.id.btn_header_right:
					gotoISRDayScreen();
					break;	
					
				case R.id.bottombar_one:
					break;

				case R.id.bottombar_two:
					gotoDSRCountersScreen();
					break;
					
				case R.id.bottombar_three:
					gotoDSRSyncScreen();
					break;

				case R.id.bottombar_four:
					gotoDSRCustListScreen();
					break;

				case R.id.bottombar_five:
					gotoDSRPendingOrders();
					break;
					
				}
			} 
		//else
		//	alertUserP(this, "Connection Error", "No Internet connection available", "OK");
			
		}

	}

	private void gotoFFListUScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "Destroying View ...");
		super.onDestroy();
	}

	private void initVar()
	{
		findViewById(R.id.img_back).setOnClickListener(this);
		findViewById(R.id.img_home).setOnClickListener(this);

		wrapper_calendar = (LinearLayout) findViewById(R.id.wrapper_calendar);
		wrapper_list_c = (LinearLayout) findViewById(R.id.wrapper_list_c);
		
		header_title = (TextView) findViewById(R.id.header_title);
		header_title.setText("Visit Plan");

		((View) findViewById(R.id.bottombar_one)).setOnClickListener(this);
		((View) findViewById(R.id.bottombar_two)).setOnClickListener(this);
		((View) findViewById(R.id.bottombar_three)).setOnClickListener(this);
		((View) findViewById(R.id.bottombar_four)).setOnClickListener(this);
		((View) findViewById(R.id.bottombar_five)).setOnClickListener(this);
		
		btn_header_left = (ImageView) findViewById(R.id.btn_header_left);
		btn_header_left.setVisibility(View.VISIBLE);
		btn_header_left.setOnClickListener(this);

		btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
		btn_header_right.setVisibility(View.VISIBLE);
		btn_header_right.setImageResource(R.drawable.date_icon_fw);

		btn_header_right.setOnClickListener(this);
		
		isdUtils  = new B2CBasicUtils(this);
		
		detector = new SimpleGestureFilter(this,this);
	}
	
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		
		private static final String TAG = "GridCellAdapter";
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private View gridcell_eventbox, pending_gridcell_eventbox;
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"dd-MMM-yyyy");

		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {
			
			super();
			this._context = context;
			this.list = new ArrayList<String>();
			
			Log.e(TAG, "==> Passed in Date FOR Month: " + month + " "
					+ "Year: " + year);
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
			Log.d(TAG, "New Calendar:= " + calendar.getTime().toString());
			Log.d(TAG, "CurrentDayOfWeek :" + getCurrentWeekDay());
			Log.d(TAG, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

			printMonth(month, year);
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
			Log.d(TAG, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			Log.d(TAG, "Current Month: " + " " + currentMonthName + " having "
					+ daysInMonth + " days.");

			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			Log.d(TAG, "Gregorian Calendar:= " + cal.getTime().toString());

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				Log.d(TAG, "*->PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				Log.d(TAG, "**--> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				Log.d(TAG, "***---> PrevYear: " + prevYear + " PrevMonth:"
						+ prevMonth + " NextMonth: " + nextMonth
						+ " NextYear: " + nextYear);
			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			Log.d(TAG, "Week Day:" + currentWeekDay + " is "
					+ getWeekDayAsString(currentWeekDay));
			Log.d(TAG, "No. Trailing space to Add: " + trailingSpaces);
			Log.d(TAG, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				if (mm == 2)
					++daysInMonth;
				else if (mm == 3)
					++daysInPrevMonth;

			for (int i = 0; i < trailingSpaces; i++) {
				Log.d(TAG,
						"PREV MONTH:= "
								+ prevMonth
								+ " => "
								+ getMonthAsString(prevMonth)
								+ " "
								+ String.valueOf((daysInPrevMonth
										- trailingSpaces + DAY_OFFSET)
										+ i));
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY"
						+ "-"
						+ getMonthAsString(prevMonth)
						+ "-"
						+ prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				Log.d(currentMonthName, String.valueOf(i) + " "
						+ getMonthAsString(currentMonth) + " " + yy);
				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				Log.d(TAG, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.screen_gridcell, parent, false);
			}

			bg_completed_eventbox_count = (TextView) row.findViewById(R.id.bg_completed_eventbox_count);
			bg_pending_eventbox_count = (TextView) row.findViewById(R.id.bg_pending_eventbox_count);

			gridcell_eventbox = (View) row.findViewById(R.id.calendar_day_gridcell_eventbox);
			pending_gridcell_eventbox = (View) row.findViewById(R.id.calendar_day_gridcell_pending_eventbox);

			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);
			String currentString = list.get(position);
			String[] day_color = currentString.split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];
			if (currentString.contains("GREY")) {
				gridcell.setTextColor(Color.parseColor("#D7D4D4"));
				gridcell_eventbox.setVisibility(View.GONE);
				pending_gridcell_eventbox.setVisibility(View.GONE);
			} else {
			if (eventPresents[Integer.parseInt(theday) - 1][0] != 0) {
				pending_gridcell_eventbox.setVisibility(View.VISIBLE);
				bg_pending_eventbox_count.setText("" + eventPresents[Integer.parseInt(theday) - 1][0]);
				Log.e("RED", "" + (Integer.parseInt(theday) - 1) + "---------" + eventPresents[Integer.parseInt(theday) - 1][0]);
			} else
				pending_gridcell_eventbox.setVisibility(View.GONE);

			if (eventPresents[Integer.parseInt(theday) - 1][1] != 0) {
				gridcell_eventbox.setVisibility(View.VISIBLE);
				bg_completed_eventbox_count.setText("" + eventPresents[Integer.parseInt(theday) - 1][1]);
				Log.e("GREEN", "" + (Integer.parseInt(theday) - 1) + "---------" + eventPresents[Integer.parseInt(theday) - 1][1]);
			} else
				gridcell_eventbox.setVisibility(View.GONE);

			//gridcell.setBackgroundResource(R.drawable.cal_bg);
		}
			gridcell.setText(theday);
			gridcell.setTag(theday + "-" + themonth + "-" + theyear);

			return row;
		}

		@Override
		public void onClick(View view) {
						
			current_day = Integer.parseInt((String)((Button)view).getText());
			DSRDayScreen.toUpdateFromMonth = true;
			
			gotoISRDayScreen();

			/*
			
			String date_month_year = (String) view.getTag();
			Log.e("Selected date", date_month_year);
			
			try {
				
								Date parsedDate = dateFormatter.parse(date_month_year);
				if (highlightedView != null && highlightedView != view) {
			        unhighlightDate(highlightedView);
			    }
			    highlightDate(view);
				
				day = Integer.parseInt((String)((Button)view).getText());


				header_title.setText(FFAppConstant.MONTH+"/"+FFAppConstant.DAY+"/"+FFAppConstant.YEAR);

				pageLoaded=0;
				totalItemCountPrev = 0;		
				
				
				//new GetRequestManager().execute(FFAppConstant.getUrlDate());

				
				if(eventPresents[day-1])
					fetchEvents(day);
				else
					entries.clear();
				Log.e("day, entries", day+","+entries.size());

				entryListAdaptor = new FFEventsAdaptor(FFCalendarScreen.this, entries);
				entryList.setAdapter(entryListAdaptor);
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		*/}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}
	
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
	}
	
	private void queryEventsOfMonth()
	{
		eventPresents = new int[31][3];			

		_calendar.set(current_year, current_month - 1, _calendar.get(Calendar.DAY_OF_MONTH));

		Logger.LogError("_calendar.getTime()",""+_calendar.getTime());
		Logger.LogError("_calendar.getTime()111",""+DateFormat.format(dateFormat,_calendar.getTime()));

	ArrayList<EventData> eventDataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().fetchSelectedByMonth(DateFormat.format(dateFormat, _calendar.getTime())+"%");
		/*Cursor mCursor = DSRApp.dsrLdbs.fetchSelected(this, DSRTableCreate.TABLE_AERP_EVENT_MASTER , null, DSRTableCreate.COLOUMNS_EVENT_MASTER.event_month.name(), DSRLocalDBStorage.SELECTION_OPERATION_LIKE, new String[]{(String)DateFormat.format(dateFormat,
				_calendar.getTime())});*/
		Logger.LogError("eventDataArrayList",""+eventDataArrayList.size());
		if(eventDataArrayList.size() <=0)
		{
			Toast.makeText(this, "Events updation failed in a timely manner", Toast.LENGTH_LONG);
			return;
		}
		


		for (int i =0;i<eventDataArrayList.size();i++){
			EventData eventData = eventDataArrayList.get(i);
			
			int tDate = Integer.parseInt(eventData.getEvent_date_absolute());
			String tStatus = eventData.getStatus();
			
			if(tStatus.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Pending.name()) || tStatus.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Completed.name()))
				eventPresents[tDate-1][0]++;
			else if(tStatus.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Visited.name()))
				eventPresents[tDate-1][1]++;			
			else if(tStatus.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Cancelled.name()))
				eventPresents[tDate-1][2]++;			
			
		}

	}
	
/*	private void highlightDate(View currentView)
	{
		if(currentView!=null)
		{
			prevBgResource = currentView.getBackground();
			currentView.setBackgroundResource(R.drawable.cal_bg_sel);
			highlightedView = currentView;
		}
	}
	
	private void unhighlightDate(View currentView)
	{
		if(currentView!=null)
		{
			currentView.setBackground(prevBgResource);
			highlightedView = null;
		}
	}*/
	
/*

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			myVibrator.vibrate(50);
			
			goBack();

		}
		return true;

	}
*/	
	
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
	
	private void goBack() {

		onBackPressed();
	}

	private void gotoDSRCustListScreen()
	{
        startActivity(new Intent(this, B2CCustSearchScreen.class)
		.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}	
	

	
	private void gotoDSRSyncScreen() {
		
		startActivity(new Intent(this, B2CSyncScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}
	
	private void gotoISRDayScreen() {
		
		startActivity(new Intent(this, DSRDayScreen.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}
	private void gotoB2CMenuScreen() {

		startActivity(new Intent(this, B2CNewMainActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}
    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
         this.detector.onTouchEvent(me);
       return super.dispatchTouchEvent(me);
    }

	@Override
	public void onDoubleTap() {
		Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
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
	 
	 private void onRTLFling()
	 {
			//entryList.setAdapter(null);

			if (current_month > 11) {
				current_month = 1;
				current_year++;
			} else {
				current_month++;
			}

			setGridCellAdapterToDate(current_month, current_year);
			queryEventsOfMonth();
	 }
	 
	 private void onLTRFling()
	 {
			//entryList.setAdapter(null);
			
			if (current_month <= 1) {
				current_month = 12;
				current_year--;
			} else {
				current_month--;
			}

			setGridCellAdapterToDate(current_month, current_year);
			queryEventsOfMonth();
	 }
	 
		private void gotoDSRPendingOrders() {

			startActivity(new Intent(this, B2CPendingOrderScreen.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		}
		
		private void gotoDSRCountersScreen() {

			startActivity(new Intent(this, B2CCountersScreen.class)
					.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
		}
		
}
