package com.example.trainingnotes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EjerciciosFragment extends Fragment {
    private TextView blockDetailNameTextViewEjercicios;
    private RecyclerView recyclerViewEJ;
    private EjercicioAdapter ejercicioAdapter;
    private Dialog dialog;
    private List<Ejercicio> selectedEjercicios = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ejercicios, container, false);
        recyclerViewEJ = view.findViewById(R.id.recyclerViewEjercicios);
        String elementName = getArguments().getString("name");

        TextView blockNameTextView = view.findViewById(R.id.blockDetailNameTextViewEjercicios);
        blockNameTextView.setText(elementName);

        view.findViewById(R.id.añadirEjercicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarListaEjercicios();
            }
        });

        return view;
    }

    private void mostrarListaEjercicios() {
        if (dialog == null) {
            dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.dialog_lista_ejercicios);

            RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            List<Ejercicio> listaEjercicios = getEjercicios();
            ejercicioAdapter = new EjercicioAdapter(requireContext(), listaEjercicios);
            ejercicioAdapter.setOnExerciseClickListener(new EjercicioAdapter.OnExerciseClickListener() {
                @Override
                public void onExerciseClick(Ejercicio ejercicio) {
                    selectedEjercicios.add(ejercicio);
                    actualizarRecyclerView();
                }
            });
            recyclerView.setAdapter(ejercicioAdapter);
        }
        dialog.show();
    }

    private void actualizarRecyclerView() {
        if (selectedEjercicios.size() > 0) {
            if (recyclerViewEJ.getVisibility() != View.VISIBLE) {
                recyclerViewEJ.setVisibility(View.VISIBLE);
            }
            if (ejercicioAdapter == null) {
                ejercicioAdapter = new EjercicioAdapter(requireContext(), selectedEjercicios);
                recyclerViewEJ.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerViewEJ.setAdapter(ejercicioAdapter);
            } else {
                ejercicioAdapter.notifyDataSetChanged();
            }
        }
    }


    private List<Ejercicio> getEjercicios() {
        List<Ejercicio> ejercicios = new ArrayList<>();

        ejercicios.add(new Ejercicio("Press Banca", R.drawable.pressdebanca));
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewEJ.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}
