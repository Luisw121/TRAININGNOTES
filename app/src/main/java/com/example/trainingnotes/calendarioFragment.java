package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class calendarioFragment extends Fragment {
    NavController navController = Navigation.findNavController(requireView());

    private CalendarView calendarView;
    private String stringDateSelected;
    private FirebaseDatabase db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseDatabase.getInstance();

        calendarView = view.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                stringDateSelected = Integer.toString(year) + Integer.toString(month+1) + Integer.toString(dayOfMonth);
                mostrarEjercicios(stringDateSelected);
            }
        });
    }
    private void mostrarEjercicios(String fechaSeleccionada) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Referencia al documento del usuario en la colección "users"
            DocumentReference userDocRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.getUid());

            // Referencia al documento en la colección "calendario" del usuario actual
            DocumentReference calendarDocRef = userDocRef.collection("calendario")
                    .document(fechaSeleccionada);

            // Consultar los ejercicios almacenados en el documento
            calendarDocRef.collection("ejercicios")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<String> ejerciciosList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String ejercicioName = documentSnapshot.getString("nombre");
                            String blockName = documentSnapshot.getString("block");
                            String elementoName = documentSnapshot.getString("elemento");
                            ejerciciosList.add(ejercicioName + " - " + blockName + " - " + elementoName);
                        }
                        // Aquí puedes mostrar los ejercicios en tu RecyclerView o en cualquier otro lugar
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error al consultar los ejercicios
                        System.out.println( "Error al consultar los ejercicios en calendario: " + e.getMessage());
                    });
        }
    }
}