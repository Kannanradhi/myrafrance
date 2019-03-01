package com.isteer.b2c.dao;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.model.EventData;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface EventData_DAO {

    @Insert
    Long insertEventData(EventData data);

    @Update
    void updateEventData(EventData data);

    @Query("Delete FROM  aerp_event_master")
    void clearTable();

    @Query("SELECT * FROM aerp_event_master ORDER BY event_date desc ")
    List<EventData> getAllEvent();


    @Query("select * from aerp_event_master where event_date between :fromDate and :toDate  AND status like  :status " +
            "order by event_date desc")
    List<EventData> getAllEventBySearch(String fromDate, String toDate, String status);

    @Query("SELECT * FROM aerp_event_master ORDER BY event_date desc,event_key desc ")
    DataSource.Factory<Integer, EventData> getAllEventLD();

    @Query("select * from aerp_event_master where event_date between :fromDate and :toDate  AND status like  :status " +
            "order by event_date desc,event_key desc")
    DataSource.Factory<Integer, EventData> getAllEventBySearchLD(String fromDate, String toDate, String status);


    @Query("SELECT count(*) FROM aerp_event_master WHERE event_key =:id")
    boolean isOldEntry(int id);


    @Query("SELECT * from aerp_event_master where event_key =:eventkey")
    EventData getEventByeventkey(String eventkey);

    @Query("SELECT * from aerp_event_master where event_date =:todayDate")
    List<EventData> getEventsOfDay(String todayDate);

    @Query("DELETE  from aerp_event_master where area LIKE :areaname and event_date =:todayDate ")
    void deleteEventsOfDay(String areaname, String todayDate);

    @Query("UPDATE aerp_event_master SET status ='Removed',is_synced_to_server = 0 where area LIKE :areaname and event_date =:todayDate and status ='Pending'")
    void updateRemoveEventsOfDay(String areaname, String todayDate);

    @Query("select count(*) from aerp_event_master txt_val_pipeline_value WHERE event_date BETWEEN :fromDate AND :toDate")
    Long getAllEventsCount(String fromDate,String toDate);

    @Query("select count(*) from aerp_event_master where status ='Visited' and event_date LIke :todateDate")
    Long getVisitCount(String todateDate);

    @Query("select count(*) from aerp_event_master WHERE status ='Visited' and event_date BETWEEN :fromDate AND :toDate")
    Long getMTDVisitCount(String fromDate,String toDate);

    @Query("select count(*) from aerp_event_master where  event_date LIke :todateDate   ")
    int todayPlannedCount(String todateDate);

    @Query("select area from aerp_event_master where  event_date LIke :todateDate " +
            "and status in(:status) group by area ")
    List<String> todayPlannedArea(String todateDate,String[] status);

    @Query("select event_key from aerp_event_master where cmkey =:currentcmkey and from_date_time like :startDate and status ='Pending' ")
    Integer fetchEventKey(Integer currentcmkey, String startDate);

    @Query("select event_key from aerp_event_master where cmkey =:currentcmkey and  status ='CheckIn' ")
    Integer fetchCheckinEventKey(Integer currentcmkey);

    @Query("UPDATE aerp_event_master SET  " +
            "order_taken =:order_taken, product_display =:product_display, " +
            "promotion_activated =:promotion_activated, " +
            "is_synced_to_server =0  WHERE event_key =:event_key " +
            "AND event_user_key =:event_user_key ")
    int updateYesOrNo(String order_taken, String product_display, String promotion_activated, int event_key, String event_user_key);

    @Query("UPDATE aerp_event_master SET status =:status,action_response =:action_response,minutes_of_meet =:minutes_of_meet,visit_update_time =:visit_time," +
            "completed_on =:completed_time, cancelled_on =:cancelled_time,is_synced_to_server =0" +
            " ,latitude =:latitude, longitude =:longitude" +
            " WHERE event_key =:eventKey " +
            " AND  event_user_key =:event_user_key ")
    int updateStatus(String eventKey, String event_user_key, String status, String visit_time,
                     String completed_time, String cancelled_time, String action_response,
                     String minutes_of_meet,String latitude,String longitude);

    @Query("UPDATE aerp_event_master SET is_synced_to_server =1, event_key =:eventKey  WHERE event_key =:dummyKey AND event_user_key =:eventUserKey")
    int updateEventKey(String dummyKey, String eventUserKey, String eventKey);

    @Query("UPDATE aerp_event_master SET is_synced_to_server = 0, preparation =:preparation, objective =:objective, plan =:plan,strategy =:strategy, " +
            "action_response =:action_response, anticipate =:anticipate , minutes_of_meet =:mins_of_meet , competition_pricing =:competition_pricing ," +
            "feedback =:feed_back WHERE event_key =:eventKey AND event_user_key =:eventUserKey")
    int updateData(String preparation, String objective, String plan, String strategy, String action_response,
                   String anticipate, String mins_of_meet, String competition_pricing,
                   String feed_back, String eventKey, String eventUserKey);

    @Query("UPDATE aerp_event_master SET from_date_time =:tVisitedTime,to_date_time =:tVisitedToTime,status ='CheckIn',is_synced_to_server = 0 where event_key =:eventkey ")
    void updateCurrentFromDate(String eventkey, String tVisitedTime, String tVisitedToTime);

    @Query("select customer_name from aerp_event_master where cmkey =:checkedincmkey")
    String fetchCompanyName(int checkedincmkey);

    @Query("UPDATE aerp_event_master SET is_synced_to_server =1 WHERE event_key =:eventKey AND event_user_key =:eventUserKey ")
    void updateisSyncedToserver(String eventKey, String eventUserKey);

    @Query("SELECT * from aerp_event_master where event_date LIKE :format")
    List<EventData> fetchSelectedByMonth(String format);

    @Query("SELECT * FROM aerp_event_master WHERE is_synced_to_server = 0 || event_key < 0  ")
    List<EventData> fetchnonSynedData();

    @Query("SELECT * FROM aerp_event_master WHERE is_synced_to_server = 0  ")
    List<EventData> fetchnonSynedDataToServer();
}
