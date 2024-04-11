package com.example.trainingnotes;
// EjerciciosAdapter.java
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListaEjerciciosAdapter extends RecyclerView.Adapter<ListaEjerciciosAdapter.EjercicioViewHolder> {

    private Context context;
    private List<ListaEjercicio> ejercicios;

    public ListaEjerciciosAdapter(Context context, List<ListaEjercicio> ejercicios) {
        this.context = context;
        this.ejercicios = ejercicios;
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ejercicios, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        ListaEjercicio ejercicio = ejercicios.get(position);
        holder.nombreTextView.setText(ejercicio.getNombre());
        holder.imagenImageView.setImageResource(ejercicio.getImagen());
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    static class EjercicioViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageView imagenImageView;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textEjecicios);
            imagenImageView = itemView.findViewById(R.id.imageEjercicios);
        }
    }
}
