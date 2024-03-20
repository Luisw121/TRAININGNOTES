package com.example.trainingnotes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BlockDetailFragment extends Fragment {
    public static BlockDetailFragment newInstance(String blockName){
        BlockDetailFragment fragment = new BlockDetailFragment();
        Bundle args = new Bundle();
        args.putString("blockName", blockName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_detail, container, false);

        String blockname = getArguments().getString("blockName");

        TextView blockNameTextView = view.findViewById(R.id.blockDetailNameTextView);
        blockNameTextView.setText(blockname);

        return view;
    }
}