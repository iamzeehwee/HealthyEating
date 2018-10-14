package com.example.healthyeating.healthyeating.Boundary;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.healthyeating.healthyeating.Controller.LocationsManager;
import com.example.healthyeating.healthyeating.Entity.HealthyLocation;
import com.example.healthyeating.healthyeating.R;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

    ListView favouriteListView;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    public ArrayList<HealthyLocation> getFavourites(){

        LocationsManager locationsManager = ((MainActivity)getActivity()).lm;
        ArrayList<HealthyLocation> healthyLocations = new ArrayList<>();
        String filename = "FavouriteListData";

        //read file
        FileInputStream inputStream;
        try{
            inputStream = getContext().openFileInput(filename);
            BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream));
            ArrayList<Integer> saved = new ArrayList<>();
            for (String line; (line = bR.readLine()) != null; ) {
                int id = Integer.parseInt(line);
                HealthyLocation healthyLocation = locationsManager.getLocation(id);
                healthyLocations.add(healthyLocation);
            }
            bR.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return healthyLocations;
    }
}
