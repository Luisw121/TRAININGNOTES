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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EjerciciosFragment extends Fragment {
    private Dialog dialog;
    private RecyclerView recyclerViewEJ;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private EjercicioAdapter ejercicioAdapter;
    private List<Ejercicio> selectedEjercicios;

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
            DocumentReference userDocRef = firestore.collection("users").document(currentUser.getUid());
            CollectionReference blocksCollectionRef = userDocRef.collection("blocks");

            blocksCollectionRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (DocumentSnapshot blockDocument : task.getResult().getDocuments()) {
                        CollectionReference elementsCollectionRef = blocksCollectionRef.document(blockDocument.getId()).collection("elements");
                        elementsCollectionRef.get().addOnCompleteListener(elementTask -> {
                            if (elementTask.isSuccessful() && !elementTask.getResult().isEmpty()) {
                                for (DocumentSnapshot elementDocument : elementTask.getResult().getDocuments()) {
                                    CollectionReference ejerciciosCollectionRef = elementsCollectionRef.document(elementDocument.getId()).collection("ejercicios");
                                    loadEjerciciosFromFirestore(ejerciciosCollectionRef);
                                }
                            } else {
                                Log.e("EjerciciosFragment", "No se encontraron documentos en la colección 'elements'");
                            }
                        });
                    }
                } else {
                    Log.e("EjerciciosFragment", "No se encontraron documentos en la colección 'blocks'");
                }
            });
        } else {
            Log.e("EjerciciosFragment", "El usuario actual es nulo");
        }

        selectedEjercicios = new ArrayList<>();
        ejercicioAdapter = new EjercicioAdapter(selectedEjercicios, null, false); // La referencia a Firestore se pasará dinámicamente al agregar ejercicios

        recyclerViewEJ.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEJ.setAdapter(ejercicioAdapter);

        view.findViewById(R.id.añadirEjercicio).setOnClickListener(v -> mostrarListaEjercicios());
    }

    private void loadEjerciciosFromFirestore(CollectionReference ejerciciosCollectionRef) {
        ejerciciosCollectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    selectedEjercicios.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Ejercicio ejercicio = document.toObject(Ejercicio.class);
                        selectedEjercicios.add(ejercicio);
                    }
                    ejercicioAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
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

        EjercicioAdapter dialogAdapter = new EjercicioAdapter(listaEjercicios, null, true);
        recyclerView.setAdapter(dialogAdapter);

        dialogAdapter.setOnExerciseClickListener(ejercicio -> {
            if (!selectedEjercicios.contains(ejercicio)) {
                selectedEjercicios.add(ejercicio);
                saveSelectedExercisesToFirestore(ejercicio);
                ejercicioAdapter.notifyDataSetChanged();
            }
            dialog.dismiss();
        });

        builder.setPositiveButton("Aceptar", (dialog, which) -> saveSelectedExercisesToFirestore(null)); // null porque no necesitas la referencia a Firestore aquí
        dialog.show();
    }

    private void saveSelectedExercisesToFirestore(Ejercicio ejercicio) {
        if (currentUser != null) {
            DocumentReference userDocRef = firestore.collection("users").document(currentUser.getUid());
            CollectionReference blocksCollectionRef = userDocRef.collection("blocks");

            // Obtener el nombre del elemento desde los argumentos o de donde sea que lo obtengas
            String elementName = getArguments().getString("name");

            // Agregar el nuevo ejercicio a la colección "ejercicios" del documento "elements" dentro del documento "blocks"
            blocksCollectionRef.document(elementName).collection("ejercicios").add(ejercicio)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("EjerciciosFragment", "Ejercicio agregado correctamente a la colección 'ejercicios'");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EjerciciosFragment", "Error al agregar el ejercicio a la colección 'ejercicios': " + e.getMessage());
                    });
        } else {
            Log.e("EjerciciosFragment", "El usuario actual es nulo");
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
/*
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EjerciciosFragment extends Fragment {
    //private TextView blockDetailNameTextViewEjercicios;
    private Dialog dialog;
    private RecyclerView recyclerViewEJ;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private EjercicioAdapter ejercicioAdapter;
    private List<Ejercicio> selectedEjercicios;
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

        ejercicioAdapter = new EjercicioAdapter(selectedEjercicios, ejerciciosCollectionRef, false);

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
                    selectedEjercicios.clear(); // Asegura que la lista esté vacía antes de añadir elementos
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Ejercicio ejercicio = document.toObject(Ejercicio.class);
                        selectedEjercicios.add(ejercicio);
                    }
                    ejercicioAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
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

        EjercicioAdapter dialogAdapter = new EjercicioAdapter(listaEjercicios, null, true); // null porque no necesitas la referencia a Firestore aquí
        recyclerView.setAdapter(dialogAdapter);

        dialogAdapter.setOnExerciseClickListener(ejercicio -> {
            if (!selectedEjercicios.contains(ejercicio)) {
                selectedEjercicios.add(ejercicio);
                saveSelectedExercisesToFirestore();
                ejercicioAdapter.notifyDataSetChanged();
            }
            dialog.dismiss();
        });

        builder.setPositiveButton("Aceptar", (dialog, which) -> saveSelectedExercisesToFirestore());
        dialog.show();
    }




    private void saveSelectedExercisesToFirestore() {
        if (currentUser != null && !selectedEjercicios.isEmpty()) {
            Ejercicio newEjercicio = selectedEjercicios.get(selectedEjercicios.size() - 1);  // Asume que el último es el nuevo
            ejerciciosCollectionRef.add(newEjercicio)
                    .addOnSuccessListener(documentReference -> {
                        // Manejar éxito
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al agregar el ejercicio: " + e.getMessage());
                    });
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
 */
