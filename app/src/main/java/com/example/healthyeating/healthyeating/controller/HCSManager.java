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
     * This method is for reading of the hcs.csv file.
     */
    public void initHCSProductList(Context c) {
        ArrayList<String[]> hcsResult;
        context = c;

        //Read local storage
        fileReader = new ReadCSVImpl();

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

    public HCSProducts getCategory(int ID) {
        return hcsProductsDAO.retrieveByID(ID);
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    /**
     * This method is for the getting the list of HCS products according to the category and sorting spinner.
     */
    public ArrayList<HCSProducts> getProductList() {
        ArrayList<HCSProducts> hcsList = hcsProductsDAO.getList(0,sortFilter,catType);
        return hcsList;
    }

    public void setSortFilter(int sortFilter) {
        this.sortFilter = sortFilter;
    }

    /**
     * This method is for searching of HCS products.
     */
    public ArrayList<HCSProducts> searchProducts(String name) {
        ArrayList<HCSProducts> prodList = hcsProductsDAO.retrieveByName(name, sortFilter, catType);

        return prodList;
    }

}