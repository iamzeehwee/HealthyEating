package com.example.healthyeating.healthyeating.Boundary;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyeating.healthyeating.Controller.SingletonManager;
import com.example.healthyeating.healthyeating.Entity.HealthyLocation;
import com.example.healthyeating.healthyeating.R;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
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

public class MainActivity extends AppCompatActivity implements LocationListener, SearchAndSlide.OnSpinnerChangeListener, SearchAndSlide.OnSliderChangeListener, SearchAndSlide.OnSearchSubmitListener, OnMapReadyCallback, LocationDetailsFragment.OnFragmentInteractionListener, SearchAndSlide.OnFragmentInteractionListener {

    private BottomNavigationView mBottomNavigation;
    private FrameLayout mMainFrame;

    //For location details
    TextView btmTextView;
    Button btn_save,btn_close;

    //Fragments
    private FavouriteFragment favouriteFragment;
    private HCSProductsFragment hcsProductsFragment;
    private SearchAndSlide searchSlide;

    //Controller
    LocationsManager lm; //This is our LocationsManager(Controller)

    //Google Maps
    private Marker prev_marker;
    private GoogleMap mMap;
    private LocationManager locationManager; //Pls do not confuse this with our locationManager.
    private ArrayList<Marker> listOfMarkers;
    private double latitude = 0;
    private double longitude = 0;
    private static final int REQUEST_LOCATION = 1;
    private float default_map_pin_color = 200.0f;
    private float selected_map_pin_color = BitmapDescriptorFactory.HUE_RED;

    //For searching
    private int prev_index = 0;
    private String searchQuery = "";
    private ListView list;
    ArrayAdapter<HealthyLocation> adapter;
    private LinearLayout resultLayout;

    //Screen related
    static int height = 0;
    int width = 0;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //   Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        ArrayList<HealthyLocation> loc = new ArrayList<>();

        loc = lm.getListOfLocation();
        displayOnMap(loc);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.3521, 103.8198), 11.0f));


        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
            LatLng latLng = new LatLng(latitude, longitude);
            //mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


            lm.setCurrentLatLng(latitude, longitude);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, 600, 0, 700);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


         initGoogleMapLocation(1000);
         init();

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);

        list = (ListView) mMainFrame.findViewById(R.id.listViewTest);
        toggleMapView(true);

      //For listview in mainactivity
        ArrayList<HealthyLocation> loc = lm.getListOfLocation();
        loc = lm.sortList(loc);
        displayOnList(loc);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                // Get the information of 1 eatery
                Object listItem = list.getItemAtPosition(pos);

                String locationDetails = listItem.toString().trim();
                int endIndex =  locationDetails.indexOf("\r\nAddress: ");//locationDetails.indexOf(":") + 1;
                String address = locationDetails.substring(0, endIndex);
                int selectedLocID = lm.searchLocationID(address);

                ArrayList<HealthyLocation> loc = new ArrayList<HealthyLocation>();
                HealthyLocation clickLoc = lm.getLocation(selectedLocID);
                loc.add(clickLoc);
                displayOnMap(loc);

                String name = clickLoc.getName();

                searchSlide.setSpinnerValue(0);

                btmTextView.setText(getInformationBoxText(clickLoc));
                searchSlide.setSearchBoxText(name);
                toggleInformationBox(true);
                getBestView();

            }
        });


        mBottomNavigation = (BottomNavigationView) findViewById((R.id.navigation));
        mBottomNavigation.setLabelVisibilityMode(1);


        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_favourite:
                        return loadFragment(favouriteFragment);

                    case R.id.navigation_eateries:
                        if(!lm.getLocationType().equals("Eateries")){
                            //Changing from other tab to eateries, reload the markers
                            lm.setLocationType("Eateries");
                            searchSlide.resetSliderAndTextBox();
                            toggleInformationBox(false);

                            ArrayList<HealthyLocation> loc = new ArrayList<>();
                            loc = lm.getListOfLocation();

                                removeAllMarkersFromMap();
                                displayOnMap(loc);


                                displayOnList(lm.sortList(loc));
                            toggleNoResultsFound(false);
                        }


                        return loadFragment(searchSlide);

                    case R.id.navigation_caterers:
                        if(!lm.getLocationType().equals("Caterers")) {
                            //Changing from other tab to caterers, reload the markers
                            lm.setLocationType("Caterers");
                            toggleInformationBox(false);

                            searchSlide.resetSliderAndTextBox();
                            ArrayList<HealthyLocation> loc = new ArrayList<>();
                            loc = lm.getListOfLocation();

                                removeAllMarkersFromMap();
                                displayOnMap(loc);
                                displayOnList(lm.sortList(loc));
                            toggleNoResultsFound(false);
                        }

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


        loadFragment(searchSlide);

        //When click close btn, set the information to be invisible
      //  btn_close = findViewById(R.id.button_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleInformationBox(false);
                searchSlide.setSearchBoxText("");
                if(prev_marker!=null)
                    prev_marker.setIcon(BitmapDescriptorFactory.defaultMarker(default_map_pin_color));
            }
        });

    }

    private void initGoogleMapLocation(int time){

        if(googleServiceAvailable()) {
            Toast.makeText(this, "Perfect", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, 0, this);
    }


    private void init(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //Get LocationManager Instance
        lm = SingletonManager.getLocationManagerInstance();

        //Init fragments
        favouriteFragment = new FavouriteFragment();
        hcsProductsFragment = new HCSProductsFragment();
        searchSlide = new SearchAndSlide();


        //Find element
        btmTextView = (TextView)findViewById(R.id.btm_textView);
        btn_save = (Button)findViewById(R.id.button_save);
        btn_close = (Button)findViewById(R.id.button_close);
        resultLayout = (LinearLayout) findViewById(R.id.resultLayout);

        //Set coordinate
        btmTextView.setY(height*0.04180602006688963210702341137124f);
        btn_save.setY(((float)height-(height*0.271739f)));
        btn_close.setY(((float)height-(height*0.271739f)));

        //Hide information box
        toggleInformationBox(false);

        //Hide resultLayout
        resultLayout.setVisibility(View.GONE);

        //Init arrayList
        listOfMarkers = new ArrayList<Marker>();
    }


   private void toggleNoResultsFound(boolean toggle){
       if(toggle){
           resultLayout.setVisibility(View.VISIBLE);

       }
       else{

           resultLayout.setVisibility(View.INVISIBLE);
       }
   }

    private void toggleInformationBox(boolean toggle){
        float value = toggle? 1.0f:0.0f;
        btmTextView.setAlpha(value);
        btn_save.setAlpha(value);
        btn_close.setAlpha(value);
    }

    private void toggleMapView(boolean toggle){
        if(toggle){
            list.setVisibility(View.INVISIBLE);
            mMainFrame.findViewById(R.id.map).setVisibility(View.VISIBLE);
        }
        else{
            list.setVisibility(View.VISIBLE);
            mMainFrame.findViewById(R.id.map).setVisibility(View.INVISIBLE);
        }
    }

   private void removeAllMarkersFromMap(){
        //Created this function because if we use mMap.clear(), the current location circle will be cleared too.
        for(int i = 0; i<listOfMarkers.size();i++){
            listOfMarkers.get(i).remove();
        }
        listOfMarkers.clear();
        prev_marker = null;
   }
    private void displayOnMap(ArrayList<HealthyLocation> loc){
        //mMap.clear();
        removeAllMarkersFromMap();

        for(int i = 0 ; i<loc.size();i++) {
            LatLng ll = new LatLng(loc.get(i).getLatitude(), loc.get(i).getLongitude());
            listOfMarkers.add(mMap.addMarker(new MarkerOptions().position(ll)
                    .snippet("" + loc.get(i).getId())));

            listOfMarkers.get(i).setIcon(BitmapDescriptorFactory.defaultMarker(default_map_pin_color));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                HealthyLocation clickLoc = lm.getLocation(Integer.parseInt(marker.getSnippet()));
                //Set infomration box to be visible
                toggleInformationBox(true);

                btmTextView.setText(getInformationBoxText(clickLoc));
                //Set marker to blue on selected
                //Issues : If location marker is in same location, i.e Shopping Mall have Koufu, McDonalds, etc, marker will have some issues changing colour,

                if(prev_marker==null){
                    prev_marker = marker;
                }
                else{
                    prev_marker.setIcon(BitmapDescriptorFactory.defaultMarker(default_map_pin_color));
                    prev_marker = marker;
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(selected_map_pin_color));

                    LatLng coordinate = new LatLng(clickLoc.getLatitude(),clickLoc.getLongitude());
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, mMap.getCameraPosition().zoom);
                    mMap.animateCamera(yourLocation);
                }
                return true;
            }
        });

    }

    private String getInformationBoxText(HealthyLocation clickLoc){
        String address = clickLoc.getAddress();
        String name = clickLoc.getName();
        String floor_number = clickLoc.getFloor();
        String unit_number = clickLoc.getUnit();
        if(floor_number.length()==0)
            floor_number = "No floor number";
        if(unit_number.length()==0)
            unit_number = "No unit number";

        String display = "\r\n\tName : "+name+"\r\n"
                + "\tAddress : "+address+"\r\n"
                +"\tFloor No. : "+floor_number+"\r\n"
                +"\tUnit No. : "+unit_number+"\r\n";

        return display;
    }


    private void getBestView(){
        int padding = 200; // offset from edges of the map in pixels

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ArrayList<Marker> markers = new ArrayList<Marker>();
        LatLng ll = new LatLng(lm.getCurrent_lat(), lm.getCurrent_long());

        markers.add(listOfMarkers.get(0));//get the first marker
        markers.add(mMap.addMarker(new MarkerOptions().position(ll)));
        markers.get(1).remove();

        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            android.location.Location network_provider_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            android.location.Location gps_provider_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            android.location.Location passive_provider_location = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (network_provider_location != null) {
                latitude = network_provider_location.getLatitude();
                longitude = network_provider_location.getLongitude();

                Log.d("LOCA11","Your current location is"+ "\n" + "Lattitude = " + latitude
                        + "\n" + "Longitude = " + longitude);

            } else  if (gps_provider_location != null) {
                latitude = gps_provider_location.getLatitude();
                longitude = gps_provider_location.getLongitude();

                Log.d("LOCA12","Your current location is"+ "\n" + "Lattitude = " + latitude
                        + "\n" + "Longitude = " + longitude);


            } else  if (passive_provider_location != null) {
                latitude =  passive_provider_location.getLatitude();
                longitude = passive_provider_location.getLongitude();

               Log.d("LOCA13","Your current location is"+ "\n" + "Lattitude = " + latitude
                        + "\n" + "Longitude = " + longitude);

            }else{

                Toast.makeText(this,"Unble to get your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

    private void displayOnList(ArrayList<HealthyLocation> loc){
        adapter = new ArrayAdapter<HealthyLocation>(getApplicationContext(), android.R.layout.simple_list_item_1, loc);
        list.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void searchSubmit(String query) {

        searchQuery = query;
        ArrayList<HealthyLocation> loc = lm.searchLocations(searchQuery);
        displayOnMap(loc);
        displayOnList(loc);
        if(searchSlide.getSpinnerValue()!=0) { //We want to show in List View only
            if (loc.size() == 0) {
                toggleNoResultsFound(true);
            } else
                toggleNoResultsFound(false);
        }

        //Toast.makeText(MainActivity.this, "Clicked "+ query, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSliderRelease(double dis) {

        lm.setLimitDistance(dis);
        ArrayList<HealthyLocation> loc = lm.searchLocations(searchQuery);
        if(searchSlide.getSpinnerValue()==0) { //We want to do this in Map View only
            if (searchQuery.length() > 0) {
                if (loc.size() == 0)
                    toggleInformationBox(false);
                else
                    toggleInformationBox(true);
            }
        }

        if(searchSlide.getSpinnerValue()!=0) { //We want to show in List View only
            if (loc.size() == 0) {
                toggleNoResultsFound(true);
            } else
                toggleNoResultsFound(false);
        }


        displayOnMap(loc);
        displayOnList(loc);

    }

    @Override
    public void onSliderHoldDown() {
        //clear map markers first
        removeAllMarkersFromMap();
    }


    @Override
    public void onSpinnerChange(int index) {


        if(index == 0){
            toggleMapView(true);
            toggleNoResultsFound(false);
           // displayOnMap(lm.searchLocations(searchQuery));
        }
        else{
            if(prev_index==0) {
                //From Map View to ListView,
                searchSlide.setSearchBoxText("");
            }
            toggleMapView(false);
            toggleInformationBox(false);

            if(index==1){
                lm.setSortFilter(0);
            }
            else if(index == 2){
                lm.setSortFilter(1);
            }


            displayOnList(lm.sortList(lm.searchLocations(searchQuery)));

        }

        if(index!=prev_index){
             prev_index = index;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    //    Log.i("Message: ","Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());
         if(lm.setCurrentLatLng(location.getLatitude(),location.getLongitude())){
             ArrayList<HealthyLocation> loc = lm.searchLocations(searchQuery);
             displayOnMap(loc);
             displayOnList(loc);
         }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
