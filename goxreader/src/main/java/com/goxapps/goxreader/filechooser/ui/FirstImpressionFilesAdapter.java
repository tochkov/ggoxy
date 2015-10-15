package com.goxapps.goxreader.filechooser.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.goxapps.goxreader.filechooser.model.SmartFile;

import java.util.ArrayList;

/**
 * Created by tochkov.
 */
public class FirstImpressionFilesAdapter extends ExtractedFilesAdapter {
    public FirstImpressionFilesAdapter(Context context, ArrayList<SmartFile> sourceSet) {
        super(context, sourceSet);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {
        super.onBindViewHolder(recyclerHolder, position);
    }
}
