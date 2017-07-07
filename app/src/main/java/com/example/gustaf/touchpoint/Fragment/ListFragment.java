package com.example.gustaf.touchpoint.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gustaf.touchpoint.Adapters.GridListAdapter;
import com.example.gustaf.touchpoint.BaseActivity;
import com.example.gustaf.touchpoint.HelpClasses.Coordinates;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment  {
    private static final int DEFAULT_SPAN_COUNT = 2;
    private RecyclerView mRecyclerView;
    private GridListAdapter mAdapter;

    private ArrayList<CityObject> cityObjects;
    android.location.Location current_location;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*Gets height and width of screen, for the image grid*/
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int height = displaymetrics.heightPixels;


        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewList);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT);



        /*Load the touchpoint objects from BaseActivity*/
        BaseActivity bs = (BaseActivity)getActivity();
        cityObjects = bs.getCityObjects();
        current_location = bs.getCurrentPosition();
        cityObjects = sortedTouchPoints(true);
        /*Sets the gridlistadapter with the touchpoint objects*/
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new GridListAdapter(cityObjects, gridLayoutManager, DEFAULT_SPAN_COUNT, width, height, getContext());
        mRecyclerView.setAdapter(mAdapter);



        return rootView;
    }

    /*
    * Receives new location from GPS
    *
    * */
    public void recieveLocation(android.location.Location loc){
        current_location = loc;
        cityObjects = sortedTouchPoints(false);


    }


    /**
     * Sort the touchpoint depening on which one is closest
     * to the device
     * */
    private ArrayList<CityObject> sortedTouchPoints(boolean isFirst) {

        if (cityObjects == null){
            return null;
        }
        Collections.sort(cityObjects, new Comparator<CityObject>() {
            @Override
            public int compare(CityObject lhs, CityObject rhs) {
                if (current_location != null){
                    float length1 = lengthBetween(current_location, lhs.getCoordinates());
                    float length2 = lengthBetween(current_location, rhs.getCoordinates());

                    return Float.compare(length1, length2);
                }

                return 0;
            }


        });
        /*Updates the sorted data to the gridview*/
        if (!isFirst){
            mAdapter.notifyDataSetChanged();
        }
        return cityObjects;
    }

    public ArrayList<CityObject> getCityObjects(){
        return cityObjects;
    }

    private float lengthBetween(android.location.Location currentLocation, Coordinates objectCoordinates) {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(currentLocation.getLatitude()- objectCoordinates.getLatitude());
        double dLng = Math.toRadians(currentLocation.getLongitude()- objectCoordinates.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(objectCoordinates.getLatitude())) * Math.cos(Math.toRadians(currentLocation.getLatitude())) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;


    }

}
