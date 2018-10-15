package com.android.settings.dashboard;

import android.content.Context;
import android.os.UserManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.content.pm.UserInfo;

import com.android.settingslib.Utils;

public class DashboardProfileIcon extends RelativeLayout {

    public DashboardProfileIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
	
	public DashboardProfileIcon(Context context) {
        super(context);
		init(context);
    }

    private void init(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        UserInfo info = com.android.settings.Utils.getExistingUser(userManager, android.os.Process.myUserHandle());
        LayoutParams lp = new LayoutParams((int) dp(46), (int) dp(46));
        setLayoutParams(lp);
        ImageView icon = new ImageView(context);
        icon.setLayoutParams(lp);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(Utils.getUserIcon(context, userManager, info));
        addView(icon);
    }

    private float dp(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
