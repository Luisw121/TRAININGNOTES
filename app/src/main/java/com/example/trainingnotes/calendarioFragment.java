package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private String selectedDate;
    private static final String TAG = "calendarioFragment";


    // Interfaz para manejar la selección de fecha
    public interface OnDateSelectedListener {
        void onDateSelected(String selectedDate, String ejercicioName);
    }

    private OnDateSelectedListener dateSelectedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        //recyclerViewEjercicios = view.findViewById(R.id.recyclerViewEjercicios1);
        textViewFechaSeleccionada = view.findViewById(R.id.textViewFechaSeleccionada);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            ejerciciosCollectionRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario");
        }

        // Configurar el evento de selección de fecha en el calendario
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Formatear la fecha seleccionada
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth; // Ajusta el formato según tu estructura

                // Cargar el nombre del documento correspondiente a la fecha seleccionada
                loadDocumentNameFromFirestore(selectedDate);
            }
        });

        textViewFechaSeleccionada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ejercicioName = textViewFechaSeleccionada.getText().toString();
                // Llamar al método de la interfaz para manejar la selección de fecha
                if (dateSelectedListener != null) {
                    dateSelectedListener.onDateSelected(selectedDate, ejercicioName);
                }
                navigateToCalendarioEjerciciosFragment(selectedDate, ejercicioName);
            }
        });

    }
    private void deleteFirstDocumentFromCalendario(String selectedDate) {
        if (currentUser != null) {
            // Dividir la fecha seleccionada en año, mes y día
            String[] parts = selectedDate.split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];

            // Construir la referencia al documento dentro de la estructura de la fecha seleccionada
            DocumentReference documentRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(day);

            // Eliminar el documento de la colección
            documentRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Documento eliminado correctamente");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error al eliminar el documento: " + e.getMessage());
                        }
                    });
        }
    }



    private void navigateToCalendarioEjerciciosFragment(String selectedDate, String ejercicioName) {
        Bundle args = new Bundle();
        args.putString("selectedDate", selectedDate);
        args.putString("ejercicioName", ejercicioName);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_calendarioFragment_to_calendarioEjercicios_Fragment, args);
    }

    // Método para configurar el listener de selección de fecha
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

    // Método para cargar el nombre del primer documento de la colección "elements" dentro del documento de "calendario" desde Firestore
    private void loadDocumentNameFromFirestore(String selectedDate) {
        if (currentUser != null) {
            // Dividir la fecha seleccionada en año, mes y día
            String[] parts = selectedDate.split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];

            // Construir la referencia a la colección de ejercicios dentro de la estructura de la fecha seleccionada
            CollectionReference ejerciciosCollectionRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("calendario")
                    .document(day)
                    .collection(month)
                    .document(year)
                    .collection("elements");

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
}

