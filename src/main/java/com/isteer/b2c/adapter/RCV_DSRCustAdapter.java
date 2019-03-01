package com.isteer.b2c.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.OrderNewData;

import java.util.ArrayList;

public class RCV_DSRCustAdapter extends RecyclerView.Adapter<RCV_DSRCustAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<OrderNewData> data;
    private ArrayList<CustomerData> data1;
    myClickListner listener;
    ItemClickListener myUnvisitedClick;
    private int from_activity;

    public RCV_DSRCustAdapter(Activity a, ArrayList<OrderNewData> d, myClickListner myClickListner) {
        activity = a;
        data = d;
        listener = myClickListner;
        from_activity = 1;
    }
    public RCV_DSRCustAdapter(Activity a, ArrayList<CustomerData> d, ItemClickListener myUnvisitedClick1) {
        activity = a;
        data1 = d;
        myUnvisitedClick = myUnvisitedClick1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.listrow_customer, parent, false);
        return new ViewHolder(view);
    }

    public void refreshList(ArrayList<OrderNewData> filtercustomerDataArrayList) {
        data = filtercustomerDataArrayList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (from_activity == 1) {
            OrderNewData orderNewData = data.get(position);


        String area = orderNewData.getArea();
        String cust = orderNewData.getCompany_name();
        holder.customer_entry.setText(cust + " - " + area);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickListener(position);
            }
        });
        }else {
            CustomerData customerData = data1.get(position);
            String area = customerData.getArea();
            String cust = customerData.getCompany_name();
            holder.customer_entry.setText(cust + " - " + area);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myUnvisitedClick.onClick(view,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return from_activity ==1?data.size():data1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView customer_entry;

        public ViewHolder(View itemView) {
            super(itemView);
            customer_entry = (TextView) itemView.findViewById(R.id.customer_entry);
        }
    }

    public interface myClickListner {
        void onClickListener(int position);
    }
}
