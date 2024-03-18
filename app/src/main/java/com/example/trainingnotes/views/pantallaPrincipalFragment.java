package com.example.trainingnotes.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.trainingnotes.MainActivity;
import com.example.trainingnotes.R;
import com.example.trainingnotes.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class pantallaPrincipalFragment extends Fragment {

    private NavController navController;
    //TextView displayNameTextVIew;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pantalla_principal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        ImageView perfil = view.findViewById(R.id.perfil);
        //displayNameTextVIew = view.findViewById(R.id.nombreperfil);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            //displayNameTextVIew.setText(user.getDisplayName());
            Glide.with(requireActivity()).load(user.getPhotoUrl())
                    .circleCrop()
                    .into(perfil);
        }
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_pantallaPrincipalFragment_to_perfilFragment);
            }
        });
        ConstraintLayout entrenamiento = view.findViewById(R.id.buttonEntrenamiento);
        entrenamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_pantallaPrincipalFragment_to_entrenamientoFragment);
            }
        });
    }
}