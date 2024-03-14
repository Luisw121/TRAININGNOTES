package com.example.trainingnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.trainingnotes.databinding.ActivityMainBinding;
import com.example.trainingnotes.views.signInFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Configurar el listener para la BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setVisibility(View.GONE);
        navController.addOnDestinationChangedListener((((navController1, navDestination, bundle) -> {
            if(navDestination.getId() == R.id.pantallaPrincipalFragment ||
            navDestination.getId() == R.id.entrenamientoFragment ||
            navDestination.getId() == R.id.calendarioFragment ||
            navDestination.getId() == R.id.mapsFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }else {
                bottomNavigationView.setVisibility(View.GONE);
            }
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}