package com.example.gustaf.touchpoint.HelpClasses;

import android.os.Message;
import android.util.Log;

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
import java.util.ArrayList;

import android.os.Handler;

/**
 * Created by gustafwennerstrom on 2017-07-05.
 */

public class GetCityObjects implements Runnable {
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
        try {
            Log.v("LOGGAR", "innanadsgasgaga....");
            String urlParameters = "longitude=" + longitude + "&latitude=" +
                    latitude + "&distance=" + distance;
            Log.v("CITYOBJ", urlParameters);
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            URL url = new URL(" http://35.158.191.6:8080/sensesmart/hey");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            ArrayList<CityObject> temp = new ArrayList<>();
            Log.v("LOGGAR", "innan 2......");

            try {

                urlConnection.setRequestMethod("POST");
                urlConnection.setChunkedStreamingMode(0);

                Log.v("LOGGAR", urlConnection.getURL().toString());

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                Log.v("LOGGAR", "innan 3......");

                out.write(postData);
                out.flush();


                int status = (urlConnection).getResponseCode();
                Log.v("LOGGAR", "status"+status);

                String json = getStringFromInputStream(urlConnection.getInputStream());
                JSONArray array = new JSONArray(json);
                Gson gson = new Gson();
                for (int i = 0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    CityObject cObject = gson.fromJson(obj.toString(), CityObject.class);
                    Log.v("LOGGAR", cObject.getName());
                    Log.v("LOGGAR", cObject.getDescription());
                    Log.d("LOGGAR", (String.valueOf(cObject.coordinates.getLatitude())));
                    Log.d("LOGGAR", (String.valueOf(cObject.coordinates.getLongitude())));
                    temp.add(cObject);
                    for(String img : cObject.getImgs()){
                        Log.d("LOGGAR", img);
                    }

                }

            } finally {
                Message msg = handler.obtainMessage();
                if (temp.size() != 0) {
                    Log.v("CITYOBJ", temp.toString());
                    msg.obj = temp;
                    handler.sendMessage(msg);
                }
                urlConnection.disconnect();

            }
        } catch (Exception e) {
            Log.v("LOGGAR", "ERROR"+e);
            e.printStackTrace();
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


}
