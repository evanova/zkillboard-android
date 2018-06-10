package org.devfleet.zkillboard.zkilla.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.CallSuper;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.devfleet.zkillboard.zkilla.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferences;

    @Override
    @CallSuper
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    @CallSuper
    public void onStop() {
        this.preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
    }
}
