package com.goxapps.goxreader.filechooser;

import android.content.Context;
import android.graphics.Bitmap;

import com.artifex.mupdfdemo.MuPDFCore;
import com.goxapps.goxreader.R;

import java.io.File;

/**
 * Created by tochkov.
 */
public class FileWrapper {

    enum Type {
        EPUB, PDF
    }

    private int fileSize;

    private String fileSizeString;

    private int pagesCount;

    private Bitmap bitmapCover;

    private File file;

    private String path;

    private String name;

    private Type type;

    private int userFrequency;

    private Object bookmarks;

    private Object quotes;

    public int getFileSize() {
        return fileSize;
    }

    public String getFileSizeString() {
        return fileSizeString;
    }

    public int getPagesCount() {
        return pagesCount;
    }

    public Bitmap getBitmapCover() {
        return bitmapCover;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getUserFrequency() {
        return userFrequency;
    }

    public Object getBookmarks() {
        return bookmarks;
    }

    public Object getQuotes() {
        return quotes;
    }

    /**
     * Do not init this object on the UI thread. Blocking constructor!
     *
     * @param file
     */
    public FileWrapper(File file, Context context) {
        this.file = file;

        this.path = file.getPath();

        if (path.endsWith(".pdf")) {
            this.name = file.getName().replace(".pdf", "");
            this.type = Type.PDF;
        } else if (path.endsWith(".epub")) {
            this.name = file.getName().replace(".epub", "");
            this.type = Type.EPUB;
        }

        this.fileSize = fileSize;

        try {
            int w = (int) context.getResources().getDimension(R.dimen.thumbnail_width);
            int h = (int) context.getResources().getDimension(R.dimen.thumbnail_height);

            MuPDFCore core = new MuPDFCore(context, file.getPath());

            this.pagesCount = core.countDisplayPage();
            this.bitmapCover = core.drawPage(Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888), 0, w, h, 0, 0, w, h);

        } catch (Exception e) {
            e.printStackTrace();
        }




        //persistance
//        this.userFrequency = userFrequency;
//        this.bookmarks = bookmarks;
//        this.quotes = quotes;
    }


}
