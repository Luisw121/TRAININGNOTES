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
                loadEjerciciosFromFirestore(selectedDate);
            }
        });

        return view;
    }

    // Método para cargar el nombre del documento desde Firestore
    private void loadDocumentNameFromFirestore(String selectedDate) {
        if (currentUser != null) {
            // Dividir la fecha seleccionada en año, mes y día
            String[] parts = selectedDate.split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];

            // Construir la referencia al documento en la colección "calendario" correspondiente a la fecha seleccionada
            DocumentReference userDocRef = firestore.collection("users")
                    .document(currentUser.getUid());

            CollectionReference dayDocumentRef = userDocRef.collection("calendario")
                    .document(day)
                    .collection(month)
                    .document(year)
                    .collection("elements");


            // Obtener todos los documentos dentro de la colección "elements"
            dayDocumentRef.get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // Iterar sobre los documentos y obtener los nombres
                            StringBuilder documentNames = new StringBuilder();
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                String documentName = documentSnapshot.getId();
                                documentNames.append(documentName).append(", ");
                            }
                            // Eliminar la última coma y espacio del StringBuilder
                            String documentNamesString = documentNames.toString();
                            if (documentNamesString.length() > 2) {
                                documentNamesString = documentNamesString.substring(0, documentNamesString.length() - 2);
                            }
                            // Mostrar los nombres de los documentos en textViewFechaSeleccionada
                            textViewFechaSeleccionada.setText(documentNamesString);
                        } else {
                            textViewFechaSeleccionada.setText("No hay ejercicios para esta fecha");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CalendarioFragment", "Error al obtener los nombres de los documentos desde Firestore: " + e.getMessage());
                    });
        }
    }



    // Método para cargar los ejercicios desde Firestore
    // Método para cargar los ejercicios desde Firestore
    private void loadEjerciciosFromFirestore(String selectedDate) {
        if (currentUser != null) {
            // Dividir la fecha seleccionada en año, mes y día
            String[] parts = selectedDate.split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];

            // Construir la referencia al documento en la colección "calendario" correspondiente a la fecha seleccionada
            DocumentReference userDocRef = firestore.collection("users")
                    .document(currentUser.getUid());

            DocumentReference dayDocumentRef = userDocRef.collection("calendario")
                    .document(day)
                    .collection(month)
                    .document(year);

            // Obtener la colección "elements" dentro del documento
            CollectionReference elementsCollectionRef = dayDocumentRef.collection("elements");

            // Obtener los documentos dentro de la colección "elements"
            elementsCollectionRef.get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // Iterar sobre los documentos
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                // Obtener el nombre del documento de "elements"
                                String elementName = documentSnapshot.getId();

                                // Obtener la referencia a la colección "ejercicios" dentro de cada documento de "elements"
                                CollectionReference ejerciciosCollectionRef = documentSnapshot
                                        .getReference()
                                        .collection("ejercicios");

                                // Obtener los documentos dentro de la colección "ejercicios"
                                ejerciciosCollectionRef.get()
                                        .addOnSuccessListener(ejerciciosQuerySnapshot -> {
                                            // Iterar sobre los documentos de "ejercicios"
                                            for (QueryDocumentSnapshot ejercicioDocument : ejerciciosQuerySnapshot) {
                                                // Obtener el nombre del ejercicio
                                                String ejercicioName = ejercicioDocument.getString("nombre");

                                                // Aquí puedes hacer lo que quieras con el nombre del ejercicio
                                                // Por ahora, lo mostraré en el TextView
                                                textViewFechaSeleccionada.setText(ejercicioName);
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("CalendarioFragment", "Error al obtener los ejercicios desde Firestore: " + e.getMessage());
                                        });
                            }
                        } else {
                            textViewFechaSeleccionada.setText("No hay ejercicios para esta fecha");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CalendarioFragment", "Error al obtener los nombres de los documentos desde Firestore: " + e.getMessage());
                    });
        }
    }


}



