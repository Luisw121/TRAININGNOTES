package com.example.trainingnotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.util.List;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ElementViewHolder> {
    private List<Element> elementList;
    private CollectionReference elementsCollection;

    public ElementAdapter(List<Element> elementList, CollectionReference elementsCollection) {
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
        Element element = elementList.get(position);
        holder.bind(element);
    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }

    public static class ElementViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public ElementViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.blockNameTextView);
        }

        public void bind(Element element) {
            textView.setText(element.getName());
        }
    }
}
