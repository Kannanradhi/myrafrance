package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "aerp_location_log")
public class LocationData {
	@ColumnInfo(name = "loc_key")
	@PrimaryKey(autoGenerate = true)
	private int loc_key;
	@ColumnInfo(name = "user_key")
	private String user_key;
	@ColumnInfo(name = "date_time")
	private String date_time;
@ColumnInfo(name = "altitude")
	private double altitude=0.0;
	@ColumnInfo(name = "latitude")
	private double latitude=0.0;
	@ColumnInfo(name = "longitude")
	private double longitude=0.0;
	@ColumnInfo(name = "update_status")
	private String update_status;
	@ColumnInfo(name = "battery_level")
	private int battery_level;


	public int getLoc_key() {
		return loc_key;
	}

	public void setLoc_key(int loc_key) {
		this.loc_key = loc_key;
	}

	public String getUser_key() {
		return user_key;
	}

	public void setUser_key(String user_key) {
		this.user_key = user_key;
	}

	public String getDate_time() {
		return date_time;
	}

	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}

	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getUpdate_status() {
		return update_status;
	}

	public void setUpdate_status(String update_status) {
		this.update_status = update_status;
	}

	public int getBattery_level() {
		return battery_level;
	}

	public void setBattery_level(int battery_level) {
		this.battery_level = battery_level;
	}
}
