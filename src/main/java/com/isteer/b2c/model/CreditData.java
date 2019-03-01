package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "b2c_customer_credits")
public class CreditData {
@PrimaryKey
@NonNull
	@ColumnInfo(name = "customer_key")
	private int customer_key;
	@ColumnInfo(name = "credit_days")
	private String credit_days = "0";
	@ColumnInfo(name = "credit_used")
	private String credit_used = "0.00";
	@ColumnInfo(name = "unmapped_amount")
	private String unmapped_amount = "0.00";
	@ColumnInfo(name = "suspense_amount ")
	private String suspense_amount = "0.00";
	@ColumnInfo(name = "max_credit_limit")
	private String max_credit_limit = "0.00";
	@ColumnInfo(name = "tin_no ")
	private String tin_no = "";
	@ColumnInfo(name = "contact_key")
	private String contact_key;
	@ColumnInfo(name = "available_credit")
	private String available_credit = "0.00";

	public int getCustomer_key() {
		return customer_key;
	}
	public void setCustomer_key(int customer_key) {
		this.customer_key = customer_key;
	}
	public String getCredit_days() {
		return credit_days;
	}
	public void setCredit_days(String credit_days) {
		this.credit_days = credit_days;
	}
	public String getCredit_used() {
		return credit_used;
	}
	public void setCredit_used(String credit_used) {
		this.credit_used = credit_used;
	}
	public String getUnmapped_amount() {
		return unmapped_amount;
	}
	public void setUnmapped_amount(String unmapped_amount) {
		this.unmapped_amount = unmapped_amount;
	}
	public String getSuspense_amount() {
		return suspense_amount;
	}
	public void setSuspense_amount(String suspense_amount) {
		this.suspense_amount = suspense_amount;
	}
	public String getMax_credit_limit() {
		return max_credit_limit;
	}
	public void setMax_credit_limit(String max_credit_limit) {
		this.max_credit_limit = max_credit_limit;
	}
	public String getAvailable_credit() {
		return available_credit;
	}
	public void setAvailable_credit(String available_credit) {
		this.available_credit = available_credit;
	}
	public String getContact_key() {
		return contact_key;
	}
	public void setContact_key(String contact_key) {
		this.contact_key = contact_key;
	}
	public String getTin_no() {
		return tin_no;
	}
	public void setTin_no(String tin_no) {
		this.tin_no = tin_no;
	}
}
