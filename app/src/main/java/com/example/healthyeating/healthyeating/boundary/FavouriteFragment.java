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
                // Get the information of 1 eatery
                Object listItem = favouritesView.getItemAtPosition(pos);

                String locationDetails = listItem.toString().trim();
                int endIndex =  locationDetails.indexOf("\r\nAddress: ");//locationDetails.indexOf(":") + 1;
                String name = locationDetails.substring(0, endIndex);
//                int addressStartIndex = locationDetails.substring(endIndex+11).indexOf("\r\n");
//                String address = locationDetails.substring(addressStartIndex);
//                int addressEndIndex = address.indexOf("\r\n");


//                Log.d("Favourite","@@"+name+"@@ "+locationDetails.substring(addressStartIndex,addressEndIndex));


               favListener.onFavListItemClicked(name,spinnerValue );



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
       // favListener = null;
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_btn);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favListener.removeFavourite(getItem(position));
                    refreshListView(categoryChosen, favouritesView);
                }
            });

            mainViewholder.title.setText(getItem(position).toString());
            return convertView;
        }
    }

    public class ViewHolder {
        TextView title;
        Button button;
    }
}
