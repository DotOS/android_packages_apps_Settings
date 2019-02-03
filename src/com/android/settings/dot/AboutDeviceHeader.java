package com.android.settings.dot;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class AboutDeviceHeader extends RelativeLayout {

    TextView device_name;


    public AboutDeviceHeader(Context context) {
        super(context);
        customView(context,null);
    }

    public AboutDeviceHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        customView(context, attrs);
    }

    private void customView(final Context context, @Nullable AttributeSet attributeSet){
        View base = inflate(context, R.layout.about_device_header, this);
        device_name = base.findViewById(R.id.device_name);
        device_name.setText(String.format("%s %s", getSystemProperty("ro.product.brand"), getSystemProperty("ro.product.model")));
    }

    public String getSystemProperty(String key) {
        String value = null;

        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }
}
