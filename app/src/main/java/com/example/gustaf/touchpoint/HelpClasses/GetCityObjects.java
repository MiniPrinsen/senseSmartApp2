package com.example.gustaf.touchpoint.HelpClasses;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.ArrayList;

/**
 * This is the java class which talks to the server.
 * We call this class when we start BaseActivity to load all the relevant city objects.
 * We send a POST request to the server and gets a response.
 */
public class GetCityObjects implements Runnable {
    private static final String             HOST = "http://35.158.191.6:8080/sensesmart/hey";
    private double                          longitude;
    private double                          latitude;
    private int                             distance;
    private Handler                         handler;

    public GetCityObjects(double lng, double lat, int dist, Handler handler){
        this.longitude = lng;
        this.latitude = lat;
        this.distance = dist;
        this.handler = handler;
    }

    /**
     * Thread to talk to the server. We add our coordinates and distance with the URL to
     * set some restrictions of what to get. For example, if we are in sweden it is worthless for us
     * to get a touch point in Australia. This is what those parameters restrains.
     */
    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        ArrayList<CityObject> temp = new ArrayList<>();
        try {
            String urlParameters = "longitude=" + longitude + "&latitude=" +
                    latitude + "&distance=" + distance;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            URL url = new URL(HOST);
            urlConnection = (HttpURLConnection) url.openConnection();


            urlConnection.setRequestMethod("POST");
            urlConnection.setChunkedStreamingMode(0);

            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");


            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

            out.write(postData);
            out.flush();

            // Retrieve the server response after we send the POST request.
            // As we can see, we store each city object in a CityObject instance.
            String json = getStringFromInputStream(urlConnection.getInputStream());
            JSONArray array = new JSONArray(json);
            Gson gson = new Gson();
            for (int i = 0; i<array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                CityObject cObject = gson.fromJson(obj.toString(), CityObject.class);
                cObject.setCurrentLocation(new Coordinates(latitude, longitude));
                cObject.setLengthBetween();
                temp.add(cObject);
            }

        }
        catch(IOException e) {
            Log.v("ERROR", "IO - "+e.getMessage());
            Message msg = handler.obtainMessage();
            msg.obj = e;
            handler.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            Message msg = handler.obtainMessage();
            if (temp.size() != 0) {
                msg.obj = temp;
                handler.sendMessage(msg);
            }
            //Closes the connection
            urlConnection.disconnect();

        }
    }



    /**
     *
     * Creates a string from inputstream
     *
     */
    private String getStringFromInputStream(InputStream inputstream) {

        int BUFFER_SIZE = 8192;

        BufferedReader br;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            InputStreamReader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);

             br = new BufferedReader(reader, BUFFER_SIZE);

            while ((line = br.readLine()) != null) {
                Log.v("ENCODE", line);
                sb.append(line);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        Log.v("ENCODE", "ÅÄÖ");

        return sb.toString();

    }


}
