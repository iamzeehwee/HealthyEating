package com.example.healthyeating.healthyeating.interfaces;

import java.util.ArrayList;


public interface DAO<T> {

    T retrieveByID(int id);
    ArrayList<T> retrieveByName(String name, int sort, String locationType);
    ArrayList<T> getListOfHealthyLocation(int sort, String locationType);
    ArrayList<T> getListOfFavourites();


    void add(T t);
    boolean addToFavourite(T favs);
    boolean removeFavourite(T fav);


    void update(T t, String[] params);

    void delete(T t); //Not applicable
}
