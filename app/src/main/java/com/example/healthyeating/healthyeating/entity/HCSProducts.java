package com.example.healthyeating.healthyeating.entity;

public class HCSProducts {

    private int ID;
    public String category = "";
    private String productName = "";
    private String productWeight = "";
    private String brandName = "";
    private String companyName = "";

    public HCSProducts(String category, String productName, String productWeight, String brandName, String companyName) {
        this.category = category;
        this.productName = productName;
        this.productWeight = productWeight;
        this.brandName = brandName;
        this.companyName = companyName;
    }

    public String getProductName() {
        return productName;
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

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public int getID() {
        return ID;
    }

    public void setID() {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return  "Brand Name:  " + brandName + '\n' +
                "Product Weight:  " + productWeight + '\n' +
                "Company Name:  " + companyName + '\n' +
                "Category:  " + category + '\n';
    }

}

