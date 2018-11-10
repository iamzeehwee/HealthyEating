package com.example.healthyeating.healthyeating.interfaces;



//This interface will allow communication between FavouritesUI Fragments and MainActivity

import com.example.healthyeating.healthyeating.entity.HealthyLocation;

import java.util.ArrayList;

public interface IFavouriteListener {

    /**
     * Retrieves the list of favourites by category (eatieries, caterers or both).
     * @param favType type of category list to retrieve
     * @return        list of favourites
     */
    ArrayList<HealthyLocation> getFavsByCategory(int favType);

    /**
     * Activates when user clicks on an item in the favourite menu.
     * @param name
     * @param spinnerValue
     */
    void onFavListItemClicked(String name,int spinnerValue);

    /**
     * Removes a location from favourite.
     * @param favourite        location to remove
     */
    void removeFavourite(HealthyLocation favourite);
}
