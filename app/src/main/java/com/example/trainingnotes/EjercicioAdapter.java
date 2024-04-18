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
    private OnDeleteClickListener onDeleteClickListenerEjercicio;
    private OnEjercicioClickListener onEjercicioClickListener;
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

        holder.deleteButtonEjercicio.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Ejercicio ejercicioToDelete = ejerciciosList.get(adapterPosition);
                onDeleteClickListenerEjercicio.onDeleteClick(ejercicioToDelete.getNombre());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Ejercicio ejercicioClicked = ejerciciosList.get(adapterPosition);
                onEjercicioClickListener.onEjercicioClick(ejercicioClicked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ejerciciosList.size();
    }
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListenerEjercicio = listener;
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(String ejercicioName);
    }

    public void setOnEjercicioClickListener(OnEjercicioClickListener listener) {
        this.onEjercicioClickListener = listener;
    }

    public interface OnEjercicioClickListener {
        void onEjercicioClick(Ejercicio ejercicio);
    }

    public class EjercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreEjercicio;
        private ImageView deleteButtonEjercicio;
        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreEjercicio = itemView.findViewById(R.id.blockNameTextViewDays);
            deleteButtonEjercicio = itemView.findViewById(R.id.deleteButtonDays);
        }

        public void bind(Ejercicio ejercicio) {
            textViewNombreEjercicio.setText(ejercicio.getNombre());
        }
    }
}