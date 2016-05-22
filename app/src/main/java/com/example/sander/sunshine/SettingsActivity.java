package com.example.sander.sunshine;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.units_key)));

    }
    private void bindPreferenceSummaryToValue(Preference preference){
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), " "));
    }
    public boolean onPreferenceChange(Preference preference,Object value){
      String valueStr=value.toString();
        if (preference instanceof ListPreference){
            ListPreference listPreference=(ListPreference)preference;
            int perfIndex=listPreference.findIndexOfValue(valueStr);
            if (perfIndex>=0) {
                preference.setSummary(listPreference.getEntries()[perfIndex]);
            }
        }
        else {
            preference.setSummary(valueStr);
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
