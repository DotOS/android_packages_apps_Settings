package com.android.settings.dot;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class dotSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "dotSettings";

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.APPLICATION;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.dot);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {

        return true;
    }
}
