package com.isteer.b2c.activity.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.isteer.b2c.activity.B2CLancher.B2CCustSearchScreen;
import com.isteer.b2c.activity.B2CLancher.B2CNewMainActivity;
import com.isteer.b2c.activity.B2CLancher.B2CProductsCatalogue;
import com.isteer.b2c.activity.counter_details.B2CCounterDetailScreen;
import com.isteer.b2c.activity.reports.B2CSyncScreen;
import com.isteer.b2c.adapter.RCV_DSRCounterAdapter;
import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.R;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.utility.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class B2CCountersScreen extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "B2CCountersScreen";

    public static ArrayList<HashMap<String, String>> listEntries = new ArrayList<HashMap<String, String>>();

    private ListView nameEntryList;
    private RecyclerView rcv_nameEntryList;
    private RCV_DSRCounterAdapter rcv_nameEntryAdaptor;

    private TextView header_title, txt_todays_count;
    private ImageView btn_header_right;

    public static boolean isFromAddProg = false;
    public static boolean toRefresh = false;
    private LinearLayout bottombar_one, bottombar_two, bottombar_three, bottombar_four, bottombar_five;
    private ArrayList<OrderNewData> orderDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.scr_b2c_counterlist);
        initVar();
    }

    @Override
    protected void onResume() {

        super.onResume();

        refreshList();
/*		
        if(toRefresh)
		{
			toRefresh = false;
			refreshList();
		}*/

    }

    private void initVar() {
        findViewById(R.id.img_back).setOnClickListener(this);
        findViewById(R.id.img_home).setOnClickListener(this);
        findViewById(R.id.lt_todays_count).setVisibility(View.GONE);
        txt_todays_count = (TextView) findViewById(R.id.txt_todays_count);
        txt_todays_count.setVisibility(View.GONE);
        header_title = (TextView) findViewById(R.id.header_title);
        header_title.setText("Today's Counters");
        header_title.setTextColor(getResources().getColor(R.color.White));
        btn_header_right = (ImageView) findViewById(R.id.btn_header_right);
        btn_header_right.setImageResource(R.drawable.catalogue);
        btn_header_right.setVisibility(View.VISIBLE);
        btn_header_right.setOnClickListener(this);
        rcv_nameEntryList = ((RecyclerView) findViewById(R.id.rcv_nameEntryList));
        rcv_nameEntryList.setLayoutManager(new LinearLayoutManager(this));


        refreshList();

    }

    private void refreshList() {

        listEntries.clear();
        long timestamp = new Date().getTime();
        String eventDate = ""+DateFormat.format(B2CAppConstant.dateFormat2, timestamp);
        String pendingDate = ""+DateFormat.format(B2CAppConstant.dateFormat2,timestamp);


   /*     Cursor cCursor = B2CApp.b2cLdbs.fetchTodaysBeat(this);



        Log.e("orderDataArrayList",""+orderDataArrayList.size());
        Log.e("orderDataArrayList",""+eventDate);
        Log.e("pendingDate",""+pendingDate);
*//*		String startDate = ""+DateFormat.format(B2CAppConstant.dateFormat, new Date().getTime());
 		Cursor cCursor = B2CApp.b2cLdbs.fetchCustomQuery2(this, DSRTableCreate.TABLE_AERP_EVENT_MASTER, null, 
				DSRTableCreate.COLOUMNS_EVENT_MASTER.event_date.name() + " like '%" + startDate + "%' " + " order by " + DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name());
*//*
        listEntries.addAll(new DSRCursorFactory().fetchColoumns(cCursor, new String[]{
                DSRTableCreate.COLOUMNS_EVENT_MASTER.event_key.name(),
                DSRTableCreate.COLOUMNS_EVENT_MASTER.cmkey.name(),
                DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(),
                B2CTableCreate.COLOUMNS_CONTACT_MASTER.cus_key.name(),
                DSRTableCreate.COLOUMNS_EVENT_MASTER.customer_name.name(), DSRTableCreate.COLOUMNS_EVENT_MASTER.area.name(),
                DSRTableCreate.COLOUMNS_EVENT_MASTER.status.name(), "ordered_count_today"}));*/

       /* nameEntryAdaptor = new DSRCounterAdapter(this, listEntries);
        nameEntryList.setAdapter(nameEntryAdaptor);*/
        String[] status = {"Visited","Cancelled","Pending","CheckIn"};
         orderDataArrayList = (ArrayList<OrderNewData>) B2CApp.getINSTANCE().getRoomDB().customerData_dao()
                 .fetchTodaysBeat(eventDate+"%",pendingDate+"%",status);
        Logger.LogError("orderDataArrayList",""+orderDataArrayList.size());
        if (orderDataArrayList.size() < 1){
            findViewById(R.id.lt_empty).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.lt_empty).setVisibility(View.GONE);

        }
        rcv_nameEntryAdaptor = new RCV_DSRCounterAdapter(this, orderDataArrayList);
        rcv_nameEntryList.setAdapter(rcv_nameEntryAdaptor);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       goBack();
    }



    private void goBack() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int backPress = extras.getInt(B2CAppConstant.BACKTOCOUNTERSCREEN);
            Logger.LogError(B2CAppConstant.BACKTOCOUNTERSCREEN, "" + backPress);
            if (backPress == B2CAppConstant.MENU_SCREEN) {
                gotoB2CMenuScreen();
                intent.putExtra("backPress", 0);

            }  else if (backPress == 0 || backPress == 3)
                gotoB2CCounterDetailScreen();

        } else {
            gotoB2CMenuScreen();
        }
    }

    @Override
    public void onClick(View pView) {

        switch (pView.getId()) {
            case R.id.img_back:

//                finish();
                goBack();

                break;
            case R.id.img_home:
                gotoB2CMenuScreen();
                break;
            case R.id.btn_header_right:
                gotoB2CProductsCatalogue();
                break;

            default:
                break;

        }
    }


    private void exitApp() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void gotoB2CCounterDetailScreen() {

        startActivity(new Intent(this, B2CCounterDetailScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCustListScreen() {

        startActivity(new Intent(this, B2CCustSearchScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CPendingOrdersScreen() {

        startActivity(new Intent(this, B2CPendingOrderScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CMenuScreen() {

        startActivity(new Intent(this, B2CNewMainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CSyncScreen() {

        startActivity(new Intent(this, B2CSyncScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CProductsCatalogue() {

        B2CProductsCatalogue.previousScreen = B2CAppConstant.SCREEN_COUNTERS;
        startActivity(new Intent(this, B2CProductsCatalogue.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    private void gotoB2CCountersScreen() {

        startActivity(new Intent(this, B2CCountersScreen.class)
                .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

}
