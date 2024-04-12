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


public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder>{
    private List<Ejercicio> ejercicios;
    private Context context;
    private CollectionReference ejerciciosCollection;
    private OnExerciseClickListener onExerciseClickListener;


    public EjercicioAdapter( List<Ejercicio> listaEjercicios, CollectionReference ejerciciosCollection) {
        this.ejercicios = listaEjercicios;
        this.ejerciciosCollection = ejerciciosCollection;
    }
    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicios, parent, false);
        return new EjercicioViewHolder(view);
    }@Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        Ejercicio ejercicio = ejercicios.get(position);

        holder.nombreTextView.setText(ejercicio.getNombre());
        holder.imagenImageView.setImageResource(ejercicio.getImagen());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExerciseClickListener.onExerciseClick(ejercicio);
            }
        });

    }
    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    // Método para establecer el listener desde fuera de la clase
    public void setOnExerciseClickListener(OnExerciseClickListener listener) {
        this.onExerciseClickListener = listener;
    }

    // Interfaz para el listener
    public interface OnExerciseClickListener {
        void onExerciseClick(Ejercicio ejercicio);
    }

    // ViewHolder
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

/*
<EditText
        android:id="@+id/editTextSearch"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Buscar ejercicio..."
        android:textColor="@color/black"
        android:textColorHint="@color/white"
        android:imeOptions="actionDone"
        android:inputType="text"
        android:visibility="invisible"
        tools:layout_editor_absoluteX="-20dp"
        tools:layout_editor_absoluteY="706dp" />
 */