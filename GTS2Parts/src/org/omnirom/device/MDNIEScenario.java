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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.ListPreference;
import android.util.Log;

public class MDNIEScenario implements OnPreferenceChangeListener {
    private static final String TAG = MDNIEScenario.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final String FILE = "/sys/class/mdnie/mdnie/scenario";

    public static boolean isSupported() {
        if (DEBUG) Log.i(TAG, "isSupported() called");
        return Utils.fileWritable(FILE);
    }

    public static String getCurrentIndex(Context context) {
       if (DEBUG) Log.i(TAG, "getCurrentIndex called");
       return Utils.getFileValue(FILE, "0");
    }

    public static void setIndex(Preference preference, Object newValue) {
       if (DEBUG) Log.i(TAG, "setIndex called");
       Utils.writeValue(FILE, (String) newValue);
    }
    
    public static String getIndex(Context context) {
        if (DEBUG) Log.i(TAG, "getIndex called");
        String mdniescenario = Utils.getFileValue(FILE, "0");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(DeviceSettings.KEY_MDNIE_SCENARIO, mdniescenario);
    }

    /**
     * Restore setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (DEBUG) Log.i(TAG, "restore called");
        if (!isSupported()) {
            return;
        }
        Utils.writeValue(FILE, getIndex(context));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (DEBUG) Log.i(TAG, "onPreferenceChange called - newValue=" + (String) newValue);

        return true;
    }
}
