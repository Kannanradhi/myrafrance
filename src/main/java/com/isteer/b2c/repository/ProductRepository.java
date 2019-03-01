package com.isteer.b2c.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.dao.ProductData_DAO;
import com.isteer.b2c.model.ProductData;
import com.isteer.b2c.room.RoomDB;

import java.util.List;

public class ProductRepository {

    private ProductData_DAO productData_dao;
    private LiveData<List<ProductData>> productDataList;



    ProductRepository() {
      //  productDataList = B2CApp.getINSTANCE().getRoomDB().productData_dao().fetchAllproductSelected();
    }

    public LiveData<List<ProductData>> getProductDataList() {
        return productDataList;
    }
    public void insert (ProductData productData) {
        new insertAsyncTask(productData_dao).execute(productData);
    }

    private static class insertAsyncTask extends AsyncTask<ProductData, Void, Void> {
        ProductData_DAO mAsyncTaskDao;
        insertAsyncTask(ProductData_DAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(ProductData... productData) {
            mAsyncTaskDao.insertProductData(productData[0]);
            return null;
        }
    }
}


