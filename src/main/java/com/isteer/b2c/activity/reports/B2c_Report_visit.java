package com.isteer.b2c.activity.reports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.R;
import com.isteer.b2c.activity.calender.DSREditProgScreen;
import com.isteer.b2c.adapter.RCV_Report_visit_adapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.dao.EventData_DAO;
import com.isteer.b2c.model.EventData;
import com.isteer.b2c.repository.ReportAllVisitVM;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by rnows on 09-Mar-18.
 */

public class B2c_Report_visit extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rcv_all_visit;
    public static ArrayList<HashMap<String, String>> listEntries = new ArrayList<>();
    private RCV_Report_visit_adapter reportEventAdapter;
    private TextView tv_from_date, tv_to_date, tv_status;
    private Dialog dialog;
    private PopupWindow popupWindow;
    private ArrayList<EventData> dataArrayList;
    private EventData_DAO eventData_dao;
    public ReportAllVisitVM reportAllVisitVM;
    private String fromDate,toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.b2c_report_visit);

         eventData_dao = B2CApp.getINSTANCE().getRoomDB().eventdata_dao();
         reportAllVisitVM = ViewModelProviders.of(this).get(ReportAllVisitVM.class);



        initVar();


        queryEventsOfDay();

    }

    @Override
    public void onBackPressed() {

        gotoB2CMenuScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        B2CApp.b2cUtils.dismissMaterialProgress();
        tv_status.setText("");

    }

    private void initVar() {
        TextView header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("All Visit");
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);
        findViewById(R.id.lt_cleartext).setOnClickListener(this);

        tv_from_date =  findViewById(R.id.tv_from_date);
        tv_to_date =  findViewById(R.id.tv_to_date);
         findViewById(R.id.til_from_date).setOnClickListener(this);
         findViewById(R.id.til_to_date).setOnClickListener(this);
         findViewById(R.id.til_status).setOnClickListener(this);
         findViewById(R.id.lt_from_date).setOnClickListener(this);
         findViewById(R.id.lt_to_date).setOnClickListener(this);
         findViewById(R.id.lt_status).setOnClickListener(this);
        tv_status =  findViewById(R.id.tv_status);
        tv_from_date.setOnClickListener(this);
        tv_to_date.setOnClickListener(this);
        tv_status.setOnClickListener(this);
        rcv_all_visit = (RecyclerView) findViewById(R.id.rcv_all_visit);
        rcv_all_visit.setLayoutManager(new LinearLayoutManager(this));

    }

    private void queryEventsOfDay() {
        reportAllVisitVM.init(eventData_dao);
        reportEventAdapter = new RCV_Report_visit_adapter(this, new RCV_Report_visit_adapter.clickListener() {
            @Override
            public void onClickLestener(int position, EventData item) {
                onItemClick(position, item);
            }
        });
        reportAllVisitVM.listEventLiveData.observe(this, pagedList -> {
            reportEventAdapter.submitList(pagedList);
            if (pagedList.size() != 0) {
                findViewById(R.id.lt_empty).setVisibility(View.GONE);
            }else {
                findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
            }
            rcv_all_visit.setAdapter(reportEventAdapter);
        });


    }
    private void fetchAllVisit() {
        if (tv_from_date.getText().length() != 0) {
            fromDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                    B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat3, "" + tv_from_date.getText()));

        } else {
            fromDate = "2";
            ;
        }
        if ((tv_to_date.getText().length() != 0)) {
            toDate = "" + DateFormat.format(B2CAppConstant.dateFormat2,
                    B2CApp.b2cUtils.getTimestamp(B2CAppConstant.dateFormat3, "" + tv_to_date.getText()));

        } else {
            toDate = "" + DateFormat.format(B2CAppConstant.dateFormat2, new Date());

        }
        String status = tv_status.getText().toString().trim();




        reportAllVisitVM.init(eventData_dao,fromDate, toDate, status + "%");
        reportEventAdapter = new RCV_Report_visit_adapter(this, new RCV_Report_visit_adapter.clickListener() {
            @Override
            public void onClickLestener(int position, EventData item) {
                onItemClick(position,item);
            }
        });
        reportAllVisitVM.listEventLiveData.observe(this, pagedList -> {
            reportEventAdapter.submitList(pagedList);
            if (pagedList.size() != 0) {
                findViewById(R.id.lt_empty).setVisibility(View.GONE);
            }else
                findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);

            rcv_all_visit.setAdapter(reportEventAdapter);
        });
/*        dataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao()
                .getAllEventBySearch(fromDate, toDate, status + "%");
        reportEventAdapter = new RCV_Report_visit_adapter(this, dataArrayList, new RCV_Report_visit_adapter.clickListener() {
            @Override
            public void onClickLestener(int position) {
                onItemClick(position);
            }
        });
        rcv_all_visit.setAdapter(reportEventAdapter);*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                gotoB2CMenuScreen();
                break;
            case R.id.img_home:
                gotoB2CMenuScreen();
                break;

                case R.id.lt_cleartext:
               tv_from_date.setText("");
               tv_to_date.setText("");
               tv_status.setText("");
                    queryEventsOfDay();
                break;
            case R.id.lt_from_date:
            case R.id.til_from_date:
            case R.id.tv_from_date:
                showDatePicker(true);
                break;

            case R.id.lt_to_date:
            case R.id.til_to_date:
            case R.id.tv_to_date:
                showDatePicker(false);
                break;
            case (R.id.lt_status):
            case (R.id.til_status):
            case R.id.tv_status:
                popUpWindowStatus(tv_status);
                break;
            default:
                break;
        }
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }
    private void gotoDSREditProgScreen(){
        startActivity(new Intent(this, DSREditProgScreen.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }
    private void showDatePicker(final boolean isFrom) {
      /*  String cDate;

        if(isFrom)
            cDate = ""+tv_from_date.getText();
        else
            cDate = ""+tv_to_date.getText();

        String[] partics = cDate.split("-");

        int mDay = Integer.parseInt(partics[0]);
        int mMonth = Integer.parseInt(partics[1])-1;
        int mYear = Integer.parseInt(partics[2]);
*/
        Calendar c = Calendar.getInstance();
        int mDay = c.get(Calendar.DATE);
        int mMonth = c.get(Calendar.MONTH);
        int mYear = c.get(Calendar.YEAR);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar tCalendar = Calendar.getInstance();
                tCalendar.set(year, monthOfYear, dayOfMonth);

                String tDate = "" + DateFormat.format(B2CAppConstant.dateFormat3, tCalendar.getTime());

                if (isFrom) {
                    tv_from_date.setText(tDate);
                    // tv_to_date.setText(tDate);

                } else {
                    tv_to_date.setText(tDate);

                }
                fetchAllVisit();
            }

        }, mYear, mMonth, mDay);

        dpd.show();
    }



    private void popUpWindowStatus(View status) {
        /*dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialog.setContentView(R.layout.popupwindow_status);*/
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwindow_status, (ViewGroup) status.findViewById(R.id.rootLayout));


        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        try {
            popupWindow.showAsDropDown(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView txt_pending = ((TextView) view.findViewById(R.id.txt_pending));
        TextView txt_visited = ((TextView) view.findViewById(R.id.txt_visited));
        TextView txt_cancelled = ((TextView) view.findViewById(R.id.txt_cancelled));

        String str_status = tv_status.getText().toString();
        if (str_status.equalsIgnoreCase(getResources().getString(R.string.pending))) {
            txt_pending.setTextColor(getResources().getColor(R.color.darkblue));
            txt_pending.setTextSize(18);
        } else if (str_status.equalsIgnoreCase(getResources().getString(R.string.visited))) {
            txt_visited.setTextColor(getResources().getColor(R.color.darkblue));
            txt_visited.setTextSize(18);
        } else if (str_status.equalsIgnoreCase(getResources().getString(R.string.cancelled))) {
            txt_cancelled.setTextColor(getResources().getColor(R.color.darkblue));
            txt_cancelled.setTextSize(18);
        }

        LinearLayout lt_pending = ((LinearLayout) view.findViewById(R.id.lt_pending));
        lt_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText(getResources().getString(R.string.pending));
                fetchAllVisit();
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.lt_visited).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText(getResources().getString(R.string.visited));
                fetchAllVisit();
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.lt_cancelled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText(getResources().getString(R.string.cancelled));
                fetchAllVisit();
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.lt_removed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText(getResources().getString(R.string.removed));
                fetchAllVisit();
                popupWindow.dismiss();
            }
        });
        view.findViewById(R.id.lt_checkin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText(getResources().getString(R.string.checkin));
                fetchAllVisit();
                popupWindow.dismiss();
            }
        });


     /*   dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog.dismiss();
            }
        });*/


    }
/*    private void refreshByStatus(String status)
    {

        Cursor mCursor = B2CApp.b2cLdbs.fetchVisitByStatus(this,status );

        listEntries.clear();
        listEntries.addAll(new B2CCursorFactory().fetchColoumns(mCursor,
                new String[]{
                        DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(),
                        DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name(),
                        DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(),
                        DSRTableCreate.COLOUMNS_EVENT_MASTER.from_date_time.name()}));
        Log.e("listEntries",""+listEntries.size());
        ArrayList<EventData> dataArrayList = (ArrayList<EventData>) B2CApp.getINSTANCE().getRoomDB().eventdata_dao().getAllEvent();
        reportEventAdapter = new RCV_Report_visit_adapter(this, dataArrayList);
        rcv_all_visit.setAdapter(reportEventAdapter);
    }*/

public void onItemClick(int position, EventData eventData){
    //B2CApp.b2cUtils.updateMaterialProgress(this,B2CAppConstant.LOADING);
   // Logger.LogError("eventKey",""+eventData.getEvent_key());
    DSREditProgScreen.toRefresh = true;
    DSREditProgScreen.eventKey = ""+eventData.getEvent_key();
    gotoDSREditProgScreen();

}
}
