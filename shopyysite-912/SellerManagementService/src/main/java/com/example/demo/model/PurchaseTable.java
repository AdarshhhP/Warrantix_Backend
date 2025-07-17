package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "purchase_table")
public class PurchaseTable {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer sale_id;
@NotBlank(message = "Model number is required")
private String modelNo;
@NotNull(message = "Price is required")
private Integer Price;
private LocalDate purchase_date;
@NotNull(message = "Warranty is required")
@Min(value = 1, message = "Warranty must be at least 1")
private Integer Warranty;
private String Name;
@Email(message = "Email format is invalid")
@NotBlank(message="Email required")
@Email(message="Provid a proper email id")
private String Email;
private String Phono;
private Integer seller_id;
private Integer is_deleted=0;
public Integer getIs_deleted() {
	return is_deleted;
}
public void setIs_deleted(Integer is_deleted) {
	this.is_deleted = is_deleted;
}
public Integer getSeller_id() {
	return seller_id;
}
public void setSeller_id(Integer seller_id) {
	this.seller_id = seller_id;
}
public Integer getSale_id() {
	return sale_id;
}
public void setSale_id(Integer sale_id) {
	this.sale_id = sale_id;
}

public String getModelNo() {
	return modelNo;
}
public void setModelNo(String modelNo) {
	this.modelNo = modelNo;
}
public Integer getPrice() {
	return Price;
}
public void setPrice(Integer price) {
	Price = price;
}
public LocalDate getPurchase_date() {
	return purchase_date;
}
public void setPurchase_date(LocalDate purchase_date) {
	this.purchase_date = purchase_date;
}
public Integer getWarranty() {
	return Warranty;
}
public void setWarranty(Integer warranty) {
	Warranty = warranty;
}
public String getName() {
	return Name;
}
public void setName(String name) {
	Name = name;
}
public String getEmail() {
	return Email;
}
public void setEmail(String email) {
	Email = email;
}
public String getPhono() {
	return Phono;
}
public void setPhono(String phono) {
	Phono = phono;
}
}
