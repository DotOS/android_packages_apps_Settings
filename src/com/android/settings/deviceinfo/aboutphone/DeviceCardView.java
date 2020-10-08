package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.settings.R;
import com.android.settings.Utils;

public class DeviceCardView extends AboutBaseCard {
	
    public DeviceCardView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public DeviceCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DeviceCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        super.init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DeviceCardView, defStyleAttr, 0);
        setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        TextView card_title = new TextView(context);
        card_title.setText(a.getString(R.styleable.DeviceCardView_extra_title));
        card_title.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary));
        card_title.setPadding(0,0,0,24);
        card_title.setTextSize(18);
        card_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView card_summary = new TextView(context);
		String mDeviceName = Settings.Global.getString(context.getContentResolver(),
                Settings.Global.DEVICE_NAME);
        if (mDeviceName == null) {
            mDeviceName = Build.MODEL;
        }
        card_summary.setText(mDeviceName);
        card_summary.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorSecondary));
        card_summary.setTextSize(14);
        card_summary.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(card_title);
        linearLayout.addView(card_summary);
		linearLayout.setBackgroundColor(getResources().getColor(R.color.search_bar_background));
        layout.addView(linearLayout);
        a.recycle();
    }
}