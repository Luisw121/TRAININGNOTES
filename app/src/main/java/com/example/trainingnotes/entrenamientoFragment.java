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

        blockList = new ArrayList<>();
        adapter = new BlockAdapter(blockList);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            loadBlocksFromFirestore(currentUser.getUid());
        }

        view.findViewById(R.id.addBlockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBlockDialog();
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
}
