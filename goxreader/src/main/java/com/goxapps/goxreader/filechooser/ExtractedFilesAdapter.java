package com.goxapps.goxreader.filechooser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goxapps.goxreader.FullReaderActivity;
import com.goxapps.goxreader.R;

import java.util.ArrayList;

/**
 * Created by tochkov.
 */
public class ExtractedFilesAdapter extends Adapter {

    private ArrayList<FileWrapper> sourceSet;
    private Context context;

    private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();

    public ExtractedFilesAdapter(Context context, ArrayList<FileWrapper> sourceSet) {
        this.context = context;
        this.sourceSet = sourceSet;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFileName;
        ImageView ivFileCover;

        public View getRootView() {
            return rootView;
        }

        private View rootView;

        public ViewHolder(View itemView) {
            super(itemView);

            rootView = itemView;
            tvFileName = (TextView) itemView.findViewById(R.id.grid_file_name);
            ivFileCover = (ImageView) itemView.findViewById(R.id.grid_cover_frame);
        }

        private void setText(String text) {
            tvFileName.setText(text);
        }

        public void setIvFileCover(Bitmap bitmap) {
            ivFileCover.setImageDrawable(new BitmapDrawable(bitmap));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_extracted_files, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, final int position) {

        final ViewHolder holder = (ViewHolder) recyclerHolder;
        final FileWrapper file = sourceSet.get(position);

        holder.setText(file.getName());
        holder.setIvFileCover(file.getBitmapCover());


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
}
