package com.example.gustaf.touchpoint.HelpClasses;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * This is the class were we actually get a location on our device. What it does is that it sends
 * a location request to Google. If it doesn't work, there are a lot of auto generated functions
 * which handles this. When the location is "accepted", getLocation holds the last known position
 * and checks if the position has changes.
 */
public class GetLocation implements LocationListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, Runnable {

    private static final long           POLLING_FREQ = 1000 * 30;
    private static final long           FASTEST_UPDATE_FREQ = 1000 * 5;
    private final Context                     mContext;
    private GoogleApiClient             mGoogleApiClient;
    private LocationRequest             mLocationRequest;
    private final Handler                     handler;

    public GetLocation(Context mContext, Handler handler) {

        this.handler = handler;
        this.mContext = mContext;


        buildGoogleApiClient();
        mGoogleApiClient.connect();
        createLocationRequest();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable()) {
            // Get best last location measurement meeting criteria
            //Log.v("LOCATION", String.valueOf(mCurrentPosition.getLatitude()));
            bestLastKnownLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {

        Message msg = handler.obtainMessage();
        msg.obj = location;
        handler.sendMessage(msg);

        //sendPost();


        Log.v("LOCATION CHANGED", location.getLongitude()+", "+location.getLatitude());

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){

    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(POLLING_FREQ);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);
    }

    private void bestLastKnownLocation() {
        // Get the best most recent location currently available
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);

        }
        catch(SecurityException e) {
            Log.d("DET HÄR VA INGE BRA", "WHUPS");
        }
    }

    private boolean servicesAvailable() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            Toast.makeText(mContext, "Google play services connection failed! Won't be able" +
                    " to get location.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Updates the location
     */
    @Override
    public void run() {
        boolean error = false;
        while(!error) {
            try {
                Thread.sleep(500);

            } catch (InterruptedException e) {
                e.printStackTrace();
                error = true;
            }
        }
    }


}