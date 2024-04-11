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
    private ListaEjerciciosAdapter ejerciciosAdapter;
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

        List<ListaEjercicio> ejercicios = getEjercicios();

        ejerciciosAdapter = new ListaEjerciciosAdapter(requireContext(), ejercicios);
        recyclerView.setAdapter(ejerciciosAdapter);

    }

    private List<ListaEjercicio> getEjercicios() {
        List<ListaEjercicio> ejercicios = new ArrayList<>();
        // Agrega aquí tus ejercicios
        ejercicios.add(new ListaEjercicio("Press de Banca", R.drawable.pressdebanca));
        ejercicios.add(new ListaEjercicio("Press Inclinado", R.drawable.pressinclinado));
        ejercicios.add(new ListaEjercicio("Aperturas", R.drawable.aperturas));
        ejercicios.add(new ListaEjercicio("Fondos profundos", R.drawable.fundoscompeso));
        ejercicios.add(new ListaEjercicio("Extension de triceps", R.drawable.extensiondetricpes));
        ejercicios.add(new ListaEjercicio("Press Frances", R.drawable.pressfrances));
        ejercicios.add(new ListaEjercicio("Elevaciones laterales", R.drawable.elevacioneslaterales));
        ejercicios.add(new ListaEjercicio("Elevaciónes frontales", R.drawable.elevacionesfrontales));
        ejercicios.add(new ListaEjercicio("Elevaciónes posteriores", R.drawable.elevacionesposterioresconmancuernas));
        ejercicios.add(new ListaEjercicio("Jalon al pecho", R.drawable.jalonalpecho));
        ejercicios.add(new ListaEjercicio("Remo con barra", R.drawable.remoconbarradepieinitpos));
        ejercicios.add(new ListaEjercicio("Remo unilateral en polea", R.drawable.remohorizontalsentadoconpoleanitpos));
        ejercicios.add(new ListaEjercicio("Remo con mancuerna", R.drawable.remoconmancuerna));
        ejercicios.add(new ListaEjercicio("Pull over", R.drawable.pulloverpoleabarra));
        ejercicios.add(new ListaEjercicio("Curl de biceps", R.drawable.curlbiceps));
        ejercicios.add(new ListaEjercicio("Hacka", R.drawable.hacka));
        ejercicios.add(new ListaEjercicio("Sentadilla", R.drawable.sentadilla));
        ejercicios.add(new ListaEjercicio("Sentadilla en multipower", R.drawable.sentadillaenmaquinasmithinitpo));
        ejercicios.add(new ListaEjercicio("Bulgaras", R.drawable.bulgaras));
        ejercicios.add(new ListaEjercicio("Extensión de quadriceps", R.drawable.extensiondequadriceps));
        ejercicios.add(new ListaEjercicio("Elevación de gemelos", R.drawable.elevaciondegemelos));
        ejercicios.add(new ListaEjercicio("Maquina abductores", R.drawable.maquinaabductores));
        ejercicios.add(new ListaEjercicio("Femoral enfocado con mancuerna", R.drawable.femoralconmancuerna));
        ejercicios.add(new ListaEjercicio("Curl Femoral", R.drawable.curlfemoral));
        ejercicios.add(new ListaEjercicio("Peso muerto", R.drawable.pesomuerto));

        return ejercicios;
    }
}