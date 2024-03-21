package com.example.trainingnotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BlockDetailFragment extends Fragment {
    private RecyclerView recyclerViewElement;
    private FirebaseFirestore firestoreElement;
    private FirebaseAuth authElement;
    private FirebaseUser currentUserElement;
    private ElementAdapter adapterElement;
    private List<Element> elementList;
    private CollectionReference elementsCollectionRef;


    public static BlockDetailFragment newInstance(String blockName){
        BlockDetailFragment fragment = new BlockDetailFragment();
        Bundle args = new Bundle();
        args.putString("blockName", blockName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_detail, container, false);

        String blockname = getArguments().getString("blockName");

        TextView blockNameTextView = view.findViewById(R.id.blockDetailNameTextView);
        blockNameTextView.setText(blockname);

        recyclerViewElement = view.findViewById(R.id.recyclerViewElements);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestoreElement = FirebaseFirestore.getInstance();
        authElement = FirebaseAuth.getInstance();
        currentUserElement = authElement.getCurrentUser();

        if (currentUserElement != null) {
            elementsCollectionRef = firestoreElement.collection("blocks").document(currentUserElement.getUid()).collection("elements");
            loadElementsFromFirestore(currentUserElement.getUid());
        }

        elementList = new ArrayList<>();
        adapterElement = new ElementAdapter(elementList, elementsCollectionRef);

        recyclerViewElement.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewElement.setAdapter(adapterElement);

        view.findViewById(R.id.añadirElemento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddElementDialog();
            }
        });
        adapterElement.setOnDeleteClickListener(new BlockAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String position) {
                deleteElementFromFirestore(position);
            }
        });

    }

    private void loadElementsFromFirestore(String uid) {
        firestoreElement.collection("blocks").document(uid).collection("elements")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    elementList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Element element = document.toObject(Element.class);
                        elementList.add(element);
                    }
                    adapterElement.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error" + e.getMessage());
                });
    }

    private void showAddElementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Agregar Bloque");

        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String blockName = getArguments().getString("blockName");

                String elementName = input.getText().toString();
                if (!blockName.isEmpty()) {
                    addElementToFirestore(blockName, elementName);
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
    private void addElementToFirestore(String blockName, String elementName) {
        Element element = new Element(elementName);
        firestoreElement.collection("users")
                .document(currentUserElement.getUid())
                .collection("blocks")
                .document(blockName)
                .collection("elements")
                .add(element)
                .addOnSuccessListener(documentReference -> {
                    elementList.add(element);
                    adapterElement.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                });
    }
    private void deleteElementFromFirestore(String blockname) {
        elementsCollectionRef.whereEqualTo("blockName", blockname)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    for (int i = 0; i < elementList.size(); i++) {
                                        if (elementList.get(i).getName().equals(blockname)) {
                                            elementList.remove(i);
                                            adapterElement.notifyItemRemoved(i);
                                            break;
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error al eliminar el bloque de Firestore: "+ e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error al consultar el bloque en Firestore: " + e.getMessage());
                });
    }
}