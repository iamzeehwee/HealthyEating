package com.example.healthyeating.healthyeating.Boundary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import com.example.healthyeating.healthyeating.Controller.LocationsManager;
import com.example.healthyeating.healthyeating.Controller.SingletonManager;
import com.example.healthyeating.healthyeating.R;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchAndSlide.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchAndSlide#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchAndSlide extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DecimalFormat f = new DecimalFormat("##.0");
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SearchView searchView;
    SeekBar seek;
    Activity activity;
    private double seekBarValue = 0;
    private static final int MAX_SEEKBAR_VALUE = 50000;
    private OnFragmentInteractionListener mListener;
   Spinner spinner;
    private int current_index = 0;

    public SearchAndSlide() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchAndSlide.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchAndSlide newInstance(String param1, String param2) {
        SearchAndSlide fragment = new SearchAndSlide();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public int getSpinnerValue(){
        return spinner.getSelectedItemPosition();
    }
    public void resetSliderAndTextBox(){
        searchView.setQuery("",false);
        seek.setProgress(MAX_SEEKBAR_VALUE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                Log.d("SearchBar", "onQueryTextChange "+newText);
                try{
                    ((OnSearchSubmitListener)getContext() ).searchSubmit(newText);
                }catch (ClassCastException cce){

                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

               Log.d("SearchBar", "OnQueryTextSubmitcalled   "+ query);
                try{
                    ((OnSearchSubmitListener)getContext() ).searchSubmit(query);
                }catch (ClassCastException cce){

                }
                return false;
            }

        });

        SeekBar sk = (SeekBar) v.findViewById(R.id.seekBar2);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView t=(TextView)v.findViewById(R.id.textView4);
                //t.setText(String.valueOf(i));

                int val = (i * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                double dis = (double)i/1000.0;
                if(dis<1.0)
                    t.setText(i+"m");
                else
                    t.setText(f.format((double)i/1000.0)+"km");
                t.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                seekBarValue = dis;



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                 Log.d("Slider1","Hold down");
                try{
                    ((OnSliderChangeListener)getContext() ).onSliderHoldDown();
                }catch (ClassCastException cce){

                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("Slider1","Release");
                //Interact with MainActivity when slider change so that MainActivity can interact with the LocationsManager

                try{
                    ((OnSliderChangeListener)getContext() ).onSliderRelease(seekBarValue);
                }catch (ClassCastException cce){

                }
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

        spinner.setSelection(0,false);
        spinner.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
               // Log.d("SpinnerChange",""+event);
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                   // Toast.makeText(this,"down",Toast.LENGTH_LONG).show();
                    // Load your spinner here
//                    try{
//                        ((OnSpinnerChangeListener)getContext() ).onSpinnerChange(4);
//                    }catch (ClassCastException cce){
//
//                    }
                }

                return false;
            }

        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + "Check " + pos + "Check " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
                        // For passing sortFilter value when choose the sort condition

//                        Intent i = new Intent(getActivity(), EateriesListView.class);
//                        if (pos == 2) {
//                            i.putExtra("key", "1");
//                            startActivity(i);
//                        }
//                        if (pos == 1) {
//                            i.putExtra("key", "0");
//                            startActivity(i);
//                        }
//
//                         // Remove animation when switch activity
//                         ((Activity) getActivity()).overridePendingTransition(0,0);

                try{
                    ((OnSpinnerChangeListener)getContext() ).onSpinnerChange(pos);
                }catch (ClassCastException cce){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Intent i = new Intent(getActivity(), MainActivity.class);
                //startActivity(i);
                Log.d("Spinner","NTH");

            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void setSearchBoxText(String s){
        searchView.setQuery(s,true);
    }


    public void setSpinnerValue(int index){
        spinner.setSelection(index);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public interface OnSearchSubmitListener{
        public void searchSubmit(String query);
    }

    public interface OnSliderChangeListener{
        public void onSliderRelease(double dis);
        public void onSliderHoldDown();
    }

    public interface OnSpinnerChangeListener{
        public void onSpinnerChange(int index);
    }


}
