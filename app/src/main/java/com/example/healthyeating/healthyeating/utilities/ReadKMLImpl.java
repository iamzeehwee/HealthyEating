package com.example.healthyeating.healthyeating.utilities;

import android.content.Context;
import android.util.Log;

import com.example.healthyeating.healthyeating.R;
import com.example.healthyeating.healthyeating.interfaces.IFileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadKMLImpl implements IFileReader {

    //Used for HealthyLocation

    @Override
    public  ArrayList readFile(Context c, String fileName) {
        String tag = "";
        String locationType = "";
        int fileValue = Integer.parseInt(fileName);

        if(fileValue == R.raw.eateries) {
            tag = "<SchemaData schemaUrl=\"#kml_schema_ft_HEALTHIERDINING\">";
            locationType = "Eateries";
        }
        else if(fileValue == R.raw.caterers){
            tag = "<SchemaData schemaUrl=\"#kml_schema_ft_HEALTHIERCATERERS\">";
            locationType = "Caterers";
        }
        ArrayList<ArrayList<String>> fullData = readKMLFile(c,fileValue,tag,locationType);

        return fullData;

    }

    public ArrayList<ArrayList<String>>  readKMLFile(Context c, int file, String tag, String locationType){

        boolean start = false;
        ArrayList<ArrayList<String>> fullData = new ArrayList<ArrayList<String>>();
        ArrayList<String> data = new ArrayList<String>();
        try {
            InputStream inputStream =  c.getApplicationContext().getResources().openRawResource(file);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!start && line.equals(tag)) {
                            start = true;
                            line = reader.readLine();
                        }
                        if (start && line.equals("</Point>")) {
                            start = false;
//                            createLocation(data,locationType);
                            fullData.add(data);
                            data = new ArrayList<String>();
                        }
                        if (start)
                            data.add(line);
                    }
                } finally { reader.close(); }
            } finally { inputStream.close(); }
        } catch (IOException e) { }

       return fullData;
    }
}
