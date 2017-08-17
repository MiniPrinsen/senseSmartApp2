package com.example.gustaf.touchpoint.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gustaf.touchpoint.BaseActivity;
import com.example.gustaf.touchpoint.DetailsActivity;
import com.example.gustaf.touchpoint.HelpClasses.BitmapLayout;
import com.example.gustaf.touchpoint.HelpClasses.CityObject;
import com.example.gustaf.touchpoint.HelpClasses.Coordinates;
import com.example.gustaf.touchpoint.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This is where we inflate the google maps API and adds the info windows.
 */
public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<CityObject> cityObjects;
    private CityObject cObject;

    public GoogleMapsFragment() {
    // Required empty public constructor
    }


    /**
     * Inflates the view
     */
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

    /**
     * Defines the google maps with markers and all that.
     * The for-loop loops through all the city objects and sets a marker at each specific location.
     * We set the camera update(zoom) and enables your own position.
     * @param googleMap the map to be inflated
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    BaseActivity bs = (BaseActivity)getActivity();
    cityObjects = bs.getCityObjects();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i<cityObjects.size(); i++){
            CityObject cObject = cityObjects.get(i);
            Coordinates loc = cObject.getCoordinates();
            LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.addMarker(new MarkerOptions().position(pos).title(cObject.getName()));


            builder.include(pos);
        }

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

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
            Log.d("Coordinate not accessed", "Security Exception");
        }
    }


    /**
     * Since we want an image and the title of the object we need to inflate our own InfoWindow.
     * We loop through all the city objects and adds a custom info window to each one of them.
     * Google maps InfoWindow doesn't allow different clicks on it, instead it takes a "screenshot"
     * of the info window and then adds that image onto the map.
     */
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter,
            GoogleMap.OnInfoWindowClickListener{
        @Override
        public View getInfoWindow(Marker arg0) {


            for (int i = 0; i < cityObjects.size(); i++){
                CityObject cityObject = cityObjects.get(i);
                if (arg0.getTitle().equals(cityObject.getName())){

                    //Doing View.inflate instead of inflater because of warning when passing null
                    // with inflater.
                    View v = View.inflate(getContext(), R.layout.layout_info_window, null);
                    //View v = getActivity().getLayoutInflater().inflate(R.layout.layout_info_window, null);
                    cObject = cityObject;
                    Button info = (Button) v.findViewById(R.id.infoButton);
                    info.setText(cityObject.getName());
                    BitmapLayout back = (BitmapLayout) v.findViewById(R.id.bitmapBackground);
                    Picasso.with(getContext()).load(cityObject.getImgs().get(0)).into(back);
                    mMap.setOnInfoWindowClickListener(this);
                    return v;

                }

            }
            return null;
        }

        /**
         * Sets the clickListener to the info window. Here we put a bundle as information on which
         * window we are clicking on. We are doing this to specify which details window we want to
         * open.
         * @param marker that has been clicked.
         */
        @Override
        public void onInfoWindowClick(Marker marker) {
            Intent i = new Intent(getContext(), DetailsActivity.class);

            i.putExtra("cityobject",cObject);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.slide_left,R.anim.empty);

        }

        @Override
        public View getInfoContents(Marker arg0) {

            return null;
        }
    }
}