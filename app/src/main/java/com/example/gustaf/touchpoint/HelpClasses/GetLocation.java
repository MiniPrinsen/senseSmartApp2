package com.example.gustaf.touchpoint.HelpClasses;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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


    Location volleyboll;
    Location mCurrentPosition;
    Handler handler;
    boolean visited = false;

    boolean arPaPlats = false;


    public GetLocation(Context mContext, Handler handler) {

        this.handler = handler;
        this.mContext = mContext;

        volleyboll = new Location("");
        volleyboll.setLatitude(64.74512696);
        volleyboll.setLongitude(20.9547472);

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

        sendPost();


        Log.v("LOCATION CHANGED", location.getLongitude()+", "+location.getLatitude());

    }

    public void sendPost() {

        if(!visited) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        String urlParameters = "longitude=" + getLongitude() + "&latitude=" +
                                getLatitude() + "&distance=" + CITY_DIST;
                        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                        URL url = new URL(" http://52.28.236.84:8080/sensesmart/hey");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {

                            urlConnection.setRequestMethod("POST");
                            urlConnection.setChunkedStreamingMode(0);

                            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                            out.write(postData);
                            out.flush();

                            int status = (urlConnection).getResponseCode();

                            String json = getStringFromInputStream(urlConnection.getInputStream());
                            JSONArray array = new JSONArray(json);
                            Gson gson = new Gson();
                            for (int i = 0; i<array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                CityObject cObject = gson.fromJson(obj.toString(), CityObject.class);
                                Log.d("LOGGAR", cObject.getName());
                                Log.d("LOGGAR", cObject.getDescription());
                                Log.d("LOGGAR", (String.valueOf(cObject.coordinates.getLatitude())));
                                Log.d("LOGGAR", (String.valueOf(cObject.coordinates.getLongitude())));
                                Log.d("LOGGAR", (String.valueOf(cObject.rating)));
                                Log.d("LOGGAR", (String.valueOf(cObject.persons_voted)));

                                for(String img : cObject.getImgs()){
                                    Log.d("LOGGAR", img);
                                }

                            }

                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            visited = true;
        }
    }

    /**
     *
     * Creates a string from inputstream
     *
     * @param inputstream
     * @return
     */
    private String getStringFromInputStream(InputStream inputstream) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(inputstream,
                    StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return sb.toString();

    }


    public double getLatitude() {

        return mCurrentPosition.getLatitude();
    }
    public double getLongitude() {

       return mCurrentPosition.getLongitude();
    }

    public static boolean inRadius(Location currentLocation, Location objectLocation, int radius) {


        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(currentLocation.getLatitude()-objectLocation.getLatitude());
        double dLng = Math.toRadians(currentLocation.getLongitude()-objectLocation.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(objectLocation.getLatitude())) *
                        Math.cos(Math.toRadians(currentLocation.getLatitude())) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        if (dist < radius){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
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

    @Override
    public void run() {
        boolean error = false;
        while(!error) {
            try {
                Thread.sleep(5000);
                Log.v("HEJ", "UPDATED FROM GETLOCATION");

            } catch (InterruptedException e) {
                e.printStackTrace();
                error = true;
            }
        }
    }


}