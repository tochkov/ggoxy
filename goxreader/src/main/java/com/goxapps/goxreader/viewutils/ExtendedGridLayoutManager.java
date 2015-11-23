package com.goxapps.goxreader.viewutils;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by tochkov.
 */
public class ExtendedGridLayoutManager extends GridLayoutManager
{
    private static final float EXTEND_RATIO = 0.15f; // 15%

    public ExtendedGridLayoutManager(Context context, int spanCount)
    {
        super(context, spanCount);
    }

    @Override
    public boolean supportsPredictiveItemAnimations()
    {
        return true;
    }

    /**
     * Items to be loaded in advance. Extends recyclerView's active area
     */
    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state)
    {
        return super.getExtraLayoutSpace(state) + (int) (getHeight() * EXTEND_RATIO);
    }
}
