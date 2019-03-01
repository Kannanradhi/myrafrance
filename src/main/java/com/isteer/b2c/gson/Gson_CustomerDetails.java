package com.isteer.b2c.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isteer.b2c.model.CustomerData;

import java.util.ArrayList;
import java.util.List;

public class Gson_CustomerDetails {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private ArrayList<CustomerData> customerDataList = new ArrayList<CustomerData>();

    public ArrayList<CustomerData> getCustomerDataList() {
        return customerDataList;
    }

    public void setCustomerDataList(ArrayList<CustomerData> customerDataList) {
        this.customerDataList = customerDataList;
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
