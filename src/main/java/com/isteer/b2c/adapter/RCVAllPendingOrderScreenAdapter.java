package com.isteer.b2c.adapter;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.model.OrderNewData;

import java.util.ArrayList;

public class RCVAllPendingOrderScreenAdapter extends PagedListAdapter<OrderNewData, RCVAllPendingOrderScreenAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<OrderNewData> data;
    private String qtyStr, orderqtyStr, dateStr, mi_nameStr;
    double lpStr;
    private ClickListener clickListener;
    private double tpStr;

    public RCVAllPendingOrderScreenAdapter(Activity a) {
        super(OrderNewData.DIFF_CALLBACK);
        activity = a;

        clickListener = (ClickListener) activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tab_row_all_pendingorders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OrderNewData tOrder = getItem(position);
        if (tOrder == null) {
            return;
        }
        String customer_name = tOrder.getCompany_name();
        String area_name = tOrder.getArea_name();
        String customer_key = tOrder.getCustomer_key();
        String contact_key = tOrder.getContact_key();

        String mi_name = tOrder.getMi_name();
        qtyStr = tOrder.getPending_qty();
        orderqtyStr = tOrder.getOrdered_qty();
        lpStr = Double.parseDouble(tOrder.getList_price());
        tpStr = Double.parseDouble(tOrder.getTaxPercent());
        dateStr = tOrder.getDate();
        mi_nameStr = tOrder.getMi_name();

        //Cursor cCursor = B2CApp.b2cLdbs.fetchPendingOrderValue(activity, customer_key, contact_key);
        //calculateOrderValue(cCursor);
        final double priceAT = (double) (lpStr * (100 + tpStr) / 100);
        String formattedPAT = String.format("%.2f", priceAT);
        holder.tv_value.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero("" + formattedPAT));
        holder.tv_customername.setText("" + customer_name);
        holder.tv_area.setText("" + mi_name);
        // holder.tv_value.setText("" + B2CApp.b2cUtils.setGroupSeparaterWithZero(String.format("%.2f", Double.parseDouble(lpStr))));
        holder.tv_ordervalue.setText("" + Double.valueOf(qtyStr).intValue());
        try {
            if (dateStr.length() > 5) {

                holder.tv_date.setText(B2CApp.b2cUtils.getFormattedDate("dd-MM-yyyy",
                        B2CApp.b2cUtils.getTimestamp("yyyyMMdd", dateStr)));
            } else {
                holder.tv_date.setText("-");
            }
        } catch (Exception e) {
            holder.tv_date.setText("-");
            e.printStackTrace();
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickListener.onclickListener(position, getItem(position));
                holder.itemView.setClickable(false);


                //  ((B2CAllOrderScreen) activity).viewPOActionClicked(tOrder);

            }
        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_customername;
        private TextView tv_area;
        private TextView tv_ordervalue;
        private ImageView tv_action;
        private TextView tv_date;
        private TextView tv_value;
        private LinearLayout lt_report_order;

        public ViewHolder(View convertView) {
            super(convertView);
            tv_customername = convertView.findViewById(R.id.tv_customername);
            tv_area = convertView.findViewById(R.id.tv_area);
            tv_ordervalue = convertView.findViewById(R.id.tv_ordervalue);
            tv_action = (ImageView) convertView.findViewById(R.id.tv_anticipate);
            tv_date = convertView.findViewById(R.id.tv_date);
            tv_value = convertView.findViewById(R.id.tv_value);
            lt_report_order = convertView.findViewById(R.id.lt_report_order);
        }
    }

    private void gotoB2CCounterDetailScreen() {

        activity.startActivity(new Intent(activity, B2CCounterDetailScreen.class)
                .putExtra(B2CAppConstant.BACKTOCOUNTERDETAILS, B2CAppConstant.REPORTORDER)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public interface ClickListener {
        void onclickListener(int position, OrderNewData item);
    }
}
