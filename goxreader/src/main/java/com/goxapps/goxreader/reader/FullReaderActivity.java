package com.goxapps.goxreader.reader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.artifex.mupdfdemo.FilePicker;
import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;
import com.artifex.mupdfdemo.SearchTaskResult;
import com.goxapps.goxreader.R;

import java.io.InputStream;

public class FullReaderActivity extends AppCompatActivity implements BottomMenuFragment.OnFragmentInteractionListener {

    public static final String TAG_BOT_MENU_FRAGMENT = "FullReaderActivity#TAG_BOT_MENU_FRAGMENT";
    public static final String KEY_SELECTED_FILE_PATH = "FullReaderActivity#KEY_SELECTED_FILE_PATH";

    private View mDecorView;

    private MuPDFCore core;

    private BottomMenuFragment menuFragment;
    private MuPDFReaderView mDocView;
    private boolean navigationVisible = true;

    private String mFilePath;

    private MuPDFCore openFile(String path) {
        System.out.println("Trying to open " + path);
        try {
            core = new MuPDFCore(this, path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return core;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_reader);

        if (core == null) {
            core = (MuPDFCore) getLastCustomNonConfigurationInstance();

            if (savedInstanceState != null) {
//                if (savedInstanceState.containsKey("FileName"))
//                    mFileName = savedInstanceState.getString("FileName");
                if (savedInstanceState.containsKey("FilePath"))
                    mFilePath = savedInstanceState.getString("FilePath");
            }

        }


        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {

//            mFilePath

        } else if (getIntent().getStringExtra(KEY_SELECTED_FILE_PATH) != null) {

            mFilePath = getIntent().getStringExtra(KEY_SELECTED_FILE_PATH);
            core = openFile(mFilePath);

        } else {
            // clear back stack/ finish()
            // start FileChooserActivity
            // return;
        }

//        initCore();

        initReaderView();
        initBottomMenu();

        mDecorView = getWindow().getDecorView();
        mDecorView.setClickable(true);

        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                navigationVisible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                menuFragment.onSystemNavigationToggled(navigationVisible);
            }
        });

        final GestureDetector singleClickDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (navigationVisible) {
                    hideSystemUI();
                    navigationVisible = false;
                } else {
                    showSystemUI();
                    navigationVisible = true;
                }

                return true;
            }
        });

        mDecorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return singleClickDetector.onTouchEvent(event);
            }
        });


        menuFragment = (BottomMenuFragment) getFragmentManager().findFragmentByTag(TAG_BOT_MENU_FRAGMENT);

        if (menuFragment == null) {
            menuFragment = BottomMenuFragment.newInstance(null, null);
            getFragmentManager().beginTransaction().add(R.id.frame_bottom_menu, menuFragment, TAG_BOT_MENU_FRAGMENT).commit();
        }


        hideSystemUI();
        showSystemUI();


    }


    private void initCore() {

        if (core == null) {

            Intent intent = getIntent();
            byte buffer[] = null;

            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                if (uri.toString().startsWith("content://")) {
                    // Handle view requests from the Transformer Prime's file
                    // manager
                    // Hopefully other file managers will use this same scheme,
                    // if not
                    // using explicit paths.
                    Cursor cursor = getContentResolver().query(uri,
                            new String[]{"_data"}, null, null, null);
                    if (cursor.moveToFirst()) {
                        String str = cursor.getString(0);
                        String reason = null;
                        if (str == null) {
                            try {
                                InputStream is = getContentResolver()
                                        .openInputStream(uri);
                                int len = is.available();
                                buffer = new byte[len];
                                is.read(buffer, 0, len);
                                is.close();
                            } catch (OutOfMemoryError e) {
                                System.out
                                        .println("Out of memory during buffer reading");
                                reason = e.toString();
                            } catch (Exception e) {
                                reason = e.toString();
                            }
                            if (reason != null) {
//                                buffer = null;
//                                Resources res = getResources();
//                                AlertDialog alert = mAlertBuilder.create();
//                                setTitle(String
//                                        .format(res
//                                                        .getString(R.string.cannot_open_document_Reason),
//                                                reason));
//                                alert.setButton(AlertDialog.BUTTON_POSITIVE,
//                                        getString(R.string.dismiss),
//                                        new DialogInterface.OnClickListener() {
//                                            public void onClick(
//                                                    DialogInterface dialog,
//                                                    int which) {
//                                                finish();
//                                            }
//                                        });
//                                alert.show();
                                return;
                            }
                        } else {
                            uri = Uri.parse(str);
                        }
                    }
                }

                mFilePath = Uri.decode(uri.getEncodedPath());
                if (buffer != null) {
//                    core = openBuffer(buffer);
                } else {
                    core = openFile(mFilePath);
                }
                SearchTaskResult.set(null);
            }
            if (core != null && core.needsPassword()) {
//                requestPassword(savedInstanceState);
                return;
            }
            if (core != null && core.countDisplayPage() == 0) {
                core = null;
            }
        }
        if (core == null) {
//            AlertDialog alert = mAlertBuilder.create();
//            alert.setTitle(R.string.cannot_open_document);
//            alert.setButton(AlertDialog.BUTTON_POSITIVE,
//                    getString(R.string.dismiss),
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });
//            alert.show();
            return;
        }
    }

    private void initReaderView() {

        mDocView = new MuPDFReaderView(this) {
            @Override
            protected void onMoveToChild(int i) {
//                if (core == null)
//                    return;
//                mCurrentPage = i;
//
//                mPageNumberView.setText(getPageNumberPerAll(i));
//                // mPageNumberView.setText(String.format("%d / %d", i + 1,
//                // core.countDisplayPage()));
//                mPageSlider.setMax((core.countDisplayPage() - 1)
//                        * mPageSliderRes);
//                mPageSlider.setProgress(i * mPageSliderRes);
//                supportInvalidateOptionsMenu();
//                super.onMoveToChild(i);
            }

            @Override
            protected void onTapMainDocArea() {
//                if (!mButtonsVisible) {
//                    showButtons();
//                } else {
//                    if (mTopBarMode == TopBarMode.Main)
//                        hideButtons();
//                }

                if (navigationVisible) {
                    hideSystemUI();
                    navigationVisible = false;
                } else {
                    showSystemUI();
                    navigationVisible = true;
                }

            }

            @Override
            protected void onDocMotion() {
//                hideButtons();
            }

//            @Override
//            protected void onHit(Hit item) {
//                switch (mTopBarMode) {
//                    case Annot:
//                        if (item == Hit.Annotation) {
//                            showButtons();
//                            mTopBarMode = TopBarMode.Delete;
//                            mTopBarSwitcher
//                                    .setDisplayedChild(mTopBarMode.ordinal());
//                        }
//                        break;
//                    case Delete:
//                        mTopBarMode = TopBarMode.Annot;
//                        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
//                        // fall through
//                    default:
//                        // Not in annotation editing mode, but the pageview will
//                        // still select and highlight hit annotations, so
//                        // deselect just in case.
//                        MuPDFView pageView = (MuPDFView) mDocView
//                                .getDisplayedView();
//                        if (pageView != null)
//                            pageView.deselectAnnotation();
//                        break;
//                }
//            }
        };

        mDocView.setAdapter(new MuPDFPageAdapter(this, null, core));

        ((FrameLayout)findViewById(R.id.frame_reader)).addView(mDocView);

    }

    private void initBottomMenu() {

    }


    public String getPageNumberPerAll(int index) {
        String pageNum = "";
        if (core.getDoubleMode()) {
            if (core.getCoverPageMode()) {
                if (index == 0) { // first page
                    pageNum = String.format("%d / %d", (index * 2) + 1,
                            core.countDocumentPages());
                } else if (index * 2 == core.countDocumentPages()) { // last
                    // single
                    // page
                    pageNum = String.format("%d / %d", index * 2,
                            core.countDocumentPages());
                } else
                    // double page
                    pageNum = String.format("%d-%d / %d", index * 2,
                            (index * 2) + 1, core.countDocumentPages());
            } else {
                if ((index * 2) + 1 == core.countDocumentPages()) { // last
                    // single
                    // page
                    pageNum = String.format("%d / %d", (index * 2) + 1,
                            core.countDocumentPages());
                } else
                    pageNum = String.format("%d-%d / %d", (index * 2) + 1,
                            (index * 2) + 2, core.countDocumentPages());
            }

        }
//        if (!core.getDoubleMode() || mReflow) {
//            pageNum = String.format("%d / %d", index + 1,
//                    core.countDocumentPages());
//        }
        return pageNum;
    }


    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
