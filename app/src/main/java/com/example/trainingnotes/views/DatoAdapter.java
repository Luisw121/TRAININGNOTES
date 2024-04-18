package com.example.trainingnotes.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainingnotes.DatoInicial;
import com.example.trainingnotes.R;

import java.util.List;

public class DatoAdapter extends RecyclerView.Adapter<DatoAdapter.DatoViewHolder> {
    private List<DatoInicial> serieDatosList;
    private int posicionSeleccionada = RecyclerView.NO_POSITION;

    private OnSerieClickListener onSerieClickListener;
    public interface OnSerieClickListener {
        void onSerieDeleteClick(int position);
        void onSerieAddClick();
    }

    public DatoAdapter(List<DatoInicial> serieDatosList) {
        this.serieDatosList = serieDatosList;
    }public void setOnSerieClickListener(OnSerieClickListener listener) {
        this.onSerieClickListener = listener;
    }

    public void setPosicionSeleccionada(int posicion) {
        posicionSeleccionada = posicion;
        notifyDataSetChanged();
    }

    public int getPosicionSeleccionada() {
        return posicionSeleccionada;
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

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    setPosicionSeleccionada(clickedPosition);
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION && onSerieClickListener != null) {
                    onSerieClickListener.onSerieDeleteClick(clickedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return serieDatosList.size();
    }

    public class DatoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewReps;
        private TextView textViewPeso;
        private TextView textViewRpe;
        private ImageView deleteButton;
        private ImageView addButton;
        public DatoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewReps = itemView.findViewById(R.id.textViewReps);
            textViewPeso = itemView.findViewById(R.id.textViewPeso);
            textViewRpe = itemView.findViewById(R.id.textViewRpe);
            deleteButton = itemView.findViewById(R.id.deleteButton1);
            addButton = itemView.findViewById(R.id.addButton);

        }

        public void bind(DatoInicial dato) {
            textViewReps.setText(String.valueOf(dato.getRepeticiones()));
            textViewPeso.setText(String.valueOf(dato.getPeso()));
            textViewRpe.setText(String.valueOf(dato.getRpe()));
        }
    }
}
