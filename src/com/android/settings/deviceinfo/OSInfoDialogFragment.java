/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.deviceinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class OSInfoDialogFragment extends InstrumentedDialogFragment {

    public static final String TAG = "OSInfo";
    private long[] mHits = new long[3];
  
    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DIALOG_SETTINGS_HARDWARE_INFO;
    }

    public static OSInfoDialogFragment newInstance() {
        final OSInfoDialogFragment fragment = new OSInfoDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View content = LayoutInflater.from(builder.getContext())
                .inflate(R.layout.device_info_dialog, null /* parent */);
        TextView model = content.findViewById(R.id.dev_info_device);
        TextView dot_version = content.findViewById(R.id.dev_info_dot_version);
        TextView build_date = content.findViewById(R.id.dev_info_build_date);
        TextView security_patch = content.findViewById(R.id.security_patch);
        TextView android_version = content.findViewById(R.id.dev_info_android_version);
        model.setText(SystemProperties.get("ro.product.model"));
        dot_version.setText(SystemProperties.get("ro.modversion") + " - " + SystemProperties.get("ro.dot.releasetype"));
        build_date.setText(SystemProperties.get("ro.build.date"));
        android_version.setText(SystemProperties.get("ro.build.version.release"));
        security_patch.setText(SystemProperties.get("ro.build.version.security_patch"));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        updateVisibilities(content);
        LinearLayout dot_version_layout = content.findViewById(R.id.dot_version_layout);
        LinearLayout android_version_layout = content.findViewById(R.id.android_version_layout);
        dot_version_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            final Intent intent = new Intent(Intent.ACTION_MAIN)
                    .putExtra("is_dot", true)
                    .setClassName(
                            "android", com.android.internal.app.PlatLogoActivity.class.getName());
                try {
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to start activity " + intent.toString());
                }
            }
            }
        });
        android_version_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            final Intent intent = new Intent(Intent.ACTION_MAIN)
                    .setClassName(
                            "android", com.android.internal.app.PlatLogoActivity.class.getName());
                try {
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Unable to start activity " + intent.toString());
                }
            }
            }
        });
        return builder.setView(content).create();
    }
   
   public void updateVisibilities(View view) {
        LinearLayout model_layout = view.findViewById(R.id.model_layout);
        LinearLayout dot_version_layout = view.findViewById(R.id.dot_version_layout);
        LinearLayout android_version_layout = view.findViewById(R.id.android_version_layout);
        LinearLayout build_date_layout = view.findViewById(R.id.build_date_layout);
        LinearLayout security_patch_layout = view.findViewById(R.id.security_patch_layout);
        if (SystemProperties.get("ro.product.model").isEmpty()) {
            model_layout.setVisibility(View.GONE);
        }
        if (SystemProperties.get("ro.modversion").isEmpty()) {
            dot_version_layout.setVisibility(View.GONE);
        }
        if (SystemProperties.get("ro.build.version.release").isEmpty()) {
            android_version_layout.setVisibility(View.GONE);
        }
        if (SystemProperties.get("ro.build.date").isEmpty()) {
            build_date_layout.setVisibility(View.GONE);
        }
        if (SystemProperties.get("ro.build.version.security_patch").isEmpty()) {
            security_patch_layout.setVisibility(View.GONE);
        }
   }
}
