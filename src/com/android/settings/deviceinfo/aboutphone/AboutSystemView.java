package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.pm.UserInfo;
import android.os.UserManager;
import android.support.annotation.Nullable;

import com.android.settings.R;
import com.android.settingslib.Utils;

import com.android.settings.dot.TabLayout;
import com.android.settings.dot.TabItem;

public class AboutSystemView extends LinearLayout {

    TabLayout tabLayout;

    public AboutSystemView(Context context) {
        super(context);
        init(context);
    }

    public AboutSystemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AboutSystemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.dot_about_system, this);
        TextView profile_name = view.findViewById(R.id.profile_name);
        ImageView avatar = view.findViewById(R.id.avatar_icon);
        tabLayout = view.findViewById(R.id.system_Sections);
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        UserInfo info = com.android.settings.Utils.getExistingUser(userManager, android.os.Process.myUserHandle());
        avatar.setImageDrawable(Utils.getUserIcon(context, userManager, info));
        profile_name.setText(info.name);
    }

    public void setOnPositionChangeListener(TabLayout.OnTabSelectedListener listener) {
        tabLayout.setOnTabSelectedListener(listener);
    }

}
