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


    /**
     * Constructor for HealthyLocation
     * @param name
     * @param address
     * @param zipCode
     * @param floor
     * @param unit
     * @param longitude
     * @param latitude
     * @param locationType
     */
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

    /**
     * Get the location name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the location name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the location address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the location address
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the location zipcode
     * @return zpcode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Set the location zipcode
     * @param zipCode
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Get the location Longitude
     * @return Longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Set the location logitude
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the location latitude
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Set the location latitude
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the location type
     * @return locationType
     */
    public String getLocationType() {
        return locationType;
    }

    /**
     * Set the location type
     * @param locationType
     */
    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    /**
     * Get the location floor
     * @return floor
     */
    public String getFloor() {
        return floor;
    }

    /**
     * Set the location floor
     * @param floor
     */
    public void setFloor(String floor) {
        this.floor = floor;
    }

    /**
     * Get the location unit no
     * @return unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Set the location unit no
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Get the location id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the location id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * For generate listview purposes
     * @return string of name with address
     */
    @Override
    public String toString() {
        return "\n" + this.name + "\r\n" + this.getAddress() + "\n";
    }
}
