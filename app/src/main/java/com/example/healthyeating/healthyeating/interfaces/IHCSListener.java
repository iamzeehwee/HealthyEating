package com.example.healthyeating.healthyeating.interfaces;

import com.example.healthyeating.healthyeating.entity.HCSProducts;

import java.util.ArrayList;

//This interface will allow communication between HCSProductUI Fragments and MainActivity

public interface IHCSListener {

    void onSortSpinnerChange(int sortIndex); //Used for sorting dropdown list
    void onCatSpinnerChange(int sortIndex); //Used for category dropdown list
    ArrayList<HCSProducts> getHCSList(int sortType); //Used for the displaying of HCS products
    ArrayList<HCSProducts> hcsSearch(String query); //Used for searching for HCS products
}
