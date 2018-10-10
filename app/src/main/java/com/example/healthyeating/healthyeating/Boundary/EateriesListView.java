package com.example.healthyeating.healthyeating.Boundary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.support.v4.app.Fragment;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthyeating.healthyeating.Controller.LocationsManager;
import com.example.healthyeating.healthyeating.Controller.SingletonManager;
import com.example.healthyeating.healthyeating.Entity.HealthyLocation;
import com.example.healthyeating.healthyeating.R;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class EateriesListView extends AppCompatActivity implements SearchAndSlide.OnFragmentInteractionListener {

    private BottomNavigationView mBottomNavigation;
    private FavouriteFragment favouriteFragment;
    private EateriesFragment eateriesFragment;
    private CaterersFragment caterersFragment;
    private HCSProductsFragment hcsProductsFragment;
    private LocationDetailsFragment ldf;
    SearchAndSlide searchSlide;

    SearchView searchView;
    ListView listView;
    Spinner spinner;
    String value;
    SeekBar seek;
    private double seekBarValue = 0;
    private static final int MAX_SEEKBAR_VALUE = 50000;
    DecimalFormat f = new DecimalFormat("##.0");
    TextView tvSearchResult;
    LinearLayout resultLayout;

    LocationsManager lm = SingletonManager.getLocationManagerInstance();
    ArrayList<HealthyLocation> loc = lm.getListOfLocation();
    ArrayAdapter<HealthyLocation> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eateries_list_view);

        //Retrieve Parameters, used for sorting
        value = getIntent().getStringExtra("key");
        //getIntent().removeExtra("key");
        lm.setSortFilter(Integer.parseInt(value));

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
                        //startActivity(new Intent(EateriesListView.this, settings.class));
                        lm.setLocationType("Eateries");
                        loc = lm.getListOfLocation();
                        adapter = new ArrayAdapter<HealthyLocation>(EateriesListView.this, android.R.layout.simple_list_item_1, loc);
                        listView.setAdapter(adapter);
                        //return loadFragment(searchSlide);
                         return true;
                    case R.id.navigation_favourite:
                        return loadFragment(favouriteFragment);
                    case R.id.navigation_caterers:
                        lm.setLocationType("Caterers");
                        loc = lm.getListOfLocation();
                        adapter = new ArrayAdapter<HealthyLocation>(EateriesListView.this, android.R.layout.simple_list_item_1, loc);
                        listView.setAdapter(adapter);
                        //return loadFragment(searchSlide);
                       return true;
                    case R.id.navigation_HCSProduct:
                        return loadFragment(hcsProductsFragment);
                    default:
                        return loadFragment(searchSlide);
                    //return false;
                }
            }
        });
        mBottomNavigation.getMenu().getItem(1).setChecked(true);

        searchView = (SearchView) findViewById(R.id.searchView);
        listView = (ListView) findViewById(R.id.eateriesListView);
        resultLayout = (LinearLayout) findViewById(R.id.resultLayout);
        resultLayout.setVisibility(View.GONE);

        loc = lm.sortList(loc);
        adapter = new ArrayAdapter<HealthyLocation>(this, android.R.layout.simple_list_item_1, loc);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loc = lm.searchLocations(query);
                adapter = new ArrayAdapter<HealthyLocation>(EateriesListView.this, android.R.layout.simple_list_item_1, loc);
                if (loc.isEmpty()) {
                    resultLayout.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(EateriesListView.this, "Toast 2", Toast.LENGTH_SHORT).show();
                    resultLayout.setVisibility(View.GONE);
                    listView.setAdapter(adapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                boolean searchResult = searchSubmit(newText);
                if (searchResult == true) {
                    resultLayout.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                } else {
                    resultLayout.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
                return false;
            }
        });

        SeekBar sk = (SeekBar) findViewById(R.id.seekBar2);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView t=(TextView)findViewById(R.id.textView4);

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
                    listView.setVisibility(View.GONE);
                }catch (ClassCastException cce){

                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("Slider1","Release");
                //Interact with MainActivity when slider change so that MainActivity can interact with the LocationsManager

                try{
                    Log.d("Slider1","In here with distance "+ seekBarValue);

                    lm.setLimitDistance(seekBarValue);
                    loc = lm.searchLocations("");
                    adapter = new ArrayAdapter<HealthyLocation>(EateriesListView.this, android.R.layout.simple_list_item_1, loc);
                    listView.setAdapter(adapter);
                    listView.setVisibility(View.VISIBLE);
                }catch (ClassCastException cce){

                }
            }
        });

        spinner = (Spinner)findViewById(R.id.sortSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getApplicationContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // If parameter passed = 0, it is either Map / A - Z. At this page, it will be A - Z so set 1 directly
        if (Integer.parseInt(value) == 0) {
            spinner.setSelection(1,false);
        }
        else {
            spinner.setSelection(2,false);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + "Check " + pos + "Check " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
                // For passing sortFilter value when choose the sort condition
                Intent i = null;
                if (pos == 2) {
                    i = new Intent(EateriesListView.this, EateriesListView.class);
                    i.putExtra("key", "1");
                }
                if (pos == 0) {
                    i = new Intent(EateriesListView.this, MainActivity.class);
                }
                startActivity(i);

                // Remove animation when switch activity
                ((Activity) EateriesListView.this).overridePendingTransition(0,0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                // Get the information of 1 eatery
                Object listItem = listView.getItemAtPosition(pos);

                String locationDetails = listItem.toString();
                int endIndex =  locationDetails.indexOf("\r\nAddress: ");//locationDetails.indexOf(":") + 1;
                String address = locationDetails.substring(0, endIndex);
                int selectedLocID = lm.searchLocationID(address);

                Intent i = null;
                i = new Intent(EateriesListView.this, MainActivity.class);
                i.putExtra("key", String.valueOf(selectedLocID));
                startActivity(i);

                // Remove animation when switch activity
                ((Activity) EateriesListView.this).overridePendingTransition(0,0);

                Toast.makeText(parent.getContext(),
                        "setOnItemClickListener : " + "Check " + 0 + "Check " + endIndex + "Check " + selectedLocID +  "End" + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
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

    public void resetSliderAndTextBox(){
        searchView.setQuery("",false);
        seek.setProgress(MAX_SEEKBAR_VALUE);
    }

    public boolean searchSubmit(String query){
        loc = lm.searchLocations(query);
        adapter = new ArrayAdapter<HealthyLocation>(EateriesListView.this, android.R.layout.simple_list_item_1, loc);
        listView.setAdapter(adapter);

        if (loc.isEmpty()) {
            return false;
        }
        else
            return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
