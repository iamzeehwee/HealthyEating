package com.example.healthyeating.healthyeating.utilities;

import android.content.Context;

import com.example.healthyeating.healthyeating.interfaces.IFileReader;
import com.example.healthyeating.healthyeating.interfaces.IFileWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class StorageImpl implements IFileReader, IFileWriter {
    @Override
    public  ArrayList readFile(Context C, String fileName) {

        ArrayList<String> data = new ArrayList<>();

        File file = new File( C.getFilesDir(), fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //read file
        FileInputStream inputStream;
        try{
            inputStream = C.openFileInput(fileName);
            BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream));
            for (String line; (line = bR.readLine()) != null; ) {
                data.add(line);     //add location to favourite list
            }
            bR.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean writeFile(Context c, String fileName, ArrayList list) {
        FileOutputStream outputStream;
        try {
            outputStream = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            BufferedWriter bW = new BufferedWriter(new OutputStreamWriter(outputStream));
            for (String s: (ArrayList<String>)list) {
                bW.write(s);
                bW.newLine();
            }
            bW.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
