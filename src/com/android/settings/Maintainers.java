/*
 * Copyright (C) 2016-2017 crDroid Android Project
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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.android.settings.SettingsPreferenceFragment;
import android.net.Uri;
import android.content.Intent;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.android.internal.logging.nano.MetricsProto;

public class Maintainers extends SettingsPreferenceFragment {

    private final String TAG = this.getClass().getSimpleName();
    private static final String REQUEST_TAG = "loadDeviceList";
    private static final String URL = "https://dotos.github.io/official_devices/devices.json";
    private static final String DOWNLOAD_WEBSITE = "https://downloads.droidontime.com";
    private static final String SHARED_PREF_NAME = "dot";
    private static final int MENU_RELOAD  = 0;
    PreferenceScreen prefScreen;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    OkHttpClient httpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);
       addPreferencesFromResource(R.xml.device_maintainers);
       prefScreen = getPreferenceScreen();
       sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
       httpClient = new OkHttpClient.Builder()
               .connectTimeout(30, TimeUnit.SECONDS)
               .writeTimeout(30, TimeUnit.SECONDS)
               .readTimeout(30, TimeUnit.SECONDS)
               .build();
       loadDeviceList();

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DOTEXTRAS;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RELOAD, 0, R.string.reload_list)
                .setIcon(com.android.internal.R.drawable.ic_menu_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RELOAD:
                loadDeviceList();
                return true;
            default:
                return false;
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading_devices));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    cancelRequests(httpClient,REQUEST_TAG);
                }
            });
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void loadDeviceList() {
        showProgressDialog();
        prefScreen.removeAll();
        final String jsonLocal = getLocalJSON();
        final Boolean isJsonLocalValid = isJSONValid(jsonLocal);
        Request request = new Request.Builder()
                .tag(REQUEST_TAG)
                .url(URL)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            if (isJsonLocalValid) {
                                populate(jsonLocal);
                            } else {
                                showToast(getString(R.string.loading_devices_failed));
                                deleteLocalJSON();
                            }
                        }
                    });
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            try {
                                if (response.isSuccessful()) {
                                    String jsonStr = response.body().string();
                                    if (!isJSONValid(jsonStr)) {
                                        if (isJsonLocalValid) {
                                            jsonStr = jsonLocal;
                                        }else{
                                            showToast(getString(R.string.loading_devices_failed));
                                            deleteLocalJSON();
                                        }
                                    } else {
                                        saveJSON(jsonStr);
                                    }
                                    populate(jsonStr);
                                }else{
                                    if (isJsonLocalValid) {
                                        populate(jsonLocal);
                                    } else {
                                        showToast(getString(R.string.loading_devices_failed));
                                        deleteLocalJSON();
                                    }
                                }
                            } catch (Exception e) {
                                showToast(getString(R.string.loading_devices_failed));
                                deleteLocalJSON();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

    }

    public void cancelRequests(OkHttpClient client, Object tag) {
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) call.cancel();
        }
    }

    private void populate(String jsonStr) {
        ArrayList<String> brands = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            if (jsonArray.length() < 1){
                showToast(getString(R.string.devices_list_empty));
                deleteLocalJSON();
            }else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    final String brand = jsonObj.getString("brand");
                    if (!brands.contains(brand)){
                        brands.add(brand);
                    }
                }
                Collections.sort(brands);
                for (final String brand: brands) {
                    PreferenceCategory brandCategory;
                    brandCategory = new PreferenceCategory(prefScreen.getContext());
                    brandCategory.setTitle(brand);
                    prefScreen.addPreference(brandCategory);
                    HashMap<String, HashMap<String, String>> devices = getDevicesByBrand(jsonArray,brand);
                    SortedSet<String> devices_sorted = new TreeSet<>(devices.keySet());
                    for (final String name : devices_sorted) {
                        final String codename = devices.get(name).get("codename");
                        final String maintainer_name = devices.get(name).get("maintainer_name");
                        final String xda_thread = devices.get(name).get("xda_thread");
                        Preference devicePref = new Preference(prefScreen.getContext());
                        devicePref.setIcon(R.drawable.ic_maintainers);
                        devicePref.setTitle(name);
                        devicePref.setSummary(codename + "\n" + String.format(getString(R.string.maintainer_description), maintainer_name));
                        devicePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                boolean isEmpty = xda_thread == null || xda_thread.trim().length() == 0;
                                try{
                                    if (isEmpty) {
                                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DOWNLOAD_WEBSITE + "/" + Uri.encode(codename) + "/")));
                                    }else{
                                        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(xda_thread)));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, e.toString());
                                }
                                return true;
                            }
                        });
                        brandCategory.addPreference(devicePref);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }


    private HashMap<String, HashMap<String, String>> getDevicesByBrand(JSONArray jsonArray_, String brand_){
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        JSONArray jsonArray = jsonArray_;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                String name = jsonObj.getString("name");
                String brand = jsonObj.getString("brand");
                String codename = jsonObj.getString("codename");
                String maintainer_name = jsonObj.getString("maintainer_name");
                String xda_thread = jsonObj.getString("xda_thread");
                if (brand.equals(brand_)){
                    HashMap<String, String> vars = new HashMap<>();
                    vars.put("codename",codename);
                    vars.put("maintainer_name",maintainer_name);
                    vars.put("xda_thread",xda_thread);
                    result.put(name,vars);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = new HashMap<>();
            }
        }
        return result;
    }

    private void showToast(String message, int duration) {
        Toast.makeText(getActivity(), message, duration).show();
    }

    private void showToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    private void deleteLocalJSON() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private String getLocalJSON() {
        return sharedPreferences.getString("devices", null);
    }

    private void saveJSON(String json) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("devices", json);
        editor.apply();
    }

    private boolean isJSONValid(String str) {
        if (str == null || str.isEmpty()){
            return false;
        }
        try {
            new JSONObject(str);
        } catch (JSONException ex) {
            try {
                new JSONArray(str);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

}
