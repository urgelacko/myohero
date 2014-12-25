package com.urgesoft.gopro.ui.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.urgesoft.gopro.R;

public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_GOPRO_URL = "gopro_url";
    public static final String KEY_GOPRO_PW = "gopro_pw";
    public static final String KEY_GOPRO_SSID = "gopro_ssid";

    public static final String KEY_MYO_POSE_HOLD_TIME = "pref_myo_hold_time";
    public static final String KEY_MYO_COMBO_EXPIRE_TIME = "pref_myo_combo_expire_time";
    public static final String KEY_MYO_COMMAND_ASSIGNMENTS = "pref_myo_command_assignments";
    public static final String KEY_MYO_COMBO_ENABLED = "pref_myo_combo_enabled";
    public static final String KEY_MYO_AUTOLOCK_ENABLED = "pref_myo_autolock_enabled";

    private ListPreference comboExpireTimeList;
    private CheckBoxPreference comboSupportCb;

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        comboExpireTimeList = (ListPreference) findPreference(KEY_MYO_COMBO_EXPIRE_TIME);
        comboExpireTimeList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int holdThreshold = Integer.parseInt(preference.getSharedPreferences().getString(KEY_MYO_POSE_HOLD_TIME, "0"));
                return holdThreshold > Integer.parseInt(newValue.toString());
            }

        });

    }

}
