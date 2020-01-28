package com.simply.schedule.ui;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class MarginItemDecoration extends RecyclerView.ItemDecoration {

    private int marginTopOfFirstElement;
    private int marginBottomOfLastElement;
    private int marginRight;
    private int marginLeft;
    private int marginBetween;

    public MarginItemDecoration(int horizontal, int topAndBottom, int betweenElements) {
        this.marginTopOfFirstElement = topAndBottom;
        this.marginBottomOfLastElement = topAndBottom;
        this.marginLeft = horizontal;
        this.marginRight = horizontal;
        this.marginBetween = betweenElements;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int layoutPosition = parent.getChildLayoutPosition(view);
        if (layoutPosition == 0) {
            outRect.top = marginTopOfFirstElement;
        }
        if (layoutPosition == state.getItemCount() - 1) {
            outRect.bottom = marginBottomOfLastElement;
        } else {
            outRect.bottom = marginBetween;
        }
        outRect.right = marginRight;
        outRect.left = marginLeft;
    }
}
