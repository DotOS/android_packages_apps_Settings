package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.settings.R;
import com.android.settings.Utils;

public class RomCardView extends AboutBaseCard {

    public RomCardView(@NonNull Context context) {
        super(context);
    }

    public RomCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RomCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        layout.setId(R.id.rom_card_id);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ImageView rom_logo = new ImageView(context);
        rom_logo.setPadding(72, 72, 72, 72);
        rom_logo.setAdjustViewBounds(true);
        rom_logo.setImageTintList(ColorStateList.valueOf(Utils.getColorAttrDefaultColor(context, android.R.attr.colorAccent)));
        linearLayout.setId(R.id.rom_logo_id);
        rom_logo.setImageResource(R.drawable.ic_rom_logo);
        RelativeLayout.LayoutParams rlparams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rlparams.addRule(RelativeLayout.ABOVE, R.id.rom_logo_id);
        rom_logo.setLayoutParams(rlparams);
        setMinimumWidth(rom_logo.getWidth());
        String version = SystemProperties.get("ro.modversion");
        String releaseType = SystemProperties.get("ro.dot.releasetype");
        TextView rom_title = new TextView(context);
        rom_title.setText(String.format(getResources().getString(R.string.about_device_rom_title), version));
        rom_title.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorPrimary));
        rom_title.setPadding(0, 12, 0, 12);
        rom_title.setTextSize(18);
        rom_title.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        rom_title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView rom_version = new TextView(context);
        rom_version.setText(releaseType);
        rom_version.setTextColor(Utils.getColorAttrDefaultColor(context, android.R.attr.textColorSecondary));
        rom_version.setPadding(0, 12, 0, 24);
        rom_version.setTextSize(14);
        rom_version.setSingleLine(true);
        rom_version.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        rom_version.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        RelativeLayout.LayoutParams lparamas = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lparamas.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        linearLayout.setLayoutParams(lparamas);
        linearLayout.addView(rom_title);
        linearLayout.addView(rom_version);
		linearLayout.setBackgroundColor(getResources().getColor(R.color.search_bar_background));
        layout.addView(rom_logo, rlparams);
        layout.addView(linearLayout, lparamas);
    }
}