package com.isteer.b2c.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.reports.B2CInvoiceMapBill;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.Logger;

import java.util.ArrayList;

public class RCVUnmappedCollectionAdapter extends RecyclerView.Adapter<RCVUnmappedCollectionAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<CollectionData> data;
    private int fromActivity;
    private String pay_mode;

    public RCVUnmappedCollectionAdapter(Activity a, ArrayList<CollectionData> d) {

        activity = a;
        data = d;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.tab_row_collection_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CollectionData tCollection = data.get(position);
        holder.cs_tv_customer.setText(tCollection.getCustomer_name());
        holder.cs_tv_date.setText("" + DateFormat.format(B2CAppConstant.dateFormat3,
                B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat2, tCollection.getTrans_date())));
        holder.cs_tv_paymode.setText(tCollection.getPAYMENT_TYPE());
        holder.cs_tv_amount.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero(tCollection.getAmount()));

        holder.cs_tv_status.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero("" + tCollection.getBalance_amount()));
        holder.lt_bill_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (B2CApp.b2cUtils.isNetAvailable()) {
                    B2CInvoiceMapBill.collectionData = tCollection;
                    gotoB2CInvoiceMapBill();
                }else{
                    String message = activity.getResources().getString(R.string.nointernetdescription);
                    AlertPopupDialog.noInternetAlert(activity, message, new AlertPopupDialog.myOnClickListenerRight() {
                        @Override
                        public void onButtonClickRight() {
                            if (B2CApp.b2cUtils.isNetAvailable()) {
                                AlertPopupDialog.dialogDismiss();
                                B2CInvoiceMapBill.collectionData = tCollection;
                                gotoB2CInvoiceMapBill();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cs_tv_customer;
        public TextView cs_tv_date;
        public TextView cs_tv_paymode;
        public TextView cs_tv_amount;
        public TextView cs_tv_status;
        public LinearLayout lt_bill_map;

        public ViewHolder(View itemView) {
            super(itemView);
            cs_tv_customer = (TextView) itemView.findViewById(R.id.cs_tv_customer);
            cs_tv_date = (TextView) itemView.findViewById(R.id.cs_tv_date);
            cs_tv_paymode = (TextView) itemView.findViewById(R.id.cs_tv_paymode);
            cs_tv_amount = (TextView) itemView.findViewById(R.id.cs_tv_amount);
            cs_tv_status = (TextView) itemView.findViewById(R.id.cs_tv_status);
            lt_bill_map = itemView.findViewById(R.id.lt_bill_map);

        }
    }

    private void gotoB2CInvoiceMapBill() {
        activity.startActivity(new Intent(activity, B2CInvoiceMapBill.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }
}
