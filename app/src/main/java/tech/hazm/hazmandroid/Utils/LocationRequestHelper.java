package tech.hazm.hazmandroid.Utils;

import android.content.Context;
import android.preference.PreferenceManager;

public class LocationRequestHelper {
    public static final String KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested";

    public static void setRequesting(Context context, boolean z) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_LOCATION_UPDATES_REQUESTED, z).apply();
    }

    public static boolean getRequesting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false);
    }
}
