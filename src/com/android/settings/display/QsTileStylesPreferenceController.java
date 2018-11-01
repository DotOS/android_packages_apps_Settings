/*
 * Copyright (C) 2017-2018 The Dirty Unicorns Project
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

package com.android.settings.display;

import android.app.Fragment;
import android.content.Context;
import android.os.UserHandle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;

import com.android.internal.util.du.Utils;

import com.android.settings.core.PreferenceControllerMixin;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;

import com.dirtyunicorns.tweaks.fragments.QsTileStyles;

public class QsTileStylesPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, LifecycleObserver, OnResume {

    private static final String KEY_QS_TILE_STYLES_FRAGMENT_PREF = "qs_tile_style";
    private static final int MY_USER_ID = UserHandle.myUserId();

    private static final String SUBS_PACKAGE = "projekt.substratum";

    private final Fragment mParent;
    private Preference mQsTileStylesPref;

    public QsTileStylesPreferenceController(Context context, Lifecycle lifecycle, Fragment parent) {
        super(context);
        mParent = parent;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        mQsTileStylesPref  = (Preference) screen.findPreference(KEY_QS_TILE_STYLES_FRAGMENT_PREF);
    }

    @Override
    public void onResume() {
        updateEnableState();
        updateSummary();
    }

    @Override
    public boolean isAvailable() {
        return !Utils.isPackageInstalled(mContext, SUBS_PACKAGE);
    }

    @Override
    public String getPreferenceKey() {
        return KEY_QS_TILE_STYLES_FRAGMENT_PREF;
    }

    public void updateEnableState() {
        if (mQsTileStylesPref == null) {
            return;
        }

        mQsTileStylesPref.setOnPreferenceClickListener(
            new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    QsTileStyles.show(mParent);
                    return true;
                }
            });
    }

    public void updateSummary() {
        if (mQsTileStylesPref != null) {
                mQsTileStylesPref.setSummary(mContext.getString(
                        com.android.settings.R.string.qs_styles_dialog_title));
        }
    }
}
