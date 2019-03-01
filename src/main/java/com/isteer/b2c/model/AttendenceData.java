package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "aerp_attendence_log")
public class AttendenceData {

    @ColumnInfo(name = "att_key")
    @PrimaryKey()
    private int att_key;
    @ColumnInfo(name = "user_key")
    private String user_key;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "start_time")
    private String start_time;
    @ColumnInfo(name = "stop_time")
    private String stop_time;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "update_status")
    private String update_status;
    @ColumnInfo(name = "start_latitude")
    private double start_latitude=0.0;
    @ColumnInfo(name = "start_longitude")
    private double start_longitude=0.0;
    @ColumnInfo(name = "stop_latitude")
    private double stop_latitude=0.0;
    @ColumnInfo(name = "stop_longitude")
    private double stop_longitude=0.0;

    public int getAtt_key() {
        return att_key;
    }

    public void setAtt_key(int att_key) {
        this.att_key = att_key;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStop_time() {
        return stop_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdate_status() {
        return update_status;
    }

    public void setUpdate_status(String update_status) {
        this.update_status = update_status;
    }

    public double getStart_latitude() {
        return start_latitude;
    }

    public void setStart_latitude(double start_latitude) {
        this.start_latitude = start_latitude;
    }

    public double getStart_longitude() {
        return start_longitude;
    }

    public void setStart_longitude(double start_longitude) {
        this.start_longitude = start_longitude;
    }

    public double getStop_latitude() {
        return stop_latitude;
    }

    public void setStop_latitude(double stop_latitude) {
        this.stop_latitude = stop_latitude;
    }

    public double getStop_longitude() {
        return stop_longitude;
    }

    public void setStop_longitude(double stop_longitude) {
        this.stop_longitude = stop_longitude;
    }
}
