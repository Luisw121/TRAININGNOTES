package com.example.trainingnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MostratCalendarioDatsAdapter extends RecyclerView.Adapter<MostratCalendarioDatsAdapter.MostrarCalendarioDatsViewHolder> {
    private List<MostrarCalendarioDats> serieDatosList;
    private OnserieClickListener onserieClickListener;
    public void setOnDeleteClickListener(OnserieClickListener listener) {
        this.onserieClickListener = listener;
    }
    public interface OnserieClickListener {
        void onSerieDeleteClick(int position);
        void onSerieAddClick();
    }
    public MostratCalendarioDatsAdapter(List<MostrarCalendarioDats> serieDatosList, OnserieClickListener listener) {
        this.serieDatosList = serieDatosList;
        this.onserieClickListener = listener;
    }
    @NonNull
    @Override
    public MostrarCalendarioDatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dato, parent, false);
        return new MostrarCalendarioDatsViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MostrarCalendarioDatsViewHolder holder, int position) {
        MostrarCalendarioDats mostrarCalendarioDats = serieDatosList.get(position);
        holder.bin(mostrarCalendarioDats);
    }

    @Override
    public int getItemCount() {
        return serieDatosList.size();
    }

    public class MostrarCalendarioDatsViewHolder extends RecyclerView.ViewHolder {
        private EditText textViewReps;
        private EditText textViewPeso;
        private EditText textViewRpe;
        public MostrarCalendarioDatsViewHolder(@NonNull View itemView){
            super(itemView);
            textViewReps = itemView.findViewById(R.id.textViewReps);
            textViewPeso = itemView.findViewById(R.id.textViewPeso);
            textViewRpe = itemView.findViewById(R.id.textViewRpe);
        }

        public void bin(MostrarCalendarioDats mostrarCalendarioDats) {
            textViewReps.setText(String.valueOf(mostrarCalendarioDats.getRepeticiones()));
            textViewPeso.setText(String.valueOf(mostrarCalendarioDats.getPeso()));
            textViewRpe.setText(String.valueOf(mostrarCalendarioDats.getRpe()));
        }
    }
}
