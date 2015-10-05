package com.goxapps.goxreader.filechooser;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.goxapps.goxreader.util.TimeTracker;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtractFilesService extends Service {

    public final static String FILE_SCAN_COMPLETE = "ExtractFilesService#FILE_SCAN_COMPLETE";
    public final static int AVAILABLE_CORES = Runtime.getRuntime().availableProcessors();

    private ConcurrentLinkedQueue<FileWrapper> extractedFilesList;

    private ExecutorService threadPool;

    private AtomicInteger threadCounter = new AtomicInteger(0);

    private FileFilter mFilenameFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory()
                    || pathname.getName().endsWith(".pdf");
//                    || pathname.getName().endsWith(".epub");
//                    || pathname.getName().endsWith(".jpg");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        extractedFilesList = new ConcurrentLinkedQueue<>();

        // What is the optimal pool size ?
        // It should differ on various devices, so
        // how to adjust the value based on the device?
        // Are there any other best practices for executing unknown number of threads simultaneously?
        threadPool = Executors.newFixedThreadPool(AVAILABLE_CORES * 15 + 10);

        // getting the root directory and start to iterate recursively
        extractFiles(Environment.getExternalStorageDirectory());

        return super.onStartCommand(intent, flags, startId);
    }

    private void extractFiles(final File dir) {

        if (threadCounter.get() == 0)
            TimeTracker.start("1");

        // Counting the threads so we know when the last finishes
        threadCounter.incrementAndGet();

        threadPool.execute(new Runnable() {
            @Override
            public void run() {

                File[] files = dir.listFiles(mFilenameFilter);

                for (File file : files) {
                    if (file.isDirectory()) {
                        extractFiles(file); // recursive call
                    } else {
                        extractedFilesList.add(new FileWrapper(file, ExtractFilesService.this)); // add to collection
                    }
                }

                if (threadCounter.decrementAndGet() == 0) {
                    TimeTracker.stop("1");
                    onScanEnd();
                }
            }
        });
    }

    private void onScanEnd() {
        Log.e(getClass().getSimpleName(), "SYSTEM SCAN END");
        ExtractedFilesManager.getInstance().initAll(extractedFilesList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FILE_SCAN_COMPLETE));
        stopSelf();
    }

}
