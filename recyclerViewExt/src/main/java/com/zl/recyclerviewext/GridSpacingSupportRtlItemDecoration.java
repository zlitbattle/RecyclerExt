package com.zl.recyclerviewext;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;


public class GridSpacingSupportRtlItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacingHorizontal;
    private int spacingVertical;
    private boolean includeEdge;

    public GridSpacingSupportRtlItemDecoration(int spanCount, int spacingHorizontal, int spacingVertical, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingHorizontal = spacingHorizontal;
        this.spacingVertical = spacingVertical;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager.SpanSizeLookup spanSizeLookup = ((GridLayoutManager) parent.getLayoutManager()).getSpanSizeLookup();
            if (spanSizeLookup != null && spanSizeLookup.getSpanSize(position) == spanCount) {
                return;
            }
        }
        int column = position % spanCount;
        if (includeEdge) {
            if (isLayoutDirectionRtL()) {
                outRect.right = spacingHorizontal - column * spacingHorizontal / spanCount;
                outRect.left = (column + 1) * spacingHorizontal / spanCount;
            } else {
                outRect.left = spacingHorizontal - column * spacingHorizontal / spanCount;
                outRect.right = (column + 1) * spacingHorizontal / spanCount;
            }
            if (position < spanCount) {
                outRect.top = spacingVertical;
            }
            outRect.bottom = spacingVertical;
        } else {
            if (isLayoutDirectionRtL()) {
                outRect.right = column * spacingHorizontal / spanCount;
                outRect.left = spacingHorizontal - (column + 1) * spacingHorizontal / spanCount;
            } else {
                outRect.left = column * spacingHorizontal / spanCount;
                outRect.right = spacingHorizontal - (column + 1) * spacingHorizontal / spanCount;
            }
            if (position < spanCount) {
                outRect.top = spacingVertical;
            }
            outRect.bottom = spacingVertical;
        }
    }

    private boolean isLayoutDirectionRtL() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
    }
}