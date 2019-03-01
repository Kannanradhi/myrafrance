package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

public class OrderNewData {

    public static DiffUtil.ItemCallback<OrderNewData> DIFF_CALLBACK = new DiffUtil.ItemCallback<OrderNewData>() {

        @Override
        public boolean areItemsTheSame(OrderNewData oldItem, OrderNewData newItem) {
            return oldItem.getSo_item_key() == newItem.getSo_item_key();
        }

        @Override
        public boolean areContentsTheSame(OrderNewData oldItem, OrderNewData newItem) {
            return oldItem.equals(newItem);
        }
    };
    private String so_item_key;
    private String customer_key;
    private String contact_key;
    private String company_name;
    private String area_name;
    private String quantity;
    private String mi_name;
    private String pending_qty;
    private String ordered_qty;
    private String list_price;
    private String taxPercent;
    private String date;

    private String event_key;
    private String ordered_count_today = "";
    private String cus_key = "";
    private String cmkey;
    private String area = "";
    private String customer_name = "";
    private String status;
    private String count;
    private String mi_key;

    public String getSo_item_key() {
        return so_item_key;
    }

    public void setSo_item_key(String so_item_key) {
        this.so_item_key = so_item_key;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public String getContact_key() {
        return contact_key;
    }

    public void setContact_key(String contact_key) {
        this.contact_key = contact_key;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMi_name() {
        return mi_name;
    }

    public void setMi_name(String mi_name) {
        this.mi_name = mi_name;
    }

    public String getPending_qty() {
        return pending_qty;
    }

    public void setPending_qty(String pending_qty) {
        this.pending_qty = pending_qty;
    }

    public String getOrdered_qty() {
        return ordered_qty;
    }

    public void setOrdered_qty(String ordered_qty) {
        this.ordered_qty = ordered_qty;
    }

    public String getList_price() {
        return list_price;
    }

    public void setList_price(String list_price) {
        this.list_price = list_price;
    }

    public String getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(String taxPercent) {
        this.taxPercent = taxPercent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEvent_key() {
        return event_key;
    }

    public void setEvent_key(String event_key) {
        this.event_key = event_key;
    }

    public String getOrdered_count_today() {
        return ordered_count_today;
    }

    public void setOrdered_count_today(String ordered_count_today) {
        this.ordered_count_today = ordered_count_today;
    }

    public String getCus_key() {
        return cus_key;
    }

    public void setCus_key(String cus_key) {
        this.cus_key = cus_key;
    }

    public String getCmkey() {
        return cmkey;
    }

    public void setCmkey(String cmkey) {
        this.cmkey = cmkey;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMi_key() {
        return mi_key;
    }

    public void setMi_key(String mi_key) {
        this.mi_key = mi_key;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
