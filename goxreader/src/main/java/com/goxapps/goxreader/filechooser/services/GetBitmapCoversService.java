package com.goxapps.goxreader.filechooser.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.goxapps.goxreader.filechooser.FileManager;
import com.goxapps.goxreader.filechooser.model.SmartFile;
import com.goxapps.goxreader.util.AppUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tochkov.
 *
 * A service to obtain all file covers as bitmaps.
 * Bitmaps are stored in each SmartFile in FilesManager.
 * When rendering is finished Intent(BITMAP_LOADED) is fired
 */
public class GetBitmapCoversService extends Service {

    public final static String BITMAP_LOADED = "GetBitmapCoversService#BITMAP_LOADED";
    public final static String KEY_BITMAP_LOADED_POSITION = "GetBitmapCoversService#KEY_BITMAP_LOADED_POSITION";
    public final static String KEY_BITMAP_WIDTH = "GetBitmapCoversService#KEY_BITMAP_WIDTH";

    private ExecutorService threadPoolBitmap;

    private AtomicInteger threadCounter = new AtomicInteger(0);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final int coverWidth = intent.getIntExtra(KEY_BITMAP_WIDTH, 250);

        threadPoolBitmap = Executors.newFixedThreadPool(AppUtils.getCores() - 1);

        for (int i = 0; i < FileManager.getInstance().getFiles().size(); i++) {
            final int ii = i;

            threadCounter.incrementAndGet();

            threadPoolBitmap.execute(new Runnable() {
                @Override
                public void run() {

                    SmartFile file = FileManager.getInstance().getFile(ii);
                    if (file.getBitmapCover() == null) {
                        boolean generatedFromPdfFile = file.generateBitmapCover(coverWidth, GetBitmapCoversService.this);

                        Intent intent = new Intent(BITMAP_LOADED);

                        if (generatedFromPdfFile)
                            intent.putExtra(KEY_BITMAP_LOADED_POSITION, ii);

                        LocalBroadcastManager.getInstance(GetBitmapCoversService.this).sendBroadcast(intent);
                    }

                    if (threadCounter.decrementAndGet() == 0) {
                        onAllBitmapsGenerated();
                    }
                }
            });

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void onAllBitmapsGenerated() {
        startService(new Intent(this, UpdateCacheService.class));
        stopSelf();
    }


}
