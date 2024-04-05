package com.example.trainingnotes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        List<Ejercicio> ejercicios = getEjercicios();

        ejerciciosAdapter = new EjerciciosAdapter(requireContext(), ejercicios);
        recyclerView.setAdapter(ejerciciosAdapter);

    }

    private List<Ejercicio> getEjercicios() {
        List<Ejercicio> ejercicios = new ArrayList<>();
        // Agrega aquí tus ejercicios
        ejercicios.add(new Ejercicio("Press de Banca", R.drawable.pressdebanca));
        ejercicios.add(new Ejercicio("Press Inclinado", R.drawable.pressinclinado));
        ejercicios.add(new Ejercicio("Aperturas", R.drawable.aperturas));
        ejercicios.add(new Ejercicio("Fondos profundos", R.drawable.fundoscompeso));
        ejercicios.add(new Ejercicio("Extension de triceps", R.drawable.extensiondetricpes));
        ejercicios.add(new Ejercicio("Press Frances", R.drawable.pressfrances));
        ejercicios.add(new Ejercicio("Elevaciones laterales", R.drawable.elevacioneslaterales));
        ejercicios.add(new Ejercicio("Elevaciónes frontales", R.drawable.elevacionesfrontales));
        ejercicios.add(new Ejercicio("Elevaciónes posteriores", R.drawable.elevacionesposterioresconmancuernas));
        ejercicios.add(new Ejercicio("Jalon al pecho", R.drawable.jalonalpecho));
        ejercicios.add(new Ejercicio("Remo con barra", R.drawable.remoconbarradepieinitpos));
        ejercicios.add(new Ejercicio("Remo unilateral en polea", R.drawable.remohorizontalsentadoconpoleanitpos));
        ejercicios.add(new Ejercicio("Remo con mancuerna", R.drawable.remoconmancuerna));
        ejercicios.add(new Ejercicio("Pull over", R.drawable.pulloverpoleabarra));
        ejercicios.add(new Ejercicio("Curl de biceps", R.drawable.curlbiceps));
        ejercicios.add(new Ejercicio("Hacka", R.drawable.hacka));
        ejercicios.add(new Ejercicio("Sentadilla", R.drawable.sentadilla));
        ejercicios.add(new Ejercicio("Sentadilla en multipower", R.drawable.sentadillaenmaquinasmithinitpo));
        ejercicios.add(new Ejercicio("Bulgaras", R.drawable.bulgaras));
        ejercicios.add(new Ejercicio("Extensión de quadriceps", R.drawable.extensiondequadriceps));
        ejercicios.add(new Ejercicio("Elevación de gemelos", R.drawable.elevaciondegemelos));
        ejercicios.add(new Ejercicio("Maquina abductores", R.drawable.maquinaabductores));
        ejercicios.add(new Ejercicio("Femoral enfocado con mancuerna", R.drawable.femoralconmancuerna));
        ejercicios.add(new Ejercicio("Curl Femoral", R.drawable.curlfemoral));
        ejercicios.add(new Ejercicio("Peso muerto", R.drawable.pesomuerto));

        return ejercicios;
    }
}