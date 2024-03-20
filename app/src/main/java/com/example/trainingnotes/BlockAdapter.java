package com.example.trainingnotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {
    private List<Block> blockList;
    private Context context;
    private CollectionReference blocksCollection;
    private OnDeleteClickListener onDeleteClickListener;
    private OnBlockClickListener onBlockClickListener;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(String blockName);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public BlockAdapter(List<Block> blockList, CollectionReference blocksCollection) {
        this.blockList = blockList;
        this.blocksCollection = blocksCollection;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block, parent, false);
        return new BlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
        Block block = blockList.get(position);
        holder.bind(block);

        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(block.getBlockName());
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (onBlockClickListener != null) {
                listener.onItemClick(block.getBlockName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return blockList.size();
    }
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(String position);
    }

    // Método para establecer el listener
    public void setOnBlockClickListener(OnBlockClickListener listener) {
        this.onBlockClickListener = listener;
    }
    public interface OnBlockClickListener {
        void onBlockClick(String blockName);
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder {
        TextView blockNameTextView;
        ImageView deleteButton;

        public BlockViewHolder(@NonNull View itemView) {
            super(itemView);
            blockNameTextView = itemView.findViewById(R.id.blockNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Block block) {
            blockNameTextView.setText(block.getBlockName());
        }
    }

}

