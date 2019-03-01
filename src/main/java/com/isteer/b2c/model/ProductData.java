package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "b2c_product_master")
public class ProductData {


	public static DiffUtil.ItemCallback<ProductData> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProductData>() {
		@Override
		public boolean areItemsTheSame(@NonNull ProductData oldItem, @NonNull ProductData newItem) {
			return oldItem.getMikey() == newItem.getMikey();
		}

		@Override
		public boolean areContentsTheSame(@NonNull ProductData oldItem, @NonNull ProductData newItem) {
			return oldItem.equals(newItem);
		}
	};

@PrimaryKey
@NonNull
@ColumnInfo(name = "mikey")
@SerializedName("mikey")
private long mikey = 0;
@ColumnInfo(name = "part_no")
@SerializedName("part_no")
private String part_no = "";
@ColumnInfo(name = "display_code")
@SerializedName("display_code")
private String display_code = "";
@ColumnInfo(name = "remark")
@SerializedName("remark")
private String remark = "";
@ColumnInfo(name = "color_name")
@SerializedName("color_name")
private String color_name = "";
@ColumnInfo(name = "list_price")
@SerializedName("list_price")
private double list_price = 0.0;
@ColumnInfo(name = "standard_price")
@SerializedName("standard_price")
private double standard_price = 0.0;
@ColumnInfo(name = "mrp_rate")
@SerializedName("mrp_rate")
private double mrp_rate = 0.0;
@ColumnInfo(name = "taxPercent")
@SerializedName("taxPercent")
private double taxPercent = 0.0;
@ColumnInfo(name = "manu_name")
@SerializedName("manu_name")
private String manu_name = "";
@ColumnInfo(name = "category")
@SerializedName("category")
private String category = "";
@Ignore
private String qty = "";
	
	public long getMikey() {
		return mikey;
	}
	public void setMikey(long mikey) {
		this.mikey = mikey;
	}
	public String getPart_no() {
		return part_no;
	}
	public void setPart_no(String part_no) {
		this.part_no = part_no;
	}
	public String getDisplay_code() {
		return display_code;
	}
	public void setDisplay_code(String display_code) {
		this.display_code = display_code;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getColor_name() {
		return color_name;
	}
	public void setColor_name(String color_name) {
		this.color_name = color_name;
	}
	public double getList_price() {
		return list_price;
	}
	public void setList_price(double list_price) {
		this.list_price = list_price;
	}
	public double getStandard_price() {
		return standard_price;
	}
	public void setStandard_price(double standard_price) {
		this.standard_price = standard_price;
	}
	public double getMrp_rate() {
		return mrp_rate;
	}
	public void setMrp_rate(double mrp_rate) {
		this.mrp_rate = mrp_rate;
	}
	public double getTaxPercent() {
		return taxPercent;
	}
	public void setTaxPercent(double taxPercent) {
		this.taxPercent = taxPercent;
	}
	public String getManu_name() {
		return manu_name;
	}
	public void setManu_name(String manu_name) {
		this.manu_name = manu_name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}
}
