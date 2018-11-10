package com.example.healthyeating.healthyeating.interfaces;

import android.content.Context;

import com.example.healthyeating.healthyeating.entity.HealthyLocation;

import java.util.ArrayList;

public interface IFileWriter {
    public boolean writeFile(Context c, String fileName, ArrayList list);
}
