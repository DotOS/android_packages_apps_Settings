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
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class OSInfoDialogFragment extends InstrumentedDialogFragment {

    public static final String TAG = "OSInfo";

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
        TextView model = view.findViewById(R.id.dev_info_device);
        model.setText(SystemProperties.get("ro.product.model"));
        TextView dot_version = view.findViewById(R.id.dev_info_dot_version);
        dot_version.setText(SystemProperties.get("ro.modversion"));
        TextView build_version = view.findViewById(R.id.dev_info_build_number);
        build_version.setText(SystemProperties.get("ro.build.display.id"));
        TextView security_patch = view.findViewById(R.id.security_patch);
        security_patch.setText(SystemProperties.get("ro.build.version.security_patch"));
        return builder.setView(content).create();
    }
}
