package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "product_serials")
public class ProductSerial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prod_id;

    @Column(name = "serial_no", nullable = false)
    private String serialNo;

    @Column(name = "is_sold")
    private Integer is_sold=0;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore  // Prevents infinite recursion
    private ProductDetails product;

    private String Model_No;
    // Getters and setters

    public String getModel_No() {
		return Model_No;
	}

	public void setModel_No(String model_No) {
		Model_No = model_No;
	}

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

	public ProductDetails getProduct() {
        return product;
    }

    public void setProduct(ProductDetails product) {
        this.product = product;
    }
}
