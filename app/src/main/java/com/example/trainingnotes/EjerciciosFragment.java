package com.example.trainingnotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EjerciciosFragment extends Fragment {
    //private TextView blockDetailNameTextViewEjercicios;
    private RecyclerView recyclerViewEJ;
    private EjercicioAdapter ejercicioAdapter;
    private Dialog dialog;
    private List<Ejercicio> selectedEjercicios;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private CollectionReference ejerciciosCollectionRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ejercicios, container, false);
        recyclerViewEJ = view.findViewById(R.id.recyclerViewEjercicios);
        String elementName = getArguments().getString("name");

        TextView blockNameTextView = view.findViewById(R.id.blockDetailNameTextViewEjercicios);
        blockNameTextView.setText(elementName);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Utilizamos el nombre del elemento como ID de la colección en Firestore
            ejerciciosCollectionRef = firestore.collection("elements").document(currentUser.getUid()).collection("ejercicios");
            loadEjerciciosFromFirestore(currentUser.getUid(), getArguments().getString("nameEjercicio"));
        }
        selectedEjercicios = new ArrayList<>();
        ejercicioAdapter = new EjercicioAdapter(selectedEjercicios, ejerciciosCollectionRef);

        recyclerViewEJ.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEJ.setAdapter(ejercicioAdapter);
        view.findViewById(R.id.añadirEjercicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarListaEjercicios();
            }
        });

    }

    private void loadEjerciciosFromFirestore(String userId, String nameEjercicio) {
        firestore.collection("elements").document(userId).collection("ejercicios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    selectedEjercicios.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Ejercicio ejercicio = document.toObject(Ejercicio.class);
                        selectedEjercicios.add(ejercicio);
                    }
                    ejercicioAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error al cargar los ejercicios desde Firestore
                    Log.e("Firestore", "Error al cargar los ejercicios desde Firestore: " + e.getMessage());
                });
    }


    private void mostrarListaEjercicios() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Añadir ejercicio");
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_lista_ejercicios);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        List<Ejercicio> listaEjercicios = getEjercicios();

        // Crear el adaptador
        ejercicioAdapter = new EjercicioAdapter(listaEjercicios, ejerciciosCollectionRef);
        recyclerView.setAdapter(ejercicioAdapter);

        // Configurar el oyente para el clic en el ejercicio
        ejercicioAdapter.setOnExerciseClickListener(new EjercicioAdapter.OnExerciseClickListener() {
            @Override
            public void onExerciseClick(Ejercicio ejercicio) {
                selectedEjercicios.add(ejercicio);
                // Notificar al adaptador que los datos han cambiado
                ejercicioAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Guardar los ejercicios seleccionados en Firebase
                saveSelectedExercisesToFirestore();
            }
        });

        dialog.show();
    }




    private void saveSelectedExercisesToFirestore() {
        if (currentUser != null && !selectedEjercicios.isEmpty()) {
            for (Ejercicio ejercicio : selectedEjercicios) {
                ejerciciosCollectionRef.add(ejercicio)
                        .addOnSuccessListener(documentReference -> {
                            // Ejercicio agregado correctamente a Firebase
                        })
                        .addOnFailureListener(e -> {
                            // Error al agregar el ejercicio a Firebase
                            Log.e("Firestore", "Error al agregar el ejercicio a Firebase: " + e.getMessage());
                        });
            }
        }
    }
    private List<Ejercicio> getEjercicios() {
        List<Ejercicio> ejercicios = new ArrayList<>();

        ejercicios.add(new Ejercicio("Press Banca", R.drawable.pressdebanca));
        ejercicios.add(new Ejercicio("Press Inclinado", R.drawable.pressinclinado));
        ejercicios.add(new Ejercicio("Aperturas", R.drawable.aperturas));
        ejercicios.add(new Ejercicio("Fondos profundos", R.drawable.fundoscompeso));
        ejercicios.add(new Ejercicio("Extension de triceps", R.drawable.extensiondetricpes));
        ejercicios.add(new Ejercicio("Press Frances", R.drawable.pressfrances));
        ejercicios.add(new Ejercicio("Elevaciones laterales", R.drawable.elevacioneslaterales));
        ejercicios.add(new Ejercicio("Elevaciónes frontales", R.drawable.elevacionesfrontales));
        ejercicios.add(new Ejercicio("Elevaciónes posteriores", R.drawable.elevacionesposterioresconmancuernas));
        ejercicios.add(new Ejercicio("Jalon al pecho", R.drawable.jalonalpecho));
        ejercicios.add(new Ejercicio("Remo con barra", R.drawable.remoconbarradepieinitpos));
        ejercicios.add(new Ejercicio("Remo unilateral en polea", R.drawable.remohorizontalsentadoconpoleanitpos));
        ejercicios.add(new Ejercicio("Remo con mancuerna", R.drawable.remoconmancuerna));
        ejercicios.add(new Ejercicio("Pull over", R.drawable.pulloverpoleabarra));
        ejercicios.add(new Ejercicio("Curl de biceps", R.drawable.curlbiceps));
        ejercicios.add(new Ejercicio("Hacka", R.drawable.hacka));
        ejercicios.add(new Ejercicio("Sentadilla", R.drawable.sentadilla));
        ejercicios.add(new Ejercicio("Sentadilla en multipower", R.drawable.sentadillaenmaquinasmithinitpo));
        ejercicios.add(new Ejercicio("Bulgaras", R.drawable.bulgaras));
        ejercicios.add(new Ejercicio("Extensión de quadriceps", R.drawable.extensiondequadriceps));
        ejercicios.add(new Ejercicio("Elevación de gemelos", R.drawable.elevaciondegemelos));
        ejercicios.add(new Ejercicio("Maquina abductores", R.drawable.maquinaabductores));
        ejercicios.add(new Ejercicio("Femoral enfocado con mancuerna", R.drawable.femoralconmancuerna));
        ejercicios.add(new Ejercicio("Curl Femoral", R.drawable.curlfemoral));
        ejercicios.add(new Ejercicio("Peso muerto", R.drawable.pesomuerto));
        return ejercicios;
    }
}
