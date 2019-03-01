package com.isteer.b2c.adapter;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.calender.DSREditProgScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.utility.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by rnows on 01-Mar-18.
 */

public class RCV_Report_visit_adapter extends PagedListAdapter<EventData, RCV_Report_visit_adapter.ViewHolder> {
    private Activity activity;
    private ArrayList<EventData> eventPresentsInDay;
    private String event_key;
    clickListener listener;
    private String from_date_time, visit_update_time;

    public RCV_Report_visit_adapter(Activity a, clickListener listener1) {
        super(EventData.DIFF_CALLBACK);
        activity = a;
        //   eventPresentsInDay = eventPresentsInDaya;
        listener = listener1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rcv_report_visit_adapter, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        EventData eventData = getItem(position);
        if (eventData == null) {
            return;
        }
        event_key = "" + eventData.getEvent_key();
        String customer_name = eventData.getCustomer_name();
        String event_date = eventData.getEvent_date();
        String status = eventData.getStatus();
        from_date_time = eventData.getFrom_date_time();
        visit_update_time = eventData.getVisit_update_time();
        if (status.equalsIgnoreCase("Visited")) {
            setVisitedMins();
        }
        //  Logger.LogError("event_key",""+event_key);
        // String endTime =
        // appointment.get(DSRTableCreate.COLOUMNS_EVENT_MASTER.to_date_time.name());
        String eventdate = "" + DateFormat.format(B2CAppConstant.dateFormat3,
                B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat2, "" + event_date));
        holder.txt_date.setText("" + eventdate);
        holder.txt_customer.setText("" + customer_name);
        holder.txt_status.setText("" + status);

        if (status.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Visited.name()))
            holder.txt_status.setTextColor(activity.getResources().getColor(R.color.very_dark_yellow));
        else if (status.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Cancelled.name()))
            holder.txt_status.setTextColor(Color.GRAY);
        else if (status.equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.CheckIn.name()))
            holder.txt_status.setTextColor(activity.getResources().getColor(R.color.checkin_color));
        else
            holder.txt_status.setTextColor(activity.getResources().getColor(R.color.veryDrakGray));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickLestener(position, getItem(position));
            }
        });


    }

    private void setVisitedMins() {
        long from_date_timeInt = B2CApp.b2cUtils.getTimestamp(B2CAppConstant.datetimeFormat1,
                "" + from_date_time);
        long visit_update_timeInt = B2CApp.b2cUtils.getTimestamp(B2CAppConstant.datetimeFormat1,
                "" + visit_update_time);

        Logger.LogError("from_date_timeInt", "" + from_date_timeInt);
        Logger.LogError("from_date_time", "" + from_date_time);
        Logger.LogError("visit_update_timeInt", "" + visit_update_timeInt);
        Logger.LogError("visit_update_time", "" + visit_update_time);

        long visitedSec = (visit_update_timeInt) - (from_date_timeInt);
        Logger.LogError("visitedSec", "" + visitedSec);
        ConvertSecondToHHMMString(visitedSec);
        Logger.LogError("ConvertSecondToHHMMString", "" + ConvertSecondToHHMMString(visitedSec));
    }

    private String ConvertSecondToHHMMString(long mills) {

        int sec = (int) (mills / 1000) % 60;
        int min = (int) ((mills / (1000 * 60)) % 60);
        int hr = (int) ((mills / (1000 * 60 * 60)) % 24);
        return hr + ":" + min + ":" + sec;


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_date;
        private TextView txt_customer;
        private TextView txt_status;
        private LinearLayout lt_all_visit;


        public ViewHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_customer = (TextView) itemView.findViewById(R.id.txt_customer);
            txt_status = (TextView) itemView.findViewById(R.id.txt_status);
            lt_all_visit = (LinearLayout) itemView.findViewById(R.id.lt_all_visit);


        }
    }

    private void gotoDSREditProgScreen() {
        activity.startActivity(new Intent(activity, DSREditProgScreen.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public interface clickListener {
        void onClickLestener(int position, EventData item);
    }
}
