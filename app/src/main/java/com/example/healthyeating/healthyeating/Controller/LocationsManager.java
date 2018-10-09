package com.example.healthyeating.healthyeating.Controller;

import android.util.Log;
import com.example.healthyeating.healthyeating.Entity.Location;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public class LocationsManager {

    private ArrayList<Location> listOfLocation;
    private int sortFilter = 0; //0 = A-Z, 1 = Z-A
    private double limitDistance = 5.0;

    public LocationsManager(){
        listOfLocation = new ArrayList<>();

    }

    public ArrayList<Location> sortList(ArrayList<Location> loc){
        ArrayList<Location> sortedList = new ArrayList<Location>();
        Collections.sort(loc, new Comparator<Location>() {
            public int compare(Location o1, Location o2) {
                if(sortFilter==0)
                return o1.getName().compareTo(o2.getName());
                else if(sortFilter==1)
                    return o2.getName().compareTo(o1.getName());
                else
                    return o1.getName().compareTo(o2.getName());
            }
        });

        return loc;
    }

    private String extractDetails(String msg,String searchTag, String endTag){
        String tempData = "";
        String result = "";
        int startTagPos = msg.indexOf(searchTag);
        tempData = msg.substring(startTagPos);
        int endTagPos = tempData.indexOf(endTag);
        result = msg.substring(startTagPos+searchTag.length(),startTagPos+endTagPos).trim();
        if(result.length()>0)
            return msg.substring(startTagPos+searchTag.length(),startTagPos+endTagPos).trim();
        else
            return "";
    }

    public Location getLocation(int id){
        return listOfLocation.get(id);
    }

    public void createLocation(ArrayList<String> data){
        //We assume that there is not much changes to the KML data format
        String address_building_name="";
        String address_blk_no="";
        String address_street_name="";
        String postal_code="";
        String floor="";
        String unit = "";
        double longitude = 0.0;
        double latitude = 0.0;

        String name = "";
        String address = "";


        for(int i = 0; i<data.size();i++){
            if (data.get(i).contains("<SimpleData name=\"NAME\">")) {
                name = extractDetails(data.get(i),"<SimpleData name=\"NAME\">", "</SimpleData>");
                name = name.replace("&amp;","&");
            }
            else if(data.get(i).contains("<SimpleData name=\"ADDRESSBLOCKHOUSENUMBER\">")) {
                address_blk_no = extractDetails(data.get(i),"<SimpleData name=\"ADDRESSBLOCKHOUSENUMBER\">", "</SimpleData>");
            }
            else if(data.get(i).contains("<SimpleData name=\"ADDRESSPOSTALCODE\">")) {
                postal_code = extractDetails(data.get(i),"<SimpleData name=\"ADDRESSPOSTALCODE\">", "</SimpleData>");
            }
            else if(data.get(i).contains("<SimpleData name=\"ADDRESSBUILDINGNAME\">")) {
                address_building_name = extractDetails(data.get(i),"<SimpleData name=\"ADDRESSBUILDINGNAME\">", "</SimpleData>");
            }
            else if(data.get(i).contains("<SimpleData name=\"ADDRESSSTREETNAME\">")) {
                address_street_name = extractDetails(data.get(i),"<SimpleData name=\"ADDRESSSTREETNAME\">", "</SimpleData>");
            }
            else if(data.get(i).contains("<SimpleData name=\"ADDRESSUNITNUMBER\">")) {
                unit = extractDetails(data.get(i),"<SimpleData name=\"ADDRESSUNITNUMBER\">", "</SimpleData>");
            }
            else if(data.get(i).contains("<SimpleData name=\"ADDRESSFLOORNUMBER\">")) {
                floor = extractDetails(data.get(i),"<SimpleData name=\"ADDRESSFLOORNUMBER\">", "</SimpleData>");
            }
            else if(data.get(i).contains("<coordinates>")) {
                String coordinate = extractDetails(data.get(i),"<coordinates>", "</coordinates>");
                longitude = Double.parseDouble(coordinate.split(",")[0]);
                latitude = Double.parseDouble(coordinate.split(",")[1]);
            }

        }

        address = address_building_name+","+address_blk_no+" "+address_street_name+" S"+postal_code;
        if(address.startsWith(","))
            address = address.substring(1);
       // Log.d("Location Details : ", name+" Address : "+address);
       // Log.d("Location coordinate :", longitude+","+latitude);

        Location loc = new Location(listOfLocation.size(),name,address,postal_code,  floor,  unit,  longitude,  latitude, "Eateries");
        listOfLocation.add(loc);

    }






    public ArrayList<Location> getListOfLocation(String type) {

        ArrayList<Location> res = new ArrayList<>();
        for(int i = 0; i<listOfLocation.size();i++){
            if(listOfLocation.get(i).getLocationType().equals(type))
                res.add(listOfLocation.get(i));
        }
        return res;
    }

    public void setListOfLocation(ArrayList<Location> listOfLocation) {
        this.listOfLocation = listOfLocation;
    }

    public int getSortFilter() {
        return sortFilter;
    }

    public void setSortFilter(int sortFilter) {
        this.sortFilter = sortFilter;
    }

    public double getLimitDistance() {
        return limitDistance;
    }

    public void setLimitDistance(double limitDistance) {
        this.limitDistance = limitDistance;
    }

    public ArrayList<Location> searchLocations(String type, String address){
       ArrayList<Location> results = new ArrayList<Location>();
       address = address.toLowerCase();
       for(int i = 0 ; i<listOfLocation.size();i++){
            if(listOfLocation.get(i).getLocationType().equals(type)) {
             String concat = listOfLocation.get(i).getName()+" "+listOfLocation.get(i).getAddress()+" "+listOfLocation.get(i).getZipCode();
             concat = concat.toLowerCase();
             String[] addressSplit = address.split(" ");
             boolean found = true;
             for(int j = 0; j<addressSplit.length;j++) {
                 if (concat.indexOf(addressSplit[j])==-1)
                       found = false;
             }
                if(found)
                results.add(listOfLocation.get(i));
        }
       }

        results = sortList(results);

       return results;
    }

    public int searchLocationID (String type, String address){
        //ArrayList<Location> results = new ArrayList<Location>();

        address = address.toLowerCase();
        for(int i = 0 ; i<listOfLocation.size();i++){
            if(listOfLocation.get(i).getLocationType().equals(type)) {
                String concat = listOfLocation.get(i).getName();
                concat = concat.toLowerCase();

                if (address.equals(concat)) {
                    return listOfLocation.get(i).getId();
                }
            }
        }
        return -1;
    }

    public ArrayList<Location> getSearchLocationID(int ID){
        ArrayList<Location> results = new ArrayList<Location>();

        for(int i = 0 ; i<listOfLocation.size();i++){
            if(listOfLocation.get(i).getId() == ID) {
               results.add(listOfLocation.get(i));
            }
        }
        return results;
    }

}
