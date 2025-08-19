package com.example.demo.response;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InventoryPost {
    
    @NotBlank(message = "Model number is required")
    private String model_no;
    
    @NotNull(message = "Company ID is required")
    private Integer company_id;
    
    @NotNull(message = "Category ID is required")
    private Integer category_id;
    
    private LocalDate purchase_date;
    
    @NotNull(message = "Price is required")
    private Integer price;
    
    @NotNull(message = "Warranty is required")
    @Min(value = 1, message = "Warranty must be at least 1 month")
    private Integer warranty;
    
    @NotNull(message = "Seller ID is required")
    private Integer seller_id;
    
    private Integer is_sold;
    
    public Integer getIs_sold() {
		return is_sold;
	}

	public void setIs_sold(Integer is_sold) {
		this.is_sold = is_sold;
	}

	private List<String> serial_no;

    public List<String> getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(List<String> serial_no) {
		this.serial_no = serial_no;
	}

	// Getters and Setters
    public String getModel_no() {
        return model_no;
    }

    public void setModel_no(String model_no) {
        this.model_no = model_no;
    }

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public LocalDate getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(LocalDate purchase_date) {
        this.purchase_date = purchase_date;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getWarranty() {
        return warranty;
    }

    public void setWarranty(Integer warranty) {
        this.warranty = warranty;
    }

    public Integer getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(Integer seller_id) {
        this.seller_id = seller_id;
    }
}
