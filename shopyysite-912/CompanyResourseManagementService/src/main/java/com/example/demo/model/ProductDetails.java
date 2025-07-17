	package com.example.demo.model;
	import jakarta.persistence.JoinColumn;
	import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
	import jakarta.persistence.GeneratedValue;
	import jakarta.persistence.GenerationType;
	import jakarta.persistence.Id;
	import jakarta.persistence.Table;
	import jakarta.validation.constraints.Min;
	import jakarta.validation.constraints.NotBlank;
	import jakarta.validation.constraints.NotNull;
	import jakarta.validation.constraints.PastOrPresent;
	import jakarta.validation.constraints.Size;
	
	@Entity
	@Table(name = "product_table")
	public class ProductDetails {
		   
			@Id
			@GeneratedValue(strategy = GenerationType.IDENTITY)
			private Integer prod_id; 
	
			@NotBlank(message = "Model number is required")
			@Size(min = 2, max = 50, message = "Model number must be between 2 and 50 characters")
			private String Model_no; 
	
			@NotBlank(message = "Product name is required")
			@Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
			private String Product_name; 
	
			@NotBlank(message = "Product category is required")
			private String Product_category; 
	
			@NotNull(message = "Product price is required")
			@Min(value = 1, message = "Product price must be at least 1")
			private Integer Product_price; 
	
			@NotNull(message = "Manufacture date is required")
			@PastOrPresent(message = "Manufacture date cannot be in the future")
			private LocalDate Man_date; 
	
			@NotNull(message = "Warranty tenure is required")
			@Min(value = 0, message = "Warranty tenure cannot be negative")
			private Integer Warrany_tenure; 
	
			 @ElementCollection
			    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "prod_id"))
			    @Column(name = "image_url") // Increased length for base64
			    private List<String> productImages = new ArrayList<>();
			
			@NotNull(message = "Company ID is required")
			private Integer Company_id; 
			
			private Integer HolderStatus=1;
	
		    public Integer getProd_id() {
				return prod_id;
			}
			public Integer getHolderStatus() {
				return HolderStatus;
			}
			public void setHolderStatus(Integer holderStatus) {
				HolderStatus = holderStatus;
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
			public String getProduct_category() {
				return Product_category;
			}
			public void setProduct_category(String product_category) {
				Product_category = product_category;
			}
			public Integer getProduct_price() {
				return Product_price;
			}
			public void setProduct_price(Integer product_price) {
				Product_price = product_price;
			}
			public LocalDate getMan_date() {
				return Man_date;
			}
			public void setMan_date(LocalDate man_date) {
				Man_date = man_date;
			}
			public Integer getWarrany_tenure() {
				return Warrany_tenure;
			}
			public void setWarrany_tenure(Integer warrany_tenure) {
				Warrany_tenure = warrany_tenure;
			}
			 public List<String> getProductImages() {
			        return productImages;
			    }
			    
			    public void setProductImages(List<String> productImages) {
			        this.productImages = productImages;
			    }
			    
			    
			public Integer getCompany_id() {
				return Company_id;
			}
			public void setCompany_id(Integer company_id) {
				Company_id = company_id;
			}
	
	}
