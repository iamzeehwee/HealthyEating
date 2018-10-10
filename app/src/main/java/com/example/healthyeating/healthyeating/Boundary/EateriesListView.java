package com.example.healthyeating.healthyeating.Boundary;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.support.v4.app.Fragment;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.example.healthyeating.healthyeating.Controller.LocationsManager;
import com.example.healthyeating.healthyeating.Controller.SingletonManager;
import com.example.healthyeating.healthyeating.Entity.HealthyLocation;
import com.example.healthyeating.healthyeating.R;
import java.util.ArrayList;

public class EateriesListView extends AppCompatActivity {

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

        searchView = (SearchView) findViewById(R.id.searchView);
        listView = (ListView) findViewById(R.id.eateriesListView);

        loc = lm.sortList(loc);
        adapter = new ArrayAdapter<HealthyLocation>(this, android.R.layout.simple_list_item_1, loc);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loc = lm.searchLocations(query);
                adapter = new ArrayAdapter<HealthyLocation>(EateriesListView.this, android.R.layout.simple_list_item_1, loc);
                listView.setAdapter(adapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
