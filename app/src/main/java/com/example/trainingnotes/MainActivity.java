package com.example.trainingnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.trainingnotes.databinding.ActivityMainBinding;
import com.example.trainingnotes.views.signInFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.Manifest;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    NavController navController;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Configurar el listener para la BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setVisibility(View.GONE);
        navController.addOnDestinationChangedListener((((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.pantallaPrincipalFragment ||
                    navDestination.getId() == R.id.entrenamientoFragment ||
                    navDestination.getId() == R.id.calendarioFragment ||
                    navDestination.getId() == R.id.mapsFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
            }

            // Obtener el ID del destino actual
            int destinationId = navDestination.getId();

            // Actualizar el elemento seleccionado en la BottomNavigationView
            actualizarElementoSeleccionado(destinationId);
        })));
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Obtener el ID del elemento del menú seleccionado
                int itemId = item.getItemId();

                // Comparar el ID del elemento del menú seleccionado con los IDs específicos
                if (itemId == R.id.Home) {
                    navController.navigate(R.id.pantallaPrincipalFragment);
                    return true;
                } else if (itemId == R.id.entrenamiento) {
                    navController.navigate(R.id.entrenamientoFragment);
                    return true;
                } else if (itemId == R.id.calendar) {
                    navController.navigate(R.id.calendarioFragment);
                    return true;
                } else if (itemId == R.id.ubication) {
                    navController.navigate(R.id.mapsFragment);
                    return true;
                }
                return false;
            }

        });
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Si el usuario ha iniciado sesión, navega a pantallaPrincipalFragment
            navController.navigate(R.id.pantallaPrincipalFragment);
        } else {
            // Si el usuario no ha iniciado sesión, navega a signInFragment
            navController.navigate(R.id.signInFragment);
        }
    }
    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        int currentDestination = navController.getCurrentDestination().getId();

        // Obtener el usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Verificar si el usuario ha iniciado sesión y está en la pantalla principal
        if (currentUser != null && currentDestination == R.id.pantallaPrincipalFragment) {
            // Si el usuario ha iniciado sesión y está en la pantalla principal, cierra la aplicación
            finish();
        } else {
            // Si no, realiza el comportamiento de retroceso normal
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
    public void actualizarElementoSeleccionado(int itemId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(itemId);
    }

}
/*
private void checkLoginStatus() {
        if (AuthManager.getInstance().isUserLoggedIn()) {
            // Usuario autenticado, no necesitamos hacer nada más
        } else {
            // Usuario no autenticado, navegar al fragmento de inicio de sesión
            navController.navigate(R.id.signInFragment);
        }
    }
 */