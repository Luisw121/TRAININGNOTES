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

        if (currentUser != null) {
            // Obtener el nombre del ejercicio desde los argumentos
            String ejercicioName = getArguments().getString("ejercicioName");
            String selectedDate = getArguments().getString("selectedDate");

            // Construir la referencia a la colección de ejercicios dentro de la estructura de la fecha seleccionada
            DocumentReference ejerciciosDocumentRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(selectedDate)
                    .collection("elements")
                    .document(ejercicioName);

            // Obtener la referencia a la subcolección "ejercicios" dentro del documento de ejercicios
            ejerciciosCollectionRef = ejerciciosDocumentRef.collection("ejercicios");

            // Cargar los ejercicios desde Firestore
            loadEjerciciosFromFirestore();
        }

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

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private void loadEjerciciosFromFirestore() {
        String ejercicioName = getArguments().getString("ejercicioName");
        String selectedDate = getArguments().getString("selectedDate");
        if (currentUser != null) {
            // Construir la referencia a la colección de ejercicios dentro de la estructura de la fecha seleccionada
            DocumentReference ejerciciosCollectionRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(selectedDate)
                    .collection("elements")
                    .document(ejercicioName);

            CollectionReference ejerciciosCollectionREf = ejerciciosCollectionRef.collection("ejercicios");

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


/*
if (currentUser != null) {
            Bundle args = getArguments();
            if (args != null && args.containsKey("name")) {
                String elementName = args.getString("name");
                if (elementName != null) {
                    collectionReference = firestore.collection("users")
                            .document(currentUser.getUid())
                            .collection("calendario")
                            .document(elementName)
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
 */
/*
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
 */