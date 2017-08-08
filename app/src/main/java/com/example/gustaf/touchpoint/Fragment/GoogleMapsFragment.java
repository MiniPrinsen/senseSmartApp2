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
 * A simple {@link Fragment} subclass.
 */
public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<CityObject> cityObjects;
    Bundle args;
    CityObject cObject;

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

    public void loadInfoWindow() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {

                for (CityObject cityObject : cityObjects){
                    if (arg0.getTitle().equals(cityObject.getName())){
                        View v = getActivity().getLayoutInflater().inflate(R.layout.layout_info_window, null);
                        Button info = (Button) v.findViewById(R.id.infoButton);
                        info.setText(cityObject.getName());
                        BitmapLayout back = (BitmapLayout) v.findViewById(R.id.bitmapBackground);

                        Picasso.with(getContext()).load(cityObject.getImgs().get(0)).into(back);
                        return v;
                    }

                }
                return null;
            }

            @Override
            public View getInfoContents(Marker arg0) {

                return null;
            }
        });

    }


    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter,
            GoogleMap.OnInfoWindowClickListener{
        @Override
        public View getInfoWindow(Marker arg0) {


            for (int i = 0; i < cityObjects.size(); i++){
                CityObject cityObject = cityObjects.get(i);
                if (arg0.getTitle().equals(cityObject.getName())){
                    View v = getActivity().getLayoutInflater().inflate(R.layout.layout_info_window, null);
                   // args = new Bundle();
                   // args.putInt("cityobject",i);
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