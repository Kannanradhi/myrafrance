package com.isteer.b2c.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isteer.b2c.config.B2CAppConstant;
import com.isteer.b2c.model.BillData;
import com.isteer.b2c.model.OrderData;
import com.isteer.b2c.model.PendingOrderData;

import java.util.ArrayList;
import java.util.List;

public class Gson_PendingOrders {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName(B2CAppConstant.KEY_DATA)
    @Expose
    private ArrayList<PendingOrderData> orderDataList = new ArrayList<>();

    public ArrayList<PendingOrderData> getOrderDataList() {
        return orderDataList;
    }

    public void setOrderDataList(ArrayList<PendingOrderData> orderDataList) {
        this.orderDataList = orderDataList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
