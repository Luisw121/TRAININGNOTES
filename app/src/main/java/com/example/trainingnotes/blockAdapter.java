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

public class blockAdapter extends RecyclerView.Adapter<blockAdapter.ElementViewHolder> {
    private List<block> elementList;
    private CollectionReference elementsCollection;
    private OnDeleteClickListener onDeleteClickListenerBlock;
    private OnElemntBlockClickListener onElemntBlockClickListener;
    private onElementClickListener listener;
    public interface onElementClickListener {
        void onElementClick(String elementName);
    }
    public void setOnElementClickListener(onElementClickListener listener) {this.listener = listener;}
    public blockAdapter(List<block> elementList, CollectionReference elementsCollection) {
        this.elementList = elementList;
        this.elementsCollection = elementsCollection;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_days, parent, false);
        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        block element = elementList.get(position);
        holder.bind(element);

        holder.deleteButtonDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la posición del elemento
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Eliminar el elemento visualmente
                    elementList.remove(adapterPosition);
                    onDeleteClickListenerBlock.onDeleteClick(element.getName());
                    notifyItemRemoved(adapterPosition);

                }
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (onElemntBlockClickListener != null) {
                listener.onElementClick(element.getName());
            }

        });
    }
    @Override
    public int getItemCount() {
        return elementList.size();
    }
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListenerBlock = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String position);
    }

    //metodo para establecer el listener
    public void setOnElemntBlockClickListener(OnElemntBlockClickListener listener) {
        this.onElemntBlockClickListener = listener;
    }
    public interface OnElemntBlockClickListener {
        void onElementClickBlock(String elementName);
    }

    public static class ElementViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDAy;
        ImageView deleteButtonDay;

        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDAy = itemView.findViewById(R.id.blockNameTextViewDays);
            deleteButtonDay = itemView.findViewById(R.id.deleteButtonDays);
        }

        public void bind(block element) {
            textViewDAy.setText(element.getName());
        }
    }
}
