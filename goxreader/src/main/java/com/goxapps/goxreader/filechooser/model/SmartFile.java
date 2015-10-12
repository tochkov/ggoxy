package com.goxapps.goxreader.filechooser.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;

import com.artifex.mupdfdemo.MuPDFCore;
import com.goxapps.goxreader.filechooser.AspectRatioImageView;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tochkov.
 * <p/>
 * A smart file wrapper used for interacting with the model.
 * Each file supported by the app is a SmartFile.
 * "smart" behaviour - write/read DB and storage,generate covers,
 * user info, settings etc. /non business logic/
 * <p/>
 * In the future could be abstract and subclassed by many classes - PdfSmartFile, EpubSmartFile etc.
 */
public class SmartFile extends SugarRecord {

    public static final int TYPE_PDF = 777;
    public static final int TYPE_EPUB = 888;

    private String filePath;
    private int fileType;
    private String coverFilePath;
    private int fileSize;
    private int pagesCount;

    private int userLastPage;
    private int userFrequency;

    @Ignore
    private Bitmap bitmapCover;

    @Ignore
    private ArrayList<UserBookmark> userBookmarks;
    @Ignore
    private ArrayList<UserNote> userNotes;
    @Ignore
    private UserBookSettings userBookSettings;

    @Ignore
    private File file;

    @Ignore
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o instanceof SmartFile && filePath != null)
            return filePath.equals(((SmartFile) o).getFilePath());

        return false;
    }

    public SmartFile() {
        //required by SugarRecord
    }

    public SmartFile(File file) {
        this.file = file;
        this.filePath = file.getPath();

//        if (filePath.endsWith(".pdf")) {
//
//        } else if (filePath.endsWith(".epub")) {
//
//        }
    }

    public File getFile() {
        if (file == null)
            file = new File(filePath);

        return file;
    }

    public Bitmap getBitmapCover() {
//        if(bitmapCover == null && coverFilePath != null){
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            bitmapCover = BitmapFactory.decodeFile(coverFilePath, options);
//        }

        return bitmapCover;
    }

    /**
     * Blocking operation. Do not call on UI thread
     *
     * @param coverWidth
     * @param context
     * @return true if generated from PDF file
     */
    public boolean generateBitmapCover(int coverWidth, Context context) {

        if (bitmapCover != null)
            return false;

        try {
            if (coverFilePath != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                bitmapCover = BitmapFactory.decodeFile(coverFilePath, options);

            } else {
                MuPDFCore core = new MuPDFCore(context, filePath);
                pagesCount = core.countDisplayPage();

                int w = coverWidth;
                int h = (int) (coverWidth * AspectRatioImageView.PROPORTION);

                bitmapCover = core.drawPage(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888), 0, w, h, 0, 0, w, h);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Blocking operation. Do not call on UI thread
     *
     * @param context
     */
    public void saveCoverToStorage(Context context) {
        if (bitmapCover == null)
            return;

        FileOutputStream out = null;
        try {
            String directory = ContextCompat.getExternalCacheDirs(context)[0].getPath();

            //TODO not so clever
            String uniqueName = file.getName() + System.currentTimeMillis();

            coverFilePath = directory + uniqueName;

            out = new FileOutputStream(coverFilePath);
            bitmapCover.compress(Bitmap.CompressFormat.JPEG, 90, out);

            // DB persistence
            save();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    String value;
//    long filesize = getFolderSize(new File(directory)) / 1024;//call function and convert bytes into Kb
//    if (filesize >= 1024)
//    value = filesize / 1024 + " Mb";
//    else
//    value = filesize + " Kb";
//
//    Log.e("XXX", "cache : " + value);

//    private long getFolderSize(File f) {
//        long size = 0;
//        if (f.isDirectory()) {
//            for (File file : f.listFiles()) {
//                size += getFolderSize(file);
//            }
//        } else {
//            size = f.length();
//        }
//        return size;
//    }


    public long getLastModifiedDate() {
        return getFile().lastModified();
    }

    public String getCoverFilePath() {
        return coverFilePath;
    }

    public String getName() {
        return getFile().getName();
    }

    public int getUserFrequency() {
        return userFrequency;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFileType() {
        return fileType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getPagesCount() {
        return pagesCount;
    }

    public int getUserLastPage() {
        return userLastPage;
    }

    public ArrayList<UserBookmark> getUserBookmarks() {
        return userBookmarks;
    }

    public ArrayList<UserNote> getUserNotes() {
        return userNotes;
    }

    public UserBookSettings getUserBookSettings() {
        return userBookSettings;
    }
}
