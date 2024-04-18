package com.example.trainingnotes;

import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder> {
    private List<Ejercicio> ejerciciosList;
    private CollectionReference ejerciciosCollectionRef;

    public EjercicioAdapter(List<Ejercicio> ejerciciosList, CollectionReference ejerciciosCollectionRef) {
        this.ejerciciosList = ejerciciosList;
        this.ejerciciosCollectionRef = ejerciciosCollectionRef;
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        Ejercicio ejercicio = ejerciciosList.get(position);
        holder.bind(ejercicio);
    }

    @Override
    public int getItemCount() {
        return ejerciciosList.size();
    }

    public class EjercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreEjercicio;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreEjercicio = itemView.findViewById(R.id.blockNameTextViewDays);
        }

        public void bind(Ejercicio ejercicio) {
            textViewNombreEjercicio.setText(ejercicio.getNombre());
        }
    }
}