package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.android.settings.R;
import com.android.settings.Utils;

public class AboutBaseCard extends CardView {

    protected RelativeLayout layout;
    protected int defaultPadding = 38;
    protected int defaultRadius = 48;

    public AboutBaseCard(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AboutBaseCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AboutBaseCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        setLayoutParams(new LayoutParams(getResources().getDimensionPixelSize(R.dimen.storage_card_min_width), getResources().getDimensionPixelSize(R.dimen.storage_card_min_height)));
        layout = new RelativeLayout(context);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        layout.setPadding(defaultPadding, (int) (defaultPadding*1.5), defaultPadding, (int) (defaultPadding*1.5));
		layout.setBackgroundColor(getResources().getColor(R.color.search_bar_background));
        addView(layout);
        setRadius(defaultRadius);
        setCardBackgroundColor(getResources().getColor(R.color.search_bar_background));
    }
}