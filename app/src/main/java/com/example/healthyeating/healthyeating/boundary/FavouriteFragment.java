package com.example.healthyeating.healthyeating.boundary;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
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
    private ListView favouritesView;
    private TabLayout categoryTabLayout;
    private IFavouriteListener favListener; // interface for interaction with main activity

    // constants for favourite selection
    private static final int FAV_EATERIES = 0;
    private static final int FAV_CATERERS = 1;
    private static final int ALL_FAVS = 2;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        // bind the layout elements to variables
        favouritesView = (ListView) view.findViewById(R.id.favouritesView);
        categoryTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        categoryTabLayout.addTab(categoryTabLayout.newTab().setText("Eateries"), 0);
        categoryTabLayout.addTab(categoryTabLayout.newTab().setText("Caterers"), 1);
        categoryTabLayout.addTab(categoryTabLayout.newTab().setText("All"), 2);

        refreshListView(FAV_EATERIES, favouritesView);

        // show favourites based on chosen tab
        categoryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshListView(tab.getPosition(), favouritesView);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        favouritesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                // Get the information about clicked location
                HealthyLocation clickedFavourite = (HealthyLocation) favouritesView.getItemAtPosition(pos);
                favListener.onFavListItemClicked(clickedFavourite.getName(),spinnerValue);
            }
        });

        return view;
    }

    // refresh the list view with favourites
    public void refreshListView(int favType, ListView favouritesView) {
        ArrayList<HealthyLocation> displayedList = favListener.getFavsByCategory(favType);
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
                    int favType = categoryTabLayout.getSelectedTabPosition();
                    refreshListView(favType, favouritesView);
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

