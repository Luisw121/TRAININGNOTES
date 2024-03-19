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
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class entrenamientoFragment extends Fragment {
    private RecyclerView recyclerView;
    private BlockAdapter adapter;
    private List<Block> blockList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private CollectionReference blocksCollectionRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrenamiento, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewBlocks);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            blocksCollectionRef = firestore.collection("users").document(currentUser.getUid()).collection("blocks");
            loadBlocksFromFirestore(currentUser.getUid());
        }

        blockList = new ArrayList<>();
        adapter = new BlockAdapter(blockList, blocksCollectionRef);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.addBlockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBlockDialog();
            }
        });

        // Configurar el RecyclerView para manejar los clics en los botones de eliminar
        adapter.setOnDeleteClickListener(new BlockAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String blockName) {
                deleteBlockFromFirestore(blockName);
            }
        });
    }

    private void loadBlocksFromFirestore(String userId) {
        firestore.collection("users").document(userId).collection("blocks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    blockList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Block block = document.toObject(Block.class);
                        blockList.add(block);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }

    private void showAddBlockDialog() {
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
                String blockName = input.getText().toString();
                if (!blockName.isEmpty()) {
                    addBlockToFirestore(blockName);
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

    private void addBlockToFirestore(String blockName) {
        Block block = new Block(blockName);
        firestore.collection("users").document(currentUser.getUid()).collection("blocks")
                .add(block)
                .addOnSuccessListener(documentReference -> {
                    // Block added successfully
                    blockList.add(block);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }
    private void deleteBlockFromFirestore(String blockName) {
        blocksCollectionRef.whereEqualTo("blockName", blockName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Eliminar el bloque localmente
                                    for (int i = 0; i < blockList.size(); i++) {
                                        if (blockList.get(i).getBlockName().equals(blockName)) {
                                            blockList.remove(i);
                                            adapter.notifyItemRemoved(i);
                                            break;
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error al eliminar el bloque de Firestore: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error al consultar el bloque en Firestore: " + e.getMessage());
                });
    }

}
