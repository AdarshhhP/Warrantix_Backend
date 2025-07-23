package com.example.demo.response;

import java.util.List;

public class ResponseModelData {

    private Integer prod_id;
    private List<String> productImages;
    private String product_name;
    private String man_date;
    private String model_no;
    private Integer product_price;
    private Integer warrany_tenure;
    private String product_category;
    private Integer holderStatus;

    private Integer company_id;

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }
    
    // Getters and Setters

    public Integer getProd_id() {
        return prod_id;
    }

    public void setProd_id(Integer prod_id) {
        this.prod_id = prod_id;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getMan_date() {
        return man_date;
    }

    public void setMan_date(String man_date) {
        this.man_date = man_date;
    }

    public String getModel_no() {
        return model_no;
    }

    public void setModel_no(String model_no) {
        this.model_no = model_no;
    }

    public Integer getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Integer product_price) {
        this.product_price = product_price;
    }

    public Integer getWarrany_tenure() {
        return warrany_tenure;
    }

    public void setWarrany_tenure(Integer warrany_tenure) {
        this.warrany_tenure = warrany_tenure;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public Integer getHolderStatus() {
        return holderStatus;
    }

    public void setHolderStatus(Integer holderStatus) {
        this.holderStatus = holderStatus;
    }
}
