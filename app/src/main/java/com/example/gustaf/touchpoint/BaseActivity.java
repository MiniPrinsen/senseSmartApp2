package com.example.gustaf.touchpoint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.gustaf.touchpoint.Adapters.ViewPagerAdapter;
import com.example.gustaf.touchpoint.Fragment.AchievementFragment;
import com.example.gustaf.touchpoint.Fragment.ChatFragment;
import com.example.gustaf.touchpoint.Fragment.GoogleMapsFragment;
import com.example.gustaf.touchpoint.Fragment.ListFragment;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.HelpClasses.Coordinates;
import com.example.gustaf.touchpoint.HelpClasses.GetCityObjects;
import com.example.gustaf.touchpoint.HelpClasses.GetLocation;
import com.example.gustaf.touchpoint.HelpClasses.NoSwipeViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *  BaseActivity contains the toolbar and navigationbar that
 *  is used by many of the activities in the app.
 */
public class BaseActivity extends AppCompatActivity {
    TabLayout tabLayout;

    private static final int CITY_DIST = 50000;

    android.location.Location current_position;
    ViewPager viewPagerTabbed;
    NoSwipeViewPager viewPagerDeafult;

    ArrayList<CityObject> cityObjects;

    ViewPagerAdapter viewPagerAdapterTabbed;
    ViewPagerAdapter viewPagerAdapterDeafult;
    /*
    * BottomNavigation -> tabs in the bottom
    * Toolbar -> Acitivtybar in the top
    *
    * */
    protected AHBottomNavigation bottomNavigation;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);
       // cityObjects = createExampleTouchPoints();

        /* Implements the toolbar */
        toolbar = (Toolbar) findViewById(R.id.gustaf_toolbar);
        setSupportActionBar(toolbar);

        /*Defines the navigationbar*/
        bottomNavigation=(AHBottomNavigation)findViewById(R.id.myBottomNavigation_ID);
        initializeNavigationBar();
        //addTabs();
        //addPages();

//        showTabbed("SKELLEFTEÅ");
    }

    /**
     * Implements the bottom navigation
     */
    protected void initializeNavigationBar()
    {
        /*Creates the items*/
        final AHBottomNavigationItem crimeItem = new AHBottomNavigationItem(
                getResources().getString(R.string.map_name), R.drawable.ic_map_empty);
        final AHBottomNavigationItem dramaItem = new AHBottomNavigationItem(
                getResources().getString(R.string.chat_name), R.drawable.ic_chat_empty);
        final AHBottomNavigationItem documentaryItem = new AHBottomNavigationItem(
                getResources().getString(R.string.achievement_name), R.drawable.ic_trophy_empty);

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
        bottomNavigation.setCurrentItem(0);
        crimeItem.setDrawable(R.drawable.ic_map_filled);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case 0:
                        showTabbed("SKELLEFTEÅ");
                        crimeItem.setDrawable(R.drawable.ic_map_filled);
                        dramaItem.setDrawable(R.drawable.ic_chat_empty);
                        documentaryItem.setDrawable(R.drawable.ic_trophy_empty);
                        break;
                    case 1:
                        showPage(0, "CHATT");
                        crimeItem.setDrawable(R.drawable.ic_map_empty);
                        dramaItem.setDrawable(R.drawable.ic_chat_filled);
                        documentaryItem.setDrawable(R.drawable.ic_trophy_empty);
                        break;
                    case 2:
                        showPage(1, "PRESTATIONER");
                        crimeItem.setDrawable(R.drawable.ic_map_empty);
                        dramaItem.setDrawable(R.drawable.ic_chat_empty);
                        documentaryItem.setDrawable(R.drawable.ic_trophy_filled);
                        break;
                }
                return true;
            }
        });

    }

    public void showPage(int pos, String title){
        viewPagerDeafult.setCurrentItem(pos, false);
        viewPagerDeafult.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        viewPagerTabbed.setVisibility(View.INVISIBLE);
        setToolBarTitle(title);
    }

    public void showTabbed(String title){
        viewPagerTabbed.setVisibility(View.VISIBLE);
        viewPagerDeafult.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        setToolBarTitle(title);
    }

    public void addTabs(){
        if (tabLayout == null) {
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            viewPagerTabbed = (ViewPager) findViewById(R.id.tabbed_viewPager);

            viewPagerAdapterTabbed = new ViewPagerAdapter(getSupportFragmentManager());
            viewPagerAdapterTabbed.addFragments(new ListFragment(), "LISTA");
            viewPagerAdapterTabbed.addFragments(new GoogleMapsFragment(), "KARTA");
            viewPagerTabbed.setAdapter(viewPagerAdapterTabbed);
            tabLayout.setupWithViewPager(viewPagerTabbed);
        }
        if (viewPagerAdapterDeafult != null){
            viewPagerDeafult.setVisibility(View.INVISIBLE);
        }
        viewPagerTabbed.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);

    }

    public void addPages(){
        tabLayout.setVisibility(View.GONE);
        viewPagerTabbed.setVisibility(View.INVISIBLE);
        viewPagerDeafult = (NoSwipeViewPager) findViewById(R.id.viewPager_deafult);

        viewPagerAdapterDeafult = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapterDeafult.addFragments(new ChatFragment(), "CHAT");
        viewPagerAdapterDeafult.addFragments(new AchievementFragment(), "ACHIEVEMENTS");
        viewPagerDeafult.setAdapter(viewPagerAdapterDeafult);
    }


    /**
     *
     * @param classToOpen Which class to open
     * @param animation which animation when class opens
     */
    protected void classStarter(Class classToOpen, int animation){
        Intent myIntent = new Intent(getApplicationContext(), classToOpen);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(myIntent, 0);
        overridePendingTransition(0,0);
    }

    protected void setToolBarTitle(String title){
        TextView toolbarText = (TextView)findViewById(R.id.toolbar_title);
        toolbarText.setText(title);
        getSupportActionBar().setTitle("");

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    System.exit(1);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onStart(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        super.onStart();
        Thread background = new Thread(new GetLocation(getApplicationContext(), locationHandler));
        background.start();
    }

    Handler locationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            android.location.Location loc = (android.location.Location) msg.obj;
            Log.v("ASD", String.valueOf(loc.getLongitude()));
            current_position = loc;
            if(cityObjects == null){
                new Thread(new GetCityObjects(loc.getLongitude(), loc.getLatitude(), CITY_DIST, receiveFromDb)).start();
            }
            else {
                ((ListFragment) (viewPagerAdapterTabbed.getItem(0))).recieveLocation(loc);
                for (CityObject tPoint : cityObjects) {
                    tPoint.setDistance(lengthBetween(loc, tPoint.getCoordinates()));
                }
                ((ChatFragment) (viewPagerAdapterDeafult.getItem(0))).updateLocation(cityObjects.get(0));
            }


            super.handleMessage(msg);
        }

    };

    Handler receiveFromDb = new Handler(){
        @Override
        public void handleMessage(Message msg){
            cityObjects = (ArrayList<CityObject>)msg.obj;
            if (cityObjects != null) {
                Log.v("CITYOBJ", cityObjects.toString());
                addTabs();
                addPages();
                showTabbed("SKELLEFTEÅ");
            }
            else{
                Log.v("CITYOBJ", "ISNULL");

            }

        }
    };

    public ArrayList<CityObject> createExampleTouchPoints() {
        ArrayList<Integer> images12 = new ArrayList<>();
        /*images12.add(R.drawable.bonnstan_square600);
        images12.add(R.drawable.johannaiparken_square600);*/
        CityObject bonnstan = new CityObject("Bonnstan", "Bonnstan är en stad som ser ut som den är jättegammal"
        , new Coordinates(64.751409,20.929309), (float)34.5, (float)2.5, images12);

        ArrayList<String> bnstnImages = new ArrayList<>();
        bnstnImages.add("http://www.stiftsgarden.se/wp-content/uploads/2013/05/Bonnstan.jpg");
        bnstnImages.add("http://skellefteamuseum.se/wp-content/uploads/2012/10/Brand_panorama1b-640x320.jpg");
        bonnstan.addImages(bnstnImages);


        CityObject bonnstan2 = new CityObject("Lejonströmsbron", "Lejonströmsbron är en stad som ser ut som den är jättegammal"
                , new Coordinates( 64.750233,20.914101), (float)34.5, (float)2.5, images12);
        ArrayList<String> ljnstromsbronImages = new ArrayList<>();
        ljnstromsbronImages.add("http://static.panoramio.com/photos/large/45113441.jpg");
        ljnstromsbronImages.add("http://visitskelleftea.se/wp-content/uploads/2016/04/eb240e6da475aa792ff1025cee55d05a.jpeg");
        bonnstan2.addImages(ljnstromsbronImages);

        CityObject bonnstan3 = new CityObject("Johanna", "Johanna är en stad som ser ut som den är jättegammal"
                , new Coordinates(64.749489,20.951445), (float)34.5, (float)2.5, images12);
        ArrayList<CityObject> tpoints = new ArrayList<>();
        ArrayList<String> johannaImages = new ArrayList<>();
        johannaImages.add("https://cdn3.cdnme.se/4435958/8-3/105_johanna_i_parken_minus_12_grader_2015-12-13_566da84fddf2b364016af473.jpg");
        johannaImages.add("http://www.skelleftea.se/Bilder/Bildspel/Stadsparken%20-%20historiska%20bilder/af56b70f-69ba-492c-b60f-68fc63a0ff60~__H.png?doprocessing=1&w=980");
        bonnstan3.addImages(johannaImages);

        tpoints.add(bonnstan);
        tpoints.add(bonnstan2);
        tpoints.add(bonnstan3);
        return tpoints;
    }

   /* public ArrayList<CityObject> createExampleTouchPoints(){

        CityObject bonnstan = new CityObject("Bonnstan", R.drawable.bonnstan_square600,
                "Bonnstan är det område med 116 kyrkstugor i Skellefteå, som ligger strax öster " +
                        "om landsförsamlingens kyrka. Bonnstan ingår tillsammans med bland annat " +
                        "landskyrkan och Lejonströmsbron i riksintresset" +
                        " \"Skellefteå sockencentrum\" (AC 20 Skellefteå," +
                        " västra delen) med motiveringen \"Skellefteå sockencentrum, " +
                        "kyrkstad och marknadsplats vid den gamla kustlandsvägen," +
                        " som berättar om platsens betydelse för bygdens kyrkliga," +
                        " sociala och kommersiella liv sedan medeltiden.\"[1]" +
                        " Bonnstan är sedan 1982 byggnadsminne.",
                new Coordinates(64.7506874,20.933061199999997));
        CityObject johanna_i_parken = new CityObject("Johanna i Parken",
                R.drawable.johannaiparken_square600, "", new Coordinates(64.7497674, 20.95096460000002));

        CityObject lejonstromsbron = new CityObject("Lejonströmsbron",
                R.drawable.lejonstromsbron_square600, "", new Coordinates(64.7501393,20.91393830000004));

        CityObject volleyBoll = new CityObject("Volleybollplanen Campus",
                R.drawable.volleyball_square600, "", new Coordinates(64.74512696, 20.9547472));

        ArrayList<CityObject> tpoints = new ArrayList<>();

        tpoints.add(bonnstan);
        tpoints.add(lejonstromsbron);
        tpoints.add(johanna_i_parken);
        tpoints.add(volleyBoll);

        return tpoints;
    }*/

    private ArrayList<CityObject> sortedTouchPoints() {

        if (cityObjects == null){
            return null;
            //cityObjects = createExampleTouchPoints();
        }
        Collections.sort(cityObjects, new Comparator<CityObject>() {
            @Override
            public int compare(CityObject lhs, CityObject rhs) {
                if (current_position != null){
                    float length1 = lengthBetween(current_position, lhs.getCoordinates());
                    float length2 = lengthBetween(current_position, rhs.getCoordinates());
                    return Float.compare(length1, length2);
                }

                return 0;
            }

        });
        return cityObjects;
    }

    public ArrayList<CityObject> getCityObjects(){
        return cityObjects;
    }

    public CityObject getCityObject(int i){
        return cityObjects.get(i);
    }

    private float lengthBetween(android.location.Location currentLocation, Coordinates objectCoordinates) {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(currentLocation.getLatitude()- objectCoordinates.getLatitude());
        double dLng = Math.toRadians(currentLocation.getLongitude()- objectCoordinates.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(objectCoordinates.getLatitude())) *
                        Math.cos(Math.toRadians(currentLocation.getLatitude())) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

}