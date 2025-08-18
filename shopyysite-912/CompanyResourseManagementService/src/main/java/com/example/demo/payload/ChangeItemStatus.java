package com.example.demo.payload;

import java.util.List;

public class ChangeItemStatus {

	private String ModelNo;
	private Integer ItemStatus;
	public Integer getItemStatus() {
		return ItemStatus;
	}

	public void setItemStatus(Integer itemStatus) {
		ItemStatus = itemStatus;
	}

	public String getModelNo() {
		return ModelNo;
	}
	
	public void setModelNo(String modelNo) {
		ModelNo = modelNo;
	}
	public List<String> getSerialNos() {
		return SerialNos;
	}
	public void setSerialNos(List<String> serialNos) {
		SerialNos = serialNos;
	}
	private List<String> SerialNos;
}
