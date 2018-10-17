package com.example.healthyeating.healthyeating.boundary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.healthyeating.healthyeating.entity.HealthyLocation;
import com.example.healthyeating.healthyeating.interfaces.ILocationListener;
import com.example.healthyeating.healthyeating.R;

import java.util.ArrayList;



public class LocationDetailsFragment extends Fragment {

    private int current_pageNumber= 0 ;
    private ArrayList<HealthyLocation> loc;

    //Interface
    private ILocationListener locListener;

    //UI elements
    private TextView pageText;
    private TextView address,name;
    private ImageButton btnLeft,btnRight;
    private Button btn_save,btn_close;
    private RelativeLayout relativeLayout;

    public LocationDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        btnLeft =(ImageButton) v.findViewById(R.id.button_left);
        btnRight =(ImageButton) v.findViewById(R.id.button_right);
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
                loc = null;
                if (locListener != null)
                    locListener.onCloseBtnPress();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (locListener != null)
                    locListener.onSaveButtonPressed(loc.get(current_pageNumber));
            }
        });

        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        //locListener = null;
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

    public void reset(){
        loc = null;
        current_pageNumber = 0;

    }

    public HealthyLocation getInformation(){
        if(loc!=null)
        return loc.get(0);
        else
            return null;
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
            if(loc.size()>1)
                togglePageButton(true,true);
            else
                togglePageButton(false,false);
        }
    }

    public void togglePageButton(boolean left, boolean right){
        btnLeft.setVisibility(left ? View.VISIBLE:View.INVISIBLE);
        btnRight.setVisibility(right ? View.VISIBLE:View.INVISIBLE);

    }




}
