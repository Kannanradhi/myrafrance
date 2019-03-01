package com.isteer.b2c.dao;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.isteer.b2c.model.BillData;
import com.isteer.b2c.model.CustomerIndividual;
import com.isteer.b2c.model.PendingOrderData;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface BillData_DAO  {



    @Update
    void updateBillData(BillData data);

    @Query("Delete FROM  b2c_customer_pending_bills  ")
    void clearTable();

    @Insert
    Long insertBillData(BillData customerData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllBillData(List<BillData> customerData);

    @Query("SELECT count(*) FROM b2c_customer_pending_bills WHERE invoice_key = :id")
    boolean isOldEntry(int id);

    @Query("SELECT * FROM b2c_customer_pending_bills WHERE customer_key = :cus_key  ")
    List<BillData> fetchAllBills(String cus_key);

    @Query("SELECT * FROM b2c_customer_pending_bills WHERE customer_key = :cus_key  ")
    DataSource.Factory<Integer,BillData> fetchAllBillsPaged(String cus_key);



}
