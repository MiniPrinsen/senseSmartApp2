package com.example.gustaf.touchpoint.HelpClasses;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Gustaf on 16-08-09.
 */
public class CityObject {
    String name;
    String description;
    Coordinates coordinates;
    float persons_voted;
    float rating;
    private Oid _id;
    int distance;
    ArrayList<String> images;

    ArrayList<Integer> imgs;
    boolean isOnline = false;
   /* {     "_id" : { "$oid" : "595524a99e575b7952858fc6" },
            "name" : "sgksp�",
            "description" : "kp�k�pk",
            "coordinates" : { "longitude" : 12.0, "latitude" : 12.0 },
            "persons_voted" : 0.0,
            "rating" : 0.0,
            "images" : ["http://localhost:8080/hello/images/31d907ab-3685-43ee-973c-40dd74056269.png"] }*/
    public  CityObject() {
        imgs = new ArrayList<>();
        coordinates = new Coordinates();
        images = new ArrayList<>();
    }

    public ArrayList<String> getImgs(){
        return images;
    }

    public CityObject(String name, String description, Coordinates coordinates, float persons_voted
    , float rating, ArrayList<Integer> images) {
        this.name = name;
        this.description = description;
        this.coordinates = coordinates;
        this.persons_voted = persons_voted;
        this.rating = rating;
        this.imgs = images;
    }

   /* public CityObject(String name, int image, String description, Coordinates location){
        this.name = name;
        this.image = image;
        this.description = description;
        this.location = location;

    }*/

   public void addImages(ArrayList<String> images){
       this.images = images;
   }

    public void setDistance(float distance){

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

    public void setLengthBetween(double lng, double lat) {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat- coordinates.getLatitude());
        double dLng = Math.toRadians(lng- coordinates.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(coordinates.getLatitude())) *
                        Math.cos(Math.toRadians(lat)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        distance = (int)(earthRadius * c);
        if (distance<700){
            isOnline = true;
        }
        else{
            isOnline = false;
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
    public ArrayList<Integer> getImage(){
        return imgs;
    }
    public Coordinates getCoordinates(){
        return coordinates;
    }

    @Override
    public boolean equals(Object tp){
        try{
            CityObject cityObject = (CityObject)tp;
            return cityObject.getName().equals(name);
        }
        catch (Exception ex){
            return false;
        }
    }
    /**
     * Mongodb will automatically generate ObjectId
     * @author fhp
     *
     */
    public class Oid{
        String $oid;
        public String get$oid() {
            return $oid;
        }

        public void set$oid(String $oid) {
            this.$oid = $oid;
        }

    }

   /* public class Coordinates{
        double longitude;
        double latitude;

        public double getLongitude(){
            return this.longitude;
            }
        public void setLongitude(double longitude){
            this.longitude = longitude;
        }
        public double getLatitude(){
            return this.latitude;
        }

        public void setLatitude(double latitude){
            this.latitude = latitude;
    }
    }*/

}
