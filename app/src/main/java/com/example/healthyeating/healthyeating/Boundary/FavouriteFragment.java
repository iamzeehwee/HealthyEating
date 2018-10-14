package com.example.healthyeating.healthyeating.Boundary;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.healthyeating.healthyeating.R;


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

        View view = inflater.inflate(R.layout.fragment_favourite,container,false);
        ListView listView = (ListView) view.findViewById(R.id.favouriteListView);


        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

}
