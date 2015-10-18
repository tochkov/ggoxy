package com.goxapps.goxreader.filechooser.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.goxapps.goxreader.R;
import com.goxapps.goxreader.filechooser.FileManager;
import com.goxapps.goxreader.filechooser.UpdateFilesService;
import com.goxapps.goxreader.filechooser.model.SmartFile;

import java.util.ArrayList;

/**
 * Created by tochkov.
 */
public class ExtractedFilesFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyView;
    private ProgressBar progressBar;
    private ArrayList<SmartFile> fileList;
    private ExtractedFilesAdapter adapter;
    private GridLayoutManager layoutManager;

    public ExtractedFilesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileList = FileManager.getInstance().getFiles();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_chooser, container, false);

        emptyView = rootView.findViewById(R.id.textViewNoResults);
        progressBar = (ProgressBar) rootView.findViewById(R.id.loadingFilesProgress);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerExtractedFiles);

        layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        if (fileList.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            adapter = new ExtractedFilesAdapter(getActivity(), fileList);
            recyclerView.setAdapter(adapter);

        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(fileScanReceiver, new IntentFilter(UpdateFilesService.FILE_SCAN_COMPLETE));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(coverReadyReceiver, new IntentFilter(UpdateFilesService.COVER_READY));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(fileScanReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(coverReadyReceiver);
    }

    private BroadcastReceiver fileScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            progressBar.setVisibility(View.GONE);

            if (fileList.isEmpty()) {
                emptyView.setVisibility(View.VISIBLE);
                return;
            }

            if (adapter == null) {
                adapter = new ExtractedFilesAdapter(getActivity(), fileList);
                recyclerView.setAdapter(adapter);

            } else {
                adapter.notifyDataSetChanged();
            }
        }
    };

    private BroadcastReceiver coverReadyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra(UpdateFilesService.KEY_COVER_READY, -1);

            if (adapter != null && position > -1) {
//                adapter.notifyItemChanged(position);
                adapter.notifyDataSetChanged();
            }
        }
    };

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}
