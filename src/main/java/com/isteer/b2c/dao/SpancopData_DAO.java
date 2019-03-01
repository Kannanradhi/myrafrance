package com.isteer.b2c.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.isteer.b2c.model.SpancopData;

import java.util.List;
@Dao
public interface SpancopData_DAO {

    @Query("Delete FROM  aerp_customer_wise_target  ")
    void clearTable();

    @Query("SELECT * FROM aerp_customer_wise_target")
    List<SpancopData> getAll();

    @Insert
    Long insertSpancopData(SpancopData data);

    @Update
    void updateSpancopData(SpancopData data);

    @Query("SELECT count(*) FROM aerp_customer_wise_target WHERE cp_key =:id")
    boolean isOldEntry(int id);
}
