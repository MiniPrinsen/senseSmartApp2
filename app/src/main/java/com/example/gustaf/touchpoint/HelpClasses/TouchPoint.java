package com.example.gustaf.touchpoint.HelpClasses;

import java.text.DecimalFormat;

/**
 * Created by Gustaf on 16-08-09.
 */
public class TouchPoint {
    int image;
    String description;
    String name;
    Location location;
    int distance;
    boolean isOnline = false;

    public TouchPoint(String name, int image, String description, Location location){
        this.name = name;
        this.image = image;
        this.description = description;
        this.location = location;

    }

    public void setDistance(float distance){
        if (distance<50){
            isOnline = true;
        }
        else{
            isOnline = false;
        }
        this.distance = (int)distance;
    }

    public String getDistance(){

        if (distance<1000){
            return distance +" M";
        }

        else if(distance>1000 && distance<10000){
            double db = distance/1000.0;
            return new DecimalFormat("#.#").format(db) +" KM";
        }

        else{
            return distance/10000+" MIL";
        }


    }

    public boolean isOnline(){
        return isOnline;
    }

    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public int getImage(){
        return image;
    }
    public Location getLocation(){
        return location;
    }

    @Override
    public boolean equals(Object tp){
        try{
            TouchPoint touchPoint = (TouchPoint)tp;
            return touchPoint.getName().equals(name);
        }
        catch (Exception ex){
            return false;
        }
    }

}
