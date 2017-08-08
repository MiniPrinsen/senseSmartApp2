package com.example.gustaf.touchpoint;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.gustaf.touchpoint.Adapters.ViewPagerAdapter;
import com.example.gustaf.touchpoint.Fragment.AchievementFragment;
import com.example.gustaf.touchpoint.Fragment.ChatFragment;
import com.example.gustaf.touchpoint.Fragment.GoogleMapsFragment;
import com.example.gustaf.touchpoint.Fragment.ListFragment;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.HelpClasses.GetCityObjects;
import com.example.gustaf.touchpoint.HelpClasses.GetLocation;
import com.example.gustaf.touchpoint.HelpClasses.NoSwipeViewPager;

import java.util.ArrayList;

/**
 *  BaseActivity contains the toolbar and navigationbar that
 *  is used by many of the activities in the app.
 */
public class BaseActivity extends AppCompatActivity{
    private static final int                      CITY_DIST = 50000;
    private TabLayout                            tabLayout;
    private android.location.Location      current_position;
    private ViewPager                       viewPagerTabbed;
    private NoSwipeViewPager               viewPagerDeafult;
    private ArrayList<CityObject>               cityObjects;
    private ViewPagerAdapter         viewPagerAdapterTabbed;
    private ViewPagerAdapter        viewPagerAdapterDeafult;
    protected AHBottomNavigation           bottomNavigation;
    protected Toolbar                               toolbar;

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

    @Override
    public void onStart(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        super.onStart();

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
                        showPage(0, "CHAT");
                        crimeItem.setDrawable(R.drawable.ic_map_empty);
                        dramaItem.setDrawable(R.drawable.ic_chat_filled);
                        documentaryItem.setDrawable(R.drawable.ic_trophy_empty);
                        break;
                    case 2:
                        showPage(1, "ACHIEVEMENTS");
                        crimeItem.setDrawable(R.drawable.ic_map_empty);
                        dramaItem.setDrawable(R.drawable.ic_chat_empty);
                        documentaryItem.setDrawable(R.drawable.ic_trophy_filled);
                        break;
                }
                return true;
            }
        });

    }

    /**
     *
     * @param pos position of the tab we want to display
     * @param title title of the tab we want to open
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
     * @param title
     */
    public void showTabbed(String title){
        viewPagerTabbed.setVisibility(View.VISIBLE);
        viewPagerDeafult.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        setToolBarTitle(title);
    }

    /**
     * Creates the list and map fragments
     */
    public void addTabs(){
        if (tabLayout == null) {
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            viewPagerTabbed = (ViewPager) findViewById(R.id.tabbed_viewPager);
            viewPagerAdapterTabbed = new ViewPagerAdapter(getSupportFragmentManager());
            viewPagerAdapterTabbed.addFragments(new ListFragment(), "LIST");
            viewPagerAdapterTabbed.addFragments(new GoogleMapsFragment(), "MAP");
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
     * Open achievement and chat fragments
     */
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
     * Sets the title for the toolbar
     * @param title
     */
    public void setToolBarTitle(String title){
        TextView toolbarText = (TextView)findViewById(R.id.toolbar_title);
        toolbarText.setText(title);
        //getSupportActionBar().setTitle(Html.fromHtml("<font color=\"red\">") + title + "</font>");

    }

    /**
     * Checks permission to use location
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If the app have permission to use location we will start using it
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("perms", "first If");
                    Thread background = new Thread(new GetLocation(getApplicationContext(), locationHandler));
                    background.start();
                }
                /* if not a new layout is loaded which says we need the location to use the app */
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
                            //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                        }
                    });
                }
                return;
            }
        }
    }


    /**
     * Gets called when location changes and updates the other fragments.
     */
    private Handler locationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            current_position = (android.location.Location) msg.obj;
            super.handleMessage(msg);
            if(cityObjects == null){
                new Thread(new GetCityObjects(current_position.getLongitude(),
                        current_position.getLatitude(), CITY_DIST, receiveFromDb)).start();
            }
            else {
                ((ListFragment) (viewPagerAdapterTabbed.getItem(0))).recieveLocation(current_position);
                for (CityObject tPoint : cityObjects) {
                    tPoint.setLengthBetween(current_position.getLongitude(), current_position.getLatitude());
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
            cityObjects = (ArrayList<CityObject>)msg.obj;
            if (cityObjects != null) {
                addTabs();
                addPages();
                showTabbed("SKELLEFTEÅ");
                ((ChatFragment) (viewPagerAdapterDeafult.getItem(0))).updateLocation(cityObjects.get(0));

            }
        }
    };

    public CityObject getCityObject(int i){
        return cityObjects.get(i);
    }
    public ArrayList<CityObject> getCityObjects(){ return cityObjects; }
    public Location getCurrentPosition(){ return current_position; }

}