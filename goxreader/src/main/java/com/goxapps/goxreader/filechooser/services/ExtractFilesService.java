package com.goxapps.goxreader.filechooser.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.goxapps.goxreader.filechooser.FileManager;
import com.goxapps.goxreader.filechooser.model.SmartFile;
import com.goxapps.goxreader.util.AppUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtractFilesService extends Service {

    public final static String FILE_SCAN_COMPLETE = "ExtractFilesService#FILE_SCAN_COMPLETE";

    private ConcurrentLinkedQueue<SmartFile> extractedFilesList;

    private ExecutorService threadPool;

    private AtomicInteger threadCounter = new AtomicInteger(0);

    private FileFilter mFilenameFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory()
                    || pathname.getName().endsWith(".pdf");
//                    || pathname.getName().endsWith(".epub");
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        extractedFilesList = new ConcurrentLinkedQueue<>();
        threadPool = Executors.newFixedThreadPool(AppUtils.getCores() * 5 + 10);

        // getting the root directory and start to iterate recursively
        extractFiles(Environment.getExternalStorageDirectory());

        return super.onStartCommand(intent, flags, startId);
    }

    private void extractFiles(final File dir) {

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
        FileManager.getInstance().updateFileRecords(extractedFilesList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FILE_SCAN_COMPLETE));

        stopSelf();
    }

}
