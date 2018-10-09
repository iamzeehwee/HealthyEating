package com.example.healthyeating.healthyeating.Boundary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyeating.healthyeating.Controller.SingletonManager;
import com.example.healthyeating.healthyeating.Entity.Location;
import com.example.healthyeating.healthyeating.R;

import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.example.healthyeating.healthyeating.Controller.LocationsManager;
import java.util.ArrayList;
import android.view.View;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SearchAndSlide.OnSearchSubmitListener,OnMapReadyCallback,LocationDetailsFragment.OnFragmentInteractionListener,SearchAndSlide.OnFragmentInteractionListener {
    private ActionBar toolbar;
    private BottomNavigationView mBottomNavigation;
    private FrameLayout mMainFrame;
    TextView btmTextView;
    Button btn_save;
    Button btn_close;
    private FavouriteFragment favouriteFragment;
    private EateriesFragment eateriesFragment;
    private CaterersFragment caterersFragment;
    private HCSProductsFragment hcsProductsFragment;
    private LocationDetailsFragment ldf;
    SearchAndSlide searchSlide;
    SearchView searchView ;
    static int height = 0;
    int width = 0;
    LocationsManager lm;

    GoogleMap mMap;
String value;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //   Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }







    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (value != null) {
            ArrayList<Location> loc = lm.getSearchLocationID(Integer.parseInt(value));
            loadMapWithSelectedMarkers(loc);
        }
        else {
            ArrayList<Location> loc = lm.getListOfLocation("Eateries");
            loadMapWithMarkers(loc);
        }
//        for(int i = 0 ; i<loc.size();i++){
//            LatLng ll = new LatLng(loc.get(i).getLatitude(), loc.get(i).getLongitude());//1.315119,103.8909238,17.92z
//            googleMap.addMarker(new MarkerOptions().position(ll)
//                    .snippet(""+loc.get(i).getId()));
//
//        }
//        KmlLayer kmlLayer= null;
//
//        try {
//            kmlLayer = new KmlLayer(googleMap, R.raw.eateries,getApplicationContext());
//            kmlLayer.addLayerToMap();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.3521, 103.8198 ), 11.0f));
       //googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);



//        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Toast.makeText(MainActivity.this, "Clicked on LocationID "+marker.getSnippet(), Toast.LENGTH_LONG).show();
//
//                Location clickLoc = lm.getLocation(Integer.parseInt(marker.getSnippet()));
//
//                String address = clickLoc.getAddress();
//                String name = clickLoc.getName();
//                String floor_number = clickLoc.getFloor();
//                String unit_number = clickLoc.getUnit();
//
//                String display = "\r\n\tName : "+name+"\r\n"
//                        + "\tAddress : "+address+"\r\n"
//
//                        +"\tFloor No. : "+floor_number+"\r\n"
//                        +"\tUnit No. : "+unit_number;
//
//                btmTextView.setText(display);
//                btmTextView.setY(((float)height-800.0f));
//                btn_save.setY(((float)height-500.0f));
//
//                return true;
//            }
//        });
         //mMap = googleMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get LocationManager Instance
        lm = SingletonManager.getLocationManagerInstance();

        value = getIntent().getStringExtra("key");
        getIntent().removeExtra("key");

        if(googleServiceAvailable()){
            Toast.makeText(this,"Perfect",Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);

        mBottomNavigation = (BottomNavigationView) findViewById((R.id.navigation));
        mBottomNavigation.setLabelVisibilityMode(1);

        favouriteFragment = new FavouriteFragment();
        eateriesFragment = new EateriesFragment();
        caterersFragment = new CaterersFragment();
        hcsProductsFragment = new HCSProductsFragment();
        searchSlide = new SearchAndSlide();
        ldf = new LocationDetailsFragment();

        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_eateries:
                        return loadFragment(searchSlide);
                    case R.id.navigation_favourite:
                        return loadFragment(favouriteFragment);
                    case R.id.navigation_caterers:
                        return loadFragment(searchSlide);
                    case R.id.navigation_HCSProduct:
                        return loadFragment(hcsProductsFragment);
                    default:
                        return loadFragment(searchSlide);
                        //return false;
                }
            }
        });

        mBottomNavigation.getMenu().getItem(1).setChecked(true);
        btmTextView = (TextView)findViewById(R.id.btm_textView);
        btn_save = (Button)findViewById(R.id.button_save);
        btn_close = (Button)findViewById(R.id.button_close);
        btmTextView.setY(2500.0f);
        btn_save.setY(2500.0f);
        btn_close.setY(2000.0f);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        loadFragment(searchSlide);

        //When click close btn, set the information to be invisible
        btn_close = findViewById(R.id.button_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btmTextView.setAlpha(0.0f);
                btn_save.setAlpha(0.0f);
                btn_close.setAlpha(0.0f);
                loadMapWithMarkers(lm.getListOfLocation("Eateries"));
            }
        });

    }

    private void loadMapWithSelectedMarkers(ArrayList<Location> loc) {
        mMap.clear();
        Log.d("SearchBar","Cleared");
        for(int i = 0 ; i<loc.size();i++){
            LatLng ll = new LatLng(loc.get(i).getLatitude(), loc.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll)
                    .snippet(""+loc.get(i).getId()));

            Location clickLoc = lm.getLocation(loc.get(i).getId());

            String address = clickLoc.getAddress();
            String name = clickLoc.getName();
            String floor_number = clickLoc.getFloor();
            String unit_number = clickLoc.getUnit();

            String display = "\r\n\tName : "+name+"\r\n"
                    + "\tAddress : "+address+"\r\n"
                    +"\tFloor No. : "+floor_number+"\r\n"
                    +"\tUnit No. : "+unit_number+"\r\n";

            //Set infomration box to be visible
            btmTextView.setAlpha(1.0f);
            btn_save.setAlpha(1.0f);
            btn_close.setAlpha(1.0f);

            btmTextView.setText(display);
            btmTextView.setY(((float)height-(height*0.397157f)));
            //  btmTextView.setY(((float)height-800.0f));
            btn_save.setY(((float)height-(height*0.271739f)));
            btn_close.setY(((float)height-(height*0.271739f)));

        }
    }

    private void loadMapWithMarkers(ArrayList<Location> loc){
        mMap.clear();
        Log.d("SearchBar","Cleared");
        for(int i = 0 ; i<loc.size();i++){
            LatLng ll = new LatLng(loc.get(i).getLatitude(), loc.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll)
                    .snippet(""+loc.get(i).getId()));

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MainActivity.this, "Clicked on LocationID "+marker.getSnippet(), Toast.LENGTH_LONG).show();

                Location clickLoc = lm.getLocation(Integer.parseInt(marker.getSnippet()));

                String address = clickLoc.getAddress();
                String name = clickLoc.getName();
                String floor_number = clickLoc.getFloor();
                String unit_number = clickLoc.getUnit();

                String display = "\r\n\tName : "+name+"\r\n"
                        + "\tAddress : "+address+"\r\n"
                        +"\tFloor No. : "+floor_number+"\r\n"
                        +"\tUnit No. : "+unit_number+"\r\n";

                //Set infomration box to be visible
                btmTextView.setAlpha(1.0f);
                btn_save.setAlpha(1.0f);
                btn_close.setAlpha(1.0f);

                btmTextView.setText(display);
                btmTextView.setY(((float)height-(height*0.397157f)));
              //  btmTextView.setY(((float)height-800.0f));
                btn_save.setY(((float)height-(height*0.271739f)));
                btn_close.setY(((float)height-(height*0.271739f)));
                return true;
            }
        });

    }



    private boolean loadFragment(Fragment fragment){
        if(fragment !=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,fragment).commit();
            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.navigation_settings:
                //mBottomNavigation.setSelected(false);
                startActivity(new Intent(MainActivity.this, settings.class));
                return true;
                //startActivity(new Intent(this, About.class));
            default:
                startActivity(new Intent(MainActivity.this, settings.class));
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean googleServiceAvailable()
    {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable== ConnectionResult.SUCCESS){
            return true;
        }
        else if(api.isUserResolvableError(isAvailable)){
            Dialog dialog = api.getErrorDialog(this, isAvailable,0);
            dialog.show();
        }
        else{
            Toast.makeText(this,"Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void searchSubmit(String query) {
        Log.d("SearchBar","This called");
        loadMapWithMarkers(lm.searchLocations("Eateries",query));

        Toast.makeText(MainActivity.this, "Clicked "+ query, Toast.LENGTH_LONG).show();
    }
}
