package com.example.trainingnotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
    private RecyclerView recyclerViewEjercicios;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private EjercicioAdapter adapterEjercicios;
    private List<Ejercicio> ejerciciosList;
    private CollectionReference ejerciciosCollectionRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ejercicios, container, false);
        recyclerViewEjercicios = view.findViewById(R.id.recyclerViewEjercicios);

        String blockName = getArguments().getString("blockName");
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
            String elementName = getArguments().getString("name");
            ejerciciosCollectionRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("elements")
                    .document(elementName)
                    .collection("ejercicios");
            loadEjerciciosFromFirestore();
        }

        ejerciciosList = new ArrayList<>();
        adapterEjercicios = new EjercicioAdapter(ejerciciosList, ejerciciosCollectionRef);

        recyclerViewEjercicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEjercicios.setAdapter(adapterEjercicios);

        view.findViewById(R.id.añadirEjercicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEjercicioDialog();
            }
        });
        adapterEjercicios.setOnDeleteClickListener(new EjercicioAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String ejercicioName) {
                deleteEjercicioFromFirestore(ejercicioName);
            }
        });
        adapterEjercicios.setOnEjercicioClickListener(new EjercicioAdapter.OnEjercicioClickListener() {
            @Override
            public void onEjercicioClick(Ejercicio ejercicio) {
                String blockName = getArguments().getString("blockName");
                String elementName = getArguments().getString("name");
                String ejercicioName = ejercicio.getNombre();
                navigateToDatosEjerciciosFragment(blockName, elementName, ejercicioName);
            }
        });
    }
    private void navigateToDatosEjerciciosFragment(String blockName, String elementName, String ejercicioName) {
        Bundle bundle = new Bundle();
        bundle.putString("blockName", blockName);
        bundle.putString("elementName", elementName);
        bundle.putString("ejercicioName", ejercicioName);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_ejerciciosFragment_to_datosEjerciciosFragment, bundle);
    }

    private void loadEjerciciosFromFirestore() {
        // Obtener el nombre del bloque y el elemento
        String blockName = getArguments().getString("blockName");
        String elementName = getArguments().getString("name");

        // Verificar que el usuario actual y los nombres del bloque y el elemento no sean nulos
        if (currentUser != null && blockName != null && elementName != null) {
            // Construir la referencia al documento del elemento en Firestore
            DocumentReference elementDocumentRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("blocks")
                    .document(blockName)
                    .collection("elements")
                    .document(elementName);

            // Construir la referencia a la colección de ejercicios del elemento
            CollectionReference ejerciciosCollectionRef = elementDocumentRef.collection("ejercicios");

            // Obtener los ejercicios de Firestore
            ejerciciosCollectionRef.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        ejerciciosList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Ejercicio ejercicio = document.toObject(Ejercicio.class);
                            ejerciciosList.add(ejercicio);
                        }
                        adapterEjercicios.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al cargar los ejercicios desde Firestore: " + e.getMessage());
                    });
        } else {
            // Manejar el caso en que el usuario, el nombre del bloque o el nombre del elemento sean nulos
            Log.e("Firestore", "El usuario, el nombre del bloque o el nombre del elemento son nulos");
            // También puedes mostrar un mensaje de error al usuario si lo deseas
        }
    }
    private void showAddEjercicioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Agregar Ejercicio");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombreEjercicio = input.getText().toString().trim();
                if (!nombreEjercicio.isEmpty()) {
                    String blockName = getArguments().getString("blockName");
                    String elementName = getArguments().getString("name");
                    // Verificar que los nombres del bloque y del elemento no sean nulos
                    if (blockName != null && elementName != null) {
                        // Agregar el ejercicio a Firestore
                        addEjercicioToFirestore(nombreEjercicio);
                    } else {
                        Toast.makeText(requireContext(), "Los nombres de bloque y elemento no pueden ser nulos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Por favor, introduce un nombre válido para el ejercicio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void addEjercicioToFirestore(String ejercicioName) {
        Ejercicio ejercicio = new Ejercicio(ejercicioName);
        String blockName = getArguments().getString("blockName");
        String elementName = getArguments().getString("name");
        // Crear la referencia al documento del elemento en Firestore
        DocumentReference elementoDocumentRef = firestore.collection("users")
                .document(currentUser.getUid())
                .collection("blocks")
                .document(blockName)
                .collection("elements")
                .document(elementName);

        // Agregar el ejercicio al subcoleccion de ejercicios del elemento
        elementoDocumentRef.collection("ejercicios").document(ejercicioName).set(ejercicio)
                .addOnSuccessListener(documentReference -> {
                    // Manejar el éxito, si es necesario
                    ejerciciosList.add(ejercicio);
                    adapterEjercicios.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al agregar el ejercicio: " + e.getMessage());
                });
    }
    private void deleteEjercicioFromFirestore(String ejercicioName) {
        // Obtener el nombre del bloque y el elemento
        String blockName = getArguments().getString("blockName");
        String elementName = getArguments().getString("name");

        // Verificar que el usuario actual y los nombres del bloque y el elemento no sean nulos
        if (currentUser != null && blockName != null && elementName != null) {
            // Construir la referencia al documento del elemento en Firestore
            DocumentReference elementDocumentRef = firestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("blocks")
                    .document(blockName)
                    .collection("elements")
                    .document(elementName);

            // Construir la referencia al documento del ejercicio en Firestore
            DocumentReference ejercicioDocumentRef = elementDocumentRef.collection("ejercicios").document(ejercicioName);

            // Eliminar el ejercicio de Firestore
            ejercicioDocumentRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Manejar la eliminación exitosa, si es necesario
                        Log.d("Firestore", "Ejercicio eliminado exitosamente");
                        // También puedes actualizar la lista de ejercicios si lo deseas
                        loadEjerciciosFromFirestore();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el fallo en la eliminación
                        Log.e("Firestore", "Error al eliminar el ejercicio: " + e.getMessage());
                    });
        } else {
            // Manejar el caso en que el usuario, el nombre del bloque o el nombre del elemento sean nulos
            Log.e("Firestore", "El usuario, el nombre del bloque o el nombre del elemento son nulos");
            // También puedes mostrar un mensaje de error al usuario si lo deseas
        }
    }

}
