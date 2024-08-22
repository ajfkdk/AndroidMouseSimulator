package com.tencent.cursor.viewclass;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.tencent.cursor.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}