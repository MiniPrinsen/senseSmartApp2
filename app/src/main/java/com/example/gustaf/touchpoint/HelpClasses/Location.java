package com.example.gustaf.touchpoint.HelpClasses;

/**
 * Created by Gustaf on 16-08-09.
 */
public class Location {
    double longitude;
    double latitude;

    public Location(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
}
