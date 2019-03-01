package com.isteer.b2c.model;

public class OrderData {

	private long mikey = 0;
	private String part_no = "";
	private String display_code = "";
	private String manu_name = "";
	private int qty = 0;
	private double price = 0.0;
	private String tax_percent = "";
	private double total = 0.0;
	private String remark = "";







	public long getMikey() {
		return mikey;
	}
	public void setMikey(long mikey) {
		this.mikey = mikey;
	}
	public String getPart_no() {
		return part_no;
	}
	public void setPart_no(String part_no) {
		this.part_no = part_no;
	}
	public String getDisplay_code() {
		return display_code;
	}
	public void setDisplay_code(String display_code) {
		this.display_code = display_code;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getManu_name() {
		return manu_name;
	}
	public void setManu_name(String manu_name) {
		this.manu_name = manu_name;
	}
	public String getTax_percent() {
		return tax_percent;
	}
	public void setTax_percent(String tax_percent) {
		this.tax_percent = tax_percent;
	}


}
