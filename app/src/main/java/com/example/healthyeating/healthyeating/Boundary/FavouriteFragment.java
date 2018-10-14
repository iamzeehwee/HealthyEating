package com.example.healthyeating.healthyeating.Boundary;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.healthyeating.healthyeating.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {
    private static final String TAG = "Favourite";

    // declare the layout elements
    private Spinner categorySpinner;
    private TextView categoryTextView;
    private ListView favouritesView;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourite, container, false);

        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        categoryTextView = (TextView) view.findViewById(R.id.categoryTextView);
        favouritesView = (ListView) view.findViewById(R.id.favouritesView);

        // Inflate the layout for this fragment
        return view;
    }

}
