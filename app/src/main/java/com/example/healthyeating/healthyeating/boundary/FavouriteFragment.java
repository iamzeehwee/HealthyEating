package com.example.healthyeating.healthyeating.boundary;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.healthyeating.healthyeating.controller.LocationsManager;
import com.example.healthyeating.healthyeating.entity.HealthyLocation;
import com.example.healthyeating.healthyeating.R;
import com.example.healthyeating.healthyeating.interfaces.IFavouriteListener;
import com.example.healthyeating.healthyeating.interfaces.ILocationListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

    private static final String TAG = "Favourite";

    private int spinnerValue = 0;
    // declare the layout elements
    private Spinner categorySpinner;
    private TextView categoryTextView;
    private ListView favouritesView;

    private String categoryChosen; // input from dropdown menu
    private IFavouriteListener favListener; // interface for interaction with main activity

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        // bind the layout elements to variables
        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        categoryTextView = (TextView) view.findViewById(R.id.categoryTextView);
        favouritesView = (ListView) view.findViewById(R.id.favouritesView);

        favouritesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                // Get the information about clicked location
                HealthyLocation clickedFavourite = (HealthyLocation) favouritesView.getItemAtPosition(pos);
                favListener.onFavListItemClicked(clickedFavourite.getName(),spinnerValue);
            }
        });


        // set up the dropdown menu
        String[] categoryArray = {"Favourite Eateries", "Favourite Caterers", "All Favourite"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>((Context) favListener,
                android.R.layout.simple_spinner_item, categoryArray);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // get input from dropdown menu and display favourites depending on chosen category
        categoryChosen = "All Favourite";
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryChosen = parent.getItemAtPosition(position).toString();
                refreshListView(categoryChosen, favouritesView);
                categoryTextView.setText(categoryChosen);
                spinnerValue = position;
            }

            public void onNothingSelected(AdapterView<?> parent){}
        });

        return view;
    }

    // refreshes the list view with favourites
    public void refreshListView(String categoryChosen, ListView favouritesView) {
        ArrayList<HealthyLocation> displayedList = favListener.getFavsByCategory(categoryChosen);
        CustomListAdapter favouritesAdapter = new CustomListAdapter((Context) favListener, R.layout.list_item, displayedList);
        favouritesView.setAdapter(favouritesAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFavouriteListener) {
            favListener = (IFavouriteListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // custom adapter for complex views in favourite tab
    private class CustomListAdapter extends ArrayAdapter<HealthyLocation> {
        private int layout;
        private List<HealthyLocation> locationList;
        private CustomListAdapter(Context context, int resource, List<HealthyLocation> locationList) {
            super(context, resource, locationList);
            this.locationList = locationList;
            layout = resource;
        }

        // build list item view
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();

                // establish links to layout elements
                viewHolder.locationName = (TextView) convertView.findViewById(R.id.list_item_name);
                viewHolder.locationDetails = (TextView) convertView.findViewById(R.id.list_item_details);
                viewHolder.deleteButton = (Button) convertView.findViewById(R.id.list_item_delete_btn);

                convertView.setTag(viewHolder);
            }

            mainViewholder = (ViewHolder) convertView.getTag();

            // removal button code
            mainViewholder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favListener.removeFavourite(getItem(position));
                    refreshListView(categoryChosen, favouritesView);
                }
            });

            // set variable text into text views
            mainViewholder.locationName.setText(getItem(position).getName());
            mainViewholder.locationDetails.setText(getItem(position).getAddress() + "\n");

            return convertView;
        }
    }

    public class ViewHolder {
        TextView locationName;    // location's name
        TextView locationDetails; // location's address, floor and unit
        Button deleteButton;      // button for item deletion
    }
}

