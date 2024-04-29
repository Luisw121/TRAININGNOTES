package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private RecyclerView recyclerViewCalEj;
    private CalendarioEjercicioAdapter ejercicioAdapter;
    private FirebaseFirestore firestore;
    private List<CalendarioEjercicios> calendarioEjerciciosList;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private CollectionReference collectionReference;
    private String selectedDate;
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
        if (args != null && args.containsKey("selectedDate")) {
            selectedDate = args.getString("selectedDate");
        }
        recyclerViewCalEj = view.findViewById(R.id.recyclerViewEjercicios);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Bundle args = getArguments();
            if (args != null && args.containsKey("name")) {
                String elementName = args.getString("name");
                if (elementName != null) {
                    collectionReference = firestore.collection("users")
                            .document(currentUser.getUid())
                            .collection("elements")
                            .document(elementName)
                            .collection("ejercicios");

                    // Obtén la fecha seleccionada
                    String selectedDate = args.getString("selectedDate");

                    // Carga los datos del RecyclerView usando la fecha seleccionada
                    cargarDatosDelRecyclerView(selectedDate);
                }
            }
        }
    }


    private void cargarDatosDelRecyclerView(String selectedDate) {
        String elementName = getArguments().getString("name");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String[] parts = selectedDate.split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];
            // Obtener la referencia de Firestore
            CollectionReference ejerciciosRef = firestore
                    .collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(day)
                    .collection(month)
                    .document(year)
                    .collection("elements")
                    .document(elementName)
                    .collection("ejercicios");

            // Obtener y mostrar los ejercicios en el RecyclerView
            ejerciciosRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                // Limpiar la lista de ejercicios existente
                calendarioEjerciciosList.clear();

                // Agregar los nuevos ejercicios a la lista
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    CalendarioEjercicios ejercicio = document.toObject(CalendarioEjercicios.class);
                    calendarioEjerciciosList.add(ejercicio);
                }

                // Notificar al adaptador sobre los cambios en los datos
                ejercicioAdapter.notifyDataSetChanged();
            }).addOnFailureListener(e -> {
                // Manejar errores al obtener los ejercicios desde Firebase
                Log.e("CalendarioEjercicios_Fragment", "Error al cargar los ejercicios desde Firebase: " + e.getMessage());
            });
        }
    }




}