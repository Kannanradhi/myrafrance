package com.isteer.b2c.adapter;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.model.OrderNewData;

import java.util.ArrayList;

public class RCV_PendingOrderScreenAdapter extends PagedListAdapter<OrderNewData, RCV_PendingOrderScreenAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<OrderNewData> data;
    private RCVAllPendingOrderScreenAdapter.ClickListener clickListener;

    public RCV_PendingOrderScreenAdapter(Activity a, RCVAllPendingOrderScreenAdapter.ClickListener clickListener1) {
        super(OrderNewData.DIFF_CALLBACK);
        clickListener = clickListener1;
        activity = a;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tab_row_pendingorders, parent, false);
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
        ArrayList<OrderNewData> orderNewDataArrayList = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().pendingorderdata_dao().fetchPendingOrderValue(customer_key);
        //  Cursor cCursor = B2CApp.b2cLdbs.fetchPendingOrderValue(activity, customer_key, contact_key);
        double orderValue = calculateOrderValue(orderNewDataArrayList);

        holder.tv_customername.setText("" + customer_name);
        holder.tv_area.setText("" + area_name);
        holder.tv_ordervalue.setText("" + B2CApp.b2cUtils.setGroupSeparaterWithZero(String.format("%.2f", orderValue)));

        holder.tv_action.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickListener.onclickListener(position, getItem(position));
                holder.itemView.setClickable(false);
/*Logger.LogError("tOrder.getCustomer_key()",""+tOrder.getCustomer_key());
Logger.LogError("tOrder.getCus_key()()",""+tOrder.getCus_key());
				*//*Log.e("cus name", " : " + tOrder.get("customer_name"));
				Log.e("cus key ", " : " + tOrder.get(B2CTableCreate.COLOUMNS_PENDING_ORDERS.customer_key.name()));
				Log.e("con key ", " : " + tOrder.get(B2CTableCreate.COLOUMNS_PENDING_ORDERS.contact_key.name()));*//*
B2CApp.b2cUtils.updateMaterialProgress(activity,B2CAppConstant.LOADING);
                B2CCounterDetailScreen.currentMap = tOrder;
                B2CCounterDetailScreen.toUpdateCounterDetail = true;
                gotoB2CCounterDetailScreen();*/
                //   ((B2CPendingOrderScreen)activity).viewPOActionClicked(tOrder);

            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_customername;
        public TextView tv_area;
        public TextView tv_ordervalue;
        public ImageView tv_action;
        public TableLayout maintbl;

        public ViewHolder(View convertView) {
            super(convertView);

            tv_customername = (TextView) convertView.findViewById(R.id.tv_customername);
            tv_area = (TextView) convertView.findViewById(R.id.tv_area);
            tv_ordervalue = (TextView) convertView.findViewById(R.id.tv_ordervalue);
            tv_action = (ImageView) convertView.findViewById(R.id.tv_anticipate);
            maintbl = (TableLayout) convertView.findViewById(R.id.maintbl);
        }
    }

    public double calculateOrderValue(ArrayList<OrderNewData> orderNewDataArrayList) {
        double orderValue = 0.00d;
        for (int i = 0; i < orderNewDataArrayList.size(); i++) {
            OrderNewData orderNewData = orderNewDataArrayList.get(i);
            if (orderNewData == null)
                return orderValue;


            String qtyStr = orderNewData.getPending_qty() != null ? orderNewData.getPending_qty() : "0";
            String lpStr = orderNewData.getList_price() != null ? orderNewData.getList_price() : "0.0";
            String tpStr = orderNewData.getTaxPercent() != null ? orderNewData.getTaxPercent() : "0.0";

            int qty = (Double.valueOf(qtyStr)).intValue();
            double lp = Double.parseDouble(lpStr);
            double tp = Double.parseDouble(tpStr);

            //    Log.e("calculateOrderValue", " pending_qty : " + qty + " -list_price : " + lp + " -taxPercent : " + tp);

            //  orderValue += qty * lp;
            //   Log.e("orderValue", " : " + orderValue);
            orderValue += qty * lp * ((100 + tp) / 100);

            //  Logger.LogError("orderValue", " : " + orderValue);


        }
        return orderValue;
    }

    private void gotoB2CCounterDetailScreen() {

        activity.startActivity(new Intent(activity, B2CCounterDetailScreen.class)
                .putExtra(B2CAppConstant.BACKTOCOUNTERDETAILS, B2CAppConstant.ACTIONORDER)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
