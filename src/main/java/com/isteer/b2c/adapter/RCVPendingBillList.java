package com.isteer.b2c.adapter;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.model.BillData;

import java.util.Date;

/**
 * Created by rnows on 01-Mar-18.
 */

public class RCVPendingBillList extends PagedListAdapter<BillData, RCVPendingBillList.ViewHolder> {
    private Activity activity;
    // private ArrayList<BillData> data;
    private static double[] cum_balance;
    private Typeface raleway_bold, raleway;
    private String invoice_Date;
    private long invoiceDateBalance;

    public RCVPendingBillList(Activity a) {
        super(BillData.DIFF_CALLBACK);
        activity = a;
        // data = d;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        View contactView = inflater.inflate(R.layout.tab_row_cd_one, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        BillData tBill = getItem(position);
        if (tBill == null) {
            return;
        }
        cum_balance = new double[getItemCount()];
        initCumBalance();
        setAvailableCreditLimit();
        if (position == getItemCount() - 1)
            setAvailableCreditLimit();
        //  Logger.LogError("tBill.getInv_date()",""+tBill.getInv_date());
        //  Logger.LogError("B2CApp.b2cUtils.getTimestamp",""+B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat2, ""+tBill.getInv_date()));
        //holder.pb_tv_date.setText(Html.fromHtml("<font color=\"#ff0000\">"+ 23 +"</font>" + "\n" + "<font color=\"#00ff00\">"+ 00 +"</font>"));
        if (tBill.getInv_date().contains("-")) {
            invoice_Date = B2CApp.b2cUtils.getFormattedDate("dd-MM", B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat4, tBill.getInv_date()));
        } else {
            invoice_Date = B2CApp.b2cUtils.getFormattedDate("dd-MM", B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat2, tBill.getInv_date()));
        }
        holder.pb_tv_date.setText(invoice_Date);
        holder.pb_tv_dayscount.setText(Html.fromHtml("<b><font color=\"#ff0000\">" + calculateAging(tBill.getInv_date()) + "d" + "</font></b>"));
        holder.pb_tv_no.setText(tBill.getInvoice_no());
        holder.pb_tv_amount.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + tBill.getInvoice_amount()));
        holder.pb_tv_balance.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + tBill.getPending_amount()));
        holder.pb_tv_cum.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + cum_balance[position]));
        holder.pb_tv_action.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ((B2CCounterDetailScreen) activity).collectionEntryClicked(true, "" + holder.pb_tv_cum.getText());

            }
        });
       /* if (position % 2 == 1) {
            holder.maintbl.setBackground(new ColorDrawable(activity.getResources().getColor(R.color.very_light_gray)));
        } else {
            holder.maintbl.setBackground(new ColorDrawable(activity.getResources().getColor(R.color.White)));

        }*/
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pb_tv_date;
        private TextView pb_tv_no;
        private TextView pb_tv_amount;
        private TextView pb_tv_balance;
        private TextView pb_tv_cum;
        private TextView pb_tv_dayscount;
        private TextView pb_tv_action;
        private TableLayout maintbl;

        public ViewHolder(View itemView) {
            super(itemView);
            maintbl = itemView.findViewById(R.id.maintbl);
            pb_tv_date = itemView.findViewById(R.id.pb_tv_date);
            pb_tv_no = itemView.findViewById(R.id.pb_tv_no);
            pb_tv_amount = itemView.findViewById(R.id.pb_tv_amount);
            pb_tv_balance = itemView.findViewById(R.id.pb_tv_balance);
            pb_tv_cum = itemView.findViewById(R.id.pb_tv_cum);
            pb_tv_dayscount = itemView.findViewById(R.id.pb_tv_dayscount);
            pb_tv_action = itemView.findViewById(R.id.pb_tv_action);


        }

    }

    private void setAvailableCreditLimit() {
        if (B2CCounterDetailScreen.currentCustomer.getMax_credit_limit() != null) {
            double maxCredit = Double.parseDouble(B2CCounterDetailScreen.currentCustomer.getMax_credit_limit()), usedCredit;
            if (getItemCount() == 0)
                usedCredit = 0.00d;
            else
                usedCredit = cum_balance[getItemCount() - 1];
            String availableCredit = "" + (maxCredit - usedCredit);
            ((B2CCounterDetailScreen) activity).tv_avail_credit.setText(B2CApp.b2cUtils.setGroupSeparaterWithoutDecimal("" + availableCredit));
            B2CCounterDetailScreen.currentCustomer.setAvailable_credit(availableCredit);
        }
    }

    private int calculateAging(String invoice_date) {

        int datesAging = 0;

        if (invoice_date.contains("-")) {
            invoiceDateBalance = B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat4, invoice_date);
        } else {
            invoiceDateBalance = B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat2, invoice_date);
        }
        long invoiceTimestamp = invoiceDateBalance;
        long currentTimestamp = new Date().getTime();

        datesAging = (int) ((currentTimestamp - invoiceTimestamp) / (24 * 60 * 60 * 1000));

        return datesAging;
    }

    private void initCumBalance() {

        double current_balance = 0.00d;
        double cumulative_balance = 0.00d;

        for (int i = 0; i < getItemCount(); i++) {
            current_balance = Double.parseDouble("" + String.format("%.2f", Double.parseDouble(getItem(i).getPending_amount())));
            cumulative_balance += current_balance;
            cum_balance[i] = cumulative_balance;
        }
    }
}
