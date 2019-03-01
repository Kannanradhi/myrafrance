package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Entity(tableName = "b2c_customer_pending_bills")
public class BillData {
    public static DiffUtil.ItemCallback<BillData> DIFF_CALLBACK = new DiffUtil.ItemCallback<BillData>() {
        @Override
        public boolean areItemsTheSame(@NonNull BillData oldItem, @NonNull BillData newItem) {
            return oldItem.getInvoice_key() == newItem.getInvoice_key();
        }

        @Override
        public boolean areContentsTheSame(@NonNull BillData oldItem, @NonNull BillData newItem) {
            return oldItem.equals(newItem);
        }
    };



    @ColumnInfo(name = "invoice_key")
    @SerializedName("invoice_key")
    @PrimaryKey
    @NonNull
    private int invoice_key;
    @ColumnInfo(name = "customer_key")
    @SerializedName("customer_key")
    private String customer_key;
    @ColumnInfo(name = "invoice_no")
    @SerializedName("invoice_no")
    private String invoice_no;
    @ColumnInfo(name = "inv_date")
    @SerializedName("inv_date")
    private String inv_date;
    @ColumnInfo(name = "invoice_amount")
    @SerializedName("invoice_amount")
    private String invoice_amount;
    @ColumnInfo(name = "pending_amount")
    @SerializedName("pending_amount")
    private String pending_amount;
    @ColumnInfo(name = "contact_key")
    @SerializedName("contact_key")
    private String contact_key;
    @ColumnInfo(name = "type")
    @SerializedName("type")
    private String type;
    @Ignore
    private String map_bill_amount;


    public int getInvoice_key() {
        return invoice_key;
    }

    public void setInvoice_key(int invoice_key) {
        this.invoice_key = invoice_key;
    }

    public String getCustomer_key() {
        return customer_key;
    }

    public void setCustomer_key(String customer_key) {
        this.customer_key = customer_key;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getInv_date() {
        return inv_date;
    }

    public void setInv_date(String inv_date) {
        this.inv_date = inv_date;
    }

    public String getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(String invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public String getPending_amount() {
        return pending_amount;
    }

    public void setPending_amount(String pending_amount) {
        this.pending_amount = pending_amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContact_key() {
        return contact_key;
    }

    public void setContact_key(String contact_key) {
        this.contact_key = contact_key;
    }

    public String getMap_bill_amount() {
        return map_bill_amount;
    }

    public void setMap_bill_amount(String map_bill_amount) {
        this.map_bill_amount = map_bill_amount;
    }
}
