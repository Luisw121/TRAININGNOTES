package com.example.trainingnotes;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NearbySearchTask extends AsyncTask<String, Void, String> {
    GoogleMap mMap;
    @Override
    protected String doInBackground(String... urls) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject placeObject = resultsArray.getJSONObject(i);
                String placeName = placeObject.getString("name");
                JSONObject locationObject = placeObject.getJSONObject("geometry").getJSONObject("location");
                double lat = locationObject.getDouble("lat");
                double lng = locationObject.getDouble("lng");

                LatLng placeLatLng = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
