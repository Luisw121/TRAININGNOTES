package com.example.trainingnotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BlockDetailFragment extends Fragment {
    private RecyclerView recyclerViewElement;
    private FirebaseFirestore firestoreElement;
    private FirebaseAuth authElement;
    private FirebaseUser currentUserElement;
    private blockAdapter adapterElement;
    private List<block> elementList;
    private CollectionReference elementsCollectionRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_detail, container, false);
        recyclerViewElement = view.findViewById(R.id.recyclerViewDays);

        String blockname = getArguments().getString("blockName");

        TextView blockNameTextView = view.findViewById(R.id.blockDetailNameTextViewDays);
        blockNameTextView.setText(blockname);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestoreElement = FirebaseFirestore.getInstance();
        authElement = FirebaseAuth.getInstance();
        currentUserElement = authElement.getCurrentUser();

        if (currentUserElement != null) {
            elementsCollectionRef = firestoreElement.collection("users").document(currentUserElement.getUid()).collection("blocks");
            loadElementsFromFirestore(currentUserElement.getUid(), getArguments().getString("blockName"));
        }

        elementList = new ArrayList<>();
        adapterElement = new blockAdapter(elementList, elementsCollectionRef);

        recyclerViewElement.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewElement.setAdapter(adapterElement);

        view.findViewById(R.id.añadirDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddElementDialog();
            }
        });

        adapterElement.setOnDeleteClickListener(new blockAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String elementName) {
                deleteElementFromFirestore(elementName);
            }
        });

        adapterElement.setOnElemntBlockClickListener(new blockAdapter.OnElemntBlockClickListener() {
            @Override
            public void onElementClickBlock(String elementName) {
            }
        });

        adapterElement.setOnElementClickListener(new blockAdapter.onElementClickListener() {
            @Override
            public void onElementClick(String elementName) {
                String blockName = getArguments().getString("blockName");
                navigateToElemenetFragment(elementName, blockName);
            }
        });
    }

    private void navigateToElemenetFragment(String elementName, String blockName) {
        Bundle bundle = new Bundle();
        bundle.putString("name", elementName);
        bundle.putString("blockName", blockName);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_blockDetailFragment_to_ejerciciosFragment, bundle);
    }

    private void loadElementsFromFirestore(String userId, String blockName) {
        firestoreElement.collection("users").document(userId).collection("blocks")
                .document(blockName)
                .collection("elements")
                .orderBy("timestamp") // Ordenar por timestamp
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    elementList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        block element = document.toObject(block.class);
                        if (element != null) { // Verificar si el elemento no es nulo
                            elementList.add(element);
                        }
                    }
                    adapterElement.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Manejar el error
                });
    }

    private void showAddElementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Agregar Elemento");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String elementName = input.getText().toString();
                if (!elementName.isEmpty()) {
                    String blockName = getArguments().getString("blockName");
                    if (blockName != null && !blockName.isEmpty()) {
                        addElementToFirestore(blockName, elementName);
                    }
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
        block element = new block(elementName);

        DocumentReference blockDocumentRef = firestoreElement.collection("users")
                .document(currentUserElement.getUid())
                .collection("blocks")
                .document(blockName);

        blockDocumentRef.collection("elements").document(elementName).set(element)
                .addOnSuccessListener(documentReference -> {
                    elementList.add(element);
                    adapterElement.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error al agregar elemento: " + e.getMessage());
                });
    }

    private void deleteElementFromFirestore(String elementName) {
        DocumentReference documentRef = firestoreElement.collection("users")
                .document(currentUserElement.getUid())
                .collection("blocks")
                .document(getArguments().getString("blockName"))
                .collection("elements")
                .document(elementName);

        documentRef.delete()
                .addOnSuccessListener(aVoid -> {
                    for (int i = 0; i < elementList.size(); i++) {
                        if (elementList.get(i).getName().equals(elementName)) {
                            elementList.remove(i);
                            adapterElement.notifyItemRemoved(i);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error al eliminar el elemento: " + e.getMessage());
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentUserElement != null) {
            loadElementsFromFirestore(currentUserElement.getUid(), getArguments().getString("blockName"));
        }
    }
}
