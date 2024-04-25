package com.example.trainingnotes;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.recyclerview.widget.DividerItemDecoration;

public class DatosEjerciciosFragment extends Fragment {

    private List<DatoInicial> serieDatosList = new ArrayList<>();
    private RecyclerView recyclerViewSerieDatos;
    private DatoAdapter serieDatosAdapter;

    private Button saveButton;
    private ImageView addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datos_ejercicios, container, false);


        saveButton = view.findViewById(R.id.saveButton);
        
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });
        //TextView blockNameTextView = view.findViewById(R.id.nameDetailNameTextViewDatos);
        //TextView elementNameTextView = view.findViewById(R.id.elementNameTextViewDatos);
        TextView ejercicioNameTextView = view.findViewById(R.id.nameDetailNameTextViewDatos);

        //Secundarios
        String blockName = getArguments().getString("blockName"); // Pilla los bloques
        String elementName = getArguments().getString("elementName"); // Pilla los dias

        //Este es el importante
        String ejercicioName = getArguments().getString("ejercicioName"); // Pilla los ejercicios

        ejercicioNameTextView.setText(ejercicioName);

        recyclerViewSerieDatos = view.findViewById(R.id.recyclerViewSerieDatos);
        recyclerViewSerieDatos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSerieDatos.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));

        serieDatosList = new ArrayList<>();

        serieDatosAdapter = new DatoAdapter(serieDatosList, new DatoAdapter.OnSerieClickListener() {
            @Override
            public void onSerieDeleteClick(int position) {
                eliminarSerieSeleccionada(position);
            }

            @Override
            public void onSerieAddClick() {
                agregarSerieInicial();
            }
        });
        recyclerViewSerieDatos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewSerieDatos.setAdapter(serieDatosAdapter);

        //elementNameTextView.setText(elementName);
        //ejercicioNameTextView.setText(ejercicioName);


        ImageView addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarSerieInicial();
            }
        });
        
        ImageView deleteButton = view.findViewById(R.id.deleteButton1);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarUltimaSerie();

            }
        });
        loadDatosFromFirebase();
        return view;
    }

    private void mostrarCalendario() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear un DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Aquí puedes guardar la fecha seleccionada en el calendario
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        // Luego puedes guardar las series en la fecha seleccionada
                        guardarSeriesEnCalendario(selectedDate);
                    }
                }, year, month, dayOfMonth);

        // Mostrar el DatePickerDialog
        datePickerDialog.show();
    }

    private void guardarSeriesEnCalendario(String selectedDate) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String ejercicioName = getArguments().getString("ejercicioName");
        String blockName = getArguments().getString("blockName");
        String elementName = getArguments().getString("elementName");

        // Referencia al documento del usuario en la colección "users"
        DocumentReference userDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid());

        // Referencia al documento en la colección "calendario" del usuario actual
        DocumentReference calendarDocRef = userDocRef.collection("calendario")
                .document(selectedDate);

        DocumentReference ejercicioDocRef2 = calendarDocRef.collection("elements")
                .document(elementName)
                .collection("ejercicios")
                .document(ejercicioName);

        // Crear un mapa con los datos de las series
        List<Map<String, Object>> serieDatosMapList = new ArrayList<>();
        for (DatoInicial serieDatos : serieDatosList) {
            Map<String, Object> serieDatosMap = new HashMap<>();
            serieDatosMap.put("repeticiones", serieDatos.getRepeticiones());
            serieDatosMap.put("peso", serieDatos.getPeso());
            serieDatosMap.put("rpe", serieDatos.getRpe());
            serieDatosMapList.add(serieDatosMap);
        }

        // Crear un mapa con los datos del ejercicio
        Map<String, Object> ejercicioMap = new HashMap<>();
        ejercicioMap.put("nombre", ejercicioName);
        ejercicioMap.put("series", serieDatosMapList);

        // Guardar los datos del ejercicio en el documento correspondiente en "calendario"
        ejercicioDocRef2.set(ejercicioMap)
                .addOnSuccessListener(aVoid -> {
                    // Éxito al guardar las series
                    Toast.makeText(requireContext(), "Series guardadas en calendario", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error al guardar las series
                    Toast.makeText(requireContext(), "Error al guardar las series en calendario", Toast.LENGTH_SHORT).show();
                    System.out.println("Error al guardar las series en calendario: " + e.getMessage());
                });
    }




    private void eliminarUltimaSerie() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String ejercicioName = getArguments().getString("ejercicioName");
            String blockName = getArguments().getString("blockName");
            String elementName = getArguments().getString("elementName");

            // Referencia al documento de ejercicios en Firestore
            DocumentReference ejercicioDocRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.getUid())
                    .collection("blocks")
                    .document(blockName)
                    .collection("elements")
                    .document(elementName)
                    .collection("ejercicios")
                    .document(ejercicioName);

            // Obtener el array actual de series
            ejercicioDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<Map<String, Object>> seriesList = (List<Map<String, Object>>) documentSnapshot.get("series");
                    if (seriesList != null && !seriesList.isEmpty()) {
                        // Eliminar el último elemento del array
                        seriesList.remove(seriesList.size() - 1);

                        // Actualizar el documento con el nuevo array
                        ejercicioDocRef.update("series", seriesList)
                                .addOnSuccessListener(aVoid -> {
                                    // Actualización exitosa
                                    datosCambiados = true;
                                    loadDatosFromFirebase();//importante, elimina visualmente
                                    System.out.println("Última serie eliminada con éxito.");
                                })
                                .addOnFailureListener(e -> {
                                    // Error al actualizar el documento
                                    System.out.println("Error al eliminar la última serie: " + e.getMessage());
                                });
                    } else {
                        // No hay series para eliminar
                        System.out.println("No hay series para eliminar.");
                    }
                } else {
                    // El documento no existe
                    System.out.println("El documento no existe.");
                }
            }).addOnFailureListener(e -> {
                // Error al obtener el documento
                System.out.println("Error al obtener el documento: " + e.getMessage());
            });
        }
    }



    private boolean datosCambiados = false;
    private void loadDatosFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String ejercicioName = getArguments().getString("ejercicioName");
        String blockName = getArguments().getString("blockName");
        String elementName = getArguments().getString("elementName");

        DocumentReference ejercicioDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("blocks")
                .document(blockName)
                .collection("elements")
                .document(elementName)
                .collection("ejercicios")
                .document(ejercicioName);

        ejercicioDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> datosEjercicio = documentSnapshot.getData();
                List<Map<String, Object>> serieDatosMapList = (List<Map<String, Object>>) datosEjercicio.get("series");
                if (serieDatosMapList != null) {
                    // Limpiar la lista local antes de agregar las series de Firebase
                    serieDatosList.clear(); // Limpiar la lista antes de agregar las nuevas series
                    for (Map<String, Object> serieDatosMap : serieDatosMapList) {
                        Long repeticionesLong = (Long) serieDatosMap.get("repeticiones");
                        int repeticiones = repeticionesLong != null ? repeticionesLong.intValue() : 0;

                        Long pesoLong = (Long) serieDatosMap.get("peso");
                        int peso = pesoLong != null ? pesoLong.intValue() : 0;

                        Long rpeLong = (Long) serieDatosMap.get("rpe");
                        int rpe = rpeLong != null ? rpeLong.intValue() : 0;

                        DatoInicial serieDatos = new DatoInicial(repeticiones, peso, rpe);
                        serieDatosList.add(serieDatos);
                    }
                    // Notificar al adaptador del cambio
                    serieDatosAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(e -> {
            // Manejar el error al obtener datos de Firebase
        });
    }



    private void agregarSerieInicial() {
        // Inflar el layout con los EditText
        View inflatedView = getLayoutInflater().inflate(R.layout.item_dato, null);
        EditText repsEditText = inflatedView.findViewById(R.id.textViewReps);
        EditText pesoEditText = inflatedView.findViewById(R.id.textViewPeso);
        EditText rpeEditText = inflatedView.findViewById(R.id.textViewRpe);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Agregar Serie");
        repsEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        pesoEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        rpeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(inflatedView);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener los valores de los EditText después de hacer clic en Aceptar
                String repsString = repsEditText.getText().toString();
                String pesoString = pesoEditText.getText().toString();
                String rpeString = rpeEditText.getText().toString();

                // Verificar si los campos no están vacíos
                if (!TextUtils.isEmpty(repsString) && !TextUtils.isEmpty(pesoString) && !TextUtils.isEmpty(rpeString)) {
                    // Convertir los valores a enteros
                    int reps = Integer.parseInt(repsString);
                    int peso = Integer.parseInt(pesoString);
                    int rpe = Integer.parseInt(rpeString);

                    // Generar un ID único para la nueva serie
                    String serieId = FirebaseFirestore.getInstance().collection("users").document().getId();

                    // Agregar la nueva serie a la lista con el ID único
                    DatoInicial nuevaSerie = new DatoInicial(reps, peso, rpe);
                    nuevaSerie.setId(serieId); // Asumiendo que tienes un método setId en tu clase DatoInicial
                    serieDatosList.add(nuevaSerie);
                    serieDatosAdapter.notifyDataSetChanged();

                    // Marcar los datos como modificados
                    datosCambiados = true;
                    guardarDatosEnFirebase();
                } else {
                    // Mostrar un mensaje de error si algún campo está vacío
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void eliminarSerieSeleccionada(int position) {
        // Verificar si hay elementos en la lista antes de intentar eliminar
        if (!serieDatosList.isEmpty()) {
            // Obtener la serie a eliminar
            DatoInicial serieAEliminar = serieDatosList.get(position);

            // Eliminar la serie de Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String ejercicioName = getArguments().getString("ejercicioName");
                String blockName = getArguments().getString("blockName");
                String elementName = getArguments().getString("elementName");

                DocumentReference serieDocRef = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(currentUser.getUid())
                        .collection("blocks")
                        .document(blockName)
                        .collection("elements")
                        .document(elementName)
                        .collection("ejercicios")
                        .document(ejercicioName)
                        .collection("series")
                        .document(serieAEliminar.getId());

                serieDocRef.delete()
                        .addOnSuccessListener(aVoid -> {
                            // Éxito al eliminar la serie de Firebase
                            System.out.println("Serie eliminada de Firebase");

                            // Eliminar la serie de la lista local
                            serieDatosList.remove(position);

                            // Notificar al adaptador del cambio
                            serieDatosAdapter.notifyItemRemoved(position);
                        })
                        .addOnFailureListener(e -> {
                            // Error al eliminar la serie de Firebase
                            System.out.println("Error al eliminar serie de Firebase: " + e.getMessage());
                        });
            }
        }
    }

    private void guardarDatosEnFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String blockName = getArguments().getString("blockName");
        String elementName = getArguments().getString("elementName");
        String ejercicioName = getArguments().getString("ejercicioName"); // Guardar la referencia localmente

        // Referencia al documento de ejercicios en Firestore
        DocumentReference ejercicioDocRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("blocks")
                .document(blockName)
                .collection("elements")
                .document(elementName)
                .collection("ejercicios")
                .document(ejercicioName);

        // Eliminar todas las series existentes del documento
        ejercicioDocRef.update("series", FieldValue.delete())
                .addOnSuccessListener(aVoid -> {
                    // Agregar las nuevas series al documento
                    Map<String, Object> data = new HashMap<>();
                    List<Map<String, Object>> serieDatosMapList = new ArrayList<>();
                    for (DatoInicial serieDatos : serieDatosList) {
                        Map<String, Object> serieDatosMap = new HashMap<>();
                        serieDatosMap.put("repeticiones", serieDatos.getRepeticiones());
                        serieDatosMap.put("peso", serieDatos.getPeso());
                        serieDatosMap.put("rpe", serieDatos.getRpe());
                        serieDatosMapList.add(serieDatosMap);
                    }
                    data.put("nombre", ejercicioName);
                    data.put("series", serieDatosMapList);
                    ejercicioDocRef.set(data)
                            .addOnSuccessListener(aVoid1 -> {
                                System.out.println("Datos de series actualizados en el documento de ejercicios en Firestore");

                                // Restaurar la referencia después de la actualización

                            })
                            .addOnFailureListener(e -> {
                                System.out.println("Error al actualizar datos de series en el documento de ejercicios en Firestore: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error al eliminar las series existentes del documento de ejercicios en Firestore: " + e.getMessage());
                });
    }

    private void agregarDatosSerie(int repeticiones, int peso, int rpe) {
        DatoInicial serieDatos = new DatoInicial();
        serieDatos.setRepeticiones(repeticiones);
        serieDatos.setPeso(peso);
        serieDatos.setRpe(rpe);
        serieDatosList.add(serieDatos);
    }

}