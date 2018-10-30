package com.example.healthyeating.healthyeating.boundary;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import com.example.healthyeating.healthyeating.R;
import com.example.healthyeating.healthyeating.entity.HCSProducts;
import com.example.healthyeating.healthyeating.interfaces.IHCSListener;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HCSProductsFragment extends Fragment {


    private ArrayList<HCSProducts> hscProducts;
    private IHCSListener hscListener;
    private int spinnerCatValue;
    private int spinnerSortValue;
    private Spinner sortSpinner;
    private Spinner catSpinner;
    private String catChosen;
    private String sortChosen;
    private SearchView hSCSearchView;
    private ListView hCSListView;
    private Button btn_search ,btn_meatPoultry, btn_seafood, btn_eggs, btn_dairy, btn_cereals, btn_fruitsVeggie,btn_legumesNutsSeeds, btn_crips, btn_iceCream, btn_beverages, btn_SaucesSoupsRecipesMixes, btn_misc ;



    public HCSProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hcsproducts, container, false);
    }


    //public void onAttach(Context context)
    {


    }

    //public void onDetach()
    {


    }

    //public void displaySelectedCatProducts(String catName)
    {


    }

    // public void displaySelectedProductDetails(String brandName, String productName)
    {


    }


}