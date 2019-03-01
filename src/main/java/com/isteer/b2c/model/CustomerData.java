package com.isteer.b2c.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "b2c_contact_master")
public class CustomerData {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "cmkey")
    @SerializedName("cmkey")
    @Expose
    private int cmkey;
    @ColumnInfo(name = "GST_NO")
    @SerializedName("gst_no")
    @Expose
    private String GST_NO;
    @ColumnInfo(name = "userkey")
    @SerializedName("userkey")
    @Expose
    private String userkey;
    @ColumnInfo(name = "cmp_phone1")
    @SerializedName("cmp_phone1")
    @Expose
    private String cmp_phone1;
    @ColumnInfo(name = "cmp_phone2")
    @SerializedName("cmp_phone2")
    @Expose
    private String cmp_phone2;
    @ColumnInfo(name = "cmp_email")
    @SerializedName("cmp_email")
    @Expose
    private String cmp_email;
    @ColumnInfo(name = "company_name")
    @SerializedName("company_name")
    @Expose
    private String company_name;
    @ColumnInfo(name = "address1")
    @SerializedName("address1")
    @Expose
    private String address1;
    @ColumnInfo(name = "address2")
    @SerializedName("address2")
    @Expose
    private String address2;
    @ColumnInfo(name = "address3")
    @SerializedName("address3")
    @Expose
    private String address3;
    @ColumnInfo(name = "area")
    @SerializedName("area")
    @Expose
    private String area;
    @ColumnInfo(name = "city")
    @SerializedName("city")
    @Expose
    private String city;
    @ColumnInfo(name = "state")
    @SerializedName("state")
    @Expose
    private String state;
    @ColumnInfo(name = "country")
    @SerializedName("country")
    @Expose
    private String country;
    @ColumnInfo(name = "pincode")
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @ColumnInfo(name = "email")
    @SerializedName("email")
    @Expose
    private String email;
    @ColumnInfo(name = "phone1")
    @SerializedName("phone1")
    @Expose
    private String phone1;
    @ColumnInfo(name = "phone2")
    @SerializedName("phone2")
    @Expose
    private String phone2;
    @ColumnInfo(name = "website")
    @SerializedName("website")
    @Expose
    private String website;
    @ColumnInfo(name = "industry")
    @SerializedName("industry")
    @Expose
    private String industry;
    @ColumnInfo(name = "category1")
    @SerializedName("category1")
    @Expose
    private String category1;
    @ColumnInfo(name = "category2")
    @SerializedName("category2")
    @Expose
    private String category2;
    @ColumnInfo(name = "category3")
    @SerializedName("category3")
    @Expose
    private String category3;
    @ColumnInfo(name = "display_code")
    @SerializedName("display_code")
    @Expose
    private String display_code;
    @ColumnInfo(name = "area_name")
    @SerializedName("area_name")
    @Expose
    private String area_name;
    @ColumnInfo(name = "first_name")
    @SerializedName("first_name")
    @Expose
    private String first_name;
    @ColumnInfo(name = "cus_key")
    @SerializedName("cus_key")
    @Expose
    private String cus_key;
    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    @Expose
    private String latitude = "0.0";
    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    @Expose
    private String longitude = "0.0";
    @ColumnInfo(name = "is_synced_to_server")
    private int is_synced_to_server = 1;
    @ColumnInfo(name = "max_credit_limit")
    private String max_credit_limit = "0.00";
    @ColumnInfo(name = "available_credit")
    private String available_credit = "0.00";
    @ColumnInfo(name = "credit_days")
    private int credit_days = 0;
    @ColumnInfo(name = "amount_exceed")
    private String amount_exceed = "0.00";


    public int getIs_synced_to_server() {
        return is_synced_to_server;
    }

    public void setIs_synced_to_server(int is_synced_to_server) {
        this.is_synced_to_server = is_synced_to_server;
    }




    public Integer getCmkey() {
        return cmkey;
    }

    public void setCmkey(int cmkey) {
        this.cmkey = cmkey;
    }

    public String getGST_NO() {
        return GST_NO;
    }

    public void setGST_NO(String GST_NO) {
        this.GST_NO = GST_NO;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public String getCmp_phone1() {
        return cmp_phone1;
    }

    public void setCmp_phone1(String cmp_phone1) {
        this.cmp_phone1 = cmp_phone1;
    }

    public String getCmp_phone2() {
        return cmp_phone2;
    }

    public void setCmp_phone2(String cmp_phone2) {
        this.cmp_phone2 = cmp_phone2;
    }

    public String getCmp_email() {
        return cmp_email;
    }

    public void setCmp_email(String cmp_email) {
        this.cmp_email = cmp_email;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public String getDisplay_code() {
        return display_code;
    }

    public void setDisplay_code(String display_code) {
        this.display_code = display_code;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getCus_key() {
        return cus_key;
    }

    public void setCus_key(String cus_key) {
        this.cus_key = cus_key;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMax_credit_limit() {
        return max_credit_limit;
    }

    public void setMax_credit_limit(String max_credit_limit) {
        this.max_credit_limit = max_credit_limit;
    }

    public String getAvailable_credit() {
        return available_credit;
    }

    public void setAvailable_credit(String available_credit) {
        this.available_credit = available_credit;
    }

    public int getCredit_days() {
        return credit_days;
    }

    public void setCredit_days(int credit_days) {
        this.credit_days = credit_days;
    }

    public String getAmount_exceed() {
        return amount_exceed;
    }

    public void setAmount_exceed(String amount_exceed) {
        this.amount_exceed = amount_exceed;
    }
}
