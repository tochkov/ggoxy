package com.goxapps.goxreader.filechooser.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goxapps.goxreader.R;
import com.goxapps.goxreader.filechooser.model.SmartFile;
import com.goxapps.goxreader.reader.FullReaderActivity;

import java.util.ArrayList;

/**
 * Created by tochkov.
 */
public class ExtractedFilesAdapter extends Adapter {

    private ArrayList<SmartFile> sourceSet;
    private Context context;

    public ExtractedFilesAdapter(Context context, ArrayList<SmartFile> sourceSet) {
        this.context = context;
        this.sourceSet = sourceSet;

        setHasStableIds(true);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        View rootView;

        TextView tvFileName;
        ImageView ivFileCover;

        public ViewHolder(View itemView) {
            super(itemView);

            rootView = itemView;
            tvFileName = (TextView) itemView.findViewById(R.id.grid_file_name);
            ivFileCover = (ImageView) itemView.findViewById(R.id.grid_cover_frame);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_extracted_files, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, final int position) {

        final ViewHolder holder = (ViewHolder) recyclerHolder;
        final SmartFile file = sourceSet.get(position);

        holder.tvFileName.setText(file.getName());

        holder.ivFileCover.setImageBitmap(file.getBitmapCover());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, FullReaderActivity.class);
                intent.putExtra(FullReaderActivity.KEY_SELECTED_FILE_POSITION, position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sourceSet.size();
    }

    @Override
    public long getItemId(int position) {
        return sourceSet.get(position).getId();
    }
}
