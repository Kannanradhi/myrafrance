package com.isteer.b2c.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isteer.b2c.R;
import com.isteer.b2c.activity.action.B2CAddTodaysBeat;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.utility.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by rnows on 31-Mar-18.
 */

public class RCV_B2CAddTodaysBeatAdapter extends RecyclerView.Adapter<RCV_B2CAddTodaysBeatAdapter.ViewHolder> {


    private Activity activity;
    private ArrayList<OrderNewData> data;
    private static LayoutInflater inflater = null;
    private static ArrayList<String> alreadyAddedCounters = new ArrayList<String>();

    public RCV_B2CAddTodaysBeatAdapter(Activity a, ArrayList<OrderNewData> d) {

        activity = a;
        data = d;
        updateAlreadyAddedCounters();


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.listrow_addbeat, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OrderNewData currentEntry = data.get(position);

        String areaName = currentEntry.getArea_name();
        final String area = currentEntry.getArea();

        if (areaName == null || areaName.equalsIgnoreCase(null) || areaName.equalsIgnoreCase("null"))
            areaName = "Area NA";

        String rowText = areaName + " (" + currentEntry.getCount() + ")";
        holder.customer_entry.setText(rowText);

        if (alreadyAddedCounters.contains(areaName)) {
		/*	holder.btn_addtobeat.setBackgroundResource(R.drawable.round_green_green);
			holder.btn_addtobeat.setText("Added");*/
            holder.btn_addtobeat.setImageResource(R.drawable.ic_check_circle_black_24dp);
        } else {
            holder.btn_addtobeat.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
			/*holder.btn_addtobeat.setBackgroundResource(R.drawable.round_red_red);
			holder.btn_addtobeat.setText("Add");*/
        }

        final String finalAreaName = areaName;
        holder.btn_addtobeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  Logger.LogError("finalAreaName",""+finalAreaName);
                //  Logger.LogError("area",""+area);
//Logger.LogError("alreadyAddedCounters",""+alreadyAddedCounters);
                if (!alreadyAddedCounters.contains(finalAreaName)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(
                            "Are you sure to Add the area ?")
                            .setTitle("Alert!")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Logger.LogError("alreadyAddedCounters.size()", "" + alreadyAddedCounters.size());
                                    Logger.LogError("area.name()", "" + currentEntry.getArea());
                                    ((B2CAddTodaysBeat) activity).addToBeatClicked(currentEntry.getArea_name(), currentEntry.getArea());


                                    alreadyAddedCounters.add(finalAreaName);
                                    Logger.LogError("alreadyAddedCounters", "" + alreadyAddedCounters);
                                    holder.btn_addtobeat.setImageResource(R.drawable.ic_check_circle_black_24dp);

                                    ((B2CAddTodaysBeat) activity).addOneCount(alreadyAddedCounters);
                                    dialogInterface.cancel();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();


                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(
                            "Are you sure to remove the area ?")
                            .setTitle("Alert!")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                            dialog.cancel();

                                            ((B2CAddTodaysBeat) activity).removeFromBeatClicked(currentEntry.getArea_name(), currentEntry.getArea());

											/*holder.btn_addtobeat.setBackgroundResource(R.drawable.round_red_red);
											holder.btn_addtobeat.setText("Add");*/
                                            holder.btn_addtobeat.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);

                                            alreadyAddedCounters.remove(finalAreaName);
                                            ((B2CAddTodaysBeat) activity).addOneCount(alreadyAddedCounters);
                                            // selectedCount();

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
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView customer_entry;
        public ImageView btn_addtobeat;

        public ViewHolder(View itemView) {
            super(itemView);

            customer_entry = (TextView) itemView.findViewById(R.id.customer_entry);
            btn_addtobeat = (ImageView) itemView.findViewById(R.id.btn_addtobeat);
        }
    }

    private void updateAlreadyAddedCounters() {
        String startDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date());
       /* alreadyAddedCounters.clear();

        Cursor cCursor = B2CApp.b2cLdbs.fetchCustomQuery2(activity, DSRTableCreate.TABLE_AERP_EVENT_MASTER, new String[]{DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name()},
                DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name() + " like '%" + startDate + "%' " +
                        " group by " + DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name()+" order by " + DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name());
        alreadyAddedCounters.addAll(new DSRCursorFactory().fetchAColoumn(cCursor, DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name()));*/
        String[] status = {"Visited", "Cancelled", "Pending", "CheckIn"};
        alreadyAddedCounters = (ArrayList<String>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().todayPlannedArea(startDate, status);

    }
}
