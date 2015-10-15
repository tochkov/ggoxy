package com.goxapps.goxreader.filechooser.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.goxapps.goxreader.filechooser.FileManager;
import com.goxapps.goxreader.filechooser.model.SmartFile;
import com.goxapps.goxreader.util.AppUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtractFilesAndCoversService extends Service {

    private static boolean isRunning;

    public static final String FILE_SCAN_COMPLETE = "ExtractFilesService#FILE_SCAN_COMPLETE";
    public final static String COVER_PERSISTED = "GetBitmapCoversService#COVER_PERSISTED";
    public final static String KEY_COVER_PERSISTED_POSITION = "GetBitmapCoversService#KEY_COVER_PERSISTED_POSITION";

    private ConcurrentLinkedQueue<SmartFile> extractedFilesList;

    private ExecutorService threadPoolExtract;

    private ExecutorService threadPoolCovers;

    private AtomicInteger threadCounter = new AtomicInteger(0);

    private FileFilter mFilenameFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return (file.isDirectory() && !isFromIgnoredDirs(file))
                    || file.getName().endsWith(".pdf");
//                    || pathname.getName().endsWith(".epub");
        }
    };

    private File[] ignoredDirs = {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isRunning = true;
        extractedFilesList = new ConcurrentLinkedQueue<>();
        threadPoolExtract = Executors.newFixedThreadPool(AppUtils.getCores() * 5 + 10);

        File[] ss = Environment.getExternalStorageDirectory().listFiles();

        for (File f : ss) {
            Log.e("DIR", f.getPath());
        }

        // getting the root directory and start to iterate recursively
        extractFiles(Environment.getExternalStorageDirectory());


        return super.onStartCommand(intent, flags, startId);
    }

    private void extractFiles(final File dir) {

        // Counting the threads so we know when the last finishes
        threadCounter.incrementAndGet();

        threadPoolExtract.execute(new Runnable() {
            @Override
            public void run() {

                File[] files = dir.listFiles(mFilenameFilter);

                for (File file : files) {


                    if (file.isDirectory()) {
                        extractFiles(file); // recursive call
                    } else {
                        extractedFilesList.add(new SmartFile(file)); // add to collection
                    }
                }

                // when the last thread finishes
                if (threadCounter.decrementAndGet() == 0) {
                    onScanComplete();
                }
            }
        });
    }

    private void onScanComplete() {
        //update the model
        FileManager.getInstance().updateFileRecords(extractedFilesList);
        //notify the view
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FILE_SCAN_COMPLETE));

        threadPoolCovers = Executors.newFixedThreadPool(AppUtils.getCores() + 1);

        for (int i = 0; i < FileManager.getInstance().getFiles().size(); i++) {
            final int ii = i;

            threadCounter.incrementAndGet();

            threadPoolCovers.execute(new Runnable() {
                @Override
                public void run() {

                    SmartFile file = FileManager.getInstance().getFile(ii);
                    if (file.getCoverFilePath() == null) {
                        int approximateCoverWidth = getResources().getDisplayMetrics().widthPixels / 3;
                        file.savePdfInfoAndCover(ExtractFilesAndCoversService.this, approximateCoverWidth);

                        Intent intent = new Intent(COVER_PERSISTED);
                        intent.putExtra(KEY_COVER_PERSISTED_POSITION, ii);
                        LocalBroadcastManager.getInstance(ExtractFilesAndCoversService.this).sendBroadcast(intent);
                    }

                    if (threadCounter.decrementAndGet() == 0) {
                        onAllBitmapsGenerated();
                    }
                }
            });

        }

    }

    private void onAllBitmapsGenerated() {

        startService(new Intent(this, DeleteRedundantFilesService.class));
        isRunning = false;
        stopSelf();

    }

    public static boolean isRunning() {
        return isRunning;
    }


    private boolean isFromIgnoredDirs(File directory) {

        for (File ignored : ignoredDirs) {
            if (ignored.equals(directory))
                return true;
        }

        return false;
    }

}
