package com.example.trainingnotes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CalendarioEjercicios_Fragment extends Fragment {
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendario_ejercicios_, container, false);
        TextView nameDetailNameTextViewDatosCal = view.findViewById(R.id.nameDetailNameTextViewDatosCal);

        Bundle args = getArguments();
        if (args != null && args.containsKey("ejercicioName")) {
            String ejercicioName = args.getString("ejercicioName");
            nameDetailNameTextViewDatosCal.setText(ejercicioName);
        }

        return view;
    }

    private void cargarDatosDelRecyclerView() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String blockName = getArguments().getString("blockName");
            String elementName = getArguments().getString("elementName");
            String ejercicioName = getArguments().getString("ejercicioName");

            // Referencia a la colección de ejercicios en Firestore
            CollectionReference ejerciciosRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.getUid())
                    .collection("blocks")
                    .document(blockName)
                    .collection("elements")
                    .document(elementName)
                    .collection("ejercicios");

            // Configurar el RecyclerView
            RecyclerView recyclerViewSerieDatosCal = view.findViewById(R.id.recyclerViewSerieDatosCal);
            recyclerViewSerieDatosCal.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerViewSerieDatosCal.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

            // Obtener y mostrar los ejercicios en el RecyclerView
            ejerciciosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<String> nombresEjercicios = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    nombresEjercicios.add(document.getId());
                }
                // Configurar el adaptador del RecyclerView
                CalendarioEjercicioAdapter adapter = new CalendarioEjercicioAdapter(nombresEjercicios);
                recyclerViewSerieDatosCal.setAdapter(adapter);
            }).addOnFailureListener(e -> {
                // Manejar errores al obtener los ejercicios desde Firebase
                System.out.println( "Error al cargar los ejercicios desde Firebase: " + e.getMessage());
            });
        }
    }



}