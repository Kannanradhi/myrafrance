package com.isteer.b2c.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isteer.b2c.model.CreditData;
import com.isteer.b2c.model.PendingOrderData;

import java.util.ArrayList;
import java.util.List;

public class Gson_CustomerCredit {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private ArrayList<CreditData> creditDataList =new ArrayList<>();

    public ArrayList<CreditData> getCreditDataList() {
        return creditDataList;
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

    public void setCreditDataList(ArrayList<CreditData> creditDataList) {
        this.creditDataList = creditDataList;

    }
}
