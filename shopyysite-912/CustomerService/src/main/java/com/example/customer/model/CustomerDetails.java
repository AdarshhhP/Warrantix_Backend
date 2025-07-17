package com.example.customer.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="warranty_register_table")
public class CustomerDetails {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer purchase_Id;
	 @NotNull(message = "Customer ID is required")
	    private Integer customerId;

	    @NotBlank(message = "Model number is required")
	    private String model_no;

	    @NotNull(message = "Purchase date is required")
	    private LocalDate purchase_date;

	    @NotNull
	    private Integer isDeleted = 0;
	    
	   
	public Integer getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}
	public Integer getPurchase_Id() {
		return purchase_Id;
	}
	public void setPurchase_Id(Integer purchase_Id) {
		this.purchase_Id = purchase_Id;
	}
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
