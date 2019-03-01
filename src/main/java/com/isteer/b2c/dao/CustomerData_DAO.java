package com.isteer.b2c.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.isteer.b2c.app.B2CApp;
import com.isteer.b2c.db.B2CTableCreate;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.OrderNewData;
import com.isteer.b2c.model.PendingOrderData;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CustomerData_DAO {

    @Query("Delete FROM  b2c_contact_master  ")
    void clearTable();

    @Query("SELECT * FROM b2c_contact_master")
    List<CustomerData> getAll();

    @Query("select distinct area_name,area ,count(*) as count,cmkey from b2c_contact_master group by  area order by  area;")
    List<OrderNewData> getTodaysBeatPlan();

    @Query("select * from b2c_contact_master WHERE  area_name LIKE :slectedArea;")
    List<CustomerData> getareaForEvent(String slectedArea);

    @Insert
    void insertAllLead(List<CustomerData> data);

    @Update
    void updateLead(CustomerData data);

    @Insert
    Long insertCustomerData(CustomerData customerData);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCustomerData(List<CustomerData> customerData);

    @Query("SELECT count(*) FROM b2c_contact_master WHERE cmkey = :id")
    boolean isOldEntry(int id);

    @Query("select a.event_key,a.cmkey,a.status,a.customer_name,a.area ," +
            "( select sum(b.pending_qty)  from  b2c_pending_orders b where b.customer_key = a.cus_key " +
            "AND b.date  like :pendingDate) as ordered_count_today  ," +
            "( select c.cus_key from b2c_contact_master " +
            " c where c.cmkey " +
            " = a.cmkey)  as cus_key  " +
            " from aerp_event_master a " +
            " where event_date like :event_date and status in (:status) order by ordered_count_today ")
    List<OrderNewData> fetchTodaysBeat(String event_date, String pendingDate, String[] status);

    @Query("select * from b2c_contact_master where company_name  like :hintCusName OR area_name like :hintCusName order by company_name ")
    List<OrderNewData> fetchCustomerSearch(String hintCusName);

    @Query("SELECT * FROM b2c_contact_master WHERE cmkey = :cmkey ")
    CustomerData fetchCustomerDetails(String cmkey);

    @Query("select DISTINCT cus_key from b2c_contact_master order by cus_key ")
    List<String> fetchDistinct();

    @Query("UPDATE b2c_contact_master SET latitude =:latitude ,longitude =:longitude ,is_synced_to_server =0  WHERE  " +
            "cmkey =:contact_key ")
    int updateCustomerLocation(String contact_key, double latitude, double longitude);

    @Query("UPDATE b2c_contact_master SET is_synced_to_server = 1  WHERE  " +
            "cmkey =:contact_key")
    int updateCustomerIndSynctoserver(String contact_key);

    @Query("SELECT * FROM b2c_contact_master WHERE is_synced_to_server = 0")
    List<CustomerData> fetchAllCustomerLocation();
}
