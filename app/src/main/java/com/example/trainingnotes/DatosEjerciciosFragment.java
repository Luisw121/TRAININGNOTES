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
import android.widget.ImageView;
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
import java.util.UUID;

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

        serieDatosAdapter = new DatoAdapter(serieDatosList, new DatoAdapter.OnSerieClickListener() {
            @Override
            public void onSerieDeleteClick(int position) {
                eliminarSerieSeleccionada(position);
            }

            @Override
            public void onSerieAddClick() {
                agregarSerieInicial();
            }
        });
        recyclerViewSerieDatos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSerieDatos.setAdapter(serieDatosAdapter);

        //elementNameTextView.setText(elementName);
        //ejercicioNameTextView.setText(ejercicioName);


        ImageView addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarSerieInicial();
            }
        });
        
        ImageView deleteButton = view.findViewById(R.id.deleteButton1);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUltimaSerie(); 
            }
        });
        
        loadDatosFromFirebase(); 
        return view;
    }

    private void eliminarUltimaSerie() {
        // Verificar si hay elementos en la lista antes de intentar eliminar
        if (!serieDatosList.isEmpty()) {
            // Obtener la posición del último elemento en la lista
            int position = serieDatosList.size() - 1;
            // Eliminar el elemento en esa posición
            serieDatosList.remove(position);
            // Notificar al adaptador del cambio
            serieDatosAdapter.notifyItemRemoved(position);
            datosCambiados = true;
        }
    }
    private boolean datosCambiados = false;
    private void loadDatosFromFirebase() {
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

        ejercicioDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> datosEjercicio = documentSnapshot.getData();
                List<Map<String, Object>> serieDatosMapList = (List<Map<String, Object>>) datosEjercicio.get("series");
                if (serieDatosMapList != null) {
                    // Limpiar la lista local antes de agregar las series de Firebase
                    serieDatosList.clear();
                    for (Map<String, Object> serieDatosMap : serieDatosMapList) {
                        Long repeticionesLong = (Long) serieDatosMap.get("repeticiones");
                        int repeticiones = repeticionesLong.intValue();

                        double peso = (double) serieDatosMap.get("peso");
                        double rpe = (double) serieDatosMap.get("rpe");
                        DatoInicial serieDatos = new DatoInicial(repeticiones, peso, rpe);
                        serieDatosList.add(serieDatos);
                    }
                    // Notificar al adaptador del cambio
                    serieDatosAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(e -> {
            // Manejar el error al obtener datos de Firebase
        });
    }


    private void agregarSerieInicial() {
        // Generar un ID único para la nueva serie
        String serieId = FirebaseFirestore.getInstance().collection("users").document().getId();

        // Agregar la nueva serie a la lista con el ID único
        DatoInicial nuevaSerie = new DatoInicial(0, 0.0, 5.0);
        nuevaSerie.setId(serieId); // Asumiendo que tienes un método setId en tu clase DatoInicial
        serieDatosList.add(nuevaSerie);
        serieDatosAdapter.notifyDataSetChanged();

        // Marcar los datos como modificados
        datosCambiados = true;
        guardarDatosEnFirebase();
    }


    private void eliminarSerieSeleccionada(int position) {
        // Verificar si hay elementos en la lista antes de intentar eliminar
        if (!serieDatosList.isEmpty()) {
            // Obtener la serie a eliminar
            DatoInicial serieAEliminar = serieDatosList.get(position);

            // Eliminar la serie de la lista local
            serieDatosList.remove(position);
            serieDatosAdapter.notifyDataSetChanged();

            // Actualizar la bandera
            datosCambiados = true;

            // Eliminar la serie de Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String ejercicioName = getArguments().getString("ejercicioName");
                String blockName = getArguments().getString("blockName");
                String elementName = getArguments().getString("elementName");

                DocumentReference serieDocRef = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(currentUser.getUid())
                        .collection("blocks")
                        .document(blockName)
                        .collection("elements")
                        .document(elementName)
                        .collection("ejercicios")
                        .document(ejercicioName)
                        .collection("series")
                        .document(serieAEliminar.getId());

                serieDocRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            // Éxito al eliminar la serie de Firebase
                            System.out.println("Serie eliminada de Firebase");
                        })
                        .addOnFailureListener(e -> {
                            // Error al eliminar la serie de Firebase
                            System.out.println("Error al eliminar serie de Firebase: " + e.getMessage());
                        });
            }
        }
    }


    private void guardarDatosEnFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String ejercicioName = getArguments().getString("ejercicioName");
        String blockName = getArguments().getString("blockName");
        String elementName = getArguments().getString("elementName");

        // Referencia al documento de ejercicios en Firestore
        DocumentReference ejercicioDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("blocks")
                .document(blockName)
                .collection("elements")
                .document(elementName)
                .collection("ejercicios")
                .document(ejercicioName);
        if (!datosCambiados) {
            // Si no hay cambios, salir del método sin guardar
            return;
        }

        // Obtener los datos existentes del documento de ejercicios en Firestore
        ejercicioDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> datosEjercicio = documentSnapshot.getData();
                List<Map<String, Object>> serieDatosMapList = new ArrayList<>();
                // Obtener los datos de series existentes
                List<Map<String, Object>> existingSerieDatosList = (List<Map<String, Object>>) datosEjercicio.get("series");

                // Agregar los datos existentes a la lista de mapas
                if (existingSerieDatosList != null) {
                    serieDatosMapList.addAll(existingSerieDatosList);
                }

                // Agregar los nuevos datos de series a la lista de mapas
                for (DatoInicial serieDatos : serieDatosList) {
                    Map<String, Object> serieDatosMap = new HashMap<>();
                    serieDatosMap.put("repeticiones", serieDatos.getRepeticiones());
                    serieDatosMap.put("peso", serieDatos.getPeso());
                    serieDatosMap.put("rpe", serieDatos.getRpe());
                    serieDatosMapList.add(serieDatosMap);
                }

                // Actualizar los datos de series en el documento de ejercicios en Firestore
                datosEjercicio.put("series", serieDatosMapList);
                ejercicioDocRef.set(datosEjercicio)
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("Datos de series actualizados en el documento de ejercicios en Firestore");
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error al actualizar datos de series en el documento de ejercicios en Firestore: " + e.getMessage());
                        });
            }
        }).addOnFailureListener(e -> {
            System.out.println("Error al obtener los datos existentes del documento de ejercicios en Firestore: " + e.getMessage());
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