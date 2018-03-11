/*
 * Copyright (C) 2018 Project dotOS
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

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.app.Fragment;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.deviceinfo.OSInfoDialogFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class OSInformationsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {

    private static final String TAG = "OSInfoPref";
    private static final String KEY_OS_INFO = "device_dialog";

    private final Fragment mHost;
	
    public OSInformationsPreferenceController(Context context, Fragment host) {
        super(context);
	mHost = host;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_OS_INFO;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
		if (TextUtils.equals(preference.getKey(), KEY_OS_INFO)) {
			OSInfoDialogFragment fragment = OSInfoDialogFragment.newInstance();
            fragment.show(mHost.getFragmentManager(), OSInfoDialogFragment.TAG);
			return true;
		}
        return false;
    }
}

