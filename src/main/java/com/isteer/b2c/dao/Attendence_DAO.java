package com.isteer.b2c.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.isteer.b2c.config.DSRAppConstant;
import com.isteer.b2c.model.AttendenceData;
import com.isteer.b2c.model.SpancopData;

import java.util.List;

@Dao
public interface Attendence_DAO {

    @Query("Delete FROM  aerp_attendence_log  ")
    void clearTable();

    @Query("SELECT count(*) FROM aerp_location_log")
    boolean getAttendenceCount();

    @Insert
    Long insertAttendence(AttendenceData data);

    @Update
    void updateAttendence(AttendenceData data);

@Query("UPDATE aerp_attendence_log SET stop_time =:stop_time,status ='"+ DSRAppConstant.KEY_STATUS_STOPPED+"' ," +
        " update_status ='"+DSRAppConstant.KEY_UPDATE_STATUS_PENDING+"' ,stop_latitude =:stoplatitude ," +
        "stop_longitude =:stoplongitude   WHERE status ='"+ DSRAppConstant.KEY_STATUS_STARTED+"' and stop_time ='"+""+"'" )
    long updateAttendenceLog(String stop_time,Double stoplatitude,Double stoplongitude);

    @Query("SELECT * FROM aerp_attendence_log WHERE update_status ='"+DSRAppConstant.KEY_UPDATE_STATUS_PENDING+"'")
      List<AttendenceData> getAsyncronizedAttendence();

    @Query("SELECT * FROM aerp_attendence_log WHERE status = '"+ DSRAppConstant.KEY_STATUS_STOPPED+"'")
      List<AttendenceData> getAttendenceStarted();

    @Query("UPDATE aerp_attendence_log set att_key = :att_key,update_status ='"+DSRAppConstant.KEY_UPDATE_STATUS_UPDATED+"'" +
            " WHERE att_key =:dummy_key ")
    void updateattendence_log(String dummy_key,String att_key);

    @Query("Delete FROM  aerp_attendence_log where  att_key =:att_key AND status = '"+ DSRAppConstant.KEY_STATUS_STOPPED+"'")
    void delete_attendence_log(String att_key);

    @Query("SELECT date FROM aerp_attendence_log WHERE stop_time ='"+""+"'")
    String start_date();
}
