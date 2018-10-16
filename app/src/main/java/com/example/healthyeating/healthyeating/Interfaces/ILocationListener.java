package com.example.healthyeating.healthyeating.Interfaces;

import com.example.healthyeating.healthyeating.Entity.HealthyLocation;


//This interface will allow communication between LocationUI Fragments and MainActivity

public interface ILocationListener {
    //LocationDetailsFragment
    void onCloseBtnPress();
    void onSaveButtonPressed(HealthyLocation location);

    //SearchAndSlide
    void searchSubmit(String query);
    void onSliderRelease(double dis);
    void onSliderHoldDown();
    void onSpinnerChange(int index);


}
