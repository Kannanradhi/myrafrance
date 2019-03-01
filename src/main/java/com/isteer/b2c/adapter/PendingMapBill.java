package com.isteer.b2c.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.model.BillData;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.InputFilterMinMax;
import com.isteer.b2c.utility.Logger;

import java.util.ArrayList;

/**
 * Created by rnows on 01-Mar-18.
 */

public class PendingMapBill extends RecyclerView.Adapter<PendingMapBill.ViewHolder> {
    private Activity activity;
    private ArrayList<BillData> data;
    private static double[] cum_balance;
    private Typeface raleway_bold, raleway;
    private String invoice_Date;
    private long invoiceDateBalance;
    private double coll_balance_amount;
    private onclickInterface onclick;
    private int inv_Balance_amount;
    private double int_Map_amt;
    private int finalMax_amount;

    public PendingMapBill(Activity a, ArrayList<BillData> d, double amount, PendingMapBill.onclickInterface onclick1) {

        activity = a;
        data = d;
        coll_balance_amount = amount;
        onclick = onclick1;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        View contactView = inflater.inflate(R.layout.pending_mapbill_adapter, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        BillData tBill = data.get(position);
        if (tBill == null) {
            return;
        }
        if (position == getItemCount() - 1)

            Logger.LogError("tBill.getInv_date()", "" + tBill.getInv_date());
        holder.pb_tv_date.setText(""+tBill.getInv_date());
        holder.pb_tv_no.setText(tBill.getInvoice_no());
        holder.pb_tv_amount.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero("" + tBill.getInvoice_amount()));
        holder.pb_tv_cum.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero("" + tBill.getPending_amount()));
        int inv_pending_amt = Double.valueOf(tBill.getPending_amount()).intValue();

        holder.pb_tv_balance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                holder.pb_tv_balance.removeTextChangedListener(this);
             //   Logger.LogError("finalMax_amount", "" + finalMax_amount);
                if (finalMax_amount > 0) {
                    holder.pb_tv_balance.setFilters(new InputFilter[]{new InputFilterMinMax(0, finalMax_amount)});

                } else {
                    holder.pb_tv_balance.setFocusable(false);
                    holder.pb_tv_balance.getText().clear();
                    return;
                }

                holder.pb_tv_balance.addTextChangedListener(this);

                if (editable.length() != 0) {
                    if (finalMax_amount >= Integer.parseInt(""+editable))
                    calculateBalanceAmount(inv_pending_amt, editable, holder);
                    else
                        editable.clear();
                } else {
                    setColBalAmt();
                }

            }
        });
        if (position == 0) {
            holder.pb_tv_balance.setFocusable(true);
            holder.iv_mapbill.setFocusable(true);
            holder.iv_mapbill.setClickable(true);
            holder.iv_mapbill.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);

            int coll_bal_amt = Double.valueOf(coll_balance_amount).intValue();
            setFinalMax_amount(inv_pending_amt, coll_bal_amt);
        } else {
            holder.pb_tv_balance.setFocusable(false);
            holder.iv_mapbill.setFocusable(false);
            holder.iv_mapbill.setFocusableInTouchMode(false);
            holder.iv_mapbill.setImageResource(R.drawable.ic_add_circle_outline_lightgray);
        }
        holder.iv_mapbill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (B2CApp.b2cUtils.isNetAvailable()) {
                    if (!holder.pb_tv_balance.getText().toString().isEmpty()) {
                        int maped_amount = Integer.parseInt(holder.pb_tv_balance.getText().toString());
                        if (maped_amount > 0) {
                            tBill.setPending_amount("" + inv_Balance_amount);
                            tBill.setMap_bill_amount("" + holder.pb_tv_balance.getText().toString());
                            onclick.onclick(tBill,holder);
                        } else {
                            alertUserP(activity, "Alert !", "please enter the map amount ", "OK");
                        }
                    } else {
                        alertUserP(activity, "Alert !", "please enter the map amount ", "OK");
                    }
                } else {
                    String message = activity.getResources().getString(R.string.nointernetdescription);
                    AlertPopupDialog.noInternetAlert(activity, message, new AlertPopupDialog.myOnClickListenerRight() {
                        @Override
                        public void onButtonClickRight() {
                            if (B2CApp.b2cUtils.isNetAvailable()) {
                                AlertPopupDialog.dialogDismiss();

                                if (!holder.pb_tv_balance.getText().toString().isEmpty()) {
                                    int maped_amount = Integer.parseInt(holder.pb_tv_balance.getText().toString());
                                    if (maped_amount > 0) {
                                        tBill.setPending_amount("" + inv_Balance_amount);
                                        tBill.setMap_bill_amount("" + holder.pb_tv_balance.getText().toString());
                                      //  Logger.LogError("holder.pb_tv_balance.getText().toString()", "" + holder.pb_tv_balance.getText().toString());
                                     //   Logger.LogError("tBill.getMap_bill_amount()", "" + tBill.getMap_bill_amount());
                                        onclick.onclick(tBill, holder);
                                    } else {
                                        alertUserP(activity, "Alert !", "please enter the map amount ", "OK");
                                    }
                                } else
                                    alertUserP(activity, "Alert !", "please enter the map amount ", "OK");
                            }
                        }
                    });


                }
            }
        });
    }

    private void setFinalMax_amount(int inv_pending_amt, int coll_bal_amt) {


        int max_amount = inv_pending_amt > coll_bal_amt ? coll_bal_amt : inv_pending_amt;
        max_amount = max_amount < 1 ? 0 : max_amount;
    //  Logger.LogError("max_amount", "" + max_amount);
        finalMax_amount = max_amount;
      //  Logger.LogError("finalMax_amount", "" + finalMax_amount);
    }

    private void calculateBalanceAmount(int inv_pending_amt, Editable editable, ViewHolder holder) {

       // Logger.LogError("inv_pending_amt", "" + inv_pending_amt);
      //  Logger.LogError("editable", "" + editable);
        inv_Balance_amount = inv_pending_amt - Integer.parseInt(editable.toString());
        int_Map_amt = (coll_balance_amount - Double.parseDouble(editable.toString()));

       // Logger.LogError("coll_balance_amount", "" + coll_balance_amount);
      //  Logger.LogError("inv_Balance_amount", "" + inv_Balance_amount);
      //  Logger.LogError("int_Map_amt", "" + int_Map_amt);
        onclick.onEdittextclick(int_Map_amt);
    }

    private void setColBalAmt() {

        double int_Map_amt = coll_balance_amount - 0;
      //  Logger.LogError("coll_balance_amount", "" + coll_balance_amount);
     //   Logger.LogError("inv_Balance_amount", "" + inv_Balance_amount);
     //   Logger.LogError("int_Map_amt", "" + int_Map_amt);
        onclick.onEdittextclick(int_Map_amt);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pb_tv_date;
        private TextView pb_tv_no;
        private TextView pb_tv_amount;
        public EditText pb_tv_balance;
        private TextView pb_tv_cum;
        public ImageView iv_mapbill;

        public ViewHolder(View itemView) {
            super(itemView);
            pb_tv_date = itemView.findViewById(R.id.pb_tv_date);
            pb_tv_no = itemView.findViewById(R.id.pb_tv_no);
            pb_tv_amount = itemView.findViewById(R.id.pb_tv_amount);
            pb_tv_balance = itemView.findViewById(R.id.pb_tv_balance);
            pb_tv_cum = itemView.findViewById(R.id.pb_tv_cum);
            iv_mapbill = itemView.findViewById(R.id.iv_mapbill);



        }

    }

    public interface onclickInterface {
        void onclick(BillData billData, ViewHolder holder);

        void onEdittextclick(double map_amount);
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


}
