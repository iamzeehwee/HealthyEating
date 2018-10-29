package com.example.healthyeating.healthyeating.interfaces;

import java.util.ArrayList;


public interface DAO<T> {

    T retrieveByID(int id);
    ArrayList<T> retrieveByName(String name, int sort, String locationType);
    ArrayList<T> getList(int index, int sort, String locationType); //index is use to select between HealthyLocation and Favourites


    boolean add(int index, T t);
  //  boolean addToFavourite(T favs);
 //   boolean removeFavourite(T fav);
    boolean delete(int index, T t);

    void update(T t, String[] params);


}
