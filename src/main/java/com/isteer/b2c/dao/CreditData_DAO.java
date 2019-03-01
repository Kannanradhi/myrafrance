package com.isteer.b2c.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.isteer.b2c.model.CreditData;
import com.isteer.b2c.model.CustomerIndividual;

import java.util.List;

@Dao
public interface CreditData_DAO {
    @Update
    void updateCreditData(CreditData data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertCreditData(CreditData customerData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCreditData(List<CreditData> customerDataList);

    @Query("Delete FROM  b2c_customer_credits  ")
    void clearTable();

    @Query("SELECT count(*) FROM b2c_customer_credits WHERE customer_key = :id")
    boolean isOldEntry(int id);

    @Query("SELECT * FROM b2c_customer_credits WHERE customer_key = :cus_key")
    CreditData fetchSelected(String cus_key);
}
