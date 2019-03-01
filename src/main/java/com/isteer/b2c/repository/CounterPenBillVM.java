package com.isteer.b2c.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.isteer.b2c.dao.BillData_DAO;
import com.isteer.b2c.dao.PendingOrderData_DAO;
import com.isteer.b2c.model.BillData;
import com.isteer.b2c.model.PendingOrderData;

public class CounterPenBillVM extends ViewModel{

    public LiveData<PagedList<BillData>> billDataList;

    public void init(BillData_DAO userDao, String cus_key) {
      /*  PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder()).setEnablePlaceholders(true)
                        .setPageSize(20).build();*/

        billDataList = (new LivePagedListBuilder(userDao.fetchAllBillsPaged(cus_key), 50))
                .build();
    }




}
