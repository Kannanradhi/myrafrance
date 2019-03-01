package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "b2c_contact_individual")
public class CustomerIndividual {

	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "con_key")
	private int con_key;
	@ColumnInfo(name = "contact_key")
	private String contact_key;
	@ColumnInfo(name = "first_name")
	private String first_name;
	@ColumnInfo(name = "last_name")
	private String last_name;
	@ColumnInfo(name = "sex")
	private String sex;
	@ColumnInfo(name = "dob")
	private String dob;
	@ColumnInfo(name = "wedding")
	private String wedding;
	@ColumnInfo(name = "company_name")
	private String company_name;
	@ColumnInfo(name = "designation")
	private String designation;
	@ColumnInfo(name = "address1")
	private String address1;
	@ColumnInfo(name = "address2")
	private String address2;
	@ColumnInfo(name = "area")
	private String area;
	@ColumnInfo(name = "city")
	private String city;
	@ColumnInfo(name = "state")
	private String state;
	@ColumnInfo(name = "country")
	private String country;
	@ColumnInfo(name = "email")
	private String email;
	@ColumnInfo(name = "phone1")
	private String phone1;
	@ColumnInfo(name = "phone2")
	private String phone2;
	@ColumnInfo(name = "pincode")
	private String pincode;
	@ColumnInfo(name = "unit_key")
	private String unit_key;
	@ColumnInfo(name = "se_key")
	private String se_key;
	@ColumnInfo(name = "entered_by")
	private String entered_by;
	@ColumnInfo(name = "entered_on")
	private String entered_on;
	@ColumnInfo(name = "last_modified_by")
	private String last_modified_by;
	@ColumnInfo(name = "last_modified_on")
	private String last_modified_on;
	@ColumnInfo(name = "active_status")
	private String active_status;
	@ColumnInfo(name = "latitude")
	private String latitude = "0.0";
	@ColumnInfo(name = "longitude")
	private String longitude = "0.0";
	@ColumnInfo(name = "altitude")
	private String altitude = "0.0";
	@ColumnInfo(name = "is_synced_to_server")
	private int is_synced_to_server = 0;

	public int getIs_synced_to_server() {
		return is_synced_to_server;
	}

	public void setIs_synced_to_server(int is_synced_to_server) {
		this.is_synced_to_server = is_synced_to_server;
	}

	public int getCon_key() {
		return con_key;
	}
	public void setCon_key(int con_key) {
		this.con_key = con_key;
	}
	public String getContact_key() {
		return contact_key;
	}
	public void setContact_key(String contact_key) {
		this.contact_key = contact_key;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getWedding() {
		return wedding;
	}
	public void setWedding(String wedding) {
		this.wedding = wedding;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone1() {
		return phone1;
	}
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	public String getPhone2() {
		return phone2;
	}
	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getUnit_key() {
		return unit_key;
	}
	public void setUnit_key(String unit_key) {
		this.unit_key = unit_key;
	}
	public String getSe_key() {
		return se_key;
	}
	public void setSe_key(String se_key) {
		this.se_key = se_key;
	}
	public String getEntered_by() {
		return entered_by;
	}
	public void setEntered_by(String entered_by) {
		this.entered_by = entered_by;
	}
	public String getEntered_on() {
		return entered_on;
	}
	public void setEntered_on(String entered_on) {
		this.entered_on = entered_on;
	}
	public String getLast_modified_by() {
		return last_modified_by;
	}
	public void setLast_modified_by(String last_modified_by) {
		this.last_modified_by = last_modified_by;
	}
	public String getLast_modified_on() {
		return last_modified_on;
	}
	public void setLast_modified_on(String last_modified_on) {
		this.last_modified_on = last_modified_on;
	}
	public String getActive_status() {
		return active_status;
	}
	public void setActive_status(String active_status) {
		this.active_status = active_status;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAltitude() {
		return altitude;
	}
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}
	
}
