package com.github.mattiadellepiane.gnssraw.ui.main.tabs;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        setServerSwitchListener();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(!preferences.getBoolean("server_enabled", false)){
            EditTextPreference serverEditText = findPreference("server_address");
            EditTextPreference serverPort = findPreference("server_port");
            serverEditText.setEnabled(false);
            serverPort.setEnabled(false);
        }
        /*if(preferences.getString("server_address", "").equalsIgnoreCase("true")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("server_address", getString(R.string.server_ip_default));
            editor.apply();
        }*/
    }

    private void setServerSwitchListener(){
        SwitchPreference serverSwitch = findPreference("server_enabled");
        if (serverSwitch != null) {
            serverSwitch.setOnPreferenceChangeListener(
                (preference, boolValue)->{
                    boolean value = (Boolean) boolValue;
                    EditTextPreference serverEditText = findPreference("server_address");
                    serverEditText.setEnabled(value);
                    EditTextPreference serverPort = findPreference("server_port");
                    serverPort.setEnabled(value);
                    return true;
                }
            );
        }
    }

}