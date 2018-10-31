package com.example.healthyeating.healthyeating.boundary;

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
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyeating.healthyeating.controller.SingletonManager;
import com.example.healthyeating.healthyeating.entity.HealthyLocation;
import com.example.healthyeating.healthyeating.interfaces.IFavouriteListener;
import com.example.healthyeating.healthyeating.interfaces.IHCSListener;
import com.example.healthyeating.healthyeating.interfaces.ILocationListener;
import com.example.healthyeating.healthyeating.R;

import com.google.android.gms.maps.CameraUpdate;
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
import com.example.healthyeating.healthyeating.controller.LocationsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.view.View;

public class MainActivity extends AppCompatActivity implements LocationListener,
        ILocationListener,IFavouriteListener,IHCSListener,OnMapReadyCallback {

    private BottomNavigationView mBottomNavigation;
    private FrameLayout mMainFrame;

    //Fragments
    private FavouriteFragment favouriteFragment;
    private HCSProductsFragment hcsProductsFragment;
    private LocationSearchAndSlide searchSlide;
    private LocationDetailsFragment ldf;

    //Controller
    public LocationsManager lm; //This is our LocationsManager(Controller)

    //Google Maps
    private Marker prev_marker;
    private GoogleMap mMap;
    private LocationManager locationManager; //Pls do not confuse this with our locationManager.
    private ArrayList<Marker> listOfMarkers;
    private double latitude = 0;
    private double longitude = 0;
    private static final int REQUEST_LOCATION = 1;
    private float default_map_pin_color = BitmapDescriptorFactory.HUE_AZURE;//200.0f;
    private float selected_map_pin_color = BitmapDescriptorFactory.HUE_RED;

    //For searching
    private int prev_index = 0;
    private String searchQuery = "";
    private ListView list;
    ArrayAdapter<HealthyLocation> adapter;
    private LinearLayout resultLayout;

    //For favourite Transition
    private String favouriteLocName= "";
    private boolean favClicked = false;
    //Screen related
    static int height = 0;
    int width = 0;

    /**
     * This method inflates the menu and adds items to the action bar if it is present.
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //   Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * This method is called when the map is ready to be used.
     * It will call the displayOnMap method where markers will be allocated on the map
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<HealthyLocation> loc = lm.getListOfLocation();
        ArrayList<HealthyLocation> nearbyLocations = new ArrayList<HealthyLocation>();
        for (HealthyLocation location : loc) {
            if (lm.isWithinRange(location))
                nearbyLocations.add(location);
        }
        displayOnMap(nearbyLocations);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.3521, 103.8198), 11.0f));

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            lm.setCurrentLatLng(latitude, longitude);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, (int)(height*0.25), 0, (int)(height*0.293));
    }

    /**
     * This method is used to initialize the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         initGoogleMapLocation(1000);
         init();
         initNavigationBar();

         toggleMapView(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * This method handles the event when an item of the list view is selected
             * @param parent
             * @param view
             * @param pos
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                // Get the information about clicked location
                HealthyLocation clickedLocation = (HealthyLocation) list.getItemAtPosition(pos);
                listViewToMap(clickedLocation.getName());
            }
        });

        //Load the searchAndSlide fragment by default
        loadFragment(searchSlide);
    }

    /**
     * This method calls the toggleInformationBox method to display the location of a specific eatery/caterer when a location is selected
     * @param address
     */
    private void listViewToMap(String address){
        ArrayList<HealthyLocation> loc = lm.searchLocations(address);
        displayOnMap(loc);

        String name = loc.get(0).getName();
        searchSlide.setSpinnerValue(0);
        ldf.setInformation(loc);

        searchSlide.setSearchBoxText(name);
        toggleInformationBox(true);
        getBestView();
        toggleMapView(true);
    }

    /**
     * This method is used to place a map in the activity
     * @param time
     */
    private void initGoogleMapLocation(int time){

        if(googleServiceAvailable()) {
            setContentView(R.layout.activity_main);
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, 0, this);
    }


    /**
     * This method is used for initializing fragments, variables, layout etc, and hiding some fragment that should not be shown when the app first starts up
     */
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
        searchSlide = new LocationSearchAndSlide();
        ldf = new LocationDetailsFragment();

        //Find layout
        resultLayout = findViewById(R.id.resultLayout);
        mMainFrame = findViewById(R.id.main_frame);
        list = mMainFrame.findViewById(R.id.listViewTest);

        //Hide information box
        toggleInformationBox(false);

        //Hide resultLayout
        resultLayout.setVisibility(View.GONE);

        //Init arrayList
        listOfMarkers = new ArrayList<Marker>();

        //add location detail fragment to activity
        FragmentManager manager = getSupportFragmentManager();//create an instance of fragment manager
        FragmentTransaction transaction = manager.beginTransaction();//create an instance of Fragment-transaction
        transaction.add(R.id.informationLayout, ldf, "Frag_btm_tag");
        transaction.commit();
        ldf.hide();

    }

    /**
     * This method is for customizing the navigation bar and handle the changes when a one of the navigation selection is selected
     */
    private void initNavigationBar(){
        //Find btm navigation bar
        mBottomNavigation = (BottomNavigationView) findViewById((R.id.navigation));
        mBottomNavigation.setLabelVisibilityMode(1);

        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            /**
             * This method handles the event when one of the navigation item is selected
             * This method will return the fragment of the selected navigation item
             * @param item
             * @return loadFragment
             */
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_favourite:
                        searchSlide.setSpinnerValue(0);
                        return loadFragment(favouriteFragment);
                    case R.id.navigation_eateries:
                        if(!lm.getLocationType().equals("Eateries")){
                            //Changing from other tab to eateries, reload the markers
                            lm.setLocationType("Eateries");
                            reset();
                        }
                        return loadFragment(searchSlide);

                    case R.id.navigation_caterers:
                        if(!lm.getLocationType().equals("Caterers")) {
                            //Changing from other tab to caterers, reload the markers
                            lm.setLocationType("Caterers");
                            reset();
                        }
                        return loadFragment(searchSlide);
                    case R.id.navigation_HCSProduct:
                        return loadFragment(hcsProductsFragment);
                    default:
                        return loadFragment(searchSlide);

                }
            }
        });

        //Make eateries selected by default
        mBottomNavigation.getMenu().getItem(1).setChecked(true);
    }

    /**
     * This method is to handle the change when the user switch from eatery to caterers
     */
    private void reset(){
      //We do not want to reset the seekbar as it is troublesome for the user to set it again.

      toggleInformationBox(false);
      searchSlide.setSearchBoxText("");
      removeAllMarkersFromMap();

      //We want to display on both map and list to cater for the case where user switch from:
      // - eateries list view -> caterer list view or vice versa
      // - eateries map view -> caterer list view or vice versa
      toggleNoResultsFound(false);
      ldf.reset();
      //lm.setSortFilter(0);
      ArrayList<HealthyLocation> loc = lm.getListOfLocation();
      displayOnMap(loc);
      displayOnList(loc);
  }

    /**
     * This method is to display an error message that indicates no search results found when user search for a location
     * @param toggle
     */
   private void toggleNoResultsFound(boolean toggle){
        resultLayout.setVisibility(toggle? View.VISIBLE: View.INVISIBLE);
   }

    /**
     * This method is used to hide the fragment that displays the information of a specific location
     * @param toggle
     */
    private void toggleInformationBox(boolean toggle){
      if(!toggle)
          ldf.hide();
     else
         ldf.show();
    }

    /**
     * This method is used to switch between list view and the map when user selects the Sort by filter
     * Sort by filter can vary from Map to List View A-Z and List View Z-A
     * @param toggle
     */
    private void toggleMapView(boolean toggle){
            list.setVisibility(toggle?View.INVISIBLE:View.VISIBLE);
            mMainFrame.findViewById(R.id.map).setVisibility(toggle?View.VISIBLE:View.INVISIBLE);
    }

    /**
     * This method is used to remove all markers from the map
     */
   private void removeAllMarkersFromMap(){
        //Created this function because if we use mMap.clear(), the current location circle will be cleared too.
        for(int i = 0; i<listOfMarkers.size();i++){
            listOfMarkers.get(i).remove();
        }
        listOfMarkers.clear();
        prev_marker = null;
   }


    /**
     * This method is used to display specific locations on the map
     * This method also handles the display of information when one marker is selected from the map
     * @param loc
     */
    private void displayOnMap(ArrayList<HealthyLocation> loc){
        HashMap<LatLng,Marker> existingLatLng = new HashMap<>();
        removeAllMarkersFromMap();
        HealthyLocation selectedHealthyLocation = ldf.getInformation();
        for(int i = 0 ; i<loc.size();i++) {
            LatLng ll = new LatLng(loc.get(i).getLatitude(), loc.get(i).getLongitude());

            if(existingLatLng.get(ll)==null){
                Marker m = mMap.addMarker(new MarkerOptions().position(ll)
                        .snippet("" + loc.get(i).getId()));

                existingLatLng.put(ll,m);
                listOfMarkers.add(m);
                m.setIcon(BitmapDescriptorFactory.defaultMarker(default_map_pin_color));
                if(selectedHealthyLocation!=null){

                    if(selectedHealthyLocation.getId() == loc.get(i).getId()){
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(selected_map_pin_color));
                    }
                }
            }
            else{
                String temp = existingLatLng.get(ll).getSnippet();
                existingLatLng.get(ll).setSnippet(temp+","+loc.get(i).getId());
            }

        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            /**
             * This method shows the information of the location when a marker on the map is clicked
             * @param marker
             * @return true
             */
            @Override
            public boolean onMarkerClick(Marker marker) {
                String[] snippet = marker.getSnippet().split(",");
                if (prev_marker == null) {
                    prev_marker = marker;
                } else {
                    prev_marker.setIcon(BitmapDescriptorFactory.defaultMarker(default_map_pin_color));
                    prev_marker = marker;

                }
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(selected_map_pin_color));

                LatLng coordinate = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, mMap.getCameraPosition().zoom);
                mMap.animateCamera(yourLocation);

                ArrayList<HealthyLocation> clickLocs = new ArrayList<>();
                for(int i = 0; i<snippet.length;i++){

                    clickLocs.add(lm.getLocation(Integer.parseInt(snippet[i])));
                }
                ldf.setInformation(clickLocs);

                //Set infomration box to be visible
                toggleInformationBox(true);

               return true;
            }

        });
    }

    /**
     * This method will zoom the camera out till the current location and the markers can be seen on the map.
     */
    private void getBestView(){
        int padding = 100; // offset from edges of the map in pixels

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ArrayList<Marker> markers = new ArrayList<Marker>();
        LatLng ll = new LatLng(lm.getCurrent_lat(), lm.getCurrent_long());

        markers.add(listOfMarkers.get(0));//get the first marker
        markers.add(mMap.addMarker(new MarkerOptions().position(ll)));
        markers.get(1).remove();

        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        CameraUpdate cu = null;
        if(lm.getLatLngSet())
            cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
        else
            cu = CameraUpdateFactory.newLatLngZoom(listOfMarkers.get(0).getPosition(),12);

        mMap.animateCamera(cu);
    }

    /**
     * This method gets the current location of the user
     */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location network_provider_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location gps_provider_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location passive_provider_location = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

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

    /**
     * This method displays the pop up alert message if the user did not switch on the GPS setting on the mobile
     */
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

    /**
     * This method is used for loading various fragments
     * @param fragment
     * @return true if fragment is present, else return false
     */
    private boolean loadFragment(Fragment fragment){
        if(fragment !=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,fragment).commit();
            return true;
        }
        return false;
    }

    /**
     * This method is to check if googleServiceAvailable is available
     * @return true if GooglePlayServices is available, else return false if GooglePlayServices is not available
     */
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

    /**
     * This method display the list of location in the list view
     * It will toggle the no search found message if there no results is returned
     * @param loc
     */
    private void displayOnList(ArrayList<HealthyLocation> loc){
        CustomListAdapter locAdapter = new CustomListAdapter(getApplicationContext(), R.layout.list_item_eateries, loc);
        list.setAdapter(locAdapter);

        //adapter = new ArrayAdapter<HealthyLocation>(getApplicationContext(), android.R.layout.simple_list_item_1, loc);
        //list.setAdapter(adapter);
        if(searchSlide.getSpinnerValue()!=0) { //We want to show in List View only
            if (loc.size() == 0) {
                toggleNoResultsFound(true);
            } else
                toggleNoResultsFound(false);
        }

    }

    /**
     * This method is for the custom adapter for complex views in Eateries/Caterers tab
     */
    private class CustomListAdapter extends ArrayAdapter<HealthyLocation> {
        private int layout;
        private List<HealthyLocation> locationList;
        private CustomListAdapter(Context context, int resource, List<HealthyLocation> locationList) {
            super(context, resource, locationList);
            this.locationList = locationList;
            layout = resource;
        }

        /**
         * This method is for the building the list view for the Eateries and Caterers List View
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MainActivity.ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                MainActivity.ViewHolder viewHolder = new MainActivity.ViewHolder();

                // establish links to layout elements
                viewHolder.locationName = (TextView) convertView.findViewById(R.id.eateries_list_item_name);
                viewHolder.locationDetails = (TextView) convertView.findViewById(R.id.eateries_list_item_details);

                convertView.setTag(viewHolder);
            }

            mainViewholder = (MainActivity.ViewHolder) convertView.getTag();

            // set variable text into text views
            mainViewholder.locationName.setText(getItem(position).getName());
            mainViewholder.locationDetails.setText(getItem(position).getAddress() + "\n");

            return convertView;
        }
    }

    /**
     * This class is for the View that is used to display Eateries / Caterers list view
     */
    public class ViewHolder {
        TextView locationName;    // location's name
        TextView locationDetails; // location's address, floor and unit
    }

    /**
     * This is method that search for location via the search box
     * @param query
     */
    @Override
    public void searchSubmit(String query) {
        searchQuery = query;
        ArrayList<HealthyLocation> loc = lm.searchLocations(query);
        displayOnMap(loc);
        displayOnList(loc);
    }

    /**
     * This method is to change the value of the distance filter, changes of the location within the distance will be displayed
     * @param dis
     */
    @Override
    public void onSliderRelease(double dis) {
        lm.setLimitDistance(dis);
        ArrayList<HealthyLocation> loc = lm.searchLocations(searchQuery);
        HealthyLocation getSelectedLocation = ldf.getInformation();
boolean near = false;
        if(searchSlide.getSpinnerValue()==0) { //We want to do this in Map View only
            if (searchQuery.length() > 0)
                toggleInformationBox(loc.size() == 0 ? false : true);
            if (getSelectedLocation != null) {
                near = lm.isWithinRange(getSelectedLocation);
                toggleInformationBox(near);
            }
        }
        displayOnMap(loc);
        displayOnList(loc);
    }

    /**
     * This method is used to remove markers from the map when the user is attempting to change the distance filter
     */
    @Override
    public void onSliderHoldDown() {
        //clear map markers first
        removeAllMarkersFromMap();
    }

    /**
     * This method is to determine which options selected by the user when user change the sort by filter
     * @param index
     */
    @Override
    public void onSpinnerChange(int index) {
        Log.d("Spinner","I AM HERE CHANGED "+index);
        if(index == 0){
            toggleMapView(true);
            toggleNoResultsFound(false);
        }
        else{
            if(prev_index==0)
                //From Map View to ListView,
                searchSlide.setSearchBoxText("");

            toggleMapView(false);
            Log.d("Spinner","I AM HERE CALLED FALSE");
            toggleInformationBox(false);
            if(index==1)
                lm.setSortFilter(0);
            else if(index == 2)
                lm.setSortFilter(1);

            displayOnList(lm.searchLocations(searchQuery));
        }
        if(index!=prev_index)
             prev_index = index;
    }

    /**
     * This method search for a location based on the latitude and longitude
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
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

    /**
     * This method closes the location information
     */
    @Override
    public void onCloseBtnPress() {
        toggleInformationBox(false);
        searchSlide.setSearchBoxText("");
        if(prev_marker!=null)
             prev_marker.setIcon(BitmapDescriptorFactory.defaultMarker(default_map_pin_color));
    }

    /**
     * This method is for saving a location to favourite list
     * @param location
     */
    @Override
    public void onSaveButtonPressed(HealthyLocation location) {
        if(lm.addToFavourite(location)) {
            // Close information box after saving to favourite


            Log.d("SUCCESS", "successfully saved");
        }
        else {
            lm.removeFavourite(location);
            //Log.d("ERROR", "failed to save");

        }
    }

    @Override
    public ArrayList<HealthyLocation> getFavsByCategory(String categoryChosen) {
        if (categoryChosen.equals("Favourite Eateries"))
            return lm.getFavouriteEateries();
        else if (categoryChosen.equals("Favourite Caterers"))
            return lm.getFavouriteCaterers();
        else
            return lm.getFavouriteList();
    }

    @Override
    public void onFavListItemClicked(String name, int spinnerValue) {
        lm.setLimitDistance(50000.0);
        searchSlide = new LocationSearchAndSlide();

        favouriteLocName=name;
        loadFragment(searchSlide);
        favClicked = true;
    }


    @Override
    public void removeFavourite(HealthyLocation location) {
        lm.removeFavourite(location);
        ldf.toggleSaveButton();
        //toggleInformationBox(false);
    }

    @Override
    public void searchSlideOnResume() {
        if (favClicked) {
            favClicked = false;
            searchSlide.setSearchBoxText(favouriteLocName);
            lm.setLocationType("Eateries");
            ArrayList<HealthyLocation> loc = lm.searchLocations(favouriteLocName);
            if (loc.size() == 0) {
                lm.setLocationType("Caterers");
                loc = lm.searchLocations(favouriteLocName);
            }

            listViewToMap(favouriteLocName);

            HealthyLocation clickLoc = loc.get(0);
            if (clickLoc.getLocationType().equals("Eateries")) {

                mBottomNavigation.getMenu().getItem(1).setChecked(true);

            } else if (clickLoc.getLocationType().equals("Caterers")) {
                mBottomNavigation.getMenu().getItem(2).setChecked(true);

            }
            toggleMapView(true);
        }

    }
/*
    @Override
    public void onCatButtonPressed(String catName) {

    }

    @Override
    public void onCatSearchSubmit(String search) {

    }

    @Override
    public void onCatSpinnerChange(int catIndex) {

    }

    @Override
    public void onSortSpinnerChange(int sortIndex) {

    } */
}
