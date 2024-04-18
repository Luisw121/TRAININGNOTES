package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.trainingnotes.views.DatoAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatosEjerciciosFragment extends Fragment {

    private List<DatoInicial> serieDatosList = new ArrayList<>();
    private RecyclerView recyclerViewSerieDatos;
    private DatoAdapter serieDatosAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datos_ejercicios, container, false);


        //TextView blockNameTextView = view.findViewById(R.id.nameDetailNameTextViewDatos);
        //TextView elementNameTextView = view.findViewById(R.id.elementNameTextViewDatos);
        TextView ejercicioNameTextView = view.findViewById(R.id.nameDetailNameTextViewDatos);

        //Secundarios
        String blockName = getArguments().getString("blockName"); // Pilla los bloques
        String elementName = getArguments().getString("elementName"); // Pilla los dias

        //Este es el importante
        String ejercicioName = getArguments().getString("ejercicioName"); // Pilla los ejercicios

        ejercicioNameTextView.setText(ejercicioName);

        recyclerViewSerieDatos = view.findViewById(R.id.recyclerViewSerieDatos);

        serieDatosList = new ArrayList<>();
        agregarSerieInicial();

        serieDatosAdapter = new DatoAdapter(serieDatosList);
        recyclerViewSerieDatos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSerieDatos.setAdapter(serieDatosAdapter);

        //elementNameTextView.setText(elementName);
        //ejercicioNameTextView.setText(ejercicioName);

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatosEnFirebase();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }


    private void eliminarSerieSeleccionada() {
        // Obtener la posición de la serie seleccionada (si la hay)
        int posicionSeleccionada = serieDatosAdapter.getPosicionSeleccionada();
        if (posicionSeleccionada != RecyclerView.NO_POSITION) {
            // Eliminar la serie de la lista
            serieDatosList.remove(posicionSeleccionada);
            // Notificar al adaptador sobre el cambio en los datos
            serieDatosAdapter.notifyDataSetChanged();
            // Opcional: Guardar los cambios en Firestore
            // guardarDatosEnFirestore();
        }
    }

    private void agregarSerieInicial() {
        // Crear una nueva instancia de SerieDatos con valores predeterminados
        DatoInicial nuevaSerie = new DatoInicial(0, 0.0, 0.0);
        // Agregar la nueva serie a la lista
        serieDatosList.add(nuevaSerie);
    }

    private void guardarDatosEnFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String ejercicioName = getArguments().getString("ejercicioName");
        String blockName = getArguments().getString("blockName");
        String elementName = getArguments().getString("elementName");

        DocumentReference ejercicioDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("blocks")
                .document(blockName)
                .collection("elements")
                .document(elementName)
                .collection("ejercicios")
                .document(ejercicioName);

        Map<String, Object> datosEjercicio = new HashMap<>();
        List<Map<String, Object>> serieDatosMapList = new ArrayList<>();
        for (DatoInicial serieDatos : serieDatosList) {
            Map<String, Object> serieDatosMap = new HashMap<>();
            serieDatosMap.put("repeticiones", serieDatos.getRepeticiones());
            serieDatosMap.put("peso", serieDatos.getPeso());
            serieDatosMap.put("rpe", serieDatos.getRpe());
            serieDatosMapList.add(serieDatosMap);
        }
        datosEjercicio.put("series", serieDatosMapList);

        ejercicioDocRef.set(datosEjercicio)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Datos de series guardados en el documento de ejercicios en Firestore");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error al guardar datos de series en el documento de ejercicios en Firestore: " + e.getMessage());
                });
    }

    private void agregarDatosSerie(int repeticiones, double peso, double rpe) {
        DatoInicial serieDatos = new DatoInicial();
        serieDatos.setRepeticiones(repeticiones);
        serieDatos.setPeso(peso);
        serieDatos.setRpe(rpe);
        serieDatosList.add(serieDatos);
    }

}