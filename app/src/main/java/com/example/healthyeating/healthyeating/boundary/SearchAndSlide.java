package com.example.healthyeating.healthyeating.boundary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;

import com.example.healthyeating.healthyeating.interfaces.ILocationListener;
import com.example.healthyeating.healthyeating.R;

import java.text.DecimalFormat;


public class SearchAndSlide extends Fragment  {


    private DecimalFormat f = new DecimalFormat("##.0");

    private static int spinnerValue = 0;
    private SearchView searchView;
    private SeekBar seek;
    private double seekBarValue = 0;
    private Spinner spinner;

    private ILocationListener locListener;



    public SearchAndSlide() {
        // Required empty public constructor
    }


    public int getSpinnerValue(){
        return spinner.getSelectedItemPosition();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_search_and_slide, container, false);
        seek = (SeekBar) v.findViewById(R.id.seekBar2);
         searchView = (SearchView) v.findViewById(R.id.searchBar);
         searchView.setQuery("", false);
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                if (locListener != null)
                    locListener.searchSubmit(newText);

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (locListener != null)
                    locListener.searchSubmit(query);

                return false;
            }

        });

        SeekBar sk = (SeekBar) v.findViewById(R.id.seekBar2);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView t=(TextView)v.findViewById(R.id.textView4);

                int val = (i * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                double dis = (double)i/1000.0;
                if(dis<1.0)
                    t.setText(i+"m");
                else if (dis == 30.0) {
                    t.setText("Max");
                }
                else
                    t.setText(f.format((double)i/1000.0)+"km");
                t.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                seekBarValue = dis;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (locListener != null)
                    locListener.onSliderHoldDown();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (locListener != null)
                    locListener.onSliderRelease(seekBarValue);
            }
        });

        spinner = (Spinner) v.findViewById(R.id.sortSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (locListener != null) {
                    locListener.onSpinnerChange(pos);
                }
              spinnerValue = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        setSpinnerValue(0);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(spinner!=null)
            spinner.setSelection(getSpinnerValue());
        if(locListener==null)
        if (context instanceof ILocationListener) {
            locListener = (ILocationListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ILocationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setSearchBoxText(String s){
        searchView.setQuery(s,true);
    }
    public void setSpinnerValue(int index){
        spinnerValue=index;
        spinner.setSelection(index,true);
        if (locListener != null)
            locListener.onSpinnerChange(index);

    }


    @Override
    public void onResume() {
        super.onResume();
        locListener.searchSlideOnResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
