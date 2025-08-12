package com.example.demo.payload;

import java.util.List;

public class UpdateSerialStatusRequest {
	private Integer prod_id;
    private Integer sold_status;
    private List<String> serialNos;
	public Integer getProd_id() {
		return prod_id;
	}
	public void setProd_id(Integer prod_id) {
		this.prod_id = prod_id;
	}
	public Integer getSold_status() {
		return sold_status;
	}
	public void setSold_status(Integer sold_status) {
		this.sold_status = sold_status;
	}
	public List<String> getSerialNos() {
		return serialNos;
	}
	public void setSerialNos(List<String> serialNos) {
		this.serialNos = serialNos;
	}
}
