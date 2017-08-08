package com.example.gustaf.touchpoint.HelpClasses;

/**
 * Created by Gustaf on 16-08-09.
 */
public class Coordinates {
    double longitude;
    double latitude;

    public Coordinates() {

    }

    public Coordinates(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }
    public double getLatitude(){
        return latitude;
    }
    public void setLongitude(double longitude){this.longitude = longitude;}
    public void setLatitude(double latitude){this.latitude = latitude;}

}
