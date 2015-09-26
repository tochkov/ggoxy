package com.goxapps.goxreader.filechooser;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goxapps.goxreader.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tochkov.
 */
public class ExtractedFilesAdapter extends Adapter {

    private ArrayList<File> sourceSet;

    public ExtractedFilesAdapter(ArrayList<File> sourceSet) {
        this.sourceSet = sourceSet;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvFileName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvFileName = (TextView) itemView.findViewById(R.id.grid_file_name);
        }

        private void setText(String text) {
            tvFileName.setText(text);
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_extracted_files, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {

        ViewHolder holder = (ViewHolder) recyclerHolder;

        holder.setText(sourceSet.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return sourceSet.size();
    }
}
