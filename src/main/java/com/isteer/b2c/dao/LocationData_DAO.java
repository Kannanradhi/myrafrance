package com.isteer.b2c.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.db.DSRTableCreate;
import com.isteer.b2c.model.LocationData;
import com.isteer.b2c.model.SpancopData;

import java.util.List;
@Dao
public interface LocationData_DAO {

    @Query("Delete FROM  aerp_location_log  ")
    void clearTable();

    @Query("SELECT count(*) FROM aerp_location_log")
    Long getLocationCount();

    @Insert
    Long insertLocationData(LocationData data);

    @Update
    void updateLocationData(LocationData data);

@Query("select * from aerp_location_log" )
    List<LocationData> getAsyncronizedLocationList();

/*@Query("select * from aerp_location_log where update_status ='"+DSRAppConstant.KEY_UPDATE_STATUS_PENDING +"'" )
    List<LocationData> getAsyncronizedLocationList();*/


@Query("UPDATE aerp_location_log set update_status = '"+ DSRAppConstant.KEY_UPDATE_STATUS_UPDATED +"' WHERE loc_key =:loc_key ")
    void updateLocationLogUpdateStatus1(String loc_key);

    @Query("Delete FROM  aerp_location_log  WHERE loc_key =:loc_key ")
    void deleteLocationLogUpdateStatus1(String loc_key);
}
