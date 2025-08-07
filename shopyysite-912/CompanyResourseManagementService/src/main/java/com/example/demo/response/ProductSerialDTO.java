package com.example.demo.response;

public class ProductSerialDTO {
    private Long prod_id;
    private String serialNo;
    private Integer is_sold;
    private String modelNo;
	public Long getProd_id() {
		return prod_id;
	}
	public void setProd_id(Long prod_id) {
		this.prod_id = prod_id;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public Integer getIs_sold() {
		return is_sold;
	}
	public void setIs_sold(Integer is_sold) {
		this.is_sold = is_sold;
	}
	public String getModelNo() {
		return modelNo;
	}
	public void setModelNo(String modelNo) {
		this.modelNo = modelNo;
	}

    // Getters & Setters
}
