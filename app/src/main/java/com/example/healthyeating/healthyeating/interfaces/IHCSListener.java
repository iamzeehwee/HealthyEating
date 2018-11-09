package com.example.healthyeating.healthyeating.interfaces;

import com.example.healthyeating.healthyeating.entity.HCSProducts;

import java.util.ArrayList;

//This interface will allow communication between HCSProductUI Fragments and MainActivity

public interface IHCSListener {

    void onSortSpinnerChange(int sortIndex);
    void onCatSpinnerChange(int sortIndex);
    ArrayList<HCSProducts> getAllHCSList(int sortType);
    ArrayList<HCSProducts> hcsSearch(String query);
}
