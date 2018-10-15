package com.example.healthyeating.healthyeating.Boundary;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import com.example.healthyeating.healthyeating.Controller.LocationsManager;
import com.example.healthyeating.healthyeating.Controller.SingletonManager;
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
public class FavouriteFragment extends Fragment implements LocationDetailsFragment.OnFragmentInteractionListener, SearchAndSlide.OnFragmentInteractionListener {

    private static final String TAG = "Favourite";

    // declare the layout elements
    private Spinner categorySpinner;
    private TextView categoryTextView;
    private ListView favouritesView;

    private String categoryChosen; // input from dropdown menu

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        // bind the layout elements to variables
        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        categoryTextView = (TextView) view.findViewById(R.id.categoryTextView);
        favouritesView = (ListView) view.findViewById(R.id.favouritesView);

        // set up the dropdown menu
        String[] categoryArray = {"Favourite Eateries", "Favourite Caterers", "All Favourite"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, categoryArray);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // get input from dropdown menu
        categoryChosen = "All Favourite";
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryChosen = parent.getItemAtPosition(position).toString();

                // put locations into list view depending on chosen location category
                ArrayList<HealthyLocation> favouritesList = getFavourites();
                ArrayList<HealthyLocation> displayedList = new ArrayList<HealthyLocation>();

                // show only favourite eateries
                if (categoryChosen.equals("Favourite Eateries")) {
                    for (HealthyLocation favourite : favouritesList) {
                        if (favourite.getLocationType().equals("Eateries"))
                            displayedList.add(favourite);
                    }
                // show only favourite caterers
                } else if (categoryChosen.equals("Favourite Caterers")) {
                    for (HealthyLocation favourite : favouritesList) {
                        if (favourite.getLocationType().equals("Caterers"))
                            displayedList.add(favourite);
                    }
                // show all favourite
                } else {
                    displayedList = favouritesList;
                }

                ArrayAdapter<HealthyLocation> favouritesAdapter = new ArrayAdapter<HealthyLocation>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, displayedList);
                favouritesView.setAdapter(favouritesAdapter);

                categoryTextView.setText(categoryChosen);
            }

            public void onNothingSelected(AdapterView<?> parent){}
        });

        // failed attempt to make items show on map
        /*favouritesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                // Get the information of 1 eatery
                Object listItem = favouritesView.getItemAtPosition(pos);

                String locationDetails = listItem.toString().trim();
                int endIndex =  locationDetails.indexOf("\r\nAddress: ");//locationDetails.indexOf(":") + 1;
                String address = locationDetails.substring(0, endIndex);
                int selectedLocID = ((MainActivity)getActivity()).lm.searchLocationIDByAddress(address);

                ArrayList<HealthyLocation> loc = new ArrayList<HealthyLocation>();
                HealthyLocation clickLoc = ((MainActivity)getActivity()).lm.getLocation(selectedLocID);
                loc.add(clickLoc);
                ((MainActivity)getActivity()).displayOnMap(loc);

                String name = clickLoc.getName();
                Log.e("NAME ", name);
                ((MainActivity)getActivity()).searchSlide.setSpinnerValue(0);

                ((MainActivity)getActivity()).ldf.setInformation(loc);

                //((MainActivity)getActivity()).searchSlide.setSearchBoxText(name);
                ((MainActivity)getActivity()).toggleInformationBox(true);
                ((MainActivity)getActivity()).getBestView();

            }
        });*/

        return view;
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

    @Override
    public void onFragmentInteraction(Uri uri) { }
}
