package com.isteer.b2c.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.isteer.b2c.model.CollectionData;
import com.isteer.b2c.model.CustomerData;
import com.isteer.b2c.model.CustomerIndividual;

import java.util.ArrayList;
import java.util.List;

public class Gson_CustomerCollection {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private ArrayList<CollectionData> collectionList = new ArrayList<>();


    public ArrayList<CollectionData> getCollectionList() {
        return collectionList;
    }

    public void setCollectionList(ArrayList<CollectionData> collectionList) {
        this.collectionList = collectionList;
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
