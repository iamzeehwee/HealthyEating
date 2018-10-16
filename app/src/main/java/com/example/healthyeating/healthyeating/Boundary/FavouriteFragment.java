package com.example.healthyeating.healthyeating.Boundary;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.healthyeating.healthyeating.Controller.LocationsManager;
import com.example.healthyeating.healthyeating.Controller.SingletonManager;
import com.example.healthyeating.healthyeating.Entity.HealthyLocation;
import com.example.healthyeating.healthyeating.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

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
                             @Nullable Bundle savedInstanceState) {
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
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // get input from dropdown menu and display favourites depending on chosen category
        categoryChosen = "All Favourite";
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryChosen = parent.getItemAtPosition(position).toString();

                // put locations into list view depending on chosen location category
                LocationsManager lm = ((MainActivity)getActivity()).getLocationsManager();
                ArrayList<HealthyLocation> favouritesList = lm.getFavouriteList();

                // show only favourite eateries
                if (categoryChosen.equals("Favourite Eateries")) {
                    favouritesList = lm.getFavouriteEateries();
                // show only favourite caterers
                } else if (categoryChosen.equals("Favourite Caterers")) {
                    favouritesList = lm.getFavouriteCaterers();
                }

                CustomListAdapter favouritesAdapter = new CustomListAdapter(getActivity().getApplicationContext(), R.layout.list_item, favouritesList);
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

//    @Override
//    public void onFragmentInteraction(Uri uri) { }

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
                    Toast.makeText(getContext(), "Button was clicked for list item " + getItem(position).getName(), Toast.LENGTH_SHORT).show();
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

