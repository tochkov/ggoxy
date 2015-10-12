package com.goxapps.goxreader.filechooser;

import android.content.Context;
import android.content.SharedPreferences;

import com.goxapps.goxreader.GoxApp;
import com.goxapps.goxreader.R;
import com.goxapps.goxreader.filechooser.model.SmartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by tochkov.
 */
public class FileManager {

    public static final String KEY_SORTED_BY = "FileManager#KEY_SORTED_BY";

    public static final int SORT_BY_NAME = 1818;
    public static final int SORT_BY_DATE = 1919;

    private int sortingCriteria;

    private ArrayList<SmartFile> fileList;

    private ArrayList<SmartFile> filesNewArrival;
    private ArrayList<SmartFile> filesRedundant;

    private static FileManager instance;

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    private FileManager() {
        sortingCriteria = getCurrentSorting();
        fileList = new ArrayList<>(SmartFile.listAll(SmartFile.class));
        sortFilesByCriteria(sortingCriteria);

        filesNewArrival = new ArrayList<>();
        filesRedundant = new ArrayList<>();
    }

    public void updateFileRecords(ConcurrentLinkedQueue<SmartFile> newFiles) {

        filesNewArrival.addAll(newFiles);
        filesRedundant.addAll(fileList);

        filesNewArrival.removeAll(fileList);
        filesRedundant.removeAll(newFiles);

        fileList.addAll(filesNewArrival);
        fileList.removeAll(filesRedundant);

        // Sugar SQL persistence
        for (SmartFile file : filesNewArrival)
            file.save();

        sortFilesByCriteria(sortingCriteria);
    }


    public void sortFilesByCriteria(int criteria) {
        if (criteria == SORT_BY_DATE) {
            sortFilesByDate();
        } else if (criteria == SORT_BY_NAME) {
            sortFilesByName();
        } else {
            throw new UnsupportedOperationException("No such sorting criteria");
        }

        saveCurrentSorting(criteria);
    }

    private void sortFilesByName() {
        Collections.sort(fileList, new Comparator<SmartFile>() {
            @Override
            public int compare(SmartFile lhs, SmartFile rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
    }

    private void sortFilesByDate() {
        Collections.sort(fileList, new Comparator<SmartFile>() {
            @Override
            public int compare(SmartFile lhs, SmartFile rhs) {
                long result = rhs.getLastModifiedDate() - lhs.getLastModifiedDate();

                if (result > 0)
                    return 1;
                else if (result < 0)
                    return -1;
                else
                    return 0;

            }
        });
    }

    public int getCurrentSorting() {
        SharedPreferences prefs = GoxApp.get().getSharedPreferences(GoxApp.get().getString(R.string.app_shared_prefs), Context.MODE_PRIVATE);
        return prefs.getInt(KEY_SORTED_BY, SORT_BY_DATE); // by date by default
    }

    public void saveCurrentSorting(int byCriteria) {
        sortingCriteria = byCriteria;
        SharedPreferences prefs = GoxApp.get().getSharedPreferences(GoxApp.get().getString(R.string.app_shared_prefs), Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_SORTED_BY, sortingCriteria).apply();
    }

    public ArrayList<SmartFile> getFilesRedundant() {
        return filesRedundant;
    }

    public ArrayList<SmartFile> getFiles() {
        return fileList;
    }

    public SmartFile getFile(int position) {
        return fileList != null ? fileList.get(position) : null;
    }


}
