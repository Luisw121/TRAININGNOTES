package com.example.trainingnotes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DatosEjerciciosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datos_ejercicios, container, false);

        //TextView blockNameTextView = view.findViewById(R.id.nameDetailNameTextViewDatos);
        //TextView elementNameTextView = view.findViewById(R.id.elementNameTextViewDatos);
        TextView ejercicioNameTextView = view.findViewById(R.id.nameDetailNameTextViewDatos);

        //Secundarios
        String blockName = getArguments().getString("blockName"); // Pilla los bloques
        String elementName = getArguments().getString("elementName"); // Pilla los dias

        //Este es el importante
        String ejercicioName = getArguments().getString("ejercicioName"); // Pilla los ejercicios

        ejercicioNameTextView.setText(ejercicioName);

        //elementNameTextView.setText(elementName);
        //ejercicioNameTextView.setText(ejercicioName);

        return view;
    }

}