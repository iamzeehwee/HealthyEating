package com.example.healthyeating.healthyeating.Entity;

public class HCSProducts {

    private int id;
    private String category;
    private String companyName;
    private String productName;
    private String brandName;
    private String productWeight;

    public HCSProducts(int id, String category, String companyName, String productName, String brandName, String productWeight){
        this.id = id;
        this.category = category;
        this.companyName = companyName;
        this.productName = productName;
        this.brandName = brandName;
        this.productWeight = productWeight;

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {this.productWeight = productWeight;}

    }



