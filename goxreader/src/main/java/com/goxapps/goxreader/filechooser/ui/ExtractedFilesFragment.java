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

import com.goxapps.goxreader.R;
import com.goxapps.goxreader.filechooser.FileManager;
import com.goxapps.goxreader.filechooser.model.SmartFile;
import com.goxapps.goxreader.filechooser.services.ExtractFilesService;
import com.goxapps.goxreader.filechooser.services.GetBitmapCoversService;
import com.goxapps.goxreader.util.AppUtils;

import java.util.ArrayList;

/**
 * Created by tochkov.
 */
public class ExtractedFilesFragment extends Fragment {

    private RecyclerView recyclerView;
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

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerExtractedFiles);

        layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        if (!fileList.isEmpty()) {
            adapter = new ExtractedFilesAdapter(getActivity(), fileList);
            recyclerView.setAdapter(adapter);

            startBitmapCoversService();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(fileScanReceiver, new IntentFilter(ExtractFilesService.FILE_SCAN_COMPLETE));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bitmapLoadedReceiver, new IntentFilter(GetBitmapCoversService.BITMAP_LOADED));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(fileScanReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(bitmapLoadedReceiver);
    }

    private BroadcastReceiver fileScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter == null) {
                adapter = new ExtractedFilesAdapter(getActivity(), fileList);
                recyclerView.setAdapter(adapter);

            } else {
                adapter.notifyDataSetChanged();
            }
            startBitmapCoversService();
        }
    };

    private BroadcastReceiver bitmapLoadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null) {
                int position = intent.getIntExtra(GetBitmapCoversService.KEY_BITMAP_LOADED_POSITION, -1);

                /**
                 * if loaded from cache - notifyItem /to prevent ugly grid flashing/
                 * if generated from pdf - notifyItem /for smoother animation/
                 */
                if (position > -1) {
                    adapter.notifyItemChanged(position);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void startBitmapCoversService() {
        Intent intent = new Intent(getActivity(), GetBitmapCoversService.class);
        intent.putExtra(GetBitmapCoversService.KEY_BITMAP_WIDTH, AppUtils.getScreenWidth(getActivity()) / 3); // approximate cover width
        getActivity().startService(intent);
    }


}
