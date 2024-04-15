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


public class EjercicioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SELECTION = 1;
    private static final int VIEW_TYPE_NORMAL = 2;

    private List<Ejercicio> ejercicios;
    private CollectionReference ejerciciosCollection;
    private OnDeleteClickListener onDeleteClickListenerEjercicioBlock;
    private OnEjercicioBlockListener OnEjercicioBlockListener;
    public void setOnEjercicioBlockListener(OnEjercicioBlockListener listener) {
        this.OnEjercicioBlockListener = listener;
    }
    public interface OnEjercicioBlockListener{
        void onjercicioClickBLock(String ejercicioName);
    }

    private onEjercicioCliclListener listener;
    public interface onEjercicioCliclListener {
        void onEjercicioClick(String ejercicioName);
    }
    public void setOnEjercicioClickListener(onEjercicioCliclListener listener){this.listener = listener;}
    public void setOnDeleteClickListenerEjercicioBlock(OnDeleteClickListener listener) {
        this.onDeleteClickListenerEjercicioBlock = listener;
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(String position);
    }

    private Context context;
    private OnExerciseClickListener onExerciseClickListener;
    private boolean isSelectionMode;

    public EjercicioAdapter(List<Ejercicio> listaEjercicios, CollectionReference ejerciciosCollection, boolean isSelectionMode) {
        this.ejercicios = listaEjercicios;
        this.ejerciciosCollection = ejerciciosCollection;
        this.isSelectionMode = isSelectionMode;
    }

    @Override
    public int getItemViewType(int position) {
        return isSelectionMode ? VIEW_TYPE_SELECTION : VIEW_TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_SELECTION) {
            View view = inflater.inflate(R.layout.item_ejercicios, parent, false);
            return new EjercicioViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_ejercicios2, parent, false);
            return new EjercicioViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Ejercicio ejercicio = ejercicios.get(position);

        if (isSelectionMode) {
            EjercicioViewHolder viewHolder = (EjercicioViewHolder) holder;
            viewHolder.nombreTextView.setText(ejercicio.getNombre());
            viewHolder.imagenImageView.setImageResource(ejercicio.getImagen());
        } else {
            EjercicioViewHolder2 viewHolder = (EjercicioViewHolder2) holder;
            viewHolder.nombreTextView.setText(ejercicio.getNombre());
            viewHolder.imagenImageView.setImageResource(ejercicio.getImagen());
        }
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

    // ViewHolder para el modo de selección
    static class EjercicioViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageView imagenImageView;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textEjecicios);
            imagenImageView = itemView.findViewById(R.id.imageEjercicios);
        }
    }

    // ViewHolder para el modo normal
    static class EjercicioViewHolder2 extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageView imagenImageView;

        public EjercicioViewHolder2(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.textEjecicios2);
            imagenImageView = itemView.findViewById(R.id.imageEjercicios2);
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