package com.example.gustaf.touchpoint.HelpClasses;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.Excluder;

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
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by gustafwennerstrom on 2017-07-05.
 */

public class GetCityObjects implements Runnable {
    private final String HOST = "http://35.158.191.6:8080/sensesmart/hey";
    private double longitude;
    private double latitude;
    private int distance;
    private Handler handler;

    public GetCityObjects(double lng, double lat, int dist, Handler handler){
        this.longitude = lng;
        this.latitude = lat;
        this.distance = dist;
        this.handler = handler;
    }

    public void startThread(){

    }

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


            int status = (urlConnection).getResponseCode();

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
            Log.v("ERRORJA", "IO - "+e.getMessage());
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
            urlConnection.disconnect();

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
