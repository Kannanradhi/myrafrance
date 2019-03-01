package com.isteer.b2c.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.isteer.b2c.model.EventData;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

public class DataInterface {
	private static Context context = null;
	private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");

	public DataInterface(Context mContext) {
		this.context = mContext;
	}

	public static ArrayList<EventData> getCurrentDayEvents(String startDate)
			throws ParseException {
		Uri l_eventUri;
		ContentResolver cr = context.getContentResolver();
		ArrayList<EventData> eventCollection = new ArrayList<EventData>();
		Calendar calendar = Calendar.getInstance();
		if (Build.VERSION.SDK_INT >= 8) {
			l_eventUri = Uri.parse("content://com.android.calendar/events");
		} else {
			l_eventUri = Uri.parse("content://calendar/events");
		}
		String dtstart = "dtstart";
		String dtend = "dtend";

		String[] l_projection = new String[] { "title", "dtstart", "dtend" };

		// Date dateCC = formatter.parse("04/27/2013");
		Date dateCC = formatter.parse(startDate);
		Calendar calendarStartDate = Calendar.getInstance();
		calendar.setTime(dateCC);

		long after = calendar.getTimeInMillis();

		SimpleDateFormat formatterr = new SimpleDateFormat("MM/dd/yy hh:mm:ss");

		Calendar endOfDay = Calendar.getInstance();
		// Date dateCCC = formatterr.parse("04/27/2013 23:59:59");
		Date dateCCC = formatterr.parse(startDate + " 23:59:59");
		endOfDay.setTime(dateCCC);

		long before = endOfDay.getTimeInMillis();

		Cursor eventCursorr = cr.query(l_eventUri, new String[] { "title",
				"dtstart", "dtend" }, "(" + dtstart + ">" + after + " and "
				+ dtend + "<" + endOfDay.getTimeInMillis() + ")", null,
				"dtstart ASC");

		if (eventCursorr.moveToFirst()) {

			String l_title;
			String l_begin;
			String l_end;
			int l_colTitle = eventCursorr.getColumnIndex(l_projection[0]);
			int l_colBegin = eventCursorr.getColumnIndex(l_projection[1]);
			int l_colEnd = eventCursorr.getColumnIndex(l_projection[2]);

			do {/*
				EventData eventData = new EventData();

				l_title = eventCursorr.getString(l_colTitle);
				l_begin = Utility.startTimeEndTime(eventCursorr
						.getString(l_colBegin));
				l_end = Utility.startTimeEndTime(eventCursorr
						.getString(l_colEnd));
				eventData.setDtstart(l_begin);
				eventData.setDtend(l_end);
				eventCollection.add(eventData);

			*/} while (eventCursorr.moveToNext());
		}
		return eventCollection;

	}
}
