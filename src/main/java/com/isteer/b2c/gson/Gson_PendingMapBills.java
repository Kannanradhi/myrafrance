package com.isteer.b2c.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isteer.b2c.model.BillData;

import java.util.ArrayList;

public class Gson_PendingMapBills {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("bills")
    @Expose
    private ArrayList<BillData> billDataList = new ArrayList<>();

    public ArrayList<BillData> getBillDataList() {
        return billDataList;
    }

    public void setBillDataList(ArrayList<BillData> billDataList) {
        this.billDataList = billDataList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
