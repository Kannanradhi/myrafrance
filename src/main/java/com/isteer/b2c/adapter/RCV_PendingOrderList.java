package com.isteer.b2c.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
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
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;
import com.isteer.b2c.utility.AlertPopupDialog;

import java.util.ArrayList;

/**
 * Created by rnows on 01-Mar-18.
 */

public class RCV_PendingOrderList extends PagedListAdapter<PendingOrderData,RCV_PendingOrderList.ViewHolder> {
    private Activity activity;
    private ArrayList<PendingOrderData> data;

    public RCV_PendingOrderList(Activity a) {
        super(PendingOrderData.DIFF_CALLBACK);

        activity = a;
      //  data = d;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tab_row_cd_two, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PendingOrderData tOrder = getItem(position);
if (tOrder == null){
    return;
}
        holder.po_tv_date.setText(B2CApp.b2cUtils.getFormattedDate("dd-MM-yyyy", B2CApp.b2cUtils.getTimestamp("yyyyMMdd", tOrder.getDate())));
        holder.po_tv_partno.setText(tOrder.getMi_name());
        holder.po_tv_qty.setText(""+Double.valueOf(tOrder.getPending_qty()).intValue());

        holder.btn_po_action.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ((B2CCounterDetailScreen) activity).viewPODetailClicked(tOrder);
            }
        });

        holder.btn_po_checkstock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkStock(tOrder,position);
               /* if (B2CApp.b2cUtils.isNetAvailable()) {
                    ((B2CCounterDetailScreen) activity).onCheckStockClicked(tOrder, position);
                }else {
                    alertUserP(activity, "Alert !", "No Internet Available", "OK");
                }*/
            }
        });
        if (B2CApp.b2cUtils.isNetAvailable())
            holder.btn_po_checkstock.setBackgroundResource(R.drawable.round_darkblue);
        else
            holder.btn_po_checkstock.setBackgroundResource(R.drawable.round_lightblue);

    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView po_tv_date;
        private TextView po_tv_partno;
        private TextView po_tv_qty;
        private ImageView btn_po_action;
        private ImageView btn_po_checkstock;
        private LinearLayout lt_pending_order;


        public ViewHolder(View itemView) {
            super(itemView);
            lt_pending_order = (LinearLayout) itemView.findViewById(R.id.lt_pending_order);
            po_tv_date = (TextView) itemView.findViewById(R.id.po_tv_date);
            po_tv_partno = (TextView) itemView.findViewById(R.id.po_tv_partno);
            po_tv_qty = (TextView) itemView.findViewById(R.id.po_tv_qty);
            btn_po_action = (ImageView) itemView.findViewById(R.id.btn_po_action);
            btn_po_checkstock = (ImageView) itemView.findViewById(R.id.btn_po_checkstock);


        }
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
    private void checkStock(PendingOrderData tOrder, int position) {

        if(B2CApp.b2cUtils.isNetAvailable())
        {
            AlertPopupDialog.dialogDismiss();
            ((B2CCounterDetailScreen) activity).onCheckStockClicked(tOrder, position);
        }else {
            String message = activity.getResources().getString(R.string.internetForlocation);
            AlertPopupDialog.noInternetAlert(activity, message, new AlertPopupDialog.myOnClickListenerRight() {
                @Override
                public void onButtonClickRight() {
                    if(B2CApp.b2cUtils.isNetAvailable())
                    {
                        AlertPopupDialog.dialogDismiss();
                        ((B2CCounterDetailScreen) activity).onCheckStockClicked(tOrder, position);
                    }
                }
            });
            //  alertUserP(activity, "Alert !", "No Internet Available", "OK");
        }
    }
}
