package android.mohamed.worldwidenews.ui

import android.mohamed.worldwidenews.R
import android.mohamed.worldwidenews.utils.Constants
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}