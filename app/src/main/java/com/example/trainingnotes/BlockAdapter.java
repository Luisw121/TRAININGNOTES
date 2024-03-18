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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder> {
    private List<Block> blockList;
    private Context context;
    private DatabaseReference databaseReference;

    public BlockAdapter( List<Block> blockList, DatabaseReference databaseReference) {
        this.blockList = blockList;
        this.databaseReference = databaseReference;
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

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBlock(holder.getAdapterPosition());
            }
        });

    }

    private void deleteBlock(int position) {
        Block block = blockList.get(position);
        String blockName = block.getBlockName();

        blockList.remove(position);
        notifyDataSetChanged();

        databaseReference.child(blockName).removeValue();
    }

    @Override
    public int getItemCount() {
        return blockList.size();
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

