package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarioEjercicios_Fragment extends Fragment {
    private RecyclerView recyclerViewEjercicios;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private CalendarioEjercicioAdapter calendarioEjercicioAdapter;
    private List<CalendarioEjercicios> calendarioEjerciciosList;
    private CollectionReference ejerciciosCollectionRef;
    private String selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_ejercicios_, container, false);
        recyclerViewEjercicios = view.findViewById(R.id.recyclerViewSerieDatosCal);

        String ejercicioName = getArguments().getString("ejercicioName");

        TextView blockNameTextView = view.findViewById(R.id.nameDetailNameTextViewDatosCal);
        blockNameTextView.setText(ejercicioName);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        calendarioEjerciciosList = new ArrayList<>();
        calendarioEjercicioAdapter = new CalendarioEjercicioAdapter(calendarioEjerciciosList, ejerciciosCollectionRef);

        recyclerViewEjercicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEjercicios.setAdapter(calendarioEjercicioAdapter);

        // Verificar si se recibieron los datos seleccionados desde CalendarioFragment
        Bundle args = getArguments();
        selectedDate = getArguments().getString("selectedDate");
        String ejercicioName = getArguments().getString("ejercicioName");

        if (currentUser != null && ejercicioName != null && selectedDate != null) {
            // Construir la referencia a la colección de ejercicios
            ejerciciosCollectionRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(selectedDate)
                    .collection("elements")
                    .document(ejercicioName)
                    .collection("ejercicios");

            // Cargar los ejercicios desde Firestore
            loadEjerciciosFromFirestore(selectedDate);
        } else {
            // Si no hay usuario autenticado, puedes manejar el caso aquí
            Log.e("CalendarioEjercicios", "No hay usuario autenticado");
        }
        CalendarView calendarView = view.findViewById(R.id.calendarView1);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Formatear la fecha seleccionada
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth; // Ajusta el formato según tu estructura

                // Cargar el nombre del documento correspondiente a la fecha seleccionada
                loadEjerciciosFromFirestore(selectedDate);
            }
        });

        calendarioEjercicioAdapter.setOnEjercicioClickListener(new CalendarioEjercicioAdapter.OnEjercicioClickListener() {
            @Override
            public void onEjercicioClick(CalendarioEjercicios calendarioEjercicios) {
                String blockName = getArguments().getString("calendario");
                String elementName = getArguments().getString("name");
                String ejercicioName = calendarioEjercicios.getNombre();
                navigateToDatosEjerciciosFragment(blockName, elementName, ejercicioName);
            }
        });
    }

    private void navigateToDatosEjerciciosFragment(String blockName, String elementName, String ejercicioName) {
        Bundle bundle = new Bundle();
        bundle.putString("calendario", blockName);
        bundle.putString("elementName", elementName);
        bundle.putString("ejercicioName", ejercicioName);

        MostrarCalendarioDatsFragment fragment = new MostrarCalendarioDatsFragment();
        fragment.setArguments(bundle);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_calendarioEjercicios_Fragment_to_mostrarCalendarioDatsFragment);
    }

    private void loadEjerciciosFromFirestore(String selectedDate) {
        String ejercicioName = getArguments().getString("ejercicioName");

        if (currentUser != null) {
            // Dividir la fecha seleccionada en año, mes y día
            String[] parts = selectedDate.split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];

            if (currentUser != null) {
                // Construir la referencia a la colección de ejercicios dentro de la estructura de la fecha seleccionada
                CollectionReference ejerciciosCollectionREf = firestore.collection("users")
                        .document(currentUser.getUid())
                        .collection("calendario")
                        .document(day).collection(month).document(year)
                        .collection("elements")
                        .document(ejercicioName)
                        .collection("ejercicios");

                // Obtener los ejercicios de Firestore
                ejerciciosCollectionREf.get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            calendarioEjerciciosList.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                CalendarioEjercicios ejercicio = document.toObject(CalendarioEjercicios.class);
                                calendarioEjerciciosList.add(ejercicio);
                            }
                            calendarioEjercicioAdapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error al cargar los ejercicios desde Firestore: " + e.getMessage());
                        });
            } else {
                // Manejar el caso en que el usuario sea nulo
                Log.e("Firestore", "El usuario es nulo");
                // También puedes mostrar un mensaje de error al usuario si lo deseas
            }
        }
    }
}