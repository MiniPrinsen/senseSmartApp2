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
import com.example.gustaf.touchpoint.HelpClasses.GetLocation;
import com.example.gustaf.touchpoint.HelpClasses.Location;
import com.example.gustaf.touchpoint.HelpClasses.NoSwipeViewPager;
import com.example.gustaf.touchpoint.HelpClasses.TouchPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *  BaseActivity contains the toolbar and navigationbar that
 *  is used by many of the activities in the app.
 */
public class BaseActivity extends AppCompatActivity {
    TabLayout tabLayout;

    android.location.Location current_position;
    ViewPager viewPagerTabbed;
    NoSwipeViewPager viewPagerDeafult;

    ArrayList<TouchPoint> touchPoints;

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
        setContentView(R.layout.activity_base);
        super.onCreate(savedInstanceState);
        touchPoints = createExampleTouchPoints();

        /* Implements the toolbar */
        toolbar = (Toolbar) findViewById(R.id.gustaf_toolbar);
        setSupportActionBar(toolbar);

        /*Defines the navigationbar*/
        bottomNavigation=(AHBottomNavigation)findViewById(R.id.myBottomNavigation_ID);
        initializeNavigationBar();
        addTabs();
        addPages();

        showTabbed("SKELLEFTEÅ");
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
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        bottomNavigation.setForceTint(true);

        bottomNavigation.setTitleTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
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
                        showPage(0, "CHAT");
                        crimeItem.setDrawable(R.drawable.ic_map_empty);
                        dramaItem.setDrawable(R.drawable.ic_chat_filled);
                        documentaryItem.setDrawable(R.drawable.ic_trophy_empty);
                        break;
                    case 2:
                        showPage(1, "DINA TROFÈER");
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
        viewPagerAdapterDeafult.addFragments(new ChatFragment(), "CHATT");
        viewPagerAdapterDeafult.addFragments(new AchievementFragment(), "DINA TROFÈER");
        //viewPagerAdapterDeafult.addFragments(new ChatWindowFragment(), "STATSFONTÄNEN");
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
        Thread background = new Thread(new GetLocation(getApplicationContext(), handler));
        background.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            android.location.Location loc = (android.location.Location) msg.obj;
            Log.v("LOCATION HEHE", String.valueOf(loc.getLongitude()));
            current_position = loc;
            ((ListFragment)(viewPagerAdapterTabbed.getItem(0))).recieveLocation(loc);
            for (TouchPoint tPoint : touchPoints) {
                tPoint.setDistance(lengthBetween(loc, tPoint.getLocation()));
            }
            ((ChatFragment)(viewPagerAdapterDeafult.getItem(0) ) ).updateLocation(touchPoints.get(0));

            super.handleMessage(msg);
        }

    };

    public ArrayList<TouchPoint> createExampleTouchPoints(){

        TouchPoint bonnstan = new TouchPoint("Bonnstan", R.drawable.bonnstan_square600, "Bonnstan är det område med 116 kyrkstugor i Skellefteå, som ligger strax öster om landsförsamlingens kyrka. Bonnstan ingår tillsammans med bland annat landskyrkan och Lejonströmsbron i riksintresset \"Skellefteå sockencentrum\" (AC 20 Skellefteå, västra delen) med motiveringen \"Skellefteå sockencentrum, kyrkstad och marknadsplats vid den gamla kustlandsvägen, som berättar om platsens betydelse för bygdens kyrkliga, sociala och kommersiella liv sedan medeltiden.\"[1] Bonnstan är sedan 1982 byggnadsminne.", new Location(64.7506874,20.933061199999997));
        TouchPoint johanna_i_parken = new TouchPoint("Johanna i Parken", R.drawable.johannaiparken_square600, "", new Location(64.7497674, 20.95096460000002));
        TouchPoint lejonstromsbron = new TouchPoint("Lejonströmsbron", R.drawable.lejonstromsbron_square600, "", new Location(64.7501393,20.91393830000004));
        TouchPoint volleyBoll = new TouchPoint("Volleybollplanen Campus", R.drawable.volleyball_square600, "", new Location(64.74512696, 20.9547472));

        ArrayList<TouchPoint> tpoints = new ArrayList<>();

        tpoints.add(bonnstan);
        tpoints.add(lejonstromsbron);
        tpoints.add(johanna_i_parken);
        tpoints.add(volleyBoll);

        return tpoints;
    }

    private ArrayList<TouchPoint> sortedTouchPoints() {

        if (touchPoints == null){
            touchPoints = createExampleTouchPoints();
        }
        Collections.sort(touchPoints, new Comparator<TouchPoint>() {
            @Override
            public int compare(TouchPoint lhs, TouchPoint rhs) {
                if (current_position != null){
                    float length1 = lengthBetween(current_position, lhs.getLocation());
                    float length2 = lengthBetween(current_position, rhs.getLocation());
                    return Float.compare(length1, length2);
                }

                return 0;
            }

        });
        return touchPoints;
    }

    public ArrayList<TouchPoint> getTouchPoints(){
        return touchPoints;
    }

    private float lengthBetween(android.location.Location currentLocation, Location objectLocation) {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(currentLocation.getLatitude()-objectLocation.getLatitude());
        double dLng = Math.toRadians(currentLocation.getLongitude()-objectLocation.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(objectLocation.getLatitude())) * Math.cos(Math.toRadians(currentLocation.getLatitude())) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public void hideNavigationBar(boolean hide){
        bottomNavigation.setVisibility(View.GONE);
        viewPagerDeafult.setPadding(0, 0, 0, 0);
    }

}