package com.android.settings.dashboard;

import android.graphics.Rect;
import android.view.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public class DashboardRecyclerSpacer extends RecyclerView.ItemDecoration {
    private int top;
    private int position;

    public DashboardRecyclerSpacer(int top, int pos) {
        this.top = top;
        this.position = pos;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if(position == this.position){
            if (top != 0)
                outRect.top = top;
        }
    }
}