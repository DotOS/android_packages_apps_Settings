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

package com.android.settings.deviceinfo.aboutphone;

import static com.android.settings.bluetooth.Utils.getLocalBtManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.view.View;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.settings.dot.TabLayout;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.accounts.EmergencyInfoPreferenceController;
import com.android.settings.applications.LayoutPreference;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.deviceinfo.BluetoothAddressPreferenceController;
import com.android.settings.deviceinfo.BuildNumberPreferenceController;
import com.android.settings.deviceinfo.DeviceModelPreferenceController;
import com.android.settings.deviceinfo.DeviceNamePreferenceController;
import com.android.settings.deviceinfo.FccEquipmentIdPreferenceController;
import com.android.settings.deviceinfo.FeedbackPreferenceController;
import com.android.settings.deviceinfo.IpAddressPreferenceController;
import com.android.settings.deviceinfo.SELinuxStatusPreferenceController;
import com.android.settings.deviceinfo.WifiMacAddressPreferenceController;
import com.android.settings.deviceinfo.firmwareversion.FirmwareVersionPreferenceController;
import com.android.settings.deviceinfo.BuildDatePreferenceController;
import com.android.settings.deviceinfo.DotVersionPreferenceController;
import com.android.settings.deviceinfo.imei.ImeiInfoPreferenceController;
import com.android.settings.deviceinfo.simstatus.SimStatusPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyDeviceInfoFragment extends DashboardFragment
        implements DeviceNamePreferenceController.DeviceNamePreferenceHost {
    private static final String LOG_TAG = "MyDeviceInfoFragment";

    private static final String KEY_MY_DEVICE_INFO_HEADER = "my_device_info_header_type2";
    //private static final String KEY_LEGAL_CONTAINER = "legal_container";

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.DEVICEINFO;
    }

    @Override
    public int getHelpResource() {
        return R.string.help_uri_about;
    }
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        initScreenControllers();
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.my_device_info;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getActivity(), this /* fragment */,
                getLifecycle());
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context,
            Activity activity,
            MyDeviceInfoFragment fragment,
            Lifecycle lifecycle) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new EmergencyInfoPreferenceController(context));
        DeviceNamePreferenceController deviceNamePreferenceController =
                new DeviceNamePreferenceController(context);
        deviceNamePreferenceController.setLocalBluetoothManager(getLocalBtManager(context));
        deviceNamePreferenceController.setHost(fragment);
        if (lifecycle != null) {
            lifecycle.addObserver(deviceNamePreferenceController);
        }
        controllers.add(deviceNamePreferenceController);
        controllers.add(new SimStatusPreferenceController(context, fragment));
        controllers.add(new DeviceModelPreferenceController(context, fragment));
        controllers.add(new FirmwareVersionPreferenceController(context, fragment));
        controllers.add(new DotVersionPreferenceController(context, lifecycle));
        controllers.add(new BuildDatePreferenceController(context));
        controllers.add(new IpAddressPreferenceController(context, lifecycle));
        controllers.add(new WifiMacAddressPreferenceController(context, lifecycle));
        controllers.add(new BluetoothAddressPreferenceController(context, lifecycle));
        controllers.add(new FeedbackPreferenceController(fragment, context));
        controllers.add(new FccEquipmentIdPreferenceController(context));
        controllers.add(new SELinuxStatusPreferenceController(context));
        controllers.add(new BuildNumberPreferenceController(context, activity, fragment, lifecycle));
        return controllers;
    }
    
    private void initScreenControllers() {
        final LayoutPreference headerPreference =
                (LayoutPreference) getPreferenceScreen().findPreference(KEY_MY_DEVICE_INFO_HEADER);
        final AboutSystemView abs = headerPreference.findViewById(R.id.abs_pref);
        Activity context = getActivity();
        EntityHeaderController controller = EntityHeaderController
                .newInstance(context, this, abs)
                .setRecyclerView(getListView(), getLifecycle())
                .setButtonActions(EntityHeaderController.ActionType.ACTION_NONE,
                        EntityHeaderController.ActionType.ACTION_NONE);
        PreferenceScreen pref = (PreferenceScreen) getPreferenceScreen();
        Preference software = (Preference) pref.findPreference("pref_screen_software");
        Preference status = (Preference) pref.findPreference("pref_screen_status");
        Preference other = (Preference) pref.findPreference("pref_screen_other");
        //Preference sim2 = (Preference) pref.findPreference("sim_status2");
        pref.removePreference(status);
        pref.removePreference(other);
        //pref.removePreference(sim2);
        abs.setOnPositionChangeListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pref.removePreference(software);
                pref.removePreference(status);
                pref.removePreference(other);
                //pref.removePreference(sim2);
                switch (tab.getPosition()) {
                    case 0:
                        pref.addPreference(software);
                        break;
                    case 1:
                        pref.addPreference(status);
                        //pref.addPreference(sim2);
                        break;
                    case 2:
                        pref.addPreference(other);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        controller.done(context, true /* rebindActions */);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final BuildNumberPreferenceController buildNumberPreferenceController =
            use(BuildNumberPreferenceController.class);
        if (buildNumberPreferenceController.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showDeviceNameWarningDialog(String deviceName) {
        DeviceNameWarningDialog.show(this);
    }

    public void onSetDeviceNameConfirm() {
        final DeviceNamePreferenceController controller = use(DeviceNamePreferenceController.class);
        controller.confirmDeviceName();
    }

    private static class SummaryProvider implements SummaryLoader.SummaryProvider {

        private final SummaryLoader mSummaryLoader;

        public SummaryProvider(SummaryLoader summaryLoader) {
            mSummaryLoader = summaryLoader;
        }

        @Override
        public void setListening(boolean listening) {
            if (listening) {
                mSummaryLoader.setSummary(this, DeviceModelPreferenceController.getDeviceModel());
            }
        }
    }

    public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY
            = (activity, summaryLoader) -> new SummaryProvider(summaryLoader);

    /**
     * For Search.
     */
    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.my_device_info;
                    return Arrays.asList(sir);
                }

                @Override
                public List<AbstractPreferenceController> createPreferenceControllers(
                        Context context) {
                    return buildPreferenceControllers(context, null /*activity */,
                            null /* fragment */, null /* lifecycle */);
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    // The legal container is duplicated, so we ignore it here.
                    //keys.add(KEY_LEGAL_CONTAINER);
                    return keys;
                }
            };
}
