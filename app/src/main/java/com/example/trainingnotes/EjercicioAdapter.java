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
//public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.EjercicioViewHolder>
public class EjercicioAdapter {
    /*
    private List<Ejercicio> ejercicioList;
    private CollectionReference ejerciciosCollection;
    private OnDeleteCLickListenerEj onDeleteCLickListenerEj;
    private OnEjercicioBlockClickListener onEjercicioBlockClickListener;
    private onEjercicioClickListener listener;

    public interface onEjercicioClickListener {
        void onEjercicioClick(String ejercicioName);
    }
    public void setEjercicioClickListener(onEjercicioClickListener listener) {this.listener = listener;}

    public EjercicioAdapter(List<Ejercicio> ejercicioList, CollectionReference ejerciciosCollection) {
        this.ejercicioList = ejercicioList;
        this.ejerciciosCollection = ejerciciosCollection;
    }

    @NonNull
    @Override
    public EjercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new EjercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EjercicioAdapter.EjercicioViewHolder holder, int position) {
        Ejercicio elemento = ejercicioList.get(position);
        holder.bind(elemento);

        holder.deleteButtonEjercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adpterposition = holder.getAdapterPosition();
                if (adpterposition != RecyclerView.NO_POSITION) {
                    ejercicioList.remove(adpterposition);
                    onDeleteCLickListenerEj.onDeleteClickEj(elemento.getEjercicioName());
                    notifyItemRemoved(adpterposition);
                }
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (onEjercicioBlockClickListener != null) {
                listener.onEjercicioClick(elemento.getEjercicioName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ejercicioList.size();
    }

    public void setOnDeleteCLickListenerEj(OnDeleteCLickListenerEj listenerEj) {
        this.onDeleteCLickListenerEj = listenerEj;
    }
    public interface OnDeleteCLickListenerEj {
        void onDeleteClickEj(String position);
    }
    //metodo para establecer el listener
    public void setOnEjercicioBlockClickListener(OnEjercicioBlockClickListener listener) {
        this.onEjercicioBlockClickListener = listener;
    }
    public interface OnEjercicioBlockClickListener {
        void onEjercicioClickBlock(String ejercicioName);
    }

    public class EjercicioViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEjercicio;
        ImageView deleteButtonEjercicio;

        public EjercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEjercicio = itemView.findViewById(R.id.blockDetailNameTextViewDays);
            deleteButtonEjercicio = itemView.findViewById(R.id.deleteButtonDays);
        }
        public void bind(Ejercicio elemento) {
            textViewEjercicio.setText(elemento.getEjercicioName());
        }
    }
     */

}
