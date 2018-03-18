/*
 * Copyright (C) 2016 AospExtended ROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import android.os.Bundle;
import com.android.settings.SettingsPreferenceFragment;
import android.net.Uri;
import android.content.Intent;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;

import com.android.internal.logging.nano.MetricsProto;

public class AboutUs extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about_us);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DOTEXTRAS;
    }

}
