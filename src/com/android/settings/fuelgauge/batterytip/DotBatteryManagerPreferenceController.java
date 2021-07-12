/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.settings.fuelgauge.batterytip;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.Settings.Global;

import androidx.annotation.VisibleForTesting;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.BatterySaverReceiver;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.overlay.FeatureFactory;

import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.fuelgauge.BatterySaverUtils;

/**
 * Preference controller to control the dot battery manager
 */
public class DotBatteryManagerPreferenceController extends BasePreferenceController 
        implements LifecycleObserver, OnStart, OnStop, BatterySaverReceiver.BatterySaverListener {
    private static final String KEY_BATTERY_MANAGER = "dot_battery_manager";
    private static final int ON = 1;
    private PowerUsageFeatureProvider mPowerUsageFeatureProvider;
    private final BatterySaverReceiver mBatteryStateChangeReceiver;
    
    private AppOpsManager mAppOpsManager;
    private UserManager mUserManager;
    private final PowerManager mPowerManager;

    private Preference mBatteryManagerPref;

    public DotBatteryManagerPreferenceController(Context context) {
        super(context, KEY_BATTERY_MANAGER);
        mPowerUsageFeatureProvider = FeatureFactory.getFactory(
                context).getPowerUsageFeatureProvider(context);
        mAppOpsManager = context.getSystemService(AppOpsManager.class);
        mUserManager = context.getSystemService(UserManager.class);
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mBatteryStateChangeReceiver = new BatterySaverReceiver(context);
        mBatteryStateChangeReceiver.setBatterySaverListener(this);
        BatterySaverUtils.revertScheduleToNoneIfNeeded(context);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE_UNSEARCHABLE;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_BATTERY_MANAGER;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mBatteryManagerPref = screen.findPreference(KEY_BATTERY_MANAGER);
    }

    @Override
    public void onStart() {
        mContext.getContentResolver().registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL)
                , true, mObserver);

        mBatteryStateChangeReceiver.setListening(true);
        updateSummary();
    }

    @Override
    public void onStop() {
        mContext.getContentResolver().unregisterContentObserver(mObserver);
        mBatteryStateChangeReceiver.setListening(false);
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        updateSummary();
    }

    private final ContentObserver mObserver = new ContentObserver(
            new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange) {
            updateSummary();
        }
    };

    @Override
    public void onPowerSaveModeChanged() {
        updateSummary();
    }

    @Override
    public void onBatteryChanged(boolean pluggedIn) {
    }

    @VisibleForTesting
    void updateSummary() {
        final int num = BatteryTipUtils.getRestrictedAppsList(mAppOpsManager, mUserManager).size();
        final String smbSetting = mPowerUsageFeatureProvider.isSmartBatterySupported()
                ? Settings.Global.ADAPTIVE_BATTERY_MANAGEMENT_ENABLED
                : Settings.Global.APP_AUTO_RESTRICTION_ENABLED;
        final boolean smartBatteryOn =
                Settings.Global.getInt(mContext.getContentResolver(), smbSetting, ON) == ON;
        String bssummary = "";
        String smbsummary = "";
        if (num > 0) {
            smbsummary = mContext.getResources().getQuantityString(R.plurals.battery_manager_app_restricted, num, num);
        } else if (smartBatteryOn) {
            smbsummary =  mContext.getString(R.string.battery_manager_on);
        } else {
            smbsummary =  mContext.getString(R.string.battery_manager_off);
        }

        final ContentResolver resolver = mContext.getContentResolver();
        final boolean isPowerSaveOn = mPowerManager.isPowerSaveMode();
        final int percent = Settings.Global.getInt(resolver,
                Settings.Global.LOW_POWER_MODE_TRIGGER_LEVEL, 0);
        final int mode = Settings.Global.getInt(resolver,
                Global.AUTOMATIC_POWER_SAVE_MODE, PowerManager.POWER_SAVE_MODE_TRIGGER_PERCENTAGE);
        if (isPowerSaveOn) {
            bssummary = mContext.getString(R.string.battery_saver_on_summary);
        } else if (mode == PowerManager.POWER_SAVE_MODE_TRIGGER_PERCENTAGE) {
            if (percent != 0) {
                bssummary = mContext.getString(R.string.battery_saver_off_scheduled_summary,
                        Utils.formatPercentage(percent));
            } else {
                bssummary = mContext.getString(R.string.battery_saver_off_summary);
            }
        } else {
            bssummary = mContext.getString(R.string.battery_saver_auto_routine);
        }
        mBatteryManagerPref.setSummary(mContext.getString(R.string.dot_battery_manager_summary1) + " " + bssummary 
                                     + mContext.getString(R.string.dot_battery_manager_summary2) + " " + smbsummary);
    }
}
