/*
 * Copyright (C) 2017 dotOS
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

package com.android.settings.dot;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Changelog extends SettingsPreferenceFragment {

    private static final String CHANGELOG_PATH = "/system/etc/Changelog.txt";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

        InputStreamReader inputReader = null;
        String text = null;
        StringBuilder data = new StringBuilder();

        Pattern date = Pattern.compile("(={20}|\\d{4}-\\d{2}-\\d{2})");
        Pattern commit = Pattern.compile("([a-f0-9]{7})");
        Pattern committer = Pattern.compile("\\[(\\D.*?)]");
        Pattern title = Pattern.compile("([\\*].*)");

        try {
            char tmp[] = new char[2048];
            int numRead;

            inputReader = new FileReader(CHANGELOG_PATH);
            while ((numRead = inputReader.read(tmp)) >= 0) {
                data.append(tmp, 0, numRead);
            }
            text = data.toString();
        } catch (IOException e) {
            text = getString(R.string.changelog_dot_error);
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException e) {
            }
        }

        SpannableStringBuilder sb = new SpannableStringBuilder(data);
        Matcher m = date.matcher(data);
        while (m.find()){
            sb.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.accent_device_default_light, getActivity().getTheme())), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(new StyleSpan(Typeface.BOLD), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        m = commit.matcher(data);
        while (m.find()){
            sb.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.secondary_device_default_settings, getActivity().getTheme())), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(new StyleSpan(Typeface.BOLD), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        m = committer.matcher(data);
        while (m.find()){
            sb.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.secondary_device_default_settings, getActivity().getTheme())), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(new StyleSpan(Typeface.ITALIC), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        m = title.matcher(data);
        while (m.find()){
            sb.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.accent_device_default_light, getActivity().getTheme())), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(new StyleSpan(Typeface.BOLD), m.start(1), m.end(1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        final TextView textView = new TextView(getActivity());
        textView.setText(sb);

        final ScrollView scrollView = new ScrollView(getActivity());
        scrollView.addView(textView);

        return scrollView;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.DOT_EXTRAS;
    }
} 
