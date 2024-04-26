package com.example.trainingnotes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalendarioEjercicios_Fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_ejercicios_, container, false);
        TextView nameDetailNameTextViewDatosCal = view.findViewById(R.id.nameDetailNameTextViewDatosCal);


        String ejercicioName = getArguments().getString("ejercicioName");


        nameDetailNameTextViewDatosCal.setText(ejercicioName);
        return view;
    }
}