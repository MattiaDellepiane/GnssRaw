package com.github.mattiadellepiane.gnssraw.ui.main.tabs


import android.os.Bundle
import com.github.mattiadellepiane.gnssraw.R
import android.os.Build
import androidx.preference.*

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setServerSwitchListener()
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (!preferences.getBoolean("server_enabled", false)) {
            val serverEditText = findPreference<EditTextPreference>("server_address")
            val serverPort = findPreference<EditTextPreference>("server_port")
            serverEditText!!.isEnabled = false
            serverPort!!.isEnabled = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val fullTracking = findPreference<SwitchPreference>("full_tracking")
            fullTracking!!.isEnabled = true
        }
    }

    private fun setServerSwitchListener() {
        val serverSwitch = findPreference<SwitchPreference>("server_enabled")
        if (serverSwitch != null) {
            serverSwitch.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference: Preference?, boolValue: Any ->
                val value = boolValue as Boolean
                val serverEditText = findPreference<EditTextPreference>("server_address")
                serverEditText!!.isEnabled = value
                val serverPort = findPreference<EditTextPreference>("server_port")
                serverPort!!.isEnabled = value
                true
            }
        }
    }
}