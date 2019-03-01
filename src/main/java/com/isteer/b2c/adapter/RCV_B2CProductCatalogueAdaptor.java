package com.isteer.b2c.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.paging.PagedListAdapter;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;
import com.isteer.b2c.utility.AlertPopupDialog;
import com.isteer.b2c.utility.ValidationUtils;

import java.util.List;

/**
 * Created by rnows on 31-Mar-18.
 */

public class RCV_B2CProductCatalogueAdaptor extends PagedListAdapter<ProductData, RCV_B2CProductCatalogueAdaptor.ViewHolder> {
    private Activity activity;
    LayoutInflater inflater;

    List<ProductData> productData;

    public RCV_B2CProductCatalogueAdaptor(Activity a) {
        super(ProductData.DIFF_CALLBACK);
        activity = a;

    }
  /*  public RCV_B2CProductCatalogueAdaptor(Activity a) {

        inflater = LayoutInflater.from(a);
    }*/


    /*public void setProductData(List<ProductData> productDataList) {
        productData = productDataList;
        notifyDataSetChanged();
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.listrow_catalogue, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //  final ProductData prod = productData.get(position);
        if (holder == null) {
            return;
        }


        final ProductData prod = getItem(position);
        if (prod == null) {
            return;
        }
        holder.listr_po_tv_name.setText(ValidationUtils.stringFormater(prod.getDisplay_code()) + "\n" + prod.getRemark() + " (" + prod.getManu_name() + ")");
        holder.listr_po_tv_mrp.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero("" + prod.getMrp_rate()));

        final double priceAT = (double) (prod.getList_price() * (100 + prod.getTaxPercent()) / 100);
        String formattedPAT = String.format("%.2f", priceAT);
        final double priceDAT = Double.parseDouble(formattedPAT);
        holder.listr_po_tv_netprice.setText(B2CApp.b2cUtils.setGroupSeparaterWithZero("" + formattedPAT));
        listr_cat_tv_checkClick(holder, prod, position);


        if (B2CApp.b2cUtils.isNetAvailable())
            holder.listr_cat_tv_check.setBackgroundResource(R.drawable.round_darkblue);
        else
            holder.listr_cat_tv_check.setBackgroundResource(R.drawable.round_lightblue);
    }

    private void listr_cat_tv_checkClick(ViewHolder holder, ProductData prod, int position) {
        holder.listr_cat_tv_check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                checkStock(prod, position);
            }
        });
    }


    private void checkStock(ProductData prod, int position) {
        checkNetwork(prod, position);
    }

    private void checkNetwork(ProductData prod, int position) {
        if (B2CApp.b2cUtils.isNetAvailable()) {
            final PendingOrderData tOrder = new PendingOrderData();
            tOrder.setMi_key("" + prod.getMikey());
            tOrder.setMi_name("" + prod.getDisplay_code() + " " + prod.getRemark());

            ((B2CProductsCatalogue) activity).onCheckStockClicked(tOrder, position);
        } else {
            noInternetAlert(prod, position);
        }
    }

    private void noInternetAlert(ProductData prod, int position) {
        String title = activity.getResources().getString(R.string.nointernet);
        String message = activity.getResources().getString(R.string.nointernetdescription);
        String left_btn = activity.getResources().getString(R.string.cancel);
        String right_btn = activity.getResources().getString(R.string.retry);

        AlertPopupDialog alertPopupDialog = new AlertPopupDialog(activity, title, message, left_btn, right_btn,
                new AlertPopupDialog.myOnClickListenerLeft() {
                    @Override
                    public void onButtonClickLeft() {

                    }
                }, new AlertPopupDialog.myOnClickListenerRight() {
            @Override
            public void onButtonClickRight() {
                checkNetwork(prod, position);

            }
        });
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView listr_po_tv_name;
        public TextView listr_po_tv_mrp;
        private TextView listr_po_tv_netprice;
        private ImageView listr_cat_tv_check;

        public ViewHolder(View itemView) {
            super(itemView);
            listr_po_tv_name = (TextView) itemView.findViewById(R.id.listr_po_tv_name);
            listr_po_tv_mrp = (TextView) itemView.findViewById(R.id.listr_po_tv_mrp);
            listr_po_tv_netprice = (TextView) itemView.findViewById(R.id.listr_po_tv_netprice);
            listr_cat_tv_check = (ImageView) itemView.findViewById(R.id.listr_cat_tv_check);
        }
    }
}
