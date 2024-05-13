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
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class MostrarCalendarioDatsFragment extends Fragment {
    private String selectedDate;
    private CollectionReference ejerciciosCollectionRef;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private List<MostrarCalendarioDats> seriesDatosList = new ArrayList<>();
    private RecyclerView recyclerViewSerieDatos;
    private MostratCalendarioDatsAdapter serieDatosAdapter;
    private static final String TAG = "MostrarCalendarioDatsFragment";
    private String ejercicioName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mostrar_calendario_dats, container, false);

        String blockName = getArguments().getString("blockName"); // Utiliza el mismo nombre aquí, nombre de la colection ejercicios
        String ejercicioName = getArguments().getString("ejercicioName");//nombre de la colection elements

        String elementName = getArguments().getString("elementName");

        TextView ejercicioNameTextView = view.findViewById(R.id.nameDetailNameTextViewDatos2);
        ejercicioNameTextView.setText(blockName);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        Bundle args = getArguments();
        selectedDate = getArguments().getString("selectedDate");
        String ejercicioName = args.getString("ejercicioName");
        String blockName = getArguments().getString("blockName");
        if (currentUser != null) {
            // Construir la referencia a la colección de ejercicios
             ejerciciosCollectionRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(selectedDate)
                    .collection("elements")
                    .document(ejercicioName)
                     .collection("ejercicios");

            // Cargar los ejercicios desde Firestore
            loadDatosFromFirebase(selectedDate);
        } else {
            // Si no hay usuario autenticado, puedes manejar el caso aquí
            Log.e("CalendarioEjercicios", "No hay usuario autenticado");
        }
        recyclerViewSerieDatos = view.findViewById(R.id.recyclerViewSerieDatos2);
        recyclerViewSerieDatos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSerieDatos.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        CalendarView calendarView = view.findViewById(R.id.calendarView2);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Formatear la fecha seleccionada
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth; // Ajusta el formato según tu estructura

                // Cargar el nombre del documento correspondiente a la fecha seleccionada
                //loadDatosFromFirebase(selectedDate);
            }
        });
        serieDatosAdapter = new MostratCalendarioDatsAdapter(seriesDatosList, new MostratCalendarioDatsAdapter.OnserieClickListener() {
            @Override
            public void onSerieDeleteClick(int position) {

            }

            @Override
            public void onSerieAddClick() {

            }
        });
        recyclerViewSerieDatos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSerieDatos.setAdapter(serieDatosAdapter);

    }

    private void loadDatosFromFirebase(String selectedDate) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String[] parts = selectedDate.split("-");
        String year = parts[0];
        String month = parts[1];
        String day = parts[2];

        String ejercicioName = getArguments().getString("ejercicioName");
        String blockName = getArguments().getString("blockName");

        DocumentReference ejercicioDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("calendario")
                .document(day).collection(month).document(year)
                .collection("elements")
                .document(ejercicioName)
                .collection("ejercicios")
                .document(blockName);

        ejercicioDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> datosEjercicio = documentSnapshot.getData();
                List<Map<String, Object>> serieDatosMapList = (List<Map<String, Object>>) datosEjercicio.get("series");
                if (serieDatosMapList != null) {
                    // Limpiar la lista local antes de agregar las series de Firebase
                    seriesDatosList.clear(); // Limpiar la lista antes de agregar las nuevas series
                    for (Map<String, Object> serieDatosMap : serieDatosMapList) {
                        Long repeticionesLong = (Long) serieDatosMap.get("repeticiones");
                        int repeticiones = repeticionesLong != null ? repeticionesLong.intValue() : 0;

                        double pesoDouble = (double) serieDatosMap.get("peso");
                        float peso = (float) pesoDouble;

                        Long rpeLong = (Long) serieDatosMap.get("rpe");
                        int rpe = rpeLong != null ? rpeLong.intValue() : 0;

                        MostrarCalendarioDats serieDatos = new MostrarCalendarioDats(repeticiones, peso, rpe);
                        seriesDatosList.add(serieDatos);
                    }
                    // Notificar al adaptador del cambio
                    serieDatosAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(e -> {
            // Manejar el error al obtener datos de Firebase
        });
    }
}
/*

 */