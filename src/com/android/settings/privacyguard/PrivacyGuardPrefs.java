/*
 * Copyright (C) 2013 Slimroms
 * Copyright (C) 2016 The CyanogenMod Project
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

package com.android.settings.privacyguard;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

public class PrivacyGuardPrefs extends SettingsPreferenceFragment {

    private static final String TAG = "PrivacyGuardPrefs";

    public static PrivacyGuardPrefs newInstance() {
        PrivacyGuardPrefs privacyGuardFragment = new PrivacyGuardPrefs();
        return privacyGuardFragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.privacy_guard_prefs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        final ViewGroup list = (ViewGroup) getListView().getParent();
        // our container already takes care of the padding
        int paddingTop = list.getPaddingTop();
        int paddingBottom = list.getPaddingBottom();
        list.setPadding(0, paddingTop, 0, paddingBottom);
        return view;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.OWLSNEST;
    }
}
