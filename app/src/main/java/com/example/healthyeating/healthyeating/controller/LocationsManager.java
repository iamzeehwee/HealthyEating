package com.example.healthyeating.healthyeating.controller;

import com.example.healthyeating.healthyeating.R;
import com.example.healthyeating.healthyeating.entity.HealthyLocation;
import com.example.healthyeating.healthyeating.entity.HealthyLocationStorage;
import com.example.healthyeating.healthyeating.interfaces.DAO;
import com.example.healthyeating.healthyeating.interfaces.IFileReader;
import com.example.healthyeating.healthyeating.interfaces.IFileWriter;
import com.example.healthyeating.healthyeating.utilities.ReadKMLImpl;
import com.example.healthyeating.healthyeating.utilities.StorageImpl;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;


public class LocationsManager {

    private Context context;

    private int sortFilter = 0; //0 = A-Z, 1 = Z-A
    private double limitDistance = 50000.0;
    private String locationType = "Eateries";
    private double current_lat = -1;
    private double current_long = -1;
    private boolean isLatLngSet = false;
    private String favFileName = "FavouriteListData";

    //Interface
    private IFileReader fileReader;
    private DAO<HealthyLocation> locationDAO;


    public LocationsManager(){
        locationDAO = new HealthyLocationStorage();

    }


    public void initHealthyLocationList(Context context){
        ArrayList<ArrayList<String>> fullData;
        if(this.context==null)
            this.context = context;

        fileReader = new ReadKMLImpl(); //Strategy pattern, I want to read data file that is in KML format

        fullData = fileReader.readFile(context,""+R.raw.eateries);

          for(int i = 0; i<fullData.size();i++)
              createLocation(fullData.get(i),"Eateries");

          fullData = fileReader.readFile(context,""+R.raw.caterers);

          for(int i = 0; i<fullData.size();i++)
            createLocation(fullData.get(i),"Caterers");

    }


    public double getCurrent_lat() {
        return current_lat;
    }
    public double getCurrent_long() {
        return current_long;
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
        return locationDAO.retrieveByID(id);
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

        HealthyLocation loc = new HealthyLocation(name,address,postal_code,  floor,  unit,  longitude,  latitude, locationType);
        locationDAO.add(0,loc);

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

        ArrayList<HealthyLocation> res = locationDAO.getList(0,sortFilter, locationType);

        if(!isLatLngSet)
            return res;
        for(int i = 0; i< res.size(); i++){
                if(!isWithinRange(res.get(i))) {
                    res.remove(res.get(i));
                    i--;
                }
        }
        return res;
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

    public ArrayList<HealthyLocation> searchLocations(String name){

        ArrayList<HealthyLocation> locList = locationDAO.retrieveByName(name,sortFilter,locationType);

        //We check if the Location is within the range of the current user because it is not the job of
        //the Storage to check
        //Storage should handle manipulations of data.
        if(!isLatLngSet)
            return locList;
        for(int i = 0; i<locList.size();i++){
                if(!isWithinRange(locList.get(i))){
                    locList.remove(locList.get(i));
                    i--;
                }
        }
        return locList;
    }


    public void setSortFilter(int sortFilter){
        this.sortFilter = sortFilter;
}

    public void initFavouriteList(Context c){
        context = c;

        //Read local storage
        fileReader = new StorageImpl();

        ArrayList<String> favouritesData = fileReader.readFile(c,favFileName);


        for(String s: favouritesData){
            locationDAO.add(1,getLocation(Integer.parseInt(s)));
        }
    }

    private boolean saveFavouriteList(){

        IFileWriter fileWriter = new StorageImpl();

        ArrayList<String> list = new ArrayList<>();
        for (HealthyLocation fav: locationDAO.getList(1,0,"")){
            list.add(Integer.toString(fav.getId()));
        }

        return fileWriter.writeFile(context, favFileName, list);
    }

    public boolean addToFavourite(HealthyLocation location){
             if(locationDAO.add(1,location)) {
                 saveFavouriteList();
            return true;}
        else
            return false;
    }

    public boolean removeFavourite(HealthyLocation location){
        if(locationDAO.delete(1,location)) {
            saveFavouriteList();
            return true;}
        else
            return false;

    }
    public boolean getLatLngSet(){
        return isLatLngSet;
    }
    public ArrayList<HealthyLocation> getFavouriteList(){
        return locationDAO.getList(1,0,"");
    }

    public ArrayList<HealthyLocation> getFavouriteEateries() {
        ArrayList<HealthyLocation> favouriteEateries = new ArrayList<HealthyLocation>();
        for (HealthyLocation favourite : getFavouriteList()) {
            if (favourite.getLocationType().equals("Eateries"))
                favouriteEateries.add(favourite);
        }
        return favouriteEateries;
    }

    public ArrayList<HealthyLocation> getFavouriteCaterers() {
        ArrayList<HealthyLocation> favouriteCaterers = new ArrayList<HealthyLocation>();
        for (HealthyLocation favourite : getFavouriteList()) {
            if (favourite.getLocationType().equals("Caterers"))
                favouriteCaterers.add(favourite);
        }
        return favouriteCaterers;
    }
}
