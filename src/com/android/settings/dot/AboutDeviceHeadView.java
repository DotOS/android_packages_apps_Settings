package com.android.settings.dot;

import android.content.Context;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.R;

public class AboutDeviceHeadView extends RelativeLayout {

    TextView device_name, android_ver, cpu_name, dot_ver;

    public AboutDeviceHeadView(Context context) {
        super(context);
        init(context, null);
    }

    public AboutDeviceHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(final Context context, @Nullable AttributeSet attrs) {
        View base = inflate(context, R.layout.device_about_top, this);
        device_name = base.findViewById(R.id.device_name);
        android_ver = base.findViewById(R.id.android_ver);
        cpu_name = base.findViewById(R.id.cpu_name);
        dot_ver = base.findViewById(R.id.dot_ver);
        device_name.setText(String.format("%s %s", SystemProperties.get("ro.product.brand"), SystemProperties.get("ro.product.model")));
        android_ver.setText(SystemProperties.get("ro.build.version.release"));
        cpu_name.setText(SystemProperties.get("ro.board.platform"));
        dot_ver.setText(SystemProperties.get("ro.modversion"));
    }

}
