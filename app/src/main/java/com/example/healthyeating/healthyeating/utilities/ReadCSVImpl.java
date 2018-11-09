package com.example.healthyeating.healthyeating.utilities;

import android.content.Context;

import com.example.healthyeating.healthyeating.R;
import com.example.healthyeating.healthyeating.interfaces.IFileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadCSVImpl implements IFileReader {

    /**
     * This method is used for Strategy pattern to read file  .
     * @param c
     * @param fileName
     * @return prodData Data extracted from the csv file
     */
    @Override
    public ArrayList readFile(Context c, String fileName) {

        int fileValue = R.raw.hcs;

        ArrayList<String[]> prodData = readCSVFile(c, fileValue);

        return prodData;
    }

    /**
     * This method is for reading of the hcs.csv file.
     * @param c
     * @param file
     * @return Data extracted from the csv file
     */
    public ArrayList<String[]> readCSVFile(Context c, int file) {

        InputStream inputStream = c.getApplicationContext().getResources().openRawResource(file);
        ArrayList<String[]> hcsData = new  ArrayList<String[]>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine = " ";
            reader.readLine();
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split("\t");

                hcsData.add(row);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading Healthy Choice Symbols Products" + ex);

        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing Input Stream" + e);

            }
        }

        return hcsData;
    }

}

