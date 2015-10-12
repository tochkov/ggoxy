package com.goxapps.goxreader.reader;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.goxapps.goxreader.R;
import com.goxapps.goxreader.filechooser.FileManager;

public class FullReaderActivity extends AppCompatActivity implements BottomMenuFragment.OnFragmentInteractionListener {

    public static final String TAG_BOT_MENU_FRAGMENT = "FullReaderActivity#TAG_BOT_MENU_FRAGMENT";
    public static final String KEY_SELECTED_FILE_POSITION = "FullReaderActivity#KEY_SELECTED_FILE_POSITION";

    private View mDecorView;

    private BottomMenuFragment menuFragment;

    private boolean navigationVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_reader);

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


//        int mSystemNavigationHeight = AppUtils.getNav(this);
//        int mToolbarSize = AppUtils.getActionBarHeight(this);
//
//        findViewById(R.id.frame_bottom_menu).setMinimumHeight(3* mToolbarSize + mSystemNavigationHeight);



        menuFragment = (BottomMenuFragment) getFragmentManager().findFragmentByTag(TAG_BOT_MENU_FRAGMENT);

        if (menuFragment == null) {
            menuFragment = BottomMenuFragment.newInstance(null, null);
            getFragmentManager().beginTransaction().add(R.id.frame_bottom_menu, menuFragment, TAG_BOT_MENU_FRAGMENT).commit();
        }


        hideSystemUI();
        showSystemUI();


        FileManager.getInstance().getFile(getIntent().getIntExtra(KEY_SELECTED_FILE_POSITION, 0)).save();







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
