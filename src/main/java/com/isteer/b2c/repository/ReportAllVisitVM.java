package com.isteer.b2c.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.isteer.b2c.dao.EventData_DAO;
import com.isteer.b2c.dao.PendingOrderData_DAO;
import com.isteer.b2c.dao.ProductData_DAO;
import com.isteer.b2c.model.EventData;

public class ReportAllVisitVM extends ViewModel {

    public LiveData<PagedList<EventData>> listEventLiveData;


    public void init(EventData_DAO userDao, String fromdate, String todate, String status) {


        listEventLiveData = (new LivePagedListBuilder(userDao.getAllEventBySearchLD(fromdate,todate,status), 50))
                .build();

    }

    public void init(EventData_DAO userDao) {

        listEventLiveData = (new LivePagedListBuilder(userDao.getAllEventLD(), 50))
                .build();
    }
}
