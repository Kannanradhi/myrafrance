package com.isteer.b2c.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;
import android.util.Log;

import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.model.PendingOrderData;
import com.isteer.b2c.model.ProductData;

import java.util.List;

@Dao
public interface CollectionData_DAO {

    @Query("SELECT * FROM b2c_collections  order by trans_date desc, pay_coll_key desc")
    List<CollectionData> getAllCollection();
    @Update
    void updateCollectionKey(CollectionData data);

    @Query("Delete FROM  b2c_collections  ")
    void clearTable();

    @Insert
    Long insertCollectionData(CollectionData data);

    @Insert
    void insertAllCollectionData(List<CollectionData> dataList);
    @Delete
    void deleteCollectionData(CollectionData data);

    @Query("UPDATE b2c_collections set pay_coll_key =:collectionKey ,sc_ledger_key =:scledgerkey," +
            "is_synced_to_server =:issyced where pay_coll_key =:dummyKey and cus_key =:cusKey ")
    int updateCollectionKey(String dummyKey, String cusKey, String scledgerkey,int collectionKey, int issyced);

    @Query("SELECT count(*) FROM b2c_collections WHERE pay_coll_key = :id")
    boolean isOldEntry(long id);

    @Query("select * from b2c_collections where trans_date  BETWEEN :fromDate AND :toDate order by trans_date desc , pay_coll_key desc ")

    List<CollectionData> getCollectionByDate(String fromDate,String toDate);

    @Query("select sum(amount)  from b2c_collections WHERE trans_date  BETWEEN :fromDate AND :toDate")
    double getCollectionSum(String fromDate,String toDate);

    @Query("select sum(amount)  from b2c_collections where trans_date  =:todaysDate")
    double getColectiontodaysSum( String todaysDate);


    @Query("SELECT * FROM b2c_collections WHERE is_synced_to_server = 0  || pay_coll_key < 0")
    List<CollectionData> fetchCustomSelection();
}
