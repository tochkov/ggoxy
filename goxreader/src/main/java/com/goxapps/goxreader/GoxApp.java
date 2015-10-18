package com.goxapps.goxreader;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.orm.SugarApp;

/**
 * Created by tochkov.
 */
public class GoxApp extends SugarApp {

    private static GoxApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new FadeInBitmapDisplayer(300, true, true, false))
                .build();


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                .defaultDisplayImageOptions(options)
                .threadPoolSize(Runtime.getRuntime().availableProcessors() + 1)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static GoxApp get() {
        return instance;
    }
}
