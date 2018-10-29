package com.example.healthyeating.healthyeating.entity;

import android.util.Log;

import com.example.healthyeating.healthyeating.interfaces.DAO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HealthyLocationStorage implements DAO<HealthyLocation> {

    private ArrayList<HealthyLocation> listOfHealthyLocation = new ArrayList<HealthyLocation>();
    private ArrayList<HealthyLocation> favouriteList = new ArrayList<HealthyLocation>();
    private int location_sortFilter = -1; //0 = A-Z, 1 = Z-A

    public HealthyLocationStorage(){
    }

    public ArrayList<HealthyLocation> sortList(ArrayList<HealthyLocation> loc){

        Collections.sort(loc, new Comparator<HealthyLocation>() {
            public int compare(HealthyLocation o1, HealthyLocation o2) {
                if(location_sortFilter==0)
                    return o1.getName().compareTo(o2.getName());
                else if(location_sortFilter==1)
                    return o2.getName().compareTo(o1.getName());
                else
                    return o1.getName().compareTo(o2.getName());
            }
        });

        return loc;
    }

    @Override
    public HealthyLocation retrieveByID(int id) {
        for(int i = 0 ;i<listOfHealthyLocation.size();i++) {
            if(listOfHealthyLocation.get(i).getId() == id)
            return listOfHealthyLocation.get(i);
        }
        return null;
    }


    @Override
    public ArrayList<HealthyLocation> retrieveByName(String name, int sort, String locationType) {
        //Instead of doing sort every single time you try to retrieve,
        // we do it once. When a user selected A-Z as sort filter, most likely they will use that sorted A-Z list again
        // e.g we want to do multiple searches in a sorted A-Z list.
        // There is no need to sort the list multiple time as the list is already sorted in A-Z
        //We only want to do sorting whenever there is a change

        //**I know is not really practical because in a normal database, we use ORDER BY to sort the list



        ArrayList<HealthyLocation> results = new ArrayList<HealthyLocation>();

        if(sort!=location_sortFilter){
            location_sortFilter = sort;
            listOfHealthyLocation = sortList(listOfHealthyLocation);

        }


        name = name.toLowerCase().replace("-"," ");
        for(int i = 0; i< listOfHealthyLocation.size(); i++){
            if(listOfHealthyLocation.get(i).getLocationType().equals(locationType)) {
                String concat = listOfHealthyLocation.get(i).getName() + " " + listOfHealthyLocation.get(i).getAddress() + " " + listOfHealthyLocation.get(i).getZipCode();
                concat = concat.toLowerCase();
                String[] addressSplit = name.split(" ");
                boolean found = true;
                for (int j = 0; j < addressSplit.length; j++) {
                    if (concat.indexOf(addressSplit[j]) == -1) {
                        found = false;
                        break;
                    }
                }
                if(found)
                results.add(listOfHealthyLocation.get(i));

            }
        }



        return results;



    }

    @Override
    public ArrayList<HealthyLocation> getList(int index, int sort, String locationType) {
        ArrayList<HealthyLocation> res = new ArrayList<>();
        if(index == 0) {


            if (sort != location_sortFilter) {
                location_sortFilter = sort;
                listOfHealthyLocation = sortList(listOfHealthyLocation);
            }

            for (int i = 0; i < listOfHealthyLocation.size(); i++) {
                if (listOfHealthyLocation.get(i).getLocationType().equals(locationType)) {

                    res.add(listOfHealthyLocation.get(i));


                }
            }

        }
        else if(index == 1){
            res = favouriteList;
        }
        return res;

    }





    @Override
    public boolean delete(int index, HealthyLocation loc){
        if(index == 1) {
            if (favouriteList.contains(loc)) {
                favouriteList.remove(loc);
            } else {
                return false;  //the item is not saved yet
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean add(int index, HealthyLocation healthyLocation) {
        if(index == 0) {
            healthyLocation.setId(listOfHealthyLocation.size());
            listOfHealthyLocation.add(healthyLocation);
            return true;
        }
        else if(index == 1){
            if(!favouriteList.contains(healthyLocation)){
                favouriteList.add(healthyLocation);

            }else{
                return false; //already added
            }
            return true;
        }
        return false;
    }


    @Override
    public void update(HealthyLocation healthyLocation, String[] params) {

    }


}
