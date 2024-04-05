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

import java.util.ArrayList;
import java.util.List;

public class ListaEjerciciosFragment extends Fragment {
    private RecyclerView recyclerView;
    private EjerciciosAdapter ejerciciosAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_ejercicios, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Ejercicio> ejercicios = getEjercicios();

        ejerciciosAdapter = new EjerciciosAdapter(requireContext(), ejercicios);
        recyclerView.setAdapter(ejerciciosAdapter);
    }

    private List<Ejercicio> getEjercicios() {
        List<Ejercicio> ejercicios = new ArrayList<>();
        // Agrega aquí tus ejercicios
        ejercicios.add(new Ejercicio("Press de Banca", R.drawable.perfilimage));
        ejercicios.add(new Ejercicio("Press Inclinado", R.drawable.gymsharklogo));
        // Agrega más ejercicios según sea necesario
        return ejercicios;
    }
}