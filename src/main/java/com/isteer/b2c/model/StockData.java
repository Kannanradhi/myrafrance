package com.isteer.b2c.model;

public class StockData {

	private String unit_name = "";
	private String unit_location = "";
	private double stock = 0;
	
	public String getUnit_name() {
		return unit_name;
	}
	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}
	public String getUnit_location() {
		return unit_location;
	}
	public void setUnit_location(String unit_location) {
		this.unit_location = unit_location;
	}
	public double getStock() {
		return stock;
	}
	public void setStock(double stock) {
		this.stock = stock;
	}
	
}
