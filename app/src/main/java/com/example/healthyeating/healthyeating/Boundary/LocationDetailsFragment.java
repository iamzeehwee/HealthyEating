package com.example.healthyeating.healthyeating.Boundary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.healthyeating.healthyeating.Entity.HealthyLocation;
import com.example.healthyeating.healthyeating.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView pageText;
    Button btnLeft,btnRight,btn_save,btn_close;

    private int current_pageNumber= 0 ;
    private int max_pageNumber = 0;
    TextView address,name;
    ArrayList<HealthyLocation> loc;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RelativeLayout relativeLayout;
    private OnFragmentInteractionListener mListener;

    public LocationDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationDetailsFragment newInstance(String param1, String param2) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        final View v = inflater.inflate(R.layout.fragment_location_details, container, false);

        relativeLayout = (RelativeLayout) v.findViewById(R.id.rel_layout);
        address = (TextView) v.findViewById(R.id.address);
        name = (TextView) v.findViewById(R.id.textView);
        pageText = (TextView) v.findViewById(R.id.emptyTextView);
        btnLeft =(Button) v.findViewById(R.id.button_left);
        btnRight =(Button) v.findViewById(R.id.button_right);
        btn_save =(Button) v.findViewById(R.id.button_save);
        btn_close =(Button) v.findViewById(R.id.button_close);

        hide();

        btnLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(current_pageNumber==0)
                    current_pageNumber= loc.size()-1;
                else
                 current_pageNumber--;

                displayInfo(current_pageNumber);

            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(current_pageNumber==loc.size()-1)
                    current_pageNumber= 0;
                else
                    current_pageNumber++;
                displayInfo(current_pageNumber);
            }
        });


        btn_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    ((OnLocationDetailListener)getContext() ).onCloseBtnPress();
                }catch (ClassCastException cce){

                }

            }
        });



        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try{
                    ((OnLocationDetailListener)getContext() ).onSaveButtonPressed(loc.get(current_pageNumber).getId());
                }catch (ClassCastException cce){

                }
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

    public void displayInfo(int index){
        this.name.setText(loc.get(index).getName());
        this.address.setText(loc.get(index).getAddress());
        pageText.setText((index+1)+"/"+loc.size()+" result(s) shown");
    }

    public void setInformation(ArrayList<HealthyLocation> loc){
        this.loc = loc;
        current_pageNumber = 0;
        displayInfo(current_pageNumber);//Display the very first one



    }

    public void hide(){
        if(relativeLayout!=null) {
            relativeLayout.setVisibility(View.INVISIBLE);
                togglePageButton(false,false);

        }
    }

    public void show(){
        if(relativeLayout!=null) {
            relativeLayout.setVisibility(View.VISIBLE);


            if(loc.size()>1){

                togglePageButton(true,true);
            }
            else{

                togglePageButton(false,false);
            }
        }
    }

    public void togglePageButton(boolean left, boolean right){
        btnLeft.setVisibility(left ? View.VISIBLE:View.INVISIBLE);
        btnRight.setVisibility(right ? View.VISIBLE:View.INVISIBLE);

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

    public interface OnLocationDetailListener{
        void onCloseBtnPress();
        void onSaveButtonPressed(int id);

    }

}
