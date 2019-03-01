package com.isteer.b2c.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.dao.ProductData_DAO;
import com.isteer.b2c.model.ProductData;

public class ProductViewModel extends ViewModel{

    public LiveData<PagedList<ProductData>> productDataList;

    public void init(ProductData_DAO userDao,String bands,String hint) {
       /* PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder()).setEnablePlaceholders(true)
                        .setPageSize(20).build();*/

        productDataList = (new LivePagedListBuilder(userDao.getAllProductDataTest(hint,bands), 50))
                .build();
    }



}
