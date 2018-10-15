package com.example.healthyeating.healthyeating.Controller;

import com.example.healthyeating.healthyeating.Boundary.MainActivity;
import com.example.healthyeating.healthyeating.Entity.HealthyLocation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class LocationsManager {

    private Context context;
    private ArrayList<HealthyLocation> listOfHealthyLocation;
    private int sortFilter = 0; //0 = A-Z, 1 = Z-A
    private double limitDistance = 50000.0;
    private String locationType = "Eateries";
    private double current_lat = -1;
    private double current_long = -1;
    private boolean isLatLngSet = false;
    private String favFileName = "FavouriteListData";
    private ArrayList<HealthyLocation> favouriteList;

    public LocationsManager(){
        listOfHealthyLocation = new ArrayList<>();
        favouriteList = new ArrayList<>();
    }

    public double getCurrent_lat() {
        return current_lat;
    }

    public double getCurrent_long() {
        return current_long;
    }

    public ArrayList<HealthyLocation> sortList(ArrayList<HealthyLocation> loc){
        ArrayList<HealthyLocation> sortedList = new ArrayList<HealthyLocation>();
        Collections.sort(loc, new Comparator<HealthyLocation>() {
            public int compare(HealthyLocation o1, HealthyLocation o2) {
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

    public HealthyLocation getLocation(int id){
        return listOfHealthyLocation.get(id);
    }

    public void setLocationType(String locationType){
        this.locationType = locationType;
    }
    public String getLocationType(){
        return locationType;
    }
    public void createLocation(ArrayList<String> data, String locationType){
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

        address = address_building_name+", "+address_blk_no+" "+address_street_name;
        if(floor.length()>0 || unit.length()>0) {
            address += " #";
            if(floor.length()>0){
                address+= String.format("%2s", floor).replace(' ', '0');
            }
            if(unit.length()>0){
                address+="-"+String.format("%2s", unit).replace(' ', '0');;

            }
        }

          address+=" S"+postal_code;
        if(address.startsWith(","))
            address = address.substring(1);


        HealthyLocation loc = new HealthyLocation(listOfHealthyLocation.size(),name,address,postal_code,  floor,  unit,  longitude,  latitude, locationType);
        listOfHealthyLocation.add(loc);

    }

    public boolean setCurrentLatLng(double lat, double longitude){
        //Validation of lat long.
       boolean changed = false;
        if( (lat>=0 && lat<=90) && (longitude >=0 && longitude<=180)) {
            if (lat != current_lat || longitude != current_long) {
                changed = true;
            }
            current_lat = lat;
            current_long = longitude;
            isLatLngSet = true;


        }

        return changed;
   }

    public ArrayList<HealthyLocation> getListOfLocation() {

        ArrayList<HealthyLocation> res = new ArrayList<>();
        for(int i = 0; i< listOfHealthyLocation.size(); i++){
            if(listOfHealthyLocation.get(i).getLocationType().equals(locationType)) {
                if(isWithinRange(listOfHealthyLocation.get(i))){
                    res.add(listOfHealthyLocation.get(i));
                }

            }
        }
        return res;
    }

    public void setListOfHealthyLocation(ArrayList<HealthyLocation> listOfHealthyLocation) {
        this.listOfHealthyLocation = listOfHealthyLocation;
    }

    public void setSortFilter(int sortFilter) {
        this.sortFilter = sortFilter;
    }

    public void setLimitDistance(double limitDistance) {
        this.limitDistance = limitDistance;
    }

    public boolean isWithinRange(HealthyLocation h_loc2){

        Location loc1 = new Location("currentLoc");
        loc1.setLatitude(current_lat);
        loc1.setLongitude(current_long);

        Location loc2 = new Location("loc2");
        loc2.setLatitude(h_loc2.getLatitude());
        loc2.setLongitude(h_loc2.getLongitude());

        float distanceInMeters = (loc1.distanceTo(loc2))/1000;
        if(distanceInMeters<=limitDistance)
            return true;

        return false;
    }

    public ArrayList<HealthyLocation> searchLocations(String address){
        ArrayList<HealthyLocation> results = new ArrayList<HealthyLocation>();
        address = address.toLowerCase().replace("-"," ");
        for(int i = 0; i< listOfHealthyLocation.size(); i++){
            if(listOfHealthyLocation.get(i).getLocationType().equals(locationType)) {
                String concat = listOfHealthyLocation.get(i).getName() + " " + listOfHealthyLocation.get(i).getAddress() + " " + listOfHealthyLocation.get(i).getZipCode();
                concat = concat.toLowerCase();
                String[] addressSplit = address.split(" ");
                boolean found = true;
                for (int j = 0; j < addressSplit.length; j++) {
                    if (concat.indexOf(addressSplit[j]) == -1)
                        found = false;
                }
                if (found) {

                    if(!isLatLngSet)
                    results.add(listOfHealthyLocation.get(i));
                    else{
                        if(isWithinRange(listOfHealthyLocation.get(i))){
                            results.add(listOfHealthyLocation.get(i));
                        }
                    }
                }
            }
        }

        results = sortList(results);

        return results;
    }

    public int searchLocationIDByAddress (String address){

        address = address.toLowerCase();
        for(int i = 0; i< listOfHealthyLocation.size(); i++){
            if(listOfHealthyLocation.get(i).getLocationType().equals(locationType)) {
                String concat = listOfHealthyLocation.get(i).getName();
                concat = concat.toLowerCase();

                if (address.equals(concat)) {
                    return listOfHealthyLocation.get(i).getId();
                }
            }
        }
        return -1;
    }

    public void initFavouriteList(Context c){
        context = c;

        //check if file existes
        File file = new File( context.getFilesDir(), favFileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //read file
        FileInputStream inputStream;
        try{
            inputStream = context.openFileInput(favFileName);
            BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = bR.readLine()) != null; ) {
                favouriteList.add(getLocation(Integer.parseInt(line)));     //add location to favourite list
            }
            bR.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean saveFavouriteList(){
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(favFileName, Context.MODE_PRIVATE);
            BufferedWriter bW = new BufferedWriter(new OutputStreamWriter(outputStream));
            for (HealthyLocation fav: favouriteList) {
                bW.write(Integer.toString(fav.getId()));
                bW.newLine();
            }
            bW.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addToFavourite(HealthyLocation location){
        if(!favouriteList.contains(location)){
            favouriteList.add(location);
        }else{
            return false; //already added
        }
        return saveFavouriteList();
    }

    public boolean removeFavourite(HealthyLocation location){
        if(favouriteList.contains(location)){
            favouriteList.remove(location);
        }else{
            return false;  //the item is not saved yet
        }
        return saveFavouriteList();
    }

    public ArrayList<HealthyLocation> getFavouriteList(){
        return favouriteList;
    }

}
