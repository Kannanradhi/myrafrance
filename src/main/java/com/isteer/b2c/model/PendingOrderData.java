package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

@Entity(tableName = "b2c_pending_orders")
public class PendingOrderData {

	public static DiffUtil.ItemCallback<PendingOrderData> DIFF_CALLBACK = new DiffUtil.ItemCallback<PendingOrderData>() {
		@Override
		public boolean areItemsTheSame(@NonNull PendingOrderData oldItem, @NonNull PendingOrderData newItem) {
			return oldItem.getSo_item_key() == newItem.getSo_item_key();
		}

		@Override
		public boolean areContentsTheSame(@NonNull PendingOrderData oldItem, @NonNull PendingOrderData newItem) {
			return oldItem.equals(newItem);
		}
	};
	@ColumnInfo(name = "so_key")

	private int so_key;
	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "so_item_key")
	private int so_item_key;
	@ColumnInfo(name = "customer_key")
	private String customer_key;
	@ColumnInfo(name = "mi_key")
	private String mi_key;
	@ColumnInfo(name = "mi_name")
	private String mi_name;
	@ColumnInfo(name = "tax_percent")
	private String tax_percent = "0.00";
	@ColumnInfo(name = "date")
	private String date;
	@ColumnInfo(name = "ordered_qty")
	private String ordered_qty;
	@ColumnInfo(name = "contact_key")
	private String contact_key;
	@ColumnInfo(name = "pending_qty")
	private String pending_qty;
	@ColumnInfo(name = "purchase_order_type")
	private String purchase_order_type;
	@ColumnInfo(name = "is_synced_to_server")
	private int is_synced_to_server = 1;
	@ColumnInfo(name = "entered_on")
	private String entered_on;

	public int getSo_key() {
		return so_key;
	}
	public void setSo_key(int so_key) {
		this.so_key = so_key;
	}
	public int getSo_item_key() {
		return so_item_key;
	}
	public void setSo_item_key(int so_item_key) {
		this.so_item_key = so_item_key;
	}
	public String getCustomer_key() {
		return customer_key;
	}
	public void setCustomer_key(String customer_key) {
		this.customer_key = customer_key;
	}
	public String getMi_key() {
		return mi_key;
	}
	public void setMi_key(String mi_key) {
		this.mi_key = mi_key;
	}
	public String getMi_name() {
		return mi_name;
	}
	public void setMi_name(String mi_name) {
		this.mi_name = mi_name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getOrdered_qty() {
		return ordered_qty;
	}
	public void setOrdered_qty(String ordered_qty) {
		this.ordered_qty = ordered_qty;
	}
	public String getPending_qty() {
		return pending_qty;
	}
	public void setPending_qty(String pending_qty) {
		this.pending_qty = pending_qty;
	}
	public String getContact_key() {
		return contact_key;
	}
	public void setContact_key(String contact_key) {
		this.contact_key = contact_key;
	}
	public String getTax_percent() {
		return tax_percent;
	}
	public void setTax_percent(String tax_percent) {
		this.tax_percent = tax_percent;
	}
	public String getPurchase_order_type() {
		return purchase_order_type;
	}
	public void setPurchase_order_type(String purchase_order_type) {
		this.purchase_order_type = purchase_order_type;
	}

	public int getIs_synced_to_server() {
		return is_synced_to_server;
	}

	public void setIs_synced_to_server(int is_synced_to_server) {
		this.is_synced_to_server = is_synced_to_server;
	}

	public String getEntered_on() {
		return entered_on;
	}

	public void setEntered_on(String entered_on) {
		this.entered_on = entered_on;
	}
}
