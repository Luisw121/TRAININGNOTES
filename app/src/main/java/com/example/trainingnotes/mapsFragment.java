package com.example.trainingnotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.widget.ImageView;

public class mapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Inicializar la API de Places
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        getLocationPermission();

        ImageView imageView1 = view.findViewById(R.id.imageViewLogo2);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.myprotein.es/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        ImageView imageView2 = view.findViewById(R.id.imageViewLogo3);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://eu.gymshark.com/?gad_source=1&gclid=CjwKCAjwx-CyBhAqEiwAeOcTdV0R-TRELht1daMzvYyxmNVM7oD7abmc4qScE04mB8Z9gAwvu35KdxoC_v0QAvD_BwE&gclsrc=aw.ds");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        return view;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));

                        String locationString = currentLocation.latitude + "," + currentLocation.longitude;
                        String apiKey = getString(R.string.google_maps_key);

                        String nearbySearchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                                locationString + "&radius=3000&type=gym&key=" + apiKey;

                        new NearbySearchTask().execute(nearbySearchUrl);
                    }
                });
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
    }

    private class NearbySearchTask extends AsyncTask<String, Void, String> {
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

                    if (isGym(placeName) && !isUnwantedPlace(placeName)) {
                        JSONObject locationObject = placeObject.getJSONObject("geometry").getJSONObject("location");
                        double lat = locationObject.getDouble("lat");
                        double lng = locationObject.getDouble("lng");

                        LatLng placeLatLng = new LatLng(lat, lng);
                        mMap.addMarker(new MarkerOptions().position(placeLatLng).title(placeName));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean isGym(String name) {
            name = name.toLowerCase();
            List<String> gymKeywords = Arrays.asList("gym", "gimnasio", "fitness", "fit", "crossfit", "deportivo", "esport", "centro deportivo", "basic-fit", "gimnasio");
            for (String keyword : gymKeywords) {
                if (name.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isUnwantedPlace(String name) {
            name = name.toLowerCase();
            List<String> unwantedKeywords = Arrays.asList("druni", "perfumerias", "bon pastor");
            for (String keyword : unwantedKeywords) {
                if (name.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
    }
}
