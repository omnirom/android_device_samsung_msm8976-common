/*
* Copyright (C) 2016 The OmniROM Project
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

    private static final String KEY_SLIDER_MODE = "mdnie_senario";
    private static final String KEY_SWAP_BACK_RECENTS = "swap_back_recents";

    private ListPreference mMdnieSenario;
    private TwoStatePreference mSwapBackRecents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.main);

        mMdnieSenario = (ListPreference) findPreference(KEY_SLIDER_MODE);
        mMdnieSenario.setOnPreferenceChangeListener(this);
        int mdiescenario = Settings.System.getInt(getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING, 0);
        int valueIndex = mMdnieSenario.findIndexOfValue(String.valueOf(mdniescenario));
        mMdnieSenario.setValueIndex(valueIndex);
        mMdnieSenario.setSummary(mMdnieSenario.getEntries()[valueIndex]);

        mSwapBackRecents = (TwoStatePreference) findPreference(KEY_SWAP_BACK_RECENTS);
        mSwapBackRecents.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.BUTTON_SWAP_BACK_RECENTS, 0) != 0);

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
/*
        if (preference == mProxiSwitch) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.DEVICE_PROXI_CHECK_ENABLED, mProxiSwitch.isChecked() ? 1 : 0);
            return true;
        }
*/
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mMdnieSenario) {
            String value = (String) newValue;
            int mdniescenario = Integer.valueOf(value);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.BUTTON_EXTRA_KEY_MAPPING, mdniescenario);
            int valueIndex = mMdnieSenario.findIndexOfValue(value);
            mMdnieSenario.setSummary(mMdnieSenario.getEntries()[valueIndex]);
        }
        return true;
    }
}
