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
 * "smart" behaviour - write/read DB and storage, generate covers,
 * user info, settings etc. /non-business logic/
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

    /**
     * Blocking operation. Do not call on UI thread
     *
     * @param context
     * @param coverWidth
     */
    public void savePdfInfoAndCover(Context context, int coverWidth) {

        if (coverFilePath != null)
            return;

        Bitmap bitmapCover = null;
        FileOutputStream out = null;

        try {
            MuPDFCore core = new MuPDFCore(context, filePath);
            pagesCount = core.countDisplayPage();

            int w = coverWidth;
            int h = (int) (coverWidth * AspectRatioImageView.PROPORTION);

            bitmapCover = core.drawPage(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888), 0, w, h, 0, 0, w, h);

            String directory = ContextCompat.getExternalCacheDirs(context)[0].getPath();
            String uniqueName = "cvr_" + System.currentTimeMillis(); //TODO not so clever
            String filePath = directory + uniqueName;

            out = new FileOutputStream(filePath);
            bitmapCover.compress(Bitmap.CompressFormat.JPEG, 90, out);

            coverFilePath = filePath;

            // DB persistence
            save();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bitmapCover != null) {
                    bitmapCover.recycle();
                }
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
