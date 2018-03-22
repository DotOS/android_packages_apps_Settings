package com.dot.settings.deviceinfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.settings.R;

public class AboutDeviceHeadView extends RelativeLayout {

    RelativeLayout edit_button;
    TextView device_name;
    TextView phone_number;
    TextView model_number;
    TextView serial_number;
    TextView imei;

    public AboutDeviceHeadView(Context context) {
        super(context);
        init(context, null);
    }

    public AboutDeviceHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @SuppressLint("MissingPermission")
    private void init(Context context, @Nullable AttributeSet attrs) {
        View base = inflate(context, R.layout.device_about_top, this);
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phone_number = base.findViewById(R.id.phone_number);
        model_number = base.findViewById(R.id.model_number);
        serial_number = base.findViewById(R.id.serial_number);
        imei = base.findViewById(R.id.imei);
        if (tMgr.getLine1Number() != null) {
            phone_number.setText(tMgr.getLine1Number());
        } else {
            phone_number.setText("UNKNOWN");
        }
        model_number.setText(SystemProperties.get("ro.product.device"));
        serial_number.setText(Build.getSerial());
        imei.setText(tMgr.getDeviceId());
        edit_button = base.findViewById(R.id.edit_button);
        device_name = base.findViewById(R.id.device_name);
        device_name.setText(SystemProperties.get("ro.product.model"));
    }
}

