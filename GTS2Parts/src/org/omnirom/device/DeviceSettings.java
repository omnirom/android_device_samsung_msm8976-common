/*
* Copyright (C) 2017 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.view.MenuItem;
import android.util.Log;

public class DeviceSettings extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = DeviceSettings.class.getSimpleName();
    private static final boolean DEBUG = true;
    public static final String KEY_MDNIE_SCENARIO = "mdnie_senario";
    private static final String KEY_SWAP_BACK_RECENTS = "swap_back_recents";
    private static final String KEY_HOMEBUTTON_SWITCH = "homebutton_switch";

    private ListPreference mMdnieSenario;
    private TwoStatePreference mSwapBackRecents;
    private TwoStatePreference mHomeButtonSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.main);

        mMdnieSenario = (ListPreference) findPreference(KEY_MDNIE_SCENARIO);
        mMdnieSenario.setEnabled(MDNIEScenario.isSupported());
        mMdnieSenario.setValueIndex(Integer.valueOf(MDNIEScenario.getIndex(this)));
        mMdnieSenario.setSummary(mMdnieSenario.getEntries()[Integer.parseInt(MDNIEScenario.getIndex(this))]);
        mMdnieSenario.setOnPreferenceChangeListener(this);

        mSwapBackRecents = (TwoStatePreference) findPreference(KEY_SWAP_BACK_RECENTS);
        mSwapBackRecents.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.BUTTON_SWAP_BACK_RECENTS, 0) != 0);

        mHomeButtonSwitch = (TwoStatePreference) findPreference(KEY_HOMEBUTTON_SWITCH);
        mHomeButtonSwitch.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING, 0) != 0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSwapBackRecents) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BUTTON_SWAP_BACK_RECENTS, mSwapBackRecents.isChecked() ? 1 : 0);
            return true;
        }

        if (preference == mHomeButtonSwitch) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING, mHomeButtonSwitch.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (DEBUG) Log.i(TAG, "onPreferenceChange called in DeviceSettings");
        if (preference == mMdnieSenario) {
            if (DEBUG) Log.i(TAG, "onPreferenceChange mMdnieSceanrio");
            String value = (String) newValue;
            MDNIEScenario.setIndex(preference, newValue);
            int valueIndex = mMdnieSenario.findIndexOfValue(value);
            mMdnieSenario.setSummary(mMdnieSenario.getEntries()[valueIndex]);

        }
        return true;
    }
}
