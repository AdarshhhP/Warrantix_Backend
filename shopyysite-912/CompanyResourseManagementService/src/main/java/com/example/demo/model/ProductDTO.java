package com.example.demo.model;
import java.time.LocalDate;

public class ProductDTO {
    private String model_no;
    private String product_name;
    private String product_category;
    private Integer product_price;
    private LocalDate man_date;
    private Integer warrany_tenure;
    private Integer company_id;
    private Integer holderStatus = 1;
	public String getModel_no() {
		return model_no;
	}
	public void setModel_no(String model_no) {
		this.model_no = model_no;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getProduct_category() {
		return product_category;
	}
	public void setProduct_category(String product_category) {
		this.product_category = product_category;
	}
	public Integer getProduct_price() {
		return product_price;
	}
	public void setProduct_price(Integer product_price) {
		this.product_price = product_price;
	}
	public LocalDate getMan_date() {
		return man_date;
	}
	public void setMan_date(LocalDate man_date) {
		this.man_date = man_date;
	}
	public Integer getWarrany_tenure() {
		return warrany_tenure;
	}
	public void setWarrany_tenure(Integer warrany_tenure) {
		this.warrany_tenure = warrany_tenure;
	}
	public Integer getCompany_id() {
		return company_id;
	}
	public void setCompany_id(Integer company_id) {
		this.company_id = company_id;
	}
	public Integer getHolderStatus() {
		return holderStatus;
	}
	public void setHolderStatus(Integer holderStatus) {
		this.holderStatus = holderStatus;
	}

}
