package com.isteer.b2c.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.CustomerIndividual;

import java.util.List;

@Dao
public interface CustomerIndividual_DAO {

    @Query("Delete FROM  b2c_contact_individual  ")
    void clearTable();



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCustomerIndividual(List<CustomerIndividual> customerData);



    @Query("SELECT * FROM b2c_contact_individual WHERE contact_key = :cmkey")
    CustomerIndividual getCustomerDetails(String cmkey);

    @Query("UPDATE b2c_contact_individual SET latitude =:latitude ,longitude =:longitude ,is_synced_to_server =0  WHERE  " +
            "con_key =:con_key and contact_key =:contact_key")
    int updateCustomerLocation(String con_key, String contact_key, double latitude, double longitude);


    @Query("UPDATE b2c_contact_individual SET is_synced_to_server =1  WHERE  " +
            "con_key =:con_key and contact_key =:contact_key")
    int updateCustomerIndSynctoserver(String con_key, String contact_key);

}
