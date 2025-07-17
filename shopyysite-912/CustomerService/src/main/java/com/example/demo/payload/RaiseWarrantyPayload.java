package com.example.demo.payload;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RaiseWarrantyPayload {
	@NotNull(message = "Customer ID is required")
	private Integer customer_id;
	@NotNull(message = "Request date is required")
	private LocalDate request_date;
	@NotBlank(message = "Customer name is required")
	private String customer_name;
	@NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
	private String customer_email;
	
	private String phone_number;
	@NotBlank(message = "Model number is required")
	private String model_no;
	@NotNull(message = "Purchase date is required")
	private LocalDate purchase_date;
    private List<String> product_images;
	@NotNull(message = "Company ID is required")
	private Integer company_id;
	@NotBlank(message = "Reason required")
    private String reason;

    public String getReason() {
    	return reason;
    }
    public void setReason(String reason) {
    	this.reason = reason;
    }
	public Integer getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Integer customer_id) {
		this.customer_id = customer_id;
	}
	public LocalDate getRequest_date() {
		return request_date;
	}
	public void setRequest_date(LocalDate request_date) {
		this.request_date = request_date;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getCustomer_email() {
		return customer_email;
	}
	public void setCustomer_email(String customer_email) {
		this.customer_email = customer_email;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
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
	 public List<String> getProduct_images() {
	        return product_images;
	    }

	    public void setProduct_images(List<String> product_images) {
	        this.product_images = product_images;
	    }
	public Integer getCompany_id() {
		return company_id;
	}
	public void setCompany_id(Integer company_id) {
		this.company_id = company_id;
	}
}
