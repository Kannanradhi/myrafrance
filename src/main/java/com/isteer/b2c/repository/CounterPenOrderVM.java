package com.isteer.b2c.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.isteer.b2c.dao.PendingOrderData_DAO;
import com.isteer.b2c.dao.ProductData_DAO;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;

public class CounterPenOrderVM extends ViewModel{

    public LiveData<PagedList<PendingOrderData>> pendingOrderDataList;

    public void init(PendingOrderData_DAO userDao, String cus_key) {
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder()).setEnablePlaceholders(true)
                        .setPageSize(20).build();
        pendingOrderDataList = (new LivePagedListBuilder(userDao.fetchAllOrdersPaged(cus_key), 50))
                .build();
    }




}
