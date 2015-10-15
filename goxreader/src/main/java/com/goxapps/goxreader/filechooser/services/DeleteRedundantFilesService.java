package com.goxapps.goxreader.filechooser.services;

import android.app.IntentService;
import android.content.Intent;

import com.goxapps.goxreader.filechooser.FileManager;
import com.goxapps.goxreader.filechooser.model.SmartFile;

import java.io.File;

/**
 * Created by tochkov.
 * <p/>
 * A service to delete unused SmartFiles from the DB, and redundant cached jpg covers
 */
public class DeleteRedundantFilesService extends IntentService {

    public DeleteRedundantFilesService() {
        super("DeleteRedundantFilesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        for (SmartFile file : FileManager.getInstance().getFilesRedundant()) {
            file.delete();
            new File(file.getCoverFilePath()).delete();
        }

        FileManager.getInstance().getFilesRedundant().clear();

        stopSelf();
    }


}
