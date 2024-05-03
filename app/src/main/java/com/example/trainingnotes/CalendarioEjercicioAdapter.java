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
    private CollectionReference ejerciciosCollectionCal;
    private OndeleteClickListener ondeleteClickListener;
    private OnEjercicioClickListener onEjercicioClickListener;

    public CalendarioEjercicioAdapter(List<CalendarioEjercicios> ejerciciosList, CollectionReference ejerciciosCollectionCal) {
        this.ejerciciosList = ejerciciosList;
        this.ejerciciosCollectionCal = ejerciciosCollectionCal;
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

        holder.deleteButtonEjercicio.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                CalendarioEjercicios ejercicioToDelete = ejerciciosList.get(adapterPosition);
                ondeleteClickListener.onDeleteClick(ejercicioToDelete.getNombre());
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (onEjercicioClickListener != null) {
                onEjercicioClickListener.onEjercicioClick(ejercicio.getNombre());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ejerciciosList.size();
    }
    public void setOndeleteClickListener(OndeleteClickListener listener) {
        this.ondeleteClickListener = listener;
    }
    public interface OndeleteClickListener {
        void onDeleteClick(String ejercicioName);
    }
    public void setOnEjercicioClickListener(OnEjercicioClickListener listener) {
        this.onEjercicioClickListener = listener;
    }
    public interface OnEjercicioClickListener {
        void onEjercicioClick(String blockName);
    }

    public static class CalendarioEjercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseNameTextView;
        private ImageView deleteButtonEjercicio;

        public CalendarioEjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.blockNameTextViewDays);
            deleteButtonEjercicio = itemView.findViewById(R.id.deleteButtonDays);
        }

        public void bind(CalendarioEjercicios ejercicio) {
            exerciseNameTextView.setText(ejercicio.getNombre());
        }
    }
}


