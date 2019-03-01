package com.isteer.b2c.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.model.OrderData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.utility.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rnows on 31-Mar-18.
 */

public class RCV_DSRCounterAdapter extends RecyclerView.Adapter<RCV_DSRCounterAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<OrderNewData> data;

    public RCV_DSRCounterAdapter(Activity a, ArrayList<OrderNewData> d) {

        activity = a;
        data = d;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.listrow_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        OrderNewData customer = data.get(position);

        Logger.LogError("customer.getCustomer_name()", "" + data.get(position).getCustomer_name());
        Logger.LogError("customer.getArea()", "" + data.get(position).getArea());
        String cust = customer.getCustomer_name() + " - " +
                customer.getArea();
        holder.customer_entry.setText(cust);
        Logger.LogError("customer.getOrdered_count_today()", "" + customer.getOrdered_count_today());
        String ordered_count = customer.getOrdered_count_today() == null ? "0" : customer.getOrdered_count_today();
        if (ordered_count == null || ordered_count.equalsIgnoreCase("null") || ordered_count.isEmpty())
            ordered_count = "0";
        Logger.LogError("ordered_count", "" + ordered_count);
        int ordered_count_today = Integer.parseInt(ordered_count);

        if (customer.getStatus().equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.CheckIn.name())) {
            holder.customer_entry.setTextColor(activity.getResources().getColor(R.color.checkin_color));
        } else if (ordered_count_today > 0) {
            holder.customer_entry.setTextColor(activity.getResources().getColor(R.color.green));
        } else {
            if (customer.getStatus().equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Visited.name()))
                holder.customer_entry.setTextColor(activity.getResources().getColor(R.color.very_dark_yellow));
            else if (customer.getStatus().equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.Cancelled.name()))
                holder.customer_entry.setTextColor(Color.GRAY);
            else if (customer.getStatus().equalsIgnoreCase(DSRAppConstant.EVENT_STATUS.CheckIn.name()))
                holder.customer_entry.setTextColor(activity.getResources().getColor(R.color.checkin_color));
            else
                holder.customer_entry.setTextColor(activity.getResources().getColor(R.color.veryDrakGray));

           /* if (B2CApp.b2cPreference.getCHECKEDINCUSKEY() == Integer.parseInt(customer.getCus_key())) {
                holder.customer_entry.setTextColor(activity.getResources().getColor(R.color.darkblue));
            }*/
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                B2CCounterDetailScreen.currentMap = data.get(position);
                B2CCounterDetailScreen.toUpdateCounterDetail = true;
                gotoB2CCounterDetailScreen();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView customer_entry;

        public ViewHolder(View itemView) {
            super(itemView);
            customer_entry = (TextView) itemView.findViewById(R.id.customer_entry);
        }
    }

    private void gotoB2CCounterDetailScreen() {

        activity.startActivity(new Intent(activity, B2CCounterDetailScreen.class)
                .putExtra(B2CAppConstant.BACKTOCOUNTERDETAILS, B2CAppConstant.ADDTOBEATPLAN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
