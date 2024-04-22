package com.example.trainingnotes.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainingnotes.DatoInicial;
import com.example.trainingnotes.R;

import java.util.List;

public class DatoAdapter extends RecyclerView.Adapter<DatoAdapter.DatoViewHolder> {
    private List<DatoInicial> serieDatosList;
    private OnSerieClickListener onSerieClickListener;
    public void setOnDeleteClickListener(OnSerieClickListener listener) {
        this.onSerieClickListener = listener;
    }


    public interface OnSerieClickListener {
        void onSerieDeleteClick(int position);
        void onSerieAddClick();
    }

    public DatoAdapter(List<DatoInicial> serieDatosList, OnSerieClickListener listener) {
        this.serieDatosList = serieDatosList;
        this.onSerieClickListener = listener;
    }

    @NonNull
    @Override
    public DatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dato, parent, false);
        return new DatoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DatoViewHolder holder, int position) {
        DatoInicial dato = serieDatosList.get(position);
        holder.bind(dato);
    }

    @Override
    public int getItemCount() {
        return serieDatosList.size();
    }

    public class DatoViewHolder extends RecyclerView.ViewHolder {
        private EditText textViewReps;
        private EditText textViewPeso;
        private EditText textViewRpe;
        private ImageView deleteButton;

        public DatoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReps = itemView.findViewById(R.id.textViewReps);
            textViewPeso = itemView.findViewById(R.id.textViewPeso);
            textViewRpe = itemView.findViewById(R.id.textViewRpe);

            // Buscar deleteButton en el diseño principal
            deleteButton = itemView.findViewById(R.id.deleteButton1);
            if (deleteButton == null) {
                // Si deleteButton no se encuentra en el diseño principal, buscar en el diseño específico
                deleteButton = itemView.findViewById(R.id.deleteButton1);
            }

            // Aquí puedes configurar el click listener para deleteButton
            if (deleteButton != null) {
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // Obtener la posición del elemento
                        if (position != RecyclerView.NO_POSITION) {
                            onSerieClickListener.onSerieDeleteClick(position);
                        }
                    }
                });
            }
        }



        public void bind(DatoInicial dato) {
            textViewReps.setText(String.valueOf(dato.getRepeticiones()));
            textViewPeso.setText(String.valueOf(dato.getPeso()));
            textViewRpe.setText(String.valueOf(dato.getRpe()));
        }
    }
}

