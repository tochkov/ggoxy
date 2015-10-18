package com.goxapps.goxreader.filechooser;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by tochkov.
 */
public class AspectRatioImageView extends ImageView {

    public static final double PROPORTION = 1.414; // A4 standard

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, (int) (width * PROPORTION));
    }
}
