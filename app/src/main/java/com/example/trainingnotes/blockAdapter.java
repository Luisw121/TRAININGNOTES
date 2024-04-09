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
    private OnDeleteClickListener onDeleteClickListener;


    public blockAdapter(List<block> elementList, CollectionReference elementsCollection) {
        this.elementList = elementList;
        this.elementsCollection = elementsCollection;
    }

    @NonNull
    @Override
    public ElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block, parent, false);
        return new ElementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementViewHolder holder, int position) {
        block element = elementList.get(position);
        holder.bind(element);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la posición del elemento
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Eliminar el elemento de la lista y notificar al adaptador
                    elementList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }


    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String position);
    }

    public static class ElementViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        ImageView deleteButton;

        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.blockNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(block element) {
            textView.setText(element.getName());
        }
    }
}
