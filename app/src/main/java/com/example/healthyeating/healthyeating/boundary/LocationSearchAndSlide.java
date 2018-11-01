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


public class LocationSearchAndSlide extends Fragment  {


    private DecimalFormat f = new DecimalFormat("##.0");

    private static int spinnerValue = 0;
    private SearchView searchView;
    private SeekBar seek;
    private double seekBarValue = 0;
    private Spinner spinner;

    private ILocationListener locListener;


    /**
     * This method is a public constructor
     */
    public LocationSearchAndSlide() {
        // Required empty public constructor
    }

    /**
     * This method is to get the current selected item of the spinner
     * @return int spinner.getSelectedItemPosition
     */
    public int getSpinnerValue(){
        return spinner.getSelectedItemPosition();
    }

    /**
     * This method is used to initialize the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method is used when a fragment inflate a view
     * This method also handles the event when search text is entered into the search box by user
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View v
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_search_and_slide, container, false);
        seek = (SeekBar) v.findViewById(R.id.seekBar2);
        searchView = (SearchView) v.findViewById(R.id.searchBar);
        searchView.setQuery("", false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * This method will calls the searchSubmit method to search for locaton based on the search text entered in the search box before the user selects enter
             * @param newText
             * @return false
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                if (locListener != null)
                    locListener.searchSubmit(newText);

                return false;
            }

            /**
             * This method will calls the searchSubmit method to search for locaton based on the search text entered in the search box when the user selects enter
             * @param query
             * @return false
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (locListener != null)
                    locListener.searchSubmit(query);

                return false;
            }
        });

        SeekBar sk = (SeekBar) v.findViewById(R.id.seekBar2);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            /**
             * This method is used to determine the new value of the distance filter
             * @param seekBar
             * @param i
             * @param b
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView t=(TextView)v.findViewById(R.id.textView4);

                int val = (i * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                double dis = (double)i/1000.0;
                if(dis<1.0)
                    t.setText(i+"m");
                else
                    t.setText(f.format((double)i/1000.0)+"km");
                t.setX(seekBar.getX() + val - 15);
                seekBarValue = dis;
            }

            /**
             * This method will call the onSliderHoldDown method when the user starts sliding the distance slider
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (locListener != null)
                    locListener.onSliderHoldDown();
            }

            /**
             * This method will call the onSlideRelease method when the user release from sliding the distance slider
             * @param seekBar
             */
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

            /**
             * This method is to be invoked when the selection of the spinner disappears
             * @param parent
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        setSpinnerValue(0);

        return v;
    }

    /**
     * This method is called once when the fragment is associated with its activity
     * @param context
     */
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

    /**
     * This method is called when the fragment removed itself from the association with its activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This method set the text in search box
     * @param s
     */
    public void setSearchBoxText(String s){
        searchView.setQuery(s,true);
    }

    /**
     * This method sets the sort by spinner when user changes this
     * @param index
     */
    public void setSpinnerValue(int index){
        spinnerValue=index;
        spinner.setSelection(index,true);
        if (locListener != null)
            locListener.onSpinnerChange(index);
    }

    /**
     * This method is used when the activity interacts with the fragment again
     */
    @Override
    public void onResume() {
        super.onResume();
        locListener.searchSlideOnResume();
    }

    /**
     * This method is used when the user leaves the fragment
     */
    @Override
    public void onPause() {
        super.onPause();
    }

}
