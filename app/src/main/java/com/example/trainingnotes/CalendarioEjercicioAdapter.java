package com.example.trainingnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.util.Collections;
import java.util.List;

public class CalendarioEjercicioAdapter extends RecyclerView.Adapter<CalendarioEjercicioAdapter.CalendarioEjercicioViewHolder> {

    private List<CalendarioEjercicios> ejerciciosList;

    public CalendarioEjercicioAdapter(List<CalendarioEjercicios> ejerciciosList) {
        this.ejerciciosList = ejerciciosList;
    }

    @NonNull
    @Override
    public CalendarioEjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new CalendarioEjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarioEjercicioViewHolder holder, int position) {
        CalendarioEjercicios ejercicio = ejerciciosList.get(position);
        holder.bind(ejercicio);
    }

    @Override
    public int getItemCount() {
        return ejerciciosList.size();
    }

    public static class CalendarioEjercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseNameTextView;

        public CalendarioEjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.blockNameTextViewDays);
        }

        public void bind(CalendarioEjercicios ejercicio) {
            exerciseNameTextView.setText(ejercicio.getNombre());
        }
    }
}


