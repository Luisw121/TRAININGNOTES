package com.example.trainingnotes;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;
import android.Manifest;

public class mapsFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap mMap;
    private PlacesClient placesClient;
    private static final int PROXIMITY_RADIUS = 10000;

    // Lista de palabras clave para filtrar lugares
    private List<String> keywords = Arrays.asList("gimnasio", "crossfit", "fit", "gym", "gimnas", "deportivo", "esport", "deporte", "centro deportivo", "esport", "fitnes", "basic-fit", "Basic-Fit");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Inicializar la API de Places
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey);
        }
        placesClient = Places.createClient(requireContext());

        // Obtener el mapa asincrónicamente
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Habilitar la capa de ubicación del usuario y mostrar el botón de mi ubicación en el mapa
        mMap.setMyLocationEnabled(true);

        // Configurar el proveedor de ubicación del mapa
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Establecer el oyente de clics en los marcadores
        mMap.setOnMarkerClickListener(marker -> {
            // Aquí puedes agregar la lógica para manejar el clic en un marcador si lo deseas
            return false;
        });
        // Crear una solicitud para encontrar lugares actuales con los campos de nombre y latitud-longitud
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        // Realizar la solicitud para encontrar los lugares actuales
        placesClient.findCurrentPlace(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindCurrentPlaceResponse response = task.getResult();
                if (response != null) {
                    // Iterar sobre los lugares encontrados
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Place place = placeLikelihood.getPlace();
                        String name = place.getName();

                        // Verificar si el nombre del lugar contiene alguna palabra clave
                        if (name != null && containsKeyword(name.toLowerCase())) {
                            LatLng location = place.getLatLng();

                            // Agregar marcadores en el mapa para cada lugar encontrado
                            if (location != null) {
                                mMap.addMarker(new MarkerOptions().position(location).title(name));
                            }
                        }
                    }

                    // Si se encuentran lugares, mover la cámara al primer lugar encontrado
                    if (!response.getPlaceLikelihoods().isEmpty()) {
                        Place place = response.getPlaceLikelihoods().get(0).getPlace();
                        LatLng location = place.getLatLng();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f));
                    }
                }
            } else {
                // Manejar el caso de que ocurra un error al realizar la solicitud
                Exception exception = task.getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });
    }

    // Método para verificar si el nombre del lugar contiene alguna palabra clave
    private boolean containsKeyword(String name) {
        for (String keyword : keywords) {
            if (name.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}



/*
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;

public class mapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    EditText txtLatitud, txtLongitud;
    GoogleMap mMap;
    private PlacesClient placesClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        txtLatitud = view.findViewById(R.id.txtLatitud);
        txtLongitud = view.findViewById(R.id.txtLongitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);

        LatLng barcelona = new LatLng(41.38879, 2.15899);
        mMap.addMarker(new MarkerOptions().position(barcelona).title("Barcelona"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona));
    }
    //para darle click en el mapa pueda generarse
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText(""+latLng.latitude);
        txtLongitud.setText(""+latLng.longitude);

        mMap.clear();
        LatLng barcelona = new LatLng(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(barcelona).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona));
    }
    //para cuando presionemos en el mapa peuda generar el evento
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText(""+latLng.latitude);
        txtLongitud.setText(""+latLng.longitude);

        mMap.clear();
        LatLng barcelona = new LatLng(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(barcelona).title(""));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona));
    }
}
 */