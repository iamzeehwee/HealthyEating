package com.example.healthyeating.healthyeating.interfaces;

import android.content.Context;

import java.util.ArrayList;

public interface IFileWriter {
    public boolean writeFile(Context c, String fileName, ArrayList list);
}
