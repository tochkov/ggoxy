package com.goxapps.goxreader.filechooser;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.goxapps.goxreader.filechooser.model.SmartFile;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateFilesService extends Service {

    private static AtomicBoolean isRunning = new AtomicBoolean(false);

    public static final String FILE_SCAN_COMPLETE = "UpdateFilesService#FILE_SCAN_COMPLETE";
    public final static String COVER_READY = "UpdateFilesService#COVER_READY";
    public final static String KEY_COVER_READY = "UpdateFIiesService#KEY_COVER_READY";

    public final static int DEVICE_CORES = Runtime.getRuntime().availableProcessors();

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
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS),
            new File(Environment.getExternalStorageDirectory() + "/Android"),
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isRunning.set(true);
        extractedFilesList = new ConcurrentLinkedQueue<>();
        threadPoolExtract = Executors.newFixedThreadPool(DEVICE_CORES * 5 + 10);

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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                //update the model
                FileManager.getInstance().updateFileRecords(extractedFilesList);
                //notify the view
                LocalBroadcastManager.getInstance(UpdateFilesService.this).sendBroadcast(new Intent(FILE_SCAN_COMPLETE));

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                generateCoversAndSave();
            }
        }.execute();
    }

    private void generateCoversAndSave() {

        threadPoolCovers = Executors.newFixedThreadPool(DEVICE_CORES + 1);

        for (int i = 0; i < FileManager.getInstance().getFiles().size(); i++) {
            final int ii = i;

            threadCounter.incrementAndGet();

            threadPoolCovers.execute(new Runnable() {
                @Override
                public void run() {

                    SmartFile file = FileManager.getInstance().getFile(ii);
                    if (file.getCoverFilePath() == null) {
                        int approximateCoverWidth = getResources().getDisplayMetrics().widthPixels / 3;
                        file.savePdfInfoAndCover(UpdateFilesService.this, approximateCoverWidth);

                        Intent intent = new Intent(COVER_READY);
                        intent.putExtra(KEY_COVER_READY, ii);
                        LocalBroadcastManager.getInstance(UpdateFilesService.this).sendBroadcast(intent);
                    }

                    if (threadCounter.decrementAndGet() == 0) {
                        deleteRedundantFiles();
                    }
                }
            });

        }
    }

    private void deleteRedundantFiles() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                for (SmartFile file : FileManager.getInstance().getFilesRedundant()) {
                    file.delete();
                    new File(file.getCoverFilePath()).delete();
                }
                FileManager.getInstance().getFilesRedundant().clear();

                isRunning.set(false);
                stopSelf();
            }
        });


    }

    public static boolean isRunning() {
        return isRunning.get();
    }

    private boolean isFromIgnoredDirs(File directory) {

        for (File ignored : ignoredDirs) {
            if (ignored.equals(directory))
                return true;
        }

        return false;
    }

}
