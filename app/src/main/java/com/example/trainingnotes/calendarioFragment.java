package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class calendarioFragment extends Fragment {

    private RecyclerView recyclerViewEjercicios;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private EjercicioAdapter2 adapterEjercicios;
    private List<Ejercicio2> ejerciciosList;
    private CollectionReference ejerciciosCollectionRef;
    private TextView textViewFechaSeleccionada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        recyclerViewEjercicios = view.findViewById(R.id.recyclerViewEjercicios1);
        textViewFechaSeleccionada = view.findViewById(R.id.textViewFechaSeleccionada); // Agrega este TextView

        // Obtener la referencia al Firestore y al usuario actual
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Obtener la referencia a la colección de ejercicios
        if (currentUser != null) {
            ejerciciosCollectionRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario"); // Ajusta la ruta según tu estructura
        }

        // Inicializar la lista de ejercicios y el adaptador
        ejerciciosList = new ArrayList<>();
        adapterEjercicios = new EjercicioAdapter2(ejerciciosList, ejerciciosCollectionRef);

        // Configurar el RecyclerView
        recyclerViewEjercicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEjercicios.setAdapter(adapterEjercicios);

        // Configurar el evento de selección de fecha en el calendario
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Formatear la fecha seleccionada
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth; // Ajusta el formato según tu estructura

                // Cargar el nombre del documento correspondiente a la fecha seleccionada
                loadDocumentNameFromFirestore(selectedDate);
            }
        });

        return view;
    }

    // Método para cargar el nombre del documento desde Firestore
    // Método para cargar el nombre del documento desde Firestore
    private void loadDocumentNameFromFirestore(String selectedDate) {
        if (currentUser != null) {
            // Dividir la fecha seleccionada en año, mes y día
            String[] parts = selectedDate.split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];

            // Construir la referencia a la colección de ejercicios dentro de la estructura de la fecha seleccionada
            DocumentReference dayDocumentRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(day);
            DocumentReference monthDocumentRef = dayDocumentRef.collection(month).document(year);
            CollectionReference ejerciciosCollectionRef = monthDocumentRef.collection("ejercicios");

            // Obtener el nombre del documento de la colección "ejercicios"
            ejerciciosCollectionRef.get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // Suponiendo que solo hay un documento en la colección de ejercicios
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            // Obtener el nombre del documento y mostrarlo en textViewFechaSeleccionada
                            String documentName = documentSnapshot.getId();
                            textViewFechaSeleccionada.setText(documentName);
                        } else {
                            textViewFechaSeleccionada.setText("No hay ejercicios para esta fecha");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CalendarioFragment", "Error al obtener el nombre del documento desde Firestore: " + e.getMessage());
                    });
        }
    }




    // Método para cargar los ejercicios desde Firestore
    private void loadEjerciciosFromFirestore(String documentName) {
        if (currentUser != null) {
            // Construir la referencia al documento de la fecha seleccionada en Firestore
            DocumentReference fechaDocumentRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(documentName);

            // Obtener los ejercicios de Firestore
            fechaDocumentRef.collection("ejercicios")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        ejerciciosList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Obtener el nombre del documento (o cualquier otro dato necesario) y agregarlo a la lista
                            String ejercicioName = documentSnapshot.getId();
                            Ejercicio2 ejercicio = new Ejercicio2();
                            ejercicio.setNombre(ejercicioName);
                            ejerciciosList.add(ejercicio);
                        }
                        adapterEjercicios.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CalendarioFragment", "Error al cargar los ejercicios desde Firestore: " + e.getMessage());
                    });
        }
    }
}



