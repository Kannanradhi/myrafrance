package com.isteer.b2c.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.counter_details.B2CPlaceOrderScreen;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;

import java.util.ArrayList;

/**
 * Created by rnows on 03-Apr-18.
 */

public class RCV_B2CPlaceOrderAdaptorTest extends RecyclerView.Adapter<RCV_B2CPlaceOrderAdaptorTest.ViewHolder> {
    private Activity activity;
    private ArrayList<ProductData> data;
    private static LayoutInflater inflater=null;
    public static boolean[] isOrderPlaced;

    public RCV_B2CPlaceOrderAdaptorTest(Activity a, ArrayList<ProductData> d) {

        activity = a;
        data=d;
        isOrderPlaced = new boolean[d.size()];

    }


   @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.listrow_place_order,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(isOrderPlaced[position])
        {
            holder.iv_add_order.setImageResource(R.drawable.ic_check_circle_black_24dp);
        }
        else
        {
            holder.iv_add_order.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
        }

        final ProductData prod = data.get(position);

        holder.listr_po_tv_name.setText(prod.getDisplay_code()+ "\n" +prod.getRemark()+ " (" + prod.getManu_name() + ")");
        holder.listr_po_tv_mrp.setText(""+String.format( "%.2f", prod.getMrp_rate()));
        holder.listr_po_et_qty.setText("");

        final double priceAT = (double)(prod.getList_price()*(100+prod.getTaxPercent())/100);
        String formattedPAT = String.format( "%.2f", priceAT);
        final double priceDAT = Double.parseDouble(formattedPAT);
        holder.listr_po_tv_netprice.setText(""+formattedPAT);

        holder.iv_add_order.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int qty = Integer.parseInt("0"+holder.listr_po_et_qty.getText());

                if(qty>0)
                {
                    final PendingOrderData tOrder = new PendingOrderData();
                    tOrder.setMi_key(""+prod.getMikey());
                    tOrder.setOrdered_qty(""+qty);
                    tOrder.setPending_qty(""+qty);
                    tOrder.setMi_name(""+prod.getDisplay_code()+" "+prod.getRemark());
                    tOrder.setTax_percent(""+prod.getTaxPercent());

/*					if(!B2CApp.b2cUtils.isNetAvailable())
					{
						((B2CPlaceOrderScreen)activity).alertUserP(activity, "Error", "No internet connectivity to place order", "OK");
						return;
					}*/

                    final double orderValue = qty * priceDAT;

                    if(isOrderPlaced[position])
                    {
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
                                                ((B2CPlaceOrderScreen)activity).onOrderClicked(tOrder, holder.iv_add_order, position, orderValue);
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else
                        ((B2CPlaceOrderScreen)activity).onOrderClicked(tOrder, holder.iv_add_order, position, orderValue);
                }
                else {
                    holder.listr_po_et_qty.requestFocus();

                    Toast.makeText(activity, "Enter a valid quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.iv_check_stock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(B2CApp.b2cUtils.isNetAvailable())
                {
                    final PendingOrderData tOrder = new PendingOrderData();
                    tOrder.setMi_key(""+prod.getMikey());
                    tOrder.setMi_name(""+prod.getDisplay_code()+" "+prod.getRemark());

                    ((B2CPlaceOrderScreen)activity).onCheckStockClicked(tOrder, position);
                }else {
                    alertUserP(activity, "Alert !", "No Internet Available", "OK");
                }
            }
        });

        if(B2CApp.b2cUtils.isNetAvailable())
            holder.iv_check_stock.setBackgroundResource(R.drawable.round_darkblue);
        else
            holder.iv_check_stock.setBackgroundResource(R.drawable.round_lightblue);

    }

    private void alertUserP(Activity activity, String s, String no_internet_available, String ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(no_internet_available).setTitle(s).setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView listr_po_tv_name;
        public EditText listr_po_et_qty;
        public TextView listr_po_tv_mrp;
        private TextView listr_po_tv_netprice;
        private ImageView iv_check_stock;
        public ImageView iv_add_order;
        public ViewHolder(View itemView) {
            super(itemView);

            listr_po_tv_name = (TextView)itemView.findViewById(R.id.listr_po_tv_name);
            listr_po_et_qty = (EditText)itemView.findViewById(R.id.listr_po_et_qty);
            listr_po_tv_mrp =(TextView)itemView.findViewById(R.id.listr_po_tv_mrp);
            listr_po_tv_netprice =(TextView)itemView.findViewById(R.id.listr_po_tv_netprice);
            iv_check_stock =(ImageView)itemView.findViewById(R.id.iv_check_stock);
            iv_add_order =(ImageView) itemView.findViewById(R.id.iv_add_order);
        }
    }
}
