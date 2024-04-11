package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EjerciciosFragment extends Fragment {
    /*
    private RecyclerView recyclerViewEjercicio;
    private FirebaseFirestore firestoreEjercicio;
    private FirebaseAuth authEjercicio;
    private FirebaseUser currentUserEjercicio;
    private EjercicioAdapter ejercicioAdapter;
    private List<Ejercicio> ejercicioList;
    private CollectionReference ejerciciosCollectionRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ejercicios, container, false);
        recyclerViewEjercicio = view.findViewById(R.id.recyclerViewEjercicios);

        TextView elementTextView = view.findViewById(R.id.blockDetailNameTextViewEjercicios);

        String Ejercicioname = getArguments().getString("nameEjercicio");
        elementTextView.setText(Ejercicioname);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savesInstanceState) {
        super.onViewCreated(view, savesInstanceState);

        firestoreEjercicio = FirebaseFirestore.getInstance();
        authEjercicio = FirebaseAuth.getInstance();
        currentUserEjercicio = authEjercicio.getCurrentUser();

        if (currentUserEjercicio != null) {
            ejerciciosCollectionRef = firestoreEjercicio.collection("elements").document(currentUserEjercicio.getUid())
                    .collection("ejercicios");
            loadEjerciciosFromFirestore(currentUserEjercicio.getUid(), getArguments().getString("nameEjercicio"));
        }
        ejercicioList = new ArrayList<>();

        ejercicioAdapter = new EjercicioAdapter(ejercicioList, ejerciciosCollectionRef);

        recyclerViewEjercicio.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewEjercicio.setAdapter(ejercicioAdapter);
    }

    private void loadEjerciciosFromFirestore(String userId, String nameEjercicio) {
        firestoreEjercicio.collection("blocks").document(userId).collection("elements")
                .document(nameEjercicio)
                .collection("ejercicios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ejercicioList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Ejercicio element = document.toObject(Ejercicio.class);
                        ejercicioList.add(element);
                    }
                    ejercicioAdapter.notifyDataSetChanged();
                });
    }
     */
}