package com.isteer.b2c.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.databinding.ListrowPlaceOrderBinding;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.counter_details.B2CPlaceOrderScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rnows on 03-Apr-18.
 */

public class RCV_B2CPlaceOrderAdaptor extends PagedListAdapter<ProductData, RCV_B2CPlaceOrderAdaptor.ViewHolder> {
    private Activity activity;
    private ArrayList<ProductData> data;
    private ArrayList<Long> miKeyList = new ArrayList<>();
    private static LayoutInflater inflater = null;
    public static boolean[] isOrderPlaced;

    public RCV_B2CPlaceOrderAdaptor(Activity a) {
        super(ProductData.DIFF_CALLBACK);
        activity = a;
        //data=d;
        // isOrderPlaced = new boolean[d.size()];

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        // View view = inflater.inflate(R.layout.listrow_place_order,parent,false);
        ListrowPlaceOrderBinding binding = DataBindingUtil.inflate(inflater, R.layout.listrow_place_order, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProductData prod = getItem(position);
        if (prod == null) {
            return;
        } else {
            holder.bindTo(prod, position);

        }
        //  Logger.LogError("getItemCount()",""+getItemCount());

        isOrderPlaced = new boolean[getItemCount()];
        //   Logger.LogError("currentProduct",""+miKeyList);
        //   Logger.LogError("prod.getMikey()",""+prod.getMikey());
        // if(isOrderPlaced[position])]


    }

    private void alertUserP(String s, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message).setTitle(s).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ListrowPlaceOrderBinding item;

        public ViewHolder(ListrowPlaceOrderBinding itemView) {
            super(itemView.getRoot());

            item = itemView;


        }

        public void bindTo(ProductData prod, int position) {

            item.setProduct(prod);

            Logger.LogError("beforeqty", prod.getQty());

            if (miKeyList.contains(prod.getMikey())) {
                item.ivAddOrder.setImageResource(R.drawable.ic_check_circle_black_24dp);
            } else {
                item.ivAddOrder.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            }


            item.listrPoTvName.setText(prod.getDisplay_code() + "\n" + prod.getRemark() + " (" + prod.getManu_name() + ")");
            String getMRP = prod.getMrp_rate() == 0 ? "-" : "" + prod.getMrp_rate();
            item.listrPoTvMrp.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero(getMRP));

            final double priceAT = (double) (prod.getList_price() * (100 + prod.getTaxPercent()) / 100);
            String formattedPAT = String.format("%.2f", priceAT);
            final double priceDAT = Double.parseDouble(formattedPAT);
            item.listrPoTvNetprice.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero(prod.getList_price() == 0 ? "-" : "" + formattedPAT));

            item.ivAddOrder.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int qty = Integer.parseInt("0" + item.listrPoEtQty.getText());

                    if (qty > 0) {
                        final PendingOrderData tOrder = new PendingOrderData();
                        tOrder.setMi_key("" + prod.getMikey());
                        tOrder.setOrdered_qty("" + qty);
                        tOrder.setPending_qty("" + qty);
                        tOrder.setMi_name("" + prod.getDisplay_code() + " " + prod.getRemark());
                        tOrder.setTax_percent("" + prod.getTaxPercent());
                        tOrder.setEntered_on("" + DateFormat.format(B2CAppConstant.datetimeFormat, new Date().getTime()));

/*					if(!B2CApp.b2cUtils.isNetAvailable())
					{
						((B2CPlaceOrderScreen)activity).alertUserP(activity, "Error", "No internet connectivity to place order", "OK");
						return;
					}*/
                        //   Logger.LogError("currentProduct",""+miKeyList);
                        //  Logger.LogError("prod.getMikey()",""+prod.getMikey());

                        final double orderValue = qty * priceDAT;

                        // if(isOrderPlaced[position])
                        if (miKeyList.contains(prod.getMikey())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    activity);
                            builder.setMessage(
                                    "You have already ordered this product. Are you sure to order it again ?")
                                    .setTitle("Alert!")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                    dialog.cancel();
                                                    ((B2CPlaceOrderScreen) activity).onOrderClicked(tOrder, item.ivAddOrder, position, orderValue);
                                                }
                                            })
                                    .setNegativeButton("No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                    dialog.cancel();
                                                    miKeyList.remove(prod.getMikey());
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            String message = "Are you sure to order this product ?";

                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(message).setTitle("Alert !").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    ((B2CPlaceOrderScreen) activity).onOrderClicked(tOrder, item.ivAddOrder, position, orderValue);
                                    getItem(position).setQty(item.listrPoEtQty.getText().toString());
                                    miKeyList.add(prod.getMikey());

                                    Logger.LogError("afterqty", getItem(position).getQty());
                                    notifyDataSetChanged();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }


                    } else {
                        item.listrPoEtQty.requestFocus();
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        Toast.makeText(activity, "Enter a valid quantity", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            item.ivCheckStock.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    checkStock(prod, position);
                }
            });

            if (B2CApp.b2cUtils.isNetAvailable())
                item.ivCheckStock.setBackgroundResource(R.drawable.round_darkblue);
            else
                item.ivCheckStock.setBackgroundResource(R.drawable.round_lightblue);

        }

    }

    private void checkStock(ProductData prod, int position) {

        if (B2CApp.b2cUtils.isNetAvailable()) {
            AlertPopupDialog.dialogDismiss();
            final PendingOrderData tOrder = new PendingOrderData();
            tOrder.setMi_key("" + prod.getMikey());
            tOrder.setMi_name("" + prod.getDisplay_code() + " " + prod.getRemark());

            ((B2CPlaceOrderScreen) activity).onCheckStockClicked(tOrder, position);
        } else {
            String message = activity.getResources().getString(R.string.internetForlocation);
            AlertPopupDialog.noInternetAlert(activity, message, new AlertPopupDialog.myOnClickListenerRight() {
                @Override
                public void onButtonClickRight() {
                    if (B2CApp.b2cUtils.isNetAvailable()) {
                        AlertPopupDialog.dialogDismiss();
                        final PendingOrderData tOrder = new PendingOrderData();
                        tOrder.setMi_key("" + prod.getMikey());
                        tOrder.setMi_name("" + prod.getDisplay_code() + " " + prod.getRemark());

                        ((B2CPlaceOrderScreen) activity).onCheckStockClicked(tOrder, position);
                    }
                }
            });
            //  alertUserP(activity, "Alert !", "No Internet Available", "OK");
        }
    }

    public static boolean contains(int[] arr, int item) {
        for (int n : arr) {
            if (item == n) {
                return true;
            }
        }
        return false;
    }
}
