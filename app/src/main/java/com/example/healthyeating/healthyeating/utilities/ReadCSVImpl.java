package com.example.healthyeating.healthyeating.utilities;

import android.content.Context;

import com.example.healthyeating.healthyeating.interfaces.IFileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadCSVImpl implements IFileReader {

    InputStream inputStream;

    public ReadCSVImpl(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public  ArrayList readFile(Context C, String fileName) {

        ArrayList<String[]> hscData = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try
        {
            String csvLine;

            while ((csvLine = reader.readLine()) != null)
            {
                String[] row = csvLine.split(",");
                hscData.add(row);
            }
        }

        catch(IOException ex)
        {
            throw new RuntimeException("Error in reading Healthy Choice Symbols Products" + ex);

        }

        finally
        {
            try
            {
                inputStream.close();
            }

            catch (IOException e)
            {
                throw new RuntimeException("Error while closing Input Stream" + e);

            }
        }

        return hscData;
    }
}
