package com.example.gustaf.touchpoint.HelpClasses;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by Soulstorm on 2/4/2015.
 */
public class GetLocation implements LocationListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, Runnable {

    private static final long ONE_MIN = 1000 * 60;
    private static final long CITY_DIST = 50000;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;

    Context mContext;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;


    Location mCurrentPosition;
    Handler handler;
    boolean visited = false;

    boolean arPaPlats = false;


    public GetLocation(Context mContext, Handler handler) {

        this.handler = handler;
        this.mContext = mContext;


        buildGoogleApiClient();
        mGoogleApiClient.connect();
        createLocationRequest();
    }

    public boolean checkApp(){
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equalsIgnoreCase("com.example.gustaf.touchpoint.menutest")) {
            return true;
        }
        else {
                return false;
            }
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

        mCurrentPosition = location;

        Message msg = handler.obtainMessage();
        msg.obj = location;
        handler.sendMessage(msg);

        //sendPost();


        Log.v("LOCATION CHANGED", location.getLongitude()+", "+location.getLatitude());

    }

    public double getLatitude() {

        return mCurrentPosition.getLatitude();
    }
    public double getLongitude() {

       return mCurrentPosition.getLongitude();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
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
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            Toast.makeText(mContext, "Google play services connection failed! Won't be able" +
                    " to get location.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void removeLocationUpdates () {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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