package com.example.trainingnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarioEjercicioAdapter extends RecyclerView.Adapter<CalendarioEjercicioAdapter.CalendarioEjercicioViewHolder> {

    private List<String> nombresEjercicios;

    public CalendarioEjercicioAdapter(List<String> nombresEjercicios) {
        this.nombresEjercicios = nombresEjercicios;
    }

    @NonNull
    @Override
    public CalendarioEjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new CalendarioEjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarioEjercicioViewHolder holder, int position) {
        String ejercicioName = nombresEjercicios.get(position);
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

        public void bind(String ejercicioName) {
            textViewNombreEjercicio.setText(ejercicioName);
        }
    }
}

