package com.example.demo.response;

import java.util.List;

public class ProductDetailsDTO {
    private Integer prod_id;
    private String Model_no;
    private String Product_name;
    private List<String> productImages;
    private List<ProductSerialDTO> productSerials;
	public Integer getProd_id() {
		return prod_id;
	}
	public void setProd_id(Integer prod_id) {
		this.prod_id = prod_id;
	}
	public String getModel_no() {
		return Model_no;
	}
	public void setModel_no(String model_no) {
		Model_no = model_no;
	}
	public String getProduct_name() {
		return Product_name;
	}
	public void setProduct_name(String product_name) {
		Product_name = product_name;
	}
	public List<String> getProductImages() {
		return productImages;
	}
	public void setProductImages(List<String> productImages) {
		this.productImages = productImages;
	}
	public List<ProductSerialDTO> getProductSerials() {
		return productSerials;
	}
	public void setProductSerials(List<ProductSerialDTO> productSerials) {
		this.productSerials = productSerials;
	}

}
