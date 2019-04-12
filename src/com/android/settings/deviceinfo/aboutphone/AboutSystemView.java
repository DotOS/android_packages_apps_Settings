package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.pm.UserInfo;
import android.os.UserManager;
import android.view.View;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.android.settings.R;
import com.android.settingslib.Utils;

import android.support.annotation.Nullable;

import com.android.settings.dot.TabLayout;
import com.android.settings.dot.TabItem;

public class AboutSystemView extends LinearLayout {

    FragmentManager fm;
    int tabs[] = {
       R.string.tab_software,
       R.string.tab_status,
       R.string.tab_other
    };
    TabLayout tabLayout;
    Context context;

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
        this.context = context;
        View view = inflate(context, R.layout.dot_about_system, this);
        TextView profile_name = view.findViewById(R.id.profile_name);
        ImageView avatar = view.findViewById(R.id.avatar_icon);
        tabLayout = view.findViewById(R.id.system_Sections);
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        UserInfo info = com.android.settings.Utils.getExistingUser(userManager, android.os.Process.myUserHandle());
        avatar.setImageDrawable(Utils.getUserIcon(context, userManager, info));
        profile_name.setText(info.name);
        ViewPager placepager = view.findViewById(R.id.placeholderPager);
        if (fm != null)
            placepager.setAdapter(new PlaceholderAdapter(fm));
        tabLayout.setupWithViewPager(placepager);
    }
    
    public void shareFragmentManager(FragmentManager fm) {
        this.fm = fm;
    }

    public void setOnPositionChangeListener(TabLayout.OnTabSelectedListener listener) {
        tabLayout.setOnTabSelectedListener(listener);
    }
    
    public class PlaceholderAdapter extends FragmentStatePagerAdapter {

        public PlaceholderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return context.getString(tabs[position]);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

    }

}
