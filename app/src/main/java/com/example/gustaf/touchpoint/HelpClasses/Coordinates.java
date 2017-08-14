package com.example.gustaf.touchpoint.HelpClasses;

/**
 * Class for coordinates. This class only includes the getter and setter for long and lat.
 */
public class Coordinates {
   private double longitude;
   private double latitude;

    Coordinates() {

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
    void setLongitude(double longitude){this.longitude = longitude;}
    void setLatitude(double latitude){this.latitude = latitude;}

}
