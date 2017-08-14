package com.example.gustaf.touchpoint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.gustaf.touchpoint.Adapters.ViewPagerAdapter;
import com.example.gustaf.touchpoint.Fragment.ChatFragment;
import com.example.gustaf.touchpoint.Fragment.GoogleMapsFragment;
import com.example.gustaf.touchpoint.Fragment.ListFragment;
import com.example.gustaf.touchpoint.Fragment.SettingsFragment;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.HelpClasses.Coordinates;
import com.example.gustaf.touchpoint.HelpClasses.GetCityObjects;
import com.example.gustaf.touchpoint.HelpClasses.GetLocation;
import com.example.gustaf.touchpoint.HelpClasses.NoSwipeViewPager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 *  BaseActivity is the starting activity of the entire application. This is where we Initialize
 *  the navigation for all pages as well as the toolbar for all the fragments that BaseActivity
 *  holds. BaseActivity also updates the location of the device to be able to dynamically change
 *  to the closest city object etc.
 */
public class BaseActivity extends AppCompatActivity{
    private static int                      CITY_DIST;
    private TabLayout                            tabLayout;
    private android.location.Location      current_position;
    private ViewPager                       viewPagerTabbed;
    private NoSwipeViewPager               viewPagerDeafult;
    private ArrayList<CityObject>               cityObjects;
    private ViewPagerAdapter         viewPagerAdapterTabbed;
    private ViewPagerAdapter        viewPagerAdapterDeafult;
    protected AHBottomNavigation           bottomNavigation;
    protected Toolbar                               toolbar;
    int position;

    /**
     *
     * @param savedInstanceState
     * Starts by setting the splash screen, and when the app is loaded, changes that to the layout
     * of BaseActivity. This is where we call the functions to inflate the toolbar and navigationbar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        /* Implements the toolbar */
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        /*Defines the navigationbar*/
        bottomNavigation=(AHBottomNavigation)findViewById(R.id.myBottomNavigation_ID);
        initializeNavigationBar();
    }

    /**
     * Here we are checking the parameters we are changing in SettingsFragment.
     * cityDistance is the slider we have in Settings. So, when the user changes the distance, we
     * pass the variable by using SharedPreferences. This also applies to the language.
     * We are doing it this way since we need to restart the entire app to be able to change
     * language etc. so this is the best way to do that.
     */
    @Override
    public void onStart(){
        Intent intent = getIntent();
        if(intent != null) {
             position = intent.getIntExtra("position",0);

        }
        SharedPreferences cityDistance = this.getSharedPreferences("cityDistance", Context.MODE_PRIVATE);
        CITY_DIST = cityDistance.getInt("seekBarProgress", 50000);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        SharedPreferences sharedPreferences = this.getSharedPreferences("selectedLanguage", Context.MODE_PRIVATE);
        String pine = sharedPreferences.getString("language","");
        Locale locale = new Locale(pine);//Set Selected Locale
        Locale.setDefault(locale);//set new locale as default
        Configuration config = new Configuration();//get Configuration
        config.locale = locale;//set config locale as selected locale
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
        super.onStart();

    }

    /**
     * Implements the bottom navigation as well as setting the proper behavior of it.
     * This includes unselected/selected, size, color, no text.
     * We are using AHBottomNavigation which can be found
     * at: https://github.com/aurelhubert/ahbottomnavigation. The version we are using is 2.0 which
     * is specified in the Gradle(app).
     */
    protected void initializeNavigationBar()
    {
        /*Creates the items*/
        final AHBottomNavigationItem crimeItem = new AHBottomNavigationItem(
                getResources().getString(R.string.map_name).toUpperCase(), R.drawable.ic_map_empty);
        final AHBottomNavigationItem dramaItem = new AHBottomNavigationItem(
                getResources().getString(R.string.chat_name), R.drawable.ic_chat_empty);
        final AHBottomNavigationItem documentaryItem = new AHBottomNavigationItem(
                getResources().getString(R.string.settings_name).toUpperCase(), R.drawable.ic_settings_empty);

        /*Adds the items to the bar*/
        bottomNavigation.addItem(crimeItem);
        bottomNavigation.addItem(dramaItem);
        bottomNavigation.addItem(documentaryItem);

        /*Sets the properties*/
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.colorPrimary));
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimary));

        bottomNavigation.setForceTint(true);

        bottomNavigation.setTitleTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigation.setTitleTextSize(24, 24);

        /*Sets the current position*/
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setCurrentItem(position);
        crimeItem.setDrawable(R.drawable.ic_map_filled);

        //HIDE
        bottomNavigation.hideBottomNavigation(false);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case 0:
                        showTabbed(getApplicationContext().getString(R.string.city).toUpperCase());
                        crimeItem.setDrawable(R.drawable.ic_map_filled);
                        dramaItem.setDrawable(R.drawable.ic_chat_empty);
                        documentaryItem.setDrawable(R.drawable.ic_settings_empty);
                        break;
                    case 1:
                        showPage(0, getApplicationContext().getString(R.string.nearby_name).toUpperCase());
                        crimeItem.setDrawable(R.drawable.ic_map_empty);
                        dramaItem.setDrawable(R.drawable.ic_chat_filled);
                        documentaryItem.setDrawable(R.drawable.ic_settings_empty);
                        break;
                    case 2:
                        showPage(1, getApplicationContext().getString(R.string.settings_name).toUpperCase());
                        crimeItem.setDrawable(R.drawable.ic_map_empty);
                        dramaItem.setDrawable(R.drawable.ic_chat_empty);
                        documentaryItem.setDrawable(R.drawable.ic_settings_filled);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * This function is used in SettingsFragment to refresh BaseActivity which is required to
     * be able to change language/max distance. The animations are set to make it look like we
     * are only refreshing the settings page.
     */
    public void refreshActivity() {
        Intent intent = new Intent(this, BaseActivity.class);
        intent.putExtra("position", 2);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.empty);

    }

    /**
     *
     * @param pos position of the tab we want to display
     * @param title title of the tab we want to open
     * This function shows the selected page from the bottom navigation. This only includes settings
     * and chat. The listFragment is of type tabLayout which we set as GONE here.
     */
    public void showPage(int pos, String title){
        viewPagerDeafult.setCurrentItem(pos, false);
        viewPagerDeafult.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        viewPagerTabbed.setVisibility(View.INVISIBLE);
        setToolBarTitle(title);

    }

    /**
     *
     * @param title title of the tab we want to open
     * This function shows the selected tab from the toolbar. This only includes the MAP and LIST
     * since chat and settings are of the type viewPagerDefault.
     */
    public void showTabbed(String title){
        viewPagerTabbed.setVisibility(View.VISIBLE);
        viewPagerDeafult.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        setToolBarTitle(title);
    }
    /**
     * Creates the list and map fragments by adding them to the viewPagerAdapterTabbed.
     */
    public void addTabs(){
        if (tabLayout == null) {
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            viewPagerTabbed = (ViewPager) findViewById(R.id.tabbed_viewPager);
            viewPagerAdapterTabbed = new ViewPagerAdapter(getSupportFragmentManager());
            viewPagerAdapterTabbed.addFragments(new ListFragment(), getApplicationContext().getString(R.string.list_name).toUpperCase());
            viewPagerAdapterTabbed.addFragments(new GoogleMapsFragment(), getApplicationContext().getString(R.string.map_name).toUpperCase());
            viewPagerTabbed.setAdapter(viewPagerAdapterTabbed);
            tabLayout.setupWithViewPager(viewPagerTabbed);
        }
        if (viewPagerAdapterDeafult != null){
            viewPagerDeafult.setVisibility(View.INVISIBLE);
        }
        viewPagerTabbed.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);



    }

    /**
     * Adds settings and chat fragments by adding them to viewPagerAdapterDefault.
     */
    public void addPages(){
        tabLayout.setVisibility(View.GONE);
        viewPagerTabbed.setVisibility(View.INVISIBLE);
        viewPagerDeafult = (NoSwipeViewPager) findViewById(R.id.viewPager_deafult);
        viewPagerAdapterDeafult = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapterDeafult.addFragments(new ChatFragment(), getApplicationContext().getString(R.string.chat_name).toUpperCase());
        viewPagerAdapterDeafult.addFragments(new SettingsFragment(), getApplicationContext().getString(R.string.settings_name).toUpperCase());
        viewPagerDeafult.setAdapter(viewPagerAdapterDeafult);
    }


    /**
     * Sets the title for the toolbar
     * @param title title of the page we want to open.
     */
    public void setToolBarTitle(String title){
        TextView toolbarText = (TextView)findViewById(R.id.toolbar_title);
        toolbarText.setText(title);

    }

    /**
     * Checks permission to use location. This is shown the first time we open the app.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If the app have permission to use location we will start using it and therefore
                // load the app.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("perms", "first If");
                    Thread background = new Thread(new GetLocation(getApplicationContext(), locationHandler));
                    background.start();
                }
                // if not a new layout is loaded which says we need the location to use the app.
                // The user gets a button which redirects to the settings for Touch Point.
                else {
                    Log.v("perms", "2nd If");
                    setContentView(R.layout.access_location);
                    Button settingsBtn = (Button)findViewById(R.id.settingsBtn);
                    settingsBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);

                        }
                    });
                }
            }
        }
    }


    /**
     * Gets called when location changes and updates the other fragments to show the appropriate
     * information.
     */
    private Handler locationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            current_position = (android.location.Location) msg.obj;
            super.handleMessage(msg);
            //If it's the first time we open the app, get the city objects.
            if(cityObjects == null){
                new Thread(new GetCityObjects(current_position.getLongitude(),
                        current_position.getLatitude(), CITY_DIST, receiveFromDb)).start();
            }
            //Updating
            else {
                ((ListFragment) (viewPagerAdapterTabbed.getItem(0))).recieveLocation(current_position);
                for (CityObject tPoint : cityObjects) {
                    tPoint.setCurrentLocation(new Coordinates(current_position.getLatitude(),
                            current_position.getLongitude()));
                    tPoint.setLengthBetween();
                }
                ((ChatFragment) (viewPagerAdapterDeafult.getItem(0))).updateLocation(cityObjects.get(0));
            }
        }

    };

    /**
     * Recieves the cityobjects from the database via a handler from getCityObjects class
     */
    private Handler receiveFromDb = new Handler(){
        @Override
        public void handleMessage(Message msg){

            if (msg.obj instanceof java.util.ArrayList){
                cityObjects = (ArrayList<CityObject>)msg.obj;

                    bottomNavigation.restoreBottomNavigation(true);
                    AppBarLayout app = (AppBarLayout)findViewById(R.id.toolbar);
                    app.setVisibility(View.VISIBLE);
                    app.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                    addTabs();
                    addPages();
                    if (position == 2){
                        showPage(1, getString(R.string.settings_name));
                        bottomNavigation.setCurrentItem(position);
                    }
                    else{
                        showTabbed(getString(R.string.city));
                    }
                    ((ChatFragment) (viewPagerAdapterDeafult.getItem(0))).updateLocation(cityObjects.get(0));


            }
            else if(msg.obj instanceof IOException){
                LinearLayout error = (LinearLayout)findViewById(R.id.error_container);
                error.setVisibility(View.VISIBLE);


            }


        }
    };

    public ArrayList<CityObject> getCityObjects(){ return cityObjects; }
    public Location getCurrentPosition(){ return current_position; }

}