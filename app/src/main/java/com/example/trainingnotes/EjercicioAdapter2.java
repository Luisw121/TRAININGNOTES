package com.example.trainingnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.util.List;

public class EjercicioAdapter2 extends RecyclerView.Adapter<EjercicioAdapter2.EjercicioViewHolder> {

    private List<Ejercicio2> ejerciciosList;
    private OnDeleteClickListener onDeleteClickListenerEjercicio;
    private OnEjercicioClickListener onEjercicioClickListener;
    private CollectionReference ejercicios2Ref;

    public EjercicioAdapter2(List<Ejercicio2> ejerciciosList, CollectionReference ejercicios2Ref) {
        this.ejerciciosList = ejerciciosList;
        this.ejercicios2Ref = ejercicios2Ref;
    }

    // ViewHolder para cada elemento de la lista
    public class EjercicioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreEjercicio;
        private ImageView deleteButtonEjercicio;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreEjercicio = itemView.findViewById(R.id.blockNameTextViewDays);
            deleteButtonEjercicio = itemView.findViewById(R.id.deleteButtonDays);
        }

        public void bind(Ejercicio2 ejercicio) {
            textViewNombreEjercicio.setText(ejercicio.getNombre());
        }
    }

    // Métodos de RecyclerView.Adapter
    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioViewHolder holder, int position) {
        Ejercicio2 ejercicio = ejerciciosList.get(position);
        holder.bind(ejercicio);

        // Manejar clics en el botón de eliminar
        holder.deleteButtonEjercicio.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Ejercicio2 ejercicioToDelete = ejerciciosList.get(adapterPosition);
                onDeleteClickListenerEjercicio.onDeleteClick(ejercicioToDelete.getNombre());
            }
        });

        // Manejar clics en el elemento de la lista
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Ejercicio2 ejercicioClicked = ejerciciosList.get(adapterPosition);
                onEjercicioClickListener.onEjercicioClick(ejercicioClicked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ejerciciosList.size();
    }

    // Interfaz para manejar clics en el botón de eliminar
    public interface OnDeleteClickListener {
        void onDeleteClick(String ejercicioName);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListenerEjercicio = listener;
    }

    // Interfaz para manejar clics en el elemento de la lista
    public interface OnEjercicioClickListener {
        void onEjercicioClick(Ejercicio2 ejercicio);
    }

    public void setOnEjercicioClickListener(OnEjercicioClickListener listener) {
        this.onEjercicioClickListener = listener;
    }
}

