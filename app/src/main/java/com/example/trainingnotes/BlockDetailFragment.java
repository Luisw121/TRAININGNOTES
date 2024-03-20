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
    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private ElementAdapter adapter;
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

        recyclerView = view.findViewById(R.id.recyclerViewElements);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            elementsCollectionRef = firestore.collection("blocks").document(currentUser.getUid()).collection("elements");
            loadElementsFromFirestore(currentUser.getUid());
        }

        elementList = new ArrayList<>();
        adapter = new ElementAdapter(elementList, elementsCollectionRef);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.añadirElemento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddElementDialog();
            }
        });

    }

    private void loadElementsFromFirestore(String uid) {
        firestore.collection("blocks").document(uid).collection("elements")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    elementList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Element element = document.toObject(Element.class);
                        elementList.add(element);
                    }
                    adapter.notifyDataSetChanged();
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
        firestore.collection("users")
                .document(currentUser.getUid())
                .collection("blocks")
                .document(blockName)
                .collection("elements")
                .add(element)
                .addOnSuccessListener(documentReference -> {
                    // Elemento agregado con éxito
                })
                .addOnFailureListener(e -> {
                    // Maneja el error
                });
    }

    }