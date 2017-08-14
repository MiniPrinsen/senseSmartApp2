package com.example.gustaf.touchpoint.HelpClasses;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * CityObject is what it says, a whole City Object. When we get all the city objects from the server
 * , we store each one of them into a CityObject. This holds everything relevant for a city object.
 */
public class CityObject implements Parcelable {

    private                     String name;
    private String              description;
    private Coordinates         coordinates;
    private Oid                         _id;
    private int                    distance;
    private static final int    RADIUS = 50;
    private ArrayList<String>        images;
    private Coordinates     currentLocation;
    private ArrayList<Integer>         imgs;
    private boolean        isOnline = false;

    public  CityObject() {
        imgs = new ArrayList<>();
        coordinates = new Coordinates();
        images = new ArrayList<>();
    }

    public void setCurrentLocation(Coordinates cord){
        this.currentLocation = cord;
    }

    private Coordinates getCurrentLocation(){
        return currentLocation;
    }

    public ArrayList<String> getImgs(){
        return images;
    }

    /**
     * As we mentioned above, this is all the relevant information we need for a city object.
     * @param name title of the city object
     * @param description descriptive text of the city object. Will be shown in the details.
     * @param coordinates coordinates of the city object. This includes latitude and longitude.
     * @param persons_voted Number of persons which have voted on the city object.
     * @param rating Rating of the city object.
     * @param images An arraylist of images of the city object.
     */
    public CityObject(String name, String description, Coordinates coordinates, float persons_voted
    , float rating, ArrayList<Integer> images) {
        this.name = name;
        this.description = description;
        this.coordinates = coordinates;
        this.imgs = images;
    }

    /**
     * Since we don't want to return "43000 M", we format the text to show M, KM or MIL.
     * @return the appropreate format of the distance.
     */
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

    /**
     * Function to get the coordinates into meters. This makes it possible for us to actually show
     * the distance to the object. This and being able to set the distance which is required to
     * chat with the object. EXTREMELY IMPORTANT.
     */
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

    private CityObject (Parcel source){
      /*
       * Reconstruct from the Parcel. Keep same order as in writeToParcel()
       */
        name = source.readString();
        description = source.readString();
        images = new ArrayList<>();
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

    /**
     * Funcion to make it possible to parse a city object.
     */
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
    private class Oid{
        String $oid;
        String get$oid() {
            return $oid;
        }

        void set$oid(String $oid) {
            this.$oid = $oid;
        }

    }
}
