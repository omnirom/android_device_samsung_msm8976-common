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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;

public class SRGBModeSwitch implements OnPreferenceChangeListener {

    private static final String FILE = "/sys/class/mdnie/mdnie/scenario"";

    public static boolean isSupported() {
        return Utils.fileWritable(FILE);
    }

//    public static boolean isCurrentlyEnabled(Context context) {
//        return Utils.getFileValue(FILE, '0');
//    }

    public static String getCurrentIndex(Context context) {
       return Utils.getFileValue(FILE, '0');
    }


    public static String getIndex(Context context) {
        String mdniescenario = Utils.getFileValue(FILE, '0');
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(DeviceSettings.KEY_SRGB_SWITCH, mdniescenario);
    }

    /**
     * Restore setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        Utils.writeValue(FILE, getIndex(context));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Utils.writeValue(FILE, (String) newValue);
        return true;
    }

}
