package com.android.settings.dot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.android.settings.R;

public class AboutUSHeadView extends LinearLayout {

    CardView gplus, tg, git, web;

    public AboutUSHeadView(Context context) {
        super(context);
        init(context);
    }

    public AboutUSHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected void init(Context context) {
        View view = inflate(context, R.layout.about_us_top, this);
        gplus = view.findViewById(R.id.g_plus);
        tg = view.findViewById(R.id.tg);
        git = view.findViewById(R.id.git);
        web = view.findViewById(R.id.web);
        gplus.setOnClickListener(v -> openUrl("https://plus.google.com/communities/101137692192340076065"));
        tg.setOnClickListener(v -> openUrl("https://t.me/dotos"));
        git.setOnClickListener(v -> openUrl("https://github.com/DotOS"));
        web.setOnClickListener(v -> openUrl("https://www.droidontime.com/"));
    }

    private void openUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getContext().startActivity(intent);
    }

}
