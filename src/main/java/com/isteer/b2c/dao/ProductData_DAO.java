package com.isteer.b2c.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ProductData_DAO {
    @Update
    void updateProductData(ProductData data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertProductData(ProductData data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllProductData(List<ProductData> data);

    @Query("Delete FROM  b2c_product_master")
    void clearTable();

    @Query("SELECT count(*) FROM b2c_product_master WHERE mikey= :id")
    boolean isOldEntry(long id);

    @Query("select * from b2c_product_master where manu_name like :manufacturerSelected AND (remark like :hintdisplaycode OR display_code like :hintdisplaycode )" +
            " order by display_code ")
    List<ProductData> getAllProductData(String hintdisplaycode,String manufacturerSelected);

    @Query("select * from b2c_product_master where manu_name like :manufacturerSelected AND (remark like :hintdisplaycode OR display_code like :hintdisplaycode )" +
            " order by display_code ")
   DataSource.Factory<Integer,ProductData> getAllProductDataTest(String hintdisplaycode,String manufacturerSelected);


    @Query("select * from b2c_product_master where mikey like :mikey ")
    ProductData fetchproductSelected(String mikey);

    @Query("select * from b2c_product_master")
    DataSource.Factory<Integer,ProductData> fetchAllProduct();

    @Query("SELECT DISTINCT manu_name from b2c_product_master group by manu_name order by manu_name")
    List<String> fetchDistinctMenuName();
}
