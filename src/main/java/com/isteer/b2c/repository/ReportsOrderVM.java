package com.isteer.b2c.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.isteer.b2c.dao.BillData_DAO;
import com.isteer.b2c.dao.PendingOrderData_DAO;
import com.isteer.b2c.model.BillData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.model.PendingOrderData;

public class ReportsOrderVM extends ViewModel{

    public LiveData<PagedList<OrderNewData>> pendingOrderDataList;
    public LiveData<PagedList<OrderNewData>> pendingAllOrderDataList;
    public LiveData<PagedList<OrderNewData>> ReportPendingAllOrderDataList;

    public void init(PendingOrderData_DAO userDao, String fromdate,String todate) {


        pendingOrderDataList = (new LivePagedListBuilder(userDao.getReportOrderByDate(fromdate,todate), 50))
                .build();

    }

    public void init(PendingOrderData_DAO userDao) {

        pendingAllOrderDataList = (new LivePagedListBuilder(userDao.getReportOrder(), 50))
                .build();

        ReportPendingAllOrderDataList = (new LivePagedListBuilder(userDao.getActionOrder(), 50))
                .build();
    }




}
