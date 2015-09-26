package com.goxapps.goxreader.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by tochkov.
 */
public class ExtractedFilesManager {

    private ArrayList<File> fileList;

    private ExtractedFilesManager() {
        fileList = new ArrayList<>();
    }

    private static ExtractedFilesManager instance;

    public static ExtractedFilesManager getInstance() {
        if (instance == null) {
            instance = new ExtractedFilesManager();
        }
        return instance;
    }

    public void initAll(ConcurrentLinkedQueue<File> newFiles) {

        fileList.clear();
        fileList.addAll(newFiles);
    }

    public ArrayList<File> getFiles() {
        return fileList;
    }


    public void sortByCriteria() {
        //TODO
    }

    public void deleteFileFromDevice() {
        //TODO
    }

    public void getInfoForFile() {
        //TODO
    }

}
