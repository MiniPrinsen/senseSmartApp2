package com.example.gustaf.touchpoint.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gustaf.touchpoint.BaseActivity;
import com.example.gustaf.touchpoint.HelpClasses.Location;
import com.example.gustaf.touchpoint.HelpClasses.TouchPoint;
import com.example.gustaf.touchpoint.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    public GoogleMapsFragment() {
    // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_google_maps, container, false);
        // Inflate the layout for this fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.frag_map);
        mapFragment.getMapAsync(this);

        return view;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    BaseActivity bs = (BaseActivity)getActivity();
    ArrayList<TouchPoint> touchPoints = bs.getTouchPoints();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (TouchPoint tPoint : touchPoints) {
            Location loc = tPoint.getLocation();
            LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.addMarker(new MarkerOptions().position(pos).title(tPoint.getName()));
            builder.include(pos);
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
        try {
            mMap.setMyLocationEnabled(true);
        }
        catch(SecurityException e) {
            Log.d("Location not accessed", "Security Exception");
        }
    }
}