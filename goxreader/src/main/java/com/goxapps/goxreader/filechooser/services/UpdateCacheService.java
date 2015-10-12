package com.goxapps.goxreader.filechooser.services;

import android.app.IntentService;
import android.content.Intent;

import com.goxapps.goxreader.filechooser.FileManager;
import com.goxapps.goxreader.filechooser.model.SmartFile;

import java.io.File;

/**
 * Created by tochkov.
 * <p/>
 * A service to persist bitmap covers as jpg to system cache.
 * It also deletes unused files from the DB, and redundant cached jpg covers
 */
public class UpdateCacheService extends IntentService {

    public UpdateCacheService() {
        super("UpdateCacheService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        for (SmartFile file : FileManager.getInstance().getFiles()) {
            if (file.getCoverFilePath() == null) {
                file.saveCoverToStorage(this);
            }
        }

        for (SmartFile file : FileManager.getInstance().getFilesRedundant()) {
            file.delete();
            new File(file.getCoverFilePath()).delete();
        }

        stopSelf();
    }


}
