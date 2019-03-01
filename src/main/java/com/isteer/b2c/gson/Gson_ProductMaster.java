package com.isteer.b2c.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isteer.b2c.model.ProductData;

import java.util.ArrayList;
import java.util.List;

public class Gson_ProductMaster {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    ArrayList<ProductData> productDataList = new ArrayList<>();

    public ArrayList<ProductData> getProductDataList() {
        return productDataList;
    }

    public void setProductDataList(ArrayList<ProductData> productDataList) {
        this.productDataList = productDataList;
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
