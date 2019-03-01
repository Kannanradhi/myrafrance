package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "aerp_customer_wise_target")
public class SpancopData {
@PrimaryKey
@NonNull
	@ColumnInfo(name = "cp_key")
	private int cp_key;
	@ColumnInfo(name = "cmkey")
	private String cmkey;
	@ColumnInfo(name = "sekey")
	private String sekey;
	@ColumnInfo(name = "manu_code")
	private String manu_code;
	@ColumnInfo(name = "item_code")
	private String item_code;
	@ColumnInfo(name = "prod_key")
	private String prod_key;
	@ColumnInfo(name = "mapping_status")
	private String mapping_status;
	@ColumnInfo(name = "actual_qty")
	private String actual_qty;
	@ColumnInfo(name = "annual_qty")
	private String annual_qty;
	@ColumnInfo(name = "type")
	private String type;
	@ColumnInfo(name = "uom")
	private String uom;
	@ColumnInfo(name = "buying_frequency")
	private String buying_frequency;
	@ColumnInfo(name = "added_date")
	private String added_date;
	@ColumnInfo(name = "added_by")
	private String added_by;
	@ColumnInfo(name = "target_date")
	private String target_date;
	@ColumnInfo(name = "replaced_brand")
	private String replaced_brand;
	@ColumnInfo(name = "replaced_product")
	private String replaced_product;
	@ColumnInfo(name = "lost_date")
	private String lost_date;
	@ColumnInfo(name = "lost_reason")
	private String lost_reason;
	@ColumnInfo(name = "insert_stat")
	private String insert_stat;
	@ColumnInfo(name = "modified_by")
	private String modified_by;
	@ColumnInfo(name = "modified_on")
	private String modified_on;
	@ColumnInfo(name = "status")
	private String status;
	@ColumnInfo(name = "brand")
	private String brand;
	@ColumnInfo(name = "prod_name")
	private String prod_name;
	@ColumnInfo(name = "category_name")
	private String category_name;
	@ColumnInfo(name = "map_status")
	private String map_status;
	@ColumnInfo(name = "remarks")
	private String remarks;
	@ColumnInfo(name = "is_synced_to_server")
	private int is_synced_to_server = 1;
	
	public int getCp_key() {
		return cp_key;
	}
	public void setCp_key(int cp_key) {
		this.cp_key = cp_key;
	}
	public String getCmkey() {
		return cmkey;
	}
	public void setCmkey(String cmkey) {
		this.cmkey = cmkey;
	}
	public String getSekey() {
		return sekey;
	}
	public void setSekey(String sekey) {
		this.sekey = sekey;
	}
	public String getManu_code() {
		return manu_code;
	}
	public void setManu_code(String manu_code) {
		this.manu_code = manu_code;
	}
	public String getItem_code() {
		return item_code;
	}
	public void setItem_code(String item_code) {
		this.item_code = item_code;
	}
	public String getProd_key() {
		return prod_key;
	}
	public void setProd_key(String prod_key) {
		this.prod_key = prod_key;
	}
	public String getMapping_status() {
		return mapping_status;
	}
	public void setMapping_status(String mapping_status) {
		this.mapping_status = mapping_status;
	}
	public String getActual_qty() {
		return actual_qty;
	}
	public void setActual_qty(String actual_qty) {
		this.actual_qty = actual_qty;
	}
	public String getAnnual_qty() {
		return annual_qty;
	}
	public void setAnnual_qty(String annual_qty) {
		this.annual_qty = annual_qty;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getBuying_frequency() {
		return buying_frequency;
	}
	public void setBuying_frequency(String buying_frequency) {
		this.buying_frequency = buying_frequency;
	}
	public String getAdded_date() {
		return added_date;
	}
	public void setAdded_date(String added_date) {
		this.added_date = added_date;
	}
	public String getAdded_by() {
		return added_by;
	}
	public void setAdded_by(String added_by) {
		this.added_by = added_by;
	}
	public String getTarget_date() {
		return target_date;
	}
	public void setTarget_date(String target_date) {
		this.target_date = target_date;
	}
	public String getReplaced_brand() {
		return replaced_brand;
	}
	public void setReplaced_brand(String replaced_brand) {
		this.replaced_brand = replaced_brand;
	}
	public String getReplaced_product() {
		return replaced_product;
	}
	public void setReplaced_product(String replaced_product) {
		this.replaced_product = replaced_product;
	}
	public String getLost_date() {
		return lost_date;
	}
	public void setLost_date(String lost_date) {
		this.lost_date = lost_date;
	}
	public String getLost_reason() {
		return lost_reason;
	}
	public void setLost_reason(String lost_reason) {
		this.lost_reason = lost_reason;
	}
	public String getInsert_stat() {
		return insert_stat;
	}
	public void setInsert_stat(String insert_stat) {
		this.insert_stat = insert_stat;
	}
	public String getModified_by() {
		return modified_by;
	}
	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}
	public String getModified_on() {
		return modified_on;
	}
	public void setModified_on(String modified_on) {
		this.modified_on = modified_on;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public String getMap_status() {
		return map_status;
	}
	public void setMap_status(String map_status) {
		this.map_status = map_status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public int getIs_synced_to_server() {
		return is_synced_to_server;
	}
	public void setIs_synced_to_server(int is_synced_to_server) {
		this.is_synced_to_server = is_synced_to_server;
	}
	
}
