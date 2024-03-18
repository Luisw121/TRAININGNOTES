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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class entrenamientoFragment extends Fragment {
    private RecyclerView recyclerView;
    private BlockAdapter adapter;
    private List<Block> blockList;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        blockList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("blocks");
        loadBlocksFromFirebase();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrenamiento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewBlocks);
        view.findViewById(R.id.addBlockButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBlockDialog();
            }
        });

        adapter = new BlockAdapter(blockList, databaseReference);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);


    }

    private void loadBlocksFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blockList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Block block = dataSnapshot.getValue(Block.class);
                    blockList.add(block);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
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
                    addBlockToFirebase(blockName);
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

    private void addBlockToFirebase(String blockName) {
        // Generar una clave única para el nuevo bloque
        String blockId = databaseReference.push().getKey();

        // Crear un objeto Block con el nombre proporcionado
        Block block = new Block(blockName);

        // Guardar el bloque en la base de datos Firebase
        if (blockId != null) {
            databaseReference.child(blockId).setValue(block);
        }
    }


}