package com.example.gustaf.touchpoint.HelpClasses;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Gustaf on 16-08-09.
 */
public class CityObject implements Parcelable {
    private String              name;
    private String              description;
    private Coordinates         coordinates;
    private float               persons_voted;
    private float               rating;
    private Oid                 _id;
    private int                 distance;
    private final int           RADIUS =  30;
    private ArrayList<String>   images;
    private Coordinates         currentLocation;
    private ArrayList<Integer>  imgs;
    private boolean             isOnline = false;


    public  CityObject() {
        imgs = new ArrayList<>();
        coordinates = new Coordinates();
        images = new ArrayList<>();
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

    public void setLengthBetween() {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(currentLocation.getLatitude()- coordinates.getLatitude());
        double dLng = Math.toRadians(currentLocation.getLongitude()- coordinates.getLongitude());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(coordinates.getLatitude())) *
                        Math.cos(Math.toRadians(currentLocation.getLatitude())) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        distance = (int)(earthRadius * c);
        if (distance<RADIUS){
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

    @Override
    public int describeContents() {
        return 0;
    }

    public CityObject (Parcel source){
      /*
       * Reconstruct from the Parcel. Keep same order as in writeToParcel()
       */
        name = source.readString();
        description = source.readString();
        images = new ArrayList<String>();
        source.readStringList(images);
        _id = new Oid();
        _id.set$oid(source.readString());
        isOnline = source.readByte() != 0;
        coordinates = new Coordinates();
        coordinates.setLongitude(source.readDouble());
        coordinates.setLatitude(source.readDouble());
        currentLocation = new Coordinates();
        currentLocation.setLongitude(source.readDouble());
        currentLocation.setLatitude(source.readDouble());

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeStringList(images);
        parcel.writeString(_id.get$oid());
        parcel.writeByte((byte) (isOnline ? 1 : 0));
        parcel.writeDouble(getCoordinates().getLongitude());
        parcel.writeDouble(getCoordinates().getLatitude());
        parcel.writeDouble(getCurrentLocation().getLongitude());
        parcel.writeDouble(getCurrentLocation().getLatitude());
    }

    public static final Parcelable.Creator<CityObject> CREATOR
            = new Parcelable.Creator<CityObject>() {
        public CityObject createFromParcel(Parcel in) {
            return new CityObject(in);
        }

        public CityObject[] newArray(int size) {
            return new CityObject[size];
        }
    };
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

    public void setCurrentLocation(Coordinates cord){
        this.currentLocation = cord;
    }
    public Coordinates getCurrentLocation(){
        return currentLocation;
    }
    public ArrayList<String> getImgs(){
        return images;
    }

}
