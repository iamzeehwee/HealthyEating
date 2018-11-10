package com.example.healthyeating.healthyeating.controller;

import android.content.Context;

import com.example.healthyeating.healthyeating.R;
import com.example.healthyeating.healthyeating.entity.HCSProducts;
import com.example.healthyeating.healthyeating.entity.HCSProductsStorage;
import com.example.healthyeating.healthyeating.interfaces.DAO;
import com.example.healthyeating.healthyeating.interfaces.IFileReader;
import com.example.healthyeating.healthyeating.utilities.ReadCSVImpl;

import java.util.ArrayList;

public class HCSManager {
    private IFileReader fileReader;
    private DAO<HCSProducts> hcsProductsDAO;

    private Context context;
    private int sortFilter = 0; //0 = A-Z, 1 = Z-A
    private String catType = ""; //Category


    public HCSManager() {
        hcsProductsDAO = new HCSProductsStorage();
    }

    /**
     * This method is for the loading the HCS list of products upon the app startup
     * @param c
     */
    public void initHCSProductList(Context c) {
        ArrayList<String[]> hcsResult; //Arraylist to store HCS data
        context = c;

        //Read local storage
        fileReader = new ReadCSVImpl(); //Reading of csv files

        hcsResult = fileReader.readFile(context, "" + R.raw.hcs);

        for (int i = 0; i < hcsResult.size(); i++) {

            String[] row = hcsResult.get(i);

            String category = row[0];
            String comName = row[1];
            String prodName = row[2];
            String brandName = row[3];
            String prodWeight = row[4];

            HCSProducts pro = new HCSProducts(category, prodName.toUpperCase(), prodWeight.toUpperCase(), brandName.toUpperCase(), comName.toUpperCase());

            hcsProductsDAO.add(0,pro);
        }
    }

    /**
     * This method is for the setting category type
     * @param catType User selected category type
     */
    public void setCatType(String catType) {
        this.catType = catType;
    }

    /**
     * This method is for the setting sort filter
     * @param sortFilter User selected sort filter
     */
    public void setSortFilter(int sortFilter) {
        this.sortFilter = sortFilter;
    }

    /**
     * This method is for the getting the list of HCS products according to the category and sorting spinner.
     * @return hcsList Return list of HCS products according to category and sorting spinner
     */
    public ArrayList<HCSProducts> getProductList() {
        ArrayList<HCSProducts> hcsList = hcsProductsDAO.getList(0,sortFilter,catType);
        return hcsList;
    }

    /**
     * This method is for searching of HCS products.
     * @param name User input
     * @return Search result of User input
     */
    public ArrayList<HCSProducts> searchProducts(String name) {
        ArrayList<HCSProducts> prodList = hcsProductsDAO.retrieveByName(name, sortFilter, catType);

        return prodList;
    }

}