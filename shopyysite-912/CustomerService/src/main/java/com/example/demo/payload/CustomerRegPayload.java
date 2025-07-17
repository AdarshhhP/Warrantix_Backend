package com.example.demo.payload;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CustomerRegPayload {
	@NotNull(message = "Customer ID is required")
	private Integer customerId;
	@NotBlank(message = "Model number is required")
	private String model_no;
	@NotNull(message = "Purchase date is required")
	private LocalDate purchase_date;
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public String getModel_no() {
		return model_no;
	}
	public void setModel_no(String model_no) {
		this.model_no = model_no;
	}
	public LocalDate getPurchase_date() {
		return purchase_date;
	}
	public void setPurchase_date(LocalDate purchase_date) {
		this.purchase_date = purchase_date;
	}	
	
}
