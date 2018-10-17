package com.example.healthyeating.healthyeating.interfaces;

import android.content.Context;

import java.util.ArrayList;

public interface IFileReader {
    //Task is to read data file, but you have a lot of ways to read it.
    //That is why you apply Strategy pattern
    ArrayList readFile(Context c, String fileName);


}
