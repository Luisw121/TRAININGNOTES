package com.example.trainingnotes.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.trainingnotes.MainActivity;
import com.example.trainingnotes.R;
import com.example.trainingnotes.viewmodel.AuthViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class pantallaPrincipalFragment extends Fragment {

    private NavController navController;
    private TextView pesoTextView;
    private static final String TAG = "signInFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantalla_principal, container, false);

        pesoTextView = requireActivity().findViewById(R.id.peso);

        return view;
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

                ((MainActivity) requireActivity()).actualizarElementoSeleccionado(R.id.entrenamiento);
            }
        });
        ConstraintLayout buttonListaEjercicios = view.findViewById(R.id.buttonListaEjercicios);
        buttonListaEjercicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_pantallaPrincipalFragment_to_listaEjerciciosFragment);

            }
        });

        ConstraintLayout pesoCorporalLayout = view.findViewById(R.id.pesoCorporalBottom);

        pesoCorporalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorDePeso();
            }
        });
    }

    private void mostrarSelectorDePeso() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cambiar Peso");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        // Configurar botón de "Guardar"
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pesoIngresado = input.getText().toString().trim();
                // Verificar si se ingresó un peso
                if (!pesoIngresado.isEmpty()) {
                    // Guardar el peso en Firebase
                    guardarPesoSeleccionadoEnFirebase(pesoIngresado);
                } else {
                    Toast.makeText(requireContext(), "Por favor, ingresa tu peso", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar botón de "Cancelar"
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar el cuadro de diálogo
        builder.show();
    }

    private void guardarPesoSeleccionadoEnFirebase(String pesoSeleccionado) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uidUsuario = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(uidUsuario);

            userRef.update("peso", pesoSeleccionado)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(requireContext(), "Peso guardado correctamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al guardar el peso en Firestore: " + e.getMessage());
                        }
                    });
        }
    }
}