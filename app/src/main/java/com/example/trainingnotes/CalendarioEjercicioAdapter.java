package com.example.trainingnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.util.Collections;
import java.util.List;

public class CalendarioEjercicioAdapter extends RecyclerView.Adapter<CalendarioEjercicioAdapter.CalendarioEjercicioViewHolder> {

    private List<CalendarioEjercicios> nombresEjercicios;
    private CollectionReference collectionReference;
    public CalendarioEjercicioAdapter(List<CalendarioEjercicios> nombresEjercicios, CollectionReference collectionReference) {
        this.nombresEjercicios = nombresEjercicios;
        this.collectionReference = collectionReference;
    }

    @NonNull
    @Override
    public CalendarioEjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new CalendarioEjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarioEjercicioViewHolder holder, int position) {
        CalendarioEjercicios ejercicioName = nombresEjercicios.get(position);
        holder.bind(ejercicioName);
    }

    @Override
    public int getItemCount() {
        return nombresEjercicios.size();
    }

    public static class CalendarioEjercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreEjercicio;

        public CalendarioEjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreEjercicio = itemView.findViewById(R.id.nameDetailNameTextViewDatosCal);
        }

        public void bind(CalendarioEjercicios ejercicioName) {
            textViewNombreEjercicio.setText(ejercicioName.getNombre());
        }
    }
}

