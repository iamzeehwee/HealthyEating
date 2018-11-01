package com.example.healthyeating.healthyeating.interfaces;

import com.example.healthyeating.healthyeating.entity.HealthyLocation;

import java.util.ArrayList;


//This interface will allow communication between LocationUI Fragments and MainActivity

public interface ILocationListener {
    //LocationDetailsFragment
    void onCloseBtnPress();
    void onSaveButtonPressed(HealthyLocation location);
    ArrayList<HealthyLocation> getFavsByCategory(int favType);

    //LocationSearchAndSlide
    void searchSubmit(String query);
    void onSliderRelease(double dis);
    void onSliderHoldDown();
    void onSpinnerChange(int index);
    void searchSlideOnResume();

}
