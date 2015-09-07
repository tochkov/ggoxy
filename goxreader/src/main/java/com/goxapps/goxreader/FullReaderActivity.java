package com.goxapps.goxreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class FullReaderActivity extends AppCompatActivity {

    private View mDecorView;
    private boolean navigationVisible = true;
    private View mCenterView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_reader);

        mCenterView = findViewById(R.id.view_center);

        mDecorView = getWindow().getDecorView();

        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                boolean visible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;

                Toast.makeText(FullReaderActivity.this, visible ? "visible" : "invisible", Toast.LENGTH_SHORT).show();

            }
        });


        mCenterView.setClickable(true);

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


        mCenterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return singleClickDetector.onTouchEvent(event);
            }
        });

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

}
