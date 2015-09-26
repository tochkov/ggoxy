package com.goxapps.goxreader.filechooser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goxapps.goxreader.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ExtractedFilesFragment extends Fragment {


    private GridRecyclerView recyclerView;

    public ExtractedFilesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_chooser, container, false);

        recyclerView = (GridRecyclerView) rootView.findViewById(R.id.recyclerExtractedFiles);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return rootView;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            recyclerView.setAdapter(new ExtractedFilesAdapter(ExtractedFilesManager.getInstance().getFiles()));
            recyclerView.scheduleLayoutAnimation();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(ExtractFilesService.FILE_SCAN_COMPLETE));
    }
}
