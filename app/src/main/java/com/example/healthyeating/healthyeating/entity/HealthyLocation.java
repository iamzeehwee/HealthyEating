package com.example.healthyeating.healthyeating.entity;

public class HealthyLocation {

    private int id;
    private String name;
    private String address;
    private String floor;
    private String unit;
    private String zipCode;
    private double longitude;
    private double latitude;



    private String locationType;


    public HealthyLocation( String name, String address, String zipCode, String floor, String unit, double longitude, double latitude, String locationType){

        this.name = name;
        this.address= address;
        this.zipCode = zipCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationType = locationType;
        this.floor = floor;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "\n" + this.name + "\r\n" + this.getAddress() + "\n";
    }
}
